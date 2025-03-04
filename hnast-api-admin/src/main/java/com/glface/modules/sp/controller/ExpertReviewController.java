package com.glface.modules.sp.controller;

import com.glface.base.bean.DynamicBean;
import com.glface.base.bean.R;
import com.glface.base.utils.DateUtils;
import com.glface.base.utils.StringUtils;
import com.glface.common.exeception.ServiceException;
import com.glface.log.LoggerMonitor;
import com.glface.model.SysOffice;
import com.glface.model.SysUser;
import com.glface.modules.model.*;
import com.glface.modules.service.FileService;
import com.glface.modules.service.UserService;
import com.glface.modules.sp.model.*;
import com.glface.modules.sp.service.*;
import com.glface.modules.sys.utils.UserUtils;
import com.glface.modules.utils.ExpertStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;

import static com.glface.common.web.ApiCode.SP_EXPERT_NOT_EXIST;

/**
 * 评审
 */
@Slf4j
@RestController
@RequestMapping("/specialist/review")
public class ExpertReviewController {

    @Resource
    private ExpertService expertService;

    @Resource
    private ExpertProcessService expertProcessService;

    @Resource
    private ExpertCategoryService expertCategoryService;

    @Resource
    private BaseCategoryService baseCategoryService;
    @Resource
    private ExpertExtService expertExtService;
    @Resource
    private ExpertAvoidOrgService avoidOrgService;
    @Resource
    private ExpertMajorService expertMajorService;
    @Resource
    private ExpertTechnicalTitleService technicalTitleService;
    @Resource
    private ExpertQualificationService qualificationService;
    @Resource
    private ExpertFileService expertFileService;
    @Resource
    private BaseMajorService baseMajorService;
    @Resource
    private BaseMajorCategoryService baseMajorCategoryService;
    @Resource
    private SampleService sampleService;
    @Resource
    private SampleExpertService sampleExpertService;

    @Resource
    private UserService userService;
    @Resource
    private FileService fileService;
    @Resource
    private BaseEducationService baseService;
    @Resource
    private BasePositionalService basePositionalService;
    /**
     * 通过id查询专家信息
     */
    @RequestMapping(value = "/getExpert")
    public R<Object> getExpert(String id) {
        if (StringUtils.isBlank(id)) {
            throw new ServiceException(SP_EXPERT_NOT_EXIST);
        }
        Expert expert = expertService.get(id);
        if (expert == null) {
            throw new ServiceException(SP_EXPERT_NOT_EXIST);
        }

        //查询内容
        ExpertExt expertExt = new ExpertExt();
        if (StringUtils.isNotBlank(expert.getExtId())) {
            expertExt = expertExtService.get(expert.getExtId());
        }
        //回避单位信息
        List<ExpertAvoidOrg> avoidOrgs = avoidOrgService.findByExpertId(id);
        //评标专业信息
        List<ExpertMajor> expertMajors = expertMajorService.findByExpertId(id);
        //技术职称信息
        List<ExpertTechnicalTitle> technicalTitles = technicalTitleService.findByExpertId(id);
        //职业资格
        List<ExpertQualification> qualifications = qualificationService.findByExpertId(id);
        //附件
        ExpertFile expertFile = new ExpertFile();
        if (StringUtils.isNotBlank(expert.getExpertFileId())) {
            expertFile = expertFileService.get(expert.getExpertFileId());
        }

        //返回数据
        Object expertExtObject = new DynamicBean.Builder().setPV("id", expertExt.getId())
                .setPV("work", expertExt.getWork())
                .setPV("achievement", expertExt.getAchievement())
                .setPV("partTime", expertExt.getPartTime())
                .setPV("former", expertExt.getFormer())
                .build().getObject();
        List<Object> avoidOrgList = new ArrayList<>();//回避单位信息
        for (ExpertAvoidOrg s : avoidOrgs) {
            Object bean = new DynamicBean.Builder().setPV("id", s.getId())
                    .setPV("orgName", s.getOrgName())
                    .setPV("orgCode", s.getOrgCode())
                    .setPV("remark", s.getRemark())
                    .build().getObject();
            avoidOrgList.add(bean);
        }
        //评标专业信息
        List<Object> expertMajorList = new ArrayList<>();
        for (ExpertMajor expertMajor : expertMajors) {
            BaseMajor baseMajor = baseMajorService.get(expertMajor.getBaseMajorId());
            baseMajor = baseMajor==null?new BaseMajor():baseMajor;
            BaseMajorCategory baseMajorCategory = baseMajorService.getByMajorId(baseMajor.getId());
            baseMajorCategory = baseMajorCategory ==null?new BaseMajorCategory():baseMajorCategory;

            Object bean = new DynamicBean.Builder().setPV("id", expertMajor.getId())
                    .setPV("expertId", expertMajor.getExpertId())
                    .setPV("majorName", baseMajor.getName())
                    .setPV("majorCategoryName", baseMajorCategory.getName())
                    .setPV("seniorFlag", expertMajor.getSeniorFlag())
                    .setPV("fileId", expertMajor.getFileId())
                    .setPV("fileName", fileService.getNameById(expertMajor.getFileId()))
                    .build().getObject();
            expertMajorList.add(bean);
        }
        //技术职称信息
        List<Object> technicalTitleList = new ArrayList<>();
        for (ExpertTechnicalTitle technicalTitle : technicalTitles) {
            Object bean = new DynamicBean.Builder().setPV("id", technicalTitle.getId())
                    .setPV("expertId", technicalTitle.getExpertId())
                    .setPV("title", technicalTitle.getTitle())
                    .setPV("start", DateUtils.formatDate(technicalTitle.getStart(), "yyyy-MM-dd"))
                    .setPV("end", DateUtils.formatDate(technicalTitle.getEnd(), "yyyy-MM-dd"))
                    .setPV("fileId", technicalTitle.getFileId())
                    .setPV("fileName", fileService.getNameById(technicalTitle.getFileId()))
                    .build().getObject();
            technicalTitleList.add(bean);
        }
        //职业资格
        List<Object> qualificationList = new ArrayList<>();
        for (ExpertQualification qualification : qualifications) {
            Object bean = new DynamicBean.Builder().setPV("id", qualification.getId())
                    .setPV("expertId", qualification.getExpertId())
                    .setPV("title", qualification.getTitle())
                    .setPV("orgName", qualification.getOrgName())
                    .setPV("number", qualification.getNumber())
                    .setPV("start", DateUtils.formatDate(qualification.getStart(), "yyyy-MM-dd"))
                    .setPV("end", DateUtils.formatDate(qualification.getEnd(), "yyyy-MM-dd"))
                    .setPV("fileId", qualification.getFileId())
                    .setPV("fileName", fileService.getNameById(qualification.getFileId()))
                    .build().getObject();
            qualificationList.add(bean);
        }
        //附件
        Object fileObject = new DynamicBean.Builder().setPV("id", expertFile.getId())
                .setPV("expertId", expertFile.getExpertId())
                .setPV("educationFileId", expertFile.getEducationFileId())
                .setPV("identityFileId", expertFile.getIdentityFileId())
                .setPV("orgFileId", expertFile.getOrgFileId())
                .setPV("commitmentFileId", expertFile.getCommitmentFileId())
                .setPV("academicianFileId", expertFile.getAcademicianFileId())
                .setPV("secrecyFileId", expertFile.getSecrecyFileId())
                .setPV("reviewFileId", expertFile.getReviewFileId())
                .build().getObject();

        Object expertBean = new DynamicBean.Builder().setPV("id", expert.getId())
                .setPV("name", expert.getName())
                .setPV("sex", expert.getSex())
                .setPV("birthday", DateUtils.formatDate(expert.getBirthday(), "yyyy-MM-dd"))
                .setPV("identityCard", expert.getIdentityCard())
                .setPV("parties", expert.getParties())
                .setPV("educationId", expert.getEducationId())
                .setPV("degreeId", expert.getDegreeId())
                .setPV("studied", expert.getStudied())
                .setPV("specialty", expert.getSpecialty())
                .setPV("majorCategoryId", expert.getMajorCategoryId())
                .setPV("orgName", expert.getOrgName())
                .setPV("job", expert.getJob())
                .setPV("positionalId", expert.getPositionalId())
                .setPV("address", expert.getAddress())
                .setPV("post", expert.getPost())
                .setPV("mobile", expert.getMobile())
                .setPV("email", expert.getEmail())
                .setPV("wx", expert.getWx())
                .setPV("qq", expert.getQq())
                .setPV("pictureFileId", expert.getPictureFileId())

                .setPV("avoidOrgs", avoidOrgList, List.class)
                .setPV("expertExt", expertExtObject)
                .setPV("expertMajors", expertMajorList,List.class)
                .setPV("technicalTitles", technicalTitleList,List.class)
                .setPV("qualifications", qualificationList,List.class)
                .setPV("expertFile", fileObject)

                .build().getObject();
        return R.ok(expertBean);
    }

