package com.glface.modules.controller;

import cn.afterturn.easypoi.word.WordExportUtil;
import com.glface.base.bean.DynamicBean;
import com.glface.base.bean.Page;
import com.glface.base.bean.R;
import com.glface.base.utils.AntiSQLInjectionUtil;
import com.glface.base.utils.DateUtils;
import com.glface.base.utils.NamingStrategyUtils;
import com.glface.base.utils.StringUtils;
import com.glface.common.exeception.ServiceException;
import com.glface.common.utils.SpringContextUtil;
import com.glface.common.web.ApiCode;
import com.glface.log.LoggerMonitor;
import com.glface.model.SysArea;
import com.glface.model.SysOffice;
import com.glface.model.SysUser;
import com.glface.modules.model.*;
import com.glface.modules.model.json.ProjectJson;
import com.glface.modules.model.json.ProjectStageJson;
import com.glface.modules.service.*;
import com.glface.modules.sys.utils.UserUtils;
import com.glface.modules.utils.ProjectStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Paths;
import java.util.*;

import static com.glface.common.web.ApiCode.*;

@Slf4j
@RestController
@RequestMapping("/project")
public class ProjectController {

    @Value("${myself.filePreviewDir}")
    private String filePreviewDir;

    @Resource
    private ProjectService projectService;
    @Resource
    private ProjectCategoryService projectCategoryService;
    @Resource
    private ProjectCategoryService cateService;
    @Resource
    private ProjectPlanTypeService planTypeService;
    @Resource
    private ProjectBatchService batchService;
    @Resource
    private ProjectSpecialService specialService;
    @Resource
    private ProjectOrgService orgService;
    @Resource
    private OfficeService officeService;
    @Resource
    private AreaService areaService;
    @Resource
    private SubjectContentService subjectContentService;
    @Resource
    private ProjectContentService projectContentService;
    @Resource
    private ProjectStageService stageService;
    @Resource
    private ProjectFundsService fundsService;
    @Resource
    private ProjectFileService projectFileService;
    @Resource
    private FileService fileService;
    @Resource
    private DictService dictService;
    @Resource
    private ProjectProcessService projectProcessService;

    @Resource
    private ProjectJsonService projectJsonService;


    @Resource
    private UserService userService;

    /**
     * 所有项目类别
     */
    @RequestMapping(value = "/allCategories")
    public R<Object> allCategories() {
        List<ProjectCategory> categories = projectCategoryService.all();
        List<Object> dataList = new DynamicBean.Builder().setPV("id", null)
                .setPV("name", null)
                .build().copyList(categories);
        return R.ok(dataList);
    }

    /**
     * 所有计划类型
     */
    @RequestMapping(value = "/allPlanTypes")
    public R<Object> allPlanTypes() {
        List<ProjectPlanType> planTypes = planTypeService.all();
        List<Object> dataList = new DynamicBean.Builder().setPV("id", null)
                .setPV("name", null)
                .setPV("officeId", null)
                .setPV("code", null)
                .setPV("sort", null)
                .build().copyList(planTypes);
        return R.ok(dataList);
    }

    /**
     * 所有批次信息
     */
    @RequestMapping(value = "/allSpecials")
    public R<Object> allSpecials() {
        List<ProjectSpecial> batches = specialService.all();
        // 构造返回数据
        List<Object> specialList = new ArrayList<>();
        for (ProjectSpecial s : batches) {
            Object o = new DynamicBean.Builder().setPV("id", s.getId())
                    .setPV("name", s.getName())
                    .setPV("planTypeId", s.getPlanTypeId())
                    .setPV("remark", s.getRemark())
                    .setPV("updateDate", DateUtils.formatDate(s.getUpdateDate(), "yyyy-MM-dd HH:mm:ss"))
                    .setPV("createDate", DateUtils.formatDate(s.getCreateDate(), "yyyy-MM-dd HH:mm:ss")).build().getObject();
            specialList.add(o);
        }
        return R.ok(specialList);
    }

