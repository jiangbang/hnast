package com.glface.modules.sp.model.json;

import com.glface.modules.sp.model.ExpertCategory;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ExpertJson {
    private String name;//姓名
    private String sex;//性别
    private Date birthday;//'出生年月'
    private String identityCard;//身份证
    private PartyJson party;//党派
    private EducationJson education;//学历
    private DegreeJson degree;//学位
    private String studied;//所学专业
    private String specialty;//专业特长
    private MajorCategoryJson majorCategory;//现从事行业
    private String orgName;//工作单位
    private String job;//职务
    private PositionalJson positional;//职称
    private String address;//联系地址
    private String post;//邮政编码
    private String mobile;//联系电话
    private String email;//电子邮箱
    private String wx;//微信号
    private String qq;//QQ号
    private ExpertExtJson expertExt;//详细信息
    private String pictureFileId;//照片
    private String status;//状态
    private String star;//专家星级
    private Date applyDate;//申请时间

    private List<ExpertCategory> categories;//类别
}