    /**
     * 待审核专家
     * 管理员可以查询所有
     * 归口部门管理员可以查询到归口部门所有待评审专家
     * @param name 名称
     * @param mobile 联系方式
     */
    @PreAuthorize("hasAuthority('specialist:review:review')")
    @RequestMapping(value = "/searchWaitReview")
    public R<Object> searchWaitReview(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startTime,
                                      @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endTime,
                                      String name, String mobile,
                                      @RequestParam(value = "page", defaultValue = "1") int pageNo,
                                      @RequestParam(value = "limit", defaultValue = "10") int limit) {
        if(endTime==null){
            endTime = new Date();
        }
        //所有专家库类别
        List<BaseCategory> baseCategories = baseCategoryService.all();
        Map<String,BaseCategory> baseCategoryMap = new HashMap<>();
        for(BaseCategory baseCategory:baseCategories){
            baseCategoryMap.put(baseCategory.getId(),baseCategory);
        }

        //取所有处于已提交状态的专家
        List<Expert> expertList = expertService.searchByStatus(ExpertStatusEnum.SUBMITTED.getValue());
        //依据搜索条件过滤
        List<Expert> filterList = expertList;
        if(StringUtils.isNotBlank(name)){
            List<Expert> tmpList = new ArrayList<>();
            for(Expert expert:filterList){
                if(expert.getName().contains(name)){
                    tmpList.add(expert);
                }
            }
            filterList = tmpList;
        }
        if(StringUtils.isNotBlank(mobile)){
            List<Expert> tmpList = new ArrayList<>();
            for(Expert expert:filterList){
                if(expert.getMobile().contains(mobile)){
                    tmpList.add(expert);
                }
            }
            filterList = tmpList;
        }
        if(endTime!=null){
            List<Expert> tmpList = new ArrayList<>();
            for(Expert expert:filterList){
                if(endTime.after(expert.getApplyDate())){
                    if(startTime!=null&&startTime.before(expert.getApplyDate())){
                        tmpList.add(expert);
                    }else if(startTime==null){
                        tmpList.add(expert);
                    }
                }
            }
            filterList = tmpList;
        }

        //依据用户角色过滤
        SysUser currentUser = UserUtils.getUser();
        List<Expert> experts = new ArrayList<>();
        boolean isDistrictManager = false;
        if(expertProcessService.isManager(currentUser.getId())){//管理员可以查看所有
            experts = filterList;
        }else if(expertProcessService.isGkbmManager(currentUser.getId())){//是归口部门管理员
            List<SysOffice> userOffices = userService.findOfficesByUserId(currentUser.getId());
            for(Expert expert:filterList){
                List<ExpertCategory> expertCategories = expertCategoryService.findByExpertId(expert.getId());//申请的专家库类别
                for(ExpertCategory expertCategory:expertCategories){
                    boolean has = false;
                    BaseCategory baseCategory = baseCategoryMap.get(expertCategory.getCategoryId());
                    for(SysOffice office:userOffices){
                        if(baseCategory!=null&&office.getId().equals(baseCategory.getOfficeId())){
                            has = true;
                            break;
                        }
                    }
                    if(has){
                        experts.add(expert);
                        break;
                    }
                }
            }
        }

        //排序
        Collections.sort(experts, new Comparator<Expert>() {
            @Override
            public int compare(Expert o1, Expert o2) {
                if(o1.getApplyDate().after(o2.getApplyDate())){
                    return 1;
                }else if(o1.getApplyDate().before(o2.getApplyDate())){
                    return -1;
                }
                return  0;
            }
        });

        //内存分页
        List<Expert> pageList = new ArrayList<>();
        int start = (pageNo-1)*limit;
        int end = start + limit-1;
        for(int i=start;i>=0&&i<=end&&i<experts.size();i++){
            pageList.add(experts.get(i));
        }


        // 构造返回数据
        List<Object> results = new ArrayList<>();
        for (Expert expert : pageList) {
            //专家库类别
            List<ExpertCategory> expertCategories = expertCategoryService.findByExpertId(expert.getId());
            //返回数据
            List<Object> expertCategoryList = new ArrayList<>();
            for (ExpertCategory s : expertCategories) {
                BaseCategory baseCategory = baseCategoryService.get(s.getCategoryId());
                String categoryName = baseCategory!=null?baseCategory.getName():"";
                Object bean = new DynamicBean.Builder()
                        .setPV("categoryId", s.getCategoryId())
                        .setPV("categoryName", categoryName)
                        .build().getObject();
                expertCategoryList.add(bean);
            }
            BaseEducation education = baseService.get(expert.getEducationId());
            BasePositional positional = basePositionalService.get(expert.getPositionalId());
            Object o = new DynamicBean.Builder()
                    .setPV("id", expert.getId())
                    .setPV("applyDate", DateUtils.formatDate(expert.getApplyDate(), "yyyy-MM-dd HH:mm:ss"))
                    .setPV("name",  expert.getName())
                    .setPV("orgName", expert.getOrgName())
                    .setPV("studied", expert.getStudied())
                    .setPV("positionalId", expert.getPositionalId())
                    .setPV("positionalName", positional!=null?positional.getName():null)
                    .setPV("educationId", expert.getEducationId())
                    .setPV("educationName",education!=null?education.getName():null)
                    .setPV("expertCategories", expertCategoryList, List.class)
                    .setPV("mobile", expert.getMobile()).build().getObject();
            results.add(o);
        }

        Object data = new DynamicBean.Builder()
                .setPV("total", experts.size())
                .setPV("list", results, List.class)
                .build().getObject();
        return R.ok(data);
    }

