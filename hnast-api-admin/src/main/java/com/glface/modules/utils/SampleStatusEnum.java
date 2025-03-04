package com.glface.modules.utils;

/**
 *评审状态
 */
public enum SampleStatusEnum {
	NO("0", "待抽取","待抽取"),
	YES("1", "已抽取","已抽取"),
	;

	private String value;
	private String label;
	private String shortLabel;

	SampleStatusEnum(String value, String label, String shortLabel) {
		this.setValue(value);
		this.setLabel(label);
		this.setShortLabel(shortLabel);
	}

	public static SampleStatusEnum getProjectStatusEnumByValue(String value) {
		SampleStatusEnum[] enums = SampleStatusEnum.values();
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
