package com.glface.modules.controller;

import com.glface.base.bean.DynamicBean;
import com.glface.base.bean.Page;
import com.glface.base.bean.R;
import com.glface.base.utils.AntiSQLInjectionUtil;
import com.glface.base.utils.DateUtils;
import com.glface.base.utils.NamingStrategyUtils;
import com.glface.log.LoggerMonitor;
import com.glface.modules.model.ProjectPlanType;
import com.glface.modules.model.ProjectSpecial;
import com.glface.modules.service.ProjectCategoryService;
import com.glface.modules.service.ProjectPlanTypeService;
import com.glface.modules.service.ProjectSpecialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 项目专项信息管理
 */
@Slf4j
@RestController
@RequestMapping("/project/special")
public class ProjectSpecialController {

    @Resource
    private ProjectSpecialService projectSpecialService;
    @Resource
    private ProjectCategoryService projectCategoryService;
    @Resource
    private ProjectPlanTypeService projectPlanTypeService;

    @RequestMapping(value = "/get")
    public R<Object> get(String id) {
        ProjectSpecial projectSpecial = projectSpecialService.get(id);
        //构造返回数据
        Object object = new DynamicBean.Builder().setPV("id", projectSpecial.getId())
                .setPV("name", projectSpecial.getName())
                .setPV("planTypeId", projectSpecial.getPlanTypeId())
                .setPV("remark", projectSpecial.getRemark())
                .build().getObject();
        return R.ok(object);
    }
    @RequestMapping(value = "/getByPlanTypeId")
    public R<Object> getByCategoryId(String planTypeId) {
        List<ProjectSpecial> specials = projectSpecialService.findByPlanTypeId(planTypeId);
        //构造返回数据
        List<Object> specialList = new ArrayList<>();
        for (ProjectSpecial s : specials) {
            Object o = new DynamicBean.Builder().setPV("id", s.getId())
                    .setPV("name", s.getName())
                    .setPV("planTypeId", s.getPlanTypeId())
                    .setPV("remark", s.getRemark())
                    .setPV("updateDate", DateUtils.formatDate(s.getUpdateDate(), "yyyy-MM-dd HH:mm:ss"))
                    .setPV("createDate", DateUtils.formatDate(s.getCreateDate(), "yyyy-MM-dd HH:mm:ss")).build().getObject();
            specialList.add(o);
        }
        return R.ok(specialList);
    }
    /**
     * 通过批次年份查询
     * @param name   专项名称
     * @param pageNo  查询分页
     * @param limit 查询数
     * @param order 排序 默认开始时间降序
     */
    @RequestMapping(value = "/search")
    public R<Object> search(String name,
                            @RequestParam(value = "page", defaultValue = "1") int pageNo,
                            @RequestParam(value = "limit", defaultValue = "10") int limit,
                            @RequestParam(value = "order", defaultValue = "createDate desc") String order) {
        order = NamingStrategyUtils.underscoreName(AntiSQLInjectionUtil.filter(order.trim()));
        order = "s." + order;
        // 设置查询条件
        Page<ProjectSpecial> page = new Page<>(pageNo,limit);
        page.setOrderBy(order);
        ProjectSpecial special = new ProjectSpecial();
        special.setName(name);

        // 查询
        page = projectSpecialService.pageSearch(page,special);

        // 构造返回数据
        List<Object> batchList = new ArrayList<>();
        for (ProjectSpecial s : page.getList()) {
            ProjectPlanType planType = projectPlanTypeService.get(s.getPlanTypeId());
            Object batchBean = new DynamicBean.Builder().setPV("id", s.getId())
                    .setPV("name", s.getName())
                    .setPV("remark", s.getRemark())
                    .setPV("planType", planType,ProjectPlanType.class)
                    .setPV("updateDate", DateUtils.formatDate(s.getUpdateDate(), "yyyy-MM-dd HH:mm:ss"))
                    .setPV("createDate", DateUtils.formatDate(s.getCreateDate(), "yyyy-MM-dd HH:mm:ss")).build().getObject();
            batchList.add(batchBean);
        }

        Object data = new DynamicBean.Builder()
                .setPV("total", page.getCount())
                .setPV("specials", batchList, List.class)
                .build().getObject();

        return R.ok(data);
    }

    /**
     * 新增
     * @param name         专项名称
     * @param remark       说明
     * @return
     */
    @LoggerMonitor(value = "项目专项信息-新增")
    @PreAuthorize("hasAuthority('project:special:add')")
    @RequestMapping(value = "/create")
    public R<Object> create(
            String name,
            String planTypeId,
            String remark) {
        projectSpecialService.create(name, planTypeId,remark);
        return R.ok();
    }

    /**
     * 编辑
     */
    @LoggerMonitor(value = "项目专项信息-编辑")
    @PreAuthorize("hasAuthority('project:special:edit')")
    @RequestMapping(value = "/update")
    public R<Object> update(
            String id,
            String name,
            String planTypeId,
            String remark) {
        projectSpecialService.update(id,name,planTypeId, remark);
        return R.ok();
    }

    /**
     * 删除
     */
    @LoggerMonitor(value = "项目专项信息-删除")
    @PreAuthorize("hasAuthority('project:special:del')")
    @RequestMapping(value = "/delete")
    public R<Object> delete(String id) {
        projectSpecialService.delete(id);
        return R.ok();
    }
}
