package com.glface.modules.sp.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.glface.base.bean.BaseEntity;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 专家职业资格
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@TableName(value = "sp_expert_qualification")
public class ExpertQualification extends BaseEntity {
    private String expertId;//专家id
    private String title;//职业资格名称
    private String orgName;//注册单位
    private String number;//职业资格证书号
    private Date start;//注册时间
    private Date end;//过期时间
    private String fileId;//证书扫描件.jpg
}
