package com.leyou.web;

import com.leyou.dto.OrderDTO;
import com.leyou.service.OrderService;
import com.leyou.vo.ResponseModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(tags = "订单API")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/createOrder")
    @ApiOperation(value = "创建订单",notes = "创建订单")
    @ApiImplicitParam(name = "orderDTO",value = "订单详情",required = true,dataType = "OrderDTO")
    public ResponseEntity<ResponseModel> createOrder(@RequestBody OrderDTO orderDTO,@RequestParam(value = "userId")String userId,@RequestParam(value = "token")String token){
        return ResponseEntity.ok(new ResponseModel(HttpStatus.OK.value(),"ok",orderService.createOrder(orderDTO,userId,token)));
    }

    @GetMapping("/orderDetail")
    @ApiOperation(value = "查询订单",notes = "查询订单")
    @ApiImplicitParam(name = "orderId",value = "订单id",required = true,dataType = "Long")
    public ResponseEntity<ResponseModel> queryOrder(@RequestParam(value = "orderId")Long orderId){
        return ResponseEntity.ok(new ResponseModel(HttpStatus.OK.value(),"ok",orderService.queryOrder(orderId)));
    }

    /**
     * 向微信后台发起订单支付
     * */
    @GetMapping("/payOrder")
    public ResponseEntity<ResponseModel> payOrderByOrderId(@RequestParam("orderId")Long orderId){
        return ResponseEntity.ok(new ResponseModel(HttpStatus.OK.value(),"ok",orderService.payOrderByOrderId(orderId)));
    }

    /**
    * 查询支付结果
    * */
    @GetMapping("/state")
    public ResponseEntity<ResponseModel> queryOrderStateByOrderId(@RequestParam("orderId")Long orderId){
        return ResponseEntity.ok(new ResponseModel(HttpStatus.OK.value(),"ok",orderService.queryOrderStateByOrderId(orderId).getValue()));
    }
}
