package com.leyou.utils;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayUtil;
import com.leyou.config.PayConfig;
import com.leyou.enums.ExceptionEnum;
import com.leyou.enums.OrderStatusEnum;
import com.leyou.enums.PayStateEnum;
import com.leyou.exception.LyException;
import com.leyou.mapper.OrderMapper;
import com.leyou.mapper.OrderStatusMapper;
import com.leyou.pojo.Order;
import com.leyou.pojo.OrderStatus;
import com.sun.xml.internal.bind.v2.TODO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class PayHelper {
    @Autowired
    WXPay wxPay;
    @Autowired
    PayConfig config;
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    OrderStatusMapper orderStatusMapper;

    public static final String TRADE_TYPE_APP = "APP";

    public Map createOrder(Long orderId, Long totalPay, String desc) {
        try {
            Map<String, String> data = new HashMap<>();
            //生成商户订单号，不可重复
            data.put("appid", config.getAppID());
            data.put("mch_id", config.getMchID());
            data.put("nonce_str", WXPayUtil.generateNonceStr());
            data.put("body", desc);//订单描述
            data.put("out_trade_no", orderId.toString());//订单编号
            data.put("total_fee", totalPay.toString());//订单金额
            //自己的服务器IP地址
            data.put("spbill_create_ip", "127.0.0.1");
            //异步通知地址（请注意必须是外网）
            data.put("notify_url", config.getNotifyUrl());
            //交易类型  app支付  扫码支付 等等
            data.put("trade_type", TRADE_TYPE_APP);
            //附加数据，在查询API和支付通知中原样返回，该字段主要用于商户携带订单的自定义数据
            data.put("attach", "");
            data.put("sign", WXPayUtil.generateSignature(data, config.getKey(),
                    WXPayConstants.SignType.MD5));
            //使用官方API请求预付订单
            Map<String, String> response = wxPay.unifiedOrder(data);
            //业务失败
            if (WXPayConstants.FAIL.equals(response.get("result_code"))){
                log.info("【微信支付】 微信下单业务失败，错误码{}，失败原因:{}",response.get("err_code"),response.get("err_code_des"));
                throw new LyException(ExceptionEnum.WX_PAY_FAIL);
            }
            //通信成功
            if (WXPayConstants.SUCCESS.equals(response.get("return_code"))) {//主要返回以下5个参数
                //这里要做sign的校验，支付金额的校验，金额类型的校验 TODO
                isValidSign(response);
                Map<String, String> param = new HashMap<>();
                param.put("appid", config.getAppID());
                param.put("partnerid", response.get("mch_id"));
                param.put("prepayid", response.get("prepay_id"));
                param.put("package", "Sign=WXPay");
                param.put("noncestr", WXPayUtil.generateNonceStr());
                param.put("timestamp", System.currentTimeMillis() / 1000 + "");
                param.put("sign", WXPayUtil.generateSignature(param, config.getKey(),
                        WXPayConstants.SignType.MD5));
                return param;
            }else {
                log.info("【微信支付】 创建与支付订单失败，失败原因:{}",response.get("return_msg"));
                throw new LyException(ExceptionEnum.WX_PAY_FAIL);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new LyException(ExceptionEnum.WX_PAY_FAIL);
        }
    }


    public void isValidSign(Map<String, String> result) {
        //重新生成签名 很传过来的签名进行比较   由于不能确定微信用的是哪个签名，所以两个都生成
        try {
            String md5_sign = WXPayUtil.generateSignature(result, config.getKey(),
                    WXPayConstants.SignType.MD5);
            String HMACSHA256_sign = WXPayUtil.generateSignature(result, config.getKey(),
                    WXPayConstants.SignType.HMACSHA256);
            String sign = result.get("sign");
            if (!StringUtils.equals(sign,md5_sign) && !StringUtils.equals(sign,HMACSHA256_sign)){
                throw new LyException(ExceptionEnum.WX_SIGN_ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public PayStateEnum queryPayState(Long orderId) {
        //组织请求参数
        try {
            Map<String, String> data = new HashMap<>();
            data.put("out_trade_no", orderId.toString());
            Map<String, String> result = wxPay.orderQuery(data);
            //校验状态
            isSuccess(result);
            //鉴定签名
            isValidSign(result);
            //支付金额的校验，金额类型的校验
            String totalFeeStr = result.get("total_fee");
            String outTradeNoStr = result.get("out_trade_no");
            if (StringUtils.isEmpty(totalFeeStr) || StringUtils.isEmpty(outTradeNoStr)){
                throw new LyException(ExceptionEnum.WX_PAY_PARAMS_ERROR);
            }
            Long totalFee = Long.valueOf(totalFeeStr);
            Long outTradeNo = Long.valueOf(outTradeNoStr);
            //查询订单信息
            Order order = orderMapper.selectByPrimaryKey(orderId);
            if (totalFee != /*order.getTotalPay()*/ 1){
                //金额不符合
                throw new LyException(ExceptionEnum.WX_PAY_PARAMS_ERROR);
            }
            //查看交易状态
            /**
             * SUCCESS—支付成功
             * REFUND—转入退款
             * NOTPAY—未支付
             * CLOSED—已关闭
             * REVOKED—已撤销（付款码支付）
             * USERPAYING--用户支付中（付款码支付）
             * PAYERROR--支付失败(其他原因，如银行返回失败)
             * */
            String tradeState = result.get("trade_state ");
            if ("SUCCESS".equals(tradeState)){
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
                return PayStateEnum.SUCCESS;
            }
            if ("NOTPAY".equals(tradeState) || "USERPAYING".equals(tradeState)){
                 return PayStateEnum.NOT_PAY;
            }
            return PayStateEnum.FAIL;

        } catch (Exception e) {
            e.printStackTrace();
            return PayStateEnum.NOT_PAY;
        }
    }

    public void isSuccess(Map<String, String> result) {
        //校验状态
        if (WXPayConstants.FAIL.equals(result.get("result_code"))){
            log.info("【微信支付】 微信下单业务失败，错误码{}，失败原因:{}",result.get("err_code"),result.get("err_code_des"));
            throw new LyException(ExceptionEnum.WX_PAY_FAIL);
        }
        //通信失败
        if (WXPayConstants.FAIL.equals(result.get("return_code"))) {
            log.info("【微信支付】 创建与支付订单失败，失败原因:{}",result.get("return_msg"));
            throw new LyException(ExceptionEnum.WX_PAY_FAIL);
        }
    }
}