    /**
     * 审核
     * @param id
     * @param result 评审结果  1:通过  2：未通过
     * @param opinion 评审意见
     */
    @LoggerMonitor(value = "【专家库】专家审核")
    @PreAuthorize("hasAuthority('specialist:review:review')")
    @RequestMapping(value = "/review")
    public R<Object> review(String id,String result,String opinion){
        Expert expert = expertService.get(id);
        expertProcessService.review(expert,result,opinion);
        return R.ok();
    }

    /**
     * 查询审核记录
     * 管理员可以查询所有
     * 归口部门管理员可以查询到归口部门所有评审记录
     * @param startTime   申报开始时间
     * @param endTime     申报结束时间
     * @param name 名称
     * @param mobile 联系方式
     */
    @PreAuthorize("hasAuthority('specialist:review:review')")
    @RequestMapping(value = "/searchReviewHis")
    public R<Object> searchReviewHis(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startTime,
                                      @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endTime,
                                      String name, String mobile,
                                      @RequestParam(value = "page", defaultValue = "1") int pageNo,
                                      @RequestParam(value = "limit", defaultValue = "10") int limit) {
        if(endTime==null){
            endTime = new Date();
        }
        //所有专家库类别
        List<BaseCategory> baseCategories = baseCategoryService.all();
        Map<String,BaseCategory> baseCategoryMap = new HashMap<>();
        for(BaseCategory baseCategory:baseCategories){
            baseCategoryMap.put(baseCategory.getId(),baseCategory);
        }
        //找出所有被删除的
        Set<String> dels = expertService.findDels();
        //取所有审核记录
        List<ExpertProcess> processes = expertProcessService.findByStatus(ExpertStatusEnum.AGREE.getValue());
        processes.addAll(expertProcessService.findByStatus(ExpertStatusEnum.REJECT.getValue()));
        List<ExpertProcess> processesTmp = new ArrayList<>();
        for(ExpertProcess process:processes){
            boolean isDel = false;
            if(dels.contains(process.getExpertId())){
                isDel = true;
            }
            if(!isDel){
                processesTmp.add(process);
            }
        }
        processes = processesTmp;
        //取满足搜索条件的专家
        List<Expert> expertList = expertService.findAll();
        //依据搜索条件过滤
        List<Expert> filterList = expertList;
        if(StringUtils.isNotBlank(name)){
            List<Expert> tmpList = new ArrayList<>();
            for(Expert expert:filterList){
                if(expert.getName().contains(name)){
                    tmpList.add(expert);
                }
            }
            filterList = tmpList;
        }
        if(StringUtils.isNotBlank(mobile)){
            List<Expert> tmpList = new ArrayList<>();
            for(Expert expert:filterList){
                if(expert.getMobile().contains(mobile)){
                    tmpList.add(expert);
                }
            }
            filterList = tmpList;
        }
        if(endTime!=null){
            List<Expert> tmpList = new ArrayList<>();
            for(Expert expert:filterList){
                if(endTime.after(expert.getApplyDate())){
                    if(startTime!=null&&startTime.before(expert.getApplyDate())){
                        tmpList.add(expert);
                    }else if(startTime==null){
                        tmpList.add(expert);
                    }
                }
            }
            filterList = tmpList;
        }

        List<ExpertProcess> processList = new ArrayList<>();
        for(ExpertProcess process:processes){
            boolean has = false;
            for(Expert expert:filterList){
                if(expert.getId().equals(process.getExpertId())){
                    has = true;
                    break;
                }
            }
            if(has){
                processList.add(process);
            }
        }

        //依据用户角色过滤
        SysUser currentUser = UserUtils.getUser();
        List<ExpertProcess> ableProcessList = new ArrayList<>();
        if(expertProcessService.isManager(currentUser.getId())){//管理员可以查看所有
            ableProcessList = processList;
        }else if(expertProcessService.isGkbmManager(currentUser.getId())){//是归口部门管理员
            List<SysOffice> userOffices = userService.findOfficesByUserId(currentUser.getId());
            for(ExpertProcess process:processList){
                Expert expert = expertService.get(process.getExpertId());
                List<ExpertCategory> expertCategories = expertCategoryService.findByExpertId(expert.getId());//申请的专家库类别
                for(ExpertCategory expertCategory:expertCategories){
                    boolean has = false;
                    BaseCategory baseCategory = baseCategoryMap.get(expertCategory.getCategoryId());
                    for(SysOffice office:userOffices){
                        if(baseCategory!=null&&office.getId().equals(baseCategory.getOfficeId())){
                            has = true;
                            break;
                        }
                    }
                    if(has){
                        ableProcessList.add(process);
                        break;
                    }
                }
            }
        }

        //排序
        Collections.sort(ableProcessList, new Comparator<ExpertProcess>() {
            @Override
            public int compare(ExpertProcess o1, ExpertProcess o2) {
                if(o1.getCreateDate().after(o2.getCreateDate())){
                    return -1;
                }else if(o1.getCreateDate().before(o2.getCreateDate())){
                    return 1;
                }
                return  0;
            }
        });

        //内存分页
        List<ExpertProcess> pageList = new ArrayList<>();
        int start = (pageNo-1)*limit;
        int end = start + limit-1;
        for(int i=start;i>=0&&i<=end&&i<ableProcessList.size();i++){
            pageList.add(ableProcessList.get(i));
        }

        // 构造返回数据
        List<Object> expertResults = new ArrayList<>();
        for (ExpertProcess process : pageList) {
            Expert expert = expertService.get(process.getExpertId());
            if(expert==null){
                log.info("expert不存在,process.getExpertId()=={}",process.getExpertId());
                continue;
            }

            SysUser creater = userService.get(process.getCreateBy());
            String userName = "";
            if(creater!=null){
                userName = creater.getNickname();
                if(StringUtils.isBlank(userName)){
                    userName = creater.getAccount();
                }
            }

            ExpertStatusEnum statusEnum = ExpertStatusEnum.getProjectStatusEnumByValue(process.getResult());
            String approveResult = "";
            String approveOpinion = process.getResultOpinion();
            if(statusEnum!=null){
                approveResult = statusEnum.getShortLabel();
            }

            Object o = new DynamicBean.Builder()
                    .setPV("id", process.getId())
                    .setPV("applyDate", DateUtils.formatDate(expert.getApplyDate(), "yyyy-MM-dd HH:mm:ss"))
                    .setPV("name",  expert.getName())
                    .setPV("sex", expert.getSex())
                    .setPV("birthday", DateUtils.formatDate(expert.getBirthday(), "yyyy-MM-dd"))
                    .setPV("identityCard", expert.getIdentityCard())
                    .setPV("parties", expert.getParties())
                    .setPV("educationId", expert.getEducationId())
                    .setPV("degreeId", expert.getDegreeId())
                    .setPV("majorCategoryId", expert.getMajorCategoryId())
                    .setPV("positionalId", expert.getPositionalId())
                    .setPV("orgName", expert.getOrgName())
                    .setPV("studied", expert.getStudied())
                    .setPV("mobile", expert.getMobile())
                    .setPV("approveResult", approveResult)
                    .setPV("approveOpinion", approveOpinion)
                    .setPV("createDate", DateUtils.formatDate(process.getCreateDate(), "yyyy-MM-dd HH:mm:ss")).build().getObject();
            expertResults.add(o);
        }

        Object data = new DynamicBean.Builder()
                .setPV("total", ableProcessList.size())
                .setPV("processes", expertResults, List.class)
                .build().getObject();
        return R.ok(data);
    }

