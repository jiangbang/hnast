package com.glface.modules.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.glface.base.bean.Page;
import com.glface.base.utils.StringUtils;
import com.glface.common.exeception.ServiceException;
import com.glface.model.SysDict;
import com.glface.modules.mapper.ProjectCategoryMapper;
import com.glface.modules.mapper.ProjectDeclareMapper;
import com.glface.modules.model.ProjectCategory;
import com.glface.modules.model.ProjectDeclare;
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
public class ProjectDeclareService {
    @Resource
    private ProjectDeclareMapper projectDeclareMapper;

    public ProjectDeclare get(String id){
        return projectDeclareMapper.selectById(id);
    }

    public List<ProjectDeclare> all(){
        LambdaQueryWrapper<ProjectDeclare> queryWrapper = Wrappers.<ProjectDeclare>query().lambda();
        queryWrapper.eq(ProjectDeclare::getDelFlag,ProjectCategory.DEL_FLAG_NORMAL).orderByAsc(ProjectDeclare::getCreateDate);
        return projectDeclareMapper.selectList(queryWrapper);
    }
    /**
     * 编辑
     */
    @Transactional
    public void update(String content, String remark) {
        LambdaQueryWrapper<ProjectDeclare> queryWrapper = Wrappers.<ProjectDeclare>query().lambda();
        ProjectDeclare declare = projectDeclareMapper.selectOne(queryWrapper);
        if (declare == null) {
            declare = new ProjectDeclare();
        }
        // 修改
        declare.setContent(content);
        declare.setRemark(remark);
        if(StringUtils.isBlank(declare.getId())) {
            UserUtils.preAdd(declare);
            projectDeclareMapper.insert(declare);
        } else {
            UserUtils.preUpdate(declare);
            projectDeclareMapper.updateById(declare);
        }
    }

    @Transactional
    public void delete(String id) {
        projectDeclareMapper.deleteById(id);
    }

 }
