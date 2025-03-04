package com.glface.modules.sp.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.glface.modules.sp.model.BaseCategory;
import com.glface.modules.sp.model.BaseProject;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Set;

/**
 * 制裁项目类别信息
 */
@Mapper
public interface BaseProjectMapper extends BaseMapper<BaseProject> {

    @Select("select name from sp_base_project where del_flag=0")
    Set<String> findProjectNamesByStatus();
}
