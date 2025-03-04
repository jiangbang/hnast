package com.glface.modules.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.glface.base.bean.BaseEntity;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 项目
 *
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@TableName(value = "pm_project")
public class Project extends BaseEntity {
    private String name;
    private String code;
    private String categoryId;
    private String areaId;
    private String planTypeId;
    private String batchId;
    private String projectType;
    /**
     * 申报单位
     */
    private String orgId;
    /**
     * 项目类容
     */
    private String contentId;
    /**
     * 项目预算
     */
    private float budget;
    /**
     * 申请金额
     */
    private float funds;
    private String bank;
    private String cardNo;
    private String accounts;
    private Date startDate;
    private Date endDate;

    private String status;
    //是否通过最终评审 1通过
    private String passReviewFlag;
    private String officeId;
    private String qxOfficeId;
    private String special;

    private Date applyDate;//申请时间(用户提交时间)

    private String zipPath;//归档文件路径


}
