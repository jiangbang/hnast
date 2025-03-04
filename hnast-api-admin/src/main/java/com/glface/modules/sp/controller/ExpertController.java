package com.glface.modules.sp.controller;

import com.glface.base.bean.DynamicBean;
import com.glface.base.bean.R;
import com.glface.base.utils.DateUtils;
import com.glface.base.utils.StringUtils;
import com.glface.common.exeception.ServiceException;
import com.glface.log.LoggerMonitor;
import com.glface.model.SysUser;
import com.glface.modules.model.FileInfo;
import com.glface.modules.service.FileService;
import com.glface.modules.sp.model.*;
import com.glface.modules.sp.service.*;
import com.glface.modules.utils.ExpertStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.glface.modules.sys.utils.UserUtils;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.glface.common.web.ApiCode.SP_EXPERT_MOBILE_REQUIRED;
import static com.glface.common.web.ApiCode.SP_EXPERT_NOT_EXIST;

@Slf4j
@RestController
@RequestMapping("/specialist/expert")
public class ExpertController {

    @Resource
    private ExpertService expertService;
    @Resource
    private ExpertAvoidOrgService avoidOrgService;
    @Resource
    private ExpertExtService expertExtService;
    @Resource
    private ExpertMajorService majorService;

    @Resource
    private ExpertTechnicalTitleService technicalTitleService;
    @Resource
    private ExpertQualificationService qualificationService;
    @Resource
    private ExpertFileService expertFileService;
    @Resource
    private BaseMajorService baseMajorService;
    @Resource
    private FileService fileService;
    @Resource
    private ExpertCategoryService expertCategoryService;
    @Resource
    private BaseCategoryService baseCategoryService;
    @Resource
    private BaseEducationService baseService;
    @Resource
    private BasePositionalService basePositionalService;
    /**
     * 查询我的内容
     * 通过createBy查询
     */
    @PreAuthorize("hasAuthority('specialist:expert:view')")
    @RequestMapping(value = "/my")
    public R<Object> my(@RequestParam(value = "page", defaultValue = "1") int pageNo,
                        @RequestParam(value = "limit", defaultValue = "10") int limit) {
        SysUser currentUser = UserUtils.getUser();
//        String mobile = currentUser.getMobile();
//        if(StringUtils.isBlank(mobile)){
//            throw new ServiceException(SP_EXPERT_MOBILE_REQUIRED);
//        }
        List<Expert> experts = expertService.findByCreateBy(currentUser.getId());
        //排序
        Collections.sort(experts, new Comparator<Expert>() {
            @Override
            public int compare(Expert o1, Expert o2) {
                return o2.getCreateDate().compareTo(o1.getCreateDate());
            }
        });

        //内存分页
        List<Expert> pageList = new ArrayList<>();
        int start = (pageNo-1)*limit;
        int end = start + limit-1;
        for(int i=start;i>=0&&i<=end&&i<experts.size();i++){
            pageList.add(experts.get(i));
        }

        List<Object> results = new ArrayList<>();
        for(Expert expert:pageList){
            ExpertExt expertExt = new ExpertExt();
            if (StringUtils.isNotBlank(expert.getExtId())) {
                expertExt = expertExtService.get(expert.getExtId());
            }
            //回避单位信息
            List<ExpertAvoidOrg> avoidOrgs = avoidOrgService.findByExpertId(expert.getId());
            //返回数据
            Object expertExtObject = new DynamicBean.Builder().setPV("id", expertExt.getId())
                    .setPV("work", expertExt.getWork())
                    .setPV("achievement", expertExt.getAchievement())
                    .setPV("partTime", expertExt.getPartTime())
                    .setPV("former", expertExt.getFormer())
                    .build().getObject();
            List<Object> avoidOrgList = new ArrayList<>();
            for (ExpertAvoidOrg s : avoidOrgs) {
                Object bean = new DynamicBean.Builder().setPV("id", s.getId())
                        .setPV("orgName", s.getOrgName())
                        .setPV("orgCode", s.getOrgCode())
                        .setPV("remark", s.getRemark())
                        .build().getObject();
                avoidOrgList.add(bean);
            }
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
            Object contentBean = new DynamicBean.Builder().setPV("id", expert.getId())
                    .setPV("name", expert.getName())
                    .setPV("sex", expert.getSex())
                    .setPV("birthday", DateUtils.formatDate(expert.getBirthday(), "yyyy-MM-dd"))
                    .setPV("identityCard", expert.getIdentityCard())
                    .setPV("parties", expert.getParties())
                    .setPV("educationId", expert.getEducationId())
                    .setPV("educationName",education!=null?education.getName():null)
                    .setPV("degreeId", expert.getDegreeId())
                    .setPV("studied", expert.getStudied())
                    .setPV("specialty", expert.getSpecialty())
                    .setPV("majorCategoryId", expert.getMajorCategoryId())
                    .setPV("orgName", expert.getOrgName())
                    .setPV("job", expert.getJob())
                    .setPV("positionalId", expert.getPositionalId())
                    .setPV("positionalName", positional!=null?positional.getName():null)
                    .setPV("address", expert.getAddress())
                    .setPV("post", expert.getPost())
                    .setPV("mobile", expert.getMobile())
                    .setPV("email", expert.getEmail())
                    .setPV("wx", expert.getWx())
                    .setPV("qq", expert.getQq())
                    .setPV("status", expert.getStatus())
                    .setPV("pictureFileId", expert.getPictureFileId())
                    .setPV("status", expert.getStatus())
                    .setPV("applyDate", DateUtils.formatDate(expert.getApplyDate(), "yyyy-MM-dd"))
                    .setPV("avoidOrgs", avoidOrgList, List.class)
                    .setPV("expertCategories", expertCategoryList, List.class)
                    .setPV("expertExt", expertExtObject,Object.class)

                    .build().getObject();
            results.add(contentBean);
        }
        Object result = new DynamicBean.Builder()
                .setPV("total", experts.size())
                .setPV("list", results)
                .build().getObject();
        return R.ok(result);
    }

