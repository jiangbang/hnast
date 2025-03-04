package com.glface.modules.controller;

import com.glface.base.bean.R;
import com.glface.log.LoggerMonitor;
import com.glface.modules.model.ProjectContent;
import com.glface.modules.service.ProjectContentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/project/content")
public class ProjectContentController {

    @Resource
    private ProjectContentService contentService;

    @RequestMapping(value = "/get")
    public R<ProjectContent> get(String id) {
        ProjectContent content = contentService.get(id);
        return R.ok(content);
    }

    @LoggerMonitor(value = "项目内容-编辑")
    @PreAuthorize("hasAuthority('permission:project:content:edit')")
    @RequestMapping(value = "/update")
    public R<Object> update(String projectId, String basis, String content, String target,String conditions) {
        contentService.update(projectId,basis, content, target,conditions);
        return R.ok();
    }
}
