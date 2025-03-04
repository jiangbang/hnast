package com.glface.modules.sp.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.glface.modules.sp.mapper.ExpertTechnicalTitleMapper;
import com.glface.modules.sp.model.ExpertTechnicalTitle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 专家技术职称
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class ExpertTechnicalTitleService {

    @Resource
    private ExpertTechnicalTitleMapper technicalTitleMapper;

    public ExpertTechnicalTitle get(String id) {
        return technicalTitleMapper.selectById(id);
    }

    public List<ExpertTechnicalTitle> findByExpertId(String expertId){
        LambdaQueryWrapper<ExpertTechnicalTitle> queryWrapper = Wrappers.<ExpertTechnicalTitle>query().lambda();
        queryWrapper.eq(ExpertTechnicalTitle::getExpertId, expertId)
                .eq(ExpertTechnicalTitle::getDelFlag,ExpertTechnicalTitle.DEL_FLAG_NORMAL);
        return technicalTitleMapper.selectList(queryWrapper);
    }

}

