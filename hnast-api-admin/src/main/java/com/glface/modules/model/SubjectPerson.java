package com.glface.modules.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.glface.base.bean.BaseEntity;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 项目申报人信息
 *
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@TableName(value = "pm_subject_person")
public class SubjectPerson extends BaseEntity {
    private String projectId;
    private String type;
    private String name;
    private String sex;
    private Date birth;
    private String post;
    private String major;
    private String professional;
    private String unit;
    private String task;
}
