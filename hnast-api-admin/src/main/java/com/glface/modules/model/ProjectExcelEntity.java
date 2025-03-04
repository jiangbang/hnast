package com.glface.modules.model;

import lombok.Data;

@Data
public class ProjectExcelEntity{
    private String name;//项目名称
    private String code;//项目编号
    private String specialName;//项目专项名称
    private String planTypeName;//计划类别名称
    private String categoryName;//类别
    private String orgName;//申报单位
    private float funds;//申报金额
}
