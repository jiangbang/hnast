package com.glface.modules.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.glface.base.bean.Page;
import com.glface.modules.model.ProjectTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 项目模板信息
 */
@Mapper
public interface ProjectTemplateMapper extends BaseMapper<ProjectTemplate> {
    /**
     * 分页查询
     */
    List<ProjectTemplate> pageSearch(@Param("page") Page<ProjectTemplate> page, @Param("template") ProjectTemplate template);

    /**
     * 查询分页总数
     */
    int pageSearchCount(@Param("template") ProjectTemplate special);
}
