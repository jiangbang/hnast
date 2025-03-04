package com.glface.modules.controller;

import com.glface.base.bean.DynamicBean;
import com.glface.base.bean.Page;
import com.glface.base.bean.R;
import com.glface.base.utils.AntiSQLInjectionUtil;
import com.glface.base.utils.DateUtils;
import com.glface.base.utils.NamingStrategyUtils;
import com.glface.log.LoggerMonitor;
import com.glface.modules.model.ProjectBatch;
import com.glface.modules.service.ProjectBatchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 项目批次管理
 */
@Slf4j
@RestController
@RequestMapping("/project/batch")
public class ProjectBatchController {

    @Resource
    private ProjectBatchService projectBatchService;

    @RequestMapping(value = "/get")
    public R<Object> get(String id) {
        ProjectBatch projectBatch = projectBatchService.get(id);
        //构造返回数据
        Object object = new DynamicBean.Builder().setPV("id", projectBatch.getId())
                .setPV("year", projectBatch.getYear())
                .setPV("number", projectBatch.getNumber(), String.class)
                .setPV("startTime", DateUtils.formatDate(projectBatch.getStartTime(), "yyyy-MM-dd HH:mm:ss"))
                .setPV("endTime", DateUtils.formatDate(projectBatch.getEndTime(), "yyyy-MM-dd HH:mm:ss"))
                .setPV("status", projectBatch.getStatus())
                .setPV("remark", projectBatch.getRemark())
                .build().getObject();
        return R.ok(object);
    }

    /**
     * 通过批次年份查询
     * @param year  批次年份
     * @param pageNo  查询分页
     * @param limit 查询数
     * @param order 排序 默认开始时间降序
     */
    @RequestMapping(value = "/search")
    public R<Object> search(String year,
                            @RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
                            @RequestParam(value = "limit", defaultValue = "10") int limit,
                            @RequestParam(value = "order", defaultValue = "startTime desc") String order) {
        order = NamingStrategyUtils.underscoreName(AntiSQLInjectionUtil.filter(order.trim()));
        order = "b." + order;
        // 设置查询条件
        Page<ProjectBatch> page = new Page<>(pageNo,limit);
        page.setOrderBy(order);
        ProjectBatch batch = new ProjectBatch();
        batch.setYear(year);

        // 查询
        page = projectBatchService.pageSearch(page,batch);

        // 构造返回数据
        List<Object> batchList = new ArrayList<>();
        for (ProjectBatch b : page.getList()) {
            Object batchBean = new DynamicBean.Builder().setPV("id", b.getId())
                    .setPV("year", b.getYear())
                    .setPV("number", b.getNumber(), String.class)
                    .setPV("startTime", DateUtils.formatDate(b.getStartTime(), "yyyy-MM-dd HH:mm:ss"))
                    .setPV("endTime", DateUtils.formatDate(b.getEndTime(), "yyyy-MM-dd HH:mm:ss"))
                    .setPV("status", b.getStatus())
                    .setPV("remark", b.getRemark())

                    .setPV("createDate", DateUtils.formatDate(b.getCreateDate(), "yyyy-MM-dd HH:mm:ss")).build().getObject();
            batchList.add(batchBean);
        }

        Object data = new DynamicBean.Builder()
                .setPV("total", page.getCount())
                .setPV("batchs", batchList, List.class)
                .build().getObject();

        return R.ok(data);
    }

    /**
     * 新增
     * @param year         批次年份
     * @param number       批次编号
     * @param startTime    开始时间
     * @param endTime      结束时间
     * @param remark       说明
     * @return
     */
    @LoggerMonitor(value = "项目批次-新增")
    @PreAuthorize("hasAuthority('project:batch:add')")
    @RequestMapping(value = "/create")
    public R<Object> create(
            String year,
            String number,
            @RequestParam("startTime") @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date startTime,
            @RequestParam("endTime") @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date endTime,
            String remark) {
        projectBatchService.create(year, number, startTime, endTime, remark);
        return R.ok();
    }

    /**
     * 编辑
     */
    @LoggerMonitor(value = "项目批次-编辑")
    @PreAuthorize("hasAuthority('project:batch:edit')")
    @RequestMapping(value = "/update")
    public R<Object> update(
            String id,
            String year,
            String number,
            @RequestParam("startTime") @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date startTime,
            @RequestParam("endTime") @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date endTime,
            String remark) {
        projectBatchService.update(id, year, number, startTime, endTime, remark);
        return R.ok();
    }

    /**
     *
     */
    @LoggerMonitor(value = "项目批次-状态变更")
    @PreAuthorize("hasAuthority('project:batch:edit')")
    @RequestMapping(value = "/changeStatus")
    public R<Object> changeStatus(String id) {
        projectBatchService.changeStatus(id);
        return R.ok();
    }

    /**
     * 删除
     */
    @LoggerMonitor(value = "项目批次-删除")
    @PreAuthorize("hasAuthority('project:batch:del')")
    @RequestMapping(value = "/delete")
    public R<Object> delete(String id) {
        projectBatchService.delete(id);
        return R.ok();
    }
}
