package com.glface.modules.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.glface.base.bean.BaseEntity;
import com.glface.base.bean.DynamicBean;
import com.glface.base.utils.DateUtils;
import com.glface.model.SysArea;
import com.google.common.collect.Lists;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 项目计划
 *
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@TableName(value = "pm_project_plan_type")
public class ProjectPlanType extends BaseEntity {
    private String fatherId;
    private String name;
    private String officeId;
    private String code;
    private int sort;

    @TableField(exist = false)
    private List<ProjectPlanType> children = Lists.newArrayList();// 拥有子机构列表

    public void addChild(ProjectPlanType child){
        if(children==null){
            children = Lists.newArrayList();
        }
        children.add(child);
    }

    public DynamicBean convertToDynamicBean(){
        return new DynamicBean.Builder().setPV("id", getId())
                .setPV("fatherId", fatherId)
                .setPV("name", name)
                .setPV("officeId", officeId)
                .setPV("code", code)
                .setPV("createDate", DateUtils.formatDate(getCreateDate(),"yyyy-MM-dd HH:mm:ss"))
                .setPV("updateDate", DateUtils.formatDate(getUpdateDate(),"yyyy-MM-dd HH:mm:ss"))
                .build();
    }

}
