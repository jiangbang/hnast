package com.glface.modules.model.json;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 构建project json对象
 */
@Data
public class ProjectJson {
    private String name;//项目名称
    private String code;//项目编码
    private ProjectPlanTypeJson planType;
    private ProjectOrgJson org;//申报单位信息
    private ProjectContentJson content;//项目内容
    @JSONField(format="yyyy-MM-dd")
    private Date applyDate;//申请时间(用户提交时间)

    @JSONField(format="yyyy-MM-dd")
    private Date startDate;//项目开始时间
    @JSONField(format="yyyy-MM-dd")
    private Date endDate;//项目结束时间

    private List<ProjectStageJson> stages;//实施阶段

    private float budget;//项目预算
    private float funds;//申请金额
    private List<ProjectFundsJson> fundsList;//经费支出预算明细

    private String bank;//开户银行
    private String cardNo;//账    号
    private String accounts;//户    名

    private String districtResult;//区县审批结果
    private String districtOpinion;//区县审批意见
    private String firstResult;//初审结果
    private String firstOpinion;//初审意见
    private String recommendResult;//推荐审核结果
    private String recommendOpinion;//推荐审核意见
    private String expertResult;//专家评审结果
    private String expertOpinion;//专家评审意见


    public ProjectJson(){
    }
}

