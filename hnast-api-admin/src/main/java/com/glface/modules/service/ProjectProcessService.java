package com.glface.modules.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.glface.base.utils.DateUtils;
import com.glface.base.utils.FileUtils;
import com.glface.base.utils.StringUtils;
import com.glface.common.exeception.ServiceException;
import com.glface.model.SysOffice;
import com.glface.model.SysRole;
import com.glface.model.SysUser;
import com.glface.modules.mapper.ProjectMapper;
import com.glface.modules.mapper.ProjectPlanTypeMapper;
import com.glface.modules.mapper.ProjectProcessMapper;
import com.glface.modules.model.*;
import com.glface.modules.utils.ProjectNodeEnum;
import com.glface.modules.utils.ProjectStatusEnum;
import com.glface.modules.sys.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.glface.common.web.ApiCode.*;

@Slf4j
@Service
@Transactional(readOnly = true)
public class ProjectProcessService {

    private static String projectStatusType = "projectStatus";//数据字典type
    private static String districtOrgTypeValue = "2";//区县科协数据字典值
    private static String ROLE_FIRST_MANAGER = "初审管理员";
    private static String ROLE_RECOMMENT_MANAGER = "推荐管理员";
    private static String ROLE_EXPERT_MANAGER = "评审管理员";
    private static String ROLE_MATERIALS_MANAGER = "实施材料审批管理员";
    private static String ROLE_MANAGER = "管理员";

    @Value("${myself.fileUploadDir}")
    private String fileUploadDir;

    @Resource
    private ProjectProcessMapper projectProcessMapper;

    @Resource
    private ProjectMapper projectMapper;

    @Resource
    private UserService userService;

    @Resource
    private ProjectJsonService projectJsonService;

    @Resource
    private ProjectFileService projectFileService;

    @Resource
    private FileService fileService;

    @Resource
    private ProjectPlanTypeMapper projectPlanTypeMapper;

    @Resource
    private ProjectSmsService projectSmsService;
    @Resource
    private RoleService roleService;

    public ProjectProcess get(String id) {
        return projectProcessMapper.selectById(id);
    }

    /**
     * 项目发起提交
     *
     * @param project
     */
    @Transactional
    public void startSubmit(Project project) {
        //验证
        if (!allowUpdateProjectContent(project.getId())) {
            throw new ServiceException(PROJECT_PROCESS_SUBMIT_NOT_ALLOW);
        }

        //设置区级初审部门id
        ProjectPlanType planType = projectPlanTypeMapper.selectById(project.getPlanTypeId());
        SysUser currentUser = UserUtils.getUser();
        if (planType != null) {
            if ("BK".equals(planType.getCode())) {//B类项目
                //是否区级管理员审核的
                SysOffice districtOffice = isDistrictUser(currentUser.getId());
                if (districtOffice != null && StringUtils.isBlank(project.getQxOfficeId())) {
                    project.setQxOfficeId(districtOffice.getId());
                }
            }
        }
        //更新项目状态
        String status = project.getStatus();
        if (ProjectStatusEnum.DISTRICT_REJECT_WAIT.getValue().equals(status) ||
                ProjectStatusEnum.FIRST_REJECT_WAIT.getValue().equals(status) ||
                ProjectStatusEnum.RECOMMEND_REJECT_WAIT.getValue().equals(status)) {//返回上一个审核状态
            LambdaQueryWrapper<ProjectProcess> queryWrapper = Wrappers.<ProjectProcess>query().lambda();
            queryWrapper.eq(ProjectProcess::getProjectId, project.getId());
            queryWrapper.eq(ProjectProcess::getDelFlag, ProjectProcess.DEL_FLAG_NORMAL).orderByAsc(ProjectProcess::getCreateDate);
            //不需要加delFlag条件 查询时会自带
            List<ProjectProcess> processes = projectProcessMapper.selectList(queryWrapper);
            if (processes != null && processes.size() > 1) {
                project.setStatus(processes.get(processes.size() - 2).getResult());
            } else {
                project.setStatus(ProjectStatusEnum.SUBMITTED.getValue());
                projectSmsService.firstReview(project,false);
            }
        } else {
            project.setStatus(ProjectStatusEnum.SUBMITTED.getValue());
            projectSmsService.submitSuccess(project);
            projectSmsService.firstReview(project,false);
        }

        projectMapper.updateById(project);
        //更新过程
        Date now = new Date();
        List<SysOffice> offices = userService.findOfficesByUserId(currentUser.getId());
        ProjectProcess processSubmit = new ProjectProcess();
        processSubmit.setProjectId(project.getId());
        processSubmit.setNodeLabel(ProjectNodeEnum.START.getLabel());
        processSubmit.setNodeValue(ProjectNodeEnum.START.getValue());
        processSubmit.setStartTime(now);
        processSubmit.setEndTime(now);
        processSubmit.setDuration(0l);
        processSubmit.setUserId(currentUser.getId());
        if (offices.size() > 0) {
            processSubmit.setOfficeId(offices.get(0).getId());
        }
        processSubmit.setResult(ProjectStatusEnum.SUBMITTED.getValue());
        processSubmit.setResultOpinion(ProjectStatusEnum.SUBMITTED.getLabel());
        UserUtils.preAdd(processSubmit);
        projectProcessMapper.insert(processSubmit);
    }

