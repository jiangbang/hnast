package com.glface.modules.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.glface.base.bean.Page;
import com.glface.modules.model.ProjectBatch;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Set;

/**
 * 项目类别
 */
@Mapper
public interface ProjectBatchMapper extends BaseMapper<ProjectBatch> {

    @Select("select * from  pm_project_batch")
    Set<ProjectBatch> findAll();

    /**
     * 分页查询
     */
    List<ProjectBatch> pageSearch(@Param("page") Page<ProjectBatch> page, @Param("batch")ProjectBatch batch);

    /**
     * 查询分页总数
     */
    int pageSearchCount(@Param("batch") ProjectBatch batch);

}
