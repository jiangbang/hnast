package com.glface.modules.service;

import com.glface.base.utils.StringUtils;
import com.glface.modules.model.Project;
import com.glface.modules.model.ProjectBatch;
import com.glface.modules.model.ProjectBatchSummary;
import com.glface.modules.model.ProjectPlanType;
import com.glface.modules.utils.ProjectStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 项目统计
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class ProjectStatisticsService {

    @Resource
    private ProjectBatchService batchService;

    @Resource
    private ProjectService projectService;

    @Resource
    private ProjectPlanTypeService projectPlanTypeService;

    /**
     * 项目搜索
     * @param batchId   批次id
     * @param planTypeOfficeId 归口部门
     * @param projectStatus  项目状态 逗号分割
     * @return
     */
    public List<Project> searchProject(String batchId,String planTypeOfficeId,String projectStatus,String planTypeId,String categoryId,String specialId){
        List<Project> list = new ArrayList<>();
        if(StringUtils.isNotBlank(batchId)){
            list = projectService.findByBatchId(batchId);
        }else{
            list = projectService.searchAll(new Project());
        }

        List<Project> applyList = new ArrayList<>();
        //去掉未申请的项目
        for(Project project:list){
            if(project.getApplyDate()!=null){
                applyList.add(project);
            }
        }
        list = applyList;
        if(StringUtils.isNotBlank(planTypeOfficeId)){//依据归口部门过滤
            List<Project> tmpList = new ArrayList<>();
            for(Project project:list){
                if(StringUtils.isBlank(project.getPlanTypeId())){
                    continue;
                }
                ProjectPlanType projectPlanType = projectPlanTypeService.get(project.getPlanTypeId());
                if(projectPlanType!=null&&planTypeOfficeId.equals(projectPlanType.getOfficeId())){
                    tmpList.add(project);
                }
            }
            list = tmpList;
        }
        if(StringUtils.isNotBlank(projectStatus)){
            projectStatus = projectStatus.trim();
            String projectStatusList[] = projectStatus.split(",");
            List<Project> tmpList = new ArrayList<>();
            for(Project project:list){
                for(String status:projectStatusList){
                    status = status.trim();
                    if(StringUtils.isNotBlank(status)&&status.equals(project.getStatus())){
                        tmpList.add(project);
                        break;
                    }
                }
            }
            list = tmpList;
        }

        if(StringUtils.isNotBlank(planTypeId)){
            List<Project> tmpList = new ArrayList<>();
            for(Project project:list){
                if(planTypeId.equals(project.getPlanTypeId())){
                    tmpList.add(project);
                }
            }
            list = tmpList;
        }

        if(StringUtils.isNotBlank(categoryId)){
            List<Project> tmpList = new ArrayList<>();
            for(Project project:list){
                if(categoryId.equals(project.getCategoryId())){
                    tmpList.add(project);
                }
            }
            list = tmpList;
        }
        if(StringUtils.isNotBlank(specialId)){
            List<Project> tmpList = new ArrayList<>();
            for(Project project:list){
                if(specialId.equals(project.getSpecial())){
                    tmpList.add(project);
                }
            }
            list = tmpList;
        }
        return list;
    }

    /**
     * 批次数据汇总
     */
    public ProjectBatchSummary summaryByBatchId(String batchId){
        List<Project> list;
        if(StringUtils.isNotBlank(batchId)){
            list = projectService.findByBatchId(batchId);
        }else{
            list = projectService.searchAll(new Project());
        }

        List<Project> applyList = new ArrayList<>();
        //去掉未申请的项目
        for(Project project:list){
            if(project.getApplyDate()!=null){
                applyList.add(project);
            }
        }
        list = applyList;
        //统计
        ProjectBatchSummary summary = new ProjectBatchSummary();
        summary.setBatchId(batchId);
        summary.setTotal(list.size());
         int waitFirst=0;	// 待初审项目总数
         int recommend=0;	// 已推荐项目
         int expert=0;//已评审项目
         int materials=0;//实施项目(专家评审通过的项目)
        for(Project project:list){
            String status = project.getStatus();
            if(ProjectStatusEnum.SUBMITTED.getValue().equals(status)||ProjectStatusEnum.TO_DISTRICT.getValue().equals(status)||ProjectStatusEnum.DISTRICT_AGREE.getValue().equals(status)){//待初审项目
                waitFirst++;
            }
            if(ProjectStatusEnum.FIRST_AGREE.getValue().equals(status)||
                    ProjectStatusEnum.RECOMMEND_AGREE.getValue().equals(status)||
                    ProjectStatusEnum.RECOMMEND_REJECT.getValue().equals(status)||
                    ProjectStatusEnum.RECOMMEND_REJECT_WAIT.getValue().equals(status)||
                    ProjectStatusEnum.EXPERT_REJECT.getValue().equals(status)||
                    ProjectStatusEnum.EXPERT_AGREE.getValue().equals(status)||
                    ProjectStatusEnum.MATERIALS_SUBMITTED.getValue().equals(status)||
                    ProjectStatusEnum.MATERIALS_AGREE.getValue().equals(status)||
                    ProjectStatusEnum.MATERIALS_REJECT.getValue().equals(status)||
                    ProjectStatusEnum.FILE.getValue().equals(status)){// 已推荐项目(当前处于初审通过、推荐审核通过、推荐审核驳回等)
                recommend++;
            }
            if(ProjectStatusEnum.EXPERT_REJECT.getValue().equals(status)||
                    ProjectStatusEnum.EXPERT_AGREE.getValue().equals(status)||
                    ProjectStatusEnum.MATERIALS_SUBMITTED.getValue().equals(status)||
                    ProjectStatusEnum.MATERIALS_AGREE.getValue().equals(status)||
                    ProjectStatusEnum.MATERIALS_REJECT.getValue().equals(status)||
                    ProjectStatusEnum.FILE.getValue().equals(status)){//已评审项目
                expert++;
            }
            if(ProjectStatusEnum.EXPERT_AGREE.getValue().equals(status)||
                    ProjectStatusEnum.MATERIALS_SUBMITTED.getValue().equals(status)||
                    ProjectStatusEnum.MATERIALS_AGREE.getValue().equals(status)||
                    ProjectStatusEnum.MATERIALS_REJECT.getValue().equals(status)||
                    ProjectStatusEnum.FILE.getValue().equals(status)){//实施项目
                materials++;
            }
        }
        summary.setWaitFirst(waitFirst);
        summary.setRecommend(recommend);
        summary.setExpert(expert);
        summary.setMaterials(materials);
        return summary;
    }

}