    /**
     * 专家信息(入库 出库专家)  注意星级只有管理员可以看到
     * 管理员可以查询所有
     * 归口部门管理员可以查询到归口部门所有待评审专家
     * @param name 名称
     * @param orgName 单位
     * @param mobile 联系方式
     * @param status 状态 ExpertStatusEnum 只处理入库(通过) 出库
     */
    @PreAuthorize("hasAuthority('specialist:review:review')")
    @RequestMapping(value = "/searchExpert")
    public R<Object> searchExpert(String name, String orgName, String mobile,String status,
                                      @RequestParam(value = "page", defaultValue = "1") int pageNo,
                                      @RequestParam(value = "limit", defaultValue = "10") int limit) {

        //所有专家库类别
        List<BaseCategory> baseCategories = baseCategoryService.all();
        Map<String,BaseCategory> baseCategoryMap = new HashMap<>();
        for(BaseCategory baseCategory:baseCategories){
            baseCategoryMap.put(baseCategory.getId(),baseCategory);
        }

        //取入库 出库专家
        List<Expert> expertList = expertService.searchByStatus(ExpertStatusEnum.AGREE.getValue());
        expertList.addAll(expertService.searchByStatus(ExpertStatusEnum.OUT.getValue()));
        //依据搜索条件过滤
        List<Expert> filterList = expertList;
        if(StringUtils.isNotBlank(name)){
            List<Expert> tmpList = new ArrayList<>();
            for(Expert expert:filterList){
                if(expert.getName().contains(name)){
                    tmpList.add(expert);
                }
            }
            filterList = tmpList;
        }
        if(StringUtils.isNotBlank(orgName)){
            List<Expert> tmpList = new ArrayList<>();
            for(Expert expert:filterList){
                if(StringUtils.isNotBlank(expert.getOrgName())&&expert.getOrgName().contains(orgName)){
                    tmpList.add(expert);
                }
            }
            filterList = tmpList;
        }
        if(StringUtils.isNotBlank(mobile)){
            List<Expert> tmpList = new ArrayList<>();
            for(Expert expert:filterList){
                if(expert.getMobile().contains(mobile)){
                    tmpList.add(expert);
                }
            }
            filterList = tmpList;
        }
        if(StringUtils.isNotBlank(status)){
            List<Expert> tmpList = new ArrayList<>();
            for(Expert expert:filterList){
                if(status.equals(expert.getStatus())){
                    tmpList.add(expert);
                }
            }
            filterList = tmpList;
        }

        //依据用户角色过滤
        SysUser currentUser = UserUtils.getUser();
        List<Expert> experts = new ArrayList<>();
        boolean isManager = false;
        if(expertProcessService.isManager(currentUser.getId())){//管理员可以查看所有
            experts = filterList;
            isManager = true;
        }else if(expertProcessService.isGkbmManager(currentUser.getId())){//是归口部门管理员
            List<SysOffice> userOffices = userService.findOfficesByUserId(currentUser.getId());
            for(Expert expert:filterList){
                List<ExpertCategory> expertCategories = expertCategoryService.findByExpertId(expert.getId());//申请的专家库类别
                for(ExpertCategory expertCategory:expertCategories){
                    boolean has = false;
                    BaseCategory baseCategory = baseCategoryMap.get(expertCategory.getCategoryId());
                    for(SysOffice office:userOffices){
                        if(baseCategory!=null&&office.getId().equals(baseCategory.getOfficeId())){
                            has = true;
                            break;
                        }
                    }
                    if(has){
                        experts.add(expert);
                        break;
                    }
                }
            }
        }
        //统计专家评审项目数
        Map<String,Set<Sample>> expertSampleSet = new HashMap<>();
        List<Sample> allSamples = sampleService.findAll();
        List<SampleExpert> allSampleExperts = sampleExpertService.findAll();
        Map<String,Sample> sampleMap = new HashMap<>();
        for(Sample sample:allSamples){
            sampleMap.put(sample.getId(),sample);
        }
        for(SampleExpert sampleExpert:allSampleExperts){
            if(!"1".equals(sampleExpert.getConfirmFlag())){
                continue;
            }
            String expertId = sampleExpert.getExpertId();
            Sample sample = sampleMap.get(sampleExpert.getSampleId());
            if(StringUtils.isBlank(expertId)||sample==null){
                continue;
            }
            Set<Sample> sampleSet = expertSampleSet.get(expertId);
            if(sampleSet==null){
                sampleSet = new HashSet<>();
                expertSampleSet.put(expertId,sampleSet);
            }
            sampleSet.add(sample);
        }
        //排序,根据申报时间
        Collections.sort(experts, new Comparator<Expert>() {
            @Override
            public int compare(Expert o1, Expert o2) {
                return o2.getApplyDate().compareTo(o1.getApplyDate());
            }
        });

        //内存分页
        List<Expert> pageList = new ArrayList<>();
        int start = (pageNo-1)*limit;
        int end = start + limit-1;
        for(int i=start;i>=0&&i<=end&&i<experts.size();i++){
            pageList.add(experts.get(i));
        }

        // 构造返回数据
        List<Object> results = new ArrayList<>();
        for (Expert expert : pageList) {
            //专家库类别
            List<ExpertCategory> expertCategories = expertCategoryService.findByExpertId(expert.getId());
            //返回数据
            List<Object> expertCategoryList = new ArrayList<>();
            for (ExpertCategory s : expertCategories) {
                BaseCategory baseCategory = baseCategoryService.get(s.getCategoryId());
                String categoryName = baseCategory!=null?baseCategory.getName():"";
                Object bean = new DynamicBean.Builder()
                        .setPV("categoryId", s.getCategoryId())
                        .setPV("categoryName", categoryName)
                        .build().getObject();
                expertCategoryList.add(bean);
            }
            BaseEducation education = baseService.get(expert.getEducationId());
            BasePositional positional = basePositionalService.get(expert.getPositionalId());
            Set<Sample> samples = expertSampleSet.get(expert.getId());
            int reviewNum = samples!=null?samples.size():0;
            BaseMajorCategory majorCategory = baseMajorCategoryService.get(expert.getMajorCategoryId());
            String majorCategoryName = majorCategory!=null?majorCategory.getName():"";
            Object o = new DynamicBean.Builder()
                    .setPV("id", expert.getId())
                    .setPV("applyDate", DateUtils.formatDate(expert.getApplyDate(), "yyyy-MM-dd HH:mm:ss"))
                    .setPV("name",  expert.getName())
                    .setPV("exProject", expert.getExProject())
                    .setPV("sex", expert.getSex())
                    .setPV("birthday", DateUtils.formatDate(expert.getBirthday(), "yyyy-MM-dd"))
                    .setPV("identityCard", expert.getIdentityCard())
                    .setPV("parties", expert.getParties())
                    .setPV("educationId", expert.getEducationId())
                    .setPV("educationName",education!=null?education.getName():null)
                    .setPV("degreeId", expert.getDegreeId())
                    .setPV("majorCategoryId", expert.getMajorCategoryId())
                    .setPV("majorCategoryName", majorCategoryName)
                    .setPV("positionalId", expert.getPositionalId())
                    .setPV("positionalName", positional!=null?positional.getName():null)
                    .setPV("orgName", expert.getOrgName())
                    .setPV("studied", expert.getStudied())
                    .setPV("mobile", expert.getMobile())
                    .setPV("star", expert.getStar())
                    .setPV("reviewNum", reviewNum)
                    .setPV("status", expert.getStatus())
                    .setPV("expertCategories", expertCategoryList, List.class)
                    .build().getObject();
            results.add(o);
        }

        Object data = new DynamicBean.Builder()
                .setPV("isManager", isManager)
                .setPV("total", experts.size())
                .setPV("list", results, List.class)
                .build().getObject();
        return R.ok(data);
    }

