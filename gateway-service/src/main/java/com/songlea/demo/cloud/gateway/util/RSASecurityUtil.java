package com.songlea.demo.cloud.gateway.util;

import com.songlea.demo.cloud.gateway.constants.ZuulFilterConstants;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import static com.songlea.demo.cloud.gateway.constants.ZuulFilterConstants.MAX_DECRYPT_BLOCK;
import static com.songlea.demo.cloud.gateway.constants.ZuulFilterConstants.MAX_ENCRYPT_BLOCK;

/**
 * 加解密、密钥、数字签名管理工具类
 *
 * @author Song Lea
 */
public class RSASecurityUtil {

    /**
     * 生成RSA算法的公私密钥对
     * 公钥使用key:ZuulFilterConstants.PUBLIC_KEY获取
     * 私钥使用key:ZuulFilterConstants.PRIVATE_KEY获取
     *
     * @return map 公私密钥对
     * @throws NoSuchAlgorithmException 算法名称不正确
     */
    public static Map<String, String> generateRSAKeyPairs() throws NoSuchAlgorithmException {
        Map<String, String> keyPairMap = new HashMap<>(2);
        // 初始化算法
        KeyPairGenerator generator = KeyPairGenerator.getInstance(ZuulFilterConstants.RSA_ALGORITHM);
        // 秘钥长度
        generator.initialize(1024, new SecureRandom());
        // 生成秘钥对
        KeyPair keyPair = generator.genKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();
        keyPairMap.put(ZuulFilterConstants.PUBLIC_KEY, Base64.encodeBase64String(publicKey.getEncoded()));
        keyPairMap.put(ZuulFilterConstants.PRIVATE_KEY, Base64.encodeBase64String(privateKey.getEncoded()));
        return keyPairMap;
    }

    /**
     * 用私钥对信息生成数字签名
     *
     * @param data          已加密数据byte[]
     * @param privateKeyStr 私钥(BASE64编码的字符串)
     * @return String 数字签名后数据
     * @throws Exception 执行异常
     */
    public static String digitalSignature(byte[] data, String privateKeyStr) throws Exception {
        byte[] keyBytes = Base64.decodeBase64(privateKeyStr);
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ZuulFilterConstants.RSA_ALGORITHM);
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        // 数字签名(MD5withRSA算法)
        Signature signature = Signature.getInstance(ZuulFilterConstants.SIGNATURE_ALGORITHM);
        signature.initSign(privateKey);
        signature.update(data);
        return Base64.encodeBase64String(signature.sign());
    }

    /**
     * 校验数字签名
     *
     * @param data         已加密数据byte[]
     * @param publicKeyStr 公钥(BASE64编码字符串)
     * @param sign         数字签名后数据
     * @return true表示数字签名校验通过
     * @throws Exception 执行异常
     */
    public static boolean verifySignature(byte[] data, String publicKeyStr, String sign) throws Exception {
        byte[] keyBytes = Base64.decodeBase64(publicKeyStr);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ZuulFilterConstants.RSA_ALGORITHM);
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance(ZuulFilterConstants.SIGNATURE_ALGORITHM);
        signature.initVerify(publicKey);
        signature.update(data);
        return signature.verify(Base64.decodeBase64(sign));
    }

    /**
     * 使用公钥加密(要分段加密)
     *
     * @param data         待加密的数据
     * @param publicKeyStr 公钥字符串
     * @return byte[] 加密后的数据byte[]
     * @throws Exception 执行异常
     */
    public static byte[] encryptByPublicKey(byte[] data, String publicKeyStr) throws Exception {
        byte[] keyBytes = Base64.decodeBase64(publicKeyStr);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ZuulFilterConstants.RSA_ALGORITHM);
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicK);
        // 对数据分段加密
        return handlerDataBySegment(data, cipher, MAX_ENCRYPT_BLOCK);
    }

    /**
     * 使用私钥解密(要分段解密)
     *
     * @param encryptedData 待解密的数据
     * @param privateKeyStr 私钥字符串
     * @return String 解密后的数据
     */
    public static byte[] decipherByPrivateKey(byte[] encryptedData, String privateKeyStr) throws Exception {
        byte[] keyBytes = Base64.decodeBase64(privateKeyStr);
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ZuulFilterConstants.RSA_ALGORITHM);
        Key privateK = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateK);
        // 对数据分段解密
        return handlerDataBySegment(encryptedData, cipher, MAX_DECRYPT_BLOCK);
    }

    // 对数据分段加密或解密
    private static byte[] handlerDataBySegment(byte[] data, Cipher cipher, int dataBlock) throws Exception {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            int inputLen = data.length;
            int offSet = 0;
            byte[] cache;
            int i = 0;
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > dataBlock) {
                    cache = cipher.doFinal(data, offSet, dataBlock);
                } else {
                    cache = cipher.doFinal(data, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * dataBlock;
            }
            return out.toByteArray();
        }
    }
}
