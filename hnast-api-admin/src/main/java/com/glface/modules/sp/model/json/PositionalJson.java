package com.glface.modules.sp.model.json;

import com.baomidou.mybatisplus.annotation.TableName;
import com.glface.base.bean.BaseEntity;
import com.glface.modules.sp.model.BaseParty;
import com.glface.modules.sp.model.BasePositional;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 职称信息
 */
@Data
public class PositionalJson {
    private String name;

    public static PositionalJson fromBasePositional(BasePositional basePositional){
        PositionalJson json = new PositionalJson();
        if(basePositional==null){
            return json;
        }
        json.setName(basePositional.getName());
        return json;
    }
}