    /**
     * 查找入库专家
     * 管理员可以查询所有
     * 归口部门管理员可以查询到归口部门所有入库专家
     * @param name 名称
     * @param mobile 联系方式
     */
    @PreAuthorize("hasAuthority('specialist:review:view')")
    @RequestMapping(value = "/searchValidExpert")
    public R<Object> searchValidExpert(String name, String mobile,
                                       @RequestParam(required = false) @DateTimeFormat(pattern="yyyy-MM-dd") Date startTime,
                                       @RequestParam(required = false) @DateTimeFormat(pattern="yyyy-MM-dd") Date endTime,
                                      @RequestParam(value = "page", defaultValue = "1") int pageNo,
                                      @RequestParam(value = "limit", defaultValue = "10") int limit) {
        if(endTime==null){
            endTime = new Date();
        }
        //所有专家库类别
        List<BaseCategory> baseCategories = baseCategoryService.all();
        Map<String,BaseCategory> baseCategoryMap = new HashMap<>();
        for(BaseCategory baseCategory:baseCategories){
            baseCategoryMap.put(baseCategory.getId(),baseCategory);
        }

        //取所有处于通过状态的专家
        List<Expert> expertList = expertService.searchByStatus(ExpertStatusEnum.AGREE.getValue());
        //依据搜索条件过滤
        List<Expert> filterList = expertList;
        if(StringUtils.isNotBlank(name)){
            List<Expert> tmpList = new ArrayList<>();
            for(Expert expert:filterList){
                if(expert.getName().contains(name)){
                    tmpList.add(expert);
                }
            }
            filterList = tmpList;
        }
        if(StringUtils.isNotBlank(mobile)){
            List<Expert> tmpList = new ArrayList<>();
            for(Expert expert:filterList){
                if(expert.getMobile().contains(mobile)){
                    tmpList.add(expert);
                }
            }
            filterList = tmpList;
        }
        if(endTime!=null){
            List<Expert> tmpList = new ArrayList<>();
            for(Expert expert:filterList){
                if(endTime.after(expert.getApplyDate())){
                    if(startTime!=null&&startTime.before(expert.getApplyDate())){
                        tmpList.add(expert);
                    }else if(startTime==null){
                        tmpList.add(expert);
                    }
                }
            }
            filterList = tmpList;
        }

        //依据用户角色过滤
        SysUser currentUser = UserUtils.getUser();
        List<Expert> experts = new ArrayList<>();
        boolean isDistrictManager = false;
        if(expertProcessService.isManager(currentUser.getId())){//管理员可以查看所有
            experts = filterList;
        }else if(expertProcessService.isGkbmManager(currentUser.getId())){//是归口部门管理员
            List<SysOffice> userOffices = userService.findOfficesByUserId(currentUser.getId());
            for(Expert expert:filterList){
                List<ExpertCategory> expertCategories = expertCategoryService.findByExpertId(expert.getId());//申请的专家库类别
                for(ExpertCategory expertCategory:expertCategories){
                    boolean has = false;
                    BaseCategory baseCategory = baseCategoryMap.get(expertCategory.getCategoryId());
                    for(SysOffice office:userOffices){
                        if(baseCategory!=null&&office.getId().equals(baseCategory.getOfficeId())){
                            has = true;
                            break;
                        }
                    }
                    if(has){
                        experts.add(expert);
                        break;
                    }
                }
            }
        }

        //排序
        Collections.sort(experts, new Comparator<Expert>() {
            @Override
            public int compare(Expert o1, Expert o2) {
                if(o1.getApplyDate().after(o2.getApplyDate())){
                    return 1;
                }else if(o1.getApplyDate().before(o2.getApplyDate())){
                    return -1;
                }
                return  0;
            }
        });

        //内存分页
        List<Expert> pageList = new ArrayList<>();
        int start = (pageNo-1)*limit;
        int end = start + limit-1;
        for(int i=start;i>=0&&i<=end&&i<experts.size();i++){
            pageList.add(experts.get(i));
        }

        // 构造返回数据
        List<Object> results = new ArrayList<>();
        for (Expert expert : pageList) {
            //专家库类别
            List<ExpertCategory> expertCategories = expertCategoryService.findByExpertId(expert.getId());
            //返回数据
            List<Object> expertCategoryList = new ArrayList<>();
            for (ExpertCategory s : expertCategories) {
                BaseCategory baseCategory = baseCategoryService.get(s.getCategoryId());
                String categoryName = baseCategory!=null?baseCategory.getName():"";
                Object bean = new DynamicBean.Builder()
                        .setPV("categoryId", s.getCategoryId())
                        .setPV("categoryName", categoryName)
                        .build().getObject();
                expertCategoryList.add(bean);
            }
            BaseEducation education = baseService.get(expert.getEducationId());
            BasePositional positional = basePositionalService.get(expert.getPositionalId());
            Object o = new DynamicBean.Builder()
                    .setPV("id", expert.getId())
                    .setPV("applyDate", DateUtils.formatDate(expert.getApplyDate(), "yyyy-MM-dd HH:mm:ss"))
                    .setPV("name",  expert.getName())
                    .setPV("orgName", expert.getOrgName())
                    .setPV("studied", expert.getStudied())
                    .setPV("positionalId", expert.getPositionalId())
                    .setPV("positionalName", positional!=null?positional.getName():null)
                    .setPV("educationId", expert.getEducationId())
                    .setPV("educationName",education!=null?education.getName():null)
                    .setPV("expertCategories", expertCategoryList, List.class)
                    .setPV("mobile", expert.getMobile()).build().getObject();
            results.add(o);
        }

        Object data = new DynamicBean.Builder()
                .setPV("total", experts.size())
                .setPV("list", results, List.class)
                .build().getObject();
        return R.ok(data);
    }

