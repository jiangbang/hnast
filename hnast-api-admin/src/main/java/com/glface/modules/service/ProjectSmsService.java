package com.glface.modules.service;


import com.alibaba.fastjson.JSONObject;
import com.glface.base.utils.StringUtils;
import com.glface.common.utils.NewSmsUtil;
import com.glface.model.SysSms;
import com.glface.model.SysUser;
import com.glface.modules.mapper.SmsMapper;
import com.glface.modules.model.Project;
import com.glface.modules.model.ProjectOrg;
import com.glface.modules.sys.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 发送短信
 */
@Slf4j
@Service
public class ProjectSmsService {
    private final static int SMS_TYPE_SYSTEM = 0;//系统消息

    @Resource
    private SmsMapper smsMapper;
    @Resource
    private UserService userService;
    @Resource
    private ProjectProcessService projectProcessService;
    @Resource
    private ProjectOrgService projectOrgService;
    @Resource
    private ProjectService projectService;
    @Resource
    private OfficeService officeService;

    /**
     * 您提交的xxx项目已申报成功，管理员正在审核中，请您耐心等待。
     * Name:项目名称
     */
    public void submitSuccess(Project project) {
        String templateId = "50cf862e5d9340dfb3eab48bc938b2b1";
        String name = project.getName();
        String templateParas = "您提交的[" + name + "]项目已申报成功，管理员正在审核中，请您耐心等待。【长沙市科技活动中心】";
        sendSmsToProjectCreator(project, templateId, templateParas);
    }

    /**
     * 您提交的xxx项目，已通过初审环节，详细信息请登录长沙科协项目管理系统
     * Name:项目名称
     */
    public void firstReviewSuccess(Project project) {
        String templateId = "1a6f4b353228460ab9a3056917c0709f";
        String name = project.getName();
        // String content = "短信模板"+templateId+"。"+orgName+"提交的项目，已通过初审环节。";
        String templateParas = "您提交的[" + name + "]项目，已通过初审环节，详细信息请登录长沙科协项目管理系统【长沙市科技活动中心】";
        sendSmsToProjectCreator(project, templateId, templateParas);
    }

    /**
     * 您提交的xxx项目，未通过初审环节，请及时登录长沙科协项目管理系统查看未通过原因
     * Name:项目名称
     */
    public void firstReviewReject(Project project) {
        String templateId = "e44d7fddb06b44a68db361bebef98518";
        String name = project.getName();
        String templateParas = "您提交的[" + name + "]项目，未通过初审环节，请及时登录长沙科协项目管理系统查看未通过原因【长沙市科技活动中心】";
        sendSmsToProjectCreator(project, templateId, templateParas);
    }

    /**
     * 您提交的xxx项目，已通过推荐环节，详细信息请登录长沙科协项目管理系统。
     * Name:项目名称
     */
    public void recommendAgree(Project project) {
        String templateId = "6d62a9efed174e1eb9c57db2a3fd0777";
        //获取申报单位
        String orgId = projectService.findOrgId(project.getId());
        String orgName = projectOrgService.findOrgName(orgId);
        // String content = "短信模板"+templateId+"。"+orgName+"提交的项目，已通过推荐环节。";
        String templateParas = "您提交的[" + orgName + "]项目，已通过推荐环节，详细信息请登录长沙科协项目管理系统。【长沙市科技活动中心】";
        sendSmsToProjectCreator(project, templateId, templateParas);
    }

    /**
     * 您提交的xxx项目，未通过推荐环节，请及时登录长沙科协项目管理系统查看未通过原因。
     * Name:项目名称
     */
    public void recommendReject(Project project) {
        String templateId = "b115e89403284196a46496920a02d1dc";
        //获取申报单位
        String orgId = projectService.findOrgId(project.getId());
        String orgName = projectOrgService.findOrgName(orgId);
        // String content = "短信模板"+templateId+"。"+orgName+"提交的项目，未通过推荐环节。";
        String templateParas = "您提交的[" + orgName + "]项目，未通过推荐环节，请及时登录长沙科协项目管理系统查看未通过原因。【长沙市科技活动中心】";
        sendSmsToProjectCreator(project, templateId, templateParas);
    }

