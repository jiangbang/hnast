package com.glface.modules.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.glface.base.bean.Page;
import com.glface.base.utils.DateUtils;
import com.glface.base.utils.StringUtils;
import com.glface.common.exeception.ServiceException;
import com.glface.constant.Common;
import com.glface.model.*;
import com.glface.modules.controller.ProjectParamContent;
import com.glface.modules.controller.ProjectParamFund;
import com.glface.modules.controller.ProjectParamStage;
import com.glface.modules.mapper.*;
import com.glface.modules.model.*;
import com.glface.modules.sys.utils.UserUtils;
import com.glface.modules.utils.FloatUtils;
import com.glface.modules.utils.ProjectStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.glface.common.web.ApiCode.*;

@Slf4j
@Service
@Transactional(readOnly = true)
public class ProjectService {

    @Resource
    private ProjectMapper projectMapper;
    @Resource
    private ProjectOrgMapper orgMapper;
    @Resource
    private ProjectContentService contentService;
    @Resource
    private ProjectContentMapper contentMapper;
    @Resource
    private ProjectStageMapper stageMapper;
    @Resource
    private ProjectStageService stageService;
    @Resource
    private ProjectFundsService fundsService;
    @Resource
    private ProjectFundsMapper fundsMapper;
    @Resource
    private ProjectFileMapper projectFileMapper;
    @Resource
    private ProjectOrgService orgService;
    @Resource
    private OfficeService officeService;
    @Resource
    private AreaService areaService;
    @Resource
    private ProjectBatchService batchService;
    @Resource
    private ProjectPlanTypeService planTypeService;
    @Resource
    private UserService userService;

    @Resource
    private ProjectProcessService projectProcessService;

    public Project get(String id) {
        return projectMapper.selectById(id);
    }

    public Set<Project> findByCodeIncludeDel(String code){
        return projectMapper.findByCodeIncludeDel(code);
    }

    /**
     * 最近进行中批次的待初审项目 已提交和区县审核通过的项目
     * @return
     */
    public List<Project> allLatestBatchFirstReview(){
        //最近进行中的申请批次
        ProjectBatch batch = batchService.latest();
        LambdaQueryWrapper<Project> queryWrapper = Wrappers.<Project>query().lambda()
                .eq(Project::getBatchId, batch.getId())
                .and(wq -> wq
                        .eq(Project::getStatus, ProjectStatusEnum.SUBMITTED.getValue())
                        .or()
                        .eq(Project::getStatus,ProjectStatusEnum.DISTRICT_AGREE.getValue())
                        .or()
                        .eq(Project::getStatus,ProjectStatusEnum.TO_DISTRICT.getValue()))
                .eq(Project::getDelFlag,Project.DEL_FLAG_NORMAL);
        return projectMapper.selectList(queryWrapper);
    }

    /**
     * 最近进行中批次的待推荐项目
     * @return
     */
    public List<Project> allLatestBatchRecommendReview(){
        //最近进行中的申请批次
        ProjectBatch batch = batchService.latest();
        LambdaQueryWrapper<Project> queryWrapper = Wrappers.<Project>query().lambda()
                .eq(Project::getBatchId, batch.getId())
                .eq(Project::getStatus, ProjectStatusEnum.FIRST_AGREE.getValue())
                .eq(Project::getDelFlag,Project.DEL_FLAG_NORMAL);
        return projectMapper.selectList(queryWrapper);
    }

    /**
     * 最近进行中批次的待专家评审项目
     * @return
     */
    public List<Project> allLatestBatchExpertReview(){
        //最近进行中的申请批次
        ProjectBatch batch = batchService.latest();
        LambdaQueryWrapper<Project> queryWrapper = Wrappers.<Project>query().lambda()
                .eq(Project::getBatchId, batch.getId())
                .eq(Project::getStatus, ProjectStatusEnum.RECOMMEND_AGREE.getValue())
                .eq(Project::getDelFlag,Project.DEL_FLAG_NORMAL);
        return projectMapper.selectList(queryWrapper);
    }

    /**
     * 所有通过专家评审的项目
     * @return
     */
    public List<Project> allPassReview(){
        LambdaQueryWrapper<Project> queryWrapper = Wrappers.<Project>query().lambda()
                .eq(Project::getPassReviewFlag, "1")
                .eq(Project::getDelFlag,Project.DEL_FLAG_NORMAL);
        return projectMapper.selectList(queryWrapper);
    }