    /**
     * 项目初审
     *
     * @param project
     * @param result            评审结果  1:通过  2：驳回 3:分发至区县审批   4:驳回补充资料后再审核
     * @param opinion           评审意见
     * @param toDistricOfficeId 分发至区县审批
     */
    @Transactional
    public void firstReview(Project project, String result, String opinion, String specialId, String toDistricOfficeId) {
        //验证
        if (!"1".equals(result) && !"2".equals(result) && !"3".equals(result) && !"4".equals(result)) {
            throw new ServiceException(PROJECT_PROCESS_INVALID_STATUS);
        }
        SysUser currentUser = UserUtils.getUser();
        //必须是初审管理员 或 管理员
        if (!hasFirstReviewRole(project.getId(), currentUser.getId())) {
            throw new ServiceException(PROJECT_PROCESS_NO_AUTHORITY);
        }
        //更新项目状态
        ProjectStatusEnum projectStatusEnum = null;
        ProjectNodeEnum projectNodeEnum = null;
        if ("1".equals(result)) {
            projectStatusEnum = ProjectStatusEnum.FIRST_AGREE;
            projectNodeEnum = ProjectNodeEnum.FIRST_REVIEW;
        } else if ("2".equals(result)) {
            projectStatusEnum = ProjectStatusEnum.FIRST_REJECT;
            projectNodeEnum = ProjectNodeEnum.FIRST_REVIEW;
        } else if ("3".equals(result)) {
            projectStatusEnum = ProjectStatusEnum.TO_DISTRICT;
            projectNodeEnum = ProjectNodeEnum.FIRST_REVIEW;
        } else if ("4".equals(result)) {
            projectStatusEnum = ProjectStatusEnum.FIRST_REJECT_WAIT;
            projectNodeEnum = ProjectNodeEnum.FIRST_REVIEW;
        }
        ProjectPlanType planType = projectPlanTypeMapper.selectById(project.getPlanTypeId());
        if ("BK".equals(planType.getCode())) {//B类项目
            //是否区级管理员审核的
            if (isDistrictFirstManager(currentUser.getId())) {
                if ("1".equals(result)) {
                    projectStatusEnum = ProjectStatusEnum.DISTRICT_AGREE;
                    projectNodeEnum = ProjectNodeEnum.DISTRICT_REVIEW;
                } else if ("2".equals(result)) {
                    projectStatusEnum = ProjectStatusEnum.DISTRICT_REJECT;
                    projectNodeEnum = ProjectNodeEnum.DISTRICT_REVIEW;
                }else if ("3".equals(result)) {
                    projectStatusEnum = ProjectStatusEnum.TO_DISTRICT;
                    projectNodeEnum = ProjectNodeEnum.DISTRICT_REVIEW;
                } else if ("4".equals(result)) {
                    projectStatusEnum = ProjectStatusEnum.DISTRICT_REJECT_WAIT;
                    projectNodeEnum = ProjectNodeEnum.DISTRICT_REVIEW;
                }
            }
        }

        //更新项目状态
        project.setStatus(projectStatusEnum.getValue());
        if ("3".equals(result)) {
            project.setQxOfficeId(toDistricOfficeId);
        }
        project.setSpecial(specialId);
        UserUtils.preUpdate(project);
        projectMapper.updateById(project);
        //更新过程
        Date now = new Date();
        List<SysOffice> offices = userService.findOfficesByUserId(currentUser.getId());
        ProjectProcess processNode = new ProjectProcess();
        processNode.setProjectId(project.getId());
        processNode.setNodeLabel(projectNodeEnum.getLabel());
        processNode.setNodeValue(projectNodeEnum.getValue());
        processNode.setEndTime(now);
        processNode.setUserId(currentUser.getId());
        if (offices.size() > 0) {
            processNode.setOfficeId(offices.get(0).getId());
        }
        processNode.setResult(projectStatusEnum.getValue());
        processNode.setResultOpinion(projectStatusEnum.getLabel());
        processNode.setResultOpinion(opinion);
        processNode.setDistrictOfficeId(toDistricOfficeId);
        UserUtils.preAdd(processNode);
        projectProcessMapper.insert(processNode);
        //发送短信
        if(projectStatusEnum==ProjectStatusEnum.FIRST_AGREE){
            projectSmsService.firstReviewSuccess(project);
        }else if(projectStatusEnum==ProjectStatusEnum.FIRST_REJECT
                ||projectStatusEnum==ProjectStatusEnum.FIRST_REJECT_WAIT
                ||projectStatusEnum==ProjectStatusEnum.DISTRICT_REJECT
                ||projectStatusEnum==ProjectStatusEnum.DISTRICT_REJECT_WAIT){
            projectSmsService.firstReviewReject(project);
        }
        if(projectStatusEnum==ProjectStatusEnum.DISTRICT_AGREE){
            projectSmsService.firstReview(project,true);
        }
        if(projectStatusEnum==ProjectStatusEnum.FIRST_AGREE){//给推荐管理员发送短信
            projectSmsService.recommentReview(project);
        }
    }

