package com.glface.modules.model.json;

import com.alibaba.fastjson.annotation.JSONField;
import com.glface.base.utils.DateUtils;
import com.glface.base.utils.StringUtils;
import com.glface.modules.model.ProjectStage;
import lombok.Data;

import java.util.*;

/**
 * 项目实施阶段信息
 */
@Data
public class ProjectStageJson {
    private String name;//阶段名称
    @JSONField(format="yyyy-MM-dd")
    private Date startDate;//开始时间
    @JSONField(format="yyyy-MM-dd")
    private Date endDate;//结束时间
    private float money;//经费预算（万元）
    private String remarks;

    public static ProjectStageJson fromProjectStage(ProjectStage projectStage){
        if(projectStage==null){
            return null;
        }
        ProjectStageJson stageJson = new ProjectStageJson();
        stageJson.setName(projectStage.getName());
        stageJson.setStartDate(projectStage.getStartDate());
        stageJson.setEndDate(projectStage.getEndDate());
        stageJson.setMoney(projectStage.getMoney());
        stageJson.setRemarks(StringUtils.replaceHtmlBr(projectStage.getRemark()));
        return stageJson;
    }

    public static List<ProjectStageJson> fromProjectStageList(List<ProjectStage> projectStageList){
        List<ProjectStageJson> list = new ArrayList<>();
        if(projectStageList==null){
            return list;
        }
        Collections.sort(projectStageList, new Comparator<ProjectStage>() {
            @Override
            public int compare(ProjectStage o1, ProjectStage o2) {
                if(o1.getSort()>o2.getSort()){
                    return 1;
                }else if(o1.getSort()<o2.getSort()){
                    return -1;
                }
                return  0;
            }
        });
        for(ProjectStage stage:projectStageList){
            ProjectStageJson stageJson = fromProjectStage(stage);
            if(stageJson!=null){
                list.add(stageJson);
            }
        }
        return list;
    }

}
