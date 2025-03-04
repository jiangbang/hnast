package com.glface.modules.sp.model;

import lombok.Data;

import java.util.Map;
import java.util.Set;

/**
 * 项目统计
 */
@Data
public class SampleSummary {
    private Map<String, Set<Sample>> categorySampleMap;//项目分类统计
}
