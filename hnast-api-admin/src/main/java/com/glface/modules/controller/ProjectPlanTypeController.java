package com.glface.modules.controller;

import com.alibaba.fastjson.JSON;
import com.glface.base.bean.DynamicBean;
import com.glface.base.bean.Page;
import com.glface.base.bean.R;
import com.glface.base.utils.AntiSQLInjectionUtil;
import com.glface.base.utils.DateUtils;
import com.glface.base.utils.NamingStrategyUtils;
import com.glface.log.LoggerMonitor;
import com.glface.model.SysArea;
import com.glface.model.SysOffice;
import com.glface.modules.model.ProjectPlanType;
import com.glface.modules.service.OfficeService;
import com.glface.modules.service.ProjectPlanTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 项目计划类型
 */
@Slf4j
@RestController
@RequestMapping("/project/planType")
public class ProjectPlanTypeController {

    @Resource
    private ProjectPlanTypeService planTypeService;
    @Resource
    private OfficeService officeService;

    @RequestMapping(value = "/get")
    public R<Object> get(String id) {
        ProjectPlanType projectPlanType = planTypeService.get(id);
        SysOffice office = officeService.get(projectPlanType.getOfficeId());
        String officeName = "";
        if(office!=null){
            officeName = office.getName();
        }
        //构造返回数据
        Object object = new DynamicBean.Builder().setPV("id", projectPlanType.getId())
                .setPV("name", projectPlanType.getName())
                .setPV("officeId", projectPlanType.getOfficeId())
                .setPV("officeName", officeName)
                .setPV("code", projectPlanType.getCode())
                .setPV("sort", projectPlanType.getSort())
                .setPV("remark", projectPlanType.getRemark())
                .build().getObject();
        return R.ok(object);
    }

    /**
     * 获取所有归口部门，并组织成树形结构
     */
    @RequestMapping(value = "/gkbmTree")
    public R<List<SysOffice>> gkbmTree() {
        List<SysOffice> officeList = officeService.gkbmTree();
        return R.ok(officeList);
    }

    /**
     * 分页查询
     * @param pageNo  查询分页
     * @param limit 查询数
     * @param order 排序 默认创建时间降序
     */
    @RequestMapping(value = "/search")
    public R<Object> search(@RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
                            @RequestParam(value = "limit", defaultValue = "10") int limit,
                            @RequestParam(value = "order", defaultValue = "createDate desc") String order) {
        order = NamingStrategyUtils.underscoreName(AntiSQLInjectionUtil.filter(order.trim()));
        order = "p." + order;
        // 设置查询条件
        Page<ProjectPlanType> page = new Page<>(pageNo,limit);
        page.setOrderBy(order);
        ProjectPlanType projectPlanType = new ProjectPlanType();

        // 查询
        page = planTypeService.pageSearch(page,projectPlanType);

        // 构造返回数据
        List<Object> batchList = new ArrayList<>();
        for (ProjectPlanType p : page.getList()) {
            SysOffice office = officeService.get(p.getOfficeId());
            String officeName = "";
            if(office!=null){
                officeName = office.getName();
            }
            Object batchBean = new DynamicBean.Builder().setPV("id", p.getId())
                    .setPV("name", p.getName())
                    .setPV("officeId", p.getOfficeId())
                    .setPV("officeName", officeName)
                    .setPV("code", p.getCode())
                    .setPV("sort", p.getSort())
                    .setPV("remark", p.getRemark())
                    .setPV("createDate", DateUtils.formatDate(p.getCreateDate(), "yyyy-MM-dd HH:mm:ss")).build().getObject();
            batchList.add(batchBean);
        }

        Object data = new DynamicBean.Builder()
                .setPV("total", page.getCount())
                .setPV("planTypes", batchList, List.class)
                .build().getObject();

        return R.ok(data);
    }

    @RequestMapping(value = "/searchOfchil")
    public R<Object> pageSearchOfchil(@RequestParam(value = "fatherId") String fatherId,
                            @RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
                            @RequestParam(value = "limit", defaultValue = "10") int limit,
                            @RequestParam(value = "order", defaultValue = "createDate desc") String order) {
        order = NamingStrategyUtils.underscoreName(AntiSQLInjectionUtil.filter(order.trim()));
        order = "p." + order;
        // 设置查询条件
        Page<ProjectPlanType> page = new Page<>(pageNo,limit);
        page.setOrderBy(order);
        ProjectPlanType projectPlanType = new ProjectPlanType();
        projectPlanType.setFatherId(fatherId);

        // 查询
        page = planTypeService.pageSearchOfchil(page,projectPlanType,fatherId);

        // 构造返回数据
        List<Object> batchList = new ArrayList<>();
        for (ProjectPlanType p : page.getList()) {
            SysOffice office = officeService.get(p.getOfficeId());
            String officeName = "";
            if(office!=null){
                officeName = office.getName();
            }
            Object batchBean = new DynamicBean.Builder().setPV("id", p.getId())
                    .setPV("name", p.getName())
                    .setPV("officeId", p.getOfficeId())
                    .setPV("officeName", officeName)
                    .setPV("code", p.getCode())
                    .setPV("sort", p.getSort())
                    .setPV("remark", p.getRemark())
                    .setPV("createDate", DateUtils.formatDate(p.getCreateDate(), "yyyy-MM-dd HH:mm:ss")).build().getObject();
            batchList.add(batchBean);
        }

        Object data = new DynamicBean.Builder()
                .setPV("total", page.getCount())
                .setPV("planTypes", batchList, List.class)
                .build().getObject();
        return R.ok(data);
    }

    @RequestMapping(value = "/tree")
    public R<List<ProjectPlanType>> tree() {
        List<ProjectPlanType> projectPlanTypes = planTypeService.allListTree();
        return R.ok(projectPlanTypes);
    }

    /**
     * 新增
     * @param name         类型名称
     * @param officeId     归口部门
     * @param code         计划类型编码
     * @param sort         排序 升序
     * @param remark       说明
     * @return
     */
    @LoggerMonitor(value = "项目计划类型-新增")
    @PreAuthorize("hasAuthority('project:planType:add')")
    @RequestMapping(value = "/create")
    public R<Object> create(String name, String officeId, String code, @RequestParam(value = "sort", defaultValue = "999") int sort,String remark) {
        planTypeService.create(name, officeId, code,sort, remark);
        return R.ok();
    }

    @LoggerMonitor(value = "项目计划类型-新增子项目类")
    @PreAuthorize("hasAuthority('project:planType:add')")
    @RequestMapping(value = "/createChild")
    public R<Object> createChild(String fatherId,String name, String officeId, String code,
                                 @RequestParam(value = "sort", defaultValue = "999") int sort,String remark) {
        planTypeService.createChild(fatherId,name, officeId, code,sort, remark);
        return R.ok();
    }

    /**
     * 编辑
     */
    @LoggerMonitor(value = "项目计划类型-编辑")
    @PreAuthorize("hasAuthority('project:planType:edit')")
    @RequestMapping(value = "/update")
    public R<Object> update(String id,String name, String officeId, String code, @RequestParam(value = "sort", defaultValue = "999") int sort,String remark) {
        planTypeService.update(id, name, officeId, code,sort, remark);
        return R.ok();
    }

    /**
     * 删除
     */
    @LoggerMonitor(value = "项目计划类型-删除")
    @PreAuthorize("hasAuthority('project:planType:del')")
    @RequestMapping(value = "/delete")
    public R<Object> delete(String id) {
        planTypeService.delete(id);
        return R.ok();
    }


}
