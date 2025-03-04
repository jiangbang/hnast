package com.glface.modules.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.glface.base.bean.Page;
import com.glface.modules.model.ProjectSpecial;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 项目专项信息
 */
@Mapper
public interface ProjectSpecialMapper extends BaseMapper<ProjectSpecial> {

    /**
     * 分页查询
     */
    List<ProjectSpecial> pageSearch(@Param("page") Page<ProjectSpecial> page, @Param("special") ProjectSpecial special);

    /**
     * 查询分页总数
     */
    int pageSearchCount(@Param("special") ProjectSpecial special);

}
