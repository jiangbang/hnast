package com.glface.modules.sp.model;

import lombok.Data;

/**
 * 专家技术职称
 */
@Data
public class ExpertQualificationParam {
    private String id;
    private String expertId;//专家id
    private String title;//职业资格名称
    private String orgName;//注册单位
    private String number;//职业资格证书号
    private String start;//注册时间
    private String end;//过期时间
    private String fileId;//证书扫描件.jpg
}
