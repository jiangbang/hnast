package com.glface.modules.sp.controller;

import com.glface.base.bean.DynamicBean;
import com.glface.base.bean.R;
import com.glface.base.utils.DateUtils;
import com.glface.base.utils.StringUtils;
import com.glface.common.utils.SpringContextUtil;
import com.glface.modules.sp.model.*;
import com.glface.modules.sp.service.BaseCategoryService;
import com.glface.modules.sp.service.ExpertStatisticsService;
import com.glface.modules.sp.service.SampleService;
import com.glface.modules.utils.SampleStatusEnum;
import lombok.extern.slf4j.Slf4j;
import net.sf.jxls.transformer.XLSTransformer;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Paths;
import java.util.*;

/**
 * 统计
 */
@Slf4j
@RestController
@RequestMapping("/specialist/statistics")
public class ExpertStatisticsController extends WebMvcConfigurerAdapter {
    @Resource
    private ExpertStatisticsService statisticsService;
    @Resource
    private BaseCategoryService baseCategoryService;
    @Resource
    SampleService sampleService;
    /**
     * 专家统计数据
     */
    @PreAuthorize("hasAuthority('specialist:statistics:search')")
    @RequestMapping(value = "/expertSummary")
    public R<Object> expertSummary() {
        ExpertSummary summary = statisticsService.expertSummary();

        // 构造返回数据
        Object result = new DynamicBean.Builder()
                .setPV("expertNum", summary.getExpertNum())
                .setPV("validNum", summary.getValidNum())
                .setPV("outNum",  summary.getOutNum())
                .setPV("rejectNum",summary.getRejectNum())
                .build().getObject();
        return R.ok(result);
    }

    /**
     * 项目统计数据
     */
    @PreAuthorize("hasAuthority('specialist:statistics:search')")
    @RequestMapping(value = "/sampleSummary")
    public R<Object> sampleSummary() {
        SampleSummary summary = statisticsService.sampleSummary();
        Map<String, Set<Sample>> categorySampleMap = summary.getCategorySampleMap();
        List<BaseCategory> allBaseCategories = baseCategoryService.all();
        // 构造返回数据
        List<Object> results = new ArrayList<>();
        for (BaseCategory baseCategory : allBaseCategories) {
            Set<Sample> sampleSet = categorySampleMap.get(baseCategory.getId());
            int sampleNum = 0;
            if(sampleSet!=null){
                sampleNum = sampleSet.size();
            }
            Object o = new DynamicBean.Builder()
                    .setPV("id", baseCategory.getId())
                    .setPV("name",  baseCategory.getName())
                    .setPV("sort", baseCategory.getSort())
                    .setPV("sampleNum",sampleNum)
                    .build().getObject();
            results.add(o);
        }
        return R.ok(results);
    }

