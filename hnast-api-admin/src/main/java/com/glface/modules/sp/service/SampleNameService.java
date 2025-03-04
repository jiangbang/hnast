package com.glface.modules.sp.service;


import com.glface.base.utils.IdGen;
import com.glface.modules.sp.mapper.SampleNameMapper;
import com.glface.modules.sp.model.SampleName;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.glface.modules.sys.utils.UserUtils;

import javax.annotation.Resource;

/**
 * 组别名称
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class SampleNameService {

    @Resource
    private SampleNameMapper sampleNameMapper;

    @Transactional
    public SampleName createSampleName(SampleName sampleName){
        sampleName.setId(IdGen.uuid());
        sampleName.setSampleId(sampleName.getSampleId());
        sampleName.setName(sampleName.getName());
        UserUtils.preAdd(sampleName);
        sampleNameMapper.insert(sampleName);
        return sampleName;
    }
}
