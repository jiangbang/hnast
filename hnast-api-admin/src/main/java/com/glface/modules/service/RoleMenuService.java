package com.glface.modules.service;

import com.glface.model.SysMenu;
import com.glface.model.SysRoleMenu;
import com.glface.modules.mapper.RoleMenuMapper;
import com.glface.modules.sys.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Set;

@Slf4j
@Service
@Transactional(readOnly = true)
public class RoleMenuService{

    @Resource
    private RoleMenuMapper roleMenuMapper;

    @Transactional
    public void add(SysRoleMenu roleMenu) {
        UserUtils.preAdd(roleMenu);
        roleMenuMapper.insert(roleMenu);
    }

    @Transactional
    public boolean delete(String id) {
        return roleMenuMapper.deleteById(id) > 0;
    }

    @Transactional
    public void update(SysRoleMenu roleMenu) {
        UserUtils.preUpdate(roleMenu);
        roleMenuMapper.updateById(roleMenu);
    }

    public SysRoleMenu get(String id) {
        return roleMenuMapper.selectById(id);
    }

    public Set<SysMenu> findMenusByRoleId(String roleId) {
        return roleMenuMapper.findMenusByRoleId(roleId);
    }
}

