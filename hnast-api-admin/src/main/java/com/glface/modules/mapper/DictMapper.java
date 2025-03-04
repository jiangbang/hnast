package com.glface.modules.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.glface.model.SysDict;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Set;

/**
 * @author maowei
 */
@Mapper
public interface DictMapper extends BaseMapper<SysDict> {

    @Select("select type from  sys_dict where del_flag=0")
    Set<String> allTypes();
}
