package com.glface.modules.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.glface.base.bean.Page;
import com.glface.model.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Date;
import java.util.List;
import java.util.Set;


/**
 * @author maowei
 */
@Mapper
public interface UserMapper extends BaseMapper<SysUser> {

    /**
     * 分页查询
     */
    List<SysUser> pageSearch(@Param("page") Page<SysUser> page, @Param("user")SysUser user);

    /**
     * 查询分页总数
     */
    int pageSearchCount(@Param("user")SysUser user);

    @Select("select * from  sys_user where mobile = #{mobile}")
    Set<SysUser> findByMobileIncludeDel(String mobile);

    @Update("UPDATE sys_user SET last_login_time = #{lastTime} , last_visit_time = #{visitTime} , check_account = #{checkAccount} WHERE account = #{account} AND del_flag = 0")
    void updataCheckAccount(@Param("lastTime") Date lastTime,@Param("visitTime") Date visitTime, @Param("checkAccount") Integer checkAccount, @Param("account") String account);
}
