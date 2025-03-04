package com.glface.constant;

/**
 * 平台常量
 * @author maowei
 */
public interface Common {

    /**
     * 对称加密算法
     */
    String KEY_ALGORITHM_AES = "AES";

    /**
     * 非对称加密算法
     */
    String KEY_ALGORITHM_RSA = "RSA";

    /**
     * 时间格式化
     */
    String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 时区
     */
    String TIMEZONE = "GMT+8";

    /**
     * 设备常量
     */
    interface Device {
        String ONLINE = "ONLINE";
        String OFFLINE = "OFFLINE";
        String MAINTAIN = "MAINTAIN";
        String FAULT = "FAULT";
    }

    /**
     * 数据类型
     */
    interface ValueType {
        String HEX = "hex";
        String BYTE = "byte";
        String SHORT = "short";
        String INT = "int";
        String LONG = "long";
        String FLOAT = "float";
        String DOUBLE = "double";
        String BOOLEAN = "boolean";
        String STRING = "string";
    }

    /**
     * 服务名称 & 服务基地址
     */
    interface Service {
        String IOT_AUTH_TOKEN = "token";
    }

    /**
     * 缓存Key
     */
    interface Cache {
        /**
         * token 在 redis 中的失效时间
         */
        int TOKEN_CACHE_TIMEOUT = 12;

        String CHANNEL_DEVICE_ERROR = "device_error";

        /**
         * 分隔符
         */
        String SEPARATOR = "::";

        String REAL_TIME_VALUE_KEY_PREFIX = "device_data" + Common.Cache.SEPARATOR;
    }

}
