package com.leyou.web;

import com.leyou.enums.ExceptionEnum;
import com.leyou.exception.LyException;
import com.leyou.pojo.Item;
import com.leyou.service.ErrorDemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/errorDemo")
public class ErrorDemoController {
    @Autowired
    ErrorDemoService errorDemoService;
    @RequestMapping(value = "/saveItem",method = RequestMethod.POST)
    public ResponseEntity<Item> saveItem(Item item){
        if (item.getPrice() == null){ //判断价格是否为空
            throw new LyException(ExceptionEnum.PRICE_CANNOT_BE_NULL);
        }
        item = errorDemoService.saveItem(item);
        return ResponseEntity.status(HttpStatus.CREATED).body(item);
    }
}
