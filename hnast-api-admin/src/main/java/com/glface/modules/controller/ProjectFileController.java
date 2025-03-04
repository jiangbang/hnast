package com.glface.modules.controller;

import com.glface.base.bean.R;
import com.glface.log.LoggerMonitor;
import com.glface.modules.model.ProjectStage;
import com.glface.modules.service.ProjectStageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/project/file")
public class ProjectFileController {

    @Resource
    private ProjectStageService stageService;

    @RequestMapping(value = "/get")
    public R<ProjectStage> get(String id) {
        ProjectStage projectStage = stageService.get(id);
        return R.ok(projectStage);
    }

    @LoggerMonitor(value = "项目文件上传")
    @PreAuthorize("hasAuthority('permission:project:file:upload')")
    @RequestMapping(value = "/update")
    public R<Object> fileUpload() {

        return R.ok();
    }

}
