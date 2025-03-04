package com.glface.modules.sp.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.glface.base.bean.BaseEntity;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 专家评标专业
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@TableName(value = "sp_expert_major")
public class ExpertMajor extends BaseEntity {
    private String expertId;//专家id
    private String baseMajorId;//专业id
    private String seniorFlag;//是否资深 1:是 0否
    private String fileId;//职业资格、技术职称.jpg
}
