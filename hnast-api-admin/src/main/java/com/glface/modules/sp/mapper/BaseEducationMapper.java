package com.glface.modules.sp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.glface.base.bean.Page;
import com.glface.modules.sp.model.BaseEducation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 学历信息
 */
@Mapper
public interface BaseEducationMapper extends BaseMapper<BaseEducation> {

    /**
     * 分页查询
     */
    List<BaseEducation> pageSearch(@Param("page") Page<BaseEducation> page, @Param("bean") BaseEducation bean);

    /**
     * 查询分页总数
     */
    int pageSearchCount(@Param("bean") BaseEducation bean);

}
