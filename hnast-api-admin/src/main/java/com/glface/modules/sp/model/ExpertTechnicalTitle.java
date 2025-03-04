package com.glface.modules.sp.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.glface.base.bean.BaseEntity;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 专家技术职称
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@TableName(value = "sp_expert_technical_title")
public class ExpertTechnicalTitle extends BaseEntity {
    private String expertId;//专家id
    private String title;//技术职称
    private Date start;//通过时间
    private Date end;//截止时间
    private String fileId;//证书扫描件.jpg
}