    /**
     * 保存或新建项目基础信息
     * @param id   如果id不为空则是修改信息
     * @param name 项目名称
     * @param batchId 批次id
     * @param categoryId 项目类别
     * @param planTypeId 计划类型
     */
    @Transactional
    public Project saveBase(String id,String batchId,String name,String categoryId,String planTypeId,String specialId,String qxOfficeId) {
        if (StringUtils.isBlank(batchId)) {
            throw new ServiceException(PROJECT_BATCH_REQUIRED);
        }
        if (StringUtils.isBlank(name)) {
            throw new ServiceException(PROJECT_NAME_REQUIRED);
        }
        if (StringUtils.isBlank(categoryId)) {
            throw new ServiceException(PROJECT_CATEGORY_REQUIRED);
        }
        if (StringUtils.isBlank(planTypeId)) {
            throw new ServiceException(PROJECT_PLAN_TYPE_REQUIRED);
        }
        Project project = null;
        if(StringUtils.isNotBlank(id)){
            project = get(id);
            if(project==null){
                throw new ServiceException(PROJECT_NOT_EXIST);
            }
        }

        if(project == null){
            project = new Project();
            project.setStatus(ProjectStatusEnum.NOT_APPLY.getValue());
        }

        //项目类别修改后重置项目编号
        if(StringUtils.isNotBlank(project.getCode())&&!planTypeId.equals(project.getPlanTypeId())) {
            ProjectPlanType projectPlanType = planTypeService.get(planTypeId);
            ProjectBatch projectBatch = batchService.get(batchId);
            String maxCode = projectMapper.findMaxCodeByCode(projectPlanType.getCode() + "-" + projectBatch.getYear() + "-%");
            String nextNum = "001";
            if (StringUtils.isNotBlank(maxCode)) {
                int index = maxCode.lastIndexOf("-");
                String max = maxCode.substring(index + 1);
                int maxInt = Integer.valueOf(max);
                int nextInt = maxInt + 1;
                if (nextInt <= 9) {
                    nextNum = "00" + nextInt;
                } else if (nextInt <= 99) {
                    nextNum = "0" + nextInt;
                }else{
                    nextNum = "" + nextInt;
                }
            }
            String projectCode = "";
            for(int i=0;i<100;i++){
                projectCode = "";
                if (projectBatch.getNumber().length() < 2) {
                    projectCode = projectPlanType.getCode() + "-" + projectBatch.getYear() + "-0" + projectBatch.getNumber();
                } else {
                    projectCode = projectPlanType.getCode() + "-" + projectBatch.getYear() + "-" + projectBatch.getNumber();
                }
                projectCode = projectCode + "-" + nextNum;
                //查询code是否存在
                Set<Project> codeProjects = findByCodeIncludeDel(projectCode);
                if(codeProjects!=null&&codeProjects.size()>0){
                    int maxInt = Integer.valueOf(nextNum);
                    int nextInt = maxInt + 1;
                    if (nextInt <= 9) {
                        nextNum = "00" + nextInt;
                    } else if (nextInt <= 99) {
                        nextNum = "0" + nextInt;
                    }else{
                        nextNum = "" + nextInt;
                    }
                    continue;
                }
            }

            project.setCode(projectCode);
        }

        project.setBatchId(batchId);
        project.setName(name);
        project.setCategoryId(categoryId);
        project.setPlanTypeId(planTypeId);
        if(StringUtils.isNotBlank(specialId)){
            project.setSpecial(specialId);
        }
        project.setQxOfficeId(qxOfficeId);



        if(StringUtils.isBlank(project.getId())) {
            UserUtils.preAdd(project);
            projectMapper.insert(project);
        } else {
            UserUtils.preUpdate(project);
            projectMapper.updateById(project);
        }
        return project;
    }

