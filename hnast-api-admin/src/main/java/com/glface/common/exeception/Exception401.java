package com.glface.common.exeception;

/**
 * 自定义401异常，未授权
 * @author maowei
 */
public class Exception401 extends RuntimeException {
    public Exception401(String message) {
        super(message);
    }
}
