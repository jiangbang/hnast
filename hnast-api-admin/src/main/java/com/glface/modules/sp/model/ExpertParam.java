package com.glface.modules.sp.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 接收专家基本信息参数
 */
@Data
@NoArgsConstructor
public class ExpertParam {
    private String id;
    private String name;//姓名
    private String sex;//性别
    private String birthday;//'出生年月'
    private String identityCard;//身份证
    private String parties;//党派
    private String educationId;//学历
    private String degreeId;//学位
    private String studied;//所学专业
    private String specialty;//专业特长
    private String majorCategoryId;//现从事行业
    private String orgName;//工作单位
    private String job;//职务
    private String positionalId;//职称
    private String address;//联系地址
    private String post;//邮政编码
    private String mobile;//联系电话
    private String email;//电子邮箱
    private String wx;//微信号
    private String qq;//QQ号
    private String pictureFileId;//照片

    //详细信息
    private String work;//近五年主要专业工作经历
    private String achievement;//发明创造、科研成果、著作译著等成果
    private String partTime;//社会兼职情况
    private String former;//曾担任评审专家情况

    //申请专家库类别
    private List<String> categoryIds;

    //回避单位信息
    private List<ExpertAvoidOrgParam> avoidOrgs;

}
