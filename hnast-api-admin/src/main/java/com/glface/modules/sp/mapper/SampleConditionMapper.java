package com.glface.modules.sp.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.glface.base.dto.SampleConditionDto;
import com.glface.modules.sp.model.ExpertCondition;
import com.glface.modules.sp.model.SampleCondition;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.security.core.parameters.P;

import java.util.List;
import java.util.Map;
import java.util.Set;

/*
 * 专家抽取
 * */
@Mapper
public interface SampleConditionMapper extends BaseMapper<SampleCondition> {

    /*
     * 查询抽取条件
     * */
    @Select("select s.hide_expert_name,s.hide_expert_project,s.id,s.base_category_id,s.name,s.num,s.star,s.hide_condition,s.hide_major_name, c.name from sp_base_category c inner join sp_sample_condition s on c.id = s.base_category_id where s.sample_id = #{sampleId} and s.name = #{name} and s.del_flag = 0 ")
    List<SampleCondition> findAll(@Param("sampleId") String sampleId, @Param("name") String name);


    /*
     * 根据类别id查询对应类别
     * */
    @Select("select s.name from sp_base_category s inner join sp_sample_condition c on s.id = c.base_category_id where c.base_category_id = #{base_category_id} limit 1")
    String findCategoryName(String id);

    /*
     * 根据行业类别id查询名称
     * */
    @Select("select name from sp_base_major_category where id = #{id}")
    String findMajorName(String id);

    /*
     * 查询所有专家名称
     * */
    @Select("select e.name from sp_expert e where del_flag = 0 ")
    Set<String> getExpertName();


    /*
     * 根据条件抽取专家
     * */
    List<SampleConditionDto> getExpertList(@Param("categoryId") String categoryId,
                                           @Param("expertId") List<String> expertId,
                                           @Param("majorId") String majorId,
                                           @Param("names") List<String> names,
                                           @Param("orgNames") List<String> orgNames,
                                           @Param("star") String star,
                                           @Param("num") Integer num,
                                           @Param("exProject") String exProject);

    /*
     * 每次抽取一个五星专家
     * */
    List<SampleConditionDto> getExpertOne(@Param("categoryId") String categoryId,
                                          @Param("expertId") List<String> expertId,
                                          @Param("majorId") String majorId,
                                          @Param("names") List<String> names,
                                          @Param("orgNames") List<String> orgNames,
                                          @Param("exProject") String exProject);

    /**
     * 查询专家id
     * */
    List<String> queryAllExpertId(String id);

    /*
     * 根据专指定家名称查询信息
     * */
    List<SampleConditionDto> findExpertOne(String name);


    /**
     * 根据name和expertNames批量更新is_item为0
     *
     * @return 返回更新的记录数
     */
    int updateExpertConditionIsItemByExpertNames(@Param("name") String name, @Param("expertNames") List<String> expertNames);


    /*
     * 根据name和expertNames将其他数据的is_item设置1
     * */
    int updateExpertConditionNotIsItemByExpertNames(@Param("name") String name, @Param("expertNames") List<String> expertNames);


    /*
     * 查询del_flag=1的专家抽取，来设置抽取轮次
     * */
    ExpertCondition selectExpertCondition(@Param("name") String name, @Param("sampleId") String sampleId, @Param("expertName") String expertName);
}
