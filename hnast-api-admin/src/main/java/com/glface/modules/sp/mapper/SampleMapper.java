package com.glface.modules.sp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.glface.base.bean.Page;
import com.glface.modules.sp.model.Expert;
import com.glface.modules.sp.model.Sample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 专家抽取
 */
@Mapper
public interface SampleMapper extends BaseMapper<Sample> {

    /**
     * 分页查询
     */
    List<Sample> pageSearch(@Param("page") Page<Sample> page, @Param("bean") Sample bean);

    /**
     * 查询分页总数
     */
    int pageSearchCount(@Param("bean") Sample bean);

    /**
     * 查询组别名称
     * */
    @Select("select project_name from sp_sample where id = #{id}")
    String findProjectName(String id);

}
