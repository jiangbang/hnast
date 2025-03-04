package com.glface.modules.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.glface.base.bean.Page;
import com.glface.modules.model.ProjectBatch;
import com.glface.modules.model.ProjectCategory;
import com.glface.modules.model.ProjectCommonWord;
import com.glface.modules.model.ProjectTemplateCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

/**
 * 项目模板类别
 */
@Mapper
public interface ProjectTemplateCategoryMapper extends BaseMapper<ProjectTemplateCategory> {

    @Select("select * from  pm_project_template_category")
    Set<ProjectBatch> findAll();

    /**
     * 分页查询
     */
    List<ProjectTemplateCategory> pageSearch(@Param("page") Page<ProjectTemplateCategory> page, @Param("category") ProjectTemplateCategory category);

    /**
     * 查询分页总数
     */
    int pageSearchCount(@Param("category") ProjectTemplateCategory category);

}