    /**
     * 推荐审核
     *
     * @param project
     * @param result  评审结果  1:通过  2：驳回  4:驳回补充资料后再审核
     * @param opinion 评审意见
     */
    @Transactional
    public void recommendReview(Project project, String result, String opinion) {
        //验证
        if (!"1".equals(result) && !"2".equals(result) && !"4".equals(result)) {
            throw new ServiceException(PROJECT_PROCESS_INVALID_STATUS);
        }
        SysUser currentUser = UserUtils.getUser();
        //必须是推荐管理员 或 管理员
        if (!hasRecommendReviewRole(currentUser.getId())) {
            throw new ServiceException(PROJECT_PROCESS_NO_AUTHORITY);
        }
        if (!ProjectStatusEnum.FIRST_AGREE.getValue().equals(project.getStatus())) {
            throw new ServiceException(PROJECT_PROCESS_NOT_FIRST_AGREE);
        }
        //更新项目状态
        ProjectStatusEnum projectStatusEnum = null;
        ProjectNodeEnum projectNodeEnum = null;
        if ("1".equals(result)) {
            projectStatusEnum = ProjectStatusEnum.RECOMMEND_AGREE;
            projectNodeEnum = ProjectNodeEnum.RECOMMEND_REVIEW;
        } else if ("2".equals(result)) {
            projectStatusEnum = ProjectStatusEnum.RECOMMEND_REJECT;
            projectNodeEnum = ProjectNodeEnum.RECOMMEND_REVIEW;
        } else if ("4".equals(result)) {
            projectStatusEnum = ProjectStatusEnum.RECOMMEND_REJECT_WAIT;
            projectNodeEnum = ProjectNodeEnum.RECOMMEND_REVIEW;
        }

        //更新项目状态
        project.setStatus(projectStatusEnum.getValue());
        UserUtils.preUpdate(project);
        projectMapper.updateById(project);
        //更新过程
        Date now = new Date();
        List<SysOffice> offices = userService.findOfficesByUserId(currentUser.getId());
        ProjectProcess processNode = new ProjectProcess();
        processNode.setProjectId(project.getId());
        processNode.setNodeLabel(projectNodeEnum.getLabel());
        processNode.setNodeValue(projectNodeEnum.getValue());
        processNode.setEndTime(now);
        processNode.setUserId(currentUser.getId());
        if (offices.size() > 0) {
            processNode.setOfficeId(offices.get(0).getId());
        }
        processNode.setResult(projectStatusEnum.getValue());
        processNode.setResultOpinion(projectStatusEnum.getLabel());
        processNode.setResultOpinion(opinion);
        UserUtils.preAdd(processNode);
        projectProcessMapper.insert(processNode);
        //发送短信
        if(projectStatusEnum == ProjectStatusEnum.RECOMMEND_AGREE){
            projectSmsService.recommendAgree(project);
        }else if(projectStatusEnum == ProjectStatusEnum.RECOMMEND_REJECT||projectStatusEnum == ProjectStatusEnum.RECOMMEND_REJECT_WAIT){
            projectSmsService.recommendReject(project);
        }
        if(projectStatusEnum == ProjectStatusEnum.RECOMMEND_AGREE){//给评审管理员发送短信
            projectSmsService.expertReview(project);
        }
    }

    /**
     * 专家评审
     *
     * @param project
     * @param result  评审结果  1:通过  2：驳回
     * @param opinion 评审意见
     */
    @Transactional
    public void expertReview(Project project, String result, String opinion) {
        //验证
        if (!"1".equals(result) && !"2".equals(result)) {
            throw new ServiceException(PROJECT_PROCESS_INVALID_STATUS);
        }
        if (!ProjectStatusEnum.RECOMMEND_AGREE.getValue().equals(project.getStatus())) {
            throw new ServiceException(PROJECT_PROCESS_NOT_RECOMMEND_AGREE);
        }
        SysUser currentUser = UserUtils.getUser();
        //必须是推荐管理员 或 管理员
        if (!hasExpertReviewRole(currentUser.getId())) {
            throw new ServiceException(PROJECT_PROCESS_NO_AUTHORITY);
        }
        //更新项目状态
        ProjectStatusEnum projectStatusEnum = null;
        ProjectNodeEnum projectNodeEnum = null;
        if ("1".equals(result)) {
            projectStatusEnum = ProjectStatusEnum.EXPERT_AGREE;
            projectNodeEnum = ProjectNodeEnum.EXPERT_REVIEW;
        } else if ("2".equals(result)) {
            projectStatusEnum = ProjectStatusEnum.EXPERT_REJECT;
            projectNodeEnum = ProjectNodeEnum.EXPERT_REVIEW;
        }

        //更新项目状态
        project.setStatus(projectStatusEnum.getValue());
        if (ProjectStatusEnum.EXPERT_AGREE.getValue().equals(project.getStatus())) {
            project.setPassReviewFlag("1");
        }
        UserUtils.preUpdate(project);
        projectMapper.updateById(project);
        //更新过程
        Date now = new Date();
        List<SysOffice> offices = userService.findOfficesByUserId(currentUser.getId());
        ProjectProcess processNode = new ProjectProcess();
        processNode.setProjectId(project.getId());
        processNode.setNodeLabel(projectNodeEnum.getLabel());
        processNode.setNodeValue(projectNodeEnum.getValue());
        processNode.setEndTime(now);
        processNode.setUserId(currentUser.getId());
        if (offices.size() > 0) {
            processNode.setOfficeId(offices.get(0).getId());
        }
        processNode.setResult(projectStatusEnum.getValue());
        processNode.setResultOpinion(projectStatusEnum.getLabel());
        processNode.setResultOpinion(opinion);
        UserUtils.preAdd(processNode);
        projectProcessMapper.insert(processNode);
        if(projectStatusEnum == ProjectStatusEnum.EXPERT_AGREE){
            projectSmsService.expertAgree(project);
        }else if(projectStatusEnum == ProjectStatusEnum.EXPERT_REJECT){
            projectSmsService.expertReject(project);
        }
    }

