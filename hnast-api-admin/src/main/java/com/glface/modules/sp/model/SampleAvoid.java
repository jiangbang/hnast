package com.glface.modules.sp.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.glface.base.bean.BaseEntity;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 专家抽取回避条件
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@TableName(value = "sp_sample_avoid")
public class SampleAvoid extends BaseEntity {
    private String sampleId;
    private String type;//0:工作单位  1:专家姓名
    private String orgName;//单位名称
    private String orgCode;//统一社会信用代码
    private String expertId;//专家id专家id
    private String appointFlag;//指定
}
