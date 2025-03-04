package com.glface.modules.controller;

import com.glface.base.bean.DynamicBean;
import com.glface.base.bean.R;
import com.glface.base.utils.DateUtils;
import com.glface.base.utils.StringUtils;
import com.glface.log.LoggerMonitor;
import com.glface.model.SysArea;
import com.glface.model.SysDict;
import com.glface.model.SysOffice;
import com.glface.model.SysUser;
import com.glface.modules.model.*;
import com.glface.modules.service.*;
import com.glface.modules.sys.utils.UserUtils;
import com.glface.modules.utils.ProjectNodeEnum;
import com.glface.modules.utils.ProjectStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;

/**
 * 评审
 */
@Slf4j
@RestController
@RequestMapping("/project")
public class ProjectReviewController {

    @Resource
    private ProjectProcessService projectProcessService;

    @Resource
    private ProjectService projectService;

    @Resource
    private ProjectPlanTypeService projectPlanTypeService;
    @Resource
    private ProjectSpecialService projectSpecialService;
    @Resource
    private ProjectCategoryService projectCategoryService;
    @Resource
    private ProjectBatchService projectBatchService;
    @Resource
    private ProjectOrgService projectOrgService;
    @Resource
    private UserService userService;
    @Resource
    private OfficeService officeService;
    @Resource
    private AreaService areaService;
    /**
     * 待初审项目库
     * 管理员可以查询所有
     * 归口部门初审管理员可以查询到归口部门所有待评审项目
     * 区县初审管理员可以查询到区县待评审项目
     * @param code 项目编号
     * @param name 项目名称
     * @param categoryId 项目分类
     */
    @PreAuthorize("hasAuthority('project:review:first')")
    @RequestMapping(value = "/searchFirstReview")
    public R<Object> searchFirstReview(String code, String name, String categoryId,
                                       String planTypeId,
                                       @RequestParam(value = "page", defaultValue = "1") int pageNo,
                                       @RequestParam(value = "limit", defaultValue = "10") int limit) {

        List<ProjectPlanType> planTypes = projectPlanTypeService.all();
        Map<String,ProjectPlanType> planTypeMap = new HashMap<>();
        for(ProjectPlanType planType:planTypes){
            planTypeMap.put(planType.getId(),planType);
        }

        //取所有最近进行中批次的待审批项目
        List<Project> projectList = projectService.allLatestBatchFirstReview();
        //依据搜索条件过滤
        List<Project> filterList = projectList;
        if(StringUtils.isNotBlank(code)){
            List<Project> tmpList = new ArrayList<>();
            for(Project project:filterList){
                if(project.getCode().contains(code)){
                    tmpList.add(project);
                }
            }
            filterList = tmpList;
        }
        if(StringUtils.isNotBlank(planTypeId)){
            List<Project> tmpList = new ArrayList<>();
            for(Project project:filterList){
                if(project.getPlanTypeId().contains(planTypeId)){
                    tmpList.add(project);
                }
            }
            filterList = tmpList;
        }
        if(StringUtils.isNotBlank(name)){
            List<Project> tmpList = new ArrayList<>();
            for(Project project:filterList){
                if(project.getName().contains(name)){
                    tmpList.add(project);
                }
            }
            filterList = tmpList;
        }
        if(StringUtils.isNotBlank(categoryId)){
            List<Project> tmpList = new ArrayList<>();
            for(Project project:filterList){
                if(categoryId.equals(project.getCategoryId())){
                    tmpList.add(project);
                }
            }
            filterList = tmpList;
        }
        //依据用户角色过滤
        SysUser currentUser = UserUtils.getUser();
        List<Project> projects = new ArrayList<>();
        boolean isDistrictManager = false;
        if(projectProcessService.isManager(currentUser.getId())){//管理员可以查看所有
            projects = filterList;
        }else if(projectProcessService.isFirstManager(currentUser.getId())){//是初审管理员
            SysOffice districtOffice = projectProcessService.isDistrictUser(currentUser.getId());
            if(districtOffice!=null){//区县初审管理员
                isDistrictManager = true;
                for(Project project:filterList){
                    ProjectPlanType planType = planTypeMap.get(project.getPlanTypeId());
                    if((ProjectStatusEnum.SUBMITTED.getValue().equals(project.getStatus())||ProjectStatusEnum.TO_DISTRICT.getValue().equals(project.getStatus()))&&planType!=null&&"BK".equals(planType.getCode())&&districtOffice.getId().equals(project.getQxOfficeId())){
                        projects.add(project);
                    }
                }
            }else{
                List<SysOffice> userOffices = userService.findOfficesByUserId(currentUser.getId());
                for(Project project:filterList){
                    ProjectPlanType planType = planTypeMap.get(project.getPlanTypeId());
                    if(planType!=null){
                        boolean hasOffice = false;
                        for(SysOffice office:userOffices){
                            if(office.getId().equals(planType.getOfficeId())){
                                hasOffice = true;
                                break;
                            }
                        }
                        if(hasOffice){
                            projects.add(project);
                        }
                    }
                }
            }
        }

        //排序
        Collections.sort(projects, new Comparator<Project>() {
            @Override
            public int compare(Project o1, Project o2) {
                if(o1.getUpdateDate().before(o2.getUpdateDate())){
                    return 1;
                }else if(o1.getUpdateDate().after(o2.getUpdateDate())){
                    return -1;
                }
                return  0;
            }
        });

        //内存分页
        List<Project> pageList = new ArrayList<>();
        int start = (pageNo-1)*limit;
        int end = start + limit-1;
        for(int i=start;i>=0&&i<=end&&i<projects.size();i++){
            pageList.add(projects.get(i));
        }

        // 构造返回数据
        List<Object> projectResults = new ArrayList<>();
        for (Project p : pageList) {
            ProjectCategory projectCate = projectCategoryService.get(p.getCategoryId());
            ProjectPlanType projectPlanType = projectPlanTypeService.get(p.getPlanTypeId());
            ProjectBatch batch = projectBatchService.get(p.getBatchId());
            ProjectOrg org = projectOrgService.get(p.getOrgId());
            SysArea area = areaService.get(p.getAreaId());
            String qxOfficeName = "市本级";
            SysOffice qxOffice = officeService.get(p.getQxOfficeId());
            if(qxOffice!=null){
                qxOfficeName = qxOffice.getName();
            }
            //项目所属专项
            String specialId = p.getSpecial();
            String specialName = "";
            if(StringUtils.isNotBlank(specialId)){
                ProjectSpecial projectSpecial = projectSpecialService.get(specialId);
                if(projectSpecial!=null){
                    specialName = projectSpecial.getName();
                }
            }

            Object o = new DynamicBean.Builder()
                    .setPV("id", p.getId())
                    .setPV("code",  p.getCode())
                    .setPV("category", projectCate, ProjectCategory.class)
                    .setPV("planType", projectPlanType, ProjectPlanType.class)
                    .setPV("area",  area, SysArea.class)
                    .setPV("qxOfficeId",  p.getQxOfficeId())
                    .setPV("qxOfficeName",  qxOfficeName)
                    .setPV("name", p.getName())
                    .setPV("batch", batch, ProjectBatch.class)
                    .setPV("organization",org, ProjectOrg.class)
                    .setPV("status", p.getStatus())
                    .setPV("specialId", specialId)
                    .setPV("specialName", specialName)

                    .setPV("createDate", DateUtils.formatDate(p.getCreateDate(), "yyyy-MM-dd HH:mm:ss")).build().getObject();
            projectResults.add(o);
        }

        Object data = new DynamicBean.Builder()
                .setPV("total", projects.size())
                .setPV("projects", projectResults, List.class)
                .setPV("isDistrictManager", isDistrictManager)
                .build().getObject();
        return R.ok(data);
    }

