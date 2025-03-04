package com.glface.modules.sp.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.glface.base.bean.BaseEntity;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 专家信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@TableName(value = "sp_expert")
public class Expert extends BaseEntity {
    private String name;//姓名
    private String sex;//性别
    private Date birthday;//'出生年月'
    private String identityCard;//身份证
    private String parties;//党派
    private String educationId;//学历
    private String degreeId;//学位
    private String studied;//所学专业
    private String specialty;//专业特长
    private String majorCategoryId;//现从事行业
    private String exProject;//执裁项目
    private String orgName;//工作单位
    private String job;//职务
    private String positionalId;//职称
    private String address;//联系地址
    private String post;//邮政编码
    private String mobile;//联系电话
    private String email;//电子邮箱
    private String wx;//微信号
    private String qq;//QQ号
    private String extId;//详细信息
    private String expertFileId;//附件
    private String pictureFileId;//照片
    private String status;//状态
    private String star;//专家星级
    private String entryMethod;//录入方式 user:用户提交  back:后台录入
    private Date applyDate;//申请时间
    private String reviewOpinion;//审核意见
}