    /**
     * 所有项目专项
     */
    @RequestMapping(value = "/allBatchs")
    public R<Object> allBatchs() {
        List<ProjectBatch> batches = batchService.all();
        // 构造返回数据
        List<Object> batchList = new ArrayList<>();
        for (ProjectBatch b : batches) {
            Object batchBean = new DynamicBean.Builder().setPV("id", b.getId())
                    .setPV("year", b.getYear())
                    .setPV("number", b.getNumber(), String.class)
                    .setPV("startTime", DateUtils.formatDate(b.getStartTime(), "yyyy-MM-dd"))
                    .setPV("endTime", DateUtils.formatDate(b.getEndTime(), "yyyy-MM-dd"))
                    .setPV("status", b.getStatus())
                    .setPV("remark", b.getRemark())
                    .setPV("createDate", DateUtils.formatDate(b.getCreateDate(), "yyyy-MM-dd HH:mm:ss")).build().getObject();
            batchList.add(batchBean);
        }
        return R.ok(batchList);
    }
    /**
     * 项目基础信息
     * 如果id不为空则查询项目基础信息资料
     */
    @PreAuthorize("hasAuthority('project:project:view')")
    @RequestMapping(value = "/base")
    public R<Object> baseInfo(String id){
        Project project = projectService.get(id);
        //项目类别
        List<ProjectCategory> categories = projectCategoryService.all();
        //计划类型
        List<ProjectPlanType> planTypes = planTypeService.allListTree();
        //最近批次
        ProjectBatch latestBatch = batchService.latest();
        //区域信息 B类项目选择
        //List<SysArea> sysAreaList = areaService.allListTree();
        //构造返回数据
        if(project==null){
            project = new Project();
        }
        DynamicBean.Builder builder = new DynamicBean.Builder().setPV("id", project.getId())
                .setPV("name", project.getName())
                .setPV("categoryId", project.getCategoryId())
                .setPV("planTypeId", project.getPlanTypeId())
                .setPV("specialId", project.getSpecial())
                .setPV("batchId", project.getBatchId())
                .setPV("batchYear", "")
                .setPV("status", project.getStatus())
                .setPV("qxOfficeId", project.getQxOfficeId())
                .setPV("batchNumber","");
        if(StringUtils.isNotBlank(project.getBatchId())){
            ProjectBatch batch = batchService.get(project.getBatchId());
            if(batch!=null){
                builder.setPV("batchYear", batch.getYear())
                        .setPV("batchNumber",batch.getNumber());
            }
        }
        if(StringUtils.isNotBlank(project.getPlanTypeId())){
            ProjectPlanType projectPlanType = planTypeService.get(project.getPlanTypeId());
            if(projectPlanType!=null){
                builder.setPV("planTypeCode", projectPlanType.getCode());
            }
        }
        if(StringUtils.isNotBlank(project.getCategoryId())){
            ProjectCategory category = cateService.get(project.getCategoryId());
            if(category!=null){
                builder.setPV("categoryName", category.getName());
            }
        }
        if(latestBatch==null) builder.setPV("latestBatch", null);
        else builder.setPV("latestBatch", latestBatch, ProjectBatch.class);

        SysUser currentUser = UserUtils.getUser();
        if(projectProcessService.isManager(currentUser.getId())){
            builder.setPV("isDistrictManager", true);
        }else {
            builder.setPV("isDistrictManager", projectProcessService.isDistrictUser(currentUser.getId())!=null?true:false);
        }
        //builder.setPV("areas",sysAreaList, List.class);
        builder.setPV("categories",new DynamicBean.Builder().setPV("name", null)
                .setPV("id", null)
                .setPV("amountMax", null)
                .setPV("amountMin", null)
                .setPV("remark", null).build().copyList(categories),List.class);
        builder.setPV("planTypes",planTypes,List.class);
        return R.ok(builder.build().getObject());
    }

    /**
     * 保存或新建项目基础信息
     * @param id   如果id不为空则是修改信息
     * @param name 项目名称
     * @param batchId 批次id
     * @param categoryId 项目类别
     * @param planTypeId 计划类型
     */
    @LoggerMonitor(value = "我的项目-保存基础信息")
    @PreAuthorize("hasAuthority('project:project:add')")
    @RequestMapping(value = "/saveBase")
    public R<Object> saveBase(String id,String batchId,String name,String categoryId,String planTypeId,String specialId,@RequestParam(value = "qxOfficeId", defaultValue = "") String qxOfficeId) {
        Project project = projectService.saveBase(id,batchId,name,categoryId,planTypeId,specialId,qxOfficeId);
        DynamicBean.Builder builder = new DynamicBean.Builder().setPV("id", project.getId());
        return R.ok(builder.build().getObject());
    }

