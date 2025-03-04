package com.glface.modules.sp.controller;

import com.glface.base.bean.DynamicBean;
import com.glface.base.bean.Page;
import com.glface.base.bean.R;
import com.glface.base.utils.AntiSQLInjectionUtil;
import com.glface.base.utils.DateUtils;
import com.glface.base.utils.NamingStrategyUtils;
import com.glface.model.SysOffice;
import com.glface.modules.model.Project;
import com.glface.modules.service.OfficeService;
import com.glface.modules.sp.model.BaseCategory;
import com.glface.modules.sp.service.BaseCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 专家库类别信息
 */
@Slf4j
@RestController
@RequestMapping("/specialist/category")
public class BaseCategoryController {

    @Resource
    private BaseCategoryService categoryService;
    @Resource
    private OfficeService officeService;

    @RequestMapping(value = "/get")
    public R<Object> get(String id) {
        BaseCategory bean = categoryService.get(id);
        SysOffice office = officeService.get(bean.getOfficeId());
        String officeName = "";
        if (office != null) {
            officeName = office.getName();
        }
        //构造返回数据
        Object object = new DynamicBean.Builder().setPV("id", bean.getId())
                .setPV("name", bean.getName())
                .setPV("officeId", bean.getOfficeId())
                .setPV("officeName", officeName)
                .setPV("sort", bean.getSort())
                .setPV("remark", bean.getRemark())
                .build().getObject();
        return R.ok(object);
    }

    @RequestMapping(value = "/allBaseCategories")
    public R<Object> allBaseCategories() {
        List<BaseCategory> categories = categoryService.all();
        //排序
        Collections.sort(categories, new Comparator<BaseCategory>() {
            @Override
            public int compare(BaseCategory o1, BaseCategory o2) {
                if(o1.getCreateDate().before(o2.getCreateDate())){
                    return 1;
                }else if(o1.getCreateDate().after(o2.getCreateDate())){
                    return -1;
                }
                return  0;
            }
        });
        List<Object> dataList = new ArrayList<>();
        for(BaseCategory bean:categories){
            SysOffice office = officeService.get(bean.getOfficeId());
            String officeName = "";
            if (office != null) {
                officeName = office.getName();
            }
            Object object = new DynamicBean.Builder().setPV("id", bean.getId())
                    .setPV("name", bean.getName())
                    .setPV("officeId", bean.getOfficeId())
                    .setPV("officeName", officeName)
                    .setPV("sort", bean.getSort())
                    .setPV("remark", bean.getRemark())
                    .build().getObject();
            dataList.add(object);
        }
        return R.ok(dataList);
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
     * @param name 名称
     * @param pageNo 查询分页
     * @param limit  查询数
     * @param order  排序 默认sort升序
     */
    @RequestMapping(value = "/search")
    public R<Object> search(
            String name,
            @RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
            @RequestParam(value = "limit", defaultValue = "10") int limit,
            @RequestParam(value = "order", defaultValue = "createDate desc") String order) {
        order = NamingStrategyUtils.underscoreName(AntiSQLInjectionUtil.filter(order.trim()));
        order = "s." + order;
        // 设置查询条件
        Page<BaseCategory> page = new Page<>(pageNo, limit);
        page.setOrderBy(order);
        BaseCategory condition = new BaseCategory();
        condition.setName(name);

        // 查询
        page = categoryService.pageSearch(page, condition);

        // 构造返回数据
        List<Object> batchList = new ArrayList<>();
        for (BaseCategory p : page.getList()) {
            SysOffice office = officeService.get(p.getOfficeId());
            String officeName = "";
            if (office != null) {
                officeName = office.getName();
            }
            Object batchBean = new DynamicBean.Builder().setPV("id", p.getId())
                    .setPV("name", p.getName())
                    .setPV("officeId", p.getOfficeId())
                    .setPV("officeName", officeName)
                    .setPV("sort", p.getSort())
                    .setPV("remark", p.getRemark())
                    .setPV("createDate", DateUtils.formatDate(p.getCreateDate(), "yyyy-MM-dd HH:mm:ss")).build().getObject();
            batchList.add(batchBean);
        }

        Object data = new DynamicBean.Builder()
                .setPV("total", page.getCount())
                .setPV("list", batchList, List.class)
                .build().getObject();

        return R.ok(data);
    }

    /**
     * 新增
     *
     * @param name     名称
     * @param officeId 归口部门
     * @param sort     排序 升序
     * @param remark   说明
     * @return
     */
    @PreAuthorize("hasAuthority('specialist:category:add')")
    @RequestMapping(value = "/create")
    public R<Object> create(String name, String officeId, @RequestParam(value = "sort", defaultValue = "1000") int sort, String remark) {
        categoryService.create(name, officeId, sort, remark);
        return R.ok();
    }

    /**
     * 编辑
     */
    @PreAuthorize("hasAuthority('specialist:category:edit')")
    @RequestMapping(value = "/update")
    public R<Object> update(String id, String name, String officeId, @RequestParam(value = "sort", defaultValue = "1000") int sort, String remark) {
        categoryService.update(id, name, officeId, sort, remark);
        return R.ok();
    }

    /**
     * 删除
     */
    @PreAuthorize("hasAuthority('specialist:category:del')")
    @RequestMapping(value = "/delete")
    public R<Object> delete(String id) {
        categoryService.delete(id);
        return R.ok();
    }


}
