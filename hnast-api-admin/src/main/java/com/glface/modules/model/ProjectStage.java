package com.glface.modules.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.glface.base.bean.BaseEntity;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 项目实施阶段表
 *
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@TableName(value = "pm_project_stage")
public class ProjectStage extends BaseEntity {
    private String projectId;
    private String name;
    private Date startDate;
    private Date endDate;
    private float money;
    private Integer sort = 0;
}
