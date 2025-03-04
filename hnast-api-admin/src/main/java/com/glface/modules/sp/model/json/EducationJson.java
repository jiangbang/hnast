package com.glface.modules.sp.model.json;

import com.baomidou.mybatisplus.annotation.TableName;
import com.glface.base.bean.BaseEntity;
import com.glface.modules.sp.model.BaseDegree;
import com.glface.modules.sp.model.BaseEducation;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 学历信息
 */
@Data
public class EducationJson {
    private String name;
    public static EducationJson fromBaseEducation(BaseEducation education){
        EducationJson json = new EducationJson();
        if(education==null){
            return json;
        }
        json.setName(education.getName());
        return json;
    }
}
