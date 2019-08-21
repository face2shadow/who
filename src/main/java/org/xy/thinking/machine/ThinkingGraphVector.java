package org.xy.thinking.machine;

public class ThinkingGraphVector {
	private ThinkingGraphNode from;
	private ThinkingGraphNode to;
	private String rule;
	private int id;
	
	public ThinkingGraphNode getFrom() {
		return from;
	}
	public void setFrom(ThinkingGraphNode from) {
		this.from = from;
	}
	public ThinkingGraphNode getTo() {
		return to;
	}
	public void setTo(ThinkingGraphNode to) {
		this.to = to;
	}
	public String getRule() {
		return rule;
	}
	public void setRule(String rule) {
		this.rule = rule;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
}