    /**
     * 保存申请单位信息
     * @param id         项目id
     * @param orgId      申报单位id
     * @param orgName          单位名称
     * @param chargeName       项目负责人
     * @param chargeTitle      职称/职务
     * @param chargeMobile     手机
     * @param chargeEmail      电子邮箱
     * @param orgPhone          单位电话
     * @param orgFax           传真
     * @param orgAddress       单位地址
     * @param orgPost          邮政编码
     */
    @Transactional
    public Project saveOrg(String id,String orgId,String orgName,String chargeName,String chargeTitle,String chargeMobile,String chargeEmail,String orgPhone,String orgFax,String orgAddress,String orgPost){
        if (StringUtils.isBlank(id)) {
            throw new ServiceException(PROJECT_NOT_EXIST);
        }
        Project project = get(id);
        if(project==null){
            throw new ServiceException(PROJECT_NOT_EXIST);
        }

        if (StringUtils.isBlank(orgName)) {
            throw new ServiceException(PROJECT_ORG_NAME_REQUIRED);
        }

        if (StringUtils.isBlank(chargeName)) {
            throw new ServiceException(PROJECT_ORG_CHARGENAME_REQUIRED);
        }

        if (StringUtils.isBlank(chargeTitle)) {
            throw new ServiceException(PROJECT_ORG_CHARGETITLE_REQUIRED);
        }

        if (StringUtils.isBlank(chargeMobile)) {
            throw new ServiceException(PROJECT_ORG_CHARGEMOBILE_REQUIRED);
        }

        if (StringUtils.isBlank(chargeEmail)) {
            throw new ServiceException(PROJECT_ORG_CHARGEEMAIL_REQUIRED);
        }

//        if (StringUtils.isBlank(orgPhone)) {
//            throw new ServiceException(PROJECT_ORG_ORGPHONE_REQUIRED);
//        }

        if (StringUtils.isBlank(orgAddress)) {
            throw new ServiceException(PROJECT_ORG_ORGADRESS_REQUIRED);
        }

//        if (StringUtils.isBlank(orgPost)) {
//            throw new ServiceException(PROJECT_ORG_ORGPOST_REQUIRED);
//        }

        if(StringUtils.isBlank(orgId)){
            orgId = project.getOrgId();
        }
        ProjectOrg projectOrg = null;
        if(StringUtils.isNotBlank(orgId)){
            projectOrg = orgService.get(orgId);
        }
        //是否需要创建新的申报单位,如果已被其它项目使用并且信息发生改变则必须新建
        if(projectOrg!=null){
            List<Project> orgProjects = findByOrgId(projectOrg.getId());
            boolean orgUsed = false;
            for(Project p:orgProjects){
                if(!p.getId().equals(project.getId())){
                    orgUsed = true;
                    break;
                }
            }
            if(orgUsed){//已经被使用 如果信息发生改变则必须新建
                if(!orgName.equals(projectOrg.getOrgName())
                        ||!chargeName.equals(projectOrg.getChargeName())){
                    projectOrg = null;
                }
            }
        }

        if(projectOrg == null){
            projectOrg = new ProjectOrg();
        }
        projectOrg.setOrgName(orgName);
        projectOrg.setChargeName(chargeName);
        projectOrg.setChargeTitle(chargeTitle);
        projectOrg.setChargeMobile(chargeMobile);
        projectOrg.setChargeEmail(chargeEmail);
        projectOrg.setOrgPhone(orgPhone);
        projectOrg.setOrgFax(orgFax);
        projectOrg.setOrgAddress(orgAddress);
        projectOrg.setOrgPost(orgPost);
        if(StringUtils.isBlank(projectOrg.getId())) {
            UserUtils.preAdd(projectOrg);
            orgMapper.insert(projectOrg);
        } else {
            UserUtils.preUpdate(projectOrg);
            orgMapper.updateById(projectOrg);
        }
        //是否修改项目orgId
        if(!projectOrg.getId().equals(project.getOrgId())){
            project.setOrgId(projectOrg.getId());
            UserUtils.preUpdate(project);
            projectMapper.updateById(project);
        }
        return project;
    }

    /**
     * 缓存内容数据 不对参数验证
     * @param content
     * @return
     */
    @Transactional
    public Project cacheContent(ProjectParamContent content) {
        String id = content.getProjectId();
        if (StringUtils.isBlank(id)) {
            throw new ServiceException(PROJECT_NOT_EXIST);
        }
        Project project = get(id);
        if(project==null){
            throw new ServiceException(PROJECT_NOT_EXIST);
        }
        //验证
//        if(!projectProcessService.allowUpdateProjectContent(project.getId())){
//            throw new ServiceException(PROJECT_PROCESS_SUBMIT_NOT_ALLOW);
//        }
        return saveContentUnChick(content);
    }

    /**
     * 保存 需要对参数进行验证
     * @return
     */
    @Transactional
    public Project saveContent(ProjectParamContent content) {
        String id = content.getProjectId();
        if (StringUtils.isBlank(id)) {
            throw new ServiceException(PROJECT_NOT_EXIST);
        }
        Project project = get(id);
        if(project==null){
            throw new ServiceException(PROJECT_NOT_EXIST);
        }
        if(StringUtils.isBlank(content.getStartDate())){
            throw new ServiceException(PROJECT_STARTTIME_REQUIRED);
        }
        if(StringUtils.isBlank(content.getEndDate())){
            throw new ServiceException(PROJECT_ENDTIME_REQUIRED);
        }
        if(StringUtils.isBlank(content.getBank())){
            throw new ServiceException(PROJECT_BANK_REQUIRED);
        }
        if(StringUtils.isBlank(content.getCardNo())){
            throw new ServiceException(PROJECT_CARDNO_REQUIRED);
        }
        if(StringUtils.isBlank(content.getAccounts())){
            throw new ServiceException(PROJECT_ACCOUNTS_REQUIRED);
        }
//        if(!projectProcessService.allowUpdateProjectContent(project.getId())){
//            throw new ServiceException(PROJECT_PROCESS_SUBMIT_NOT_ALLOW);
//        }
        return saveContentUnChick(content);
    }

