package com.glface.modules.sp.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.glface.modules.sp.mapper.ExpertQualificationMapper;
import com.glface.modules.sp.model.ExpertQualification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 专家职业资格
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class ExpertQualificationService {

    @Resource
    private ExpertQualificationMapper qualificationMapper;

    public ExpertQualification get(String id) {
        return qualificationMapper.selectById(id);
    }

    public List<ExpertQualification> findByExpertId(String expertId){
        LambdaQueryWrapper<ExpertQualification> queryWrapper = Wrappers.<ExpertQualification>query().lambda();
        queryWrapper.eq(ExpertQualification::getExpertId, expertId)
                .eq(ExpertQualification::getDelFlag,ExpertQualification.DEL_FLAG_NORMAL);
        return qualificationMapper.selectList(queryWrapper);
    }

}