    /**
     * 通过id查询内容
     */
    @PreAuthorize("hasAuthority('specialist:expert:view')")
    @RequestMapping(value = "/content")
    public R<Object> contentInfo(String id) {
        if (StringUtils.isBlank(id)) {
            throw new ServiceException(SP_EXPERT_NOT_EXIST);
        }
        Expert expert = expertService.get(id);
        if (expert == null) {
            throw new ServiceException(SP_EXPERT_NOT_EXIST);
        }

        ExpertExt expertExt = new ExpertExt();
        if (StringUtils.isNotBlank(expert.getExtId())) {
            expertExt = expertExtService.get(expert.getExtId());
        }
        //回避单位信息
        List<ExpertAvoidOrg> avoidOrgs = avoidOrgService.findByExpertId(id);
        //返回数据
        Object expertExtObject = new DynamicBean.Builder().setPV("id", expertExt.getId())
                .setPV("work", expertExt.getWork())
                .setPV("achievement", expertExt.getAchievement())
                .setPV("partTime", expertExt.getPartTime())
                .setPV("former", expertExt.getFormer())
                .build().getObject();
        List<Object> avoidOrgList = new ArrayList<>();
        for (ExpertAvoidOrg s : avoidOrgs) {
            Object bean = new DynamicBean.Builder().setPV("id", s.getId())
                    .setPV("orgName", s.getOrgName())
                    .setPV("orgCode", s.getOrgCode())
                    .setPV("remark", s.getRemark())
                    .build().getObject();
            avoidOrgList.add(bean);
        }
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

        Object contentBean = new DynamicBean.Builder().setPV("id", expert.getId())
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
                .setPV("status", expert.getStatus())

                .setPV("avoidOrgs", avoidOrgList, List.class)
                .setPV("expertCategories", expertCategoryList, List.class)
                .setPV("expertExt", expertExtObject,Object.class)

                .build().getObject();
        return R.ok(contentBean);
    }

