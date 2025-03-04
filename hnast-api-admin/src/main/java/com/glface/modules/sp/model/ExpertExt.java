package com.glface.modules.sp.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.glface.base.bean.BaseEntity;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 专家内容表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@TableName(value = "sp_expert_ext")
public class ExpertExt extends BaseEntity {
    private String expertId;//专家id
    private String work;//近五年主要专业工作经历
    private String achievement;//发明创造、科研成果、著作译著等成果
    private String partTime;//社会兼职情况
    private String former;//曾担任评审专家情况
}
