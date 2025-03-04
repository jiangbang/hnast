package com.glface.modules.sp.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.glface.base.utils.StringUtils;
import com.glface.common.exeception.ServiceException;
import com.glface.model.SysOffice;
import com.glface.model.SysRole;
import com.glface.model.SysUser;
import com.glface.modules.model.*;
import com.glface.modules.service.UserService;
import com.glface.modules.sp.mapper.ExpertMapper;
import com.glface.modules.sp.mapper.ExpertProcessMapper;
import com.glface.modules.sp.model.BaseCategory;
import com.glface.modules.sp.model.Expert;
import com.glface.modules.sp.model.ExpertCategory;
import com.glface.modules.sp.model.ExpertProcess;
import com.glface.modules.sys.utils.UserUtils;
import com.glface.modules.utils.ExpertStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

import static com.glface.common.web.ApiCode.*;

@Slf4j
@Service
@Transactional(readOnly = true)
public class ExpertProcessService {

    private static String expertStatusType = "expertStatus";//数据字典type
    private static String GKBM_MANAGER = "专家库归口部门管理员";
    private static String ROLE_MANAGER = "专家库管理员";//拥有全部权限

    @Value("${myself.fileUploadDir}")
    private String fileUploadDir;

    @Resource
    private ExpertProcessMapper expertProcessMapper;

    @Resource
    private ExpertMapper expertMapper;

    @Resource
    private ExpertCategoryService expertCategoryService;
    @Resource
    private BaseCategoryService baseCategoryService;
    @Resource
    private UserService userService;

    public ExpertProcess get(String id) {
        return expertProcessMapper.selectById(id);
    }

    /**
     * 发起提交
     * @param expert
     */
    @Transactional
    public void startSubmit(Expert expert) {
        //验证
        if (!allowUpdateContent(expert.getId())) {
            throw new ServiceException(SP_EXPERT_SUBMIT_NOT_ALLOW);
        }

        SysUser currentUser = UserUtils.getUser();
        String currentUserId = currentUser!=null?currentUser.getId():"";

        //更新状态
        String status = expert.getStatus();
        if(StringUtils.isBlank(status)||ExpertStatusEnum.NOT_APPLY.getValue().equals(status)||ExpertStatusEnum.REJECT.getValue().equals(status)){
            expert.setStatus(ExpertStatusEnum.SUBMITTED.getValue());
        }
        expertMapper.updateById(expert);

        //更新过程
        String officeId = "";
        if(StringUtils.isNotBlank(currentUserId)){
            List<SysOffice> offices = userService.findOfficesByUserId(currentUserId);
            if(offices.size()>0){
                officeId = offices.get(0).getId();
            }
        }
        ExpertProcess processSubmit = new ExpertProcess();
        processSubmit.setExpertId(expert.getId());
        processSubmit.setUserId(currentUserId);
        processSubmit.setOfficeId(officeId);

        processSubmit.setResult(ExpertStatusEnum.SUBMITTED.getValue());
        processSubmit.setResultOpinion(ExpertStatusEnum.SUBMITTED.getLabel());
        UserUtils.preAdd(processSubmit);
        expertProcessMapper.insert(processSubmit);
    }