    /**
     * 存储内容 不对参数验证
     * @param content
     * @return
     */
    private Project saveContentUnChick(ProjectParamContent content) {
        String id = content.getProjectId();
        if (StringUtils.isBlank(id)) {
            throw new ServiceException(PROJECT_NOT_EXIST);
        }
        Project project = get(id);
        if(project==null){
            throw new ServiceException(PROJECT_NOT_EXIST);
        }
        if(StringUtils.isNotBlank(content.getStartDate())){
            try {
                Date startDate = DateUtils.parseDate(content.getStartDate(),"yyyy-MM-dd");
                project.setStartDate(startDate);
            } catch (ParseException e) {
                throw new ServiceException(PROJECT_STARTTIME_ERROR);
            }
        }else{
            project.setStartDate(null);
        }
        if(StringUtils.isNotBlank(content.getEndDate())){
            try {
                Date endDate = DateUtils.parseDate(content.getEndDate(),"yyyy-MM-dd");
                project.setEndDate(endDate);
            } catch (ParseException e) {
                throw new ServiceException(PROJECT_ENDTIME_ERROR);
            }
        }else{
            project.setEndDate(null);
        }
        project.setBank(content.getBank());
        project.setCardNo(content.getCardNo());
        project.setAccounts(content.getAccounts());
        project.setFunds(content.getFund());
        project.setBudget(content.getBudget());
        UserUtils.preUpdate(project);
        projectMapper.updateById(project);

        ProjectContent projectContent = contentService.findByProjectId(project.getId());
        if(projectContent==null){
            projectContent = new ProjectContent();
            projectContent.setProjectId(project.getId());
        }
        projectContent.setBasis(content.getBasis());
        projectContent.setContent(content.getContent());
        projectContent.setConditions(content.getConditions());
        projectContent.setTarget(content.getTarget());
        if(StringUtils.isBlank(projectContent.getId())) {
            UserUtils.preAdd(projectContent);
            contentMapper.insert(projectContent);
        } else {
            UserUtils.preUpdate(projectContent);
            contentMapper.updateById(projectContent);
        }


        //需要修改(删除)和新增的
        List<ProjectParamStage> stages = content.getStages();
        List<ProjectStage> dbStages = stageService.findByProjectId(id);
        List<ProjectStage> updateStages = new ArrayList<>();
        List<ProjectStage> delStages = new ArrayList<>();
        List<ProjectStage> addStages = new ArrayList<>();

        for(int i = 0; i < stages.size(); i++){
            ProjectParamStage paramStage = stages.get(i);
            if(StringUtils.isBlank(paramStage.getId())){//需要新增
                ProjectStage stage = new ProjectStage();
                stage.setProjectId(id);
                stage.setName(paramStage.getName());
                stage.setSort(i);
                if(StringUtils.isNotBlank(paramStage.getStartDate())){
                    try {
                        stage.setStartDate(DateUtils.parseDate(paramStage.getStartDate(),"yyyy-MM-dd"));
                    } catch (ParseException e) {
                        throw new ServiceException(PROJECT_STAGE_STARTTIME_ERROR);
                    }
                }
                if(StringUtils.isNotBlank(paramStage.getEndDate())){
                    try {
                        stage.setEndDate(DateUtils.parseDate(paramStage.getEndDate(),"yyyy-MM-dd"));
                    } catch (ParseException e) {
                        throw new ServiceException(PROJECT_STAGE_ENDTIME_ERROR);
                    }
                }
                stage.setMoney(paramStage.getMoney());
                stage.setRemark(paramStage.getRemark());
                addStages.add(stage);
            }else{//需要修改
                ProjectStage stage = stageService.get(paramStage.getId());
                stage.setName(paramStage.getName());
                stage.setSort(i);
                if(StringUtils.isNotBlank(paramStage.getStartDate())){
                    try {
                        stage.setStartDate(DateUtils.parseDate(paramStage.getStartDate(),"yyyy-MM-dd"));
                    } catch (ParseException e) {
                        throw new ServiceException(PROJECT_STAGE_STARTTIME_ERROR);
                    }
                }else{
                    stage.setStartDate(null);
                }
                if(StringUtils.isNotBlank(paramStage.getEndDate())){
                    try {
                        stage.setEndDate(DateUtils.parseDate(paramStage.getEndDate(),"yyyy-MM-dd"));
                    } catch (ParseException e) {
                        throw new ServiceException(PROJECT_STAGE_ENDTIME_ERROR);
                    }
                }else{
                    stage.setEndDate(null);
                }
                stage.setMoney(paramStage.getMoney());
                stage.setRemark(paramStage.getRemark());
                updateStages.add(stage);
            }
        }
        for(ProjectStage dbStage:dbStages){//找出需要删除的
            boolean has = false;
            for(ProjectParamStage paramStage:stages){
                if(dbStage.getId().equals(paramStage.getId())){
                    has= true;
                    break;
                }
            }
            if(!has){
                delStages.add(dbStage);
            }
        }
        //需要修改(删除)和新增的经费预算明细参数
        List<ProjectParamFund> funds = content.getFunds();
        List<ProjectFunds> dbFunds = fundsService.findByProjectId(id);
        List<ProjectFunds> updateFunds = new ArrayList<>();
        List<ProjectFunds> delFunds = new ArrayList<>();
        List<ProjectFunds> addFunds = new ArrayList<>();
        for (int i = 0; i < funds.size(); i++) {
            ProjectParamFund paramFund = funds.get(i);
            if(StringUtils.isBlank(paramFund.getId())){//需要新增
                ProjectFunds fund = new ProjectFunds();
                fund.setProjectId(id);
                fund.setName(paramFund.getName());
                fund.setMoney(paramFund.getMoney());
                fund.setRemark(paramFund.getRemark());
                fund.setSort(i);
                addFunds.add(fund);
            }else{//需要修改
                ProjectFunds stage = fundsService.get(paramFund.getId());
                stage.setName(paramFund.getName());
                stage.setMoney(paramFund.getMoney());
                stage.setRemark(paramFund.getRemark());
                stage.setSort(i);
                updateFunds.add(stage);
            }
        }
        for(ProjectFunds dbFund:dbFunds){//找出需要删除的
            boolean has = false;
            for(ProjectParamFund paramFund:funds){
                if(dbFund.getId().equals(paramFund.getId())){
                    has= true;
                    break;
                }
            }
            if(!has){
                delFunds.add(dbFund);
            }
        }
        //存储
        for(ProjectStage add:addStages){
            UserUtils.preAdd(add);
            stageMapper.insert(add);
        }
        for(ProjectStage update:updateStages){
            UserUtils.preUpdate(update);
            stageMapper.updateById(update);
        }
        for(ProjectStage del:delStages){
            UserUtils.preUpdate(del);
            stageMapper.updateById(del);
            stageMapper.deleteById(del.getId());
        }

        for(ProjectFunds add:addFunds){
            UserUtils.preAdd(add);
            fundsMapper.insert(add);
        }
        for(ProjectFunds update:updateFunds){
            UserUtils.preUpdate(update);
            fundsMapper.updateById(update);
        }
        for(ProjectFunds del:delFunds){
            UserUtils.preUpdate(del);
            fundsMapper.updateById(del);
            fundsMapper.deleteById(del.getId());
        }
        return project;
    }

