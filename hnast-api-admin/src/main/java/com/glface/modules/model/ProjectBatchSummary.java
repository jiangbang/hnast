package com.glface.modules.model;

import lombok.*;

/**
 * 批次项目数据汇总
 */
@Data
public class ProjectBatchSummary{
    private String batchId;//批次id
    private int total;//申报项目总数
    private int waitFirst;	// 待初审项目总数
    private int recommend;	// 已推荐项目
    private int expert;//已评审项目
    private int materials;//实施项目(专家评审通过的项目)
}
