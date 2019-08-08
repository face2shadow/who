package org.xy.model;

import java.time.LocalDate;
import java.util.Date;

public class ThinkingResultItem {
	
	private int category = ThinkingResult.NA;
	private String code;
	private String name;
	private String value;
	private double rankValue;
	private LocalDate composed;
	
	@Override
	public String toString() {
		String s = String.format("category=%d, code=%s, name=%s, value=%s, rank=%.2f", 
				category, code, name, value, rankValue);
		return s;
	}
	public ThinkingResultItem() {
		composed = LocalDate.now();
		code = "";
		name = "";
		value = "";
	}

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public LocalDate getComposed() {
		return composed;
	}

	public void setComposed(LocalDate composed) {
		this.composed = composed;
	}
	public double getRankValue() {
		return rankValue;
	}
	public void setRankValue(double rankValue) {
		this.rankValue = rankValue;
	}
	
}
