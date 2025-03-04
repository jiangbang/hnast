package com.glface.modules.sp.model;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.glface.base.bean.BaseEntity;
import lombok.*;
import lombok.experimental.Accessors;

/*
* 专家抽取条件表
* */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@TableName(value = "sp_sample_condition")
public class SampleCondition extends BaseEntity {
    private String name; //组别名称
    private String baseCategoryId;
    private String sampleId;
    private Integer num;    //抽取专家数
    private String star;//专家星级
    private String hideCondition;//屏蔽工作单位
    private String hideExpertName;// 屏蔽专家
    private String hideMajorName;//屏蔽行业类别
    private String hideExpertProject;//指定制裁项目


    @TableField(exist = false)
    private String bseCategoryName;//类别名称,数据库不存在这个字段
    @TableField(exist = false)
    private String baseMajorName;//行业类别名称,数据库不存在这个字段
    @TableField(exist = false)
    private String index;
    @TableField(exist = false)
    private String isType;//01:代表首轮抽取;02:代表补抽;
}
