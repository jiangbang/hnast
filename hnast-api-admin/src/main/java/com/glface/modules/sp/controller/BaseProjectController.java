package com.glface.modules.sp.controller;


import com.glface.base.bean.R;
import com.glface.modules.sp.model.BaseProject;
import com.glface.modules.sp.service.BaseProjectService;
import com.glface.modules.utils.ExpertStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/*
 * 制裁项目类别信息
 * */
@Slf4j
@RestController
@RequestMapping("/specialist/project")
public class BaseProjectController {

    @Resource
    private BaseProjectService baseProjectService;


    /**
     * 所有制裁项目
     * @return
     */
    @RequestMapping(value = "/allProjectNames")
    public R<Object> allProjectNames() {
        List<String> list = baseProjectService.findProjectNamesByStatus();
        return R.ok(list);
    }
}
