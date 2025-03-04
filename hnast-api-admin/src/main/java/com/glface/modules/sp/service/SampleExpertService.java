package com.glface.modules.sp.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.glface.modules.sp.mapper.SampleExpertMapper;
import com.glface.modules.sp.model.SampleExpert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 专家抽取结果
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class SampleExpertService {
    @Resource
    private SampleExpertMapper sampleExpertMapper;

    public SampleExpert get(String id) {
        return sampleExpertMapper.selectById(id);
    }

    public List<SampleExpert> findAll() {
        LambdaQueryWrapper<SampleExpert> queryWrapper = Wrappers.<SampleExpert>query().lambda();
        queryWrapper.eq(SampleExpert::getDelFlag, SampleExpert.DEL_FLAG_NORMAL);
        return sampleExpertMapper.selectList(queryWrapper);
    }


    public List<SampleExpert> findBySampleId(String sampleId) {
        LambdaQueryWrapper<SampleExpert> queryWrapper = Wrappers.<SampleExpert>query().lambda();
        queryWrapper.eq(SampleExpert::getDelFlag, SampleExpert.DEL_FLAG_NORMAL)
                .eq(SampleExpert::getSampleId, sampleId)
                .orderByAsc(SampleExpert::getCreateDate);
        return sampleExpertMapper.selectList(queryWrapper);
    }

    @Transactional
    public void delete(String id) {
        sampleExpertMapper.deleteById(id);
    }

}
