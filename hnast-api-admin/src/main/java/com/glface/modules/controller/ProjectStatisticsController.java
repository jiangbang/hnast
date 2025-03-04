package com.glface.modules.controller;

import com.glface.base.bean.DynamicBean;
import com.glface.base.bean.R;
import com.glface.base.utils.DateUtils;
import com.glface.base.utils.StringUtils;
import com.glface.common.utils.SpringContextUtil;
import com.glface.modules.model.*;
import com.glface.modules.service.*;
import lombok.extern.slf4j.Slf4j;
import net.sf.jxls.transformer.XLSTransformer;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.file.Paths;
import java.util.*;

/**
 * 统计
 */
@Slf4j
@RestController
@RequestMapping("/project/statistics")
public class ProjectStatisticsController {
    @Resource
    private ProjectStatisticsService statisticsService;
    @Resource
    private ProjectCategoryService projectCategoryService;
    @Resource
    private ProjectPlanTypeService projectPlanTypeService;
    @Resource
    private ProjectOrgService projectOrgService;
    @Resource
    private ProjectBatchService projectBatchService;
    @Resource
    private ProjectSpecialService projectSpecialService;
    /**
     * 项目搜索
     * @param batchId   批次id
     * @param planTypeOfficeId 归口部门
     * @param projectStatus  项目状态  逗号分割
     */
    @PreAuthorize("hasAuthority('project:statistics:search')")
    @RequestMapping(value = "/searchProject")
    public R<Object> searchProject(String batchId,String planTypeOfficeId,String projectStatus,String planTypeId,String categoryId,String specialId,
                                           @RequestParam(value = "page", defaultValue = "1") int pageNo,
                                           @RequestParam(value = "limit", defaultValue = "10") int limit) {

        List<Project> projects = statisticsService.searchProject(batchId,planTypeOfficeId,projectStatus,planTypeId,categoryId,specialId);
        //排序
        Collections.sort(projects, new Comparator<Project>() {
            @Override
            public int compare(Project o1, Project o2) {
                if(o1.getApplyDate().after(o2.getApplyDate())){
                    return -1;
                }else if(o1.getApplyDate().before(o2.getApplyDate())){
                    return 1;
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
            ProjectSpecial projectSpecial = projectSpecialService.get(p.getSpecial());

            ProjectOrg org = projectOrgService.get(p.getOrgId());

            Object o = new DynamicBean.Builder()
                    .setPV("id", p.getId())
                    .setPV("code",  p.getCode())
                    .setPV("name", p.getName())
                    .setPV("organization",org, ProjectOrg.class)
                    .setPV("category", projectCate, ProjectCategory.class)
                    .setPV("planType", projectPlanType, ProjectPlanType.class)
                    .setPV("budget", p.getBudget())
                    .setPV("fund", p.getFunds())
                    .setPV("status", p.getStatus())
                    .setPV("special",projectSpecial, ProjectSpecial.class)
                    .setPV("applyDate", DateUtils.formatDate(p.getApplyDate(), "yyyy-MM-dd"))
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
     * 导出excel
     * @param batchId
     * @param planTypeOfficeId
     * @param projectStatus  逗号分割
     * @param response
     */
    @PreAuthorize("hasAuthority('project:statistics:search')")
    @RequestMapping(value = "/searchProjectExport")
    public void searchProjectExport(String batchId,String planTypeOfficeId,String projectStatus, String planTypeId,String categoryId,String specialId,HttpServletResponse response){

        //计划类型map
        List<ProjectPlanType> planTypes = projectPlanTypeService.all();
        Map<String,ProjectPlanType> planTypeMap = new HashMap<>();
        for(ProjectPlanType planType:planTypes){
            planTypeMap.put(planType.getId(),planType);
        }
        //所有专项
        List<ProjectSpecial> specials = projectSpecialService.all();
        Map<String,ProjectSpecial> specialMap = new HashMap<>();
        for(ProjectSpecial special:specials){
            specialMap.put(special.getId(),special);
        }
        //所有类别
        List<ProjectCategory> categories = projectCategoryService.all();
        Map<String,ProjectCategory> categoryMap = new HashMap<>();
        for(ProjectCategory category:categories){
            categoryMap.put(category.getId(),category);
        }
        //所有申请单位
        Set<ProjectOrg> orgs = projectOrgService.all();
        Map<String,ProjectOrg> orgMap = new HashMap<>();
        for(ProjectOrg org:orgs){
            orgMap.put(org.getId(),org);
        }

        List<Project> projects = statisticsService.searchProject(batchId,planTypeOfficeId,projectStatus,planTypeId,categoryId,specialId);
        //排序
        Collections.sort(projects, new Comparator<Project>() {
            @Override
            public int compare(Project o1, Project o2) {
                if(o1.getApplyDate().after(o2.getApplyDate())){
                    return 1;
                }else if(o1.getApplyDate().before(o2.getApplyDate())){
                    return -1;
                }
                return  0;
            }
        });
        //构造数据
        Map<String, Object> root = new HashMap<String, Object>();
        ProjectBatch batch = new ProjectBatch();
        if(StringUtils.isNotBlank(batchId)){
            batch = projectBatchService.get(batchId);
        }
        root.put("batch", batch);
        BigDecimal sum = new BigDecimal(0);//申请总金额
        List<ProjectExcelEntity> projectExcelEntityList = new ArrayList<>();
        for (Project pro : projects) {
            ProjectExcelEntity projectExcelEntity = new ProjectExcelEntity();
            projectExcelEntity.setName(pro.getName());
            projectExcelEntity.setCode(pro.getCode());

            String planTypeName = "";//计划类型名称
            if(StringUtils.isNotBlank(pro.getPlanTypeId())){
                ProjectPlanType planType = planTypeMap.get(pro.getPlanTypeId());
                if(planType!=null){
                    planTypeName = planType.getName();
                }
            }

            String specialName = "";//专项名称
            if(StringUtils.isNotBlank(pro.getSpecial())){
                ProjectSpecial special = specialMap.get(pro.getSpecial());
                if(special!=null){
                    specialName = special.getName();
                }
            }

            String categoryName = "";//类别
            if(StringUtils.isNotBlank(pro.getCategoryId())){
                ProjectCategory category = categoryMap.get(pro.getCategoryId());
                if(category!=null){
                    categoryName = category.getName();
                }
            }

            String orgName="";//申报单位
            if(StringUtils.isNotBlank(pro.getOrgId())){
                ProjectOrg org = orgMap.get(pro.getOrgId());
                if(org!=null){
                    orgName = org.getOrgName();
                }
            }

            projectExcelEntity.setSpecialName(specialName);
            projectExcelEntity.setPlanTypeName(planTypeName);
            projectExcelEntity.setCategoryName(categoryName);
            projectExcelEntity.setOrgName(orgName);
            projectExcelEntity.setFunds(pro.getFunds());

            sum = sum.add(new BigDecimal(String.valueOf(pro.getFunds())));

            projectExcelEntityList.add(projectExcelEntity);
        }

        root.put("list", projectExcelEntityList);
        root.put("sum", sum.toString());
        Map<String, Map<String, Object>> beanParams = new HashMap<String, Map<String, Object>>();
        beanParams.put("statistics", root);
        XLSTransformer former = new XLSTransformer();
        InputStream in = null;
        OutputStream out=null;
        try {
            String fileName = "项目申报统计"+ DateUtils.getDate("yyyyMMddHHmmss") +  ".xlsx";
            File statisticsFile = new File(Paths.get(SpringContextUtil.getProperty("myself.classpath"),"reports","statistics.xlsx").toString());
            in = new FileInputStream(statisticsFile);
            Workbook workbook = former.transformXLS(in, beanParams);
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            response.setCharacterEncoding("UTF-8");
            out=response.getOutputStream();
            workbook.write(out);
            out.flush();
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }finally {
            if(in!=null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(out!=null){
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 项目批次统计
     * @param batchId   批次id
     */
    @PreAuthorize("hasAuthority('project:statistics:search')")
    @RequestMapping(value = "/summaryByBatchId")
    public R<Object> summaryByBatchId(String batchId) {
        ProjectBatchSummary summary = statisticsService.summaryByBatchId(batchId);

        // 构造返回数据
        Object result = new DynamicBean.Builder()
                .setPV("total", summary.getTotal())
                .setPV("waitFirst", summary.getWaitFirst())
                .setPV("recommend",  summary.getRecommend())
                .setPV("expert",summary.getExpert())
                .setPV("materials", summary.getMaterials())
                .build().getObject();
        return R.ok(result);
    }

}
