package com.epoch.loan.workshop.common.util;

import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * @Package com.longway.daow.util
 * @Description: base64加密解密帮助类
 * @author: zeping.li
 * @Version V1.0
 * @Date: 2020/9/2 19:19
 */
public class Base64Util {
    /**
     * 解密对象
     */
    private static final Base64.Decoder DECODER = Base64.getDecoder();

    /**
     * 加密对象
     */
    private static final Base64.Encoder ENCODER = Base64.getEncoder();

    /**
     * 加密数据
     *
     * @param key   加密key
     * @param value 加密内容
     * @return
     */
    public static String encode(String key, String value) throws Exception {
        // 判断加密参数合法性
        if (StringUtils.isBlank(key) || StringUtils.isBlank(value)) {
            return null;
        }

        /*
         * 通过秘钥加密
         */
        SecureRandom sr = new SecureRandom();
        byte rawKeyData[] = key.getBytes();
        DESKeySpec dks = new DESKeySpec(rawKeyData);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, sr);
        byte data[] = value.getBytes();
        byte encryptedData[] = cipher.doFinal(data);
        return encryptedData.toString();
    }

    /**
     * 解密数据
     *
     * @param key
     * @param value
     * @return
     * @throws Exception
     */
    public static String decoded(String key, String value) throws Exception {
        // 判断加密参数合法性
        if (StringUtils.isBlank(key) || StringUtils.isBlank(value)) {
            return null;
        }

        /*
         * 通过密匙解密
         */
        SecureRandom sr = new SecureRandom();
        byte rawKeyData[] = key.getBytes();
        DESKeySpec dks = new DESKeySpec(rawKeyData);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, sr);
        byte encryptedData[] = value.getBytes();
        byte decryptedData[] = cipher.doFinal(encryptedData);
        return decryptedData.toString();
    }

    /**
     * 加密
     *
     * @param value 需要加密的内容
     * @return
     */
    public static String encode(String value) throws Exception {
        // 判断加密参数合法性
        if (StringUtils.isBlank(value)) {
            return null;
        }

        // 加密
        return ENCODER.encodeToString(value.getBytes("UTF-8"));
    }

    /**
     * 解密
     *
     * @param value 需要解密的内容
     * @return
     */
    public static String decoded(String value) throws Exception {
        // 判断加密参数合法性
        if (StringUtils.isBlank(value)) {
            return null;
        }

        // 解密
        return new String(DECODER.decode(value), "UTF-8");
    }
}
