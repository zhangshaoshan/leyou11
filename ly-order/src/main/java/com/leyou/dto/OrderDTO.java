package com.leyou.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 这个类专门用来接收前端传过来的数据
 */


@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    @NotNull
    private Long addressId;//收货人地址Id
    @NotNull
    private Integer paymentType;//付款类型
    @NotNull
    private List<CartDTO> carts;//订单详情

}
