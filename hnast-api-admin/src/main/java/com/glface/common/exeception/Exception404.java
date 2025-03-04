package com.glface.common.exeception;

/**
 * 自定义404异常，资源不存在
 * @author maowei
 */
public class Exception404 extends RuntimeException {
    public Exception404(String message) {
        super(message);
    }
}
