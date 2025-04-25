package com.glface.base.utils;

import cn.hutool.core.util.ReUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

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
//        String regex = "(?=.*\\d)(?=.*[a-zA-Z])(?=.*[^a-zA-Z0-9]).{6,16}";
//        return ReUtil.isMatch(regex, name);

        return validatePassword(name);
    }
    /**
     * 判断密码是否为弱口令密码
     *
     * */
    public static boolean isPasswords(String password){
//        String regex = "(?=.*\\d)(?=.*[a-zA-Z])(?=.*[^a-zA-Z0-9]).{6,16}";
//        return ReUtil.isMatch(regex,password);
        return validatePassword(password);
    }

    public static void main(String[] args) {
        validatePassword("Aadmin1234567.");
    }

    public static boolean validatePassword(String password) {
        // 基础校验
        if (!checkBasicRule(password)) {
            return false;
        }
        // 序列校验
        if (hasInvalidSequence(password)) {
            return false;
        }
        // 敏感信息校验
//        if (containsSensitiveInfo(password)) return false;
        return true;
    }

    private static boolean checkBasicRule(String password) {
        // 基础规则正则
        String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[\\W_]).{8,}$";
        return ReUtil.isMatch(regex, password);
    }

    private static boolean hasInvalidSequence(String password) {
        // 排除连续5+相同数字
        if (password.matches(".*(\\d)\\1{4}.*")) {
            return false;
        }

        // 递增/递减序列检测
        char[] chars = password.toCharArray();
        for (int i=0; i<chars.length-5; i++) {
            if (isConsecutive(chars, i, 1) ||
                    isConsecutive(chars, i, -1)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isConsecutive(char[] arr, int start, int step) {
        for (int j=0; j<5; j++) {
            if (arr[start+j+1] - arr[start+j] != step) {
                return false;
            }
        }
        return true;
    }

    private boolean containsSensitiveInfo(String password) {
        // 用户信息校验需外部传入（示例字段）
        Set<String> forbiddenSequences = new HashSet<>(); // 机构首字母序列等
        String userBirthday = ""; // 用户生日
        String userPhone = ""; // 用户手机号

        String lowerPwd = password.toLowerCase();
        // 生日检测
        if (userBirthday != null && lowerPwd.contains(userBirthday)) {
            return true;
        }
        // 手机号检测
        if (userPhone != null && lowerPwd.contains(userPhone)) {
            return true;
        }
        // 机构序列检测
        return forbiddenSequences.stream()
                .anyMatch(seq -> lowerPwd.contains(seq.toLowerCase()));
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