    /**
     * 暂存内容 不对参数进行验证
     * 必须填写联系电话
     * 注意：得到返回值后，页面必须调用/content 从新刷新页面
     *
     * @param expertParam
     * @return
     */
    @LoggerMonitor(value = "【专家库】我的申报-暂存")
    @PreAuthorize("hasAuthority('specialist:expert:save')")
    @RequestMapping(value = "/cacheContent")
    public R<Object> cacheContent(@RequestBody ExpertParam expertParam) {
        Expert expert = expertService.cacheContent(expertParam);
        DynamicBean.Builder builder = new DynamicBean.Builder().setPV("id", expert.getId());
        return R.ok(builder.build().getObject());
    }

    /**
     * 保存内容
     *
     * @param expertParam
     * @return
     */
    @LoggerMonitor(value = "【专家库】我的申报-保存")
    @PreAuthorize("hasAuthority('specialist:expert:save')")
    @RequestMapping(value = "/saveContent")
    public R<Object> saveContent(@RequestBody ExpertParam expertParam) {
        Expert expert = expertService.saveContent(expertParam);
        DynamicBean.Builder builder = new DynamicBean.Builder().setPV("id", expert.getId());
        return R.ok(builder.build().getObject());
    }

    /**
     * 通过专家id查询评标专业信息
     *
     * @param id 专家id
     */
    @PreAuthorize("hasAuthority('specialist:expert:view')")
    @RequestMapping(value = "/getExpertMajor")
    public R<Object> getExpertMajorInfo(String id) {
        if (StringUtils.isBlank(id)) {
            throw new ServiceException(SP_EXPERT_NOT_EXIST);
        }
        Expert expert = expertService.get(id);
        if (expert == null) {
            throw new ServiceException(SP_EXPERT_NOT_EXIST);
        }
        List<ExpertMajor> majors = majorService.findByExpertId(id);
        List<Object> majorList = new ArrayList<>();
        for (ExpertMajor major : majors) {
            //文件名
            String fileId = major.getFileId();
            String fileName = "";
            if (StringUtils.isNotBlank(fileId)) {
                FileInfo fileInfo = fileService.get(fileId);
                if (fileInfo != null) {
                    fileName = fileInfo.getName();
                }
            }
            //行业
            String majorId = major.getBaseMajorId();
            String majorName = "";
            String majorCategoryId = "";
            String majorCategoryName = "";
            if (StringUtils.isNotBlank(majorId)) {
                BaseMajor baseMajor = baseMajorService.get(majorId);
                if (baseMajor != null) {
                    majorName = baseMajor.getName();
                    BaseMajorCategory baseMajorCategory = baseMajorService.getByMajorId(majorId);
                    if (baseMajorCategory != null) {
                        majorCategoryId = baseMajorCategory.getId();
                        majorCategoryName = baseMajorCategory.getName();
                    }
                }
            }
            Object bean = new DynamicBean.Builder().setPV("id", major.getId())
                    .setPV("expertId", major.getExpertId())
                    .setPV("baseMajorId", major.getBaseMajorId())
                    .setPV("majorName", majorName)
                    .setPV("majorCategoryId", majorCategoryId)
                    .setPV("majorCategoryName", majorCategoryName)
                    .setPV("seniorFlag", major.getSeniorFlag())
                    .setPV("fileId", major.getFileId())
                    .setPV("fileName", fileName)
                    .build().getObject();
            majorList.add(bean);
        }

        Object result = new DynamicBean.Builder().setPV("id", id)
                .setPV("majors", majorList)
                .build().getObject();
        return R.ok(result);
    }


