package com.glface.modules.sp.controller;

import cn.afterturn.easypoi.word.WordExportUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.glface.base.bean.DynamicBean;
import com.glface.base.bean.Page;
import com.glface.base.bean.R;
import com.glface.base.utils.AntiSQLInjectionUtil;
import com.glface.base.utils.DateUtils;
import com.glface.base.utils.NamingStrategyUtils;
import com.glface.base.utils.StringUtils;
import com.glface.common.exeception.ServiceException;
import com.glface.common.utils.SpringContextUtil;
import com.glface.log.LoggerMonitor;
import com.glface.model.SysUser;
import com.glface.modules.model.FileInfo;
import com.glface.modules.service.FileService;
import com.glface.modules.sp.mapper.ExpertConditionMapper;
import com.glface.modules.sp.mapper.SampleConditionMapper;
import com.glface.modules.sp.mapper.SampleMapper;
import com.glface.modules.sp.mapper.SampleNameMapper;
import com.glface.modules.sp.model.*;
import com.glface.modules.sp.model.json.ExpertJson;
import com.glface.modules.sp.service.*;
import com.glface.modules.utils.SampleStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Paths;
import java.util.*;

import static com.glface.common.web.ApiCode.SP_EXPERT_NOT_EXIST;

/**
 * 专家抽取
 */
@Slf4j
@RestController
@RequestMapping("/specialist/sample")
public class SampleController {

    @Resource
    private SampleService sampleService;
    @Resource
    private SampleMapper sampleMapper;
    @Resource
    private BaseCategoryService baseCategoryService;
    @Resource
    private BasePositionalService basePositionalService;
    @Resource
    private SampleAvoidService sampleAvoidService;
    @Resource
    private ExpertService expertService;
    @Resource
    private SampleExpertService sampleExpertService;
    @Resource
    private ExpertProcessService expertProcessService;
    @Resource
    private ExpertJsonService expertJsonService;
    @Resource
    private FileService fileService;
    @Resource
    private BaseEducationService baseEducationService;
    @Resource
    private ExpertConditionMapper expertConditionMapper;
    @Resource
    SampleNameMapper sampleNameMapper;
    @Resource
    SampleConditionMapper sampleConditionMapper;

    @RequestMapping(value = "/get")
    public R<Object> get(String id) {
        Sample bean = sampleService.get(id);
        //构造返回数据
        Object object = new DynamicBean.Builder().setPV("id", bean.getId())
                .setPV("projectName", bean.getProjectName())
                .setPV("baseCategoryIds", bean.getBaseCategoryIds())
                .setPV("reviewDate", DateUtils.formatDate(bean.getReviewDate(), "yyyy-MM-dd"))
                .setPV("status", bean.getStatus())
                .setPV("number", bean.getNumber())
                .setPV("remark", bean.getRemark())
                .setPV("result", bean.getResult())
                .build().getObject();
        return R.ok(object);
    }

