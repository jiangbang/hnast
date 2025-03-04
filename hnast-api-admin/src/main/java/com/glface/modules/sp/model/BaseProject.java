package com.glface.modules.sp.model;


import com.baomidou.mybatisplus.annotation.TableName;
import com.glface.base.bean.BaseEntity;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 制裁项目类别信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@TableName(value = "sp_base_project")
public class BaseProject extends BaseEntity {
    private String name;
    private String officeId;//归口部门
    private int sort;//排序 升序
}
