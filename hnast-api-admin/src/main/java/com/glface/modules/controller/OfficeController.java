package com.glface.modules.controller;

import com.glface.base.bean.DynamicBean;
import com.glface.base.bean.Page;
import com.glface.base.bean.R;
import com.glface.model.SysDict;
import com.glface.model.SysOffice;
import com.glface.modules.service.DictService;
import com.glface.modules.service.OfficeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/system/office")
public class OfficeController {
    @Resource
    private OfficeService officeService;

    @Resource
    private DictService dictService;

    /**
     * 获取所有机构类型
     */
    @RequestMapping(value = "/types")
    public R<List<Object>> types() {
        Page<SysDict> page = new Page<>(1, 100);
        page.setOrderBy("sort asc");
        SysDict dict = new SysDict();
        dict.setType("orgType");
        dictService.find(page,dict);
        List<SysDict> typeList = page.getList();

        List<Object> dataList = new DynamicBean.Builder().setPV("value", null)
                .setPV("label", null).build().copyList(typeList);
        return R.ok(dataList);
    }

    /**
     * 获取所有部门，并组织成树形结构
     */
    @PreAuthorize("hasAuthority('permission:office:view')")
    @RequestMapping(value = "/tree")
    public R<List<SysOffice>> tree() {
        List<SysOffice> officeList = officeService.allListTree();
        return R.ok(officeList);
    }

    /**
     * 新建部门
     *
     * @param name 部门名称
     * @param pid  上级部门id
     * @param sort 排序 升序
     */
    @PreAuthorize("hasAuthority('permission:office:add')")
    @RequestMapping(value = "/create")
    public R<Object> create(String name,
                            String type,
                            String areaId,
                            @RequestParam(value = "pid", defaultValue = "0") String pid,
                            @RequestParam(value = "sort", defaultValue = "999") int sort) {
        officeService.create(pid, name, type,areaId,sort);
        return R.ok();
    }

    /**
     * 删除部门 同时删除关联的用户部门，如果有未删除的子部门则不允许删除
     */
    @PreAuthorize("hasAuthority('permission:office:del')")
    @RequestMapping(value = "/delete")
    public R<Object> delete(String id) {
        officeService.delete(id);
        return R.ok();
    }

    /**
     * 修改部门，如果修改了pid则所有自部门的code会发生响应修改
     *
     * @param name  部门名称
     * @param sort 排序 升序
     * @param pid 如果为空则不修改pid
     */
    @PreAuthorize("hasAuthority('permission:office:edit')")
    @RequestMapping(value = "/update")
    public R<Object> update(String id, String name, String type,String areaId,
                                  @RequestParam(value = "sort", defaultValue = "999") int sort,
                                  String pid) {
        officeService.update(pid, id, name, type,areaId,sort);
        return R.ok();
    }
}
