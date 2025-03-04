package com.glface.modules.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.glface.base.bean.BaseEntity;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 项目执行过程
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@TableName(value = "pm_project_process")
public class ProjectProcess extends BaseEntity {
    private String projectId;//项目id
    private String nodeLabel;//当前执行节点名称 对应数据字典processNode
    private String nodeValue;//当前执行节点
    private Date startTime;//开始时间
    private Date endTime;//结束时间
    private Long duration;//耗时
    private String userId;//执行人
    private String officeId;//执行人归属部门
    private String result;//审批结果 对应数据字典projectStatus
    private String resultOpinion;//评审意见
    private String districtUserId;//分发至区县执行人
    private String districtOfficeId;//分发至区县执行人所在部门
}
