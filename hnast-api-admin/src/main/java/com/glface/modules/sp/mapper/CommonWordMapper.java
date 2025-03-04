package com.glface.modules.sp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.glface.base.bean.Page;
import com.glface.modules.model.ProjectCommonWord;
import com.glface.modules.sp.model.CommonWord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 常用语
 */
@Mapper
public interface CommonWordMapper extends BaseMapper<CommonWord> {
    /**
     * 分页查询
     */
    List<CommonWord> pageSearch(@Param("page") Page<CommonWord> page, @Param("word") CommonWord word);

    /**
     * 查询分页总数
     */
    int pageSearchCount(@Param("word") CommonWord word);

}