    @RequestMapping(value = "/getDetail")
    public R<Object> getDetail(String id) {
        Sample bean = sampleService.get(id);
        //类别名称
        List<String> baseCategoryNames = new ArrayList<>();
        if(StringUtils.isNotBlank(bean.getBaseCategoryIds())){
            for(String baseCategoryId:bean.getBaseCategoryIds().split(",")){
                BaseCategory baseCategory = baseCategoryService.get(baseCategoryId);
                if(baseCategory!=null){
                    baseCategoryNames.add(baseCategory.getName());
                }
            }
        }
        //专家抽取屏蔽条件信息
        List<SampleAvoid> avoids = sampleAvoidService.findBySampleId(id);
        List<Object> avoidList = new ArrayList<>();
        for (SampleAvoid avoid : avoids) {
            String expertId = avoid.getExpertId();
            String expertName = "";//专家名称
            if(StringUtils.isNotBlank(expertId)){
                Expert expert = expertService.get(expertId);
                expertName = expert!=null?expert.getName():"";
            }
            Object avoidBean = new DynamicBean.Builder().setPV("id", avoid.getId())
                    .setPV("type", avoid.getType())
                    .setPV("orgName", avoid.getOrgName())
                    .setPV("orgCode", avoid.getOrgCode())
                    .setPV("expertId", avoid.getExpertId())
                    .setPV("expertName", expertName)
                    .setPV("remark", avoid.getRemark())
                    .build().getObject();
            avoidList.add(avoidBean);
        }
        //专家抽取结果
        List<SampleExpert> experts = sampleExpertService.findBySampleId(id);
        List<Object> expertList = new ArrayList<>();
        for (SampleExpert sampleExpert : experts) {
            String baseCategoryId = sampleExpert.getBaseCategoryId();
            String baseCategoryName = "";
            if(StringUtils.isNotBlank(baseCategoryId)){
                BaseCategory baseCategory = baseCategoryService.get(baseCategoryId);
                baseCategoryName = baseCategory!=null?baseCategory.getName():"";
            }
            String expertId = sampleExpert.getExpertId();
            String expertName = "";//专家名称
            String positionalName = "";//职称
            String positionalId = "";
            String orgName = ""; //工作单位;
            String mobile = "";
            String educationName = "";
            String studied = "";
            if(StringUtils.isNotBlank(expertId)){
                Expert expert = expertService.get(expertId);
                if(expert!=null){
                    expertName = expert.getName();
                    orgName = expert.getOrgName();
                    positionalId = expert.getPositionalId();
                    if(StringUtils.isNotBlank(positionalId)){
                        BasePositional basePositional = basePositionalService.get(positionalId);
                        positionalName = basePositional!=null?basePositional.getName():"";
                    }
                    mobile = expert.getMobile();

                    BaseEducation education = baseEducationService.get(expert.getEducationId());
                    educationName = education!=null?education.getName():"";
                    studied = expert.getStudied();
                }
            }

            Object expertBean = new DynamicBean.Builder().setPV("id", sampleExpert.getId())
                    .setPV("baseCategoryId", baseCategoryId)
                    .setPV("baseCategoryName", baseCategoryName)
                    .setPV("expertId", sampleExpert.getExpertId())
                    .setPV("rounds", sampleExpert.getRounds())
                    .setPV("confirmFlag", sampleExpert.getConfirmFlag())
                    .setPV("confirmDate", DateUtils.formatDate(sampleExpert.getConfirmDate(), "yyyy-MM-dd"))
                    .setPV("expertName", expertName)
                    .setPV("positionalName", positionalName)
                    .setPV("orgName", orgName)
                    .setPV("educationName", educationName)
                    .setPV("studied", studied)
                    .setPV("mobile", mobile)
                    .build().getObject();
            expertList.add(expertBean);
        }
        //是否管理员
        SysUser currentUser = com.glface.modules.sys.utils.UserUtils.getUser();
        boolean isManager = false;
        if(expertProcessService.isManager(currentUser.getId())){//管理员
            isManager = true;
        }

        //构造返回数据
        Object object = new DynamicBean.Builder().setPV("id", bean.getId())
                .setPV("projectName", bean.getProjectName())
                .setPV("baseCategoryIds", bean.getBaseCategoryIds())
                .setPV("baseCategoryNames", baseCategoryNames,List.class)
                .setPV("reviewDate", DateUtils.formatDate(bean.getReviewDate(), "yyyy-MM-dd"))
                .setPV("status", bean.getStatus())
                .setPV("number", bean.getNumber())
                .setPV("remark", bean.getRemark())
                .setPV("result", bean.getResult())
                .setPV("isManager", isManager)
                .setPV("avoidList", avoidList,List.class)
                .setPV("expertList", expertList,List.class)
                .build().getObject();
        return R.ok(object);
    }

