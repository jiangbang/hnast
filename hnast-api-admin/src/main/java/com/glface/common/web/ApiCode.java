package com.glface.common.web;

public enum ApiCode {
	//String password,String newPassword,String verifyNewPassword
	SUCCESS("0001", "成功"),
	UNKNOWN_ERROR("1000", "系统繁忙，请稍后再试...."),
	UNCAUGHT_EXCEPTION("1002","未捕获异常...."),

	ACCOUNT_NOT_LOGIN("401","请先登录！"),
	ACCOUNT_LOGIN_ACCOUNT_REQUIRED("0101","账号不能为空！"),
	ACCOUNT_LOGIN_ACCOUNT_FORMAT("","账号由字母开头包含数字字母下划线3到16位!"),
    ACCOUNT_LOGIN_PASSWORD_REQUIRED("","密码不能为空！"),
    ACCOUNT_LOGIN_FAILED("","用户名或密码错误，登录失败！"),
    ACCOUNT_LOGIN_CHECK_ACCOUNT("","用户名或密码错误次数过多请五分钟之后在登录!"),
    ACCOUNT_EDIT_PASSWORD("","当前密码为弱口令，请修改密码！"),
    ACCOUNT_EDIT_PASSWORD_REQUIRED("","请输入原密码！"),
    ACCOUNT_EDIT_NEWPASSWORD_REQUIRED("","请输入新密码！"),
    ACCOUNT_EDIT_VERIFYNEWPASSWORD_REQUIRED("","请确认密码！"),
    ACCOUNT_EDIT_PASSWORD_LENGTH("","密码过长！"),
    ACCOUNT_EDIT_PASSWORD_FORMAT("","密码格式不正确！"),
    ACCOUNT_EDIT_PASSWORD_VERIFY("","原密码错误!"),
    ACCOUNT_EDIT_PASSWORDS_VERIFY("","两次密码输入不相同!"),
	ACCOUNT_REG_SUCCESS("","注册成功！"),
	ACCOUNT_CHANGE_PASSWORD_SUCCESS("","修改密码成功！"),
	ACCOUNT_USER_NOTEXIST("", "用户不存在"),
	ACCOUNT_USER_MOBILE_REQUIRED("","手机号不能为空!"),
	ACCOUNT_USER_MOBILE_FORMAT("","手机号格式错误!"),
	ACCOUNT_USER_PASSWORD_REQUIRED("","用户密码不能为空！"),
	ACCOUNT_USER_PASSWORD_FORMAT("","密码由字母数字组成!"),
	ACCOUNT_USER_ACCOUNT_EXIST("","用户账号已经存在!"),
	ACCOUNT_USER_EMAIL_FORMAT("","请输入正确的邮箱!"),
	ACCOUNT_USER_MOBILE_ONLY("","手机号已存在!"),
	ACCOUNT_USER_RESET_PASSWORD_SUCCESS("","重置密码成功!"),
	ACCOUNT_USER_RESET_PASSWORD_FAIL("","重置密码失败!"),

	ACCOUNT_WX_USER_INFOR_FAIL("","获取微信用户信息失败!"),
	ACCOUNT_WX_USER_BINDED("","该微信已绑定其他用户!"),

	IMAGE_CODE_ERROR("","验证码错误！"),
	IMAGE_CODE_EMPTY("","验证码不能为空！"),
	IMAGE_CODE_TIME_OUT("","验证码超时！"),


