package com.glface.modules.model.json;

import com.glface.modules.model.ProjectPlanType;
import lombok.Data;

/**
 * 项目类型 1.科技与社会发展项目
 */
@Data
public class ProjectPlanTypeJson {
    private String id;
    private String name;
    private String officeId;
    private String code;
    private int sort;

    public static ProjectPlanTypeJson fromProjectPlanType(ProjectPlanType projectPlanTypet){
        ProjectPlanTypeJson planTypeJson = new ProjectPlanTypeJson();
        if(projectPlanTypet==null){
            return planTypeJson;
        }
        planTypeJson.setId(projectPlanTypet.getId());
        planTypeJson.setName(projectPlanTypet.getName());
        planTypeJson.setOfficeId(projectPlanTypet.getOfficeId());
        planTypeJson.setCode(projectPlanTypet.getCode());
        planTypeJson.setSort(projectPlanTypet.getSort());
        return planTypeJson;
    }
}
