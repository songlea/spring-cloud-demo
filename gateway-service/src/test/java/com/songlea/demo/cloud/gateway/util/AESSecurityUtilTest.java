package com.songlea.demo.cloud.gateway.util;

import org.junit.Test;

public class AESSecurityUtilTest {

    @Test
    public void encrypt() throws Exception {
        String str = "partnerNo=pengda&phone=13888888888";
        System.out.println("AES加密前的数据：" + str);
        System.out.println("AES加密后的数据Base64编码：" + AESSecurityUtil.encrypt(str));
    }

    @Test
    public void decrypt() throws Exception {
        String str = "U4ymt+UX/iGPRX/mrdMh/h07GixlHCM7ZsyJqKhKkQZ92Tl/cpdYiC2GfGEa6UIGd";
        System.out.println("AES解密前Base64编码的数据：" + str);
        System.out.println("AES解密后的数据：" + AESSecurityUtil.decrypt(str));
    }

}