    /**
     * 所有入库专家
     * @param name 名称
     * @param mobile 联系方式
     */
    @PreAuthorize("hasAuthority('specialist:review:view')")
    @RequestMapping(value = "/allValidExpert")
    public R<Object> allValidExpert(String name, String mobile) {

        //所有专家库类别
        List<BaseCategory> baseCategories = baseCategoryService.all();
        Map<String,BaseCategory> baseCategoryMap = new HashMap<>();
        for(BaseCategory baseCategory:baseCategories){
            baseCategoryMap.put(baseCategory.getId(),baseCategory);
        }

        //取所有处于通过状态的专家
        List<Expert> expertList = expertService.searchByStatus(ExpertStatusEnum.AGREE.getValue());
        //依据搜索条件过滤
        List<Expert> filterList = expertList;
        if(StringUtils.isNotBlank(name)){
            List<Expert> tmpList = new ArrayList<>();
            for(Expert expert:filterList){
                if(expert.getName().contains(name)){
                    tmpList.add(expert);
                }
            }
            filterList = tmpList;
        }
        if(StringUtils.isNotBlank(mobile)){
            List<Expert> tmpList = new ArrayList<>();
            for(Expert expert:filterList){
                if(expert.getMobile().contains(mobile)){
                    tmpList.add(expert);
                }
            }
            filterList = tmpList;
        }

        List<Expert> experts = new ArrayList<>();
        experts = filterList;

        //排序
        Collections.sort(experts, new Comparator<Expert>() {
            @Override
            public int compare(Expert o1, Expert o2) {
                if(o1.getApplyDate().after(o2.getApplyDate())){
                    return 1;
                }else if(o1.getApplyDate().before(o2.getApplyDate())){
                    return -1;
                }
                return  0;
            }
        });

        // 构造返回数据
        List<Object> results = new ArrayList<>();
        for (Expert expert : experts) {
            //专家库类别
            List<ExpertCategory> expertCategories = expertCategoryService.findByExpertId(expert.getId());
            //返回数据
            List<Object> expertCategoryList = new ArrayList<>();
            for (ExpertCategory s : expertCategories) {
                BaseCategory baseCategory = baseCategoryService.get(s.getCategoryId());
                String categoryName = baseCategory!=null?baseCategory.getName():"";
                Object bean = new DynamicBean.Builder()
                        .setPV("categoryId", s.getCategoryId())
                        .setPV("categoryName", categoryName)
                        .build().getObject();
                expertCategoryList.add(bean);
            }
            Object o = new DynamicBean.Builder()
                    .setPV("id", expert.getId())
                    .setPV("applyDate", DateUtils.formatDate(expert.getApplyDate(), "yyyy-MM-dd HH:mm:ss"))
                    .setPV("name",  expert.getName())
                    .setPV("orgName", expert.getOrgName())
                    .setPV("studied", expert.getStudied())
                    .setPV("expertCategories", expertCategoryList, List.class)
                    .setPV("mobile", expert.getMobile()).build().getObject();
            results.add(o);
        }

        Object data = new DynamicBean.Builder()
                .setPV("total", experts.size())
                .setPV("list", results, List.class)
                .build().getObject();
        return R.ok(data);
    }

