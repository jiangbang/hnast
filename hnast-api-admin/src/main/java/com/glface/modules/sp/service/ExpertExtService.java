package com.glface.modules.sp.service;

import com.glface.modules.sp.mapper.ExpertExtMapper;
import com.glface.modules.sp.model.ExpertExt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Slf4j
@Service
@Transactional(readOnly = true)
public class ExpertExtService {
    @Resource
    private ExpertExtMapper expertExtMapper;

    public ExpertExt get(String id){
        return expertExtMapper.selectById(id);
    }
 }