    /**
     * 待推荐项目库
     * 管理员可以查询所有
     * @param code 项目编号
     * @param name 项目名称
     * @param categoryId 项目分类
     */
    @PreAuthorize("hasAuthority('project:review:recommend')")
    @RequestMapping(value = "/searchRecommendReview")
    public R<Object> searchRecommendReview(String code, String name, String categoryId,
                                           String planTypeId,
                                       @RequestParam(value = "page", defaultValue = "1") int pageNo,
                                       @RequestParam(value = "limit", defaultValue = "10") int limit) {

        //取所有最近进行中批次的待推荐项目
        List<Project> projectList = projectService.allLatestBatchRecommendReview();
        //依据搜索条件过滤
        List<Project> filterList = projectList;
        if(StringUtils.isNotBlank(code)){
            List<Project> tmpList = new ArrayList<>();
            for(Project project:filterList){
                if(project.getCode().contains(code)){
                    tmpList.add(project);
                }
            }
            filterList = tmpList;
        }
        if(StringUtils.isNotBlank(name)){
            List<Project> tmpList = new ArrayList<>();
            for(Project project:filterList){
                if(project.getName().contains(name)){
                    tmpList.add(project);
                }
            }
            filterList = tmpList;
        }
        if(StringUtils.isNotBlank(categoryId)){
            List<Project> tmpList = new ArrayList<>();
            for(Project project:filterList){
                if(categoryId.equals(project.getCategoryId())){
                    tmpList.add(project);
                }
            }
            filterList = tmpList;
        }

        if(StringUtils.isNotBlank(planTypeId)){
            List<Project> tmpList = new ArrayList<>();
            for(Project project:filterList){
                if(planTypeId.equals(project.getPlanTypeId())){
                    tmpList.add(project);
                }
            }
            filterList = tmpList;
        }

        //依据用户角色过滤
        SysUser currentUser = UserUtils.getUser();
        List<Project> projects = new ArrayList<>();
        if(projectProcessService.isManager(currentUser.getId())){//管理员可以查看所有
            projects = filterList;
        }else if(projectProcessService.hasRecommendReviewRole(currentUser.getId())){//是否有推荐审核的权限
            projects = filterList;
        }

        //排序
        Collections.sort(projects, new Comparator<Project>() {
            @Override
            public int compare(Project o1, Project o2) {
                if(o1.getUpdateDate().before(o2.getUpdateDate())){
                    return 1;
                }else if(o1.getUpdateDate().after(o2.getUpdateDate())){
                    return -1;
                }
                return  0;
            }
        });

        //内存分页
        List<Project> pageList = new ArrayList<>();
        int start = (pageNo-1)*limit;
        int end = start + limit-1;
        for(int i=start;i>=0&&i<=end&&i<projects.size();i++){
            pageList.add(projects.get(i));
        }

        // 构造返回数据
        List<Object> projectResults = new ArrayList<>();
        for (Project p : pageList) {
            ProjectCategory projectCate = projectCategoryService.get(p.getCategoryId());
            ProjectPlanType projectPlanType = projectPlanTypeService.get(p.getPlanTypeId());
            ProjectBatch batch = projectBatchService.get(p.getBatchId());
            ProjectOrg org = projectOrgService.get(p.getOrgId());
            SysArea area = areaService.get(p.getAreaId());
            String qxOfficeName = "市本级";
            SysOffice qxOffice = officeService.get(p.getQxOfficeId());
            if(qxOffice!=null){
                qxOfficeName = qxOffice.getName();
            }
            //项目所属专项
            String specialId = p.getSpecial();
            String specialName = "";
            if(StringUtils.isNotBlank(specialId)){
                ProjectSpecial projectSpecial = projectSpecialService.get(specialId);
                if(projectSpecial!=null){
                    specialName = projectSpecial.getName();
                }
            }

            Object o = new DynamicBean.Builder()
                    .setPV("id", p.getId())
                    .setPV("code",  p.getCode())
                    .setPV("category", projectCate, ProjectCategory.class)
                    .setPV("planType", projectPlanType, ProjectPlanType.class)
                    .setPV("area",  area, SysArea.class)
                    .setPV("qxOfficeId",  p.getQxOfficeId())
                    .setPV("qxOfficeName",  qxOfficeName)
                    .setPV("name", p.getName())
                    .setPV("batch", batch, ProjectBatch.class)
                    .setPV("organization",org, ProjectOrg.class)
                    .setPV("status", p.getStatus())
                    .setPV("specialId", specialId)
                    .setPV("specialName", specialName)

                    .setPV("createDate", DateUtils.formatDate(p.getCreateDate(), "yyyy-MM-dd HH:mm:ss")).build().getObject();
            projectResults.add(o);
        }

        Object data = new DynamicBean.Builder()
                .setPV("total", projects.size())
                .setPV("projects", projectResults, List.class)
                .build().getObject();
        return R.ok(data);
    }

