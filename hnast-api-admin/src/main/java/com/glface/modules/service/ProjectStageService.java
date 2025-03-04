package com.glface.modules.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.glface.base.utils.StringUtils;
import com.glface.common.exeception.ServiceException;
import com.glface.modules.model.Project;
import com.glface.modules.model.ProjectContent;
import com.glface.modules.model.ProjectStage;
import com.glface.modules.mapper.ProjectStageMapper;
import com.glface.modules.utils.FloatUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import static com.glface.common.web.ApiCode.*;
import com.glface.modules.sys.utils.UserUtils;

/**
 * 项目实施阶段
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class ProjectStageService {
    @Resource
    private ProjectStageMapper stageMapper;
    @Resource
    private ProjectService projectService;

    public ProjectStage get(String id){
        return stageMapper.selectById(id);
    }

    public int insertOrUpdate(ProjectStage stage){
        if(StringUtils.isBlank(stage.getId())) {
            UserUtils.preAdd(stage);
            return stageMapper.insert(stage);
        } else {
            UserUtils.preUpdate(stage);
            return stageMapper.updateById(stage);
        }
    }

    public List<ProjectStage> findByProjectId(String projectId){
        LambdaQueryWrapper<ProjectStage> queryWrapper = Wrappers.<ProjectStage>query().lambda();
        queryWrapper.eq(ProjectStage::getProjectId, projectId)
                .eq(ProjectStage::getDelFlag,ProjectStage.DEL_FLAG_NORMAL);
        return stageMapper.selectList(queryWrapper);
    }

    public int deleteByProjectId(String projectId){
        LambdaQueryWrapper<ProjectStage> queryWrapper = Wrappers.<ProjectStage>query().lambda();
        queryWrapper.eq(ProjectStage::getProjectId, projectId);
        return stageMapper.delete(queryWrapper);
    }

    @Transactional(readOnly = false)
    public void delete(String id) {
        stageMapper.deleteById(id);
    }

    public void update(String projectId, String name,String remark, String startDate, String endDate, float money) {
        if(StringUtils.isBlank(projectId)||projectService.get(projectId)==null){
            throw new ServiceException(PROJECT_NOT_EXIST);
        }
       // if (StringUtils.isBlank(name)) throw new ServiceException(PROJECT_STAGE_NAME_REQUIRED);
        //if (StringUtils.isBlank(remark)) throw new ServiceException(PROJECT_STAGE_REMARK_REQUIRED);
        if (money==0 || FloatUtils.decimalPlaces(money)>1) throw new ServiceException(PROJECT_FLOAT_REQUIRED);

        ProjectStage projectStage = get(projectId);
        if (projectStage == null) throw new ServiceException(PROJECT_STAGE_NOT_EXIST);

        if (!StringUtils.isBlank(name)) projectStage.setName(name);
        if (!StringUtils.isBlank(remark)) projectStage.setRemark(remark);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            projectStage.setStartDate(sdf.parse(startDate));
            projectStage.setEndDate(sdf.parse(endDate));
        } catch (ParseException e) {
            throw new ServiceException(DATE_FORMAT_ERROR);
        }
        projectStage.setMoney(money);
        stageMapper.updateById(projectStage);
    }
}