    /**
     * 您提交的xxx项目，已通过评审环节，详细信息请登录长沙科协项目管理系统。
     * Name:项目名称
     */
    public void expertAgree(Project project) {
        String templateId = "0bd8e3c1287242c7bf88a70993172cc9";
        //获取申报单位
        String orgId = projectService.findOrgId(project.getId());
        String orgName = projectOrgService.findOrgName(orgId);
        // String content = "短信模板"+templateId+"。"+orgName+"提交的项目，已通过评审环节。";
        String templateParas = "您提交的[" + orgName + "]项目，已通过评审环节，详细信息请登录长沙科协项目管理系统。【长沙市科技活动中心】";
        sendSmsToProjectCreator(project, templateId, templateParas);
        //给项目负责人发送短信
        sendSmsToProjectCharge(project, templateId, templateParas);
    }

    /**
     * 您提交的xxx项目，未通过评审环节，请及时登录长沙科协项目管理系统查看未通过原因。
     * Name:项目名称
     */
    public void expertReject(Project project) {
        String templateId = "fe12d8745213491a8d22e91b5a9ee06d";
        //获取申报单位
        String orgId = projectService.findOrgId(project.getId());
        String orgName = projectOrgService.findOrgName(orgId);
        // String content = "短信模板"+templateId+"。"+orgName+"提交的项目，未通过评审环节。";
        String templateParas = "您提交的[" + orgName + "]项目，未通过评审环节，请及时登录长沙科协项目管理系统查看未通过原因。【长沙市科技活动中心】";
        sendSmsToProjectCreator(project, templateId, templateParas);
    }


    /*
     * 项目管理员发送信息
     * */


