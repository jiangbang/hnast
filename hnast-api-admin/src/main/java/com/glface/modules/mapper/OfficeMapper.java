package com.glface.modules.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.glface.model.SysOffice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.io.Serializable;
import java.util.List;
import java.util.Set;


/**
 * @author maowei
 */
@Mapper
public interface OfficeMapper extends BaseMapper<SysOffice> {

    /**
     * 查询给定office id的最大code
     */
    @Select("select max(code) from  sys_office where pid = #{id}")
    String findMaxCodeById(String id);

    /**
     * 查询给定office code的最大code  code like '"+code+"__'"
     */
    @Select("select max(code) from  sys_office where code like #{code}___")
    String findMaxCodeByCode(String code);

    /**
     * 查找子菜单，不包含孙菜单
     */
    @Select("select * from  sys_office where pid = #{officeId} and del_flag=0")
    Set<SysOffice> childrens(String officeId);

    /**
     * 查找子节点 包括子子节点
     * @param code 菜单编码
     */
    @Select("select * from  sys_office where del_flag=0 and code like #{code}_%")
    List<SysOffice> findAllChildrenByCode(String code);

    /*
    * 根据项目id获取对应的name
    * */

    @Select("select o.name from pm_project p inner join sys_office o on p.qx_office_id  = o.id where p.qx_office_id = #{QxOfficeId} limit 1")
    String findOfficeName(String id);
}
