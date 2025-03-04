package com.glface.modules.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.glface.model.SysMenu;
import com.glface.model.SysRoleMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Set;

/**
 * Mapper
 *
 * @author maowei
 */
@Mapper
public interface RoleMenuMapper extends BaseMapper<SysRoleMenu> {
    @Select("SELECT * FROM sys_menu m WHERE m.del_flag = 0 AND id IN ( SELECT menu_id FROM sys_role_menu rm WHERE rm.role_id = #{roleId} AND rm.del_flag = 0 )")
    Set<SysMenu> findMenusByRoleId(String roleId);

    @Select("SELECT * FROM sys_role_menu WHERE role_id = #{roleId} AND del_flag = 0 ")
    Set<SysRoleMenu> findByRoleId(String roleId);

    @Update("update sys_role_menu set del_flag=1 where menu_id = #{menuId}")
    void delByMenuId(String menuId);

    @Update("update sys_role_menu set del_flag=1 where role_id = #{roleId}")
    void delByRoleId(String roleId);
}
