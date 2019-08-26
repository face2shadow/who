package org.xy.thinking.machine;

import java.util.LinkedList;
import java.util.List;


public class ThinkingGraphNode {
	private int id;
	private int level;
	private String name;
	private String code;
	private String content;
	private String consistOf;
	private List<ThinkingGraphVector> links = new LinkedList<ThinkingGraphVector>();
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<ThinkingGraphVector> getLinks() {
		return links;
	}
	public void setLinks(List<ThinkingGraphVector> links) {
		this.links = links;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getConsistOf() {
		return consistOf;
	}
	public void setConsistOf(String consistOf) {
		this.consistOf = consistOf;
	}

}