    /**
     * 分页查询
     * @param projectName 名称
     * @param baseCategoryIds 分类id
     * @param status 状态 0:未抽取   1:已抽取
     * @param pageNo 查询分页
     * @param limit  查询数
     * @param order  排序
     */
    @RequestMapping(value = "/search")
    public R<Object> search(String projectName,
                            String baseCategoryIds,
                            String status,
                            @RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
                            @RequestParam(value = "limit", defaultValue = "10") int limit,
                            @RequestParam(value = "order", defaultValue = "createDate desc") String order) {
        order = NamingStrategyUtils.underscoreName(AntiSQLInjectionUtil.filter(order.trim()));
        order = "s." + order;
        // 设置查询条件
        Page<Sample> page = new Page<>(pageNo, limit);
        page.setOrderBy(order);
        Sample condition = new Sample();
        condition.setProjectName(projectName);
        condition.setBaseCategoryIds(baseCategoryIds);
        condition.setStatus(status);

        // 查询
        page = sampleService.pageSearch(page, condition);

        // 构造返回数据
        List<Object> batchList = new ArrayList<>();
        for (Sample bean : page.getList()) {
            //查找分类名称
            List<String> baseCategoryNames = new ArrayList<>();
            String categoryIds = bean.getBaseCategoryIds();
            if(StringUtils.isNotBlank(categoryIds)){
                String[] categoryList = categoryIds.split(",");
                for(String category:categoryList){
                    BaseCategory baseCategory = baseCategoryService.get(category);
                    if(baseCategory!=null){
                        baseCategoryNames.add(baseCategory.getName());
                    }
                }
            }
            //专家抽取结果
            LambdaQueryWrapper<ExpertCondition> queryWrapper = Wrappers.<ExpertCondition>query().lambda();
            queryWrapper.eq(ExpertCondition::getDelFlag, SampleExpert.DEL_FLAG_NORMAL)
                    .eq(ExpertCondition::getSampleId, bean.getId())
                    .orderByAsc(ExpertCondition::getCreateDate);
            List<ExpertCondition> experts = expertConditionMapper.selectList(queryWrapper);
            //是否可以补抽
            boolean hasNotJoin = false;
            for (ExpertCondition ExpertCondition : experts) {
                if("2".equals(ExpertCondition.getConfirmFlag())){
                   hasNotJoin = true;
                   break;
                }
            }
            Object batchBean = new DynamicBean.Builder().setPV("id", bean.getId())
                    .setPV("projectName", bean.getProjectName())
                    .setPV("baseCategoryIds", bean.getBaseCategoryIds())
                    .setPV("baseCategoryNames",baseCategoryNames,List.class)
                    .setPV("reviewDate", DateUtils.formatDate(bean.getReviewDate(), "yyyy-MM-dd"))
                    .setPV("status", bean.getStatus())
                    .setPV("remark", bean.getRemark())
                    .setPV("result", bean.getResult())
                    .setPV("hasNotJoin", hasNotJoin)
                    .setPV("createDate", DateUtils.formatDate(bean.getCreateDate(), "yyyy-MM-dd HH:mm:ss")).build().getObject();
            batchList.add(batchBean);
        }

        Object data = new DynamicBean.Builder()
                .setPV("total", page.getCount())
                .setPV("list", batchList, List.class)
                .build().getObject();

        return R.ok(data);
    }

    /**
     * 查询专家参与的评审
     */
    @PreAuthorize("hasAuthority('specialist:review:review')")
    @RequestMapping(value = "/searchExpertSamples")
    public R<Object> searchExpertSamples(String expertId,
                                  @RequestParam(value = "page", defaultValue = "1") int pageNo,
                                  @RequestParam(value = "limit", defaultValue = "10") int limit) {

        //统计专家评审项目数
        List<Sample> allSamples = sampleService.findAll();
        List<SampleExpert> allSampleExperts = sampleExpertService.findAll();
        Map<String,Sample> sampleMap = new HashMap<>();
        for(Sample sample:allSamples){
            sampleMap.put(sample.getId(),sample);
        }
        List<SampleExpert> sampleExperts = new ArrayList<>();
        for(SampleExpert sampleExpert:allSampleExperts){
            String eId = sampleExpert.getExpertId();
            Sample sample = sampleMap.get(sampleExpert.getSampleId());
            if(StringUtils.isBlank(eId)||sample==null){
                continue;
            }
            sampleExperts.add(sampleExpert);
        }
        //依据搜索条件过滤
        List<SampleExpert> filterList = sampleExperts;
        if(StringUtils.isNotBlank(expertId)){
            List<SampleExpert> tmpList = new ArrayList<>();
            for(SampleExpert sampleExpert:filterList){
                if(expertId.equals(sampleExpert.getExpertId())){
                    tmpList.add(sampleExpert);
                }
            }
            filterList = tmpList;
        }

        //排序
        Collections.sort(filterList, new Comparator<SampleExpert>() {
            @Override
            public int compare(SampleExpert o1, SampleExpert o2) {
                Sample sample1 = sampleMap.get(o1.getSampleId());
                Sample sample2 = sampleMap.get(o2.getSampleId());
                if(sample1.getProjectName().compareTo(sample2.getProjectName())==0){
                    return o1.getExpertId().compareTo(o2.getExpertId());
                }
                return sample1.getProjectName().compareTo(sample2.getProjectName());
            }
        });

        //内存分页
        List<SampleExpert> pageList = new ArrayList<>();
        int start = (pageNo-1)*limit;
        int end = start + limit-1;
        for(int i=start;i>=0&&i<=end&&i<filterList.size();i++){
            pageList.add(filterList.get(i));
        }

        // 构造返回数据
        List<Object> results = new ArrayList<>();
        for (SampleExpert sampleExpert : pageList) {
            Sample sample = sampleMap.get(sampleExpert.getSampleId());
            Expert expert = expertService.get(sampleExpert.getExpertId());
            Object o = new DynamicBean.Builder()
                    .setPV("sampleExpertId", sampleExpert.getId())
                    .setPV("expertName",  expert.getName())
                    .setPV("expertOrgName", expert.getOrgName())
                    .setPV("expertMobile", expert.getMobile())
                    .setPV("projectName", sample.getProjectName())
                    .setPV("sampleStatus", sample.getStatus())
                    .setPV("sampleStatusLabel", SampleStatusEnum.getProjectStatusEnumByValue(sample.getStatus()).getShortLabel())
                    .setPV("sampleReviewDate", DateUtils.formatDate(sample.getReviewDate(), "yyyy-MM-dd"))
                    .setPV("confirmFlag", sampleExpert.getConfirmFlag())
                    .setPV("createDate",  DateUtils.formatDate(sampleExpert.getCreateDate(), "yyyy-MM-dd HH:mm:ss"))
                    .build().getObject();
            results.add(o);
        }

        Object data = new DynamicBean.Builder()
                .setPV("total", filterList.size())
                .setPV("list", results, List.class)
                .build().getObject();
        return R.ok(data);
    }

