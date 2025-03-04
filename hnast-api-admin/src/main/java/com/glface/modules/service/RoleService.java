package com.glface.modules.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.glface.base.bean.Page;
import com.glface.base.utils.StringUtils;
import com.glface.common.exeception.ServiceException;
import com.glface.common.web.ApiCode;
import com.glface.model.SysMenu;
import com.glface.model.SysRole;
import com.glface.model.SysRoleMenu;
import com.glface.model.SysUserRole;
import com.glface.modules.mapper.RoleMapper;
import com.glface.modules.mapper.RoleMenuMapper;
import com.glface.modules.mapper.UserRoleMapper;
import com.glface.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.*;

@Slf4j
@Service
@Transactional(readOnly = true)
public class RoleService{

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private RoleMenuMapper roleMenuMapper;

    @Resource
    private UserRoleMapper userRoleMapper;

    @Resource
    private MenuService menuService;

    public SysRole get(String id) {
        return roleMapper.selectById(id);
    }

    public List<SysRole> findAll() {
        LambdaQueryWrapper<SysRole> queryWrapper = Wrappers.<SysRole>query().lambda()
                .eq(SysRole::getDelFlag, 0);
        return roleMapper.selectList(queryWrapper);
    }

    public SysRole findByName(String name) {
        LambdaQueryWrapper<SysRole> queryWrapper = Wrappers.<SysRole>query().lambda()
                .eq(SysRole::getDelFlag, 0)
                .eq(SysRole::getName, name);
        return roleMapper.selectOne(queryWrapper);
    }

    public SysRole getByName(String roleName) {
        LambdaQueryWrapper<SysRole> queryWrapper = Wrappers.<SysRole>query().lambda()
                .eq(SysRole::getName, roleName)
                .eq(SysRole::getDelFlag,SysRole.DEL_FLAG_NORMAL);
        return roleMapper.selectOne(queryWrapper);
    }

    /**
     * 分页查找
     */
    public Page<SysRole> find(Page<SysRole> page, SysRole role){
        LambdaQueryWrapper<SysRole> queryWrapper = Wrappers.<SysRole>query().lambda();
        if(role!=null){
            if (StringUtils.isNotEmpty(role.getName())) {
                queryWrapper.like(SysRole::getName, role.getName());
            }
        }

        //计算总数
        if(!page.isNotCount()){
            page.setCount(roleMapper.selectCount(queryWrapper));
        }
        //添加排序
        String lastSql = "";
        if(StringUtils.isNotEmpty(page.getOrderBy())){
            lastSql = " order by "+page.getOrderBy();
        }
        //添加分页
        lastSql = lastSql +" "+page.toLimit();
        queryWrapper.last(lastSql);//此方法只有最后一次调用的生效
        page.setList(roleMapper.selectList(queryWrapper));

        return page;
    }

