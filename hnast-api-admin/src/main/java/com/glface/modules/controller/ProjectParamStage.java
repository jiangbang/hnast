package com.glface.modules.controller;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 项目实施阶段明细参数
 */
@Data
@NoArgsConstructor
public class ProjectParamStage {
    private String id;
    private String name;
    private String startDate;
    private String endDate;
    private float money;
    private String remark;
}
