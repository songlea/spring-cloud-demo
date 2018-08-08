package com.songlea.demo.cloud.gateway.constants;

/**
 * 保存过滤器的常量
 *
 * @author Song Lea
 */
public class ZuulFilterConstants {

    // 算法
    public static final String SIGNATURE_ALGORITHM = "MD5withRSA";
    public static final String RSA_ALGORITHM = "RSA";
    public static final String AES_ALGORITHM = "AES";
    public static final String SECURE_RANDOM = "SHA1PRNG";
    public static final String AES_ALGORITHM_PASSWORD = "SPRING_CLOUD_GATEWAY";

    // 获取密钥对key
    public static final String PUBLIC_KEY = "RSAPublicKey";
    public static final String PRIVATE_KEY = "RSAPrivateKey";

    // RSA最大加密与解密明文大小
    public static final int MAX_ENCRYPT_BLOCK = 117;
    public static final int MAX_DECRYPT_BLOCK = 128;

    // 过滤默认优先级
    public static final int DEFAULT_ORDER = 999;

}