package com.glface.security;

import org.springframework.core.annotation.Order;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

/**
 * Spring Framework 远程代码执行漏洞修复
 */
@ControllerAdvice
@Order(10000)
public class GlobalControllerAdvice{
    @InitBinder
    public void setAllowedFields(WebDataBinder dataBinder){
        String[] abd= new String[]{"class.*","Class.*","*.class.*","*.Class.*"};
        dataBinder.setDisallowedFields(abd);
    }
}
