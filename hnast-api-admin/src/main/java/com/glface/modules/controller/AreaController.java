package com.glface.modules.controller;

import com.glface.base.bean.DynamicBean;
import com.glface.base.bean.Page;
import com.glface.base.bean.R;
import com.glface.model.SysArea;
import com.glface.model.SysDict;
import com.glface.modules.service.AreaService;
import com.glface.modules.service.DictService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/system/area")
public class AreaController {

    @Resource
    private AreaService areaService;

    @Resource
    private DictService dictService;

    @RequestMapping(value = "/get")
    public R<SysArea> get(String id) {
        SysArea area = areaService.get(id);
        return R.ok(area);
    }

    /**
     * 获取所有区域类型
     */
    @RequestMapping(value = "/types")
    public R<List<Object>> types() {
        Page<SysDict> page = new Page<>(1, 100);
        page.setOrderBy("sort asc");
        SysDict dict = new SysDict();
        dict.setType("areaType");
        dictService.find(page,dict);
        List<SysDict> typeList = page.getList();

        List<Object> dataList = new DynamicBean.Builder().setPV("value", null)
                .setPV("label", null).build().copyList(typeList);
        return R.ok(dataList);
    }

    /**
     * 获取所有地区，并组织成树形结构
     */
    @PreAuthorize("hasAuthority('permission:area:view')")
    @RequestMapping(value = "/tree")
    public R<List<SysArea>> tree() {
        List<SysArea> sysAreaList = areaService.allListTree();
        return R.ok(sysAreaList);
    }

    /**
     * 新建区域
     *
     * @param name 区域名称
     * @param pid  上级区域id
     * @param sort 排序 升序
     */
    @PreAuthorize("hasAuthority('permission:area:add')")
    @RequestMapping(value = "/create")
    public R<Object> create(String name,
                            String type,
                            @RequestParam(value = "pid", defaultValue = "0") String pid,
                            @RequestParam(value = "sort", defaultValue = "999") int sort) {
        areaService.create(pid, name,type,sort);
        return R.ok();
    }

    /**
     * 删除区域 同时删除关联的用户区域，如果有未删除的子区域则不允许删除
     */
    @PreAuthorize("hasAuthority('permission:area:del')")
    @RequestMapping(value = "/delete")
    public R<Object> delete(String id) {
        areaService.delete(id);
        return R.ok();
    }

    /**
     * 修改区域，如果修改了pid则所有自区域的code会发生响应修改
     *
     * @param name  区域名称
     * @param sort 排序 升序
     * @param pid 如果为空则不修改pid
     */
    @PreAuthorize("hasAuthority('permission:area:edit')")
    @RequestMapping(value = "/update")
    public R<Object> update(String id, String name, String type,
                            @RequestParam(value = "sort", defaultValue = "999") int sort,
                            String pid) {
        areaService.update(pid, id, name,type, sort);
        return R.ok();
    }
}
