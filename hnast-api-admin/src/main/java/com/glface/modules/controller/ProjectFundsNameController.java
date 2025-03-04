package com.glface.modules.controller;


import com.glface.base.bean.R;
import com.glface.modules.mapper.ProjectFundsNameMapper;
import com.glface.modules.service.ProjectFundsNameService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/fundsName")
public class ProjectFundsNameController {

    @Resource
    private ProjectFundsNameMapper projectFundsNameMapper;
    @Resource
    private ProjectFundsNameService projectFundsNameService;


    // 查询支出内容名称
    @RequestMapping("/getFundsName")
    public R<Object> getExpertName(){
        List<String> fundsName = projectFundsNameService.findAllFundsName();
        return R.ok(fundsName);
    }

}