    /**
     * 分页查询我创建的申报单位
     * @param pageNo  查询分页
     * @param limit 查询数
     */
    @RequestMapping(value = "/searchOrg")
    public R<Object> searchOrg(String orgName,
                            @RequestParam(value = "page", defaultValue = "1") int pageNo,
                            @RequestParam(value = "limit", defaultValue = "10") int limit,
                            @RequestParam(value = "order", defaultValue = "createDate desc") String order) {
        order = NamingStrategyUtils.underscoreName(AntiSQLInjectionUtil.filter(order.trim()));
        order = "t." + order;
        // 设置查询条件
        Page<ProjectOrg> page = new Page<>(pageNo,limit);
        page.setOrderBy(order);
        ProjectOrg projectOrg = new ProjectOrg();
        projectOrg.setOrgName(orgName);
        projectOrg.setCreateBy(UserUtils.getUserId());

        // 查询
        page = orgService.pageSearch(page,projectOrg);

        // 构造返回数据
        List<Object> batchList = new ArrayList<>();
        for (ProjectOrg o : page.getList()) {
            Object orgBean = new DynamicBean.Builder().setPV("id", o.getId())
                    .setPV("orgName", o.getOrgName())
                    .setPV("chargeName", o.getChargeName())
                    .setPV("chargeMobile", o.getChargeMobile())
                    .setPV("chargeEmail", o.getChargeEmail())
                    .setPV("orgAddress", o.getOrgAddress())
                    .setPV("chargeTitle", o.getChargeTitle())
                    .setPV("orgPhone", o.getOrgPhone())
                    .setPV("orgFax", o.getOrgFax())
                    .setPV("orgPost", o.getOrgPost())
                    .build().getObject();
            batchList.add(orgBean);
        }

        Object data = new DynamicBean.Builder()
                .setPV("total", page.getCount())
                .setPV("orgs", batchList, List.class)
                .build().getObject();
        return R.ok(data);
    }

    /**
     * 通过项目id查询项目申报单位信息
     * @param id  项目id
     */
    @PreAuthorize("hasAuthority('project:project:view')")
    @RequestMapping(value = "/org")
    public R<Object> orgInfo(String id){
        if (StringUtils.isBlank(id)) {
            throw new ServiceException(PROJECT_NOT_EXIST);
        }
        Project project = projectService.get(id);
        if(project==null){
            throw new ServiceException(PROJECT_NOT_EXIST);
        }
        String orgId = project.getOrgId();
        ProjectOrg projectOrg = null;
        if(StringUtils.isNotBlank(orgId)){
            projectOrg = orgService.get(orgId);
        }
        if(projectOrg==null){
            projectOrg = new ProjectOrg();
        }
        Object orgBean = new DynamicBean.Builder().setPV("id", projectOrg.getId())
                .setPV("orgName", projectOrg.getOrgName())
                .setPV("chargeName", projectOrg.getChargeName())
                .setPV("chargeMobile", projectOrg.getChargeMobile())
                .setPV("chargeEmail", projectOrg.getChargeEmail())
                .setPV("orgAddress", projectOrg.getOrgAddress())
                .setPV("chargeTitle", projectOrg.getChargeTitle())
                .setPV("orgPhone", projectOrg.getOrgPhone())
                .setPV("orgFax", projectOrg.getOrgFax())
                .setPV("orgPost", projectOrg.getOrgPost())
                .build().getObject();
        return R.ok(orgBean);
    }

    /**
     * 保存申报单位信息
     * 注意：项目的orgId有可能发生变化
     * @param id         项目id
     * @param orgId      申报单位id
     * @param orgName          单位名称
     * @param chargeName       项目负责人
     * @param chargeTitle      职称/职务
     * @param orgPhone          单位电话
     * @param chargeMobile     手机
     * @param chargeEmail      电子邮箱
     * @param orgFax           传真
     * @param orgAddress       单位地址
     * @param orgPost          邮政编码
     * @return
     */
    @LoggerMonitor(value = "我的项目-保存申报单位")
    @PreAuthorize("hasAuthority('project:project:add')")
    @RequestMapping(value = "/saveOrg")
    public R<Object> saveOrg(String id,String orgId,String orgName,String chargeName,String chargeTitle,String chargeMobile,String chargeEmail,String orgPhone,String orgFax,String orgAddress,String orgPost) {
        Project project = projectService.saveOrg(id,orgId,orgName,chargeName,chargeTitle,chargeMobile,chargeEmail,orgPhone,orgFax,orgAddress,orgPost);
        DynamicBean.Builder builder = new DynamicBean.Builder()
                .setPV("id", project.getId())
                .setPV("orgId", project.getOrgId());
        return R.ok(builder.build().getObject());
    }