    /**
     * 实施库撤回
     *
     */
    @Transactional
    public void revoke(Project project) {
        if (!ProjectStatusEnum.EXPERT_AGREE.getValue().equals(project.getStatus())) {
            throw new ServiceException(PROJECT_PROCESS_NOT_REVIEW_AGREE);
        }
        SysUser currentUser = UserUtils.getUser();
        //必须是推荐管理员 或 管理员
        if (!hasExpertReviewRole(currentUser.getId())) {
            throw new ServiceException(PROJECT_PROCESS_NO_AUTHORITY);
        }
        //更新项目状态
        ProjectStatusEnum projectStatusEnum = ProjectStatusEnum.RECOMMEND_AGREE;
        ProjectNodeEnum projectNodeEnum = ProjectNodeEnum.RECOMMEND_REVIEW;

        //更新项目状态
        project.setStatus(projectStatusEnum.getValue());
        if (ProjectStatusEnum.RECOMMEND_AGREE.getValue().equals(project.getStatus())) {
            project.setPassReviewFlag("0");
        }
        UserUtils.preUpdate(project);
        projectMapper.updateById(project);
        //更新过程
        Date now = new Date();
        List<SysOffice> offices = userService.findOfficesByUserId(currentUser.getId());
        ProjectProcess processNode = new ProjectProcess();
        processNode.setProjectId(project.getId());
        processNode.setNodeLabel(projectNodeEnum.getLabel());
        processNode.setNodeValue(projectNodeEnum.getValue());
        processNode.setEndTime(now);
        processNode.setUserId(currentUser.getId());
        if (offices.size() > 0) {
            processNode.setOfficeId(offices.get(0).getId());
        }
        processNode.setResult(projectStatusEnum.getValue());
        processNode.setResultOpinion(projectStatusEnum.getLabel());
        UserUtils.preAdd(processNode);
        projectProcessMapper.insert(processNode);
//        if(projectStatusEnum == ProjectStatusEnum.EXPERT_AGREE){
//            projectSmsService.expertAgree(project);
//        }else if(projectStatusEnum == ProjectStatusEnum.EXPERT_REJECT){
//            projectSmsService.expertReject(project);
//        }
    }

    /**
     * 实施材料提交审批
     */
    @Transactional
    public void materialsSubmitReview(String projectId) {
        Project project = projectMapper.selectById(projectId);
        if (!"1".equals(project.getPassReviewFlag())) {
            throw new ServiceException(PROJECT_PROCESS_NOT_PASS_REVIEW);
        }

        //更新项目状态
        project.setStatus(ProjectStatusEnum.MATERIALS_SUBMITTED.getValue());
        UserUtils.preUpdate(project);
        projectMapper.updateById(project);
        //更新过程
        SysUser currentUser = UserUtils.getUser();
        Date now = new Date();
        List<SysOffice> offices = userService.findOfficesByUserId(currentUser.getId());
        ProjectProcess processNode = new ProjectProcess();
        processNode.setProjectId(project.getId());
        processNode.setNodeLabel(ProjectNodeEnum.MATERIALS.getLabel());
        processNode.setNodeValue(ProjectNodeEnum.MATERIALS.getValue());
        processNode.setEndTime(now);
        processNode.setUserId(currentUser.getId());
        if (offices.size() > 0) {
            processNode.setOfficeId(offices.get(0).getId());
        }
        processNode.setResult(ProjectStatusEnum.MATERIALS_SUBMITTED.getValue());
        processNode.setResultOpinion(ProjectStatusEnum.MATERIALS_SUBMITTED.getLabel());
        UserUtils.preAdd(processNode);
        projectProcessMapper.insert(processNode);
        projectSmsService.materialsReview(project,false);
    }

