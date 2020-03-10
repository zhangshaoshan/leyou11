package com.leyou.sms.utils;


import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.leyou.sms.config.SmsProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

@Component
@EnableConfigurationProperties(SmsProperties.class)
@Slf4j
public class SmsUtils {

    @Autowired
    private SmsProperties smsProperties;
    @Autowired
    private StringRedisTemplate redisTemplate;

    //产品名称:云通信短信API产品,开发者无需替换
    static final String product = "Dysmsapi";
    //产品域名,开发者无需替换
    static final String domain = "dysmsapi.aliyuncs.com";

    //发送消息redis的 key
    private static final String KEY_PREFIX = "send_msg_phone";
    private static final long SMS_MIN_INTERVAL_IN_MILLIS = 60000;

    public SendSmsResponse sendSms(String phoneNumber,String signName,String templateCode,String templateParam) {
        //做限流 如果该手机号距离上次发送消息小于1分钟，直接返回失败
        String key = KEY_PREFIX + phoneNumber;
        String lastTime = redisTemplate.opsForValue().get(key);
        if (!StringUtils.isEmpty(lastTime)){
            Long time = Long.valueOf(lastTime);
            if (System.currentTimeMillis() - time < SMS_MIN_INTERVAL_IN_MILLIS){//小于60s
                log.info("【短信服务】 发送短信频率过高，被阻止");
                return null;
            }
        }
        try {
            //可自助调整超时时间
            System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
            System.setProperty("sun.net.client.defaultReadTimeout", "10000");

            //初始化acsClient,暂不支持region化
            IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", smsProperties.getAccessKeyId(), smsProperties.getAccessKeySecret());
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
            IAcsClient acsClient = new DefaultAcsClient(profile);

            //组装请求对象-具体描述见控制台-文档部分内容
            SendSmsRequest request = new SendSmsRequest();
            //发送短信的请求为POST
            request.setMethod(MethodType.POST);
            //必填:待发送手机号
            request.setPhoneNumbers(phoneNumber);
            //必填:短信签名-可在短信控制台中找到
            request.setSignName(signName);
            //必填:短信模板-可在短信控制台中找到
            request.setTemplateCode(templateCode);
            //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,
            // 此处的值为request.setTemplateParam("{\"name\":\"Tom\", \"code\":\"123\"}");
            //模板有多少个值，那么参数中的json字符串就需要多少个值
            request.setTemplateParam(templateParam);
            //hint 此处可能会抛出异常，注意catch
            SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
            if (!"OK".equals(sendSmsResponse.getCode())) {
                log.info("【短信服务】 发送短信失败，phoneNumber：{} 原因：{}",phoneNumber,sendSmsResponse.getMessage());
            }
            //将短信发送时间 写入缓存，，并且设置生存时间
            redisTemplate.opsForValue().set(key,String.valueOf(System.currentTimeMillis()),1, TimeUnit.MINUTES);
            return sendSmsResponse;
        }catch (Exception e){
            //这里不能重试，只能让前端在30s或者60s再去发送，否则会触发限流机制，让整个服务器都无法访问
            log.error("【短信服务】  短信服务异常  手机号码：{}",phoneNumber,e);
            return null;
        }
    }
}
