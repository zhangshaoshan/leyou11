package com.leyou.service;

import com.alibaba.fastjson.JSONObject;
import com.github.wxpay.sdk.WXPayConstants;
import com.leyou.client.AddressClient;
import com.leyou.client.GoodsClient;
import com.leyou.client.UserClient;
import com.leyou.dto.AddressDTO;
import com.leyou.dto.CartDTO;
import com.leyou.dto.OrderDTO;
import com.leyou.enums.ExceptionEnum;
import com.leyou.enums.OrderStatusEnum;
import com.leyou.enums.PayStateEnum;
import com.leyou.exception.LyException;
import com.leyou.mapper.OrderDetailMapper;
import com.leyou.mapper.OrderMapper;
import com.leyou.mapper.OrderStatusMapper;
import com.leyou.pojo.*;
import com.leyou.utils.IdWorker;
import com.leyou.utils.JsonUtils;
import com.leyou.utils.PayHelper;
import com.leyou.vo.ResponseModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private OrderStatusMapper orderStatusMapper;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private UserClient userClient;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private PayHelper payHelper;

    @Transactional
    public Long createOrder(OrderDTO orderDTO,String userId,String token) {
        //新增订单
        Order order = new Order();
        //1.1订单编号
        Long orderId = idWorker.nextId();
        order.setOrderId(orderId);
        order.setUserId(userId);
        order.setCreateTime(new Date());
        order.setPaymentType(orderDTO.getPaymentType());
        //1.2用户信息  通过用户id 获取用户信息      这里其实应该让用户传过来的，这里只是做演示用
        ResponseModel userInfoResponse = userClient.getUserInfo(userId,token);
        if (userInfoResponse.getData() != null){
            User user = JSONObject.parseObject(JSONObject.toJSONString(userInfoResponse.getData()), User.class);
            order.setBuyerNick(user.getUsername());
            order.setBuyerRate(false);
        }

        //1.3收货人地址
        AddressDTO addressDTO = AddressClient.findById(orderDTO.getAddressId());
        order.setReceiver(addressDTO.getName());
        order.setReceiverAddress(addressDTO.getAddress());
        order.setReceiverCity(addressDTO.getCity());
        order.setReceiverMobile(addressDTO.getPhone());
        order.setReceiverState(addressDTO.getState());
        order.setReceiverZip(addressDTO.getZipCode());
        //1.4金额
        Map<Long, Integer> cartDTOMap = orderDTO.getCarts().stream()
                                            .collect(Collectors.toMap(CartDTO::getSkuId, CartDTO::getNum));//直接将CartDTO转成map
        Set<Long> skuIds = cartDTOMap.keySet();
        ResponseModel skuListResponse = goodsClient.querySkuListBySkuIds(new ArrayList<>(skuIds));
        List<Sku> skuList = JsonUtils.parseList(JsonUtils.serialize(skuListResponse.getData()),Sku.class);
        Long totlePrice = 0L;
        List<OrderDetail> orderDetailList = new ArrayList<>();
        for (Sku sku:
             skuList) {
            Long skuPrice = sku.getPrice()*cartDTOMap.get(sku.getId());
            totlePrice += skuPrice;
            //同时准备订单详情
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setImage(StringUtils.substringBefore(sku.getImages(),","));
            orderDetail.setNum(cartDTOMap.get(sku.getId()));
            orderDetail.setOwnSpec(sku.getOwnSpec());
            orderDetail.setPrice(sku.getPrice());
            orderDetail.setSkuId(sku.getId());
            orderDetail.setTitle(sku.getTitle());
            orderDetailList.add(orderDetail);
        }
        order.setTotalPay(totlePrice);//总金额
        order.setActualPay(totlePrice+order.getPostFee()-0);//实付金额=总+邮费-优惠
        //将order写入数据库
        int orderInsert = orderMapper.insertSelective(order);
        if (orderInsert == 0){
            log.error("【创建订单】创建订单失败，orderId:{}",orderId);
            throw new LyException(ExceptionEnum.ORDER_INSERT_ERROR);
        }
        //新增订单详情
        int orderDetailInsert = orderDetailMapper.insertList(orderDetailList);
        if (orderDetailInsert == 0){
            log.error("【创建订单】创建订单失败，orderId:{}",orderId);
            throw new LyException(ExceptionEnum.ORDER_INSERT_ERROR);
        }
        //新增订单状态
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setCreateTime(new Date());
        orderStatus.setOrderId(orderId);
        orderStatus.setStatus(OrderStatusEnum.UNPAY.value());
        orderStatusMapper.insert(orderStatus);
        //减少库存  feign调用    失败会抛出异常，库存事务回滚，，createOrder事务也会回滚
        // 这里不能用rabbitMQ来做，如果失败了不能处理  可以用 TX-LNC来实现分布式事务
        goodsClient.decreaseStockByCarts(orderDTO.getCarts());
        return orderId;
    }

    public Order queryOrder(Long orderId) {
        //查询order
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if (order == null){
            throw new LyException(ExceptionEnum.ORDER_NOT_FOUND);
        }
        //查询orderDetail
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderId(orderId);
        List<OrderDetail> orderDetailList = orderDetailMapper.select(orderDetail);
        if (CollectionUtils.isEmpty(orderDetailList)){
            throw new LyException(ExceptionEnum.ORDER_DETAIL_NOT_FOUND);
        }
        order.setOrderDetails(orderDetailList);
        //查询订单状态
        OrderStatus orderStatus = orderStatusMapper.selectByPrimaryKey(orderId);
        if (orderStatus == null){
            throw new LyException(ExceptionEnum.ORDER_STATUS_NOT_FOUND);
        }
        order.setOrderStatus(orderStatus);
        return order;
    }

    public Map payOrderByOrderId(Long orderId) {
        Order order = queryOrder(orderId);
        OrderStatus orderStatus = order.getOrderStatus();
        if (orderStatus.getStatus() != OrderStatusEnum.UNPAY.value()){
            throw new LyException(ExceptionEnum.ORDER_STATUS_NOT_FOUND);
        }
        //商品支付金额
        Long actualPay = order.getActualPay();
        //商品描述
        String desc = order.getOrderDetails().get(0).getTitle();
        return payHelper.createOrder(orderId,actualPay,desc);
    }

    public void handleNotify(Map<String, String> result) {
        //判断是否成功
        payHelper.isSuccess(result);
        //这里要做sign的校验
        payHelper.isValidSign(result);
        //支付金额的校验，金额类型的校验
        String totalFeeStr = result.get("total_fee");
        String outTradeNoStr = result.get("out_trade_no");
        if (StringUtils.isEmpty(totalFeeStr) || StringUtils.isEmpty(outTradeNoStr)){
            throw new LyException(ExceptionEnum.WX_PAY_PARAMS_ERROR);
        }
        Long totalFee = Long.valueOf(totalFeeStr);
        Long outTradeNo = Long.valueOf(outTradeNoStr);
        //查询订单信息
        Long orderId = outTradeNo;
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if (totalFee != /*order.getTotalPay()*/ 1){
            //金额不符合
            throw new LyException(ExceptionEnum.WX_PAY_PARAMS_ERROR);
        }
        //修改订单状态
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setStatus(OrderStatusEnum.PAYED.value());
        orderStatus.setOrderId(orderId);
        orderStatus.setPaymentTime(new Date());
        //写入数据库
        int update = orderStatusMapper.updateByPrimaryKeySelective(orderStatus);
        if (update != 1){
            throw new LyException(ExceptionEnum.WX_PAY_UPDATE_ORDER_STATUS_ERROR);
        }
        log.info("【微信支付】 微信支付成功 订单编号：{}",orderId);
    }

    public PayStateEnum queryOrderStateByOrderId(Long orderId) {
        //查询订单，如果是已支付，那么证明已支付回调回来了，
        OrderStatus orderStatus = orderStatusMapper.selectByPrimaryKey(orderId);
        if (orderStatus.getStatus() == OrderStatusEnum.PAYED.value()){
            return PayStateEnum.SUCCESS;
        }
        //去微信后台查询支付状态
        return payHelper.queryPayState(orderId);
    }
}
