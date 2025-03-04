package com.glface.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.glface.base.bean.BaseEntity;
import com.google.common.collect.Lists;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 菜单
 *
 * @author maowei
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SysMenu extends BaseEntity {
    private String pid;// 上级id
    private String name; // 部门名称
    private String code; // 部门编码0101 在部门上下级调整后 这个值会变
    private Integer sort = 999;// 默认999
    private String icon;
    private Integer isShow = 1;//0 不显示   1显示
    private String permission;//权限标识

    @TableField(exist = false)
    private List<SysMenu> children = Lists.newArrayList();// 拥有子机构列表

    public void addChild(SysMenu child){
        if(children==null){
            children = Lists.newArrayList();
        }
        children.add(child);
    }

}
