package com.glface.model;

import com.glface.base.bean.BaseEntity;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 字典表
 *
 * @author maowei
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SysDict extends BaseEntity {

    private String label;	// 标签名
    private String value;	// 数据值
    private String type;	// 类型
    private Integer sort;	// 排序 升序

}
