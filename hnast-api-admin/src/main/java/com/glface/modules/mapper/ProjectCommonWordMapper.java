package com.glface.modules.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.glface.base.bean.Page;
import com.glface.modules.model.ProjectCommonWord;
import com.glface.modules.model.ProjectPlanType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 常用语
 */
@Mapper
public interface ProjectCommonWordMapper extends BaseMapper<ProjectCommonWord> {
    /**
     * 分页查询
     */
    List<ProjectCommonWord> pageSearch(@Param("page") Page<ProjectCommonWord> page, @Param("word") ProjectCommonWord word);

    /**
     * 查询分页总数
     */
    int pageSearchCount(@Param("word") ProjectCommonWord word);

}
