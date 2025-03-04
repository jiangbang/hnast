package com.glface.modules.sp.controller;

import com.glface.base.bean.DynamicBean;
import com.glface.base.bean.R;
import com.glface.base.utils.DateUtils;
import com.glface.log.LoggerMonitor;
import com.glface.modules.sp.model.BaseCategory;
import com.glface.modules.sp.model.Sample;
import com.glface.modules.sp.model.SampleCategoryNum;
import com.glface.modules.sp.service.BaseCategoryService;
import com.glface.modules.sp.service.SampleCategoryNumService;
import com.glface.modules.sp.service.SampleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 专家抽取类别数量设置
 */
@Slf4j
@RestController
@RequestMapping("/specialist/sampleCategory")
public class SampleCategoryNumController {
    @Resource
    private SampleCategoryNumService sampleCategoryNumService;
    @Resource
    private SampleService sampleService;
    @Resource
    private BaseCategoryService baseCategoryService;

    @RequestMapping(value = "/get")
    public R<Object> get(String id) {
        SampleCategoryNum bean = sampleCategoryNumService.get(id);
        Sample sample = sampleService.get(bean.getSampleId());
        String sampleProjectName = "";
        if (sample != null) {
            sampleProjectName = sample.getProjectName();
        }
        BaseCategory baseCategory = baseCategoryService.get(bean.getBaseCategoryId());
        String baseCategoryName = "";
        if (baseCategory != null) {
            baseCategoryName = baseCategory.getName();
        }
        //构造返回数据
        Object object = new DynamicBean.Builder().setPV("id", bean.getId())
                .setPV("sampleId", bean.getSampleId())
                .setPV("sampleProjectName", sampleProjectName)
                .setPV("baseCategoryId", bean.getBaseCategoryId())
                .setPV("baseCategoryName", baseCategoryName)
                .setPV("num", bean.getNum())
                .setPV("createDate", DateUtils.formatDate(bean.getCreateDate(), "yyyy-MM-dd HH:mm:ss"))
                .build().getObject();
        return R.ok(object);
    }

    /**
     * 通过sampleId查询所有
     */
    @RequestMapping(value = "/findBySample")
    public R<Object> findBySample(String sampleId) {
        List<SampleCategoryNum> list = sampleCategoryNumService.findBySampleId(sampleId);
        List<Object> result = new ArrayList<>();
        for (SampleCategoryNum bean : list) {
            Sample sample = sampleService.get(bean.getSampleId());
            String sampleProjectName = "";
            if (sample != null) {
                sampleProjectName = sample.getProjectName();
            }
            BaseCategory baseCategory = baseCategoryService.get(bean.getBaseCategoryId());
            String baseCategoryName = "";
            if (baseCategory != null) {
                baseCategoryName = baseCategory.getName();
            }
            Object batchBean = new DynamicBean.Builder().setPV("id", bean.getId())
                    .setPV("sampleId", bean.getSampleId())
                    .setPV("sampleProjectName", sampleProjectName)
                    .setPV("baseCategoryId", bean.getBaseCategoryId())
                    .setPV("baseCategoryName", baseCategoryName)
                    .setPV("num", bean.getNum())
                    .setPV("createDate", DateUtils.formatDate(bean.getCreateDate(), "yyyy-MM-dd HH:mm:ss"))
                    .build().getObject();
            result.add(batchBean);
        }
        return R.ok(result);
    }

    /**
     * 新增
     *
     * @param sampleId
     * @param baseCategoryId
     * @param num            抽取专家数
     */
    @LoggerMonitor(value = "【专家库】专家抽取类别-新增")
    @PreAuthorize("hasAuthority('specialist:sample:add')")
    @RequestMapping(value = "/create")
    public R<Object> create(
            String sampleId,
            String baseCategoryId,
            Integer num
    ) {
        sampleCategoryNumService.create(sampleId, baseCategoryId, num);
        return R.ok();
    }

    /**
     * 编辑
     *
     * @param sampleId
     * @param baseCategoryId
     * @param num            抽取专家数
     */
    @LoggerMonitor(value = "【专家库】专家抽取类别-编辑")
    @PreAuthorize("hasAuthority('specialist:sample:edit')")
    @RequestMapping(value = "/update")
    public R<Object> update(String id,
                            String sampleId,
                            String baseCategoryId,
                            Integer num
    ) {
        sampleCategoryNumService.update(id, sampleId, baseCategoryId, num);
        return R.ok();
    }

    /**
     * 删除
     */
    @LoggerMonitor(value = "【专家库】专家抽取类别-删除")
    @PreAuthorize("hasAuthority('specialist:sample:del')")
    @RequestMapping(value = "/delete")
    public R<Object> delete(String id) {
        sampleCategoryNumService.delete(id);
        return R.ok();
    }
}