    /**
     * 查找出库专家
     * 管理员可以查询所有
     * 归口部门管理员可以查询到归口部门所有出库专家
     * @param name 名称
     * @param mobile 联系方式
     */
    @PreAuthorize("hasAuthority('specialist:review:view')")
    @RequestMapping(value = "/searchOutExpert")
    public R<Object> searchOutExpert(String name, String mobile,
                                       @RequestParam(required = false) @DateTimeFormat(pattern="yyyy-MM-dd") Date startTime,
                                       @RequestParam(required = false) @DateTimeFormat(pattern="yyyy-MM-dd") Date endTime,
                                       @RequestParam(value = "page", defaultValue = "1") int pageNo,
                                       @RequestParam(value = "limit", defaultValue = "10") int limit) {
        if(endTime==null){
            endTime = new Date();
        }
        //所有专家库类别
        List<BaseCategory> baseCategories = baseCategoryService.all();
        Map<String,BaseCategory> baseCategoryMap = new HashMap<>();
        for(BaseCategory baseCategory:baseCategories){
            baseCategoryMap.put(baseCategory.getId(),baseCategory);
        }

        //取所有处于通过状态的专家
        List<Expert> expertList = expertService.searchByStatus(ExpertStatusEnum.OUT.getValue());
        //依据搜索条件过滤
        List<Expert> filterList = expertList;
        if(StringUtils.isNotBlank(name)){
            List<Expert> tmpList = new ArrayList<>();
            for(Expert expert:filterList){
                if(expert.getName().contains(name)){
                    tmpList.add(expert);
                }
            }
            filterList = tmpList;
        }
        if(StringUtils.isNotBlank(mobile)){
            List<Expert> tmpList = new ArrayList<>();
            for(Expert expert:filterList){
                if(expert.getMobile().contains(mobile)){
                    tmpList.add(expert);
                }
            }
            filterList = tmpList;
        }
        if(endTime!=null){
            List<Expert> tmpList = new ArrayList<>();
            for(Expert expert:filterList){
                if(endTime.after(expert.getApplyDate())){
                    if(startTime!=null&&startTime.before(expert.getApplyDate())){
                        tmpList.add(expert);
                    }else if(startTime==null){
                        tmpList.add(expert);
                    }
                }
            }
            filterList = tmpList;
        }

        //依据用户角色过滤
        SysUser currentUser = UserUtils.getUser();
        List<Expert> experts = new ArrayList<>();
        boolean isDistrictManager = false;
        if(expertProcessService.isManager(currentUser.getId())){//管理员可以查看所有
            experts = filterList;
        }else if(expertProcessService.isGkbmManager(currentUser.getId())){//是归口部门管理员
            List<SysOffice> userOffices = userService.findOfficesByUserId(currentUser.getId());
            for(Expert expert:filterList){
                List<ExpertCategory> expertCategories = expertCategoryService.findByExpertId(expert.getId());//申请的专家库类别
                for(ExpertCategory expertCategory:expertCategories){
                    boolean has = false;
                    BaseCategory baseCategory = baseCategoryMap.get(expertCategory.getCategoryId());
                    for(SysOffice office:userOffices){
                        if(baseCategory!=null&&office.getId().equals(baseCategory.getOfficeId())){
                            has = true;
                            break;
                        }
                    }
                    if(has){
                        experts.add(expert);
                        break;
                    }
                }
            }
        }

        //排序
        Collections.sort(experts, new Comparator<Expert>() {
            @Override
            public int compare(Expert o1, Expert o2) {
                if(o1.getApplyDate().after(o2.getApplyDate())){
                    return 1;
                }else if(o1.getApplyDate().before(o2.getApplyDate())){
                    return -1;
                }
                return  0;
            }
        });

        //内存分页
        List<Expert> pageList = new ArrayList<>();
        int start = (pageNo-1)*limit;
        int end = start + limit-1;
        for(int i=start;i>=0&&i<=end&&i<experts.size();i++){
            pageList.add(experts.get(i));
        }

        // 构造返回数据
        List<Object> results = new ArrayList<>();
        for (Expert expert : pageList) {
            //专家库类别
            List<ExpertCategory> expertCategories = expertCategoryService.findByExpertId(expert.getId());
            //返回数据
            List<Object> expertCategoryList = new ArrayList<>();
            for (ExpertCategory s : expertCategories) {
                BaseCategory baseCategory = baseCategoryService.get(s.getCategoryId());
                String categoryName = baseCategory!=null?baseCategory.getName():"";
                Object bean = new DynamicBean.Builder()
                        .setPV("categoryId", s.getCategoryId())
                        .setPV("categoryName", categoryName)
                        .build().getObject();
                expertCategoryList.add(bean);
            }
            BaseEducation education = baseService.get(expert.getEducationId());
            BasePositional positional = basePositionalService.get(expert.getPositionalId());
            Object o = new DynamicBean.Builder()
                    .setPV("id", expert.getId())
                    .setPV("applyDate", DateUtils.formatDate(expert.getApplyDate(), "yyyy-MM-dd HH:mm:ss"))
                    .setPV("name",  expert.getName())
                    .setPV("orgName", expert.getOrgName())
                    .setPV("studied", expert.getStudied())
                    .setPV("positionalId", expert.getPositionalId())
                    .setPV("positionalName", positional!=null?positional.getName():null)
                    .setPV("educationId", expert.getEducationId())
                    .setPV("educationName",education!=null?education.getName():null)
                    .setPV("expertCategories", expertCategoryList, List.class)
                    .setPV("mobile", expert.getMobile()).build().getObject();
            results.add(o);
        }

        Object data = new DynamicBean.Builder()
                .setPV("total", experts.size())
                .setPV("list", results, List.class)
                .build().getObject();
        return R.ok(data);
    }