    /**
     * 地区+单位名称有等待初审的项目，请您登录长沙科协项目管理系统查看相关信息。
     * 如果是B类项目，会向区县审批人员发送短信，如果没有区县审批人员则向归口部门管理员发送短信
     *
     * @param onlyGkbm 是否只发送给归口部门
     */
    public void firstReview(Project project, boolean onlyGkbm) {
        try {
            String templateId = "a5b1246a307e408c83cb84709b79678b";
            //获取申报单位
            String orgId = projectService.findOrgId(project.getId());
            String orgName = projectOrgService.findOrgName(orgId);
            //获取申报地区
            String QxOfficeId = projectService.findQxOfficeId(project.getId());
            String QxOfficeName = officeService.findOfficeName(QxOfficeId);
            // String content = "短信模板" + templateId + "。"+orgName+"有待初审项目等待审核中。";
            String templateParas = "";
            if (QxOfficeName == null) {
                templateParas = "[" + "市本级科协" + " , " + orgName + "]有等待初审的项目，请您登录长沙科协项目管理系统查看相关信息。【长沙市科技活动中心】";
            } else {
                templateParas = "[" + QxOfficeName + " , " + orgName + "]有等待初审的项目，请您登录长沙科协项目管理系统查看相关信息。【长沙市科技活动中心】";
            }
            List<SysUser> districtUsers = new ArrayList<>();//区县管理员
            if (!onlyGkbm) {
                districtUsers = projectProcessService.findDistrictManagerUsers(project);
            }
            List<SysUser> gkbmUsers = new ArrayList<>();
            if (districtUsers == null || districtUsers.size() == 0) {
                gkbmUsers = projectProcessService.findGkbmManagerUsers(project);//归口部门管理员
            }
            Map<String, SysUser> userMap = new HashMap<>();
            for (SysUser user : districtUsers) {
                userMap.put(user.getId(), user);
            }
            for (SysUser user : gkbmUsers) {
                userMap.put(user.getId(), user);
            }
            //发送消息
            for (String key : userMap.keySet()) {
                SysUser user = userMap.get(key);
                sendSms(user.getId(), user.getMobile(), templateId, templateParas);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 地区+单位名称有待推荐的项目，请您登录长沙科协项目管理系统查看相关信息。
     */
    public void recommentReview(Project project) {
        try {
            //String templateId = "6ed97af40aa44fe99e0cc27110419562";
            String templateId = "2424fb52b86f4f49b0dec6a6b33c3973";
            //获取申报单位
            String orgId = projectService.findOrgId(project.getId());
            String orgName = projectOrgService.findOrgName(orgId);
            //获取申报地区
            String QxOfficeId = projectService.findQxOfficeId(project.getId());
            String QxOfficeName = officeService.findOfficeName(QxOfficeId);
            // String content = "短信模板" + templateId + "。"+orgName+"有待推荐项目等待审核中。";
            String templateParas = "";
            if (QxOfficeName == null) {
                templateParas = "[" + "市本级科协" + " , " + orgName + "]有待推荐的项目，请您登录长沙科协项目管理系统查看相关信息。【长沙市科技活动中心】";
            } else {
                templateParas = "[" + QxOfficeName + " , " + orgName + "]有待推荐的项目，请您登录长沙科协项目管理系统查看相关信息。【长沙市科技活动中心】";
            }
            List<SysUser> users = projectProcessService.findRecommentManagers(project);
            //发送消息
            for (SysUser user : users) {
                sendSms(user.getId(), user.getMobile(), templateId, templateParas);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 地区+单位名称有待评审的项目，请您登录长沙科协项目管理系统查看相关信息。
     */
    public void expertReview(Project project) {
        try {
            //String templateId = "a8579544d78c4a02a5f0f041226f6a7f";
            String templateId = "54b32deff4f54555ae9a2e4d81fa75cc";
            //获取申报单位
            String orgId = projectService.findOrgId(project.getId());
            String orgName = projectOrgService.findOrgName(orgId);
            //获取申报地区
            String QxOfficeId = projectService.findQxOfficeId(project.getId());
            String QxOfficeName = officeService.findOfficeName(QxOfficeId);
            // String content = "短信模板" + templateId + "。"+orgName+"有待评审项目等待审核中。";
            String templateParas = "";
            if (QxOfficeName == null) {
                templateParas = "[" + "市本级科协" + " , " + orgName + "]有待评审的项目，请您登录长沙科协项目管理系统查看相关信息。【长沙市科技活动中心】";
            } else {
                templateParas = "[" + QxOfficeName + " , " + orgName + "]有待评审的项目，请您登录长沙科协项目管理系统查看相关信息。【长沙市科技活动中心】";
            }
            List<SysUser> users = projectProcessService.findExpertManagers(project);
            //发送消息
            for (SysUser user : users) {
                sendSms(user.getId(), user.getMobile(), templateId, templateParas);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 地区+单位名称有待实施的项目，请您登录长沙科协项目管理系统查看相关信息。
     * 如果是B类项目，会向区县审批人员发送短信，如果没有区县审批人员则向归口部门管理员发送短信
     *
     * @param onlyGkbm 是否只发送给归口部门
     */
    public void materialsReview(Project project, boolean onlyGkbm) {
//        try {
//            String templateId = "b3e5e9d5567a420dba1c9f9aaf21aae8";
//            String content = "短信模板" + templateId + "。您有待实施项目等待审核中。";
//            List<SysUser> users = projectProcessService.findMaterialsManagers(project);
//            //发送消息
//            for (SysUser user :users) {
//                sendSms(user.getId(), user.getMobile(), templateId, content);
//            }
//        }catch (Exception e){
//            log.error(e.getMessage(),e);
//        }

        try {
            //String templateId = "b3e5e9d5567a420dba1c9f9aaf21aae8";
            String templateId = "3616d1d41dcc4bebbb86b533b1dcd019";
            //获取申报单位
            String orgId = projectService.findOrgId(project.getId());
            String orgName = projectOrgService.findOrgName(orgId);
            //获取申报地区
            String QxOfficeId = projectService.findQxOfficeId(project.getId());
            String QxOfficeName = officeService.findOfficeName(QxOfficeId);
            // String content = "短信模板" + templateId + "。"+orgName+"有待实施项目等待审核中。";
            String templateParas = "";
            if (QxOfficeName == null) {
                templateParas = "[" + "市本级科协" + " , " + orgName + "]有待实施的项目，请您登录长沙科协项目管理系统查看相关信息。【长沙市科技活动中心】";
            } else {
                templateParas = "[" + QxOfficeName + " , " + orgName + "]有待实施的项目，请您登录长沙科协项目管理系统查看相关信息。【长沙市科技活动中心】";
            }
            List<SysUser> districtUsers = new ArrayList<>();//区县管理员
            if (!onlyGkbm) {
                districtUsers = projectProcessService.findDistrictManagerUsers(project);
            }
            List<SysUser> gkbmUsers = new ArrayList<>();
            if (districtUsers == null || districtUsers.size() == 0) {
                gkbmUsers = projectProcessService.findGkbmManagerUsers(project);//归口部门管理员
            }
            Map<String, SysUser> userMap = new HashMap<>();
            for (SysUser user : districtUsers) {
                userMap.put(user.getId(), user);
            }
            for (SysUser user : gkbmUsers) {
                userMap.put(user.getId(), user);
            }
            //发送消息
            for (String key : userMap.keySet()) {
                SysUser user = userMap.get(key);
                sendSms(user.getId(), user.getMobile(), templateId, templateParas);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


    /**
     * 往项目创建人发送短信
     *
     * @param project
     * @param templateId
     * @param templateParas
     */
    @Transactional
    public void sendSmsToProjectCreator(Project project, String templateId, String templateParas) {
        try {
            String toUserId = project.getCreateBy();
            String toMobile = "";
            if (StringUtils.isNotBlank(toUserId)) {
                SysUser user = userService.get(toUserId);
                if (user != null) {
                    toMobile = user.getMobile();
                }
            }
            if (StringUtils.isBlank(toMobile)) {
                log.error("找不到项目创建人的手机号，无法发送短信.项目id=" + project.getId());
                return;
            }
            sendSms(toUserId, toMobile, templateId, templateParas);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 往项目负责人发送短信
     *
     * @param project
     * @param templateId
     * @param templateParas
     */
    @Transactional
    public void sendSmsToProjectCharge(Project project, String templateId, String templateParas) {
        try {
            //查询申报单位信息
            String orgId = project.getOrgId();
            String chargeMobile = "";
            if (StringUtils.isNotBlank(orgId)) {
                ProjectOrg projectOrg = projectOrgService.get(orgId);
                chargeMobile = projectOrg.getChargeMobile();
            }
            if (StringUtils.isBlank(chargeMobile)) {
                log.error("找不到项目负责人的手机号，无法发送短信.项目id=" + project.getId());
                return;
            }
            String toUserId = project.getCreateBy();
            sendSms(toUserId, chargeMobile, templateId, templateParas);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void sendSms(String toUserId, String toMobile, String templateId, String templateParas) {
        try {
            SysSms sysSms = new SysSms();
            sysSms.setType(SMS_TYPE_SYSTEM);
            sysSms.setUserId(toUserId);
            sysSms.setPhone(toMobile);
            sysSms.setContent(templateParas);
            try {
                JSONObject sendResult = NewSmsUtil.send(toMobile, templateParas);
                sysSms.setReturnCode(sendResult.getString("code"));
                sysSms.setReturnDescription(sendResult.getString("description"));
                sysSms.setReturnResult(sendResult.getString("result"));
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
                sysSms.setReturnResult(ex.getMessage());
            }
            UserUtils.preAdd(sysSms);
            smsMapper.insert(sysSms);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