    /**
     * 待专家评审项目库
     * 管理员可以查询所有
     * @param code 项目编号
     * @param name 项目名称
     * @param categoryId 项目分类
     */
    @PreAuthorize("hasAuthority('project:review:expert')")
    @RequestMapping(value = "/searchExpertReview")
    public R<Object> searchExpertReview(String code, String name, String categoryId,String planTypeId,
                                           @RequestParam(value = "page", defaultValue = "1") int pageNo,
                                           @RequestParam(value = "limit", defaultValue = "10") int limit) {

        List<ProjectPlanType> planTypes = projectPlanTypeService.all();
        Map<String,ProjectPlanType> planTypeMap = new HashMap<>();
        for(ProjectPlanType planType:planTypes){
            planTypeMap.put(planType.getId(),planType);
        }

        //取所有最近进行中批次的待推荐项目
        List<Project> projectList = projectService.allLatestBatchExpertReview();
        //依据搜索条件过滤
        List<Project> filterList = projectList;
        if(StringUtils.isNotBlank(code)){
            List<Project> tmpList = new ArrayList<>();
            for(Project project:filterList){
                if(project.getCode().contains(code)){
                    tmpList.add(project);
                }
            }
            filterList = tmpList;
        }
        if(StringUtils.isNotBlank(name)){
            List<Project> tmpList = new ArrayList<>();
            for(Project project:filterList){
                if(project.getName().contains(name)){
                    tmpList.add(project);
                }
            }
            filterList = tmpList;
        }
        if(StringUtils.isNotBlank(categoryId)){
            List<Project> tmpList = new ArrayList<>();
            for(Project project:filterList){
                if(categoryId.equals(project.getCategoryId())){
                    tmpList.add(project);
                }
            }
            filterList = tmpList;
        }
        if(StringUtils.isNotBlank(planTypeId)){
            List<Project> tmpList = new ArrayList<>();
            for(Project project:filterList){
                if(planTypeId.equals(project.getPlanTypeId())){
                    tmpList.add(project);
                }
            }
            filterList = tmpList;
        }
        //依据用户角色过滤
        SysUser currentUser = UserUtils.getUser();
        List<Project> projects = new ArrayList<>();
        if(projectProcessService.isManager(currentUser.getId())){//管理员可以查看所有
            projects = filterList;
        }else if(projectProcessService.hasExpertReviewRole(currentUser.getId())){//是否有推荐审核的权限
            projects = filterList;
        }

        //排序
        Collections.sort(projects, new Comparator<Project>() {
            @Override
            public int compare(Project o1, Project o2) {
                if(o1.getUpdateDate().before(o2.getUpdateDate())){
                    return 1;
                }else if(o1.getUpdateDate().after(o2.getUpdateDate())){
                    return -1;
                }
                return  0;
            }
        });

        //内存分页
        List<Project> pageList = new ArrayList<>();
        int start = (pageNo-1)*limit;
        int end = start + limit-1;
        for(int i=start;i>=0&&i<=end&&i<projects.size();i++){
            pageList.add(projects.get(i));
        }

        // 构造返回数据
        List<Object> projectResults = new ArrayList<>();
        for (Project p : pageList) {
            ProjectCategory projectCate = projectCategoryService.get(p.getCategoryId());
            ProjectPlanType projectPlanType = projectPlanTypeService.get(p.getPlanTypeId());
            ProjectBatch batch = projectBatchService.get(p.getBatchId());
            ProjectOrg org = projectOrgService.get(p.getOrgId());
            SysArea area = areaService.get(p.getAreaId());
            String qxOfficeName = "市本级";
            SysOffice qxOffice = officeService.get(p.getQxOfficeId());
            if(qxOffice!=null){
                qxOfficeName = qxOffice.getName();
            }
            //项目所属专项
            String specialId = p.getSpecial();
            String specialName = "";
            if(StringUtils.isNotBlank(specialId)){
                ProjectSpecial projectSpecial = projectSpecialService.get(specialId);
                if(projectSpecial!=null){
                    specialName = projectSpecial.getName();
                }
            }

            Object o = new DynamicBean.Builder()
                    .setPV("id", p.getId())
                    .setPV("code",  p.getCode())
                    .setPV("area",  area, SysArea.class)
                    .setPV("qxOfficeId",  p.getQxOfficeId())
                    .setPV("qxOfficeName",  qxOfficeName)
                    .setPV("category", projectCate, ProjectCategory.class)
                    .setPV("planType", projectPlanType, ProjectPlanType.class)
                    .setPV("name", p.getName())
                    .setPV("batch", batch, ProjectBatch.class)
                    .setPV("organization",org, ProjectOrg.class)
                    .setPV("status", p.getStatus())
                    .setPV("specialId", specialId)
                    .setPV("specialName", specialName)

                    .setPV("createDate", DateUtils.formatDate(p.getCreateDate(), "yyyy-MM-dd HH:mm:ss")).build().getObject();
            projectResults.add(o);
        }

        Object data = new DynamicBean.Builder()
                .setPV("total", projects.size())
                .setPV("projects", projectResults, List.class)
                .build().getObject();
        return R.ok(data);
    }


