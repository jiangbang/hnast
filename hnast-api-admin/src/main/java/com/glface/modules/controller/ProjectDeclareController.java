package com.glface.modules.controller;

import com.glface.base.bean.DynamicBean;
import com.glface.base.bean.Page;
import com.glface.base.bean.R;
import com.glface.base.utils.AntiSQLInjectionUtil;
import com.glface.base.utils.DateUtils;
import com.glface.base.utils.NamingStrategyUtils;
import com.glface.log.LoggerMonitor;
import com.glface.modules.model.ProjectCategory;
import com.glface.modules.model.ProjectDeclare;
import com.glface.modules.service.ProjectCategoryService;
import com.glface.modules.service.ProjectDeclareService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 申报指南
 */
@Slf4j
@RestController
@RequestMapping("/project/declare")
public class ProjectDeclareController {

    @Resource
    private ProjectDeclareService projectDeclareService;


    @RequestMapping(value = "/search")
    public R<Object> search() {
        List<ProjectDeclare> projectDeclares = projectDeclareService.all();
        Object data = new DynamicBean.Builder()
                .setPV("declare", projectDeclares.isEmpty()?null:projectDeclares.get(0), ProjectDeclare.class)
                .build().getObject();

        return R.ok(data);
    }
    /**
     * 编辑
     */
    @LoggerMonitor(value = "申报指南-编辑")
    @PreAuthorize("hasAuthority('project:declare:edit')")
    @RequestMapping(value = "/update")
    public R<Object> update(
            String content,
            String remark) {
        projectDeclareService.update(content, remark);
        return R.ok();
    }

    /**
     * 删除
     */
    @LoggerMonitor(value = "申报指南-删除")
    @PreAuthorize("hasAuthority('project:declare:del')")
    @RequestMapping(value = "/delete")
    public R<Object> delete(String id) {
        projectDeclareService.delete(id);
        return R.ok();
    }
}
