package com.glface.modules.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.glface.base.bean.Page;
import com.glface.modules.model.ProjectCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 项目类型
 */
@Mapper
public interface ProjectCategoryMapper extends BaseMapper<ProjectCategory> {

    /**
     * 分页查询
     */
    List<ProjectCategory> pageSearch(@Param("page") Page<ProjectCategory> page, @Param("category") ProjectCategory category);

    /**
     * 查询分页总数
     */
    int pageSearchCount(@Param("category") ProjectCategory category);

}