    /**
     * 下载专家申请登记表
     */
    @RequestMapping("/downExpert")
    public void downExpert(String id, HttpServletResponse response) throws Exception{
        if (StringUtils.isBlank(id)) {
            throw new ServiceException(SP_EXPERT_NOT_EXIST);
        }

        ExpertJson expertJson = expertJsonService.getExpertJson(id);
        String birthday = expertJson.getBirthday()!=null?DateUtils.formatDate(expertJson.getBirthday(),"yyyy-MM-dd"):"";
        String fileName = "专家申请登记表";
        if(expertJson!=null){
            fileName = expertJson.getName()+"_"+"专家申请登记表.docx";
        }
        expertJson.setPost(expertJson.getPost()!=null?expertJson.getPost():"");
        Map<String, Object> root = new HashMap<>();
        root.put("expert", expertJson);
        root.put("birthday",birthday);
        root.put("post",StringUtils.isBlank(expertJson.getPost())?" ":expertJson.getPost());
        root.put("sex",expertJson.getSex().equals("1")?"男":"女");
        root.put("category1"," ");
        root.put("category2"," ");
        root.put("category3"," ");
        root.put("category4"," ");
        root.put("category5"," ");
        root.put("category6"," ");
        List<ExpertCategory> categories = expertJson.getCategories();
        for (ExpertCategory s : categories) {
            BaseCategory baseCategory = baseCategoryService.get(s.getCategoryId());
            if(baseCategory==null){
                continue;
            }
            switch (baseCategory.getName()){
                case "创新驱动助力类":
                    root.put("category1","✓");
                    break;
                case "公众科学素质建设类":
                    root.put("category2","✓");
                    break;
                case "学术交流类":
                    root.put("category3","✓");
                    break;
                case "科技团队和人才队伍建设类":
                    root.put("category4","✓");
                    break;
                case "决策咨询类":
                    root.put("category5","✓");
                    break;
                case "青少年科技教育类":
                    root.put("category6","✓");
                    break;

            }
        }
        ServletOutputStream outputStream = null;
        try {
            XWPFDocument doc = WordExportUtil.exportWord07(Paths.get(SpringContextUtil.getProperty("myself.classpath"),"reports","专家申请登记表.docx").toString(),root);
            List<XWPFTable> tables = doc.getTables();
            XWPFTable table = null;
            for(XWPFTable t:tables){
                String text =t.getText();
                if(text.contains("性别")&&text.contains("出生年月")){
                    table = t;
                    break;
                }
            }
            if(table!=null){
                XWPFTableRow row = table.getRow(0);
                XWPFParagraph newPara = row.getCell(6).getParagraphs().get(0);
                newPara.setAlignment(ParagraphAlignment.CENTER);//居中
                XWPFRun newParaRun = newPara.createRun();
                FileInfo fileInfo = fileService.get(expertJson.getPictureFileId());
                if(fileInfo!=null){
                    newParaRun.addPicture(new FileInputStream(fileInfo.getAbsoluteAddress()),XWPFDocument.PICTURE_TYPE_PNG,fileInfo.getName(), Units.toEMU(100), Units.toEMU(150));
                }
            }
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            response.setCharacterEncoding("UTF-8");
            outputStream = response.getOutputStream();
            doc.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(outputStream!=null){
                    outputStream.flush();
                    outputStream.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 新增
     * @param projectName          项目名称
     // * @param baseCategoryIds      分类
     * @param reviewDate           评审时间
     * @param remark               简要描述
     * @return
     */
    @LoggerMonitor(value = "【专家库】专家抽取-新增")
    @PreAuthorize("hasAuthority('specialist:sample:add')")
    @RequestMapping(value = "/create")
    public R<Object> create(String projectName, @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd")Date reviewDate, String remark) {
        QueryWrapper<Sample> sampleQueryWrapper = new QueryWrapper<>();
        sampleQueryWrapper.eq("project_name",projectName);
        if (sampleMapper.selectCount(sampleQueryWrapper) > 0) {
            return R.fail("会议名称已存在！");
        } else {
            sampleService.create(projectName,reviewDate, remark);
            return R.ok();
        }
    }

    /**
     * 编辑
     */
    @LoggerMonitor(value = "【专家库】专家抽取-编辑")
    @PreAuthorize("hasAuthority('specialist:sample:edit')")
    @RequestMapping(value = "/update")
    public R<Object> update(String id, String projectName, @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd")Date reviewDate, String remark) {
        sampleService.update(id, projectName,reviewDate, remark);
        return R.ok();
    }

    /**
     * 计划抽取人数(因为需要设置分类下抽取人数，此字段可以不再设置)
     * @param number          计划抽取人数
     * @return
     */
    @LoggerMonitor(value = "【专家库】专家抽取-计划抽取人数")
    @PreAuthorize("hasAuthority('specialist:sample:edit')")
    @RequestMapping(value = "/setNumber")
    public R<Object> create(String id, Integer number) {
        sampleService.setNumber(id, number);
        return R.ok();
    }

    /**
     * 抽取
     */
    @LoggerMonitor(value = "【专家库】专家抽取-抽取")
    @PreAuthorize("hasAuthority('specialist:sample:sample')")
    @RequestMapping(value = "/sampleExpert")
    public R<Object> sampleExpert(String id) {
        sampleService.sampleExpert(id);
        return R.ok();
    }

    /**
     * 确认是否参加评审
     * @param expertConditionId  评审抽取专家id
     * @param confirmFlag  1参加 2：不参加
     */
    @LoggerMonitor(value = "【专家库】专家抽取-确认是否参加评审")
    @PreAuthorize("hasAuthority('specialist:sample:edit')")
    @RequestMapping(value = "/expertConfirm")
    public R<Object> expertConfirm(String expertConditionId,String confirmFlag,@RequestParam(required = false)  @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")Date confirmDate) {
        sampleService.expertConfirm(expertConditionId,confirmFlag,confirmDate);
        return R.ok();
    }

    /**
     * 删除
     */
    @LoggerMonitor(value = "【专家库】专家抽取-删除")
    @PreAuthorize("hasAuthority('specialist:sample:del')")
    @RequestMapping(value = "/delete")
    public R<Object> delete(String id) {
        sampleService.delete(id);
        return R.ok();
    }

    @LoggerMonitor(value = "【专家库】专家抽取-删除")
    @RequestMapping(value = "/deleteSample")
    public R<Object> deleteSample(String id) {
        //删除组别
        UpdateWrapper<Sample> sampleUpdateWrapper = new UpdateWrapper<>();
        sampleUpdateWrapper.eq("id",id);
        sampleUpdateWrapper.set("del_flag",1);
        //删除组别名称
        UpdateWrapper<SampleName> sampleNameUpdateWrapper = new UpdateWrapper<>();
        sampleNameUpdateWrapper.eq("sample_id",id);
        sampleNameUpdateWrapper.set("del_flag",1);
        //删除组别名称对应条件
        UpdateWrapper<SampleCondition> sampleConditionUpdateWrapper = new UpdateWrapper<>();
        sampleConditionUpdateWrapper.eq("sample_id",id);
        sampleConditionUpdateWrapper.set("del_flag",1);
        //删除抽取条件
        UpdateWrapper<ExpertCondition> expertConditionUpdateWrapper = new UpdateWrapper<>();
        expertConditionUpdateWrapper.eq("sample_id",id);
        expertConditionUpdateWrapper.set("del_flag",1);
        sampleNameMapper.update(null,sampleNameUpdateWrapper);
        sampleConditionMapper.update(null,sampleConditionUpdateWrapper);
        expertConditionMapper.update(null,expertConditionUpdateWrapper);
        sampleMapper.update(null,sampleUpdateWrapper);
        return R.ok();
    }

}