    /**
     * 评审
     * @param result            评审结果  1:通过  2：未通过
     * @param opinion           评审意见
     */
    @Transactional
    public void review(Expert expert, String result, String opinion) {
        //验证
        if (!"1".equals(result) && !"2".equals(result) ) {
            throw new ServiceException(SP_EXPERT_INVALID_STATUS);
        }
        SysUser currentUser = UserUtils.getUser();
        //必须是管理员 或 归口部门管理员
        if (!hasReviewRole(expert.getId(), currentUser.getId())) {
            throw new ServiceException(SP_EXPERT_PROCESS_NO_AUTHORITY);
        }
        //更新项目状态
        ExpertStatusEnum expertStatusEnum = null;
        if ("1".equals(result)) {
            expertStatusEnum = ExpertStatusEnum.AGREE;
        } else if ("2".equals(result)) {
            expertStatusEnum = ExpertStatusEnum.REJECT;
        }
        //更新项目状态
        expert.setStatus(expertStatusEnum.getValue());
        expert.setReviewOpinion(opinion);
        UserUtils.preUpdate(expert);
        expertMapper.updateById(expert);
        //更新过程
        String officeId = "";
        List<SysOffice> offices = userService.findOfficesByUserId(currentUser.getId());
        if(offices.size()>0){
            officeId = offices.get(0).getId();
        }
        ExpertProcess process = new ExpertProcess();
        process.setExpertId(expert.getId());
        process.setUserId(currentUser.getId());
        process.setOfficeId(officeId);

        process.setResult(expertStatusEnum.getValue());
        process.setResultOpinion(opinion);
        UserUtils.preAdd(process);
        expertProcessMapper.insert(process);
    }

    /**
     * 依据状态查询
     * @param status
     */
    public List<ExpertProcess> findByStatus(String status) {
        LambdaQueryWrapper<ExpertProcess> queryWrapper = Wrappers.<ExpertProcess>query().lambda();
        queryWrapper.eq(ExpertProcess::getResult,status)
                .eq(ExpertProcess::getDelFlag,ExpertProcess.DEL_FLAG_NORMAL);
        return expertProcessMapper.selectList(queryWrapper);
    }

    /**
     * 出库
     * @param opinion  意见
     */
    @Transactional
    public void changeOut(Expert expert,String opinion) {
        //验证
        SysUser currentUser = UserUtils.getUser();
        //必须是管理员 或 归口部门管理员
        if (!hasReviewRole(expert.getId(), currentUser.getId())) {
            throw new ServiceException(SP_EXPERT_PROCESS_NO_AUTHORITY);
        }
        //更新项目状态
        ExpertStatusEnum expertStatusEnum = ExpertStatusEnum.OUT;
        //更新项目状态
        expert.setStatus(expertStatusEnum.getValue());
        expert.setReviewOpinion(opinion);
        UserUtils.preUpdate(expert);
        expertMapper.updateById(expert);
        //更新过程
        String officeId = "";
        List<SysOffice> offices = userService.findOfficesByUserId(currentUser.getId());
        if(offices.size()>0){
            officeId = offices.get(0).getId();
        }
        ExpertProcess process = new ExpertProcess();
        process.setExpertId(expert.getId());
        process.setUserId(currentUser.getId());
        process.setOfficeId(officeId);

        process.setResult(expertStatusEnum.getValue());
        process.setResultOpinion(opinion);
        UserUtils.preAdd(process);
        expertProcessMapper.insert(process);
    }

    /**
     * 入库
     * @param opinion  意见
     */
    @Transactional
    public void changeIn(Expert expert,String opinion) {
        //验证
        SysUser currentUser = UserUtils.getUser();
        //必须是管理员 或 归口部门管理员
        if (!hasReviewRole(expert.getId(), currentUser.getId())) {
            throw new ServiceException(SP_EXPERT_PROCESS_NO_AUTHORITY);
        }
        //更新项目状态
        ExpertStatusEnum expertStatusEnum = ExpertStatusEnum.AGREE;
        //更新项目状态
        expert.setStatus(expertStatusEnum.getValue());
        expert.setReviewOpinion(opinion);
        UserUtils.preUpdate(expert);
        expertMapper.updateById(expert);
        //更新过程
        String officeId = "";
        List<SysOffice> offices = userService.findOfficesByUserId(currentUser.getId());
        if(offices.size()>0){
            officeId = offices.get(0).getId();
        }
        ExpertProcess process = new ExpertProcess();
        process.setExpertId(expert.getId());
        process.setUserId(currentUser.getId());
        process.setOfficeId(officeId);

        process.setResult(expertStatusEnum.getValue());
        process.setResultOpinion(opinion);
        UserUtils.preAdd(process);
        expertProcessMapper.insert(process);
    }
    /**
     * 设置星级
     * 只有管理员可以操作
     */
    @Transactional
    public void changeStar(Expert expert,String star) {
        //验证
        SysUser currentUser = UserUtils.getUser();
        //必须是管理员
        if (!isManager(currentUser.getId())) {
            throw new ServiceException(SP_EXPERT_PROCESS_NO_AUTHORITY);
        }
        //更新
        expert.setStar(star);
        UserUtils.preUpdate(expert);
        expertMapper.updateById(expert);
    }
    /**
     * 删除专家
     * 必须是管理员 或 归口部门管理员
     */
    @Transactional
    public void delete(Expert expert) {
        //验证
        SysUser currentUser = UserUtils.getUser();
        //必须是管理员 或 归口部门管理员
        if (!hasReviewRole(expert.getId(), currentUser.getId())) {
            throw new ServiceException(SP_EXPERT_PROCESS_NO_AUTHORITY);
        }
        //更新
        UserUtils.preUpdate(expert);
        expertMapper.updateById(expert);
        expertMapper.deleteById(expert.getId());
    }


