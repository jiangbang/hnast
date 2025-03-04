package com.glface.modules.sp.excelOrWord;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 专家信息
 */

@Data
public class Excel {
    private String id;

    @ExcelProperty(value = "申报时间", index = 0)
    private String applyDate;

    @ExcelProperty(value = "专家名称", index = 1)
    private String name;

    @ExcelProperty(value = "工作单位名称",index = 2)
    private String orgName;

    @ExcelProperty(value = "手机号", index = 3)
    private String mobile;

    @ExcelProperty(value = "状态", index = 4)
    private String status;

    @ExcelProperty(value = "星级", index = 5)
    private String star;

    @ExcelProperty(value = "专家类别", index = 6)
    private String category;

    private String categoryId;

    private String expertId;



}