    /**
     * 通过项目id查询项目申报内容
     * @param id  项目id
     */
    @PreAuthorize("hasAuthority('project:project:view')")
    @RequestMapping(value = "/content")
    public R<Object> contentInfo(String id){
        if (StringUtils.isBlank(id)) {
            throw new ServiceException(PROJECT_NOT_EXIST);
        }
        Project project = projectService.get(id);
        if(project==null){
            throw new ServiceException(PROJECT_NOT_EXIST);
        }
        ProjectContent projectContent =  projectContentService.findByProjectId(project.getId());
        if(projectContent==null){
            projectContent = new ProjectContent();
        }
        //项目实施阶段明细
        List<ProjectStage> stages = stageService.findByProjectId(project.getId());
        Collections.sort(stages, new Comparator<ProjectStage>() {
            @Override
            public int compare(ProjectStage o1, ProjectStage o2) {
                if(o1.getSort()>o2.getSort()){
                    return 1;
                }else if(o1.getSort()<o2.getSort()){
                    return -1;
                }
                return  0;
            }
        });
        //经费预算明细
        List<ProjectFunds> funds = fundsService.findByProjectId(project.getId());
        Collections.sort(funds, new Comparator<ProjectFunds>() {
            @Override
            public int compare(ProjectFunds o1, ProjectFunds o2) {
                if(o1.getSort()>o2.getSort()){
                    return 1;
                }else if(o1.getSort()<o2.getSort()){
                    return -1;
                }
                return  0;
            }
        });
        //返回数据
        List<Object> stageList = new ArrayList<>();
        for(ProjectStage s:stages){
            Object bean = new DynamicBean.Builder().setPV("id", s.getId())
                    .setPV("name", s.getName())
                    .setPV("startDate", DateUtils.formatDate(s.getStartDate(), "yyyy-MM-dd"))
                    .setPV("endDate", DateUtils.formatDate(s.getEndDate(), "yyyy-MM-dd"))
                    .setPV("money",String.valueOf(s.getMoney()))
                    .setPV("remark",s.getRemark())
                    .build().getObject();
            stageList.add(bean);
        }
        List<Object> fundList = new ArrayList<>();
        for(ProjectFunds fund:funds){
            Object bean = new DynamicBean.Builder().setPV("id", fund.getId())
                    .setPV("name", fund.getName())
                    .setPV("money",String.valueOf(fund.getMoney()))
                    .setPV("remark",fund.getRemark())
                    .build().getObject();
            fundList.add(bean);
        }
        Object contentBean = new DynamicBean.Builder().setPV("id", projectContent.getId())
                .setPV("basis", projectContent.getBasis())
                .setPV("content", projectContent.getContent())
                .setPV("target", projectContent.getTarget())
                .setPV("conditions", projectContent.getConditions())
                .setPV("startDate", DateUtils.formatDate(project.getStartDate(), "yyyy-MM-dd"))
                .setPV("endDate", DateUtils.formatDate(project.getEndDate(), "yyyy-MM-dd"))
                .setPV("stages",stageList,List.class)
                .setPV("funds",fundList,List.class)
                .setPV("bank", project.getBank())
                .setPV("cardNo", project.getCardNo())
                .setPV("accounts", project.getAccounts())
                .setPV("budget", project.getBudget())
                .setPV("fund", project.getFunds())

                .build().getObject();
        return R.ok(contentBean);
    }

    /**
     * 暂存内容 不对参数进行验证
     * @param content
     * @return
     */
    @LoggerMonitor(value = "我的项目-暂存内容")
    @PreAuthorize("hasAuthority('project:project:add')")
    @RequestMapping(value = "/cacheContent")
    public R<Object> cacheContent(@RequestBody ProjectParamContent content) {
        Project project = projectService.cacheContent(content);
        DynamicBean.Builder builder = new DynamicBean.Builder().setPV("id", project.getId());
        return R.ok(builder.build().getObject());
    }

    /**
     * 保存内容
     * @param content
     * @return
     */
    @LoggerMonitor(value = "我的项目-保存内容")
    @PreAuthorize("hasAuthority('project:project:add')")
    @RequestMapping(value = "/saveContent")
    public R<Object> saveContent(@RequestBody ProjectParamContent content) {
        Project project = projectService.saveContent(content);
        DynamicBean.Builder builder = new DynamicBean.Builder().setPV("id", project.getId());
        return R.ok(builder.build().getObject());
    }

    /**
     * 通过项目id查询项目附件
     * @param id  项目id
     */
    @PreAuthorize("hasAuthority('project:project:view')")
    @RequestMapping(value = "/file")
    public R<Object> fileInfo(String id){
        if (StringUtils.isBlank(id)) {
            throw new ServiceException(PROJECT_NOT_EXIST);
        }
        Project project = projectService.get(id);
        if(project==null){
            throw new ServiceException(PROJECT_NOT_EXIST);
        }
        List<ProjectFile> financialFiles = projectFileService.findByProjectIdAndType(id, "financial");//财务制度
        List<ProjectFile> manageFiles = projectFileService.findByProjectIdAndType(id, "manage");//项目管理制度
        List<ProjectFile> feasibilityFiles = projectFileService.findByProjectIdAndType(id, "feasibility");//可行性研究报告
        List<ProjectFile> creditFiles = projectFileService.findByProjectIdAndType(id, "credit");//信用代码
        List<ProjectFile> deployFiles = projectFileService.findByProjectIdAndType(id, "deploy");//实施材料

        List<Object> financialList = new DynamicBean.Builder().setPV("id", null)
                .setPV("name", null)
                .setPV("fileId", null)
                .build().copyList(financialFiles);

        List<Object> manageList = new DynamicBean.Builder().setPV("id", null)
                .setPV("name", null)
                .setPV("fileId", null)
                .build().copyList(manageFiles);

        List<Object> feasibilityList = new DynamicBean.Builder().setPV("id", null)
                .setPV("name", null)
                .setPV("fileId", null)
                .build().copyList(feasibilityFiles);

        List<Object> creditList = new DynamicBean.Builder().setPV("id", null)
                .setPV("name", null)
                .setPV("fileId", null)
                .build().copyList(creditFiles);

        List<Object> deployList = new DynamicBean.Builder().setPV("id", null)
                .setPV("name", null)
                .setPV("fileId", null)
                .build().copyList(deployFiles);


        Object bean = new DynamicBean.Builder().setPV("financialList", financialList,List.class)
                .setPV("manageList", manageList,List.class)
                .setPV("feasibilityList", feasibilityList,List.class)
                .setPV("creditList", creditList,List.class)
                .setPV("deployList", deployList,List.class)

                .build().getObject();

        return R.ok(bean);
    }

