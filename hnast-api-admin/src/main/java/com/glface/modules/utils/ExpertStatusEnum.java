package com.glface.modules.utils;

/**
 * 状态 需要和数据字典(expertStatus)一致
 */
public enum ExpertStatusEnum {
	NOT_APPLY("0", "未申报","未申报"),
	SUBMITTED("1", "已提交","提交"),
	AGREE("2", "通过","通过"),
	REJECT("3", "未通过","未通过"),
	OUT("4", "出库","出库"),
	;

	private String value;
	private String label;
	private String shortLabel;

	ExpertStatusEnum(String value, String label, String shortLabel) {
		this.setValue(value);
		this.setLabel(label);
		this.setShortLabel(shortLabel);
	}

	public static ExpertStatusEnum getProjectStatusEnumByValue(String value) {
		ExpertStatusEnum[] enums = ExpertStatusEnum.values();
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