	PERMISSION_USER_ACCOUNT_EXIST("0201","用户账号已经存在，请更改!"),
	PERMISSION_USER_ADD_FAILED("","新增用户失败!"),
	PERMISSION_MENU_ID_REQUIRED("","菜单ID不能为空！"),
	PERMISSION_MENU_NAME_REQUIRED("","菜单名称不能为空！"),
	PERMISSION_MENU_CHILDREN_EXIST("","请先删除子菜单！"),
	PERMISSION_MENU_CODE_REQUIRED("","菜单权限编码不能为空！"),
	PERMISSION_USER_NICKNAME_REQUIRED("","用户名称不能为空!"),
	PERMISSION_USER_NICKNAME_LENGTH("","长度为 20 字符以内!"),
	PERMISSION_USER_EMAIL_FORMAT("","请输入正确的邮箱!"),
	PERMISSION_USER_MOBILE_REQUIRED("","手机号不能为空!"),
	PERMISSION_USER_MOBILE_FORMAT("","手机号格式错误!"),
	PERMISSION_OFFICE_CHILDREN_EXIST("","请先删除下级部门！"),
	PERMISSION_OFFICE_NAME_REQUIRED("","部门名称不能为空!"),
	PERMISSION_OFFICE_AREAID_REQUIRED("","归属区域不能为空!"),
	PERMISSION_OFFICE_NOT_EXIST("","部门不存在！"),
	PERMISSION_OFFICE_ID_REQUIRED("","部门ID不能为空！"),
	PERMISSION_USER_ACCOUNT_REQUIRED("","用户帐号不能为空！"),
	PERMISSION_USER_MOBILE_ONLY("","手机号已存在!"),
	PERMISSION_USER_PASSWORD_REQUIRED("","用户密码不能为空！"),
	PERMISSION_USER_PASSWORD_FORMAT("","密码由字母数字组成!"),
	PERMISSION_ROLE_NAME_REQUIRED("","角色名称不能为空！"),
	PERMISSION_USER_NOTEXIST("", "用户不存在"),
	PERMISSION_ROLE_NOTEXIST("","角色不存在！"),

	PERMISSION_DICT_LABEL_VERIFY("0249","标签必填且在100字符以内"),
	PERMISSION_DICT_VALUE_VERIFY("0250","键值	必填且在100字符以内"),
	PERMISSION_DICT_TYPE_VERIFY("0251","类型必填且在100字符以内"),
	PERMISSION_DICT_EXIST("","已存在！"),
	PERMISSION_DICT_NOTEXIST("","字典不存在！"),

	INFO_CONFIG_REMARK_VERIFY("0406","描述长度在255字符以内"),

	PERMISSION_AREA_CHILDREN_EXIST("","请先删除下级区域！"),
	PERMISSION_AREA_NAME_REQUIRED("","区域名称不能为空!"),
	PERMISSION_AREA_TYPE_REQUIRED("","区域类型不能为空!"),
	PERMISSION_AREA_NOT_EXIST("","区域不存在！"),
	PERMISSION_AREA_ID_REQUIRED("","区域ID不能为空！"),

	PROJECT_NAME_REQUIRED("","项目名称不能为空!"),
	PROJECT_ORG_NAME_REQUIRED("","单位名称不能为空!"),
	PROJECT_ORG_CHARGENAME_REQUIRED("","项目负责人不能为空!"),
	PROJECT_ORG_CHARGETITLE_REQUIRED("","职称/职务不能为空!"),
	PROJECT_ORG_CHARGEMOBILE_REQUIRED("","手机号不能为空!"),
	PROJECT_ORG_CHARGEEMAIL_REQUIRED("","邮箱不能为空!"),
	PROJECT_ORG_ORGPHONE_REQUIRED("","单位电话不能为空!"),
	PROJECT_ORG_ORGADRESS_REQUIRED("","单位地址不能为空!"),
	PROJECT_ORG_ORGPOST_REQUIRED("","邮政编码不能为空!"),
	AREA_REQUIRED("","区域不能为空!"),
	PROJECT_CATEGORY_REQUIRED("","项目类别不能为空!"),
	PROJECT_BATCH_REQUIRED("","项目批次不能为空!"),
	PROJECT_PLAN_TYPE_REQUIRED("","项目计划类型不能为空!"),
	PROJECT_PLAN_TYPE_FIRST("","计划类型不能选择一级分类，请选择右侧二级分类!"),
	PROJECT_NOT_EXIST("","项目不存在!"),
	PROJECT_CREATE_FAILED_EXIST("","创建项目失败，已存在暂存项目！"),
	PROJECT_STARTTIME_ERROR("","项目开始时间格式错误!"),
	PROJECT_ENDTIME_ERROR("","项目结束时间格式错误!"),
	PROJECT_STAGE_STARTTIME_ERROR("","实施开始时间格式错误!"),
	PROJECT_STAGE_ENDTIME_ERROR("","实施结束时间格式错误!"),
	PROJECT_STARTTIME_REQUIRED("","项目开始时间不能为空!"),
	PROJECT_ENDTIME_REQUIRED("","项目结束时间不能为空!"),
	PROJECT_BANK_REQUIRED("","开户银行不能为空!"),
	PROJECT_CARDNO_REQUIRED("","银行卡号不能为空!"),
	PROJECT_ACCOUNTS_REQUIRED("","开户名不能为空!"),
	PROJECT_NOT_ALLOW_CACHE("","当前状态不允许暂存操作!"),
	PROJECT_TYPE_REQUIRED("","附件类型不能为空!"),
	PROJECT_NOT_ALLOW_SUBMIT("","当前状态不允许提交!"),
	PROJECT_NOT_ALLOW_SUBMIT_OTHER("","请提交自己的项目!"),
	PROJECT_BATCH_NOT_START("", "本次申报未开始!"),
	PROJECT_BATCH_END("", "本次申报时间已截止，请您下一批次再来申报。"),

