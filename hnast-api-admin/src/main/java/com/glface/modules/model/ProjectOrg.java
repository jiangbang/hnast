package com.glface.modules.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.glface.base.bean.BaseEntity;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 项目/课题申报单位信息
 *
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@TableName(value = "pm_project_organization")
public class ProjectOrg extends BaseEntity {
    private String orgName;
    private String chargeName;
    private String chargeMobile;
    private String chargeEmail;
    private String orgAddress;
    private String orgFax;
    private String orgPost;
    private String orgPhone;
    private String superUnitName;
    private String researchField;
    private String legalRepresentative;
    private String orgCode;
    private String orgProperty;
    private int researcherNumber;
    private int seniorNumber;
    private int intermediateNumber;
    private int juniorNumber;
    private int otherNumber;
    private String chargeTitle;


}
