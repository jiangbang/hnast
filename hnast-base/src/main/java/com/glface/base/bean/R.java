package com.glface.base.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 接口请求时返回对象
 *
 * @author maowei
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class R<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private int status = 0;// 0失败 1成功

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message = "失败";

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    /**
     * 成功
     *
     * @return Response
     */
    @SuppressWarnings("unchecked")
    public static <T> R<T> ok() {
        return new R().success();
    }

    /**
     * 成功 自定义提示信息
     */
    @SuppressWarnings("unchecked")
    public static <T> R<T> ok(String message) {
        return new R().success(message);
    }

    /**
     * 成功 返回结果
     *
     * @param data 返回结果
     */
    @SuppressWarnings("unchecked")
    public static <T> R<T> ok(T data) {
        return new R(data).success();
    }

    /**
     * 成功 返回结果 & 自定义提示信息
     *
     * @param data 返回结果
     * @return Response
     */
    @SuppressWarnings("unchecked")
    public static <T> R<T> ok(T data, String message) {
        return new R(data).success(message);
    }

    /**
     * 失败
     *
     * @return Response
     */
    @SuppressWarnings("unchecked")
    public static <T> R<T> fail() {
        return new R().failure();
    }

    /**
     * 失败 自定义提示信息
     *
     * @return Response
     */
    @SuppressWarnings("unchecked")
    public static <T> R<T> fail(String message) {
        return new R().failure(message);
    }

    /**
     * 失败 返回结果
     *
     * @param data 返回结果
     * @return Response
     */
    @SuppressWarnings("unchecked")
    public static <T> R<T> fail(T data) {
        return new R(data).failure();
    }

    /**
     * 失败 返回结果 & 自定义提示信息
     *
     * @param data 返回结果
     */
    @SuppressWarnings("unchecked")
    public static <T> R<T> fail(T data, String message) {
        return new R(data).failure(message);
    }

    /**
     * 构造函数
     *
     * @param data 数据
     */
    private R(T data) {
        this.data = data;
    }

    /**
     * 成功
     *
     * @return Response
     */
    private R success() {
        this.status = 1;
        this.message = "ok";
        return this;
    }

    /**
     * 成功 自定义提示信息
     *
     * @param message 成功提示信息
     * @return Response
     */
    private R success(String message) {
        this.status = 1;
        this.message = message;
        return this;
    }

    /**
     * 失败
     *
     * @return Response
     */
    private R failure() {
        this.status = 0;
        this.message = "失败";
        return this;
    }

    /**
     * 失败 自定义提示信息
     *
     * @param message 错误提示信息
     * @return Response
     */
    private R failure(String message) {
        this.status = 0;
        this.message = message;
        return this;
    }

    public boolean isOk() {
        return status == 1;
    }
}