	PROJECT_ORG_NOT_EXIST("","项目单位不存在!"),

	PROJECT_STAGE_NOT_EXIST("","项目实施不存在!"),
	PROJECT_STAGE_NAME_REQUIRED("","项目实施阶段名称不能为空!"),
	PROJECT_STAGE_REMARK_REQUIRED("","项目实施内容描述不能为空!"),
	PROJECT_FLOAT_REQUIRED("","金额必须是大于0的1位小数!"),

	DATE_FORMAT_ERROR("","日期格式转换错误!"),

	PROJECT_BATCH_YEAR_REQUIRED("","批次年份不能为空！"),
	PROJECT_BATCH_NUMBER_REQUIRED("","批次编号不能为空！"),
	PROJECT_BATCH_STARTTIME_REQUIRED("","开始时间不能为空！"),
	PROJECT_BATCH_ENTTIME_REQUIRED("","结束时间不能为空！"),
	PROJECT_BATCH_EXIST("","已存在！"),
	PROJECT_BATCH_NOTEXIST("", "批次信息不存在"),
	PROJECT_BATCH_YEAR_NUMBER_ONLY("","存在相同的批次年份和批次号!"),
	PROJECT_BATCH_STARTTIME_ERROR("","开始时间必须早于结束时间！"),

	PROJECT_CATEGORY_NAME_REQUIRED("","类型名称不能为空！"),
	PROJECT_CATEGORY_AMOUNTMAX_ERROR("","项目金额上限必须大于0！"),
	PROJECT_CATEGORY_AMOUNTMAX_SMALL("","项目金额上限必须大于项目金额下限！"),
	PROJECT_CATEGORY_NAME_EXIST("","类型名称已存在！"),
	PROJECT_CATEGORY_NOTEXIST("", "项目类型不存在"),

	PROJECT_PLAN_TYPE_FATHER_REQUIRED("","父级计划类型不能为空！"),
	PROJECT_PLAN_TYPE_NAME_REQUIRED("","计划类型名称不能为空！"),
	PROJECT_PLAN_TYPE_OFFICEID_REQUIRED("","归口部门不能为空！"),
	PROJECT_PLAN_TYPE_CODE_REQUIRED("","计划类型编码不能为空！"),
	PROJECT_PLAN_TYPE_OFFICE_NOTEXIST("","归口部门不存在！"),
	PROJECT_PLAN_TYPE_NAME_EXIST("","计划类型名称已存在！"),
	PROJECT_PLAN_TYPE_NOTEXIST("", "计划类型不存在"),

	PROJECT_SPECIAL_NAME_REQUIRED("","专项名称不能为空！"),
	PROJECT_SPECIAL_NAME_EXIST("","专项名称已存在！"),
	PROJECT_SPECIAL_NOTEXIST("", "项目专项不存在"),

	PROJECT_TEMPLATE_NAME_REQUIRED("","模板名称不能为空!"),
	PROJECT_TEMPLATE_FILEID_REQUIRED("","模板文件不能为空!"),
	PROJECT_TEMPLATE_ID_REQUIRED("","模板不能为空！"),
	PROJECT_TEMPLATE_NOT_EXIST("","模板不存在！"),
	PROJECT_TEMPLATE_CATE_NOT_EXIST("","资料分类不能为空！"),

	PREVIEW_SUFFIX_NOT_SUPPORT("","不支持此格式！"),
	PREVIEW_FILE_NOT_EXIST("","文件不存在！"),
	PREVIEW_FILE_LENGTH_OUT("","文件过大无法预览！"),
	PREVIEW_FILE_TRANSFORM_ERROR("","文件预览转换异常！"),

	PREVIEW_FILE_ERROR("","文件预览失败！"),

	FILE_NOT_EXIST("","文件不存在！"),

