package org.xy.model;

import java.util.ArrayList;
import java.util.HashMap;

public class ThinkingResult extends ArrayList<ThinkingResultItem> {
//	public static final int THINK_NOTHING = 0;
//	public static final int THINK_AND_CONTINUE = 1;
//	public static final int THINK_AND_STOP = 2;
//	private int stage = THINK_NOTHING;

	public static final int NA = 0;
	public static final int ACT = 1;
	public static final int MEMORY = 2;
	public static final int ASK = 3;
	private ResultEnum result = ResultEnum.SystemDontKnow;
	
	public void addItem(int category, String code, String name, String value) {
		ThinkingResultItem item = new ThinkingResultItem();
		item.setCategory(category);
		item.setCode(code);
		item.setName(name);
		item.setValue(value);
		add(item);
	}
	
	public void keepLastOne() {
		if (this.size()>1) {
			this.removeRange(0, this.size()-1);
		}
	}

	public ResultEnum getResult() {
		return result;
	}

	public void setResult(ResultEnum stage) {
		this.result = stage;
	}
	
	public ThinkingResultItem getItem(int category, String code) {
		for (ThinkingResultItem item: this) {
			if (item.getCategory() == category && item.getCode().compareToIgnoreCase(code)==0) {
				return item;
			}
		}
		return null;
	}
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (ThinkingResultItem item: this) {
			builder.append(item.toString()+"\n");
		}
		return builder.toString();
	}
}
