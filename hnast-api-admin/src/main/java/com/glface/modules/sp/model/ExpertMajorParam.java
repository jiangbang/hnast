package com.glface.modules.sp.model;

import lombok.Data;

/**
 * 评标专业
 */
@Data
public class ExpertMajorParam {
    private String id;
    private String expertId;//专家id
    private String baseMajorId;//专业id
    private String seniorFlag;//是否资深 1:是 0否
    private String fileId;//职业资格、技术职称.jpg
}
