package com.glface.modules.sp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.glface.base.bean.Page;
import com.glface.modules.sp.model.BaseTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 专家库模板表
 */
@Mapper
public interface BaseTemplateMapper extends BaseMapper<BaseTemplate> {

    /**
     * 分页查询
     */
    List<BaseTemplate> pageSearch(@Param("page") Page<BaseTemplate> page, @Param("bean") BaseTemplate bean);

    /**
     * 查询分页总数
     */
    int pageSearchCount(@Param("bean") BaseTemplate bean);

}
