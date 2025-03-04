package com.glface.modules.sp.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.glface.base.bean.BaseEntity;
import lombok.*;
import lombok.experimental.Accessors;


/**
 * 专家附件表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@TableName(value = "sp_expert_file")
public class ExpertFile extends BaseEntity {
    private String expertId;//专家id
    private String educationFileId;//学历、学位材料
    private String identityFileId;//身份证
    private String orgFileId;//所在单位意见
    private String commitmentFileId;//承诺书
    private String academicianFileId;//两院院士电子件
    private String secrecyFileId;//保密协议
    private String reviewFileId;//评审纪律协议
}
