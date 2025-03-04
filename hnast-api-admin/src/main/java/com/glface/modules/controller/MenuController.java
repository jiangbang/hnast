package com.glface.modules.controller;

import com.glface.base.bean.R;
import com.glface.model.SysMenu;
import com.glface.modules.service.MenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/system/menu")
public class MenuController {
    @Resource
    private MenuService menuService;

    /**
     * 获取所有菜单，并组织成树形结构
     */
    @PreAuthorize("hasAuthority('permission:menu:view')")
    @RequestMapping(value = "/tree")
    public R<List<SysMenu>> tree() {
        List<SysMenu> menuList = menuService.allListTree();
        return R.ok(menuList);
    }

    /**
     * 删除菜单 如果有子菜单则不允许删除
     *
     */
    @PreAuthorize("hasAuthority('permission:menu:del')")
    @RequestMapping(value = "/delete")
    public R<Object> delete(String id) {
        menuService.delete(id);
        return R.ok();
    }

    /**
     * 新建菜单
     * @param name  菜单名称
     * @param pid  父节点
     * @param sort 排序升序
     * @param permission 权限标识
     * @param isShow 保留字段
     */
    @PreAuthorize("hasAuthority('permission:menu:add')")
    @RequestMapping(value = "/create")
    public R<Object> create(String name,
                                  @RequestParam(value = "pid", defaultValue = "0") String pid,
                                  @RequestParam(value = "sort", defaultValue = "999") int sort,
                                  String permission,
                                  @RequestParam(value = "isShow", defaultValue = "1")int isShow) {
        menuService.create(pid, name, permission,sort, isShow);
        return R.ok();
    }

    /**
     * 修改菜单
     * @param name  菜单名称
     * @param sort 排序升序
     * @param permission 权限标识
     * @param isShow 保留字段
     */
    @PreAuthorize("hasAuthority('permission:menu:edit')")
    @RequestMapping(value = "/update")
    public R<Object> update(String id,String name,
                                  @RequestParam(value = "sort", defaultValue = "999") int sort,
                                  String permission,
                                  @RequestParam(value = "isShow", defaultValue = "1")int isShow){
        menuService.update(id, name, permission, sort,isShow);
        return R.ok();
    }
}
