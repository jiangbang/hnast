package com.glface.modules.sp.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.glface.base.bean.BaseEntity;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 专家抽取
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@TableName(value = "sp_sample")
public class Sample extends BaseEntity {
    private String projectName;//项目名称
    private String baseCategoryIds;//类别逗号 英文逗号隔开
    private Date reviewDate;//评审时间
    private String status;//状态  0未抽取  1已抽取
    private Date  statusDate;//状态变更时间
    private String stars;//星级 英文逗号隔开
    private Integer number;//计划抽取人数
    private String result;//最新抽取结果
}
