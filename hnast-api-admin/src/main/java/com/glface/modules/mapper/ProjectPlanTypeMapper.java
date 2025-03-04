package com.glface.modules.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.glface.base.bean.Page;
import com.glface.modules.model.ProjectPlanType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 项目计划类型
 */
@Mapper
public interface ProjectPlanTypeMapper extends BaseMapper<ProjectPlanType> {


    /**
     * 分页查询
     */
    List<ProjectPlanType> pageSearch(@Param("page") Page<ProjectPlanType> page, @Param("plan") ProjectPlanType plan);


    List<ProjectPlanType> pageSearchOfchil(@Param("page") Page<ProjectPlanType> page, @Param("plan") ProjectPlanType plan, @Param("fatherId")String fatherId);

    /**
     * 查询分页总数
     */
    int pageSearchCount(@Param("plan") ProjectPlanType plan);

    int pageSearchCounts(@Param("plan") ProjectPlanType plan,@Param("fatherId")String fatherId);

}
