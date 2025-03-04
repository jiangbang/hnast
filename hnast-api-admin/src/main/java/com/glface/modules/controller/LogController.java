package com.glface.modules.controller;

import com.glface.base.bean.DynamicBean;
import com.glface.base.bean.Page;
import com.glface.base.bean.R;
import com.glface.base.utils.AntiSQLInjectionUtil;
import com.glface.base.utils.DateUtils;
import com.glface.base.utils.NamingStrategyUtils;
import com.glface.log.LoggerMonitor;
import com.glface.model.SysLog;
import com.glface.modules.model.ProjectBatch;
import com.glface.modules.service.LogService;
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
 * 系统日志
 */
@Slf4j
@RestController
@RequestMapping("/system/log")
public class LogController {

    @Resource
    private LogService logService;

    @PreAuthorize("hasAuthority('permission:log:view')")
    @RequestMapping(value = "/get")
    public R<Object> get(String id) {
        SysLog sysLog = logService.get(id);
        //构造返回数据
        Object object = new DynamicBean.Builder().setPV("id", sysLog.getId())
                .setPV("title", sysLog.getTitle())
                .setPV("remoteAddr", sysLog.getRemoteAddr())
                .setPV("userAgent", sysLog.getUserAgent())
                .setPV("requestUri", sysLog.getRequestUri())
                .setPV("method", sysLog.getMethod())
                .setPV("params", sysLog.getParams())
                .setPV("exception", sysLog.getException())
                .setPV("createDate", DateUtils.formatDate(sysLog.getCreateDate(), "yyyy-MM-dd HH:mm:ss"))
                .setPV("remark", sysLog.getRemark())
                .setPV("userId", sysLog.getCreateBy())
                .build().getObject();
        return R.ok(object);
    }

    /**
     * 查询日志
     * @param pageNo
     * @param limit
     * @param order
     * @return
     */
    @PreAuthorize("hasAuthority('permission:log:view')")
    @RequestMapping(value = "/search")
    public R<Object> search(@RequestParam(value = "page", defaultValue = "1") int pageNo,
                            @RequestParam(value = "limit", defaultValue = "10") int limit,
                            @RequestParam(value = "order", defaultValue = "createDate desc") String order) {
        // 设置查询条件
        Page<SysLog> page = new Page<>(pageNo,limit);
        page.setOrderBy(NamingStrategyUtils.underscoreName(AntiSQLInjectionUtil.filter(order)));
        // 查询
        page = logService.find(page);

        // 构造返回数据
        List<Object> logList = new ArrayList<>();
        for (SysLog sysLog : page.getList()) {
            Object batchBean = new DynamicBean.Builder().setPV("id", sysLog.getId())
                    .setPV("title", sysLog.getTitle())
                    .setPV("remoteAddr", sysLog.getRemoteAddr())
                    .setPV("userAgent", sysLog.getUserAgent())
                    .setPV("requestUri", sysLog.getRequestUri())
                    .setPV("method", sysLog.getMethod())
                    .setPV("params", sysLog.getParams())
                    .setPV("exception", sysLog.getException())
                    .setPV("remark", sysLog.getRemark())
                    .setPV("userId", sysLog.getCreateBy())
                    .setPV("createDate", DateUtils.formatDate(sysLog.getCreateDate(), "yyyy-MM-dd HH:mm:ss")).build().getObject();
            logList.add(batchBean);
        }

        Object data = new DynamicBean.Builder()
                .setPV("total", page.getCount())
                .setPV("logs", logList, List.class)
                .build().getObject();

        return R.ok(data);
    }

}
