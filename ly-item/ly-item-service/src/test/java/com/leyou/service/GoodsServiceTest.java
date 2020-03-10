package com.leyou.service;

import com.leyou.dto.CartDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GoodsServiceTest {
    @Autowired
    private GoodsService goodsService;
    @Test
    public void decreaseStockByCarts() {
        Thread thread1 = new Thread(new DecreaseStock());
        Thread thread2 = new Thread(new DecreaseStock());
        Thread thread3 = new Thread(new DecreaseStock());
        thread1.start();
        thread2.start();
        thread3.start();
    }

    class DecreaseStock implements Runnable{

        @Override
        public void run() {
            List<CartDTO> cartDTOList = Arrays.asList(new CartDTO(2600242L, 2), new CartDTO(2600248L, 2));
            goodsService.decreaseStockByCarts(cartDTOList);
        }
    }
}