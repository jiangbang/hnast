package com.glface.modules.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.glface.modules.mapper.ProjectFileMapper;
import com.glface.modules.model.ProjectFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
public class ProjectFileService {
    @Resource
    private ProjectFileMapper projectFileMapper;

    public ProjectFile get(String id){
        return projectFileMapper.selectById(id);
    }

    public List<ProjectFile> findByProjectId(String projectId){
        LambdaQueryWrapper<ProjectFile> queryWrapper = Wrappers.<ProjectFile>query().lambda();
        queryWrapper.eq(ProjectFile::getProjectId, projectId)
                .eq(ProjectFile::getDelFlag,ProjectFile.DEL_FLAG_NORMAL);
        return projectFileMapper.selectList(queryWrapper);
    }

    public List<ProjectFile> findByProjectIdAndType(String projectId,String type){
        LambdaQueryWrapper<ProjectFile> queryWrapper = Wrappers.<ProjectFile>query().lambda();
        queryWrapper.eq(ProjectFile::getProjectId, projectId)
                .eq(ProjectFile::getType, type)
                .eq(ProjectFile::getDelFlag,ProjectFile.DEL_FLAG_NORMAL);
        return projectFileMapper.selectList(queryWrapper);
    }
}
