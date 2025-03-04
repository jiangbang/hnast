package com.glface.model;

import com.glface.base.bean.BaseEntity;
import lombok.*;
import lombok.experimental.Accessors;


/**
 * 系统日志
 *
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SysLog extends BaseEntity {
    private String title;
    private String remoteAddr;
    private String userAgent;
    private String requestUri;
    private String method;
    private String params;
    private String exception;

}
