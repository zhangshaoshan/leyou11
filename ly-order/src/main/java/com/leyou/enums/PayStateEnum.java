package com.leyou.enums;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
public enum PayStateEnum {
    NOT_PAY(0,"未支付"),
    SUCCESS(1,"已支付"),
    FAIL(2,"支付失败")

    ;
    int value;
    private String desc;
    public int getValue(){
        return value;
    }
}