    /**
     * 是否允许修改内容
     * 归口部门管理员 和 管理员 可以更改内容
     * 用户只有在未申报和驳回时更改内容
     */
    public boolean allowUpdateContent(String expertId) {
        if(StringUtils.isBlank(expertId)){
            return true;
        }
        SysUser currentUser = UserUtils.getUser();
        if(currentUser!=null&&(isGkbmManager(currentUser.getId())||isManager(currentUser.getId()))){
            return true;
        }
        //不允许更改别人
        Expert expert = expertMapper.selectById(expertId);
        if(currentUser!=null&&(!expert.getMobile().equals(currentUser.getMobile())&&!currentUser.getId().equals(expert.getCreateBy()))){
            return false;
        }
        //验证
        if (ExpertStatusEnum.NOT_APPLY.getValue().equals(expert.getStatus())
                || ExpertStatusEnum.REJECT.getValue().equals(expert.getStatus())) {
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
            return false;
        }
        if(user.isAdmin()){
            return true;
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
     * 是否归口部门管理员
     *
     * @param userId
     * @return
     */
    public boolean isGkbmManager(String userId) {
        SysUser user = userService.get(userId);
        if (user == null) {
            return false;
        }
        List<SysRole> roles = userService.findRolesByUserId(userId);
        boolean isGkbmManager = false;
        for (SysRole role : roles) {
            if (role.getName().equals(GKBM_MANAGER)) {
                isGkbmManager = true;
                break;
            }
        }
        if (isGkbmManager) {
            return true;
        }
        return false;
    }

    /**
     * 是否有审核的权限
     */
    public boolean hasReviewRole(String expertId, String userId) {
        SysUser user = userService.get(userId);
        if (user == null) {
            throw new ServiceException(PERMISSION_USER_NOTEXIST);
        }
        if(user.isAdmin()){
            return true;
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
            if (role.getName().equals(GKBM_MANAGER)) {
                isFirstManager = true;
                break;
            }
        }
        if (!isFirstManager) {//不是归口部门管理员
            return false;
        }
        //查询项目归口部门
        List<SysOffice> userOffices = userService.findOfficesByUserId(userId);
        List<ExpertCategory> expertCategories = expertCategoryService.findByExpertId(expertId);//申请的专家库类别
        boolean hasOffice = false;
        for(ExpertCategory expertCategory:expertCategories){
            boolean has = false;
            BaseCategory baseCategory =baseCategoryService.get(expertCategory.getCategoryId());
            for(SysOffice office:userOffices){
                if(baseCategory!=null&&office.getId().equals(baseCategory.getOfficeId())){
                    has = true;
                    break;
                }
            }
            if(has){
                hasOffice = true;
                break;
            }
        }
        if(hasOffice){
            return true;
        }
        return false;
    }

}

