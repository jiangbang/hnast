package com.glface.modules.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.glface.base.bean.Page;
import com.glface.base.utils.StringUtils;
import com.glface.common.exeception.ServiceException;
import com.glface.modules.mapper.ProjectSpecialMapper;
import com.glface.modules.model.ProjectSpecial;
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
public class ProjectSpecialService {
    @Resource
    private ProjectSpecialMapper projectSpecialMapper;

    public ProjectSpecial get(String id){
        return projectSpecialMapper.selectById(id);
    }

    public List<ProjectSpecial> all(){
        LambdaQueryWrapper<ProjectSpecial> queryWrapper = Wrappers.<ProjectSpecial>query().lambda();
        queryWrapper.eq(ProjectSpecial::getDelFlag,ProjectSpecial.DEL_FLAG_NORMAL);
        return projectSpecialMapper.selectList(queryWrapper);
    }


    public Page<ProjectSpecial> pageSearch(Page<ProjectSpecial> page, ProjectSpecial projectSpecial) {
        page.setCount(projectSpecialMapper.pageSearchCount(projectSpecial));
        page.setList(projectSpecialMapper.pageSearch(page,projectSpecial));
        return page;
    }

    /**
     * 新增
     */
    @Transactional
    public void create(String name, String planTypeId,String remark) {
        name = StringUtils.trim(name);

        if (StringUtils.isBlank(name)) {
            throw new ServiceException(PROJECT_SPECIAL_NAME_REQUIRED);
        }

        ProjectSpecial special = findByName(name);
        if(special!=null){
            throw new ServiceException(PROJECT_SPECIAL_NAME_EXIST);
        }
        // 创建
        special = new ProjectSpecial();
        special.setName(name);
        special.setPlanTypeId(planTypeId);
        special.setRemark(remark);

        UserUtils.preAdd(special);
        projectSpecialMapper.insert(special);
    }

    /**
     * 编辑
     */
    @Transactional
    public void update(String id, String name,String planTypeId, String remark) {
        name = StringUtils.trim(name);
        // 数据验证
        ProjectSpecial special = get(id);
        if (special == null) {
            throw new ServiceException(PROJECT_SPECIAL_NOTEXIST);
        }
        if (StringUtils.isBlank(name)) {
            throw new ServiceException(PROJECT_SPECIAL_NAME_REQUIRED);
        }

        ProjectSpecial special2 = findByName(name);
        if(special2!=null&&!special.getId().equals(special2.getId())){
            throw new ServiceException(PROJECT_SPECIAL_NAME_EXIST);
        }

        // 修改
        special.setName(name);
        special.setPlanTypeId(planTypeId);
        special.setRemark(remark);

        //存储
        UserUtils.preUpdate(special);
        projectSpecialMapper.updateById(special);
    }

    public ProjectSpecial findByName(String name){
        LambdaQueryWrapper<ProjectSpecial> queryWrapper = Wrappers.<ProjectSpecial>query().lambda()
                .eq(ProjectSpecial::getName, name)
                .eq(ProjectSpecial::getDelFlag,ProjectSpecial.DEL_FLAG_NORMAL);
        return projectSpecialMapper.selectOne(queryWrapper);
    }

    public List<ProjectSpecial> findByPlanTypeId(String planTypeId){
        LambdaQueryWrapper<ProjectSpecial> queryWrapper = Wrappers.<ProjectSpecial>query().lambda()
                .eq(ProjectSpecial::getPlanTypeId, planTypeId)
                .eq(ProjectSpecial::getDelFlag,ProjectSpecial.DEL_FLAG_NORMAL);
        return projectSpecialMapper.selectList(queryWrapper);
    }


    @Transactional
    public void delete(String id) {
        projectSpecialMapper.deleteById(id);
    }

 }
