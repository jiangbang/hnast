package com.glface.base.dto;

/**
 * 请求对象dto<-->实体对象do转换器
 * DTO, Data Transfer Object，数据传输对象，可以简单理解成请求中的对象
 * DO，领域对象(Domain Object)，实体对象
 * @author maowei
 */
public interface Converter<D,T> {
    /**
     * DTO 转 DO
     * @param d Do对象
     */
    void convertToDo(D d);

    /**
     * DO 转 DTO
     *
     * @param d Do对象
     */
    T convert(D d);
}
