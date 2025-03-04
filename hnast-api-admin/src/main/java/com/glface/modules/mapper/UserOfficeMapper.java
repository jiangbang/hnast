package com.glface.modules.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.glface.model.SysOffice;
import com.glface.model.SysUserOffice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Set;

/**
 * @author maowei
 */
@Mapper
public interface UserOfficeMapper extends BaseMapper<SysUserOffice> {

    @Select("select * from  sys_user_office where user_id = #{userId} and del_flag=0")
    Set<SysUserOffice> findUserOfficesByUserId(String userId);

    @Select("SELECT * FROM sys_office o WHERE o.del_flag = 0 AND id IN ( SELECT office_id FROM sys_user_office uo WHERE uo.user_id = #{userId} AND uo.del_flag = 0 )")
    Set<SysOffice> selectOfficesByUserId(String userId);

    @Update("update sys_user_office set del_flag=1 where user_id = #{userId}")
    void delByUserId(String userId);

    @Update("update sys_user_office set del_flag=1 where office_id = #{officeId}")
    void delByOfficeId(String officeId);
}