    /**
     * 项目实施库
     * 管理员可以查询所有
     * @param code 项目编号
     * @param name 项目名称
     * @param categoryId 项目分类
     * @param status 项目状态 数据字典(projectStatus)一致
     */
    @PreAuthorize("hasAuthority('project:review:deploy')")
    @RequestMapping(value = "/searchDeployProject")
    public R<Object> searchDeployProject(String code, String name, String categoryId,String planTypeId,String status,
                                        @RequestParam(value = "page", defaultValue = "1") int pageNo,
                                        @RequestParam(value = "limit", defaultValue = "10") int limit) {

        List<ProjectPlanType> planTypes = projectPlanTypeService.all();
        Map<String,ProjectPlanType> planTypeMap = new HashMap<>();
        for(ProjectPlanType planType:planTypes){
            planTypeMap.put(planType.getId(),planType);
        }

        //所有通过专家评审的项目
        List<Project> projectList = projectService.allPassReview();
        //依据搜索条件过滤
        List<Project> filterList = projectList;
        if(StringUtils.isNotBlank(code)){
            List<Project> tmpList = new ArrayList<>();
            for(Project project:filterList){
                if(project.getCode().contains(code)){
                    tmpList.add(project);
                }
            }
            filterList = tmpList;
        }
        if(StringUtils.isNotBlank(name)){
            List<Project> tmpList = new ArrayList<>();
            for(Project project:filterList){
                if(project.getName().contains(name)){
                    tmpList.add(project);
                }
            }
            filterList = tmpList;
        }
        if(StringUtils.isNotBlank(categoryId)){
            List<Project> tmpList = new ArrayList<>();
            for(Project project:filterList){
                if(categoryId.equals(project.getCategoryId())){
                    tmpList.add(project);
                }
            }
            filterList = tmpList;
        }
        if(StringUtils.isNotBlank(planTypeId)){
            List<Project> tmpList = new ArrayList<>();
            for(Project project:filterList){
                if(planTypeId.equals(project.getPlanTypeId())){
                    tmpList.add(project);
                }
            }
            filterList = tmpList;
        }
        if(StringUtils.isNotBlank(status)){
            List<Project> tmpList = new ArrayList<>();
            for(Project project:filterList){
                if(status.equals(project.getStatus())){
                    tmpList.add(project);
                }
            }
            filterList = tmpList;
        }
        //依据用户角色过滤
        SysUser currentUser = UserUtils.getUser();
        List<Project> projects = new ArrayList<>();
        if(projectProcessService.isManager(currentUser.getId())){//管理员可以查看所有
            projects = filterList;
        }else if(projectProcessService.hasMaterialsReviewRole(currentUser.getId())){//是否有实施材料审核的权限
            projects = filterList;
        }

        //排序
        Collections.sort(projects, new Comparator<Project>() {
            @Override
            public int compare(Project o1, Project o2) {
                if(o1.getUpdateDate().before(o2.getUpdateDate())){
                    return 1;
                }else if(o1.getUpdateDate().after(o2.getUpdateDate())){
                    return -1;
                }
                return  0;
            }
        });

        //内存分页
        List<Project> pageList = new ArrayList<>();
        int start = (pageNo-1)*limit;
        int end = start + limit-1;
        for(int i=start;i>=0&&i<=end&&i<projects.size();i++){
            pageList.add(projects.get(i));
        }

        // 构造返回数据
        List<Object> projectResults = new ArrayList<>();
        for (Project p : pageList) {
            ProjectCategory projectCate = projectCategoryService.get(p.getCategoryId());
            ProjectPlanType projectPlanType = projectPlanTypeService.get(p.getPlanTypeId());
            ProjectBatch batch = projectBatchService.get(p.getBatchId());
            ProjectOrg org = projectOrgService.get(p.getOrgId());
            SysArea area = areaService.get(p.getAreaId());
            String qxOfficeName = "市本级";
            SysOffice qxOffice = officeService.get(p.getQxOfficeId());
            if(qxOffice!=null){
                qxOfficeName = qxOffice.getName();
            }
            //项目所属专项
            String specialId = p.getSpecial();
            String specialName = "";
            if(StringUtils.isNotBlank(specialId)){
                ProjectSpecial projectSpecial = projectSpecialService.get(specialId);
                if(projectSpecial!=null){
                    specialName = projectSpecial.getName();
                }
            }

            Object o = new DynamicBean.Builder()
                    .setPV("id", p.getId())
                    .setPV("code",  p.getCode())
                    .setPV("category", projectCate, ProjectCategory.class)
                    .setPV("planType", projectPlanType, ProjectPlanType.class)
                    .setPV("area",  area, SysArea.class)
                    .setPV("qxOfficeId",  p.getQxOfficeId())
                    .setPV("qxOfficeName",  qxOfficeName)
                    .setPV("name", p.getName())
                    .setPV("batch", batch, ProjectBatch.class)
                    .setPV("organization",org, ProjectOrg.class)
                    .setPV("status", p.getStatus())
                    .setPV("zipPath", p.getZipPath())
                    .setPV("specialId", specialId)
                    .setPV("specialName", specialName)

                    .setPV("createDate", DateUtils.formatDate(p.getCreateDate(), "yyyy-MM-dd HH:mm:ss")).build().getObject();
            projectResults.add(o);
        }

        Object data = new DynamicBean.Builder()
                .setPV("total", projects.size())
                .setPV("projects", projectResults, List.class)
                .build().getObject();
        return R.ok(data);
    }

