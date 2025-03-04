package com.glface.modules.controller;

import com.glface.base.bean.DynamicBean;
import com.glface.base.bean.Page;
import com.glface.base.bean.R;
import com.glface.base.utils.AntiSQLInjectionUtil;
import com.glface.base.utils.DateUtils;
import com.glface.base.utils.NamingStrategyUtils;
import com.glface.log.LoggerMonitor;
import com.glface.modules.model.ProjectTemplate;
import com.glface.modules.model.ProjectTemplateCategory;
import com.glface.modules.service.ProjectTemplateCategoryService;
import com.glface.modules.service.ProjectTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 项目模板
 */
@Slf4j
@RestController
@RequestMapping("/project/template")
public class ProjectTemplateController {

    @Resource
    private ProjectTemplateService projectTemplateService;
    @Resource
    private ProjectTemplateCategoryService templateCategoryService;

    @RequestMapping(value = "/get")
    public R<Object> get(String id) {
        ProjectTemplate template = projectTemplateService.get(id);
        //构造返回数据
        Object object = new DynamicBean.Builder().setPV("id", template.getId())
                .setPV("name", template.getName())
                .setPV("fileId", template.getFileId())
                .setPV("categoryId", template.getCategoryId())
                .setPV("sort", template.getSort())
                .setPV("remark", template.getRemark())
                .build().getObject();
        return R.ok(object);
    }

    @RequestMapping(value = "/all")
    public R<Object> all() {
        List<ProjectTemplate> roleList = projectTemplateService.all();

        List<Object> dataList = new DynamicBean.Builder().setPV("id", null)
                .setPV("name", null)
                .setPV("fileId", null)
                .setPV("sort", null, Integer.class)
                .setPV("remark", null)
                .build().copyList(roleList);
        return R.ok(dataList);
    }

    /**
     * 查询所有
     */
    @RequestMapping(value = "/searchAll")
    public R<Object> search(@RequestParam(value = "name", defaultValue = "") String name) {

        List<ProjectTemplateCategory> allCate = templateCategoryService.findAll();
        List list = new ArrayList();
        for (ProjectTemplateCategory cate: allCate) {
            // 查询
            List<ProjectTemplate> page = projectTemplateService.all(cate.getId(),name);
            // 构造返回数据
            List<Object> batchList = new ArrayList<>();
            for (ProjectTemplate t : page) {
                Object batchBean = new DynamicBean.Builder().setPV("id", t.getId())
                        .setPV("name", t.getName())
                        .setPV("fileId", t.getFileId())
                        .setPV("sort", t.getSort())
                        .setPV("remark", t.getRemark())
                        .setPV("createDate", DateUtils.formatDate(t.getCreateDate(), "yyyy-MM-dd HH:mm:ss")).build().getObject();
                batchList.add(batchBean);
            }

            Object data = new DynamicBean.Builder()
                    .setPV("id", cate.getId())
                    .setPV("name", cate.getName())
                    .setPV("total", page.size())
                    .setPV("templates", batchList, List.class)
                    .build().getObject();
            list.add(data);
        }
        return R.ok(list);
    }

    /**
     * 分页查询
     * @param pageNo  查询分页
     * @param limit 查询数
     * @param order 排序 默认创建时间降序
     */
    @RequestMapping(value = "/search")
    public R<Object> search(@RequestParam(value = "page", defaultValue = "1") int pageNo,
                            @RequestParam(value = "limit", defaultValue = "10") int limit,
                            @RequestParam(value = "name", defaultValue = "") String name,
                            @RequestParam(value = "order", defaultValue = "createDate desc") String order) {
        order = NamingStrategyUtils.underscoreName(AntiSQLInjectionUtil.filter(order.trim()));
        order = "t." + order;
        // 设置查询条件
        Page<ProjectTemplate> page = new Page<>(pageNo,limit);
        page.setOrderBy(order);
        ProjectTemplate projectTemplate = new ProjectTemplate();
        projectTemplate.setName(name);
        // 查询
        page = projectTemplateService.pageSearch(page,projectTemplate);

        // 构造返回数据
        List<Object> batchList = new ArrayList<>();
        for (ProjectTemplate t : page.getList()) {
            ProjectTemplateCategory category = templateCategoryService.get(t.getCategoryId());
            Object batchBean = new DynamicBean.Builder().setPV("id", t.getId())
                    .setPV("name", t.getName())
                    .setPV("fileId", t.getFileId())
                    .setPV("categoryId", category!=null?category.getId():null)
                    .setPV("categoryName", category!=null?category.getName():null)
                    .setPV("sort", t.getSort())
                    .setPV("remark", t.getRemark())
                    .setPV("createDate", DateUtils.formatDate(t.getCreateDate(), "yyyy-MM-dd HH:mm:ss")).build().getObject();
            batchList.add(batchBean);
        }

        Object data = new DynamicBean.Builder()
                .setPV("total", page.getCount())
                .setPV("templates", batchList, List.class)
                .build().getObject();

        return R.ok(data);
    }

    /**
     * 新增
     * @return
     */
    @LoggerMonitor(value = "项目模板-新增")
    @PreAuthorize("hasAuthority('project:template:add')")
    @RequestMapping(value = "/create")
    public R<Object> create(String name,String fileId,String categoryId,@RequestParam(value = "sort", defaultValue = "999") int sort,String remark) {
        projectTemplateService.create(name, fileId,categoryId,sort, remark);
        return R.ok();
    }

    /**
     * 编辑
     */
    @LoggerMonitor(value = "项目模板-编辑")
    @PreAuthorize("hasAuthority('project:template:edit')")
    @RequestMapping(value = "/update")
    public R<Object> update(String id,String name,String fileId,String categoryId,@RequestParam(value = "sort", defaultValue = "999") int sort,String remark) {
        projectTemplateService.update(id, name, fileId,categoryId,sort, remark);
        return R.ok();
    }

    /**
     * 删除
     */
    @LoggerMonitor(value = "项目模板-删除")
    @PreAuthorize("hasAuthority('project:template:del')")
    @RequestMapping(value = "/delete")
    public R<Object> delete(String id) {
        projectTemplateService.delete(id);
        return R.ok();
    }


}
