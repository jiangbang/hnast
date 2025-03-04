package com.glface.modules.sp.model;
import com.baomidou.mybatisplus.annotation.TableName;
import com.glface.base.bean.BaseEntity;
import lombok.*;
import lombok.experimental.Accessors;
/**
 * 专家抽取类别数量设置
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@TableName(value = "sp_sample_category_num")
public class SampleCategoryNum extends BaseEntity {
		private String sampleId;
		private String baseCategoryId;
		private Integer num;    //抽取专家数
}