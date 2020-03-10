package com.leyou.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ExceptionEnum {
    PRICE_CANNOT_BE_NULL(1001,"价格不能为空！"),
    NAME_CANNOT_BE_NULL(1002,"名称不能为空！"),
    CATEGORY_NOT_FOUND(1003,"未找到商品分类！"),
    CATEGORY_INSERT_FAIL(1004,"新增商品分类失败！"),
    PARAM_FALL(1005,"参数错误！"),
    BRAND_NOT_FOUND(1006,"未找到品牌！"),
    BRAND_SAVE_ERROR(1007,"新增品牌失败！"),
    CATEGORY_BRAND_SAVE_ERROR(1008,"新增品牌分类失败！"),
    FILE_UPLOAD_ERROR(1009,"保存文件失败！"),
    INVALID_FILE_TYPE(1010,"文件类型不匹配！"),
    SPEC_GROUP_NOT_FOUND(1011,"未找到规格分组！"),
    INSERT_SPEC_GROUP_FALL(1012,"新增规格参数失败！"),
    SPEC_PARAM_NOT_FOUND(1013,"未找到规格参数！"),
    SPEC_PARAM_INSERT_FALL(1014,"新增规格分类失败！"),
    SPEC_PARAM_EDIT_ID_NULL_FALL(1015,"规格分类参数不能为空！"),
    SPEC_PARAM_EDIT_FALL(1016,"修改分类参数失败！"),
    GOODS_INSERT_FALL(1017,"新增商品失败！"),
    GOODS_NOT_FOUND(1018,"未找到商品！"),
    SPUDETAIL_NOT_FOUND(1019,"未找到商品！"),
    SKU_NOT_FOUND(1020,"未找到商品！"),
    STOCK_NOT_FOUND(1021,"库存不存在！"),
    UPDATE_SPU_FAIL(1022,"修改商品失败！"),
    INVALID_USER_DATA_TYPE(1023,"用户数据类型无效"),
    USER_EXISTED_ERROR(1024,"用户手机号已被使用！"),
    VERIFY_CODE_ERROR(1025,"验证码错误"),
    USER_REGISTER_ERROR(1026,"用户注册失败"),
    USER_NOEXISTED_ERROR(1027,"用户不存在"),
    USER_LOGIN_ERROR(1028,"登录失败"),
    TOKEN_ERROR(1029,"token不能为空"),
    USERID_ERROR(1030,"用户id不能为空"),
    TOKEN_TIMEOUT(1031,"token已过期"),
    UPDATE_PASSWORD_FAIL(1032,"修改密码失败"),
    CART_NOFOUND(1033,"未找到购物车商品"),
    ORDER_INSERT_ERROR(1034,"创建订单失败"),
    STOCK_ON_ENOUGH(1035,"库存不足"),
    ORDER_NOT_FOUND(1036,"订单不存在"),
    ORDER_DETAIL_NOT_FOUND(1037,"订单详情不存在"),
    ORDER_STATUS_NOT_FOUND(1038,"订单状态不存在"),
    WX_PAY_FAIL(1039,"微信下单失败"),
    ORDER_STATUS_ERROR(1040,"订单状态有误"),
    WX_SIGN_ERROR(1041,"支付sign校验失败"),
    WX_PAY_PARAMS_ERROR(1042,"支付订单参数有误"),
    WX_PAY_UPDATE_ORDER_STATUS_ERROR(1042,"更新订单状态失败"),
    ;
    private Integer code;
    private String msg;

}