    /**
     * 查询初审记录(包括分发)
     * 管理员可以查询所有
     * 归口部门初审管理员可以查询到归口部门所有初审记录
     * 区县初审管理员可以查询到区县项目所有初审记录
     * @param code 项目编号
     * @param name 项目名称
     * @param categoryId 项目分类
     */
    @PreAuthorize("hasAuthority('project:review:first')")
    @RequestMapping(value = "/searchFirstReviewHis")
    public R<Object> searchFirstReviewHis(String code, String name, String categoryId,
                                       @RequestParam(value = "page", defaultValue = "1") int pageNo,
                                       @RequestParam(value = "limit", defaultValue = "10") int limit) {

        List<ProjectPlanType> planTypes = projectPlanTypeService.all();
        Map<String,ProjectPlanType> planTypeMap = new HashMap<>();
        for(ProjectPlanType planType:planTypes){
            planTypeMap.put(planType.getId(),planType);
        }
        //找出所有被删除的项目
        Set<String> dels = projectService.findDels();
        //取所有初审记录
        List<ProjectProcess> processes = projectProcessService.findByNodeLabel("初审");
        List<ProjectProcess> processesTmp = new ArrayList<>();
        for(ProjectProcess process:processes){
            boolean isDel = false;
            if(dels.contains(process.getProjectId())){
                isDel = true;
            }
            if(!isDel){
                processesTmp.add(process);
            }
        }
        processes = processesTmp;
        //取满足搜索条件的项目
        List<Project> projectList = new ArrayList<>();
        boolean hasCondition = false;
        if(StringUtils.isNotBlank(code)||StringUtils.isNotBlank(name)||StringUtils.isNotBlank(categoryId)){
            hasCondition = true;
            Project project = new Project();
            project.setCode(code);
            project.setName(name);
            project.setCategoryId(categoryId);
            projectList = projectService.searchAll(project);
        }
        //依据查询条件过滤初审记录
        List<ProjectProcess> processList = new ArrayList<>();
        if(hasCondition){
            for(ProjectProcess process:processes){
                boolean has = false;
                for(Project p:projectList){
                    if(process.getProjectId().equals(p.getId())){
                        has = true;
                        break;
                    }
                }
                if(has){
                    processList.add(process);
                }
            }
        }else{
            processList = processes;
        }

        //依据用户角色过滤
        SysUser currentUser = UserUtils.getUser();
        List<ProjectProcess> ableProcessList = new ArrayList<>();
        boolean isDistrictManager = false;
        if(projectProcessService.isManager(currentUser.getId())){//管理员可以查看所有
            ableProcessList = processList;
        }else if(projectProcessService.isFirstManager(currentUser.getId())){//是初审管理员
            SysOffice districtOffice = projectProcessService.isDistrictUser(currentUser.getId());
            if(districtOffice!=null){//区县初审管理员
                isDistrictManager = true;
                for(ProjectProcess process:processList){
                    Project project = projectService.get(process.getProjectId());
                    ProjectPlanType planType = planTypeMap.get(project.getPlanTypeId());
                    if(planType!=null&&"BK".equals(planType.getCode())&&districtOffice.getId().equals(project.getQxOfficeId())){
                        ableProcessList.add(process);
                    }
                }
            }else{
                List<SysOffice> userOffices = userService.findOfficesByUserId(currentUser.getId());
                for(ProjectProcess process:processList){
                    Project project = projectService.get(process.getProjectId());
                    ProjectPlanType planType = planTypeMap.get(project.getPlanTypeId());
                    if(planType!=null){
                        boolean hasOffice = false;
                        for(SysOffice office:userOffices){
                            if(office.getId().equals(planType.getOfficeId())){
                                hasOffice = true;
                                break;
                            }
                        }
                        if(hasOffice){
                            ableProcessList.add(process);
                        }
                    }
                }
            }
        }

        //排序
        Collections.sort(ableProcessList, new Comparator<ProjectProcess>() {
            @Override
            public int compare(ProjectProcess o1, ProjectProcess o2) {
                if(o1.getCreateDate().after(o2.getCreateDate())){
                    return -1;
                }else if(o1.getCreateDate().before(o2.getCreateDate())){
                    return 1;
                }
                return  0;
            }
        });

        //内存分页
        List<ProjectProcess> pageList = new ArrayList<>();
        int start = (pageNo-1)*limit;
        int end = start + limit-1;
        for(int i=start;i>=0&&i<=end&&i<ableProcessList.size();i++){
            pageList.add(ableProcessList.get(i));
        }

        // 构造返回数据
        List<Object> projectResults = new ArrayList<>();
        for (ProjectProcess process : pageList) {
            Project p = projectService.get(process.getProjectId());
            if(p==null){
                log.info("project不存在,process.getProjectId()=={}",process.getProjectId());
                continue;
            }
            String categoryName = "";
            ProjectCategory projectCate = projectCategoryService.get(p.getCategoryId());
            if(projectCate!=null){
                categoryName = projectCate.getName();
            }
            ProjectOrg org = projectOrgService.get(p.getOrgId());
            String orgName = "";
            if(org!=null){
                orgName = org.getOrgName();
            }
            SysUser creater = userService.get(process.getCreateBy());
            String userName = "";
            if(creater!=null){
                userName = creater.getNickname();
                if(StringUtils.isBlank(userName)){
                    userName = creater.getAccount();
                }
            }

            ProjectPlanType projectPlanType = projectPlanTypeService.get(p.getPlanTypeId());
            String planTypeOfficeId = projectPlanType.getOfficeId();
            SysOffice planTypeOffice = null;
            String planTypeOfficeName = "";
            if(StringUtils.isNotBlank(planTypeOfficeId)){
                planTypeOffice = officeService.get(planTypeOfficeId);
                planTypeOfficeName = planTypeOffice.getName();
            }

            ProjectStatusEnum statusEnum = ProjectStatusEnum.getProjectStatusEnumByValue(process.getResult());
            String approveResult = "";
            String approveOpinion = process.getResultOpinion();
            if(statusEnum!=null){
                approveResult = statusEnum.getShortLabel();
            }
            //项目所属专项
            String specialId = p.getSpecial();
            String specialName = "";
            if(StringUtils.isNotBlank(specialId)){
                ProjectSpecial projectSpecial = projectSpecialService.get(specialId);
                if(projectSpecial!=null){
                    specialName = projectSpecial.getName();
                }
            }

            Object o = new DynamicBean.Builder()
                    .setPV("id", process.getId())
                    .setPV("projectId",  p.getId())
                    .setPV("code",  p.getCode())
                    .setPV("name", p.getName())
                    .setPV("categoryName", categoryName)
                    .setPV("orgName",orgName)
                    .setPV("approveUserName",userName)
                    .setPV("planType", projectPlanType, ProjectPlanType.class)
                    .setPV("planTypeOfficeName", planTypeOfficeName)
                    .setPV("approveResult", approveResult)
                    .setPV("approveOpinion", approveOpinion)
                    .setPV("specialId", specialId)
                    .setPV("specialName", specialName)
                    .setPV("createDate", DateUtils.formatDate(process.getCreateDate(), "yyyy-MM-dd HH:mm:ss")).build().getObject();
            projectResults.add(o);
        }

        Object data = new DynamicBean.Builder()
                .setPV("total", ableProcessList.size())
                .setPV("processes", projectResults, List.class)
                .setPV("isDistrictManager", isDistrictManager)
                .build().getObject();
        return R.ok(data);
    }