    /**
     * 新增角色
     * @param name	名称
     * @permissions 角色菜单集合
     */
    @Transactional
    public void create(String name,List<String> permissions) {

        if(StringUtils.isBlank(name)){
            throw new ServiceException(ApiCode.PERMISSION_ROLE_NAME_REQUIRED);
        }

        //创建角色菜单
        //创建角色
        SysRole role = new SysRole();
        role.setName(name);
        UserUtils.preAdd(role);
        roleMapper.insert(role);

        if(permissions!=null){//找出父节点
            Set<String> parentNode = new HashSet<>();
            List<String> vessel = new ArrayList<>(permissions);
            List<SysMenu> all = menuService.findAll();
            Map<String, SysMenu> menuMap = new HashMap<>();
            for(SysMenu menu:all){
                menuMap.put(menu.getId(), menu);
            }
            for(String v : vessel){
                for(SysMenu menu:all){
                    if(v.equals(menu.getId())){
                        String menuId = menu.getPid();
                        while(true){
                            if("0".equals(menuId)){
                                break;
                            }else{
                                SysMenu parent = menuMap.get(menuId);
                                if(parent!=null){
                                    menuId = parent.getPid();
                                    parentNode.add(parent.getId());
                                }else{
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            for(String p : parentNode){
                if(!permissions.contains(p)){
                    permissions.add(p);
                }
            }

            List<SysRoleMenu> rmList = Lists.newArrayList();
            for(String permission:permissions){
                SysRoleMenu rm = new SysRoleMenu();
                rm.setMenuId(permission);
                rm.setRoleId(role.getId());
                rmList.add(rm);
            }
            //存储
            for(SysRoleMenu rm:rmList){
                UserUtils.preAdd(rm);
                roleMenuMapper.insert(rm);
            }
        }
    }

    /**
     * 更新角色
     * @param id 菜单id
     * @param name 菜单名
     * @param permissions 如果permissions为空会删除角色的菜单
     */
    @Transactional
    public void update(String id, String name, List<String> permissions) {

        if(StringUtils.isBlank(name)){
            throw new ServiceException(ApiCode.PERMISSION_ROLE_NAME_REQUIRED);
        }

        SysRole role = get(id);
        if(role==null){
            throw new ServiceException(ApiCode.PERMISSION_ROLE_NOTEXIST);
        }

        role.setName(name);


        if(permissions != null){
            Set<String> parentNode = new HashSet<>();
            List<String> vessel = new ArrayList<>(permissions);
            List<SysMenu> all = menuService.findAll();
            Map<String, SysMenu> menuMap = new HashMap<>();
            for(SysMenu menu:all){
                menuMap.put(menu.getId(), menu);
            }
            for(String v : vessel){
                for(SysMenu menu:all){
                    if(v.equals(menu.getId())){
                        String menuId = menu.getPid();
                        while(true){
                            if("0".equals(menuId)){
                                break;
                            }else{
                                SysMenu parent = menuMap.get(menuId);
                                if(parent!=null){
                                    menuId = parent.getPid();
                                    parentNode.add(parent.getId());
                                }else{
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            for(String p : parentNode){
                if(!permissions.contains(p)){
                    permissions.add(p);
                }
            }
        }

        //更新角色菜单
        Set<SysRoleMenu> roleMenuList = roleMenuMapper.findByRoleId(id);
        List<SysRoleMenu> deleteList = Lists.newArrayList();//需要删除的角色菜单
        List<SysRoleMenu> addList = Lists.newArrayList();//需要新增的角色菜单
        if(permissions==null||permissions.size()==0){
            //删除全部
            for(SysRoleMenu rm:roleMenuList){
                rm.setDelFlag(SysRoleMenu.DEL_FLAG_DELETE);//删除
                deleteList.add(rm);
            }
        }else{
            //确定需要新增的角色菜单
            for(String permission:permissions){
                boolean has = false;
                for(SysRoleMenu rm:roleMenuList){
                    if(rm.getMenuId().equals(permission)){
                        has = true;
                        break;
                    }
                }
                if(!has){//需要新增
                    SysRoleMenu rm = new SysRoleMenu();
                    rm.setRoleId(role.getId());
                    rm.setMenuId(permission);
                    addList.add(rm);
                }
            }

            //确定需要删除的菜单
            for(SysRoleMenu rm:roleMenuList){
                boolean has = false;
                for(String permission:permissions){
                    if(rm.getMenuId().equals(permission)){
                        has = true;
                        break;
                    }
                }
                if(!has){
                    rm.setDelFlag(SysRoleMenu.DEL_FLAG_DELETE);//删除
                    deleteList.add(rm);
                }
            }
        }

        //更新
        UserUtils.preUpdate(role);
        roleMapper.updateById(role);

        for(SysRoleMenu rm:addList){
            UserUtils.preAdd(rm);
            roleMenuMapper.insert(rm);
        }

        for(SysRoleMenu rm:deleteList){
            UserUtils.preUpdate(rm);
            roleMenuMapper.updateById(rm);
            roleMenuMapper.deleteById(rm.getId());
        }
    }

    public List<SysRoleMenu> findRoleMenusByRoleId(String roleId){
        List<String> munuIds = new ArrayList<>();
        List<SysMenu> all = menuService.findAll();
        Map<String, SysMenu> menuMapPid = new HashMap<>();
        for(SysMenu menu:all){
            menuMapPid.put(menu.getPid(), menu);
        }
        for(SysMenu menu:all){
            SysMenu munu = menuMapPid.get(menu.getId());
            if(munu == null){
                munuIds.add(menu.getId());
            }
        }

        List<SysRoleMenu> roleMenus = new ArrayList<>();
        Set<SysRoleMenu> list = roleMenuMapper.findByRoleId(roleId);
        for(SysRoleMenu l : list){
            for(String munuId : munuIds){
                if((l.getMenuId()).equals(munuId)){
                    roleMenus.add(l);
                    break;
                }
            }
        }
        return roleMenus;
    }

    /**
     * 删除角色，同时删除关联的角色菜单，删除关联的用户角色
     */
    @Transactional
    public boolean delete(String id) {
        roleMapper.deleteById(id);
        roleMenuMapper.delByRoleId(id);
        userRoleMapper.delByRoleId(id);
        return true;
    }

}

