package com.glface.base.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Aes/Rsa 加密密钥
 * 使用AES对称密码体制对传输数据加密，同时使用RSA不对称密码体制来传送AES的密钥，就可以综合发挥AES和RSA的优点同时
 * 避免它们缺点来实现一种新的数据加密方案
 * @author maowei
 */
public class Keys {

    /**
     * Aes 密钥
     */
    @Data
    @AllArgsConstructor
    public class Aes {
        private String privateKey;
    }

    /**
     * RSA 密钥对
     */
    @Data
    @AllArgsConstructor
    public class Rsa {
        private String publicKey;
        private String privateKey;
    }
}
