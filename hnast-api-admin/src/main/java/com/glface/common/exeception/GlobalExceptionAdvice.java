package com.glface.common.exeception;

import com.glface.base.bean.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


/**
 * controller 增强器  应用到所有@RequestMapping中
 * 全局异常处理
 * ResponseEntityExceptionHandler预提供了一个处理Spring常见异常的Exceptionhandler
 * @author maowei
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionAdvice extends ResponseEntityExceptionHandler {

    /**
     * 覆盖handleExceptionInternal这个汇总处理方法
     */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.error("全局异常处理: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(R.fail(ex.getMessage()),status);
    }

    /**
     * 401错误，未授权
     */
    @ExceptionHandler(value = { Exception401.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R unauthorizedException(Exception ex) {
        log.warn("401异常: {}", ex.getMessage(), ex);
        return R.fail(ex.getMessage());
    }

    /**
     * 403错误，权限不足
     */
    @ExceptionHandler(value = { Exception403.class, AccessDeniedException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public R forbiddenException(Exception ex) {
        log.warn("403异常: {}", ex.getMessage(), ex);
        return R.fail(ex.getMessage());
    }

    /**
     * 404错误，处理器不存在异常，资源不存在异常
     */
    @ExceptionHandler(value = { Exception404.class })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public R noHandlerFoundException(Exception ex) {
        log.warn("404异常: {}", ex.getMessage(), ex);
        return R.fail(ex.getMessage());
    }

    /**
     * 500异常
     */
    @ExceptionHandler(value = {Exception500.class,Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R exception(Exception ex) {
        log.error("500异常: {}", ex.getMessage(), ex);
        return R.fail(ex.getMessage());
    }

    /**
     * ServiceException
     */
    @ExceptionHandler(value = {ServiceException.class })
    @ResponseStatus(HttpStatus.OK)
    public R serviceException(Exception ex) {
        return R.fail(ex.getMessage());
    }

}
