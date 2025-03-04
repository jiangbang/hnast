package com.glface.modules.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.glface.base.bean.Page;
import com.glface.base.utils.StringUtils;
import com.glface.common.exeception.ServiceException;
import com.glface.modules.mapper.ProjectCategoryMapper;
import com.glface.modules.model.ProjectCategory;
import com.glface.modules.sys.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.util.List;

import static com.glface.common.web.ApiCode.*;

@Slf4j
@Service
@Transactional(readOnly = true)
public class ProjectCategoryService {
    @Resource
    private ProjectCategoryMapper projectCategoryMapper;

    public ProjectCategory get(String id){
        return projectCategoryMapper.selectById(id);
    }

    public List<ProjectCategory> all(){
        LambdaQueryWrapper<ProjectCategory> queryWrapper = Wrappers.<ProjectCategory>query().lambda();
        queryWrapper.eq(ProjectCategory::getDelFlag,ProjectCategory.DEL_FLAG_NORMAL).orderByAsc(ProjectCategory::getCreateDate);
        return projectCategoryMapper.selectList(queryWrapper);
    }

    public Page<ProjectCategory> pageSearch(Page<ProjectCategory> page, ProjectCategory projectCategory) {
        page.setCount(projectCategoryMapper.pageSearchCount(projectCategory));
        page.setList(projectCategoryMapper.pageSearch(page,projectCategory));
        return page;
    }

    /**
     * 新增
     */
    @Transactional
    public void create(String name, float amountMax, float amountMin, String remark) {
        name = StringUtils.trim(name);

        if (StringUtils.isBlank(name)) {
            throw new ServiceException(PROJECT_CATEGORY_NAME_REQUIRED);
        }
        if (amountMax<=0.0) {
            throw new ServiceException(PROJECT_CATEGORY_AMOUNTMAX_ERROR);
        }
        if(amountMax<amountMin){
            throw new ServiceException(PROJECT_CATEGORY_AMOUNTMAX_SMALL);
        }

        ProjectCategory category = findByName(name);
        if(category!=null){
            throw new ServiceException(PROJECT_CATEGORY_NAME_EXIST);
        }
        // 创建
        category = new ProjectCategory();
        category.setName(name);
        category.setAmountMax(amountMax);
        category.setAmountMin(amountMin);
        category.setRemark(remark);
        UserUtils.preAdd(category);
        projectCategoryMapper.insert(category);
    }

    /**
     * 编辑
     */
    @Transactional
    public void update(String id, String name, float amountMax, float amountMin, String remark) {
        name = StringUtils.trim(name);
        // 数据验证
        ProjectCategory category = get(id);
        if (category == null) {
            throw new ServiceException(PROJECT_CATEGORY_NOTEXIST);
        }
        if (StringUtils.isBlank(name)) {
            throw new ServiceException(PROJECT_CATEGORY_NAME_REQUIRED);
        }
        if (amountMax<=0.0) {
            throw new ServiceException(PROJECT_CATEGORY_AMOUNTMAX_ERROR);
        }
        if(amountMax<amountMin){
            throw new ServiceException(PROJECT_CATEGORY_AMOUNTMAX_SMALL);
        }

        ProjectCategory category2 = findByName(name);
        if(category2!=null&&!category.getId().equals(category2.getId())){
            throw new ServiceException(PROJECT_CATEGORY_NAME_EXIST);
        }

        // 修改
        category.setName(name);
        category.setAmountMax(amountMax);
        category.setAmountMin(amountMin);
        category.setRemark(remark);

        //存储
        UserUtils.preUpdate(category);
        projectCategoryMapper.updateById(category);
    }

    public ProjectCategory findByName(String name){
        LambdaQueryWrapper<ProjectCategory> queryWrapper = Wrappers.<ProjectCategory>query().lambda()
                .eq(ProjectCategory::getName, name)
                .eq(ProjectCategory::getDelFlag,ProjectCategory.DEL_FLAG_NORMAL);
        return projectCategoryMapper.selectOne(queryWrapper);
    }

    @Transactional
    public void delete(String id) {
        projectCategoryMapper.deleteById(id);
    }

 }
