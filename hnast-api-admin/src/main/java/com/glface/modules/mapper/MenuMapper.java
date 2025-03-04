package com.glface.modules.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.glface.model.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Set;


/**
 * @author maowei
 */
@Mapper
public interface MenuMapper extends BaseMapper<SysMenu> {
    /**
     * 查询给定menu id的最大code
     */
    @Select("select max(code) from  sys_menu where pid = #{id}")
    String findMaxCodeById(String id);

    /**
     * 删除指定code，并且删除所有子节点
     */
    @Update("update sys_menu set del_flag=1 where code like  #{code}%")
    void delAllByCode(String code);

    /**
     * 查找子菜单，不包含孙菜单
     */
    @Select("select * from  sys_menu where pid = #{menuId} and del_flag=0")
    Set<SysMenu> childrens(String menuId);
}
