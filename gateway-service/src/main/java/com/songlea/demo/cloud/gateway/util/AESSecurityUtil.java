package com.songlea.demo.cloud.gateway.util;

import com.songlea.demo.cloud.gateway.constants.ZuulFilterConstants;
import com.google.common.base.Charsets;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.SecureRandom;

/**
 * AES加解密算法
 *
 * @author Song Lea
 */
public class AESSecurityUtil {

    // AES密钥
    private static volatile SecretKey secretKey = null;

    // 生成AES的密钥
    private static SecretKey generatorSecretKey() throws Exception {
        if (secretKey == null) {
            synchronized (AESSecurityUtil.class) {
                if (secretKey == null) {
                    KeyGenerator keyGenerator = KeyGenerator.getInstance(ZuulFilterConstants.AES_ALGORITHM);
                    // 设置加密用的种子以便每次生成的密钥相同
                    // 必须指定种子,否则在Windows下正常而Linux下每次生成的种子不同导致不能解密
                    SecureRandom random = SecureRandom.getInstance(ZuulFilterConstants.SECURE_RANDOM);
                    random.setSeed(ZuulFilterConstants.AES_ALGORITHM_PASSWORD.getBytes(Charsets.UTF_8));
                    keyGenerator.init(128, random); // 256位被限制
                    return keyGenerator.generateKey();
                }
            }
        }
        return secretKey;
    }

    // 加密
    public static String encrypt(String content) throws Exception {
        //1、指定算法、获取Cipher对象
        Cipher cipher = Cipher.getInstance(ZuulFilterConstants.AES_ALGORITHM);
        //2、生成/读取用于加解密的密钥
        SecretKey secretKey = generatorSecretKey();
        //3、用指定的密钥初始化Cipher对象，指定是加密模式，还是解密模式
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        //4、进行最终的加解密操作
        byte[] result = cipher.doFinal(content.getBytes(Charsets.UTF_8));
        return Base64.encodeBase64String(result);
    }

    // 解密
    public static String decrypt(String encryptStr) throws Exception {
        Cipher cipher = Cipher.getInstance(ZuulFilterConstants.AES_ALGORITHM);
        SecretKey secretKey = generatorSecretKey();
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] encodedBytes = Base64.decodeBase64(encryptStr.getBytes(Charsets.UTF_8));
        // 对加密后的字节数组进行解密
        byte[] result = cipher.doFinal(encodedBytes);
        return new String(result, Charsets.UTF_8);
    }
}
