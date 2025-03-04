package com.glface.modules.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.glface.base.bean.Page;
import com.glface.model.SysOffice;
import com.glface.modules.mapper.ProjectTemplateCategoryMapper;
import com.glface.modules.model.ProjectCommonWord;
import com.glface.modules.model.ProjectTemplate;
import com.glface.modules.model.ProjectTemplateCategory;
import com.glface.modules.sys.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
public class ProjectTemplateCategoryService {

    @Resource
    private ProjectTemplateCategoryMapper templateCategoryMapper;

    public ProjectTemplateCategory get(String id){
        return templateCategoryMapper.selectById(id);
    }
    public List<ProjectTemplateCategory> findAll() {
        LambdaQueryWrapper<ProjectTemplateCategory> queryWrapper = Wrappers.<ProjectTemplateCategory>query().lambda()
                .eq(ProjectTemplateCategory::getDelFlag, 0);
        return templateCategoryMapper.selectList(queryWrapper);
    }

    public Page<ProjectTemplateCategory> pageSearch(Page<ProjectTemplateCategory> page, ProjectTemplateCategory category) {
        page.setCount(templateCategoryMapper.pageSearchCount(category));
        page.setList(templateCategoryMapper.pageSearch(page,category));
        return page;
    }

    @Transactional
    public void create(String name, String remark) {
        // 创建
        ProjectTemplateCategory category = new ProjectTemplateCategory();
        category.setName(name);
        category.setRemark(remark);
        UserUtils.preAdd(category);
        templateCategoryMapper.insert(category);
    }

    /**
     * 编辑
     */
    @Transactional
    public void update(String id, String name, String remark) {

        ProjectTemplateCategory category = get(id);
        // 修改
        category.setName(name);
        category.setRemark(remark);
        //存储
        UserUtils.preUpdate(category);
        templateCategoryMapper.updateById(category);
    }


    @Transactional
    public void delete(String id) {
        templateCategoryMapper.deleteById(id);
    }

 }
