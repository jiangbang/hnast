package com.glface.modules.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.glface.model.SysLog;
import com.glface.model.SysSms;
import org.apache.ibatis.annotations.Mapper;

/**
 * 短信日志
 */
@Mapper
public interface SmsMapper extends BaseMapper<SysSms> {
}
