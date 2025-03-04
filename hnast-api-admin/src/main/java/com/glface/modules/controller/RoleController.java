package com.glface.modules.controller;

import com.glface.base.bean.DynamicBean;
import com.glface.base.bean.Page;
import com.glface.base.bean.R;
import com.glface.base.utils.AntiSQLInjectionUtil;
import com.glface.base.utils.DateUtils;
import com.glface.base.utils.NamingStrategyUtils;
import com.glface.model.SysMenu;
import com.glface.model.SysRole;
import com.glface.model.SysRoleMenu;
import com.glface.modules.service.MenuService;
import com.glface.modules.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/system/role")
public class RoleController {
    @Resource
    private RoleService roleService;
    @Resource
    private MenuService menuService;

    /**
     * 获取所有角色
     */
    @RequestMapping(value = "/all")
    public R<List<Object>> all() {
        List<SysRole> roleList = roleService.findAll();

        List<Object> dataList = new DynamicBean.Builder().setPV("id", null).setPV("name", null)
                .setPV("type", null, Integer.class).build().copyList(roleList);
        return R.ok(dataList);
    }

    @PreAuthorize("hasAuthority('permission:role:view')")
    @RequestMapping(value = "/search")
    public R<Object> search(String name, @RequestParam(value = "page", defaultValue = "1") int pageNo,
                            @RequestParam(value = "limit", defaultValue = "10") int limit,
                            @RequestParam(value = "order", defaultValue = "createDate desc") String order) {

        // 设置查询条件
        Page<SysRole> page = new Page<>(pageNo, limit);
        page.setOrderBy(NamingStrategyUtils.underscoreName(AntiSQLInjectionUtil.filter(order)));
        SysRole role = new SysRole();
        role.setName(name);

        // 查询
        page = roleService.find(page, role);

        // 构造返回数据
        List<Object> roleList = new ArrayList<>();
        for (SysRole r : page.getList()) {
            Object userBean = new DynamicBean.Builder()
                    .setPV("id", r.getId())
                    .setPV("name", r.getName())
                    .setPV("createDate", DateUtils.formatDate(r.getCreateDate(), "yyyy-MM-dd HH:mm:ss")).build().getObject();
            roleList.add(userBean);
        }

        Object data = new DynamicBean.Builder()
                .setPV("total", page.getCount())
                .setPV("roles", roleList, List.class)
                .build().getObject();
        return R.ok(data);
    }

    /**
     * 取特定角色，同时会返回所有菜单及角色菜单
     */
    @PreAuthorize("hasAuthority('permission:role:view')")
    @RequestMapping(value = "/get")
    public R<Object> get(String id) {
        SysRole role = roleService.get(id);
        List<SysMenu> menus = menuService.allListTree();
        List<SysRoleMenu> roleMenus = roleService.findRoleMenusByRoleId(id);
        List<Object> roleMenuList = new DynamicBean.Builder().setPV("menuId", null).build().copyList(roleMenus);
        // 构造响应数据
        Object object = new DynamicBean.Builder().setPV("role", role, SysRole.class)
                .setPV("permissions", menus, List.class)
                .setPV("permissionsSelected", roleMenuList, List.class).build()
                .getObject();
        return R.ok(object);
    }

    /**
     * 获取所有菜单
     */
    @PreAuthorize("hasAuthority('permission:role:add')")
    @RequestMapping(value = "/menus")
    public R<Object> menus() {
        List<SysMenu> menus = menuService.allListTree();
        // 构造响应数据
        Object object = new DynamicBean.Builder().setPV("permissions", menus, List.class).build().getObject();
        return R.ok(object);
    }

    /**
     * 创建角色
     *
     * @param name        角色名
     * @param permissions 菜单权限
     */
    @PreAuthorize("hasAuthority('permission:role:add')")
    @RequestMapping(value = "/create")
    public R<Object> create(String name,
                            @RequestParam(value = "permissions[]", required = false) List<String> permissions) {
        roleService.create(name, permissions);
        return R.ok();
    }

    /**
     * 编辑角色
     *
     * @param id          编号
     * @param name        名称
     * @param permissions 菜单权限
     */
    @PreAuthorize("hasAuthority('permission:role:edit')")
    @RequestMapping(value = "/update")
    public R<Object> update(String id, String name,
                            @RequestParam(value = "permissions[]", required = false) List<String> permissions) {
        roleService.update(id, name, permissions);
        return R.ok();
    }

    /**
     * 删除角色 同时删除角色菜单
     */
    @PreAuthorize("hasAuthority('permission:role:del')")
    @RequestMapping(value = "/delete")
    public R<Object> delete(String id) {
        roleService.delete(id);
        return R.ok();
    }


}
