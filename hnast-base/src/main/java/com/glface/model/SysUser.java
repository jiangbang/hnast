package com.glface.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.glface.base.bean.BaseEntity;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * 系统用户
 *
 * @author maowei
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SysUser extends BaseEntity {
    private String account;// 帐号
    private String nickname;// 昵称
    private String mobile;// 帐号手机号码

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private String password;// 密码
    private String token;
    private Date lastLoginTime;// 最后登录时间
    private Date lastVisitTime;//最后访问时间
    private String lastLoginIp;// 最近一次登录ip
    private Integer loginCount;// 登录次数
    private Integer checkAccount;//登录失败次数
    private String email;
    private String isAdmin="0";// 1超级管理员 0不是超级管理员
    private String postName;
    private Integer sort;

    private String openid;
    private String unionid;
    private int sex;
    private String province;
    private String city;
    private String country;
    private String headimgurl;

    //归属部门 如果空则是普通用户
    private String officeId;


    /**
     * 是否可用 0:不可用  1可用
     */
    private Boolean enable;

    @TableField(exist = false)
    private Set<SysMenu> menus= new HashSet<>();

    /**
     * 部门code 作为查询条件使用
     */
    @TableField(exist = false)
    private String officeCode;

    public boolean isAdmin(){
        return "1".equals(this.isAdmin);
    }

}
