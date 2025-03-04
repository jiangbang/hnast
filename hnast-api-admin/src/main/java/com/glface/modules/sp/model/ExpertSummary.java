package com.glface.modules.sp.model;

import lombok.Data;

/**
 * 统计分析
 */
@Data
public class ExpertSummary {
    private int expertNum;//申报专家数量
    private int validNum;	// 入库专家数量
    private int outNum;	// 出库专家数量
    private int rejectNum;	// 审核不通过数量
}
