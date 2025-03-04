package com.glface.modules.sp.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.glface.modules.sp.mapper.ExpertMajorMapper;
import com.glface.modules.sp.model.ExpertMajor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
public class ExpertMajorService {

    @Resource
    private ExpertMajorMapper majorMapper;

    public ExpertMajor get(String id) {
        return majorMapper.selectById(id);
    }

    public List<ExpertMajor> findByExpertId(String expertId){
        LambdaQueryWrapper<ExpertMajor> queryWrapper = Wrappers.<ExpertMajor>query().lambda();
        queryWrapper.eq(ExpertMajor::getExpertId, expertId)
                .eq(ExpertMajor::getDelFlag,ExpertMajor.DEL_FLAG_NORMAL);
        return majorMapper.selectList(queryWrapper);
    }

}

