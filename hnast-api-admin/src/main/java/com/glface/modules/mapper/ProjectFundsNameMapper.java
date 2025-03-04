package com.glface.modules.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.glface.modules.model.ProjectFundsName;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Set;

/*
* 项目支出内容
* */
@Mapper
public interface ProjectFundsNameMapper extends BaseMapper<ProjectFundsName> {

    // 查询所有的支出内容名称
    @Select(" select name from pm_funds_name where del_flag = 0 ")
    Set<String> findAllFundsName();
}