	PROJECT_PROCESS_INVALID_STATUS("","无效状态！"),
	PROJECT_PROCESS_NO_AUTHORITY("","没有审批权限！"),
	PROJECT_PROCESS_NOT_FIRST_AGREE("","请先完成初审！"),
	PROJECT_PROCESS_SUBMIT_NOT_ALLOW("","当前状态不允许提交！"),
	PROJECT_PROCESS_NOT_RECOMMEND_AGREE("","请先完成推荐审核！"),
	PROJECT_PROCESS_NOT_REVIEW_AGREE("","请先完成评审！"),
	PROJECT_PROCESS_NOT_PASS_REVIEW("","项目未通过评审！"),
	PROJECT_PROCESS_NOT_MATERIALS_AGREE("","需要通过实施材料审核！"),

	PROJECT_COMMON_WORD_WORD_REQUIRED("","常用语不能为空！"),
	PROJECT_COMMON_WORD_WORD_EXIST("","常用语已存在！"),
	PROJECT_COMMON_WORD_NOTEXIST("", "常用语不存在"),
	CODE_SEND_FAILED("","发送验证码失败！"),
	CODE_ERROR("","验证码错误！"),
	CODE_TIME_OUT_ERROR("","验证超时，请重新发送验证码！"),

	PROJECT_FTL_NOTEXIST("","项目模板文件不存在!"),
	PROJECT_ZIP_NOT_EXIST("","归档文件不存在!"),


	SP_CATEGORY_NAME_REQUIRED("","类别名称不能为空！"),
	SP_CATEGORY_OFFICEID_REQUIRED("","归口部门不能为空！"),
	SP_CATEGORY_OFFICE_NOTEXIST("","归口部门不存在！"),
	SP_CATEGORY_NAME_EXIST("","类别名称已存在！"),
	SP_CATEGORY_NOTEXIST("", "类别不存在"),

	SP_DEGREE_NAME_REQUIRED("","名称不能为空！"),
	SP_DEGREE_NAME_EXIST("","学位名称已存在！"),
	SP_DEGREE_NOTEXIST("", "学位不存在"),

	SP_EDUCATION_NAME_REQUIRED("","名称不能为空！"),
	SP_EDUCATION_NAME_EXIST("","学历名称已存在！"),
	SP_EDUCATION_NOTEXIST("", "学历不存在"),

	SP_MAJOR_NAME_REQUIRED("","名称不能为空！"),
	SP_MAJOR_NAME_EXIST("","名称已存在！"),
	SP_MAJOR_NOTEXIST("", "专业不存在"),
	SP_MAJOR_CATEGORY_REQUIRED("","行业不能为空！"),

	SP_MAJOR_CATEGORY_NAME_REQUIRED("","名称不能为空！"),
	SP_MAJOR_CATEGORY_NAME_EXIST("","名称已存在！"),
	SP_MAJOR_CATEGORY_NOTEXIST("", "行业不存在"),

	SP_POSITIONAL_NAME_REQUIRED("","名称不能为空！"),
	SP_POSITIONAL_NAME_EXIST("","名称已存在！"),
	SP_POSITIONAL_NOTEXIST("", "职称不存在"),

	SP_TEMPLATE_NAME_REQUIRED("","名称不能为空！"),
	SP_TEMPLATE_NAME_EXIST("","名称已存在！"),
	SP_TEMPLATE_NOTEXIST("", "模板不存在"),
	SP_TEMPLATE_FILEID_REQUIRED("","模板文件不能为空!"),

	SP_EXPERT_MOBILE_REQUIRED("","联系电话不能为空！"),
	SP_EXPERT_NAME_REQUIRED("","姓名不能为空！"),
	SP_EXPERT_ORG_NAME_REQUIRED("","工作单位不能为空！"),
	SP_EXPERT_CATEGORY_REQUIRED("","专家类别不能为空！"),
	SP_EXPERT_SAVE_NOT_ALLOW("","当前状态不允许保存！"),
	SP_EXPERT_MOBILE_EXIST("","联系电话已存在！"),
	SP_EXPERT_IDENTITY_CARD_EXIST("","身份证已存在！"),
	SP_EXPERT_NOT_EXIST("", "专家不存在"),
	SP_EXPERT_BIRTHDAY_ERROR("","出生日期格式错误!"),
	SP_EXPERT_BASE_MAJOR_REQUIRED("","评标专业不能为空！"),
	SP_EXPERT_MAJOR_FILE_ID_REQUIRED("","评标专业附件不能为空！"),
	SP_EXPERT_TITLE_REQUIRED("","技术职称名称不能为空！"),
	SP_EXPERT_TITLE_FILE_ID_REQUIRED("","附件不能为空！"),
	SP_EXPERT_TITLE_START_REQUIRED("","通过时间不能为空！"),
	SP_EXPERT_TITLE_END_REQUIRED("","截止时间不能为空！"),
	SP_EXPERT_TITLE_START_ERROR("","通过时间格式错误!"),
	SP_EXPERT_TITLE_END_ERROR("","截止时间格式错误!"),