    @Transactional
    public ProjectFile uploadFile(String id,String type,boolean onlyOne,FileInfo fileInfo){
        Project project = get(id);
        if(project==null){
            throw new ServiceException(PROJECT_NOT_EXIST);
        }
        if(onlyOne){//先删除再添加
            projectFileMapper.deleteByProjectIdAndType(id,type);
        }
        ProjectFile projectFile = new ProjectFile();
        projectFile.setProjectId(id);
        projectFile.setType(type);
        projectFile.setFileId(fileInfo.getId());
        String fileName = fileInfo.getName();
//        int index = fileName.lastIndexOf(".");
//        if(index>0){
//            fileName = fileName.substring(0,index);
//        }
        projectFile.setName(fileName);
        UserUtils.preAdd(projectFile);
        projectFileMapper.insert(projectFile);
        return projectFile;
    }

    @Transactional
    public void deleteFile(String projectId,String projectFileId){
        projectFileMapper.deleteByIdAndProjectId(projectFileId,projectId);
    }

    /**
     * 提交待审批
     * @param id
     */
    @Transactional
    public synchronized void submit(String id){
        if (StringUtils.isBlank(id)) {
            throw new ServiceException(PROJECT_NOT_EXIST);
        }
        Project project = get(id);
        if(project==null){
            throw new ServiceException(PROJECT_NOT_EXIST);
        }
        if(!UserUtils.getUserId().equals(project.getCreateBy())){
            throw new ServiceException(PROJECT_NOT_ALLOW_SUBMIT_OTHER);
        }
        Date now = new Date();
        String batchId = project.getBatchId();
        if(StringUtils.isBlank(batchId)){
            throw new ServiceException(PROJECT_BATCH_REQUIRED);
        }
        ProjectBatch projectBatch = batchService.get(batchId);
        if(projectBatch==null){
            throw new ServiceException(PROJECT_BATCH_NOTEXIST);
        }
        //是否已到申报开始时间 初审只有在申报时间范围可以提交
        if(project.getStatus().equals(ProjectStatusEnum.NOT_APPLY.getValue())){
            if(now.before(projectBatch.getStartTime())){
                throw new ServiceException(PROJECT_BATCH_NOT_START);
            }
            if(now.after(projectBatch.getEndTime())){
                throw new ServiceException(PROJECT_BATCH_END);
            }
        }

        //基础信息验证
        if (StringUtils.isBlank(project.getName())) {
            throw new ServiceException(PROJECT_NAME_REQUIRED);
        }
        if (StringUtils.isBlank(project.getCategoryId())) {
            throw new ServiceException(PROJECT_CATEGORY_REQUIRED);
        }
        if (StringUtils.isBlank(project.getPlanTypeId())) {
            throw new ServiceException(PROJECT_PLAN_TYPE_REQUIRED);
        }
        ProjectPlanType projectPlanType = planTypeService.get(project.getPlanTypeId());
        if(projectPlanType==null){
            throw new ServiceException(PROJECT_PLAN_TYPE_NOTEXIST);
        }
        //单位信息验证
        if (StringUtils.isBlank(project.getOrgId())) {
            throw new ServiceException(PROJECT_ORG_NOT_EXIST);
        }
        //申报内容验证
        if(project.getStartDate()==null){
            throw new ServiceException(PROJECT_STARTTIME_REQUIRED);
        }
        if(project.getEndDate()==null){
            throw new ServiceException(PROJECT_ENDTIME_REQUIRED);
        }
        if(StringUtils.isBlank(project.getBank())){
            throw new ServiceException(PROJECT_BANK_REQUIRED);
        }
        if(StringUtils.isBlank(project.getCardNo())){
            throw new ServiceException(PROJECT_CARDNO_REQUIRED);
        }
        if(StringUtils.isBlank(project.getAccounts())){
            throw new ServiceException(PROJECT_ACCOUNTS_REQUIRED);
        }
        //附件验证

        //记录发起人部门
        List<SysOffice> userOffices = userService.findOfficesByUserId(project.getCreateBy());
        if(userOffices.size()>0){
            project.setOfficeId(userOffices.get(0).getId());
        }

        //生成项目编号
        if(StringUtils.isBlank(project.getCode())) {
            String maxCode = projectMapper.findMaxCodeByCode(projectPlanType.getCode() + "-" + projectBatch.getYear() + "-%");
            String nextNum = "001";
            if (StringUtils.isNotBlank(maxCode)) {
                int index = maxCode.lastIndexOf("-");
                String max = maxCode.substring(index + 1);
                int maxInt = Integer.valueOf(max);
                int nextInt = maxInt + 1;
                if (nextInt <= 9) {
                    nextNum = "00" + nextInt;
                } else if (nextInt <= 99) {
                    nextNum = "0" + nextInt;
                }else{
                    nextNum = "" + nextInt;
                }
            }
            String projectCode = "";
            for(int i=0;i<100;i++){
                projectCode = "";
                if (projectBatch.getNumber().length() < 2) {
                    projectCode = projectPlanType.getCode() + "-" + projectBatch.getYear() + "-0" + projectBatch.getNumber();
                } else {
                    projectCode = projectPlanType.getCode() + "-" + projectBatch.getYear() + "-" + projectBatch.getNumber();
                }
                projectCode = projectCode + "-" + nextNum;
                //查询code是否存在
                Set<Project> codeProjects = findByCodeIncludeDel(projectCode);
                if(codeProjects!=null&&codeProjects.size()>0){
                    int maxInt = Integer.valueOf(nextNum);
                    int nextInt = maxInt + 1;
                    if (nextInt <= 9) {
                        nextNum = "00" + nextInt;
                    } else if (nextInt <= 99) {
                        nextNum = "0" + nextInt;
                    }else{
                        nextNum = "" + nextInt;
                    }
                    continue;
                }
            }

            project.setCode(projectCode);
        }
        if(project.getApplyDate()==null){
            project.setApplyDate(now);
        }
        //更改状态
        UserUtils.preUpdate(project);
        projectMapper.updateById(project);
        //更新过程
        projectProcessService.startSubmit(project);
    }

