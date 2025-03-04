package com.glface.modules.controller;

import com.glface.base.bean.R;
import com.glface.log.LoggerMonitor;
import com.glface.modules.model.ProjectFunds;
import com.glface.modules.service.ProjectFundsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/project/funds")
public class ProjectFundsController {

    @Resource
    private ProjectFundsService fundsService;

    @RequestMapping(value = "/get")
    public R<ProjectFunds> get(String id) {
        ProjectFunds funds = fundsService.get(id);
        return R.ok(funds);
    }

    @LoggerMonitor(value = "项目内容-编辑")
    @PreAuthorize("hasAuthority('permission:project:funds:edit')")
    @RequestMapping(value = "/update")
    public R<Object> update(String projectId, String name, float money, String remark) {
        fundsService.update(projectId,name, money, remark);
        return R.ok();
    }
}