    /**
     * 项目申报上传文件接口
     * @param id        项目id
     * @param type      附件类型
     * @param onlyOne   此附件类型下是否只允许1个附件
     * @param file
     */
    @LoggerMonitor(value = "我的项目-上传")
    @PostMapping("/upload")
    public R<Object> uploadFile(String id,String type,boolean onlyOne,@RequestParam("file") MultipartFile file) throws Exception{
        if (StringUtils.isBlank(id)) {
            throw new ServiceException(PROJECT_NOT_EXIST);
        }
        if (StringUtils.isBlank(type)) {
            throw new ServiceException(PROJECT_TYPE_REQUIRED);
        }
        Project project = projectService.get(id);
        if(project==null){
            throw new ServiceException(PROJECT_NOT_EXIST);
        }
        FileInfo fileInfo = fileService.uploadFile(file);
        ProjectFile projectFile = projectService.uploadFile(id,type,onlyOne,fileInfo);
        //构造返回数据
        Object object = new DynamicBean.Builder().setPV("projectFileId", projectFile.getId())
                .setPV("fileId", projectFile.getFileId())
                .build().getObject();
        return R.ok(object);
    }

    /**
     * 删除附件
     * @param id
     * @param projectFileId
     * @return
     * @throws Exception
     */
    @LoggerMonitor(value = "我的项目-删除附件")
    @RequestMapping("/deleteFile")
    public R<Object> deleteFile(String id,String projectFileId) {
        if (StringUtils.isBlank(id)) {
            throw new ServiceException(PROJECT_NOT_EXIST);
        }
        Project project = projectService.get(id);
        if(project==null){
            throw new ServiceException(PROJECT_NOT_EXIST);
        }
        projectService.deleteFile(id,projectFileId);
        return R.ok();
    }

    /**
     * 提交项目申报
     * @param id
     * @return
     */
    @LoggerMonitor(value = "我的项目-提交")
    @RequestMapping("/submit")
    public R<Object> submit(String id){
        projectService.submit(id);
        return R.ok();
    }

