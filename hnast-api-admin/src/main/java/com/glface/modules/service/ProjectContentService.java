package com.glface.modules.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.glface.base.utils.StringUtils;
import com.glface.common.exeception.ServiceException;
import com.glface.modules.model.ProjectContent;
import com.glface.modules.mapper.ProjectContentMapper;
import com.glface.modules.model.ProjectOrg;
import com.glface.modules.model.SubjectContent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.glface.modules.sys.utils.UserUtils;

import javax.annotation.Resource;

import static com.glface.common.web.ApiCode.PROJECT_NOT_EXIST;
import static com.glface.common.web.ApiCode.PROJECT_ORG_NOT_EXIST;

@Slf4j
@Service
@Transactional(readOnly = true)
public class ProjectContentService {
    @Resource
    private ProjectContentMapper contentMapper;
    @Resource
    private ProjectService projectService;

    public ProjectContent get(String id){
        return contentMapper.selectById(id);
    }

    public int insertOrUpdate(ProjectContent content){
        if(StringUtils.isBlank(content.getId())) {
            UserUtils.preAdd(content);
            return contentMapper.insert(content);
        } else {
            UserUtils.preUpdate(content);
            return contentMapper.updateById(content);
        }
    }
    public ProjectContent findByProjectId(String projectId){
        LambdaQueryWrapper<ProjectContent> queryWrapper = Wrappers.<ProjectContent>query().lambda();
        queryWrapper.eq(ProjectContent::getProjectId, projectId);
        return contentMapper.selectOne(queryWrapper);
    }

    @Transactional(readOnly = false)
    public void delete(String id) {
        contentMapper.deleteById(id);
    }

    public void update(String projectId, String basis, String content, String target,String conditions){
        if(StringUtils.isBlank(projectId)||projectService.get(projectId)==null){
            throw new ServiceException(PROJECT_NOT_EXIST);
        }

        ProjectContent projectContent = get(projectId);
        if (projectContent == null) throw new ServiceException(PROJECT_ORG_NOT_EXIST);

        if (!StringUtils.isBlank(basis)) projectContent.setBasis(basis);
        if (!StringUtils.isBlank(content)) projectContent.setContent(content);
        if (!StringUtils.isBlank(target)) projectContent.setTarget(target);
        if (!StringUtils.isBlank(conditions)) projectContent.setConditions(conditions);
        contentMapper.updateById(projectContent);
    }
}