    public List<Project> findByOrgId(String orgId){
        LambdaQueryWrapper<Project> queryWrapper = Wrappers.<Project>query().lambda()
                .eq(Project::getOrgId, orgId)
                .eq(Project::getDelFlag,Project.DEL_FLAG_NORMAL);
        return projectMapper.selectList(queryWrapper);
    }

    public List<Project> findByCode(String code){
        LambdaQueryWrapper<Project> queryWrapper = Wrappers.<Project>query().lambda()
                .like(Project::getCode, code)
                .eq(Project::getDelFlag,Project.DEL_FLAG_NORMAL);
        return projectMapper.selectList(queryWrapper);
    }

    public List<Project> findByName(String name){
        LambdaQueryWrapper<Project> queryWrapper = Wrappers.<Project>query().lambda()
                .like(Project::getName, name)
                .eq(Project::getDelFlag,Project.DEL_FLAG_NORMAL);
        return projectMapper.selectList(queryWrapper);
    }

    public List<Project> findByCategoryId(String categoryId){
        LambdaQueryWrapper<Project> queryWrapper = Wrappers.<Project>query().lambda()
                .eq(Project::getCategoryId, categoryId)
                .eq(Project::getDelFlag,Project.DEL_FLAG_NORMAL);
        return projectMapper.selectList(queryWrapper);
    }
    public List<Project> findByBatchId(String batchId){
        LambdaQueryWrapper<Project> queryWrapper = Wrappers.<Project>query().lambda()
                .eq(Project::getBatchId, batchId)
                .eq(Project::getDelFlag,Project.DEL_FLAG_NORMAL);
        return projectMapper.selectList(queryWrapper);
    }

    public List<Project> searchAll(Project project){
        return projectMapper.searchAll(project);
    }

    public Set<String> findDels(){
        return projectMapper.findDels();
    }

    public int insertOrUpdate(Project project){
        if(StringUtils.isBlank(project.getId())) {
            UserUtils.preAdd(project);
            return projectMapper.insert(project);
        } else {
            UserUtils.preUpdate(project);
            return projectMapper.updateById(project);
        }
    }

    /**
     * 查找暂存项目
     *
     * @param userId
     * @return
     */
    public Project getTempSave(String userId) {
        LambdaQueryWrapper<Project> queryWrapper = Wrappers.<Project>query().lambda();
        queryWrapper.eq(Project::getStatus, ProjectStatusEnum.NOT_APPLY.getValue());
        queryWrapper.eq(Project::getCreateBy, userId);
        return projectMapper.selectOne(queryWrapper);
    }


