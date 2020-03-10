package com.leyou.service;

import com.alibaba.fastjson.JSONObject;
import com.leyou.enums.ExceptionEnum;
import com.leyou.exception.LyException;
import com.leyou.pojo.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;
    private static final String KEY_PREFIX = "cart:uid:";
    public void addCart(Cart cart,String userId) {
        String key = KEY_PREFIX + userId;
        //判断是否存在
        String hashKey = cart.getSkuId().toString();
        //直接传入bound 让这个oprations和这个key绑定，方便操作
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(key);
        Integer num = cart.getNum();
        if (operations.hasKey(hashKey)){
            //是  修改数量
            String jsonCart = operations.get(hashKey).toString();
            cart = JSONObject.parseObject(jsonCart, Cart.class);
            cart.setNum(cart.getNum()+num);
        }
        //写回redis
        operations.put(hashKey, JSONObject.toJSONString(cart));
    }

    public List<Cart> queryCartListByUserId(String userId) {
        String key = KEY_PREFIX + userId;
        if (redisTemplate.hasKey(key) == false){
            throw new LyException(ExceptionEnum.CART_NOFOUND);
        }
        //直接传入bound 让这个oprations和这个key绑定，方便操作
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(key);
        List<Cart> cartList = operations.values().stream().map(o->JSONObject.parseObject(o.toString(),Cart.class)).collect(Collectors.toList());
        return cartList;
    }

    public void updateCart(String userId,String skuId,Integer num) {
        BoundHashOperations<String, Object, Object> oprations = getBoundOperations(userId, skuId);
        Cart cart = JSONObject.parseObject(oprations.get(skuId).toString(), Cart.class);
        cart.setNum(num);
        //保存回redis
        oprations.put(skuId,JSONObject.toJSONString(cart));
    }

    public void deleteCart(String userId,String skuId) {
        BoundHashOperations<String, Object, Object> oprations = getBoundOperations(userId, skuId);
        oprations.delete(skuId);
    }

    private BoundHashOperations<String, Object, Object> getBoundOperations(String userId, String skuId) {
        String key = KEY_PREFIX + userId;
        if (!redisTemplate.hasKey(key)) {
            throw new LyException(ExceptionEnum.CART_NOFOUND);
        }
        BoundHashOperations<String, Object, Object> oprations = redisTemplate.boundHashOps(key);
        if (!oprations.hasKey(skuId)) {
            throw new LyException(ExceptionEnum.CART_NOFOUND);
        }
        return oprations;
    }
}
