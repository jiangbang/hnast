package com.glface.modules.controller;

import com.glface.base.bean.DynamicBean;
import com.glface.base.bean.Page;
import com.glface.base.bean.R;
import com.glface.base.utils.AntiSQLInjectionUtil;
import com.glface.base.utils.DateUtils;
import com.glface.base.utils.NamingStrategyUtils;
import com.glface.log.LoggerMonitor;
import com.glface.modules.model.ProjectCategory;
import com.glface.modules.service.ProjectCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 项目类型管理
 */
@Slf4j
@RestController
@RequestMapping("/project/category")
public class ProjectCategoryController {

    @Resource
    private ProjectCategoryService projectCategoryService;

    @RequestMapping(value = "/get")
    public R<Object> get(String id) {
        ProjectCategory projectCategory = projectCategoryService.get(id);
        //构造返回数据
        Object object = new DynamicBean.Builder().setPV("id", projectCategory.getId())
                .setPV("name", projectCategory.getName())
                .setPV("amountMax", String.valueOf(projectCategory.getAmountMax()))
                .setPV("amountMin", String.valueOf(projectCategory.getAmountMin()))
                .setPV("remark", projectCategory.getRemark())
                .build().getObject();
        return R.ok(object);
    }

    /**
     * 分页查询
     * @param pageNo  查询分页
     * @param limit 查询数
     * @param order 排序 默认创建时间降序
     */
    @RequestMapping(value = "/search")
    public R<Object> search(String year,
                            @RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
                            @RequestParam(value = "limit", defaultValue = "10") int limit,
                            @RequestParam(value = "order", defaultValue = "createDate desc") String order) {
        order = NamingStrategyUtils.underscoreName(AntiSQLInjectionUtil.filter(order.trim()));
        order = "c." + order;
        // 设置查询条件
        Page<ProjectCategory> page = new Page<>(pageNo,limit);
        page.setOrderBy(order);
        ProjectCategory category = new ProjectCategory();

        // 查询
        page = projectCategoryService.pageSearch(page,category);

        // 构造返回数据
        List<Object> batchList = new ArrayList<>();
        for (ProjectCategory c : page.getList()) {
            Object batchBean = new DynamicBean.Builder().setPV("id", c.getId())
                    .setPV("name", c.getName())
                    .setPV("amountMax", String.valueOf(c.getAmountMax()))
                    .setPV("amountMin", String.valueOf(c.getAmountMin()))
                    .setPV("remark", c.getRemark())
                    .setPV("createDate", DateUtils.formatDate(c.getCreateDate(), "yyyy-MM-dd HH:mm:ss")).build().getObject();
            batchList.add(batchBean);
        }

        Object data = new DynamicBean.Builder()
                .setPV("total", page.getCount())
                .setPV("categories", batchList, List.class)
                .build().getObject();

        return R.ok(data);
    }

    /**
     * 新增
     * @param name         类型名称
     * @param amountMax    项目金额上限
     * @param amountMin    项目金额下限
     * @param remark       说明
     * @return
     */
    @LoggerMonitor(value = "项目类型-新增")
    @PreAuthorize("hasAuthority('project:category:add')")
    @RequestMapping(value = "/create")
    public R<Object> create(
            String name,
            float amountMax,
            float amountMin,
            String remark) {
        projectCategoryService.create(name, amountMax, amountMin, remark);
        return R.ok();
    }

    /**
     * 编辑
     */
    @LoggerMonitor(value = "项目类型-编辑")
    @PreAuthorize("hasAuthority('project:category:edit')")
    @RequestMapping(value = "/update")
    public R<Object> update(
            String id,
            String name,
            float amountMax,
            float amountMin,
            String remark) {
        projectCategoryService.update(id, name, amountMax, amountMin, remark);
        return R.ok();
    }

    /**
     * 删除
     */
    @LoggerMonitor(value = "项目类型-删除")
    @PreAuthorize("hasAuthority('project:category:del')")
    @RequestMapping(value = "/delete")
    public R<Object> delete(String id) {
        projectCategoryService.delete(id);
        return R.ok();
    }
}
