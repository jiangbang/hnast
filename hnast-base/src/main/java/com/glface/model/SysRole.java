package com.glface.model;

import com.glface.base.bean.BaseEntity;
import com.glface.base.bean.DynamicBean;
import com.glface.base.utils.DateUtils;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 角色
 *
 * @author maowei
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SysRole extends BaseEntity {
    private String name; // 角色名称

    public DynamicBean convertToDynamicBean(){
        return new DynamicBean.Builder()
                .setPV("id", getId())
                .setPV("name", name)
                .setPV("createDate", DateUtils.formatDate(getCreateDate(),"yyyy-MM-dd HH:mm:ss"))
                .setPV("updateDate", DateUtils.formatDate(getUpdateDate(),"yyyy-MM-dd HH:mm:ss"))
                .build();
    }

}
