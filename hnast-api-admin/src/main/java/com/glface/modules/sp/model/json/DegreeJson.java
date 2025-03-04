package com.glface.modules.sp.model.json;

import com.baomidou.mybatisplus.annotation.TableName;
import com.glface.base.bean.BaseEntity;
import com.glface.modules.model.ProjectContent;
import com.glface.modules.model.json.ProjectContentJson;
import com.glface.modules.sp.model.BaseDegree;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 学位信息
 */
@Data
public class DegreeJson {
    private String name;

    public static DegreeJson fromBaseDegree(BaseDegree degree){
        DegreeJson json = new DegreeJson();
        if(degree==null){
            return json;
        }
        json.setName(degree.getName());
        return json;
    }
}