    /**
     * 保存评标专业信息
     * 需要刷新页面
     * @param expertMajorParams
     * @return
     */
    @LoggerMonitor(value = "【专家库】我的申报-保存评标专业")
    @PreAuthorize("hasAuthority('specialist:expert:save')")
    @RequestMapping(value = "/saveExpertMajor")
    public R<Object> saveExpertMajor(@RequestBody List<ExpertMajorParam> expertMajorParams) {
        expertService.saveExpertMajor(expertMajorParams);
        return R.ok();
    }


    /**
     * 通过专家id查询技术职称信息
     *
     * @param id 专家id
     */
    @PreAuthorize("hasAuthority('specialist:expert:view')")
    @RequestMapping(value = "/getTechnicalTitle")
    public R<Object> getTechnicalTitle(String id) {
        if (StringUtils.isBlank(id)) {
            throw new ServiceException(SP_EXPERT_NOT_EXIST);
        }
        Expert expert = expertService.get(id);
        if (expert == null) {
            throw new ServiceException(SP_EXPERT_NOT_EXIST);
        }
        List<ExpertTechnicalTitle> list = technicalTitleService.findByExpertId(id);
        List<Object> results = new ArrayList<>();
        for (ExpertTechnicalTitle technicalTitle : list) {
            //文件名
            String fileId = technicalTitle.getFileId();
            String fileName = "";
            if (StringUtils.isNotBlank(fileId)) {
                FileInfo fileInfo = fileService.get(fileId);
                if (fileInfo != null) {
                    fileName = fileInfo.getName();
                }
            }
            Object bean = new DynamicBean.Builder().setPV("id", technicalTitle.getId())
                    .setPV("expertId", technicalTitle.getExpertId())
                    .setPV("title", technicalTitle.getTitle())
                    .setPV("start", DateUtils.formatDate(technicalTitle.getStart(), "yyyy-MM-dd"))
                    .setPV("end", DateUtils.formatDate(technicalTitle.getEnd(), "yyyy-MM-dd"))
                    .setPV("fileId", technicalTitle.getFileId())
                    .setPV("fileName", fileName)
                    .build().getObject();
            results.add(bean);
        }

        Object result = new DynamicBean.Builder().setPV("id", id)
                .setPV("list", results)
                .build().getObject();
        return R.ok(result);
    }


    /**
     * 保存评标专业信息
     * 需要刷新页面
     * @param technicalTitleParams
     * @return
     */
    @LoggerMonitor(value = "【专家库】我的申报-保存技术职称")
    @PreAuthorize("hasAuthority('specialist:expert:save')")
    @RequestMapping(value = "/saveTechnicalTitles")
    public R<Object> saveTechnicalTitles(@RequestBody List<ExpertTechnicalTitleParam> technicalTitleParams) {
        expertService.saveTechnicalTitle(technicalTitleParams);
        return R.ok();
    }

    /**
     * 通过专家id查询职业资格
     *
     * @param id 专家id
     */
    @PreAuthorize("hasAuthority('specialist:expert:view')")
    @RequestMapping(value = "/getQualification")
    public R<Object> getQualification(String id) {
        if (StringUtils.isBlank(id)) {
            throw new ServiceException(SP_EXPERT_NOT_EXIST);
        }
        Expert expert = expertService.get(id);
        if (expert == null) {
            throw new ServiceException(SP_EXPERT_NOT_EXIST);
        }
        List<ExpertQualification> list = qualificationService.findByExpertId(id);
        List<Object> results = new ArrayList<>();
        for (ExpertQualification qualification : list) {
            //文件名
            String fileId = qualification.getFileId();
            String fileName = "";
            if (StringUtils.isNotBlank(fileId)) {
                FileInfo fileInfo = fileService.get(fileId);
                if (fileInfo != null) {
                    fileName = fileInfo.getName();
                }
            }
            Object bean = new DynamicBean.Builder().setPV("id", qualification.getId())
                    .setPV("expertId", qualification.getExpertId())
                    .setPV("title", qualification.getTitle())
                    .setPV("orgName", qualification.getOrgName())
                    .setPV("number", qualification.getNumber())
                    .setPV("start", DateUtils.formatDate(qualification.getStart(), "yyyy-MM-dd"))
                    .setPV("end", DateUtils.formatDate(qualification.getEnd(), "yyyy-MM-dd"))
                    .setPV("fileId", qualification.getFileId())
                    .setPV("fileName", fileName)
                    .build().getObject();
            results.add(bean);
        }

        Object result = new DynamicBean.Builder().setPV("id", id)
                .setPV("list", results)
                .build().getObject();
        return R.ok(result);
    }