    /**
     * 查询推荐审批记录
     * 管理员可以查询所有
     * @param code 项目编号
     * @param name 项目名称
     * @param categoryId 项目分类
     */
    @PreAuthorize("hasAuthority('project:review:recommend')")
    @RequestMapping(value = "/searchRecommendReviewHis")
    public R<Object> searchRecommendReviewHis(String code, String name, String categoryId,
                                          @RequestParam(value = "page", defaultValue = "1") int pageNo,
                                          @RequestParam(value = "limit", defaultValue = "10") int limit) {

        List<ProjectPlanType> planTypes = projectPlanTypeService.all();
        Map<String,ProjectPlanType> planTypeMap = new HashMap<>();
        for(ProjectPlanType planType:planTypes){
            planTypeMap.put(planType.getId(),planType);
        }
        //找出所有被删除的项目
        Set<String> dels = projectService.findDels();
        //取所有推荐审核记录
        List<ProjectProcess> processes = projectProcessService.findByNodeValue("recommendReview");
        List<ProjectProcess> processesTmp = new ArrayList<>();
        for(ProjectProcess process:processes){
            boolean isDel = false;
            if(dels.contains(process.getProjectId())){
                isDel = true;
            }
            if(!isDel){
                processesTmp.add(process);
            }
        }
        processes = processesTmp;

        //取满足搜索条件的项目
        List<Project> projectList = new ArrayList<>();
        boolean hasCondition = false;
        if(StringUtils.isNotBlank(code)||StringUtils.isNotBlank(name)||StringUtils.isNotBlank(categoryId)){
            hasCondition = true;
            Project project = new Project();
            project.setCode(code);
            project.setName(name);
            project.setCategoryId(categoryId);
            projectList = projectService.searchAll(project);
        }
        //依据查询条件过滤审核记录
        List<ProjectProcess> processList = new ArrayList<>();
        if(hasCondition){
            for(ProjectProcess process:processes){
                boolean has = false;
                for(Project p:projectList){
                    if(process.getProjectId().equals(p.getId())){
                        has = true;
                        break;
                    }
                }
                if(has){
                    processList.add(process);
                }
            }
        }else{
            processList = processes;
        }

        //依据用户角色过滤
        SysUser currentUser = UserUtils.getUser();
        List<ProjectProcess> ableProcessList = new ArrayList<>();
        if(projectProcessService.isManager(currentUser.getId())){//管理员可以查看所有
            ableProcessList = processList;
        }else if(projectProcessService.hasRecommendReviewRole(currentUser.getId())){//是否有推荐审核的权限
            ableProcessList = processList;
        }

        //排序
        Collections.sort(ableProcessList, new Comparator<ProjectProcess>() {
            @Override
            public int compare(ProjectProcess o1, ProjectProcess o2) {
                if(o1.getCreateDate().after(o2.getCreateDate())){
                    return -1;
                }else if(o1.getCreateDate().before(o2.getCreateDate())){
                    return 1;
                }
                return  0;
            }
        });

        //内存分页
        List<ProjectProcess> pageList = new ArrayList<>();
        int start = (pageNo-1)*limit;
        int end = start + limit-1;
        for(int i=start;i>=0&&i<=end&&i<ableProcessList.size();i++){
            pageList.add(ableProcessList.get(i));
        }

        // 构造返回数据
        List<Object> projectResults = new ArrayList<>();
        for (ProjectProcess process : pageList) {
            Project p = projectService.get(process.getProjectId());
            ProjectCategory projectCate = projectCategoryService.get(p.getCategoryId());
            String categoryName = "";
            if(projectCate!=null){
                categoryName = projectCate.getName();
            }
            ProjectOrg org = projectOrgService.get(p.getOrgId());
            String orgName = "";
            if(org!=null){
                orgName = org.getOrgName();
            }
            SysUser creater = userService.get(process.getCreateBy());
            String userName = "";
            if(creater!=null){
                userName = creater.getNickname();
                if(StringUtils.isBlank(userName)){
                    userName = creater.getAccount();
                }
            }

            ProjectPlanType projectPlanType = projectPlanTypeService.get(p.getPlanTypeId());
            String planTypeOfficeId = projectPlanType.getOfficeId();
            SysOffice planTypeOffice = null;
            String planTypeOfficeName = "";
            if(StringUtils.isNotBlank(planTypeOfficeId)){
                planTypeOffice = officeService.get(planTypeOfficeId);
                planTypeOfficeName = planTypeOffice.getName();
            }

            ProjectStatusEnum statusEnum = ProjectStatusEnum.getProjectStatusEnumByValue(process.getResult());
            String approveResult = "";
            String approveOpinion = process.getResultOpinion();
            if(statusEnum!=null){
                approveResult = statusEnum.getShortLabel();
            }
            //项目所属专项
            String specialId = p.getSpecial();
            String specialName = "";
            if(StringUtils.isNotBlank(specialId)){
                ProjectSpecial projectSpecial = projectSpecialService.get(specialId);
                if(projectSpecial!=null){
                    specialName = projectSpecial.getName();
                }
            }

            Object o = new DynamicBean.Builder()
                    .setPV("id", process.getId())
                    .setPV("projectId",  p.getId())
                    .setPV("code",  p.getCode())
                    .setPV("name", p.getName())
                    .setPV("categoryName", categoryName)
                    .setPV("orgName",orgName)
                    .setPV("approveUserName",userName)
                    .setPV("planType", projectPlanType, ProjectPlanType.class)
                    .setPV("planTypeOfficeName", planTypeOfficeName)
                    .setPV("approveResult", approveResult)
                    .setPV("approveOpinion", approveOpinion)
                    .setPV("specialId", specialId)
                    .setPV("specialName", specialName)
                    .setPV("createDate", DateUtils.formatDate(process.getCreateDate(), "yyyy-MM-dd HH:mm:ss")).build().getObject();
            projectResults.add(o);
        }

        Object data = new DynamicBean.Builder()
                .setPV("total", ableProcessList.size())
                .setPV("processes", projectResults, List.class)
                .build().getObject();
        return R.ok(data);
    }

