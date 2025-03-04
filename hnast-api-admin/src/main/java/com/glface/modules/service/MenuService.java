package com.glface.modules.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.glface.base.utils.StringUtils;
import com.glface.common.exeception.ServiceException;
import com.glface.common.web.ApiCode;
import com.glface.model.SysMenu;
import com.glface.modules.mapper.MenuMapper;
import com.glface.modules.mapper.RoleMenuMapper;
import com.glface.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@Transactional(readOnly = true)
public class MenuService{

    @Resource
    private MenuMapper menuMapper;

    @Resource
    private RoleMenuMapper roleMenuMapper;

    public SysMenu get(String id) {
        return menuMapper.selectById(id);
    }

    /**
     * 新增菜单
     * Transactional readOnly默认为false
     * @param pid 上级菜单
     * @param name	名称
     * @param permission	权限标识
     * @param sort	排序(升序)
     * @param isShow	保留字段 1展示 0不展示
     */
    @Transactional
    public void create(String pid,String name,String permission,int sort,int isShow) {
        if(StringUtils.isBlank(pid)){
            pid="0";
        }
        //验证
        if(StringUtils.isBlank(name)){
            throw new ServiceException(ApiCode.PERMISSION_MENU_NAME_REQUIRED);
        }
        if(StringUtils.isBlank(permission)){
            throw new ServiceException(ApiCode.PERMISSION_MENU_CODE_REQUIRED);
        }

        //设置code值
        String code = "";
        SysMenu parent = menuMapper.selectById(pid);
        if(parent!=null){
            code = parent.getCode();
        }
        String maxCode = menuMapper.findMaxCodeById(pid);
        if(StringUtils.isBlank(maxCode)){
            code = code+"000";
        }else{
            String childCode = maxCode.substring(maxCode.length()-3);
            int cCode = Integer.valueOf(childCode);
            cCode = cCode +1;
            if(cCode<=9){
                code = code +"00"+cCode;
            }else if(cCode<=99){
                code = code +"0"+cCode;
            }else{
                code = code + cCode;
            }
        }

        SysMenu menu = new SysMenu();
        menu.setCode(code);
        menu.setName(name);
        menu.setPid(pid);
        menu.setSort(sort);
        menu.setIsShow(isShow);
        menu.setPermission(permission);

        UserUtils.preAdd(menu);
        menuMapper.insert(menu);

    }

    /**
     * 更新菜单
     * @param id 菜单id
     * @param name	名称
     * @param permission	权限标识
     * @param sort	排序(升序)
     * @param isShow  保留字段 1展示 0不展示
     */
    @Transactional
    public void update(String id,String name,String permission,int sort,int isShow) {

        //验证
        if(StringUtils.isBlank(id)){
            throw new ServiceException(ApiCode.PERMISSION_MENU_ID_REQUIRED);
        }

        if(StringUtils.isBlank(name)){
            throw new ServiceException(ApiCode.PERMISSION_MENU_NAME_REQUIRED);
        }

        SysMenu menu = menuMapper.selectById(id);
        if(menu==null){
            throw new ServiceException(ApiCode.PERMISSION_MENU_CODE_REQUIRED);
        }

        //更新menu信息
        menu.setName(name);
        menu.setSort(sort);
        menu.setPermission(permission);
        menu.setIsShow(isShow);

        UserUtils.preUpdate(menu);
        menuMapper.updateById(menu);
    }


    /**
     * 删除菜单，并删除关联的角色菜单，如果有未删除的子菜单，则不允许删除
     */
    @Transactional
    public boolean delete(String id) {
        SysMenu menu = menuMapper.selectById(id);
        if(menu!=null){
            Set childrens = menuMapper.childrens(menu.getId());
            if(childrens.size()>0){
                throw new ServiceException(ApiCode.PERMISSION_MENU_CHILDREN_EXIST);
            }
            menuMapper.deleteById(menu.getId());
            roleMenuMapper.delByMenuId(id);
        }
        return true;
    }

    public List<SysMenu> findAll() {
        LambdaQueryWrapper<SysMenu> queryWrapper = Wrappers.<SysMenu>query().lambda()
                .eq(SysMenu::getDelFlag, 0);
        return menuMapper.selectList(queryWrapper);
    }

    /**
     * 返回树形结构的menu，最上面的节点为pid=0的节点
     */
    public List<SysMenu> allListTree(){
        List<SysMenu> menuList = Lists.newArrayList();
        LambdaQueryWrapper<SysMenu> queryWrapper = Wrappers.<SysMenu>query().lambda()
                .eq(SysMenu::getDelFlag, 0).orderByAsc(SysMenu::getSort);
        List<SysMenu>  all=menuMapper.selectList(queryWrapper);

        Map<String, SysMenu> menuMap = new HashMap<>();
        for(SysMenu menu:all){
            menuMap.put(menu.getId(), menu);
        }
        for(SysMenu menu:all){
            if("0".equals(menu.getPid())){
                menuList.add(menu);
            }else{
                SysMenu parent = menuMap.get(menu.getPid());
                if(parent!=null){
                    parent.addChild(menu);
                }
            }
        }
        return menuList;
    }
}

