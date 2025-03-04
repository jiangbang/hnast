package com.glface.log;

import java.io.Serializable;

public class ResponseResult implements Serializable {

    private static final long serialVersionUID = 6054052582421291408L;

    private String message;
    private Object data;
    private int code;
    private boolean success;
    private Long total;

    public ResponseResult(){}

    public ResponseResult(boolean success, Object data) {
        this.success = success;
        this.data = data;
    }

    public ResponseResult(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public ResponseResult setMessage(String message) {
        this.message = message;
        return this;
    }

    public Object getData() {
        return data;
    }

    public ResponseResult setData(Object data) {
        this.data = data;
        return this;
    }

    public boolean getSuccess() {
        return success;
    }

    public ResponseResult setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public int getCode() {
        return code;
    }

    public ResponseResult setCode(int code) {
        this.code = code;
        return this;
    }

    public Long getTotal() {
        return total;
    }

    public ResponseResult setTotal(Long total) {
        this.total = total;
        return this;
    }

}
