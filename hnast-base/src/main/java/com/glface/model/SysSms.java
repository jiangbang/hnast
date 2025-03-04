package com.glface.model;

import com.glface.base.bean.BaseEntity;
import lombok.*;
import lombok.experimental.Accessors;


/**
 * 短信日志
 *
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SysSms extends BaseEntity {
    private int type;
    private String userId;
    private String content;
    private String phone;
    private String code;
    private String returnCode;
    private String returnDescription;
    private String returnResult;
}
