package com.leyou.pojo;


import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

@Data
@Table(name = "tb_order")
public class Order {

    @Id
    private Long orderId;//订单Id
    private Long totalPay;//总金额
    private Long actualPay;//实付金额
    private String promotionIds;//参与促销活动的id
    private Integer paymentType;//支付方式 货到付款  线上支付
    private Long postFee = 0L;//邮费
    private Date createTime;//创建时间
    private String shippingName;//物流名称
    private String shippingCode;//物流编号
    private String userId;//用户id
    private String buyerMessage;//买家信息
    private String buyerNick;//买家昵称
    private Boolean buyerRate;//买家是否已经评价
    private String receiverState;//省份
    private String receiverCity;//城市
    private String receiverDistrict;//买家留言
    private String receiverAddress;//买家地址
    private String receiverMobile;//买家电话
    private String receiverZip;//买家邮政编码
    private String receiver;//收货人名称
    private Integer invoiceType;//发票类型
    private Integer sourceType;//订单来源 pc  app

    @Transient
    private OrderStatus orderStatus;
    @Transient
    private List<OrderDetail> orderDetails;

}
