package com.glface.modules.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.glface.base.bean.BaseEntity;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 文件信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@TableName(value = "file_info")
public class FileInfo extends BaseEntity {
    /**
     * 绝对路径
     */
    private String absoluteAddress;

    /**
     * 文件名
     */
    private String name;

    /**
     * 后缀
     */
    private String suffix;

}
