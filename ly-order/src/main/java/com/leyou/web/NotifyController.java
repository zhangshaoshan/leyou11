package com.leyou.web;

import com.leyou.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("notify")
@Slf4j
public class NotifyController {
    @Autowired
    private OrderService orderService;

    /**
     * 微信支付成功回调
     * 接收到回调之后需要给微信后台一个应答
     * */
    @RequestMapping(value = "/pay",produces = "application/xml")
    public Map<String,String> wxNotify(@RequestBody Map<String,String> result){
        log.info("【微信支付】  微信支付回调结果：{}",result);
        //处理回调
        orderService.handleNotify(result);
        /* 简单的写法，直接返回字符串 String reponse = "<xml> \n" +
                "\n" +
                "  <return_code><![CDATA[SUCCESS]]></return_code>\n" +
                "  <return_msg><![CDATA[OK]]></return_msg>\n" +
                "</xml> \n";*/
        Map<String,String> msg = new HashMap<>();
        msg.put("return_code","SUCCESS");
        msg.put("return_msg","OK");
        return msg;
    }

}
