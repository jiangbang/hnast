package com.glface.modules.sp.model;

import lombok.Data;

/**
 * 专家技术职称
 */
@Data
public class ExpertTechnicalTitleParam {
    private String id;
    private String expertId;//专家id
    private String title;//技术职称
    private String start;//通过时间
    private String end;//截止时间
    private String fileId;//证书扫描件.jpg
}