    /**
     * 出库
     * @param id
     * @param opinion 意见
     */
    @LoggerMonitor(value = "【专家库】专家出库")
    @PreAuthorize("hasAuthority('specialist:review:change')")
    @RequestMapping(value = "/changeOut")
    public R<Object> changeOut(String id,String opinion){
        Expert expert = expertService.get(id);
        expertProcessService.changeOut(expert,opinion);
        return R.ok();
    }

    /**
     * 入库
     * @param id
     * @param opinion 意见
     */
    @LoggerMonitor(value = "【专家库】专家入库")
    @PreAuthorize("hasAuthority('specialist:review:change')")
    @RequestMapping(value = "/changeIn")
    public R<Object> changeIn(String id,String opinion){
        Expert expert = expertService.get(id);
        expertProcessService.changeIn(expert,opinion);
        return R.ok();
    }

    /**searchExpert
     * 设置星级 只有管理员可以操作
     * @param id
     * @param star 星级 可以为空
     */
    @LoggerMonitor(value = "【专家库】设置星级")
    @PreAuthorize("hasAuthority('specialist:review:change')")
    @RequestMapping(value = "/changeStar")
    public R<Object> changeStar(String id,String star){
        Expert expert = expertService.get(id);
        expertProcessService.changeStar(expert,star);
        return R.ok();
    }

    /**
     * 删除
     * @param id
     */
    @LoggerMonitor(value = "【专家库】删除专家")
    @PreAuthorize("hasAuthority('specialist:review:change')")
    @RequestMapping(value = "/delete")
    public R<Object> delete(String id){
        Expert expert = expertService.get(id);
        expertProcessService.delete(expert);
        return R.ok();
    }

}
