package com.glface.base.utils;

import cn.hutool.crypto.digest.MD5;
import com.google.common.base.Charsets;

import java.util.Base64;

/**
 * 封装各种格式的编码解码工具类.
 * 1.Commons-Codec的 hex/base64 编码
 * 2.自制的base62 编码
 * 3.Commons-Lang的xml/html escape
 * 4.JDK提供的URLEncoder
 * @author maowei
 */
public class Encodes {
    /**
     * 获取 md5 加密编码
     *
     * @param str String
     * @return String
     */
    public static String md5(String str) {
        MD5 md5 = MD5.create();
        return md5.digestHex(str, Charsets.UTF_8);
    }

    /**
     * 获取 md5 & salt 加密编码
     *
     * @param str  String
     * @param salt String
     * @return String
     */
    public static String md5(String str, String salt) {
        System.out.println("md5---"+ md5(md5(str) + salt));
        return md5(md5(str) + salt);
    }

    /**
     * 将字符串进行Base64编码
     *
     * @param str String
     * @return Byte Array
     */
    public static byte[] encode(String str) {
        return Base64.getEncoder().encode(str.getBytes(Charsets.UTF_8));
    }

    /**
     * 将字节流进行Base64编码
     *
     * @param bytes Byte Array
     * @return Byte Array
     */
    public static String encode(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * 必须配合encode使用，用于encode编码之后解码
     *
     * @param str String
     * @return Byte Array
     */
    public static byte[] decode(String str) {
        return Base64.getDecoder().decode(str);
    }

    /**
     * 必须配合encode使用，用于encode编码之后解码
     *
     * @param input Byte Array
     * @return Byte Array
     */
    public static byte[] decode(byte[] input) {
        return Base64.getDecoder().decode(input);
    }

    public static void main(String[] args) {
        String p = Encodes.md5("abc123456",null);
        System.out.println(p);
    }
}
