package com.glface.modules.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.glface.model.SysUserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Set;

/**
 * @author maowei
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<SysUserRole> {
    @Select("select * from  sys_user_role where user_id = #{userId} and del_flag=0")
    Set<SysUserRole> findUserRolesByUserId(String userId);

    @Select("select * from  sys_user_role where role_id = #{userId} and del_flag=0")
    Set<SysUserRole> findUserRolesByRoleId(String roleId);

    @Update("update sys_user_role set del_flag=1 where user_id = #{userId}")
    void delByUserId(String userId);

    @Update("update sys_user_role set del_flag=1 where role_id = #{roleId}")
    void delByRoleId(String roleId);
}