	SP_EXPERT_QUALIFICATION_REQUIRED("","职业资格名称不能为空！"),
	SP_EXPERT_QUALIFICATION_START_REQUIRED("","注册时间不能为空！"),
	SP_EXPERT_QUALIFICATION_END_REQUIRED("","过期时间不能为空！"),
	SP_EXPERT_QUALIFICATION_START_ERROR("","注册时间格式错误!"),
	SP_EXPERT_QUALIFICATION_END_ERROR("","过期时间格式错误!"),

	SP_EXPERT_FILE_REQUIRED("","专家附件不能为空！"),
	SP_EXPERT_FILE_IDENTITY_REQUIRED("","身份证不能为空！"),
	SP_EXPERT_FILE_ORG_REQUIRED("","所在单位意见不能为空！"),
	SP_EXPERT_FILE_SECRECY_REQUIRED("","保密协议不能为空！"),
	SP_EXPERT_FILE_REVIEW_REQUIRED("","评审纪律协议不能为空！"),

	SP_EXPERT_SUBMIT_NOT_ALLOW("","当前状态不允许提交！"),
	SP_EXPERT_INVALID_STATUS("","无效状态！"),
	SP_EXPERT_PROCESS_NO_AUTHORITY("","没有审批权限！"),

	SP_SAMPLE_NAME_REQUIRED("","名称不能为空！"),
	SP_SAMPLE_CATEGORY_REQUIRED("","类别不能为空！"),
	SP_SAMPLE_DATE_REQUIRED("","评审时间不能为空！"),
	SP_SAMPLE_NOTEXIST("", "不存在"),
	SP_SAMPLE_NUMBER_ERROR("", "计划抽取人数数字错误"),
	SP_SAMPLE_CATEGORY_NUMBER_REQUIRED("", "专业要求设置不能为空"),
	SP_SAMPLE_NUMBER_FINISHED("", "计划抽取人数已全部抽取"),
	SP_SAMPLE_AVOID_TYPE_REQUIRED("","分类不能为空！"),
	SP_SAMPLE_AVOID_ORG_NAME_REQUIRED("","工作单位名称不能为空！"),
	SP_SAMPLE_AVOID_ORG_CODE_REQUIRED("","统一社会信用代码不能为空！"),
	SP_SAMPLE_AVOID_EXPERTID_REQUIRED("","专家不能为空！"),
	SP_SAMPLE_AVOID_EXIST("","已存在！"),
	SP_SAMPLE_AVOID_TYPE_ERROR("","类别错误！"),
	SP_SAMPLE_AVOID_NOTEXIST("", "不存在"),
	SP_SAMPLE_AVOID_STAR_REQUIRED("","星级不能为空！"),
	SP_SAMPLE_EXPERT_NOTEXIST("", "不存在"),

	BASEPARTY_NOTEXIST("", "不存在"),
	BASEPARTY_NAME_REQUIRED("", "名称不能为空！"),
	BASEPARTY_NAME_EXIST("", "名称已存在！"),

	SAMPLECATEGORYNUM_NOTEXIST("", "不存在"),
	SAMPLECATEGORYNUM_SAMPLEID_REQUIRED("", "不能为空！"),
	SAMPLECATEGORYNUM_SAMPLE_NOTEXIST("", "不存在！"),
	SAMPLECATEGORYNUM_BASECATEGORYID_REQUIRED("", "不能为空！"),
	SAMPLECATEGORYNUM_BASECATEGORY_NOTEXIST("", "不存在！"),
	SAMPLECATEGORYNUM_NUM_REQUIRED("", "抽取专家数不能为空且大于0！"),
	SAMPLECATEGORYNUM_BASECATEGORYID_EXIST("", "已存在！"),
	;


	private String code;
	private String msg;

	private ApiCode(String code, String msg) {
		this.setCode(code);
		this.setMsg(msg);
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	@Override
	public String toString() {
		return "[" + this.code + "]" + this.msg;
	}
}
