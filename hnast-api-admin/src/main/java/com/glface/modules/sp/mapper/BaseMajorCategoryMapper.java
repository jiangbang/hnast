package com.glface.modules.sp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.glface.base.bean.Page;
import com.glface.modules.sp.model.BaseMajorCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 行业(专业分类)
 */
@Mapper
public interface BaseMajorCategoryMapper extends BaseMapper<BaseMajorCategory> {

    /**
     * 分页查询
     */
    List<BaseMajorCategory> pageSearch(@Param("page") Page<BaseMajorCategory> page, @Param("bean") BaseMajorCategory bean);

    /**
     * 查询分页总数
     */
    int pageSearchCount(@Param("bean") BaseMajorCategory bean);

}
