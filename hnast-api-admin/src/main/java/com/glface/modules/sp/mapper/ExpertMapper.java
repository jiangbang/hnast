package com.glface.modules.sp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.glface.modules.sp.model.Expert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Set;


@Mapper
public interface ExpertMapper extends BaseMapper<Expert> {
    @Select("select id from  sp_expert where del_flag='1'")
    Set<String> findDels();

    @Select("select e.* from  sp_expert e,sp_expert_category c where e.id = c.expert_id and c.category_id = #{categoryId} and e.status= #{status} and e.del_flag=0 and c.del_flag=0")
    Set<Expert> findByCategoryId(@Param("categoryId")String categoryId, @Param("status")String status);

    @Select("select e.org_name from  sp_expert e where e.status= #{status} and e.del_flag=0")
    Set<String> findOrgNamesByStatus( @Param("status")String status);
}
