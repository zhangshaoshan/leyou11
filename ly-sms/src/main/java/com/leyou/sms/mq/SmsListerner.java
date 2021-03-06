package com.leyou.sms.mq;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.exceptions.ClientException;
import com.leyou.sms.config.SmsProperties;
import com.leyou.sms.utils.SmsUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Map;

@Component
@EnableConfigurationProperties(SmsProperties.class)
@Slf4j
public class SmsListerner {
    @Autowired
    private SmsUtils smsUtils;

    @Autowired
    private SmsProperties smsProperties;
    /**
     * 发送短信验证码
     * */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "sms.verify.code.queue", durable = "true"),
            exchange = @Exchange(
                    value = "ly.sms.exchange",
                    type = ExchangeTypes.TOPIC  ///  . 的形式  用topic
            ),
            key = "sms.verify.code"))
    public void sendVerifyCode(Map<String,String> msg){
        if (CollectionUtils.isEmpty(msg)){
            return;
        }
        String phone = msg.remove("phone");
        if (StringUtils.isEmpty(phone)){
            return;
        }
        //发送短信
        smsUtils.sendSms(phone,smsProperties.getSignName(),smsProperties.getVerifyCodeTemplate(), JSONObject.toJSONString(msg));
        log.info("【短信服务】 发送手机验证码  手机号：{}",phone);
    }
}
