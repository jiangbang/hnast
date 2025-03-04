package com.glface.modules.sp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.glface.base.bean.Page;
import com.glface.modules.sp.model.BasePositional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 职称信息
 */
@Mapper
public interface BasePositionalMapper extends BaseMapper<BasePositional> {

    /**
     * 分页查询
     */
    List<BasePositional> pageSearch(@Param("page") Page<BasePositional> page, @Param("bean") BasePositional bean);

    /**
     * 查询分页总数
     */
    int pageSearchCount(@Param("bean") BasePositional bean);

}
