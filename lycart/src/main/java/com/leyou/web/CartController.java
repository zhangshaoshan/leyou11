package com.leyou.web;

import com.leyou.pojo.Cart;
import com.leyou.service.CartService;
import com.leyou.vo.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class CartController {
    @Autowired
    private CartService cartService;
    /**
     * 添加到购物车
     * */
    @PostMapping("/addCart")
    public ResponseEntity<ResponseModel> addCart(@RequestBody Cart cart,@RequestParam(value = "userId")String userId){
        cartService.addCart(cart,userId);
        return ResponseEntity.ok(new ResponseModel(HttpStatus.OK.value(),"ok",null));
    }

    /**
     * 查询购物车商品
     * */
    @GetMapping("/cartList")
    public ResponseEntity<ResponseModel> getCart(@RequestParam(value = "userId")String userId){
        return ResponseEntity.ok(new ResponseModel(HttpStatus.OK.value(),"ok",cartService.queryCartListByUserId(userId)));
    }

    /**
     * 修改购物车商品数量
     * */
    @PostMapping("/updateCart")
    public ResponseEntity<ResponseModel> updateCart(@RequestParam(value = "userId")String userId,@RequestParam(value = "skuId") String skuId,@RequestParam(value = "num")Integer num){
        cartService.updateCart(userId,skuId,num);
        return ResponseEntity.ok(new ResponseModel(HttpStatus.OK.value(),"ok",null));
    }

    /***
     * 删除购物车商品
     * */
    @PostMapping("/deleteCart")
    public ResponseEntity<ResponseModel> deleteCart(@RequestParam(value = "userId")String userId,@RequestParam(value = "skuId") String skuId){
        cartService.deleteCart(userId,skuId);
        return ResponseEntity.ok(new ResponseModel(HttpStatus.OK.value(),"ok",null));
    }
}