    public Set<Project> all() {
        return projectMapper.findAll();
    }

    public Page<Project> pageSearch(Page<Project> page, Project project) {
        LambdaQueryWrapper<Project> queryWrapper = Wrappers.<Project>query().lambda();

        if (project != null) {
            if (StringUtils.isNotEmpty(project.getCategoryId())) {
                queryWrapper.eq(Project::getCategoryId, project.getCategoryId());
            }
            if (StringUtils.isNotEmpty(project.getPlanTypeId())) {
                queryWrapper.eq(Project::getPlanTypeId, project.getPlanTypeId());
            }
            if (StringUtils.isNotEmpty(project.getProjectType())) {
                queryWrapper.eq(Project::getProjectType, project.getProjectType());
            }
            if (StringUtils.isNotEmpty(project.getStatus())) {
                queryWrapper.eq(Project::getStatus, project.getStatus());
            }
            if (StringUtils.isNotEmpty(project.getBatchId())) {
                queryWrapper.eq(Project::getBatchId, project.getBatchId());
            }
        }
        //查找项目归属
        SysUser user = UserUtils.getUser();
        //归属部门的项目，如果是普通用户则通过创建者查询,暂存就查自己
        if (user.getOfficeId() == null || project.getStatus().equals(ProjectStatusEnum.NOT_APPLY.getValue() + "")) {
            project.setCreateBy(user.getId());
            queryWrapper.eq(Project::getCreateBy, project.getCreateBy());
        } else {//用户有部门查找归属他部门下的项目
            //查找他的子孙
            SysOffice sysOffice = officeService.get(user.getOfficeId());
            List<SysOffice> children = officeService.findAllChildrenByCode(sysOffice.getCode());
            for (SysOffice office : children) {
                queryWrapper.or().eq(Project::getOfficeId, office.getId());
            }
        }

        //计算总数
        if (!page.isNotCount()) {
            page.setCount(projectMapper.selectCount(queryWrapper));
        }
        //添加排序
        String lastSql = "";
        if (StringUtils.isNotEmpty(page.getOrderBy())) {
            lastSql = " order by " + page.getOrderBy();
        }
        //添加分页
        lastSql = lastSql + " " + page.toLimit();
        queryWrapper.last(lastSql);//此方法只有最后一次调用的生效
        page.setList(projectMapper.selectList(queryWrapper));

        return page;
    }

    @Transactional
    public Project create(String name, String categoryId,
                       String areaId, String batchId, String planTypeId,
                       String orgName, String chargeName, String chargeTitle, String chargeMobile,
                       String chargeEmail, String orgAddress, String orgFax,
                       String orgPost, String content, String conditions, String basis,String target,
                       String stageName, String stageStartDate, String stageEndDate,
                       Float budget, Float funds,
                       String bank, String cardNo, String accounts) {
        if (StringUtils.isBlank(name)) {
            throw new ServiceException(PROJECT_NAME_REQUIRED);
        }

        Project project = new Project();
        if (budget != null) {
            if (FloatUtils.isMatch(budget)) {
                project.setBudget(budget);
            } else if (budget < 0) {
                throw new ServiceException(PROJECT_FLOAT_REQUIRED);
            }
        }
        if (funds != null) {
            if (FloatUtils.isMatch(funds)) {
                project.setFunds(funds);
            } else if (funds < 0) {
                throw new ServiceException(PROJECT_FLOAT_REQUIRED);
            }
        }
        /*if (StringUtils.isBlank(orgName)) {
            throw new ServiceException(PROJECT_ORG_NAME_REQUIRED);
        }
        if (StringUtils.isBlank(categoryId)) {
            throw new ServiceException(PROJECT_CATE_REQUIRED);
        }
        if (StringUtils.isBlank(areaId)) {
            throw new ServiceException(AREA_REQUIRED);
        }
        if (StringUtils.isBlank(batchId)) {
            throw new ServiceException(PROJECT_BATCH_REQUIRED);
        }
        if (StringUtils.isBlank(planTypeId)) {
            throw new ServiceException(PROJECT_PLAN_TYPE_REQUIRED);
        }*/
        //单位信息
        ProjectOrg org = new ProjectOrg();
        org.setOrgName(orgName);
        org.setChargeName(chargeName);
        org.setChargeTitle(chargeTitle);
        org.setChargeMobile(chargeMobile);
        org.setChargeEmail(chargeEmail);
        org.setOrgAddress(orgAddress);
        org.setOrgFax(orgFax);
        org.setOrgPost(orgPost);

        ProjectContent projectContent = new ProjectContent();
        projectContent.setContent(content);
        projectContent.setConditions(conditions);
        projectContent.setBasis(basis);
        projectContent.setTarget(target);

        //项目实施内容
        ProjectStage stage = new ProjectStage();
        stage.setName(stageName);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (!StringUtils.isBlank(stageStartDate) && !StringUtils.isBlank(stageEndDate)) {
            try {
                stage.setStartDate(sdf.parse(stageStartDate));
                stage.setEndDate(sdf.parse(stageEndDate));
            } catch (ParseException e) {
                throw new ServiceException(DATE_FORMAT_ERROR);
            }
        }
        //项目信息
        project.setBatchId(batchId);
        project.setPlanTypeId(planTypeId);
        project.setCategoryId(categoryId);
        project.setAreaId(areaId);
        project.setName(name);
        project.setBudget(budget);
        project.setFunds(funds);
        project.setAccounts(accounts);
        project.setBank(bank);
        project.setCardNo(cardNo);
        //暂存状态
        project.setStatus(ProjectStatusEnum.NOT_APPLY.getValue() + "");

        UserUtils.preAdd(org);
        orgMapper.insert(org);

        project.setOrgId(org.getId());
        UserUtils.preAdd(project);
        projectMapper.insert(project);

        //创建项目后
        UserUtils.preAdd(projectContent);
        projectContent.setProjectId(project.getId());
        contentMapper.insert(projectContent);

        UserUtils.preAdd(stage);
        stage.setProjectId(project.getId());
        stageMapper.insert(stage);
        return project;
    }

