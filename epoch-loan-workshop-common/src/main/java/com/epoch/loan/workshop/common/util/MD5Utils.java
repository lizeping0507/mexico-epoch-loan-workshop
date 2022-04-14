package com.epoch.loan.workshop.common.util;

import org.apache.commons.codec.digest.DigestUtils;

import java.security.MessageDigest;

public class MD5Utils {

    public static String md5Hex(String context) {
        String md5Str = DigestUtils.md5Hex(context);
        return md5Str;
    }

    public static String string2MD5(String inStr) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            throw new RuntimeException("MD5 Exception");
        }
        char[] charArray = inStr.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++) {
            byteArray[i] = (byte) charArray[i];
        }
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuilder hexValue = new StringBuilder();
        for (byte md5Byte : md5Bytes) {
            int val = ((int) md5Byte) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }
}