    /**
     * 保存职业资格
     * 需要刷新页面
     * @param qualificationParams
     * @return
     */
    @LoggerMonitor(value = "【专家库】我的申报-保存职业资格")
    @PreAuthorize("hasAuthority('specialist:expert:save')")
    @RequestMapping(value = "/saveQualifications")
    public R<Object> saveQualifications(@RequestBody List<ExpertQualificationParam> qualificationParams) {
        expertService.saveQualification(qualificationParams);
        return R.ok();
    }

    /**
     * 通过专家id查询附件
     *
     * @param id 专家id
     */
    @PreAuthorize("hasAuthority('specialist:expert:view')")
    @RequestMapping(value = "/getExpertFile")
    public R<Object> getExpertFile(String id) {
        if (StringUtils.isBlank(id)) {
            throw new ServiceException(SP_EXPERT_NOT_EXIST);
        }
        Expert expert = expertService.get(id);
        if (expert == null) {
            throw new ServiceException(SP_EXPERT_NOT_EXIST);
        }
        ExpertFile expertFile = new ExpertFile();
        if (StringUtils.isNotBlank(expert.getExpertFileId())) {
            expertFile = expertFileService.get(expert.getExpertFileId());
        }
        //返回数据
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
        return R.ok(fileObject);
    }

    /**
     * 保存附件  不做验证
     * 需要刷新页面
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
    @LoggerMonitor(value = "【专家库】我的申报-保存附件")
    @PreAuthorize("hasAuthority('specialist:expert:save')")
    @RequestMapping(value = "/saveFile")
    public R<Object> saveFile(String id,
                                        String educationFileId,
                                        String identityFileId,
                                        String orgFileId,
                                        String commitmentFileId,
                                        String academicianFileId,
                                        String secrecyFileId, String reviewFileId) {
        expertService.saveFile(id,educationFileId,identityFileId,orgFileId,commitmentFileId,academicianFileId,secrecyFileId,reviewFileId);
        return R.ok();
    }

    @LoggerMonitor(value = "【专家库】我的申报-删除附件")
    @PreAuthorize("hasAuthority('specialist:expert:save')")
    @RequestMapping(value = "/deleteFile")
    public R<Object> deleteFile(String id,
                              String fileIdName) {
        expertService.deleteFile(id,fileIdName);
        return R.ok();
    }

    /**
     * 所有工作单位
     * @return
     */
    @RequestMapping(value = "/allOrgNames")
    public R<Object> allOrgNames() {
        List<String> list = expertService.findOrgNamesByStatus(ExpertStatusEnum.AGREE.getValue());
        return R.ok(list);
    }

    /**
     * 提交申报
     * @param id
     * @return
     */
    @LoggerMonitor(value = "【专家库】我的申报-提交申报")
    @RequestMapping("/submit")
    public R<Object> submit(String id){
        expertService.submit(id);
        return R.ok();
    }


    /**
     * 删除
     * @param id
     */
    @LoggerMonitor(value = "【专家库】我的申报-删除")
    @PreAuthorize("hasAuthority('specialist:expert:del')")
    @RequestMapping(value = "/delete")
    public R<Object> delete(String id){
        Expert expert = expertService.get(id);
        expertService.delete(expert);
        return R.ok();
    }
}
