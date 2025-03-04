package com.glface.modules.model.json;

import com.glface.modules.model.ProjectOrg;
import lombok.Data;

/**
 * 申报单位信息
 */
@Data
public class ProjectOrgJson {
    private String orgName;//单位名称
    private String chargeName;//项目/课题负责人
    private String chargeTitle;//项目/课题负责人职称
    private String orgPhone;//单位联系电话
    private String chargeMobile;//项目/课题负责人手机号码
    private String chargeEmail;//项目/课题负责人邮箱
    private String orgFax;//单位传真
    private String orgAddress;//单位地址
    private String orgPost;//单位邮政编码

    public static ProjectOrgJson fromProjectOrg(ProjectOrg projectOrg){
        ProjectOrgJson orgJson = new ProjectOrgJson();
        if(projectOrg==null){
            return orgJson;
        }
        orgJson.setOrgName(projectOrg.getOrgName());
        orgJson.setChargeName(projectOrg.getChargeName());
        orgJson.setChargeTitle(projectOrg.getChargeTitle());
        orgJson.setOrgPhone(projectOrg.getOrgPhone());
        orgJson.setChargeMobile(projectOrg.getChargeMobile());
        orgJson.setChargeEmail(projectOrg.getChargeEmail());
        orgJson.setOrgFax(projectOrg.getOrgFax());
        orgJson.setOrgAddress(projectOrg.getOrgAddress());
        orgJson.setOrgPost(projectOrg.getOrgPost());
        return orgJson;
    }
}
