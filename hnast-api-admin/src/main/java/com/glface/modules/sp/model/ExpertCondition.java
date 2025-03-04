package com.glface.modules.sp.model;


import com.baomidou.mybatisplus.annotation.TableName;
import com.glface.base.bean.BaseEntity;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Date;

/*
* 专家抽取结果表
* */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@TableName(value = "sp_expert_condition")
public class ExpertCondition extends BaseEntity {
    private String name; //组别名称
    private String sampleId;
    private String conditionId;
    private String expertId;//专家id
    private String expertName;//专家名称
    private String positionalName;//职称名称
    private String studiedName;//专业名称
    private String orgName;//单位名称
    private String mobile;//手机号
    private String star;//专家星级
    private Integer rounds;//抽取轮次
    private String confirmFlag;//0:待确认 1参加 2：不参加
    private Date confirmDate;//确认时间
    private String preFlag;//0保存，1没保存
    private String isItem;//0组长，1不是组长
}
