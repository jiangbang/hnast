package com.glface.modules.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.glface.model.SysArea;
import com.glface.model.SysOffice;
import com.glface.model.SysUserOffice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Set;

/**
 * @author maowei
 */
@Mapper
public interface AreaMapper extends BaseMapper<SysArea> {

    /**
     * 查询给定area id的最大code
     */
    @Select("select max(code) from  sys_area where pid = #{id}")
    String findMaxCodeById(String id);

    /**
     * 查询给定area code的最大code  code like '"+code+"__'"
     */
    @Select("select max(code) from  sys_area where code like #{code}___")
    String findMaxCodeByCode(String code);

    /**
     * 查找子菜单，不包含孙菜单
     */
    @Select("select * from  sys_area where pid = #{officeId} and del_flag=0")
    Set<SysArea> childrens(String officeId);

    /**
     * 查找子节点 包括子子节点
     * @param code 菜单编码
     */
    @Select("select * from  sys_area where del_flag=0 and code like #{code}_%")
    List<SysArea> findAllChildrenByCode(String code);
}
