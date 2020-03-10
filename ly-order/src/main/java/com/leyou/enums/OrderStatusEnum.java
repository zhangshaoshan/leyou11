package com.leyou.enums;

public enum  OrderStatusEnum {
    UNPAY(1,"未付款"),
    PAYED(2,"已付款"),
    UN_CONFIRM(3,"未确认"),
    SUCCESS(4,"交易成功"),
    CLOSED(5,"交易失败，已关闭"),
    RATED(1,"已评价"),
    ;
    private int code;
    private String desc;

    OrderStatusEnum(int code,String desc){
        this.code = code;
        this.desc = desc;
    }

    public int value(){
        return code;
    }
}
