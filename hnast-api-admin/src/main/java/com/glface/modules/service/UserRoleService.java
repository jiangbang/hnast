package com.glface.modules.service;

import com.glface.model.SysRole;
import com.glface.model.SysUserRole;
import com.glface.modules.mapper.UserRoleMapper;
import com.glface.modules.sys.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Set;

@Slf4j
@Service
@Transactional(readOnly = true)
public class UserRoleService{

    @Resource
    private UserRoleMapper userRoleMapper;

    @Transactional
    public void add(SysUserRole userRole) {
        UserUtils.preAdd(userRole);
        userRoleMapper.insert(userRole);
    }

    @Transactional
    public boolean delete(String id) {
        return userRoleMapper.deleteById(id) > 0;
    }

    @Transactional
    public void update(SysUserRole userRole) {
        UserUtils.preUpdate(userRole);
        userRoleMapper.updateById(userRole);
    }

    public SysUserRole get(String id) {
        return userRoleMapper.selectById(id);
    }

    public Set<SysUserRole> findUserRolesByUserId(String userId) {
        return userRoleMapper.findUserRolesByUserId(userId);
    }

    @Transactional
    public void insert(SysUserRole sysUserRole) {
        userRoleMapper.insert(sysUserRole);
    }
}

