package com.glface.modules.utils;

/**
 * 项目状态 需要和数据字典(projectStatus)一致
 */
public enum ProjectStatusEnum {
	NOT_APPLY("0", "未申请","未申请"),
	SUBMITTED("1", "已提交","提交"),
	DISTRICT_AGREE("2", "区县审核通过","通过"),
	DISTRICT_REJECT("3", "区县审核驳回","驳回"),
	DISTRICT_REJECT_WAIT("16", "区县审核驳回等待补充资料","驳回待补充资料"),
	FIRST_AGREE("4", "初审通过","通过"),
	TO_DISTRICT("13", "分发至区县审批","分发至区县审批"),
	FIRST_REJECT("5", "初审驳回","驳回"),
	FIRST_REJECT_WAIT("14", "初审驳回等待补充资料","驳回待补充资料"),
	RECOMMEND_AGREE("6", "推荐审核通过","通过"),
	RECOMMEND_REJECT("7", "推荐审核驳回","驳回"),
	RECOMMEND_REJECT_WAIT("15", "推荐审核驳回等待补充资料","驳回待补充资料"),
	EXPERT_REJECT("8", "评审驳回","驳回"),
	EXPERT_AGREE("9", "评审通过","通过"),
	MATERIALS_SUBMITTED("10", "实施项目材料已提交","材料已提交"),
	MATERIALS_AGREE("11", "实施项目材料审核通过","通过"),
	MATERIALS_REJECT("12", "实施项目材料审核驳回","驳回"),
	FILE("99", "归档","归档"),
	;

	private String value;
	private String label;
	private String shortLabel;

	ProjectStatusEnum(String value, String label,String shortLabel) {
		this.setValue(value);
		this.setLabel(label);
		this.setShortLabel(shortLabel);
	}

	public static ProjectStatusEnum getProjectStatusEnumByValue(String value) {
		ProjectStatusEnum[] enums = ProjectStatusEnum.values();
		for (int i = 0; i < enums.length; i++) {
			if (enums[i].getValue().equals(value)) {
				return enums[i];
			}
		}
		return null;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getShortLabel() {
		return shortLabel;
	}

	public void setShortLabel(String shortLabel) {
		this.shortLabel = shortLabel;
	}

	@Override
	public String toString() {
		return "[" + this.value + "]" + this.label;
	}
}
