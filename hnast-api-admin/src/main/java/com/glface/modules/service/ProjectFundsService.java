package com.glface.modules.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.glface.base.utils.StringUtils;
import com.glface.common.exeception.ServiceException;
import com.glface.modules.model.ProjectContent;
import com.glface.modules.model.ProjectFunds;
import com.glface.modules.mapper.ProjectFundsMapper;
import com.glface.modules.model.ProjectStage;
import com.glface.modules.utils.FloatUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static com.glface.common.web.ApiCode.*;
import com.glface.modules.sys.utils.UserUtils;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
public class ProjectFundsService {
    @Resource
    private ProjectFundsMapper fundsMapper;
    @Resource
    private ProjectService projectService;

    public ProjectFunds get(String id){
        return fundsMapper.selectById(id);
    }


    public List<ProjectFunds> findByProjectId(String projectId){
        LambdaQueryWrapper<ProjectFunds> queryWrapper = Wrappers.<ProjectFunds>query().lambda();
        queryWrapper.eq(ProjectFunds::getProjectId, projectId)
                .eq(ProjectFunds::getDelFlag,ProjectFunds.DEL_FLAG_NORMAL);
        return fundsMapper.selectList(queryWrapper);
    }

    public int insertOrUpdate(ProjectFunds funds){
        if(StringUtils.isBlank(funds.getId())) {
           UserUtils.preAdd(funds);
            return fundsMapper.insert(funds);
        } else {
            UserUtils.preUpdate(funds);
            return fundsMapper.updateById(funds);
        }
    }

    @Transactional(readOnly = false)
    public void delete(String id) {
        fundsMapper.deleteById(id);
    }

    public void update(String projectId, String name, float money, String remark){
        if(StringUtils.isBlank(projectId)||projectService.get(projectId)==null){
            throw new ServiceException(PROJECT_NOT_EXIST);
        }
        if (money == 0 || FloatUtils.decimalPlaces(money)>1) throw new ServiceException(PROJECT_FLOAT_REQUIRED);
        ProjectFunds funds = get(projectId);
        if (funds == null) throw new ServiceException(PROJECT_ORG_NOT_EXIST);

        if (!StringUtils.isBlank(name)) funds.setName(name);
        if (!StringUtils.isBlank(remark)) funds.setRemark(remark);

        fundsMapper.updateById(funds);
    }
}