    @Transactional
    public void update(String projectId, String name, String categoryId,
                       String areaId, String batchId, String planTypeId,
                       String orgName, String chargeName, String chargeTitle, String chargeMobile,
                       String chargeEmail, String orgPhone, String orgAddress, String orgFax,
                       String orgPost, String content, String conditions, String basis,String target,
                       StageModel stageModel,
                       Float budget, Float funds,
                       String bank, String cardNo, String accounts) {
        Project project = get(projectId);
        if (project == null) {
            throw new ServiceException(PROJECT_NOT_EXIST);
        }
        if (budget != null) {
            if (FloatUtils.isMatch(budget)) {
                project.setBudget(budget);
            } else if (budget < 0) {
                throw new ServiceException(PROJECT_FLOAT_REQUIRED);
            }
        }
        if (funds != null) {
            if (FloatUtils.isMatch(funds)) {
                project.setFunds(funds);
            } else if (funds < 0) {
                throw new ServiceException(PROJECT_FLOAT_REQUIRED);
            }
        }
        ProjectOrg org = orgMapper.selectById(project.getOrgId());
        if (org == null) {
            org = new ProjectOrg();
        }
        if (!StringUtils.isBlank(orgName)) org.setOrgName(orgName);
        if (!StringUtils.isBlank(chargeEmail)) org.setChargeEmail(chargeEmail);
        if (!StringUtils.isBlank(chargeMobile)) org.setChargeMobile(chargeMobile);
        if (!StringUtils.isBlank(chargeTitle)) org.setChargeTitle(chargeTitle);
        if (!StringUtils.isBlank(chargeName)) org.setChargeName(chargeName);
        if (!StringUtils.isBlank(orgAddress)) org.setOrgAddress(orgAddress);
        if (!StringUtils.isBlank(orgFax)) org.setOrgFax(orgFax);
        if (!StringUtils.isBlank(orgPost)) org.setOrgPost(orgPost);
        if (!StringUtils.isBlank(orgPhone)) org.setOrgPhone(orgPhone);

        //项目实施阶段表
        List<ProjectStage> stages = stageModel.getStages();
        if(stages!=null){
            for (ProjectStage stage: stages) {
                ProjectStage projectStage = stageService.get(stage.getId());
                if(projectStage!=null){
                    stageMapper.updateById(stage);
                }else {
                    stageMapper.insert(stage);
                }
            }
        }

        ProjectContent projectContent = contentService.findByProjectId(projectId);
        if (projectContent == null) {
            projectContent = new ProjectContent();
        }
        if (!StringUtils.isBlank(content)) projectContent.setContent(content);
        if (!StringUtils.isBlank(conditions)) projectContent.setConditions(conditions);
        if (!StringUtils.isBlank(basis)) projectContent.setBasis(basis);
        if (!StringUtils.isBlank(target)) projectContent.setTarget(target);

        if (!StringUtils.isBlank(name)) project.setName(name);
        if (!StringUtils.isBlank(categoryId)) project.setCategoryId(categoryId);
        if (!StringUtils.isBlank(areaId)) project.setAreaId(areaId);
        if (!StringUtils.isBlank(batchId)) project.setBatchId(batchId);
        if (!StringUtils.isBlank(planTypeId)) project.setPlanTypeId(planTypeId);
        if (!StringUtils.isBlank(bank)) project.setBank(bank);
        if (!StringUtils.isBlank(cardNo)) project.setCardNo(cardNo);
        if (!StringUtils.isBlank(accounts)) project.setAccounts(accounts);

        orgService.insertOrUpdate(org);
        project.setOrgId(org.getId());
        contentService.insertOrUpdate(projectContent);
        insertOrUpdate(project);
    }

    //查找
    @Transactional
    public boolean delete(String id) {
        projectMapper.deleteById(id);
        return true;
    }

    //根据id查找orgId
    public  String findOrgId(String id){
        return projectMapper.findOrgId(id);
    }

    //根据id查找qx_office_id
    public String findQxOfficeId(String id){
        return projectMapper.findQxOfficeId(id);
    }

}

