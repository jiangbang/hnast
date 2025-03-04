package com.glface.modules.sp.model;

import com.glface.base.bean.BaseEntity;
import lombok.*;

/**
 * 专家回避单位
 */
@Data
public class ExpertAvoidOrgParam {
    private String id;
    private String orgName;//单位名称
    private String orgCode;//统一社会信用代码
    private String remark;//回避说明
}
