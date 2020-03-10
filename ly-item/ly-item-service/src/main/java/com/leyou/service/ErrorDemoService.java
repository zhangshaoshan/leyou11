package com.leyou.service;

import com.leyou.pojo.Item;
import org.springframework.stereotype.Service;

@Service("errorDemoService")
public class ErrorDemoService {
    public Item saveItem(Item item){
        return item;
    }
}
