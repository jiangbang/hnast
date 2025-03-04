package com.glface.common.exeception;

/**
 * 自定义500异常，错误
 * @author maowei
 */
public class Exception500 extends RuntimeException {
    public Exception500(String message) {
        super(message);
    }
}
