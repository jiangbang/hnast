package com.glface.modules.sp.model.json;

import com.baomidou.mybatisplus.annotation.TableName;
import com.glface.base.bean.BaseEntity;
import com.glface.base.utils.StringUtils;
import com.glface.modules.sp.model.BaseEducation;
import com.glface.modules.sp.model.ExpertExt;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 专家内容表
 */
@Data
public class ExpertExtJson {
    private String expertId;//专家id
    private String work;//近五年主要专业工作经历
    private String achievement;//发明创造、科研成果、著作译著等成果
    private String partTime;//社会兼职情况
    private String former;//曾担任评审专家情况

    public static ExpertExtJson fromExpertExt(ExpertExt expertExt){
        ExpertExtJson json = new ExpertExtJson();
        if(expertExt==null){
            return json;
        }
        json.setWork(StringUtils.replaceWordNewLine(expertExt.getWork()));
        json.setExpertId(StringUtils.replaceWordNewLine(expertExt.getExpertId()));
        json.setFormer(StringUtils.replaceWordNewLine(expertExt.getFormer()));
        json.setAchievement(StringUtils.replaceWordNewLine(expertExt.getAchievement()));
        json.setPartTime(StringUtils.replaceWordNewLine(expertExt.getPartTime()));
        return json;
    }
}
