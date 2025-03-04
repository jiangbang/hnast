package com.glface.modules.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.glface.base.bean.Page;
import com.glface.base.utils.StringUtils;
import com.glface.common.exeception.ServiceException;
import com.glface.common.web.ApiCode;
import com.glface.model.SysDict;
import com.glface.modules.mapper.ProjectTemplateMapper;
import com.glface.modules.model.Project;
import com.glface.modules.model.ProjectCategory;
import com.glface.modules.model.ProjectTemplate;
import com.glface.modules.utils.ProjectStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.glface.modules.sys.utils.UserUtils;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
public class ProjectTemplateService {
    @Resource
    private ProjectTemplateMapper projectTemplateMapper;

    public ProjectTemplate get(String id){
        return projectTemplateMapper.selectById(id);
    }


    public List<ProjectTemplate> all(){
        LambdaQueryWrapper<ProjectTemplate> queryWrapper = Wrappers.<ProjectTemplate>query().lambda();
        queryWrapper.eq(ProjectTemplate::getDelFlag,ProjectTemplate.DEL_FLAG_NORMAL);
        return projectTemplateMapper.selectList(queryWrapper);
    }
    public List<ProjectTemplate> all(String templateCategoryId,String name){
        LambdaQueryWrapper<ProjectTemplate> queryWrapper = Wrappers.<ProjectTemplate>query().lambda();
        queryWrapper.eq(ProjectTemplate::getDelFlag,ProjectTemplate.DEL_FLAG_NORMAL);
        if(StringUtils.isNotBlank(name)){
            queryWrapper.and(wq -> wq.eq(ProjectTemplate::getCategoryId, templateCategoryId).eq(ProjectTemplate::getName, name)).orderByDesc(ProjectTemplate::getCreateDate);;
        }else {
            queryWrapper.and(wq -> wq
                    .eq(ProjectTemplate::getCategoryId, templateCategoryId)).orderByDesc(ProjectTemplate::getCreateDate);
        }
        return projectTemplateMapper.selectList(queryWrapper);
    }
    public Page<ProjectTemplate> pageSearch(Page<ProjectTemplate> page, ProjectTemplate template) {
        page.setCount(projectTemplateMapper.pageSearchCount(template));
        page.setList(projectTemplateMapper.pageSearch(page,template));
        return page;
    }

    @Transactional
    public void create(String name,String fileId,String categoryId,int sort,String remark) {
        if (StringUtils.isBlank(name)) {
            throw new ServiceException(ApiCode.PROJECT_TEMPLATE_NAME_REQUIRED);
        }
        if (StringUtils.isBlank(fileId)) {
            throw new ServiceException(ApiCode.PROJECT_TEMPLATE_FILEID_REQUIRED);
        }
        if (StringUtils.isBlank(categoryId)) {
            throw new ServiceException(ApiCode.PROJECT_TEMPLATE_CATE_NOT_EXIST);
        }
        ProjectTemplate template = new ProjectTemplate();
        template.setName(name);
        template.setFileId(fileId);
        template.setCategoryId(categoryId);
        template.setSort(sort);
        template.setRemark(remark);
        UserUtils.preAdd(template);
        projectTemplateMapper.insert(template);
    }

    @Transactional
    public void update(String id, String name,String fileId,String categoryId,int sort,String remark) {

        // 验证
        if (StringUtils.isBlank(id)) {
            throw new ServiceException(ApiCode.PROJECT_TEMPLATE_ID_REQUIRED);
        }

        if (StringUtils.isBlank(name)) {
            throw new ServiceException(ApiCode.PROJECT_TEMPLATE_NAME_REQUIRED);
        }
        if (StringUtils.isBlank(fileId)) {
            throw new ServiceException(ApiCode.PROJECT_TEMPLATE_FILEID_REQUIRED);
        }
        if (StringUtils.isBlank(categoryId)) {
            throw new ServiceException(ApiCode.PROJECT_TEMPLATE_CATE_NOT_EXIST);
        }
        ProjectTemplate template = projectTemplateMapper.selectById(id);
        if (template == null) {
            throw new ServiceException(ApiCode.PROJECT_TEMPLATE_NOT_EXIST);
        }

        // 更新
        template.setName(name);
        template.setFileId(fileId);
        template.setCategoryId(categoryId);
        template.setSort(sort);
        template.setRemark(remark);
        UserUtils.preUpdate(template);
        projectTemplateMapper.updateById(template);

    }


    /**
     * 删除
     */
    @Transactional
    public void delete(String id)
    {
        projectTemplateMapper.deleteById(id);
    }

}
