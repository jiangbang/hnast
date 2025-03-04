package com.glface.modules.sp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.glface.base.bean.Page;
import com.glface.modules.sp.model.BaseMajor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 专业信息
 */
@Mapper
public interface BaseMajorMapper extends BaseMapper<BaseMajor> {

    /**
     * 分页查询
     */
    List<BaseMajor> pageSearch(@Param("page") Page<BaseMajor> page, @Param("bean") BaseMajor bean);

    /**
     * 查询分页总数
     */
    int pageSearchCount(@Param("bean") BaseMajor bean);

    @Update("update sp_base_major set del_flag=1 where major_category_id = #{majorCategoryId}")
    void delByMajorCategoryId(String majorCategoryId);

}
