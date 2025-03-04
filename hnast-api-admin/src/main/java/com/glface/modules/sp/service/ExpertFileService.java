package com.glface.modules.sp.service;

import com.glface.modules.sp.mapper.ExpertFileMapper;
import com.glface.modules.sp.model.ExpertFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 专家附件表
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class ExpertFileService {

    @Resource
    private ExpertFileMapper expertFileMapper;

    public ExpertFile get(String id) {
        return expertFileMapper.selectById(id);
    }

}

