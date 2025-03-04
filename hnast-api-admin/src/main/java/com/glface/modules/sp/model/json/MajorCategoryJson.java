package com.glface.modules.sp.model.json;

import com.baomidou.mybatisplus.annotation.TableName;
import com.glface.base.bean.BaseEntity;
import com.glface.modules.sp.model.BaseMajorCategory;
import com.glface.modules.sp.model.ExpertExt;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 行业(专业分类)
 */
@Data
public class MajorCategoryJson {
    private String letter;//门类
    private String name;

    public static MajorCategoryJson fromBaseMajorCategory(BaseMajorCategory baseMajorCategory){
        MajorCategoryJson json = new MajorCategoryJson();
        if(baseMajorCategory==null){
            return json;
        }
        json.setName(baseMajorCategory.getName());
        json.setLetter(baseMajorCategory.getLetter());
        return json;
    }
}
