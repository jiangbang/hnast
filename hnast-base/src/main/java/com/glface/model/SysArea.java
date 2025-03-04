package com.glface.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.glface.base.bean.BaseEntity;
import com.glface.base.bean.DynamicBean;
import com.glface.base.utils.DateUtils;
import com.google.common.collect.Lists;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SysArea extends BaseEntity {
    private String pid;// 上级id
    private String name; // 部门名称
    private String code; // 部门编码0101 在部门上下级调整后 这个值会变
    private String type;//区域类型
    private Integer sort = 1000;// 默认1000  升序

    @TableField(exist = false)
    private List<SysArea> children = Lists.newArrayList();// 拥有子机构列表

    public void addChild(SysArea child){
        if(children==null){
            children = Lists.newArrayList();
        }
        children.add(child);
    }

    public DynamicBean convertToDynamicBean(){
        return new DynamicBean.Builder().setPV("id", getId())
                .setPV("pid", pid)
                .setPV("name", name)
                .setPV("type", type)
                .setPV("createDate", DateUtils.formatDate(getCreateDate(),"yyyy-MM-dd HH:mm:ss"))
                .setPV("updateDate", DateUtils.formatDate(getUpdateDate(),"yyyy-MM-dd HH:mm:ss"))
                .build();
    }

}
