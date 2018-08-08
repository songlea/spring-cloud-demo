package com.songlea.demo.cloud.gateway.util;

import com.google.common.base.Charsets;
import com.songlea.demo.cloud.gateway.constants.ZuulFilterConstants;
import org.apache.commons.codec.binary.Base64;

import java.util.Map;

public class RSASecurityUtilTest {

    @org.junit.Test
    public void encryptSecurityUtilTest() throws Exception {
        Map<String, String> map = RSASecurityUtil.generateRSAKeyPairs();
        map.forEach((k, v) -> System.out.println(k + "：" + v));

        String content = "{\"startDate\":\"2018-03-10\",\"endDate\":\"2018-04-09\"}";
        System.out.println("content原文：" + content);
        byte[] a = RSASecurityUtil.encryptByPublicKey(content.getBytes(Charsets.UTF_8), map.get(ZuulFilterConstants.PUBLIC_KEY));
        System.out.println("content公钥加密后Base64编码：" + Base64.encodeBase64String(a));

        byte[] b = RSASecurityUtil.decipherByPrivateKey(a, map.get(ZuulFilterConstants.PRIVATE_KEY));
        System.out.println("content私钥解密后Base64编码：" + Base64.encodeBase64String(a));
        System.out.println("content解密后原文：" + new String(b, Charsets.UTF_8));

        String c = RSASecurityUtil.digitalSignature(content.getBytes(Charsets.UTF_8), map.get(ZuulFilterConstants.PRIVATE_KEY));
        System.out.println("content的数字签名Base64编码：" + c);
        String content2 = "这是一句其他的话与content无关！";
        String content3 = "每个月，我们帮助 1000 万的开发者解决各种各样的技术问题。并助力他们在技术能力、职业生涯、影响力上获得提升。";
        System.out.println("content2原文：" + content2);
        System.out.println("content3原文：" + content3);
        System.out.println("content2的数字签名验证： " + RSASecurityUtil.verifySignature(content2.getBytes(Charsets.UTF_8),
                map.get(ZuulFilterConstants.PUBLIC_KEY), c));
        System.out.println("content3的数字签名验证： " + RSASecurityUtil.verifySignature(content3.getBytes(Charsets.UTF_8),
                map.get(ZuulFilterConstants.PUBLIC_KEY), c));
    }
}