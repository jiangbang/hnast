package com.glface.modules.sp.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.glface.modules.sp.mapper.ExpertCategoryMapper;
import com.glface.modules.sp.model.ExpertCategory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
public class ExpertCategoryService {

    @Resource
    private ExpertCategoryMapper categoryMapper;

    public ExpertCategory get(String id) {
        return categoryMapper.selectById(id);
    }

    public List<ExpertCategory> findByExpertId(String expertId){
        LambdaQueryWrapper<ExpertCategory> queryWrapper = Wrappers.<ExpertCategory>query().lambda();
        queryWrapper.eq(ExpertCategory::getExpertId, expertId)
                .eq(ExpertCategory::getDelFlag,ExpertCategory.DEL_FLAG_NORMAL);
        return categoryMapper.selectList(queryWrapper);
    }

}

