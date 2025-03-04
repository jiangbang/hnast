package com.glface.base.utils;

import cn.hutool.core.util.ReUtil;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 数据验证
 * @author maowei
 */
public class Valid {
    /**
     * 判断字符串是否为 账号格式 字母开头包含数字字母下划线3到16位
     *
     */
    public static boolean isAccount(String name) {
        String regex = "^[a-zA-Z]\\w{2,15}$";
        return ReUtil.isMatch(regex, name);
    }
    public static boolean isPassword(String name) {
        // String regex = "^[A-Za-z0-9]+$";
        String regex = "(?=.*\\d)(?=.*[a-zA-Z])(?=.*[^a-zA-Z0-9]).{6,16}";
        return ReUtil.isMatch(regex, name);
    }
    /**
     * 判断密码是否为弱口令密码
     *
     * */
    public static boolean isPasswords(String password){
        String regex = "(?=.*\\d)(?=.*[a-zA-Z])(?=.*[^a-zA-Z0-9]).{6,16}";
        return ReUtil.isMatch(regex,password);
    }

    /**
     * 判断字符串是否为 手机号码格式
     */
    public static boolean isPhone(String phone) {
        //String regex = "^(13[0-9]|14[5|7]|15[0|1|2|3|4|5|6|7|8|9]|18[0|1|2|3|5|6|7|8|9])\\d{8}$";
        String regex = "^1(3\\d|4[5-9]|5[0-35-9]|6[2567]|7[0-8]|8\\d|9[0-35-9])\\d{8}$";
        return ReUtil.isMatch(regex, phone);
    }
    /**
     * 判断字符串是否为 邮箱地址格式
     */

    public static boolean isMail(String mail) {
        String regex = "^([a-zA-Z0-9_\\.\\-])+\\@(([a-zA-Z0-9\\-])+\\.)+([a-zA-Z0-9]{2,4})+$";
        return ReUtil.isMatch(regex, mail);
    }
    /**
     * 判断字符串是否为 Host格式
     */
    public static boolean isHost(String host) {
        String regex = "^((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})(\\.((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})){3}$";
        return ReUtil.isMatch(regex, host);
    }
    /**
     * 判断字符串是否为 驱动端口格式
     */
    public static boolean isDriverPort(int port) {
        String regex = "^8[6-7][0-9]{2}$";
        return ReUtil.isMatch(regex, String.valueOf(port));
    }
}
