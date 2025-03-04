package com.glface.modules.sp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.glface.base.bean.Page;
import com.glface.modules.sp.model.BaseCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 专家库类别信息
 */
@Mapper
public interface BaseCategoryMapper extends BaseMapper<BaseCategory> {

    /**
     * 分页查询
     */
    List<BaseCategory> pageSearch(@Param("page") Page<BaseCategory> page, @Param("bean") BaseCategory category);

    /**
     * 查询分页总数
     */
    int pageSearchCount(@Param("bean") BaseCategory category);

}
