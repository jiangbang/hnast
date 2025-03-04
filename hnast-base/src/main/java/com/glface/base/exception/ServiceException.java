package com.glface.base.exception;

/**
 * 自定义服务 异常
 */
public class ServiceException extends RuntimeException {
    public ServiceException(String message) {
        super(message);
    }
}
