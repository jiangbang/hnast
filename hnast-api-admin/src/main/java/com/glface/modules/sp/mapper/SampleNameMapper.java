package com.glface.modules.sp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.glface.modules.sp.model.Sample;
import com.glface.modules.sp.model.SampleName;
import org.apache.ibatis.annotations.Mapper;


/*
* 组别名称
* */
@Mapper
public interface SampleNameMapper extends BaseMapper<SampleName> {
}