    /**
     * 查询专家审批记录
     * 管理员可以查询所有
     * @param code 项目编号
     * @param name 项目名称
     * @param categoryId 项目分类
     */
    @PreAuthorize("hasAuthority('project:review:expert')")
    @RequestMapping(value = "/searchExpertReviewHis")
    public R<Object> searchExpertReviewHis(String code, String name, String categoryId,
                                              @RequestParam(value = "page", defaultValue = "1") int pageNo,
                                              @RequestParam(value = "limit", defaultValue = "10") int limit) {

        List<ProjectPlanType> planTypes = projectPlanTypeService.all();
        Map<String,ProjectPlanType> planTypeMap = new HashMap<>();
        for(ProjectPlanType planType:planTypes){
            planTypeMap.put(planType.getId(),planType);
        }
        //找出所有被删除的项目
        Set<String> dels = projectService.findDels();
        //取所有专家审核记录
        List<ProjectProcess> processes = projectProcessService.findByNodeValue("expertReview");
        List<ProjectProcess> processesTmp = new ArrayList<>();
        for(ProjectProcess process:processes){
            boolean isDel = false;
            if(dels.contains(process.getProjectId())){
                isDel = true;
            }
            if(!isDel){
                processesTmp.add(process);
            }
        }
        processes = processesTmp;

        //取满足搜索条件的项目
        List<Project> projectList = new ArrayList<>();
        boolean hasCondition = false;
        if(StringUtils.isNotBlank(code)||StringUtils.isNotBlank(name)||StringUtils.isNotBlank(categoryId)){
            hasCondition = true;
            Project project = new Project();
            project.setCode(code);
            project.setName(name);
            project.setCategoryId(categoryId);
            projectList = projectService.searchAll(project);
        }
        //依据查询条件过滤审核记录
        List<ProjectProcess> processList = new ArrayList<>();
        if(hasCondition){
            for(ProjectProcess process:processes){
                boolean has = false;
                for(Project p:projectList){
                    if(process.getProjectId().equals(p.getId())){
                        has = true;
                        break;
                    }
                }
                if(has){
                    processList.add(process);
                }
            }
        }else{
            processList = processes;
        }

        //依据用户角色过滤
        SysUser currentUser = UserUtils.getUser();
        List<ProjectProcess> ableProcessList = new ArrayList<>();
        if(projectProcessService.isManager(currentUser.getId())){//管理员可以查看所有
            ableProcessList = processList;
        }else if(projectProcessService.hasRecommendReviewRole(currentUser.getId())){//是否有推荐审核的权限
            ableProcessList = processList;
        }

        //排序
        Collections.sort(ableProcessList, new Comparator<ProjectProcess>() {
            @Override
            public int compare(ProjectProcess o1, ProjectProcess o2) {
                if(o1.getCreateDate().after(o2.getCreateDate())){
                    return -1;
                }else if(o1.getCreateDate().before(o2.getCreateDate())){
                    return 1;
                }
                return  0;
            }
        });

        //内存分页
        List<ProjectProcess> pageList = new ArrayList<>();
        int start = (pageNo-1)*limit;
        int end = start + limit-1;
        for(int i=start;i>=0&&i<=end&&i<ableProcessList.size();i++){
            pageList.add(ableProcessList.get(i));
        }

        // 构造返回数据
        List<Object> projectResults = new ArrayList<>();
        for (ProjectProcess process : pageList) {
            Project p = projectService.get(process.getProjectId());
            ProjectCategory projectCate = projectCategoryService.get(p.getCategoryId());
            String categoryName = "";
            if(projectCate!=null){
                categoryName = projectCate.getName();
            }
            ProjectOrg org = projectOrgService.get(p.getOrgId());
            String orgName = "";
            if(org!=null){
                orgName = org.getOrgName();
            }
            SysUser creater = userService.get(process.getCreateBy());
            String userName = "";
            if(creater!=null){
                userName = creater.getNickname();
                if(StringUtils.isBlank(userName)){
                    userName = creater.getAccount();
                }
            }

            ProjectPlanType projectPlanType = projectPlanTypeService.get(p.getPlanTypeId());
            String planTypeOfficeId = projectPlanType.getOfficeId();
            SysOffice planTypeOffice = null;
            String planTypeOfficeName = "";
            if(StringUtils.isNotBlank(planTypeOfficeId)){
                planTypeOffice = officeService.get(planTypeOfficeId);
                planTypeOfficeName = planTypeOffice.getName();
            }

            ProjectStatusEnum statusEnum = ProjectStatusEnum.getProjectStatusEnumByValue(process.getResult());
            String approveResult = "";
            String approveOpinion = process.getResultOpinion();
            if(statusEnum!=null){
                approveResult = statusEnum.getShortLabel();
            }
            //项目所属专项
            String specialId = p.getSpecial();
            String specialName = "";
            if(StringUtils.isNotBlank(specialId)){
                ProjectSpecial projectSpecial = projectSpecialService.get(specialId);
                if(projectSpecial!=null){
                    specialName = projectSpecial.getName();
                }
            }

            Object o = new DynamicBean.Builder()
                    .setPV("id", process.getId())
                    .setPV("projectId",  p.getId())
                    .setPV("code",  p.getCode())
                    .setPV("name", p.getName())
                    .setPV("categoryName", categoryName)
                    .setPV("orgName",orgName)
                    .setPV("approveUserName",userName)
                    .setPV("planType", projectPlanType, ProjectPlanType.class)
                    .setPV("planTypeOfficeName", planTypeOfficeName)
                    .setPV("approveResult", approveResult)
                    .setPV("approveOpinion", approveOpinion)
                    .setPV("specialId", specialId)
                    .setPV("specialName", specialName)
                    .setPV("createDate", DateUtils.formatDate(process.getCreateDate(), "yyyy-MM-dd HH:mm:ss")).build().getObject();
            projectResults.add(o);
        }

        Object data = new DynamicBean.Builder()
                .setPV("total", ableProcessList.size())
                .setPV("processes", projectResults, List.class)
                .build().getObject();
        return R.ok(data);
    }


