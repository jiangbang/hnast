package com.glface.modules.sp.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.glface.base.bean.BaseEntity;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 专家审核执行过程
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@TableName(value = "sp_expert_process")
public class ExpertProcess extends BaseEntity {
    private String expertId;//专家id
    private String userId;//执行人
    private String officeId;//执行人归属部门
    private String result;//审批结果 对应数据字典expertStatus
    private String resultOpinion;//评审意见
}
