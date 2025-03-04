package com.glface.modules.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.glface.base.bean.Page;
import com.glface.modules.model.ProjectOrg;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

/**
 * 申报单位
 */
@Mapper
public interface ProjectOrgMapper extends BaseMapper<ProjectOrg> {

    @Select("select * from  pm_project_organization where del_flag='0'")
    Set<ProjectOrg> findAll();

    /*
    * 根据项目id查询申报单位
    * */
    @Select("select org_name from pm_project p INNER JOIN pm_project_organization a ON p.org_id  = a.id where p.org_id = #{org_id} limit 1")
    String findOrgName(String id);

    /**
     * 分页查询
     */
    List<ProjectOrg> pageSearch(@Param("page") Page<ProjectOrg> page, @Param("org")ProjectOrg org);

    /**
     * 查询分页总数
     */
    int pageSearchCount(@Param("org") ProjectOrg org);
}
