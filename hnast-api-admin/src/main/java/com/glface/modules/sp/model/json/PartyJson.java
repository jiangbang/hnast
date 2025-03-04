package com.glface.modules.sp.model.json;

import com.baomidou.mybatisplus.annotation.TableName;
import com.glface.base.bean.BaseEntity;
import com.glface.modules.sp.model.BaseParty;
import com.glface.modules.sp.model.ExpertExt;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 党派
 */
@Data
public class PartyJson {
    private String name;    //名称
    public static PartyJson fromBaseParty(BaseParty baseParty){
        PartyJson json = new PartyJson();
        if(baseParty==null){
            return json;
        }
        json.setName(baseParty.getName());
        return json;
    }
}
