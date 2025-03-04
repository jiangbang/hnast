package com.glface.modules.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.glface.base.bean.BaseEntity;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 项目类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@TableName(value = "pm_project_category")
public class ProjectCategory extends BaseEntity {
    /**
     * 类型名称
     */
    private String name;
    /**
     * 项目金额上限
     */
    private float amountMax;
    /**
     * 项目金额下限
     */
    private float amountMin;
}
