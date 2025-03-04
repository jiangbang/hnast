package com.glface.modules.model.json;

import com.glface.base.utils.StringUtils;
import com.glface.modules.model.ProjectContent;
import lombok.Data;

/**
 * 项目内容
 */
@Data
public class ProjectContentJson {
    private String basis;//立项依据
    private String content;//项目内容
    private String target;//项目目标
    private String conditions;//实施条件

    public static ProjectContentJson fromProjectContent(ProjectContent projectContent){
        ProjectContentJson contentJson = new ProjectContentJson();
        if(projectContent==null){
            return contentJson;
        }
        contentJson.setBasis(StringUtils.replaceHtmlBr(projectContent.getBasis()));
        contentJson.setContent(StringUtils.replaceHtmlBr(projectContent.getContent()));
        contentJson.setTarget(StringUtils.replaceHtmlBr(projectContent.getTarget()));
        contentJson.setConditions(StringUtils.replaceHtmlBr(projectContent.getConditions()));
        return contentJson;
    }

}
