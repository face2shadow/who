package org.xy.model;

public class KBRuleUnknownData {
	private int deepth;
	private double rankValue=0;
	private String data;
	public KBRuleUnknownData(int deepth, String data) {
		setDeepth(deepth);setData(data);
	}
	public int getDeepth() {
		return deepth;
	}
	public void setDeepth(int deepth) {
		this.deepth = deepth;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public double getRankValue() {
		return rankValue;
	}
	public void setRankValue(double rankValue) {
		this.rankValue = rankValue;
	}
}
