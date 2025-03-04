package com.glface.modules.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.glface.base.bean.BaseEntity;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 项目模板表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@TableName(value = "pm_project_template")
public class ProjectTemplate extends BaseEntity {
    /**
     * 模板名称
     */
    private String name;
    /**
     * 文件id
     */
    private String fileId;
    private String categoryId;
    private Integer sort = 1000;// 默认1000  升序
}
