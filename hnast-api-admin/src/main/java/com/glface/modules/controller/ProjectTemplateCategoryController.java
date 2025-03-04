package com.glface.modules.controller;

import com.glface.base.bean.DynamicBean;
import com.glface.base.bean.Page;
import com.glface.base.bean.R;
import com.glface.base.utils.AntiSQLInjectionUtil;
import com.glface.base.utils.DateUtils;
import com.glface.base.utils.NamingStrategyUtils;
import com.glface.log.LoggerMonitor;
import com.glface.modules.model.*;
import com.glface.modules.service.ProjectTemplateCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 项目管理
 */
@Slf4j
@RestController
@RequestMapping("/project/templateCategory")
public class ProjectTemplateCategoryController {

    @Resource
    private ProjectTemplateCategoryService categoryService;
    @RequestMapping(value = "/all")
    public R<Object> all() {
        List<ProjectTemplateCategory> list = categoryService.findAll();
        List<Object> dataList = new DynamicBean.Builder().setPV("id", null)
                .setPV("name", null)
                .setPV("remark", null)
                .build().copyList(list);
        return R.ok(dataList);
    }

    @RequestMapping(value = "/get")
    public R<Object> get(String id) {
        ProjectTemplateCategory category = categoryService.get(id);
        //构造返回数据
        Object object = new DynamicBean.Builder().setPV("id", category.getId())
                .setPV("name", category.getName())
                .setPV("remark", category.getRemark())
                .build().getObject();
        return R.ok(object);
    }
    @RequestMapping(value = "/search")
    public R<Object> search(@RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
                            @RequestParam(value = "limit", defaultValue = "10") int limit,
                            @RequestParam(value = "order", defaultValue = "createDate desc") String order) {
        order = NamingStrategyUtils.underscoreName(AntiSQLInjectionUtil.filter(order.trim()));
        order = "c." + order;
        // 设置查询条件
        Page<ProjectTemplateCategory> page = new Page<>(pageNo,limit);
        page.setOrderBy(order);
        ProjectTemplateCategory category = new ProjectTemplateCategory();

        // 查询
        page =categoryService.pageSearch(page,category);

        // 构造返回数据
        List<Object> batchList = new ArrayList<>();
        for (ProjectTemplateCategory b : page.getList()) {
            Object batchBean = new DynamicBean.Builder().setPV("id", b.getId())
                    .setPV("name", b.getName())
                    .setPV("remark", b.getRemark())
                    .setPV("createDate", DateUtils.formatDate(b.getCreateDate(), "yyyy-MM-dd HH:mm:ss")).build().getObject();
            batchList.add(batchBean);
        }

        Object data = new DynamicBean.Builder()
                .setPV("total", page.getCount())
                .setPV("templateCategorys", batchList, List.class)
                .build().getObject();

        return R.ok(data);
    }

    /**
     * 新增
     * @param name         批次年份
     * @param remark       说明
     * @return
     */
    @LoggerMonitor(value = "模板分类-新增")
    @PreAuthorize("hasAuthority('project:template:add')")
    @RequestMapping(value = "/create")
    public R<Object> create(
            String name,
            String remark) {
        categoryService.create(name, remark);
        return R.ok();
    }

    /**
     * 编辑
     */
    @LoggerMonitor(value = "模板分类-编辑")
    @PreAuthorize("hasAuthority('project:template:edit')")
    @RequestMapping(value = "/update")
    public R<Object> update(
            String id,
            String name,
            String remark) {
        categoryService.update(id,name, remark);
        return R.ok();
    }

    /**
     * 删除
     */
    @LoggerMonitor(value = "模板分类-删除")
    @PreAuthorize("hasAuthority('project:template:del')")
    @RequestMapping(value = "/delete")
    public R<Object> delete(String id) {
        categoryService.delete(id);
        return R.ok();
    }
}
