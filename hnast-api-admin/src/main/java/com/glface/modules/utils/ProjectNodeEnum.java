package com.glface.modules.utils;

/**
 * 项目节点 需要和数据字典(processNode)一致
 */
public enum ProjectNodeEnum {
	START("start", "发起申请"),
	DISTRICT_REVIEW("districtReview", "区县初审"),
	FIRST_REVIEW("firstReview", "初审"),
	REPLENISH("replenish", "补充内容"),
	RECOMMEND_REVIEW("recommendReview", "推荐审核"),
	EXPERT_REVIEW("expertReview", "专家评审"),
	MATERIALS("materials", "填报项目实施材料"),
	MATERIALS_REVIEW("materialsReview", "实施材料审核"),
	FILE("file", "归档"),
	;

	private String value;
	private String label;

	ProjectNodeEnum(String value, String label) {
		this.setValue(value);
		this.setLabel(label);
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

	@Override
	public String toString() {
		return "[" + this.value + "]" + this.label;
	}
}
