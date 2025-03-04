package com.glface.modules.sp.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.glface.modules.sp.mapper.ExpertAvoidOrgMapper;
import com.glface.modules.sp.model.ExpertAvoidOrg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
public class ExpertAvoidOrgService {

    @Resource
    private ExpertAvoidOrgMapper avoidOrgMapper;

    public ExpertAvoidOrg get(String id) {
        return avoidOrgMapper.selectById(id);
    }

    public List<ExpertAvoidOrg> findByExpertId(String expertId){
        LambdaQueryWrapper<ExpertAvoidOrg> queryWrapper = Wrappers.<ExpertAvoidOrg>query().lambda();
        queryWrapper.eq(ExpertAvoidOrg::getExpertId, expertId)
                .eq(ExpertAvoidOrg::getDelFlag,ExpertAvoidOrg.DEL_FLAG_NORMAL);
        return avoidOrgMapper.selectList(queryWrapper);
    }

}