    /**
     * 搜索
     * @param baseCategoryId 类别
     * @param status 状态 0未抽取  1已抽取
     */
    @PreAuthorize("hasAuthority('specialist:statistics:search')")
    @RequestMapping(value = "/search")
    public R<Object> search(String baseCategoryId,String status,
                                   @RequestParam(value = "page", defaultValue = "1") int pageNo,
                                   @RequestParam(value = "limit", defaultValue = "10") int limit) {

        List<Sample> samples = statisticsService.search(baseCategoryId,status);
        //排序
        Collections.sort(samples, new Comparator<Sample>() {
            @Override
            public int compare(Sample o1, Sample o2) {
                if(o2.getCreateDate().after(o1.getCreateDate())){
                    return 1;
                }else if(o2.getCreateDate().before(o1.getCreateDate())){
                    return -1;
                }
                return  0;
            }
        });

        //内存分页
        List<Sample> pageList = new ArrayList<>();
        int start = (pageNo-1)*limit;
        int end = start + limit-1;
        for(int i=start;i>=0&&i<=end&&i<samples.size();i++){
            pageList.add(samples.get(i));
        }

        // 构造返回数据
        List<Object> projectResults = new ArrayList<>();
        for (Sample sample : pageList) {
            SampleStatusEnum sampleStatusEnum = SampleStatusEnum.getProjectStatusEnumByValue(sample.getStatus());
            String statusName = sampleStatusEnum!=null?sampleStatusEnum.getShortLabel():"";
            //类别名称
            List<String> baseCategoryNames = new ArrayList<>();
            if(StringUtils.isNotBlank(sample.getBaseCategoryIds())){
                for(String categoryId:sample.getBaseCategoryIds().split(",")){
                    BaseCategory baseCategory = baseCategoryService.get(categoryId);
                    if(baseCategory!=null){
                        baseCategoryNames.add(baseCategory.getName());
                    }
                }
            }
            Object o = new DynamicBean.Builder()
                    .setPV("id", sample.getId())
                    .setPV("projectName", sample.getProjectName())
                    .setPV("remark",sample.getRemark())
                    .setPV("applyDate", DateUtils.formatDate(sample.getReviewDate(), "yyyy-MM-dd"))
                    .setPV("status", sample.getStatus())
                    .setPV("statusName", statusName)
                    .setPV("baseCategoryIds", sample.getBaseCategoryIds())
                    .setPV("baseCategoryNames", baseCategoryNames,List.class)
                    .setPV("createDate", DateUtils.formatDate(sample.getCreateDate(), "yyyy-MM-dd HH:mm:ss")).build().getObject();
            projectResults.add(o);
        }

        Object data = new DynamicBean.Builder()
                .setPV("total", samples.size())
                .setPV("list", projectResults, List.class)
                .build().getObject();
        return R.ok(data);
    }

    /**
     * 导出excel
     * @param baseCategoryId 类别
     * @param status 状态 0未抽取  1已抽取
     */
    @PreAuthorize("hasAuthority('specialist:statistics:search')")
    @RequestMapping(value = "/searchExport")
    public void searchExport(String baseCategoryId,String status, HttpServletResponse response){


        List<Sample> samples = statisticsService.search(baseCategoryId,status);
        //排序
        Collections.sort(samples, new Comparator<Sample>() {
            @Override
            public int compare(Sample o1, Sample o2) {
                if(o1.getCreateDate().after(o2.getCreateDate())){
                    return 1;
                }else if(o1.getCreateDate().before(o2.getCreateDate())){
                    return -1;
                }
                return  0;
            }
        });
        //构造数据
        Map<String, Object> root = new HashMap<String, Object>();
        List<Map<String, Object>> list = new ArrayList<>();
        for (Sample sample : samples) {
            SampleStatusEnum sampleStatusEnum = SampleStatusEnum.getProjectStatusEnumByValue(sample.getStatus());
            String statusName = sampleStatusEnum!=null?sampleStatusEnum.getShortLabel():"";
            //类别名称
            String baseCategoryNames = "";
            if(StringUtils.isNotBlank(sample.getBaseCategoryIds())){
                for(String categoryId:sample.getBaseCategoryIds().split(",")){
                    BaseCategory baseCategory = baseCategoryService.get(categoryId);
                    if(baseCategory!=null){
                        baseCategoryNames=baseCategoryNames+baseCategory.getName()+" ";
                    }
                }
            }
            Map<String, Object> entityMap = new HashMap<String, Object>();
            entityMap.put("id",sample.getId());
            entityMap.put("projectName",sample.getProjectName());
            entityMap.put("remark",sample.getRemark());
            entityMap.put("applyDate",DateUtils.formatDate(sample.getReviewDate(), "yyyy-MM-dd"));
            entityMap.put("statusName",statusName);
            entityMap.put("baseCategoryNames",baseCategoryNames);
            list.add(entityMap);
        }

        root.put("list", list);
        Map<String, Map<String, Object>> beanParams = new HashMap<String, Map<String, Object>>();
        beanParams.put("statistics", root);
        XLSTransformer former = new XLSTransformer();
        InputStream in = null;
        OutputStream out=null;
        try {
            String fileName = "项目统计"+ DateUtils.getDate("yyyyMMddHHmmss") +  ".xlsx";
            File statisticsFile = new File(Paths.get(SpringContextUtil.getProperty("myself.classpath"),"reports","expertStatistics.xlsx").toString());
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
}
