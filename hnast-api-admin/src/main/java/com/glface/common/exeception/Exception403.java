package com.glface.common.exeception;

/**
 * 自定义403异常，权限不足
 * @author maowei
 */
public class Exception403 extends RuntimeException {
    public Exception403(String message) {
        super(message);
    }
}