    /**
     * 生成pdf报告
     */
    @RequestMapping("/downReport")
    public void downReport(String id,HttpServletResponse response) throws Exception{
        if (StringUtils.isBlank(id)) {
            throw new ServiceException(PROJECT_NOT_EXIST);
        }

        ProjectJson projectJson = projectJsonService.getProjectJson(id);
        String fileName = "项目申报表";
        if(projectJson!=null){
            fileName = projectJson.getCode();
            if(StringUtils.isBlank(fileName)){
                fileName = projectJson.getName();
            }
        }

        BufferedInputStream bufferedInputStream = null;
        ServletOutputStream outputStream = null;
        try {
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName+".pdf", "UTF-8"));
            response.setCharacterEncoding("UTF-8");
            bufferedInputStream =new BufferedInputStream(new ByteArrayInputStream(projectJsonService.genPdf(id)));
            outputStream = response.getOutputStream();
            writeBytes(bufferedInputStream, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(outputStream!=null){
                    outputStream.flush();
                    outputStream.close();
                }
                if(bufferedInputStream!=null){
                    bufferedInputStream.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 下载财政支出绩效报告表模板
     */
    @RequestMapping("/downPerformanceTemplate")
    public void downPerformanceTemplate(String id,HttpServletResponse response) throws Exception{
        if (StringUtils.isBlank(id)) {
            throw new ServiceException(PROJECT_NOT_EXIST);
        }

        ProjectJson projectJson = projectJsonService.getProjectJson(id);
        String fileName = "财政支出绩效报告表";
        if(projectJson!=null){
            fileName = projectJson.getName()+projectJson.getCode()+"_"+"财政支出绩效报告表.docx";
        }
        String year = projectJson.getApplyDate()!=null?DateUtils.formatDate(projectJson.getApplyDate(),"yyyy"):"";
        String startYear = projectJson.getStartDate()!=null?DateUtils.formatDate(projectJson.getStartDate(),"yyyy"):"";
        String startMonth = projectJson.getStartDate()!=null?DateUtils.formatDate(projectJson.getStartDate(),"MM"):"";
        String endYear = projectJson.getEndDate()!=null?DateUtils.formatDate(projectJson.getEndDate(),"yyyy"):"";
        String endMonth = projectJson.getEndDate()!=null?DateUtils.formatDate(projectJson.getEndDate(),"MM"):"";
        String startEndDate = DateUtils.formatDate(projectJson.getStartDate(),"yyyy-MM-dd")+"至"+DateUtils.formatDate(projectJson.getEndDate(),"yyyy-MM-dd");
        Map<String, Object> root = new HashMap<>();
        root.put("project", projectJson);
        root.put("year", year);
        root.put("startYear", startYear);
        root.put("startMonth", startMonth);
        root.put("endYear", endYear);
        root.put("endMonth", endMonth);
        root.put("startEndDate", startEndDate);

        ServletOutputStream outputStream = null;
        try {
            XWPFDocument doc = WordExportUtil.exportWord07(Paths.get(SpringContextUtil.getProperty("myself.classpath"),"reports","财政支出绩效报告表模板.docx").toString(),root);
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
     * 下载协议模板
     */
    @RequestMapping("/downAgreementTemplate")
    public void downAgreementTemplate(String id,HttpServletResponse response) throws Exception{
        if (StringUtils.isBlank(id)) {
            throw new ServiceException(PROJECT_NOT_EXIST);
        }

        ProjectJson projectJson = projectJsonService.getProjectJson(id);
        String fileName = "项目协议书";
        if(projectJson!=null){
            fileName = projectJson.getName()+projectJson.getCode()+"_"+"项目协议书.docx";
        }
        String year = projectJson.getApplyDate()!=null?DateUtils.formatDate(projectJson.getApplyDate(),"yyyy"):"";
        String startYear = projectJson.getStartDate()!=null?DateUtils.formatDate(projectJson.getStartDate(),"yyyy"):"";
        String startMonth = projectJson.getStartDate()!=null?DateUtils.formatDate(projectJson.getStartDate(),"MM"):"";
        String endYear = projectJson.getEndDate()!=null?DateUtils.formatDate(projectJson.getEndDate(),"yyyy"):"";
        String endMonth = projectJson.getEndDate()!=null?DateUtils.formatDate(projectJson.getEndDate(),"MM"):"";
        Map<String, Object> root = new HashMap<>();
        root.put("project", projectJson);
        root.put("year", year);
        root.put("startYear", startYear);
        root.put("startMonth", startMonth);
        root.put("endYear", endYear);
        root.put("endMonth", endMonth);

        ServletOutputStream outputStream = null;
        try {
            XWPFDocument doc = WordExportUtil.exportWord07(Paths.get(SpringContextUtil.getProperty("myself.classpath"),"reports","项目协议书模板.docx").toString(),root);
            List<XWPFTable> tables = doc.getTables();
            XWPFTable table = null;
            for(XWPFTable t:tables){
                String text =t.getText();
                if(text.contains("实施阶段")&&text.contains("经费预算")&&text.contains("目标内容")){
                    table = t;
                    break;
                }
            }
            if(table!=null){
                List<ProjectStageJson> stages = projectJson.getStages();
                for(int i=0;i<stages.size();i++){
                    ProjectStageJson stage = stages.get(i);
                    XWPFTableRow row = table.getRow(i+1);
                    row.getCell(0).setText(stage.getName());
                    row.getCell(1).setText(String.valueOf(stage.getMoney()));
                    row.getCell(2).setText(stage.getRemarks());
                    // 设置单元格内段落
                    XWPFParagraph contentParagraph = row.getCell(3).getParagraphs().get(0);
                    XWPFRun run = contentParagraph.createRun();
                    run.setText(DateUtils.formatDate(stage.getStartDate(),"yyyy-MM-dd"));
                    run.addBreak();
                    run.setText(DateUtils.formatDate(stage.getEndDate(),"yyyy-MM-dd"));
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
     * 下载归档文件
     */
    @RequestMapping("/downPlaceFile")
    public void downPlaceFile(String id,HttpServletResponse response) throws Exception{
        if (StringUtils.isBlank(id)) {
            throw new ServiceException(PROJECT_NOT_EXIST);
        }

        //必须是管理员
        SysUser currentUser = UserUtils.getUser();
        if (!projectProcessService.isManager(currentUser.getId())) {
            throw new ServiceException(PROJECT_PROCESS_NO_AUTHORITY);
        }

        Project project = projectService.get(id);
        if(project==null){
            throw new ServiceException(PROJECT_NOT_EXIST);
        }

        String zip = project.getZipPath();
        if(StringUtils.isBlank(zip)){
            throw new ServiceException(PROJECT_ZIP_NOT_EXIST);
        }

        String fileName = project.getName()+project.getCode();

        BufferedInputStream bufferedInputStream = null;
        ServletOutputStream outputStream = null;
        try {
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName+".zip", "UTF-8"));
            response.setCharacterEncoding("UTF-8");
            bufferedInputStream =new BufferedInputStream(new FileInputStream(zip));
            outputStream = response.getOutputStream();
            writeBytes(bufferedInputStream, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(outputStream!=null){
                    outputStream.flush();
                    outputStream.close();
                }
                if(bufferedInputStream!=null){
                    bufferedInputStream.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void writeBytes(InputStream in, OutputStream out) throws IOException {
        byte[] buffer= new byte[1024];
        int length = -1;
        while ((length = in.read(buffer))!=-1){
            out.write(buffer,0,length);
        }
    }

    /**
     * 显示报告
     */
    @RequestMapping("/showReport")
    public R<Object> showReport(String id,HttpServletResponse response) throws Exception{
        if (StringUtils.isBlank(id)) {
            throw new ServiceException(PROJECT_NOT_EXIST);
        }
        String content = projectJsonService.getHtmlContent(id,false);
        Object data = new DynamicBean.Builder()
                .setPV("html", content).build().getObject();
        return R.ok(data);
    }

    /**
     * 查询项目执行过程
     */
    @PreAuthorize("hasAuthority('project:project:view')")
    @RequestMapping(value = "/searchProcesses")
    public R<Object> searchProcesses(String id) {
        List<ProjectProcess> processList = projectProcessService.findByProjectId(id);
        Collections.sort(processList, new Comparator<ProjectProcess>() {
            @Override
            public int compare(ProjectProcess o1, ProjectProcess o2) {
                if(o1.getCreateDate().before(o2.getCreateDate())){
                    return 1;
                }else if(o1.getCreateDate().after(o2.getCreateDate())){
                    return -1;
                }
                return  0;
            }
        });

        // 构造返回数据
        List<Object> results = new ArrayList<>();
        for (ProjectProcess process : processList) {
            String approveUserName = "";//审批人
            SysUser user = userService.get(process.getUserId());
            if(user!=null){
                approveUserName = user.getNickname();
                if(StringUtils.isBlank(approveUserName)){
                    approveUserName = user.getAccount();
                }
            }
            String approveResult = "";//审批结果
            if(StringUtils.isNotBlank(process.getResult())){
                ProjectStatusEnum statusEnum = ProjectStatusEnum.getProjectStatusEnumByValue(process.getResult());
                if(statusEnum!=null){
                    approveResult = statusEnum.getShortLabel();
                }
            }

            String planTypeOfficeId = process.getOfficeId();
            String planTypeOfficeName = "";//归口部门
            if(StringUtils.isNotBlank(planTypeOfficeId)){
                SysOffice planTypeOffice = officeService.get(planTypeOfficeId);
                planTypeOfficeName = planTypeOffice.getName();
            }

            Object orgBean = new DynamicBean.Builder()
                    .setPV("approveUserName", approveUserName)
                    .setPV("approveResult", approveResult)
                    .setPV("planTypeOfficeName", planTypeOfficeName)
                    .setPV("approveOpinion", process.getResultOpinion())
                    .setPV("createDate", DateUtils.formatDate(process.getCreateDate(), "yyyy-MM-dd HH:mm:ss"))
                    .build().getObject();
            results.add(orgBean);
        }
        return R.ok(results);
    }

    /**
     * 获取我的项目列表
     */
    @PreAuthorize("hasAuthority('project:project:view')")
    @RequestMapping(value = "/search")
    public R<Object> search(String categoryId, String planTypeId, String status,String batchId,
                            @RequestParam(value = "page", defaultValue = "1") int pageNo,
                            @RequestParam(value = "limit", defaultValue = "10") int limit,
                            @RequestParam(value = "order", defaultValue = "createDate desc") String order) {
        order = NamingStrategyUtils.underscoreName(AntiSQLInjectionUtil.filter(order.trim()));
        // 设置查询条件
        Page<Project> page = new Page<>(pageNo,limit);
        page.setOrderBy(order);

        Project project = new Project();
        project.setCategoryId(categoryId);
        project.setPlanTypeId(planTypeId);
        project.setStatus(status);
        project.setBatchId(batchId);
        // 查询
        page = projectService.pageSearch(page,project);
        page.setOrderBy(order);

        // 构造返回数据
        List<Object> projects = new ArrayList<>();
        for (Project p : page.getList()) {
            ProjectCategory projectCate = cateService.get(p.getCategoryId());
            ProjectPlanType projectPlanType = planTypeService.get(p.getPlanTypeId());
            ProjectBatch batch = batchService.get(p.getBatchId());
            SysArea area = areaService.get(p.getAreaId());
            String qxOfficeName = "市本级";
            SysOffice qxOffice = officeService.get(p.getQxOfficeId());
            if(qxOffice!=null){
                qxOfficeName = qxOffice.getName();
            }
            //立项内容
            SubjectContent subjectContent = subjectContentService.findByProjectId(p.getId());
            //项目阶段 多个
            List<ProjectStage> stages = stageService.findByProjectId(p.getId());
            //项目经费
            List<ProjectFunds> funds = fundsService.findByProjectId(p.getId());

            ProjectOrg org = orgService.get(p.getOrgId());

            Object o = new DynamicBean.Builder()
                    .setPV("id", p.getId())
                    .setPV("code",  p.getCode())
                    //.setPV("area",  area, SysArea.class)
                    .setPV("qxOfficeId",  p.getQxOfficeId())
                    .setPV("qxOfficeName",  qxOfficeName)
                    .setPV("category", projectCate, ProjectCategory.class)
                    .setPV("planType", projectPlanType, ProjectPlanType.class)
                    .setPV("name", p.getName())
                    .setPV("batch", batch, ProjectBatch.class)
                    .setPV("organization",org, ProjectOrg.class)
                    .setPV("status", p.getStatus())
                    .setPV("stages", stages, List.class)
                    .setPV("funds", funds, List.class)
                    .setPV("subjectContent", subjectContent, SubjectContent.class)
                    .setPV("zipPath", p.getZipPath())

                    .setPV("createDate", DateUtils.formatDate(p.getCreateDate(), "yyyy-MM-dd HH:mm:ss")).build().getObject();
            projects.add(o);
            log.info("数据为----{}",o);
        }
        //是否区县人员
        SysUser currentUser = UserUtils.getUser();
        boolean isDistrictManager = projectProcessService.isDistrictUser(currentUser.getId())!=null?true:false;

        Object data = new DynamicBean.Builder()
                .setPV("total", page.getCount())
                .setPV("projects", projects, List.class)
                .setPV("isDistrictManager", isDistrictManager)
                .build().getObject();
        return R.ok(data);
    }


    @LoggerMonitor(value = "我的项目-新增")
    @PreAuthorize("hasAuthority('project:project:add')")
    @RequestMapping(value = "/create")
    public R<Object> create(String name,String categoryId,
                            String areaId,String batchId,String planTypeId,
                            String orgName,String chargeName,String chargeTitle,String chargeMobile,
                            String chargeEmail,String orgAddress,String orgFax,
                            String orgPost,String content, String conditions, String basis,String target,
                            String stageName,String stageStartDate,String stageEndDate,
                            Float budget,Float funds,
                            String bank,String cardNo,String accounts) {
        Project tempSaveProject = projectService.getTempSave(UserUtils.getUserId());
        if(tempSaveProject==null){
            Project project = projectService.create(name, categoryId,
                    areaId, batchId, planTypeId,
                    orgName, chargeName, chargeTitle, chargeMobile,
                    chargeEmail, orgAddress, orgFax,
                    orgPost, content, conditions, basis, target,
                    stageName, stageStartDate, stageEndDate,
                    budget, funds,
                    bank, cardNo, accounts);
            return R.ok(project);
        }
        return R.fail(ApiCode.PROJECT_CREATE_FAILED_EXIST.getMsg());
    }

    @LoggerMonitor(value = "我的项目-编辑")
    @PreAuthorize("hasAuthority('project:project:edit')")
    @RequestMapping(value = "/update")
    public R<Object> update(String projectId, String name,String categoryId,
                            String areaId,String batchId,String planTypeId,
                            String orgName,String chargeName,String chargeTitle,String chargeMobile,
                            String chargeEmail,String orgPhone, String orgAddress,String orgFax,
                            String orgPost,String content, String conditions, String basis,String target,
                            StageModel stageModel,
                            Float budget,Float funds,
                            String bank,String cardNo,String accounts) {
        projectService.update( projectId, name, categoryId,
                areaId, batchId, planTypeId,
                orgName, chargeName, chargeTitle, chargeMobile,
                chargeEmail,orgPhone, orgAddress, orgFax,
                orgPost, content, conditions, basis,target,
                stageModel,
                budget, funds,
                bank, cardNo, accounts);
        return R.ok();
    }

    /**
     * 删除
     *
     */
    @LoggerMonitor(value = "我的项目-删除")
    @PreAuthorize("hasAuthority('project:project:del')")
    @RequestMapping(value = "/delete")
    public R<Object> delete(String id) {
        projectService.delete(id);
        return R.ok();
    }


}
