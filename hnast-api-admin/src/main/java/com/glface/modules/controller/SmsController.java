package com.glface.modules.controller;

import com.glface.base.bean.DynamicBean;
import com.glface.base.bean.Page;
import com.glface.base.bean.R;
import com.glface.base.utils.*;
import com.glface.model.SysSms;
import com.glface.modules.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/system/sms")
public class SmsController {

    @Resource
    private SmsService smsService;
    /**
     * 查询短信日志
     * @param pageNo
     * @param limit
     * @param order
     * @return
     */
    @PreAuthorize("hasAuthority('permission:sms:view')")
    @RequestMapping(value = "/search")
    public R<Object> search(@RequestParam(value = "page", defaultValue = "1") int pageNo,
                            @RequestParam(value = "limit", defaultValue = "10") int limit,
                            @RequestParam(value = "order", defaultValue = "createDate desc") String order) {
        // 设置查询条件
        Page<SysSms> page = new Page<>(pageNo,limit);
        page.setOrderBy(NamingStrategyUtils.underscoreName(AntiSQLInjectionUtil.filter(order)));
        // 查询
        page = smsService.find(page);

        // 构造返回数据
        List<Object> logList = new ArrayList<>();
        for (SysSms sysSms : page.getList()) {
            Object o = new DynamicBean.Builder().setPV("id", sysSms.getId())
                    .setPV("code", sysSms.getCode())
                    .setPV("content", sysSms.getContent())
                    .setPV("phone", sysSms.getPhone())
                    .setPV("userId", sysSms.getUserId())
                    .setPV("type", sysSms.getType())
                    .setPV("returnCode", sysSms.getReturnCode())
                    .setPV("returnDescription", sysSms.getReturnDescription())
                    .setPV("returnResult", sysSms.getReturnResult())
                    .setPV("remark", sysSms.getRemark())
                    .setPV("createDate", DateUtils.formatDate(sysSms.getCreateDate(), "yyyy-MM-dd HH:mm:ss")).build().getObject();
            logList.add(o);
        }

        Object data = new DynamicBean.Builder()
                .setPV("total", page.getCount())
                .setPV("smses", logList, List.class)
                .build().getObject();

        return R.ok(data);
    }



}