    /**
     * 实施材料审批
     *
     * @param project
     * @param result  评审结果  1:通过  2：驳回
     * @param opinion 评审意见
     */
    @Transactional
    public void materialsReview(Project project, String result, String opinion) {
        //验证
        if (!"1".equals(result) && !"2".equals(result)) {
            throw new ServiceException(PROJECT_PROCESS_INVALID_STATUS);
        }
        if (!"1".equals(project.getPassReviewFlag())) {
            throw new ServiceException(PROJECT_PROCESS_NOT_PASS_REVIEW);
        }
        SysUser currentUser = UserUtils.getUser();
        //必须是实施材料管理员 或 管理员
        if (!hasMaterialsReviewRole(currentUser.getId())) {
            throw new ServiceException(PROJECT_PROCESS_NO_AUTHORITY);
        }
        //更新项目状态
        ProjectStatusEnum projectStatusEnum = null;
        ProjectNodeEnum projectNodeEnum = null;
        if ("1".equals(result)) {
            projectStatusEnum = ProjectStatusEnum.MATERIALS_AGREE;
            projectNodeEnum = ProjectNodeEnum.MATERIALS_REVIEW;
        } else if ("2".equals(result)) {
            projectStatusEnum = ProjectStatusEnum.MATERIALS_REJECT;
            projectNodeEnum = ProjectNodeEnum.MATERIALS_REVIEW;
        }

        //更新项目状态
        project.setStatus(projectStatusEnum.getValue());
        UserUtils.preUpdate(project);
        projectMapper.updateById(project);
        //更新过程
        Date now = new Date();
        List<SysOffice> offices = userService.findOfficesByUserId(currentUser.getId());
        ProjectProcess processNode = new ProjectProcess();
        processNode.setProjectId(project.getId());
        processNode.setNodeLabel(projectNodeEnum.getLabel());
        processNode.setNodeValue(projectNodeEnum.getValue());
        processNode.setEndTime(now);
        processNode.setUserId(currentUser.getId());
        if (offices.size() > 0) {
            processNode.setOfficeId(offices.get(0).getId());
        }
        processNode.setResult(projectStatusEnum.getValue());
        processNode.setResultOpinion(projectStatusEnum.getLabel());
        processNode.setResultOpinion(opinion);
        UserUtils.preAdd(processNode);
        projectProcessMapper.insert(processNode);
    }

    /**
     * 归档
     */
    @Transactional
    public void placeOnFile(Project project) {
        //验证
        if (!ProjectStatusEnum.MATERIALS_AGREE.getValue().equals(project.getStatus())) {
            throw new ServiceException(PROJECT_PROCESS_NOT_MATERIALS_AGREE);
        }
        SysUser currentUser = UserUtils.getUser();
        //必须是管理员
        if (!isManager(currentUser.getId())) {
            throw new ServiceException(PROJECT_PROCESS_NO_AUTHORITY);
        }
        //打包相关文件
        String zipPath = projectZip(project);
        //更新项目状态
        ProjectStatusEnum projectStatusEnum = ProjectStatusEnum.FILE;
        ProjectNodeEnum projectNodeEnum = ProjectNodeEnum.FILE;

        //更新项目状态
        project.setStatus(projectStatusEnum.getValue());
        project.setZipPath(zipPath);
        UserUtils.preUpdate(project);
        projectMapper.updateById(project);
        //更新过程
        Date now = new Date();
        List<SysOffice> offices = userService.findOfficesByUserId(currentUser.getId());
        ProjectProcess processNode = new ProjectProcess();
        processNode.setProjectId(project.getId());
        processNode.setNodeLabel(projectNodeEnum.getLabel());
        processNode.setNodeValue(projectNodeEnum.getValue());
        processNode.setEndTime(now);
        processNode.setUserId(currentUser.getId());
        if (offices.size() > 0) {
            processNode.setOfficeId(offices.get(0).getId());
        }
        processNode.setResult(projectStatusEnum.getValue());
        processNode.setResultOpinion(projectStatusEnum.getLabel());
        processNode.setResultOpinion("");
        UserUtils.preAdd(processNode);
        projectProcessMapper.insert(processNode);
    }

    public List<ProjectProcess> findByProjectId(String projectId) {
        LambdaQueryWrapper<ProjectProcess> queryWrapper = Wrappers.<ProjectProcess>query().lambda();
        queryWrapper.eq(ProjectProcess::getProjectId, projectId).orderByAsc(ProjectProcess::getCreateDate);
        return projectProcessMapper.selectList(queryWrapper);
    }

    /**
     * 依据节点名称模糊查询
     * @param nodelLabel
     */
    public List<ProjectProcess> findByNodeLabel(String nodelLabel) {
        LambdaQueryWrapper<ProjectProcess> queryWrapper = Wrappers.<ProjectProcess>query().lambda();
        queryWrapper.like(ProjectProcess::getNodeLabel,nodelLabel)
                .eq(ProjectProcess::getDelFlag,ProjectProcess.DEL_FLAG_NORMAL);
        return projectProcessMapper.selectList(queryWrapper);
    }

    /**
     * 依据节点值查询
     */
    public List<ProjectProcess> findByNodeValue(String nodelValue) {
        LambdaQueryWrapper<ProjectProcess> queryWrapper = Wrappers.<ProjectProcess>query().lambda();
        queryWrapper.eq(ProjectProcess::getNodeValue,nodelValue)
                .eq(ProjectProcess::getDelFlag,ProjectProcess.DEL_FLAG_NORMAL);
        return projectProcessMapper.selectList(queryWrapper);
    }

