package com.glface.modules.controller;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 经费预算明细参数
 */
@Data
@NoArgsConstructor
public class ProjectParamFund {
    private String id;
    private String name;
    private float money;
    private String remark;
}
