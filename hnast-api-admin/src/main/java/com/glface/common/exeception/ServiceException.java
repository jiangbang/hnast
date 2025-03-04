package com.glface.common.exeception;

import com.glface.common.web.ApiCode;

/**
 * 自定义异常
 * @author maowei
 */
public class ServiceException extends RuntimeException {
    public ServiceException(String message) {
        super(message);
    }
    public ServiceException(ApiCode apiCode) {
        super(apiCode.getMsg());
    }
}
