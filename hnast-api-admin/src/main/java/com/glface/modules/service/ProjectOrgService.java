package com.glface.modules.service;

import com.glface.base.bean.Page;
import com.glface.base.utils.StringUtils;
import com.glface.common.exeception.ServiceException;
import com.glface.modules.model.ProjectOrg;
import com.glface.modules.mapper.ProjectOrgMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Set;

import static com.glface.common.web.ApiCode.PROJECT_NOT_EXIST;
import static com.glface.common.web.ApiCode.PROJECT_ORG_NOT_EXIST;
import com.glface.modules.sys.utils.UserUtils;
@Slf4j
@Service
@Transactional(readOnly = true)
public class ProjectOrgService {
    @Resource
    private ProjectOrgMapper orgMapper;
    @Resource
    private ProjectService projectService;

    public ProjectOrg get(String id){
        return orgMapper.selectById(id);
    }


    public Page<ProjectOrg> pageSearch(Page<ProjectOrg> page, ProjectOrg projectOrg) {
        page.setCount(orgMapper.pageSearchCount(projectOrg));
        page.setList(orgMapper.pageSearch(page,projectOrg));
        return page;
    }

    public Set<ProjectOrg> all(){
        return orgMapper.findAll();
    }

    public int insertOrUpdate(ProjectOrg org){
        if(StringUtils.isBlank(org.getId())) {
            UserUtils.preAdd(org);
            return orgMapper.insert(org);
        } else {
            UserUtils.preUpdate(org);
            return orgMapper.updateById(org);
        }
    }


    @Transactional(readOnly = false)
    public void delete(String id) {
        orgMapper.deleteById(id);
    }

    public void update(String projectId, String chargeName, String orgName, String chargeMobile, String chargeEmail, String orgFax, String orgAddress, String orgPost) {
        if(StringUtils.isBlank(projectId)||projectService.get(projectId)==null){
            throw new ServiceException(PROJECT_NOT_EXIST);
        }

        ProjectOrg projectOrg = get(projectId);
        if (projectOrg == null) {
            throw new ServiceException(PROJECT_ORG_NOT_EXIST);
        }
        if (!StringUtils.isBlank(chargeName)) projectOrg.setChargeName(chargeName);
        if (!StringUtils.isBlank(orgName)) projectOrg.setChargeName(orgName);
        if (!StringUtils.isBlank(chargeMobile)) projectOrg.setChargeName(chargeMobile);
        if (!StringUtils.isBlank(chargeEmail)) projectOrg.setChargeName(chargeEmail);
        if (!StringUtils.isBlank(orgFax)) projectOrg.setChargeName(orgFax);
        if (!StringUtils.isBlank(orgAddress)) projectOrg.setChargeName(orgAddress);
        if (!StringUtils.isBlank(orgPost)) projectOrg.setChargeName(orgPost);
        orgMapper.updateById(projectOrg);
    }

    public String findOrgName(String id){
        return orgMapper.findOrgName(id);
    }
}
