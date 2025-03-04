package com.glface.modules.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.glface.base.bean.Page;
import com.glface.modules.model.Project;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

/**
 * 项目类别
 */
@Mapper
public interface ProjectMapper extends BaseMapper<Project> {

    @Select("select * from  pm_project where del_flag='0'")
    Set<Project> findAll();

    @Select("select id from  pm_project where del_flag='1'")
    Set<String> findDels();

    List<Project> searchAll(@Param("project") Project project);

    /**
     * 分页查询
     */
    List<Project> pageSearch(@Param("page") Page<Project> page, @Param("user")Project user);

    /**
     * 查询分页总数
     */
    int pageSearchCount(@Param("user")Project user);

    @Select("select max(code) from  pm_project where code like #{code}")
    String findMaxCodeByCode(String code);

    @Select("select * from  pm_project where code = #{code}")
    Set<Project> findByCodeIncludeDel(String code);

    /*
    * 根据id查询orgId
    * */

    @Select("select org_id from pm_project where id = #{id} limit 1")
    String findOrgId(String id);

    /*
    * 根据id查新qx_office_id
    * */
    @Select("select qx_office_id from pm_project where id = #{id} limit 1")
    String findQxOfficeId(String id);

}
