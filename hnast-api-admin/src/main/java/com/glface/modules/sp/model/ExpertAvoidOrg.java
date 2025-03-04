package com.glface.modules.sp.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.glface.base.bean.BaseEntity;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 专家回避单位信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@TableName(value = "sp_expert_avoid_org")
public class ExpertAvoidOrg extends BaseEntity {
    private String expertId;//专家id
    private String orgName;//单位名称
    private String orgCode;//统一社会信用代码
}
