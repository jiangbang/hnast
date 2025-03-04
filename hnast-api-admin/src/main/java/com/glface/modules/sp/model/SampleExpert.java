package com.glface.modules.sp.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.glface.base.bean.BaseEntity;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 专家抽取结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@TableName(value = "sp_sample_expert")
public class SampleExpert extends BaseEntity {
    private String sampleId;
    private String baseCategoryId;//分类下的专家
    private String expertId;//专家id
    private Integer rounds;//抽取轮次
    private String confirmFlag;//0:待确认 1参加 2：不参加
    private Date confirmDate;//确认时间
}
