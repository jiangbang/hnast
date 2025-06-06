package com.glface.model;

import com.glface.base.bean.BaseEntity;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 用户部门
 *
 * @author maowei
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SysUserOffice extends BaseEntity {
    private String userId;
    private String officeId;
}
