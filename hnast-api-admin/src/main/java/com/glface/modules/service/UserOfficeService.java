package com.glface.modules.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.glface.model.SysOffice;
import com.glface.model.SysUserOffice;
import com.glface.modules.mapper.UserOfficeMapper;
import com.glface.modules.sys.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@Transactional(readOnly = true)
public class UserOfficeService{

    @Resource
    private UserOfficeMapper userOfficeMapper;

    public Set<SysOffice> findOfficesByUserId(String userId) {
        return userOfficeMapper.selectOfficesByUserId(userId);
    }
    public List<SysUserOffice> findUserOfficesByUserId(String userId) {
        LambdaQueryWrapper<SysUserOffice> queryWrapper = Wrappers.<SysUserOffice>query().lambda()
                .eq(SysUserOffice::getUserId, userId)
                .eq(SysUserOffice::getDelFlag,SysUserOffice.DEL_FLAG_NORMAL);
        return userOfficeMapper.selectList(queryWrapper);
    }

    public SysUserOffice get(String id) {
        return userOfficeMapper.selectById(id);
    }

    @Transactional
    public void add(SysUserOffice userOffice) {
        UserUtils.preAdd(userOffice);
        userOfficeMapper.insert(userOffice);
    }

    @Transactional
    public void update(SysUserOffice userOffice) {
        UserUtils.preUpdate(userOffice);
        userOfficeMapper.updateById(userOffice);
    }

    @Transactional
    public boolean delete(String id) {
        return userOfficeMapper.deleteById(id) > 0;
    }
}

