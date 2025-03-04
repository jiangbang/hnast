package com.glface.base.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.glface.constant.Common;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 实体类基础属性
 *
 * @author maowei
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键 不含中划线的UUID生成
     * groups是验证分组，只有更新的时候验证
     */
    @TableId(type = IdType.ASSIGN_UUID)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String id;

    /**
     * 创建者
     */
    private String createBy;

    /**
     * 更新者
     */
    private String updateBy;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(pattern = Common.DATE_FORMAT, timezone = Common.TIMEZONE)
    private Date createDate;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonFormat(pattern = Common.DATE_FORMAT, timezone = Common.TIMEZONE)
    private Date updateDate;

    /**
     * 逻辑删除标记（0：正常；1：删除；）
     * json序列化时不序列化该字段
     */
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private Integer delFlag=DEL_FLAG_NORMAL;

    private String remark;

    public static final int DEL_FLAG_NORMAL = 0;
    public static final int DEL_FLAG_DELETE = 1;

    public boolean isDeleted(){
        return DEL_FLAG_NORMAL!=this.delFlag;
    }
}
