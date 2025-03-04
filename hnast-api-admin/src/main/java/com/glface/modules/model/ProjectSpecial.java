package com.glface.modules.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.glface.base.bean.BaseEntity;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 项目专项信息
 *
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@TableName(value = "pm_project_special")
public class ProjectSpecial extends BaseEntity {
    private String name;
    private String planTypeId;

}
