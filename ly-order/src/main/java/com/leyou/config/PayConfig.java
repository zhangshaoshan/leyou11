package com.leyou.config;

import com.github.wxpay.sdk.WXPayConfig;
import lombok.Data;

import java.io.InputStream;

@Data
public class PayConfig implements WXPayConfig {
    private String appID; //公众账号id
    private String mchID;//商户号
    private String key;//生成签名的密钥，微信后台用它来解密
    private int httpConnectTimeoutMs;
    private int httpReadTimeoutMs;
    private String notifyUrl; //支付成功后微信回调地址

    @Override
    public InputStream getCertStream() {
        return null;
    }
}