    /**
     * 初审(包括区县初审)
     * @param id 项目id
     * @param result 评审结果  1:通过  2：驳回 3:分发至区县审批  4:驳回补充资料后再审核
     * @param opinion 评审意见
     * @param specialId 所属专项
     * @param toDistricOfficeId 分发至区县审批部门id
     */
    @LoggerMonitor(value = "初审")
    @PreAuthorize("hasAuthority('project:review:first')")
    @RequestMapping(value = "/firstReview")
    public R<Object> firstReview(String id,String result,String opinion,String specialId,String toDistricOfficeId){
        Project project = projectService.get(id);
        projectProcessService.firstReview(project,result,opinion,specialId,toDistricOfficeId);
        return R.ok();
    }

    /**
     * 获取部门，并组织成树形结构
     */
    @PreAuthorize("hasAuthority('project:review:first')")
    @RequestMapping(value = "/qxkxTree")
    public R<List<SysOffice>> qxkxTree() {
        List<SysOffice> officeList = officeService.qxkxTree();
        return R.ok(officeList);
    }

    /**
     * 项目分发 获取所有部门，并组织成树形结构
     */
    @PreAuthorize("hasAuthority('project:review:first')")
    @RequestMapping(value = "/tree")
    public R<List<SysOffice>> tree() {
        List<SysOffice> officeList = officeService.allListTree();
        return R.ok(officeList);
    }

    /**
     * 区县科协
     */
    @RequestMapping(value = "/qxkx")
    public R<Object> qxkx() {
        List<SysOffice> officeList = officeService.findAll();
        List<SysOffice> sysOffices = new ArrayList<>();
        for(SysOffice office:officeList){
            if("2".equals(office.getType())){
                sysOffices.add(office);
            }
        }
        Collections.sort(sysOffices, new Comparator<SysOffice>() {
            @Override
            public int compare(SysOffice o1, SysOffice o2) {
                if(o1.getSort() < (o2.getSort())){
                    return 1;
                }else if(o1.getSort()>(o2.getSort())){
                    return -1;
                }
                return  0;
            }
        });
        List<Object> officeObjectList = new ArrayList<>();
        for (SysOffice office : sysOffices) {
            Object bean = new DynamicBean.Builder()
                    .setPV("id", office.getId())
                    .setPV("type", office.getType())
                    .setPV("name", office.getName())
                    .setPV("sort", office.getSort())
                    .setPV("createDate", DateUtils.formatDate(office.getCreateDate(), "yyyy-MM-dd HH:mm:ss")).build().getObject();
            officeObjectList.add(bean);
        }
        return R.ok(officeObjectList);
    }

    /**
     * 推荐审核
     * @param id 项目id
     * @param result 评审结果  1:通过  2：驳回  4:驳回补充资料后再审核
     * @param opinion 评审意见
     */
    @LoggerMonitor(value = "推荐审核")
    @PreAuthorize("hasAuthority('project:review:recommend')")
    @RequestMapping(value = "/recommendReview")
    public R<Object> recommendReview(String id,String result,String opinion){
        Project project = projectService.get(id);
        projectProcessService.recommendReview(project,result,opinion);
        return R.ok();
    }

    /**
     * 专家评审
     * @param id 项目id
     * @param result 评审结果  1:通过  2：驳回
     * @param opinion 评审意见
     */
    @LoggerMonitor(value = "专家评审")
    @PreAuthorize("hasAuthority('project:review:expert')")
    @RequestMapping(value = "/expertReview")
    public R<Object> expertReview(String id,String result,String opinion){
        Project project = projectService.get(id);
        projectProcessService.expertReview(project,result,opinion);
        return R.ok();
    }

    /**
     * 实施材料提交审批
     * @param id 项目id
     */
    @LoggerMonitor(value = "实施材料提交审批")
    @RequestMapping(value = "/materialsSubmitReview")
    public R<Object> materialsSubmitReview(String id){
        projectProcessService.materialsSubmitReview(id);
        return R.ok();
    }

    /**
     * 实施材料审批
     * @param id 项目id
     * @param result 评审结果  1:通过  2：驳回
     * @param opinion 评审意见
     */
    @LoggerMonitor(value = "实施材料审批")
    @PreAuthorize("hasAuthority('project:review:deploy')")
    @RequestMapping(value = "/materialsReview")
    public R<Object> materialsReview(String id,String result,String opinion){
        Project project = projectService.get(id);
        projectProcessService.materialsReview(project,result,opinion);
        return R.ok();
    }

    /**
     * 归档
     * @param id 项目id
     */
    @LoggerMonitor(value = "归档")
    @PreAuthorize("hasAuthority('project:review:place')")
    @RequestMapping(value = "/placeOnFile")
    public R<Object> placeOnFile(String id){
        Project project = projectService.get(id);
        projectProcessService.placeOnFile(project);
        return R.ok();
    }
    /**
     * 撤回
     * @param id 项目id
     */
    @LoggerMonitor(value = "撤回")
    @PreAuthorize("hasAuthority('project:review:place')")
    @RequestMapping(value = "/revoke")
    public R<Object> revoke(String id){
        Project project = projectService.get(id);
        projectProcessService.revoke(project);
        return R.ok();
    }
}