    /**
     * 项目资料打包
     */
    public String projectZip(Project project) throws ServiceException {
        //归档目录
        File dest = new File(Paths.get(fileUploadDir, "placeOnFile", project.getId()).toString());
        if (!dest.exists()) {
            dest.mkdirs();
        }
        //临时目录 用于暂存待打包文件
        File tmp = new File(Paths.get(dest.getAbsolutePath(), "tmp" + System.currentTimeMillis() + String.format("%04d", new Random().nextInt(9999))).toString());
        if (!tmp.exists()) {
            tmp.mkdir();
        }
        //项目申报表
        byte[] pdfBytes = projectJsonService.genPdf(project.getId());
        String declarationTable = Paths.get(tmp.getAbsolutePath(), project.getName()+project.getCode()+ ".pdf").toString();//申报表
        ZipOutputStream zipOut = null;
        try {
            FileUtils.writeByteArrayToFile(new File(declarationTable), pdfBytes);

            //项目申报附件
            File attachmentDir = new File(Paths.get(tmp.getAbsolutePath(), "附件").toString());
            if (!attachmentDir.exists()) {
                attachmentDir.mkdir();
            }
            List<ProjectFile> financialFiles = projectFileService.findByProjectIdAndType(project.getId(), "financial");//财务制度
            List<ProjectFile> manageFiles = projectFileService.findByProjectIdAndType(project.getId(), "manage");//项目管理制度
            List<ProjectFile> feasibilityFiles = projectFileService.findByProjectIdAndType(project.getId(), "feasibility");//可行性研究报告
            List<ProjectFile> creditFiles = projectFileService.findByProjectIdAndType(project.getId(), "credit");//信用代码
            for (ProjectFile projectFile : financialFiles) {
                String fileId = projectFile.getFileId();
                FileInfo fileInfo = fileService.get(fileId);
                if (fileInfo == null || FileInfo.DEL_FLAG_DELETE == fileInfo.getDelFlag() || StringUtils.isBlank(fileInfo.getAbsoluteAddress())) {
                    continue;
                }
                FileUtils.copyFile(fileInfo.getAbsoluteAddress(), Paths.get(attachmentDir.getAbsolutePath(), fileInfo.getName()).toString());
            }
            for (ProjectFile projectFile : manageFiles) {
                String fileId = projectFile.getFileId();
                FileInfo fileInfo = fileService.get(fileId);
                if (fileInfo == null || FileInfo.DEL_FLAG_DELETE == fileInfo.getDelFlag() || StringUtils.isBlank(fileInfo.getAbsoluteAddress())) {
                    continue;
                }
                FileUtils.copyFile(fileInfo.getAbsoluteAddress(), Paths.get(attachmentDir.getAbsolutePath(), fileInfo.getName()).toString());
            }
            for (ProjectFile projectFile : feasibilityFiles) {
                String fileId = projectFile.getFileId();
                FileInfo fileInfo = fileService.get(fileId);
                if (fileInfo == null || FileInfo.DEL_FLAG_DELETE == fileInfo.getDelFlag() || StringUtils.isBlank(fileInfo.getAbsoluteAddress())) {
                    continue;
                }
                FileUtils.copyFile(fileInfo.getAbsoluteAddress(), Paths.get(attachmentDir.getAbsolutePath(), fileInfo.getName()).toString());
            }
            for (ProjectFile projectFile : creditFiles) {
                String fileId = projectFile.getFileId();
                FileInfo fileInfo = fileService.get(fileId);
                if (fileInfo == null || FileInfo.DEL_FLAG_DELETE == fileInfo.getDelFlag() || StringUtils.isBlank(fileInfo.getAbsoluteAddress())) {
                    continue;
                }
                FileUtils.copyFile(fileInfo.getAbsoluteAddress(), Paths.get(attachmentDir.getAbsolutePath(), fileInfo.getName()).toString());
            }
            //实施材料
            File deployDir = new File(Paths.get(tmp.getAbsolutePath(), "实施材料").toString());
            if (!deployDir.exists()) {
                deployDir.mkdir();
            }
            List<ProjectFile> deployFiles = projectFileService.findByProjectIdAndType(project.getId(), "deploy");
            for (ProjectFile projectFile : deployFiles) {
                String fileId = projectFile.getFileId();
                FileInfo fileInfo = fileService.get(fileId);
                if (fileInfo == null || FileInfo.DEL_FLAG_DELETE == fileInfo.getDelFlag() || StringUtils.isBlank(fileInfo.getAbsoluteAddress())) {
                    continue;
                }
                FileUtils.copyFile(fileInfo.getAbsoluteAddress(), Paths.get(deployDir.getAbsolutePath(), fileInfo.getName()).toString());
            }
            //压缩
            String zipPath = Paths.get(dest.getAbsolutePath(), project.getName()+project.getCode()+".zip").toString();
            zipOut = new ZipOutputStream(new FileOutputStream(zipPath));
            zipFile(tmp, project.getName()+project.getCode(), zipOut);
            //删除临时文件
            FileUtils.deleteDirectory(tmp);
            return zipPath;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceException(e.getMessage());
        }finally {
            if(zipOut!=null){
                try {
                    zipOut.close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                    throw new ServiceException(e.getMessage());
                }
            }
        }

    }

