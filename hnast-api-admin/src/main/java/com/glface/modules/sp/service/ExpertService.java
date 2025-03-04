package com.glface.modules.sp.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.glface.base.bean.Page;
import com.glface.base.utils.DateUtils;
import com.glface.base.utils.StringUtils;
import com.glface.common.exeception.ServiceException;
import com.glface.model.SysOffice;
import com.glface.model.SysUser;
import com.glface.modules.sp.mapper.*;
import com.glface.modules.sp.model.*;
import com.glface.modules.sys.utils.UserUtils;
import com.glface.modules.utils.ExpertStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.*;

import static com.glface.common.web.ApiCode.*;

/**
 * 专家信息
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class ExpertService {

    @Resource
    private ExpertMapper expertMapper;

    @Resource
    private ExpertExtMapper expertExtMapper;

    @Resource
    private ExpertProcessService expertProcessService;
    @Resource
    private ExpertAvoidOrgService expertAvoidOrgService;
    @Resource
    private ExpertCategoryService expertCategoryService;
    @Resource
    private ExpertCategoryMapper expertCategoryMapper;
    @Resource
    private ExpertAvoidOrgMapper expertAvoidOrgMapper;

    @Resource
    private ExpertMajorService expertMajorService;
    @Resource
    private ExpertMajorMapper expertMajorMapper;
    @Resource
    private ExpertTechnicalTitleService technicalTitleService;
    @Resource
    private ExpertTechnicalTitleMapper technicalTitleMapper;
    @Resource
    private ExpertQualificationService qualificationService;
    @Resource
    private ExpertQualificationMapper qualificationMapper;
    @Resource
    private ExpertFileMapper expertFileMapper;
    @Resource
    private ExpertFileService expertFileService;

    public Expert get(String id){
        return expertMapper.selectById(id);
    }

    public List<Expert> findAll(){
        LambdaQueryWrapper<Expert> queryWrapper = Wrappers.<Expert>query().lambda()
                .eq(Expert::getDelFlag,Expert.DEL_FLAG_NORMAL);
        return expertMapper.selectList(queryWrapper);
    }

    public List<String> findOrgNamesByStatus(String status){
        Set<String> orgNameSet = expertMapper.findOrgNamesByStatus(status);
        List<String> orgNameList = new ArrayList<>();
        orgNameList.addAll(orgNameSet);
        //排序
        Collections.sort(orgNameList, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return  o1.compareTo(o2);
            }
        });
        return orgNameList;
    }


    public List<Expert> searchByStatus(String status){
        //最近进行中的申请批次
        LambdaQueryWrapper<Expert> queryWrapper = Wrappers.<Expert>query().lambda()
                .eq(Expert::getStatus, status)
                .eq(Expert::getDelFlag,Expert.DEL_FLAG_NORMAL);
        return expertMapper.selectList(queryWrapper);
    }

    public Set<String> findDels(){
        return expertMapper.findDels();
    }

    /**
     * 缓存内容数据 必须填写联系电话
     * @return
     */
    @Transactional
    public Expert cacheContent(ExpertParam expertParam) {
        //验证
        if(StringUtils.isBlank(expertParam.getMobile())){//联系电话
            throw new ServiceException(SP_EXPERT_MOBILE_REQUIRED);
        }
        if(!expertProcessService.allowUpdateContent(expertParam.getId())){
            throw new ServiceException(SP_EXPERT_SAVE_NOT_ALLOW);
        }
        return saveContentUnCheck(expertParam);
    }

    /**
     * 保存 需要对参数进行验证
     * @return
     */
    @Transactional
    public Expert saveContent(ExpertParam expertParam) {
        if(StringUtils.isBlank(expertParam.getMobile())){//联系电话
            throw new ServiceException(SP_EXPERT_MOBILE_REQUIRED);
        }
        if(StringUtils.isBlank(expertParam.getName())){//姓名
            throw new ServiceException(SP_EXPERT_NAME_REQUIRED);
        }
        if(StringUtils.isBlank(expertParam.getOrgName())){//工作单位
            throw new ServiceException(SP_EXPERT_ORG_NAME_REQUIRED);
        }
        if(expertParam.getCategoryIds()==null||expertParam.getCategoryIds().size()==0){//申请专家库类别
            throw new ServiceException(SP_EXPERT_CATEGORY_REQUIRED);
        }
        if(!expertProcessService.allowUpdateContent(expertParam.getId())){
            throw new ServiceException(SP_EXPERT_SAVE_NOT_ALLOW);
        }
        return saveContentUnCheck(expertParam);
    }

    /**
     * 存储内容 不对参数验证
     * @return
     */
    private Expert saveContentUnCheck(ExpertParam expertParam) {
        String id = expertParam.getId();
        String mobile = expertParam.getMobile();
        String identityCard = expertParam.getIdentityCard();
        if(StringUtils.isNotBlank(mobile)){//联系电话是否已存在
            List<Expert> experts = findByMobile(mobile);
            if(StringUtils.isBlank(id)&&experts.size()>0){
                throw new ServiceException(SP_EXPERT_MOBILE_EXIST);
            }
            for(Expert expert:experts){
                if(StringUtils.isNotBlank(id)&&expert!=null&&!expert.getId().equals(id)){
                    throw new ServiceException(SP_EXPERT_MOBILE_EXIST);
                }
            }
        }

        if(StringUtils.isNotBlank(identityCard)){//身份证是否已存在
            Expert expert = findByIdentityCard(identityCard);
            if(StringUtils.isBlank(id)&&expert!=null){
                throw new ServiceException(SP_EXPERT_IDENTITY_CARD_EXIST);
            }
            if(StringUtils.isNotBlank(id)&&expert!=null&&!expert.getId().equals(id)){
                throw new ServiceException(SP_EXPERT_IDENTITY_CARD_EXIST);
            }
        }

        Expert expert = null;
        if(StringUtils.isNotBlank(id)){
            expert = get(id);
            if(expert==null){
                throw new ServiceException(SP_EXPERT_NOT_EXIST);
            }
        }else{
            expert = new Expert();
            expert.setStatus(ExpertStatusEnum.NOT_APPLY.getValue());
        }

        if(StringUtils.isNotBlank(expertParam.getBirthday())){
            try {
                Date birthday = DateUtils.parseDate(expertParam.getBirthday(),"yyyy-MM-dd");
                expert.setBirthday(birthday);
            } catch (ParseException e) {
                throw new ServiceException(SP_EXPERT_BIRTHDAY_ERROR);
            }
        }else{
            expert.setBirthday(null);
        }

        expert.setName(expertParam.getName());
        expert.setSex(expertParam.getSex());
        expert.setIdentityCard(expertParam.getIdentityCard());
        expert.setParties(expertParam.getParties());
        expert.setEducationId(expertParam.getEducationId());
        expert.setDegreeId(expertParam.getDegreeId());
        expert.setStudied(expertParam.getStudied());
        expert.setMajorCategoryId(expertParam.getMajorCategoryId());
        expert.setOrgName(expertParam.getOrgName());
        expert.setJob(expertParam.getJob());
        expert.setPositionalId(expertParam.getPositionalId());
        expert.setAddress(expertParam.getAddress());
        expert.setPost(expertParam.getPost());
        expert.setMobile(expertParam.getMobile());
        expert.setEmail(expertParam.getEmail());
        expert.setWx(expertParam.getWx());
        expert.setQq(expertParam.getQq());
        expert.setPictureFileId(expertParam.getPictureFileId());
        expert.setSpecialty(expertParam.getSpecialty());

        if(StringUtils.isBlank(id)){
            UserUtils.preAdd(expert);
            expertMapper.insert(expert);
        }else{
            UserUtils.preUpdate(expert);
            expertMapper.updateById(expert);
        }

        ExpertExt expertExt = null;
        if(StringUtils.isNotBlank(id)&&StringUtils.isNotBlank(expert.getExtId())){
            expertExt = expertExtMapper.selectById(expert.getExtId());
        }
        if(expertExt==null){
            expertExt = new ExpertExt();
            expertExt.setExpertId(expert.getId());
        }
        expertExt.setWork(expertParam.getWork());
        expertExt.setAchievement(expertParam.getAchievement());
        expertExt.setPartTime(expertParam.getPartTime());
        expertExt.setFormer(expertParam.getFormer());

        if(StringUtils.isBlank(expertExt.getId())){
            UserUtils.preAdd(expertExt);
            expertExtMapper.insert(expertExt);
            expert.setExtId(expertExt.getId());
            expertMapper.updateById(expert);
        }else{
            UserUtils.preUpdate(expertExt);
            expertExtMapper.updateById(expertExt);
        }

        //需要修改(删除)和新增的
        List<ExpertAvoidOrgParam> avoidOrgs = expertParam.getAvoidOrgs();
        List<ExpertAvoidOrg> dbAvoidOrgs = expertAvoidOrgService.findByExpertId(expert.getId());
        List<ExpertAvoidOrg> updateAvoidOrgs = new ArrayList<>();
        List<ExpertAvoidOrg> delAvoidOrgs = new ArrayList<>();
        List<ExpertAvoidOrg> addAvoidOrgs = new ArrayList<>();

        for(ExpertAvoidOrgParam param:avoidOrgs){
            if(StringUtils.isBlank(param.getId())){//需要新增
                ExpertAvoidOrg avoidOrg = new ExpertAvoidOrg();
                avoidOrg.setExpertId(expert.getId());
                avoidOrg.setOrgName(param.getOrgName());
                avoidOrg.setOrgCode(param.getOrgCode());
                avoidOrg.setRemark(param.getRemark());
                addAvoidOrgs.add(avoidOrg);
            }else{//需要修改
                ExpertAvoidOrg avoidOrg = expertAvoidOrgService.get(param.getId());
                avoidOrg.setOrgName(param.getOrgName());
                avoidOrg.setOrgCode(param.getOrgCode());
                avoidOrg.setRemark(param.getRemark());
                updateAvoidOrgs.add(avoidOrg);
            }
        }
        for(ExpertAvoidOrg dbAvoidOrg:dbAvoidOrgs){//找出需要删除的
            boolean has = false;
            for(ExpertAvoidOrgParam param:avoidOrgs){
                if(dbAvoidOrg.getId().equals(param.getId())){
                    has= true;
                    break;
                }
            }
            if(!has){
                delAvoidOrgs.add(dbAvoidOrg);
            }
        }

        List<String> categoryIds = expertParam.getCategoryIds();//申请专家库类别
        List<ExpertCategory> dbExpertCategories = expertCategoryService.findByExpertId(expert.getId());
        List<ExpertCategory> updateExpertCategories = new ArrayList<>();
        List<ExpertCategory> delExpertCategories = new ArrayList<>();
        List<ExpertCategory> addExpertCategories = new ArrayList<>();

        for(String categoryId:categoryIds){
            boolean has = false;
            for(ExpertCategory dbCategory:dbExpertCategories){
                if(dbCategory.getCategoryId().equals(categoryId)){
                    has = true;
                    break;
                }
            }
            if(!has){
                ExpertCategory category = new ExpertCategory();
                category.setExpertId(expert.getId());
                category.setCategoryId(categoryId);
                addExpertCategories.add(category);
            }
        }
        for(ExpertCategory dbCategory:dbExpertCategories){//找出需要删除的
            boolean has = false;
            for(String categoryId:categoryIds){
                if(dbCategory.getCategoryId().equals(categoryId)){
                    has= true;
                    break;
                }
            }
            if(!has){
                delExpertCategories.add(dbCategory);
            }
        }

        //存储
        for(ExpertAvoidOrg add:addAvoidOrgs){
            UserUtils.preAdd(add);
            expertAvoidOrgMapper.insert(add);
        }
        for(ExpertAvoidOrg update:updateAvoidOrgs){
            UserUtils.preUpdate(update);
            expertAvoidOrgMapper.updateById(update);
        }
        for(ExpertAvoidOrg del:delAvoidOrgs){
            UserUtils.preUpdate(del);
            expertAvoidOrgMapper.updateById(del);
            expertAvoidOrgMapper.deleteById(del.getId());
        }

        for(ExpertCategory add:addExpertCategories){
            UserUtils.preAdd(add);
            expertCategoryMapper.insert(add);
        }
        for(ExpertCategory update:updateExpertCategories){
            UserUtils.preUpdate(update);
            expertCategoryMapper.updateById(update);
        }
        for(ExpertCategory del:delExpertCategories){
            UserUtils.preUpdate(del);
            expertCategoryMapper.updateById(del);
            expertCategoryMapper.deleteById(del.getId());
        }

        return expert;
    }

    /**
     * 保存评标专业 需要对参数进行验证
     * @return
     */
    @Transactional
    public void saveExpertMajor(List<ExpertMajorParam> expertMajorParams) {
        //验证
        String expertId = "";
        for(ExpertMajorParam majorParam:expertMajorParams){
            if(StringUtils.isBlank(majorParam.getExpertId())){
                throw new ServiceException(SP_EXPERT_NOT_EXIST);
            }
            expertId = majorParam.getExpertId();
            if(StringUtils.isBlank(majorParam.getBaseMajorId())){
                throw new ServiceException(SP_EXPERT_BASE_MAJOR_REQUIRED);
            }
            if(StringUtils.isBlank(majorParam.getFileId())){
                throw new ServiceException(SP_EXPERT_MAJOR_FILE_ID_REQUIRED);
            }
        }

        if(!expertProcessService.allowUpdateContent(expertId)){
            throw new ServiceException(SP_EXPERT_SAVE_NOT_ALLOW);
        }

        //需要修改(删除)和新增的
        List<ExpertMajor> dbMajors = expertMajorService.findByExpertId(expertId);
        List<ExpertMajor> updateMajors = new ArrayList<>();
        List<ExpertMajor> delMajors = new ArrayList<>();
        List<ExpertMajor> addMajors = new ArrayList<>();

        for(ExpertMajorParam majorParam:expertMajorParams){
            if(StringUtils.isBlank(majorParam.getId())){//需要新增
                ExpertMajor expertMajor = new ExpertMajor();
                expertMajor.setExpertId(expertId);
                expertMajor.setBaseMajorId(majorParam.getBaseMajorId());
                expertMajor.setSeniorFlag(majorParam.getSeniorFlag());
                expertMajor.setFileId(majorParam.getFileId());
                addMajors.add(expertMajor);
            }else{//需要修改
                ExpertMajor expertMajor = expertMajorService.get(majorParam.getId());
                expertMajor.setBaseMajorId(majorParam.getBaseMajorId());
                expertMajor.setSeniorFlag(majorParam.getSeniorFlag());
                expertMajor.setFileId(majorParam.getFileId());
                updateMajors.add(expertMajor);
            }
        }
        for(ExpertMajor dbMajor:dbMajors){//找出需要删除的
            boolean has = false;
            for(ExpertMajorParam param:expertMajorParams){
                if(dbMajor.getId().equals(param.getId())){
                    has= true;
                    break;
                }
            }
            if(!has){
                delMajors.add(dbMajor);
            }
        }
        for(ExpertMajor add:addMajors){
            UserUtils.preAdd(add);
            expertMajorMapper.insert(add);
        }
        for(ExpertMajor update:updateMajors){
            UserUtils.preUpdate(update);
            expertMajorMapper.updateById(update);
        }
        for(ExpertMajor del:delMajors){
            UserUtils.preUpdate(del);
            expertMajorMapper.updateById(del);
            expertMajorMapper.deleteById(del.getId());
        }
    }

    /**
     * 保存评标专业 需要对参数进行验证
     * @return
     */
    @Transactional
    public void saveTechnicalTitle(List<ExpertTechnicalTitleParam> technicalTitleParams) {
        //验证
        String expertId = "";
        for(ExpertTechnicalTitleParam param:technicalTitleParams){
            if(StringUtils.isBlank(param.getExpertId())){
                throw new ServiceException(SP_EXPERT_NOT_EXIST);
            }
            expertId = param.getExpertId();
            if(StringUtils.isBlank(param.getTitle())){
                throw new ServiceException(SP_EXPERT_TITLE_REQUIRED);
            }
            if(StringUtils.isBlank(param.getStart())){
                throw new ServiceException(SP_EXPERT_TITLE_START_REQUIRED);
            }
            if(StringUtils.isBlank(param.getFileId())){
                throw new ServiceException(SP_EXPERT_TITLE_FILE_ID_REQUIRED);
            }
        }

        if(!expertProcessService.allowUpdateContent(expertId)){
            throw new ServiceException(SP_EXPERT_SAVE_NOT_ALLOW);
        }

        //需要修改(删除)和新增的
        List<ExpertTechnicalTitle> dbs = technicalTitleService.findByExpertId(expertId);
        List<ExpertTechnicalTitle> updates = new ArrayList<>();
        List<ExpertTechnicalTitle> dels = new ArrayList<>();
        List<ExpertTechnicalTitle> adds = new ArrayList<>();

        for(ExpertTechnicalTitleParam param:technicalTitleParams){
            if(StringUtils.isBlank(param.getId())){//需要新增
                ExpertTechnicalTitle technicalTitle = new ExpertTechnicalTitle();
                technicalTitle.setExpertId(expertId);
                technicalTitle.setTitle(param.getTitle());
                try {
                    Date startDate = DateUtils.parseDate(param.getStart(),"yyyy-MM-dd");
                    technicalTitle.setStart(startDate);
                } catch (ParseException e) {
                    throw new ServiceException(SP_EXPERT_TITLE_START_ERROR);
                }
                technicalTitle.setFileId(param.getFileId());
                adds.add(technicalTitle);
            }else{//需要修改
                ExpertTechnicalTitle technicalTitle = technicalTitleService.get(param.getId());
                technicalTitle.setTitle(param.getTitle());
                try {
                    Date startDate = DateUtils.parseDate(param.getStart(),"yyyy-MM-dd");
                    technicalTitle.setStart(startDate);
                } catch (ParseException e) {
                    throw new ServiceException(SP_EXPERT_TITLE_START_ERROR);
                }
                technicalTitle.setFileId(param.getFileId());
                updates.add(technicalTitle);
            }
        }
        for(ExpertTechnicalTitle dbTitle:dbs){//找出需要删除的
            boolean has = false;
            for(ExpertTechnicalTitleParam param:technicalTitleParams){
                if(dbTitle.getId().equals(param.getId())){
                    has= true;
                    break;
                }
            }
            if(!has){
                dels.add(dbTitle);
            }
        }
        for(ExpertTechnicalTitle add:adds){
            UserUtils.preAdd(add);
            technicalTitleMapper.insert(add);
        }
        for(ExpertTechnicalTitle update:updates){
            UserUtils.preUpdate(update);
            technicalTitleMapper.updateById(update);
        }
        for(ExpertTechnicalTitle del:dels){
            UserUtils.preUpdate(del);
            technicalTitleMapper.updateById(del);
            technicalTitleMapper.deleteById(del.getId());
        }
    }

    /**
     * 专家职业资格 需要对参数进行验证
     * @return
     */
    @Transactional
    public void saveQualification(List<ExpertQualificationParam> qualificationParams) {
        //验证
        String expertId = "";
        for(ExpertQualificationParam param:qualificationParams){
            if(StringUtils.isBlank(param.getExpertId())){
                throw new ServiceException(SP_EXPERT_NOT_EXIST);
            }
            expertId = param.getExpertId();
            if(StringUtils.isBlank(param.getTitle())){
                throw new ServiceException(SP_EXPERT_QUALIFICATION_REQUIRED);
            }
            if(StringUtils.isBlank(param.getStart())){
                throw new ServiceException(SP_EXPERT_QUALIFICATION_START_REQUIRED);
            }
            if(StringUtils.isBlank(param.getEnd())){
                throw new ServiceException(SP_EXPERT_QUALIFICATION_END_REQUIRED);
            }
            if(StringUtils.isBlank(param.getFileId())){
                throw new ServiceException(SP_EXPERT_TITLE_FILE_ID_REQUIRED);
            }
        }

        if(!expertProcessService.allowUpdateContent(expertId)){
            throw new ServiceException(SP_EXPERT_SAVE_NOT_ALLOW);
        }

        //需要修改(删除)和新增的
        List<ExpertQualification> dbs = qualificationService.findByExpertId(expertId);
        List<ExpertQualification> updates = new ArrayList<>();
        List<ExpertQualification> dels = new ArrayList<>();
        List<ExpertQualification> adds = new ArrayList<>();

        for(ExpertQualificationParam param:qualificationParams){
            if(StringUtils.isBlank(param.getId())){//需要新增
                ExpertQualification qualification = new ExpertQualification();
                qualification.setExpertId(expertId);
                qualification.setTitle(param.getTitle());
                qualification.setNumber(param.getNumber());
                qualification.setOrgName(param.getOrgName());
                try {
                    Date startDate = DateUtils.parseDate(param.getStart(),"yyyy-MM-dd");
                    qualification.setStart(startDate);
                } catch (ParseException e) {
                    throw new ServiceException(SP_EXPERT_QUALIFICATION_START_ERROR);
                }
                try {
                    Date endDate = DateUtils.parseDate(param.getEnd(),"yyyy-MM-dd");
                    qualification.setEnd(endDate);
                } catch (ParseException e) {
                    throw new ServiceException(SP_EXPERT_QUALIFICATION_END_ERROR);
                }
                qualification.setFileId(param.getFileId());
                adds.add(qualification);
            }else{//需要修改
                ExpertQualification qualification = qualificationService.get(param.getId());
                qualification.setTitle(param.getTitle());
                qualification.setNumber(param.getNumber());
                qualification.setOrgName(param.getOrgName());
                try {
                    Date startDate = DateUtils.parseDate(param.getStart(),"yyyy-MM-dd");
                    qualification.setStart(startDate);
                } catch (ParseException e) {
                    throw new ServiceException(SP_EXPERT_QUALIFICATION_START_ERROR);
                }
                try {
                    Date endDate = DateUtils.parseDate(param.getEnd(),"yyyy-MM-dd");
                    qualification.setEnd(endDate);
                } catch (ParseException e) {
                    throw new ServiceException(SP_EXPERT_QUALIFICATION_END_ERROR);
                }
                qualification.setFileId(param.getFileId());
                updates.add(qualification);
            }
        }
        for(ExpertQualification dbQualification:dbs){//找出需要删除的
            boolean has = false;
            for(ExpertQualificationParam param:qualificationParams){
                if(dbQualification.getId().equals(param.getId())){
                    has= true;
                    break;
                }
            }
            if(!has){
                dels.add(dbQualification);
            }
        }
        for(ExpertQualification add:adds){
            UserUtils.preAdd(add);
            qualificationMapper.insert(add);
        }
        for(ExpertQualification update:updates){
            UserUtils.preUpdate(update);
            qualificationMapper.updateById(update);
        }
        for(ExpertQualification del:dels){
            UserUtils.preUpdate(del);
            qualificationMapper.updateById(del);
            qualificationMapper.deleteById(del.getId());
        }
    }


    /**
     * 保存附件  不做验证
     *
     * @param id                专家id
     * @param educationFileId   学历、学位材料
     * @param identityFileId    身份证
     * @param orgFileId         所在单位意见
     * @param commitmentFileId  承诺书
     * @param academicianFileId 两院院士电子件
     * @param secrecyFileId     保密协议
     * @param reviewFileId      评审纪律协议
     * @return
     */
    @Transactional
    public void saveFile(String id,
                         String educationFileId,
                         String identityFileId,
                         String orgFileId,
                         String commitmentFileId,
                         String academicianFileId,
                         String secrecyFileId, String reviewFileId) {
        //验证
        String expertId = id;
        if(StringUtils.isBlank(expertId)){
            throw new ServiceException(SP_EXPERT_NOT_EXIST);
        }

        Expert expert = get(expertId);
        if(expert==null){
            throw new ServiceException(SP_EXPERT_NOT_EXIST);
        }

        ExpertFile expertFile = null;
        if(StringUtils.isNotBlank(id)&&StringUtils.isNotBlank(expert.getExpertFileId())){
            expertFile = expertFileMapper.selectById(expert.getExpertFileId());
        }
        if(expertFile==null){
            expertFile = new ExpertFile();
            expertFile.setExpertId(expert.getId());
        }
        if(!StringUtils.isBlank(educationFileId)){
            expertFile.setEducationFileId(educationFileId);
        }
        if(!StringUtils.isBlank(identityFileId)){
            expertFile.setIdentityFileId(identityFileId);
        }
        if(!StringUtils.isBlank(orgFileId)){
            expertFile.setOrgFileId(orgFileId);
        }
        if(!StringUtils.isBlank(commitmentFileId)){
            expertFile.setCommitmentFileId(commitmentFileId);
        }
        if(!StringUtils.isBlank(academicianFileId)){
            expertFile.setAcademicianFileId(academicianFileId);
        }
        if(!StringUtils.isBlank(secrecyFileId)){
            expertFile.setSecrecyFileId(secrecyFileId);
        }
        if(!StringUtils.isBlank(reviewFileId)){
            expertFile.setReviewFileId(reviewFileId);
        }
        if(StringUtils.isBlank(expertFile.getId())){
            UserUtils.preAdd(expertFile);
            expertFileMapper.insert(expertFile);
            expert.setExpertFileId(expertFile.getId());
            expertMapper.updateById(expert);
        }else{
            UserUtils.preUpdate(expertFile);
            expertFileMapper.updateById(expertFile);
        }
    }

    /**
     * 删除文件
     * @param id
     * @param fileIdName 文件id名称
     */
    @Transactional
    public void deleteFile(String id,
                         String fileIdName) {
        //验证
        String expertId = id;
        if(StringUtils.isBlank(expertId)){
            throw new ServiceException(SP_EXPERT_NOT_EXIST);
        }

        Expert expert = get(expertId);
        if(expert==null){
            throw new ServiceException(SP_EXPERT_NOT_EXIST);
        }

        ExpertFile expertFile = null;
        if(StringUtils.isNotBlank(id)&&StringUtils.isNotBlank(expert.getExpertFileId())){
            expertFile = expertFileMapper.selectById(expert.getExpertFileId());
        }
        if(expertFile==null){
            expertFile = new ExpertFile();
            expertFile.setExpertId(expert.getId());
        }
        switch (fileIdName){
            case "educationFileId":
                expertFile.setEducationFileId(null);
                break;
            case "identityFileId":
                expertFile.setIdentityFileId(null);
                break;
            case "orgFileId":
                expertFile.setOrgFileId(null);
                break;
            case "commitmentFileId":
                expertFile.setCommitmentFileId(null);
                break;
            case "academicianFileId":
                expertFile.setAcademicianFileId(null);
                break;
            case "secrecyFileId":
                expertFile.setSecrecyFileId(null);
                break;
            case "reviewFileId":
                expertFile.setReviewFileId(null);
                break;
            default:
                throw new ServiceException(FILE_NOT_EXIST);
        }
        if(StringUtils.isBlank(expertFile.getId())){
            UserUtils.preAdd(expertFile);
            expertFileMapper.insert(expertFile);
            expert.setExpertFileId(expertFile.getId());
            expertMapper.updateById(expert);
        }else{
            UserUtils.preUpdate(expertFile);
            expertFileMapper.updateById(expertFile);
        }
    }

    /**
     * 提交申报
     * @param id
     */
    @Transactional
    public synchronized void submit(String id){
        if (StringUtils.isBlank(id)) {
            throw new ServiceException(SP_EXPERT_NOT_EXIST);
        }
        Expert expert = get(id);
        if(expert==null){
            throw new ServiceException(SP_EXPERT_NOT_EXIST);
        }

        //基础信息验证
        if (StringUtils.isBlank(expert.getName())) {
            throw new ServiceException(SP_EXPERT_NAME_REQUIRED);
        }
        if (StringUtils.isBlank(expert.getOrgName())) {
            throw new ServiceException(SP_EXPERT_ORG_NAME_REQUIRED);
        }
        if (StringUtils.isBlank(expert.getMobile())) {
            throw new ServiceException(SP_EXPERT_MOBILE_REQUIRED);
        }
        List<ExpertCategory> expertCategoryList = expertCategoryService.findByExpertId(expert.getId());
        if(expertCategoryList==null||expertCategoryList.size()==0){//专家类别
            throw new ServiceException(SP_EXPERT_CATEGORY_REQUIRED);
        }
        //附件验证
//        String expertFileId = expert.getExpertFileId();
//        if (StringUtils.isBlank(expertFileId)) {
//            throw new ServiceException(SP_EXPERT_FILE_REQUIRED);
//        }
//        ExpertFile expertFile = expertFileService.get(expertFileId);
//        if(expertFile==null){
//            throw new ServiceException(SP_EXPERT_FILE_REQUIRED);
//        }
//
//        if (StringUtils.isBlank(expertFile.getIdentityFileId())) {
//            throw new ServiceException(SP_EXPERT_FILE_IDENTITY_REQUIRED);
//        }
//
//        if (StringUtils.isBlank(expertFile.getSecrecyFileId())) {
//            throw new ServiceException(SP_EXPERT_FILE_SECRECY_REQUIRED);
//        }
//        if (StringUtils.isBlank(expertFile.getReviewFileId())) {
//            throw new ServiceException(SP_EXPERT_FILE_REVIEW_REQUIRED);
//        }
        Date now = new Date();
        if(expert.getApplyDate()==null){
            expert.setApplyDate(now);
        }
        //设置录入方式
        if(StringUtils.isBlank(expert.getEntryMethod())){
            SysUser currentUser = UserUtils.getUser();
            if(currentUser!=null&&(expertProcessService.isManager(currentUser.getId())||expertProcessService.isGkbmManager(currentUser.getId()))){
                expert.setEntryMethod("back");
            }else{
                expert.setEntryMethod("user");
            }
        }

        //更新
        UserUtils.preUpdate(expert);
        expertMapper.updateById(expert);
        //更新状态和过程
        expertProcessService.startSubmit(expert);
    }

    public List<Expert> findByMobile(String mobile){
        LambdaQueryWrapper<Expert> queryWrapper = Wrappers.<Expert>query().lambda()
                .eq(Expert::getMobile, mobile)
                .eq(Expert::getDelFlag,Expert.DEL_FLAG_NORMAL);
        return expertMapper.selectList(queryWrapper);
    }
    public List<Expert> findByCreateBy(String createBy){
        LambdaQueryWrapper<Expert> queryWrapper = Wrappers.<Expert>query().lambda()
                .eq(Expert::getCreateBy, createBy)
                .eq(Expert::getDelFlag,Expert.DEL_FLAG_NORMAL);
        return expertMapper.selectList(queryWrapper);
    }

    public Expert findByIdentityCard(String identityCard){
        LambdaQueryWrapper<Expert> queryWrapper = Wrappers.<Expert>query().lambda()
                .eq(Expert::getIdentityCard, identityCard)
                .eq(Expert::getDelFlag,Expert.DEL_FLAG_NORMAL);
        return expertMapper.selectOne(queryWrapper);
    }

    /**
     * 删除专家
     * 必须是管理员 或 归口部门管理员
     */
    @Transactional
    public void delete(Expert expert) {
        //更新
        UserUtils.preUpdate(expert);
        expertMapper.updateById(expert);
        expertMapper.deleteById(expert.getId());
    }
}
