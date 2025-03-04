package com.glface.modules.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.glface.base.bean.Page;
import com.glface.base.utils.StringUtils;
import com.glface.common.exeception.ServiceException;
import com.glface.model.SysArea;
import com.glface.model.SysOffice;
import com.glface.modules.mapper.OfficeMapper;
import com.glface.modules.mapper.ProjectPlanTypeMapper;
import com.glface.modules.model.ProjectPlanType;
import com.glface.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.glface.common.web.ApiCode.*;

/**
 * 项目计划类型
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class ProjectPlanTypeService {
    @Resource
    private ProjectPlanTypeMapper projectPlanTypeMapper;

    @Resource
    private OfficeMapper officeMapper;


    public ProjectPlanType get(String id){
        return projectPlanTypeMapper.selectById(id);
    }

    public List<ProjectPlanType> all(){
        LambdaQueryWrapper<ProjectPlanType> queryWrapper = Wrappers.<ProjectPlanType>query().lambda();
        queryWrapper.eq(ProjectPlanType::getDelFlag,ProjectPlanType.DEL_FLAG_NORMAL).orderByAsc(ProjectPlanType::getSort);
        return projectPlanTypeMapper.selectList(queryWrapper);
    }


    public Page<ProjectPlanType> pageSearch(Page<ProjectPlanType> page, ProjectPlanType projectPlanType) {
        page.setCount(projectPlanTypeMapper.pageSearchCount(projectPlanType));
        page.setList(projectPlanTypeMapper.pageSearch(page,projectPlanType));
        return page;
    }

    public Page<ProjectPlanType> pageSearchOfchil(Page<ProjectPlanType> page, ProjectPlanType projectPlanType,String fatherId) {
        page.setCount(projectPlanTypeMapper.pageSearchCounts(projectPlanType, fatherId));
        page.setList(projectPlanTypeMapper.pageSearchOfchil(page,projectPlanType,fatherId));
        return page;
    }


    public List<ProjectPlanType> allListTree(){
        List<ProjectPlanType> typeList = Lists.newArrayList();
        LambdaQueryWrapper<ProjectPlanType> queryWrapper = Wrappers.<ProjectPlanType>query().lambda()
                .eq(ProjectPlanType::getDelFlag, 0).orderByAsc(ProjectPlanType::getSort);
        List<ProjectPlanType>  all= projectPlanTypeMapper.selectList(queryWrapper);

        Map<String, ProjectPlanType> map = new HashMap<>();
        for(ProjectPlanType menu:all){
            map.put(menu.getId(), menu);
        }
        for(ProjectPlanType type:all){
            if(type.getFatherId() == null){
                typeList.add(type);
            }else{
                ProjectPlanType parent = map.get(type.getFatherId());
                if(parent!=null){
                    parent.addChild(type);
                }
            }
        }
        return typeList;
    }

    /**
     * 新增
     */
    @Transactional
    public void create(String name, String officeId, String code, @RequestParam(value = "sort", defaultValue = "999") int sort, String remark) {
        name = StringUtils.trim(name);
        code = StringUtils.trim(code);

        if (StringUtils.isBlank(name)) {
            throw new ServiceException(PROJECT_PLAN_TYPE_NAME_REQUIRED);
        }
        if (StringUtils.isBlank(officeId)) {
            throw new ServiceException(PROJECT_PLAN_TYPE_OFFICEID_REQUIRED);
        }
        if (StringUtils.isBlank(code)) {
            throw new ServiceException(PROJECT_PLAN_TYPE_CODE_REQUIRED);
        }

        SysOffice office = officeMapper.selectById(officeId);
        if(office==null){
            throw new ServiceException(PROJECT_PLAN_TYPE_OFFICE_NOTEXIST);
        }

        ProjectPlanType projectPlanType = findByName(name);
        if(projectPlanType!=null){
            throw new ServiceException(PROJECT_PLAN_TYPE_NAME_EXIST);
        }

        // 创建
        projectPlanType = new ProjectPlanType();
        projectPlanType.setName(name);
        projectPlanType.setOfficeId(officeId);
        projectPlanType.setCode(code);
        projectPlanType.setSort(sort);
        projectPlanType.setRemark(remark);
        UserUtils.preAdd(projectPlanType);
        projectPlanTypeMapper.insert(projectPlanType);
    }

    /**
     * 新增子项目类
     */
    @Transactional
    public void createChild(String fatherId,String name, String officeId, String code, @RequestParam(value = "sort", defaultValue = "999") int sort, String remark) {
        name = StringUtils.trim(name);
        code = StringUtils.trim(code);

        if (StringUtils.isBlank(fatherId)) {
            throw new ServiceException(PROJECT_PLAN_TYPE_FATHER_REQUIRED);
        }

        if (StringUtils.isBlank(name)) {
            throw new ServiceException(PROJECT_PLAN_TYPE_NAME_REQUIRED);
        }
        if (StringUtils.isBlank(officeId)) {
            throw new ServiceException(PROJECT_PLAN_TYPE_OFFICEID_REQUIRED);
        }
        if (StringUtils.isBlank(code)) {
            throw new ServiceException(PROJECT_PLAN_TYPE_CODE_REQUIRED);
        }

        SysOffice office = officeMapper.selectById(officeId);
        if(office==null){
            throw new ServiceException(PROJECT_PLAN_TYPE_OFFICE_NOTEXIST);
        }

        ProjectPlanType projectPlanType = findByName(name);
        if(projectPlanType!=null){
            throw new ServiceException(PROJECT_PLAN_TYPE_NAME_EXIST);
        }

        // 创建
        projectPlanType = new ProjectPlanType();
        projectPlanType.setFatherId(fatherId);
        projectPlanType.setName(name);
        projectPlanType.setOfficeId(officeId);
        projectPlanType.setCode(code);
        projectPlanType.setSort(sort);
        projectPlanType.setRemark(remark);
        UserUtils.preAdd(projectPlanType);
        projectPlanTypeMapper.insert(projectPlanType);
    }

    /**
     * 编辑
     */
    @Transactional
    public void update(String id, String name, String officeId, String code, @RequestParam(value = "sort", defaultValue = "999") int sort,String remark) {
        name = StringUtils.trim(name);
        code = StringUtils.trim(code);
        // 数据验证
        ProjectPlanType projectPlanType = get(id);
        if (projectPlanType == null) {
            throw new ServiceException(PROJECT_PLAN_TYPE_NOTEXIST);
        }
        if (StringUtils.isBlank(name)) {
            throw new ServiceException(PROJECT_PLAN_TYPE_NAME_REQUIRED);
        }
        if (StringUtils.isBlank(officeId)) {
            throw new ServiceException(PROJECT_PLAN_TYPE_OFFICEID_REQUIRED);
        }
        if (StringUtils.isBlank(code)) {
            throw new ServiceException(PROJECT_PLAN_TYPE_CODE_REQUIRED);
        }

        SysOffice office = officeMapper.selectById(officeId);
        if(office==null){
            throw new ServiceException(PROJECT_PLAN_TYPE_OFFICE_NOTEXIST);
        }

        ProjectPlanType projectPlanType2 = findByName(name);
        if(projectPlanType2!=null&&!projectPlanType.getId().equals(projectPlanType2.getId())){
            throw new ServiceException(PROJECT_PLAN_TYPE_NAME_EXIST);
        }

        // 修改
        projectPlanType.setName(name);
        projectPlanType.setOfficeId(officeId);
        projectPlanType.setCode(code);
        projectPlanType.setSort(sort);
        projectPlanType.setRemark(remark);
        //存储
        UserUtils.preUpdate(projectPlanType);
        projectPlanTypeMapper.updateById(projectPlanType);
    }

    public ProjectPlanType findByName(String name){
        LambdaQueryWrapper<ProjectPlanType> queryWrapper = Wrappers.<ProjectPlanType>query().lambda()
                .eq(ProjectPlanType::getName, name)
                .eq(ProjectPlanType::getDelFlag,ProjectPlanType.DEL_FLAG_NORMAL);
        return projectPlanTypeMapper.selectOne(queryWrapper);
    }

    @Transactional
    public void delete(String id) {
        projectPlanTypeMapper.deleteById(id);
    }

 }
