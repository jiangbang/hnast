package com.glface.modules.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.glface.modules.model.ProjectFile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 项目文件
 */
@Mapper
public interface ProjectFileMapper extends BaseMapper<ProjectFile> {

    @Update("update pm_project_file set del_flag=1 where project_id = #{projectId} and type=#{type}")
    void deleteByProjectIdAndType(@Param("projectId")String projectId, @Param("type")String type);

    @Update("update pm_project_file set del_flag=1 where id=#{id} and project_id = #{projectId}")
    void deleteByIdAndProjectId(@Param("id")String id,@Param("projectId")String projectId);
}
