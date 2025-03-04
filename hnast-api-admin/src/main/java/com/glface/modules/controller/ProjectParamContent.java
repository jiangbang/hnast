package com.glface.modules.controller;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 接收新建项目类容参数
 */
@Data
@NoArgsConstructor
public class ProjectParamContent {
    private String projectId;
    private String basis;//立项依据
    private String content;//项目内容
    private String target;//项目目标
    private String conditions;//实施条件
    private String startDate;//项目开始日期
    private String endDate;//项目截止日期

    private List<ProjectParamStage> stages;//项目实施阶段明细参数
    private List<ProjectParamFund> funds;//经费预算明细参数

    private String bank;//开户银行
    private String cardNo;//银行卡号
    private String accounts;//开户名

    private float budget;
    private float fund;

}