    private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                zipOut.closeEntry();
            }
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            return;
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }

    /**
     * 是否有初审的权限
     */
    public boolean hasFirstReviewRole(String projectId, String userId) {
        SysUser user = userService.get(userId);
        if (user == null) {
            throw new ServiceException(PERMISSION_USER_NOTEXIST);
        }
        List<SysRole> roles = userService.findRolesByUserId(userId);
        boolean isManager = false;
        for (SysRole role : roles) {
            if (role.getName().equals(ROLE_MANAGER)) {
                isManager = true;
                break;
            }
        }
        if (isManager) {
            return true;
        }
        boolean isFirstManager = false;
        for (SysRole role : roles) {
            if (role.getName().equals(ROLE_FIRST_MANAGER)) {
                isFirstManager = true;
                break;
            }
        }
        if (!isFirstManager) {//不是初审管理员
            return false;
        }
        //查询项目归口部门
        Project project = projectMapper.selectById(projectId);
        ProjectPlanType planType = projectPlanTypeMapper.selectById(project.getPlanTypeId());
        if (planType == null) {
            throw new ServiceException(PROJECT_PLAN_TYPE_OFFICEID_REQUIRED);
        }
        String planTypeOfficeId = planType.getOfficeId();//归口部门
        String planTypeCode = planType.getCode();

        List<SysOffice> offices = userService.findOfficesByUserId(userId);
        boolean hasPlanTypeOffice = false;
        for (SysOffice office : offices) {
            if (office.getId().equals(planTypeOfficeId)) {
                hasPlanTypeOffice = true;
                break;
            }
        }
        if (hasPlanTypeOffice) {
            return true;
        }
        //如果是B类项目 区县可以审核
        if ("BK".equals(planTypeCode)) {
            boolean hasProjectOffice = false;
            String gkbmId = project.getQxOfficeId();
//            if (StringUtils.isBlank(gkbmId)) {
//                gkbmId = project.getOfficeId();
//            }
            for (SysOffice office : offices) {
                if (office.getId().equals(gkbmId)) {
                    hasProjectOffice = true;
                    break;
                }
            }
            if (hasProjectOffice) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否允许修改内容
     */
    public boolean allowUpdateProjectContent(String projectId) {
        Project project = projectMapper.selectById(projectId);
        //验证
        if (ProjectStatusEnum.NOT_APPLY.getValue().equals(project.getStatus())
                || ProjectStatusEnum.DISTRICT_REJECT_WAIT.getValue().equals(project.getStatus())
                || ProjectStatusEnum.FIRST_REJECT_WAIT.getValue().equals(project.getStatus())
                || ProjectStatusEnum.RECOMMEND_REJECT_WAIT.getValue().equals(project.getStatus())) {
            return true;
        }
        return false;
    }

    /**
     * 是否管理员
     *
     * @param userId
     * @return
     */
    public boolean isManager(String userId) {
        SysUser user = userService.get(userId);
        if (user == null) {
            throw new ServiceException(PERMISSION_USER_NOTEXIST);
        }
        List<SysRole> roles = userService.findRolesByUserId(userId);
        boolean isManager = false;
        for (SysRole role : roles) {
            if (role.getName().equals(ROLE_MANAGER)) {
                isManager = true;
                break;
            }
        }
        if (isManager) {
            return true;
        }
        return false;
    }

    /**
     * 是否初审人员
     *
     * @param userId
     * @return
     */
    public boolean isFirstManager(String userId) {
        SysUser user = userService.get(userId);
        if (user == null) {
            throw new ServiceException(PERMISSION_USER_NOTEXIST);
        }
        List<SysRole> roles = userService.findRolesByUserId(userId);
        boolean isFirstManager = false;
        for (SysRole role : roles) {
            if (role.getName().equals(ROLE_FIRST_MANAGER)) {
                isFirstManager = true;
                break;
            }
        }
        if (isFirstManager) {
            return true;
        }
        return false;
    }

    /**
     * 是否区县初审人员
     *
     * @param userId
     * @return
     */
    public boolean isDistrictFirstManager(String userId) {
        SysUser user = userService.get(userId);
        if (user == null) {
            throw new ServiceException(PERMISSION_USER_NOTEXIST);
        }
        List<SysRole> roles = userService.findRolesByUserId(userId);
        boolean isFirstManager = false;
        for (SysRole role : roles) {
            if (role.getName().equals(ROLE_FIRST_MANAGER)) {
                isFirstManager = true;
                break;
            }
        }
        if (!isFirstManager) {//不是初审管理员
            return false;
        }
        List<SysOffice> offices = userService.findOfficesByUserId(userId);
        boolean isDistrict = false;
        for (SysOffice office : offices) {
            if (office.getType().equals(districtOrgTypeValue)) {
                isDistrict = true;
                break;
            }
        }
        if (isDistrict) {
            return true;
        }
        return false;
    }

    /**
     * 是否区县人员
     *
     * @param userId
     * @return
     */
    public SysOffice isDistrictUser(String userId) {
        SysUser user = userService.get(userId);
        if (user == null) {
            throw new ServiceException(PERMISSION_USER_NOTEXIST);
        }
        List<SysOffice> offices = userService.findOfficesByUserId(userId);
        boolean isDistrict = false;
        SysOffice districtOffice = null;
        for (SysOffice office : offices) {
            if (office.getType() != null && office.getType().equals(districtOrgTypeValue)) {
                isDistrict = true;
                districtOffice = office;
                break;
            }
        }
        if (isDistrict) {
            return districtOffice;
        }
        return null;
    }

    /**
     * 是否有推荐审核的权限
     */
    public boolean hasRecommendReviewRole(String userId) {
        SysUser user = userService.get(userId);
        if (user == null) {
            throw new ServiceException(PERMISSION_USER_NOTEXIST);
        }
        List<SysRole> roles = userService.findRolesByUserId(userId);
        boolean isManager = false;
        for (SysRole role : roles) {
            if (role.getName().equals(ROLE_MANAGER)) {
                isManager = true;
                break;
            }
        }
        if (isManager) {
            return true;
        }
        boolean isRecommendManager = false;
        for (SysRole role : roles) {
            if (role.getName().equals(ROLE_RECOMMENT_MANAGER)) {
                isRecommendManager = true;
                break;
            }
        }
        if (isRecommendManager) {
            return true;
        }
        return false;
    }

    /**
     * 是否有专家评审的权限
     */
    public boolean hasExpertReviewRole(String userId) {
        SysUser user = userService.get(userId);
        if (user == null) {
            throw new ServiceException(PERMISSION_USER_NOTEXIST);
        }
        List<SysRole> roles = userService.findRolesByUserId(userId);
        boolean isManager = false;
        for (SysRole role : roles) {
            if (role.getName().equals(ROLE_MANAGER)) {
                isManager = true;
                break;
            }
        }
        if (isManager) {
            return true;
        }
        boolean isExpertManager = false;
        for (SysRole role : roles) {
            if (role.getName().equals(ROLE_EXPERT_MANAGER)) {
                isExpertManager = true;
                break;
            }
        }
        if (isExpertManager) {
            return true;
        }
        return false;
    }

    /**
     * 是否有实施材料评审的权限
     */
    public boolean hasMaterialsReviewRole(String userId) {
        SysUser user = userService.get(userId);
        if (user == null) {
            throw new ServiceException(PERMISSION_USER_NOTEXIST);
        }
        List<SysRole> roles = userService.findRolesByUserId(userId);
        boolean isManager = false;
        for (SysRole role : roles) {
            if (role.getName().equals(ROLE_MANAGER)) {
                isManager = true;
                break;
            }
        }
        if (isManager) {
            return true;
        }
        boolean isExpertManager = false;
        for (SysRole role : roles) {
            if (role.getName().equals(ROLE_MATERIALS_MANAGER)) {
                isExpertManager = true;
                break;
            }
        }
        if (isExpertManager) {
            return true;
        }
        return false;
    }

    /**
     * 查找项目(B类)的区县管理员
     * @param project
     * @return
     */
    public List<SysUser> findDistrictManagerUsers(Project project){
        List<SysUser> districtUsers = new ArrayList<>();
        ProjectPlanType planType = projectPlanTypeMapper.selectById(project.getPlanTypeId());
        if (planType == null) {
            return districtUsers;
        }
        String planTypeCode = planType.getCode();
        if (!"BK".equals(planTypeCode)) {
            return districtUsers;
        }
        String qxOfficeId = project.getQxOfficeId();
        if(StringUtils.isBlank(qxOfficeId)){
            return districtUsers;
        }
        SysRole role = roleService.getByName(ROLE_FIRST_MANAGER);
        List<SysUser> users = userService.findUserByRoleId(role.getId());
        for(SysUser user:users){
            List<SysOffice> offices = userService.findOfficesByUserId(user.getId());
            boolean hasQxOffice = false;
            for (SysOffice office : offices) {
                if (office.getId().equals(qxOfficeId)) {
                    hasQxOffice = true;
                    break;
                }
            }
            if(hasQxOffice){
                districtUsers.add(user);
            }
        }
        return districtUsers;
    }

    /**
     * 查找归口部门管理员
     * @param project
     * @return
     */
    public List<SysUser> findGkbmManagerUsers(Project project){
        List<SysUser> districtUsers = new ArrayList<>();
        ProjectPlanType planType = projectPlanTypeMapper.selectById(project.getPlanTypeId());
        if (planType == null) {
            return districtUsers;
        }
        String planTypeOfficeId = planType.getOfficeId();//归口部门
        if(StringUtils.isBlank(planTypeOfficeId)){
            return districtUsers;
        }
        SysRole role = roleService.getByName(ROLE_FIRST_MANAGER);
        List<SysUser> users = userService.findUserByRoleId(role.getId());
        for(SysUser user:users){
            List<SysOffice> offices = userService.findOfficesByUserId(user.getId());
            boolean hasOffice = false;
            for (SysOffice office : offices) {
                if (office.getId().equals(planTypeOfficeId)) {
                    hasOffice = true;
                    break;
                }
            }
            if(hasOffice){
                districtUsers.add(user);
            }
        }
        return districtUsers;
    }
    /**
     * 查找推荐管理员
     * @param project
     * @return
     */
    public List<SysUser> findRecommentManagers(Project project){
        List<SysUser> users = new ArrayList<>();
        SysRole role = roleService.getByName(ROLE_RECOMMENT_MANAGER);
        if(role==null){
            return users;
        }
        users = userService.findUserByRoleId(role.getId());
        return users;
    }

    /**
     * 查找评审管理员
     * @param project
     * @return
     */
    public List<SysUser> findExpertManagers(Project project){
        List<SysUser> users = new ArrayList<>();
        SysRole role = roleService.getByName(ROLE_EXPERT_MANAGER);
        if(role==null){
            return users;
        }
        users = userService.findUserByRoleId(role.getId());
        return users;
    }

    /**
     * 查找实施材料管理员
     * @param project
     * @return
     */
    public List<SysUser> findMaterialsManagers(Project project){
        List<SysUser> users = new ArrayList<>();
        SysRole role = roleService.getByName(ROLE_MATERIALS_MANAGER);
        if(role==null){
            return users;
        }
        users = userService.findUserByRoleId(role.getId());
        return users;
    }

}

