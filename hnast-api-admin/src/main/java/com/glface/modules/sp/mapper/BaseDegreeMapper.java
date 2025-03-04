package com.glface.modules.sp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.glface.base.bean.Page;
import com.glface.modules.sp.model.BaseDegree;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 学位信息
 */
@Mapper
public interface BaseDegreeMapper extends BaseMapper<BaseDegree> {

    /**
     * 分页查询
     */
    List<BaseDegree> pageSearch(@Param("page") Page<BaseDegree> page, @Param("bean") BaseDegree bean);

    /**
     * 查询分页总数
     */
    int pageSearchCount(@Param("bean") BaseDegree bean);

}
