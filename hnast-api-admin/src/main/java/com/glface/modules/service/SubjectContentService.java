package com.glface.modules.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.glface.base.utils.StringUtils;
import com.glface.common.exeception.ServiceException;
import com.glface.modules.mapper.SubjectContentMapper;
import com.glface.modules.model.ProjectOrg;
import com.glface.modules.model.ProjectStage;
import com.glface.modules.model.SubjectContent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import com.glface.modules.sys.utils.UserUtils;

import static com.glface.common.web.ApiCode.PROJECT_NOT_EXIST;
import static com.glface.common.web.ApiCode.PROJECT_ORG_NOT_EXIST;

@Slf4j
@Service
@Transactional(readOnly = true)
public class SubjectContentService {
    @Resource
    private SubjectContentMapper subjectContentMapper;
    @Resource
    private ProjectService projectService;

    public SubjectContent get(String id){
        return subjectContentMapper.selectById(id);
    }

    public int insertOrUpdate(SubjectContent subjectContent){
        if(StringUtils.isBlank(subjectContent.getId())) {
            UserUtils.preAdd(subjectContent);
            return subjectContentMapper.insert(subjectContent);
        } else {
            UserUtils.preUpdate(subjectContent);
            return subjectContentMapper.updateById(subjectContent);
        }
    }

    public SubjectContent findByProjectId(String projectId){
        LambdaQueryWrapper<SubjectContent> queryWrapper = Wrappers.<SubjectContent>query().lambda();
        queryWrapper.eq(SubjectContent::getProjectId, projectId);
        return subjectContentMapper.selectOne(queryWrapper);
    }

    @Transactional(readOnly = false)
    public void delete(String id) {
        subjectContentMapper.deleteById(id);
    }

    public void update(String projectId, String background, String scheme, String conditions) {
        if(StringUtils.isBlank(projectId)||projectService.get(projectId)==null){
            throw new ServiceException(PROJECT_NOT_EXIST);
        }

        SubjectContent subjectContent = get(projectId);
        if (subjectContent == null) {
            throw new ServiceException(PROJECT_ORG_NOT_EXIST);
        }
        if (!StringUtils.isBlank(background)) subjectContent.setBackground(background);
        if (!StringUtils.isBlank(scheme)) subjectContent.setScheme(scheme);
        if (!StringUtils.isBlank(conditions)) subjectContent.setConditions(conditions);
        subjectContentMapper.updateById(subjectContent);
    }

}
