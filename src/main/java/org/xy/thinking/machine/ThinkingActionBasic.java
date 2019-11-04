package org.xy.thinking.machine;

import java.util.HashMap;
import java.util.Map;

public class ThinkingActionBasic implements Comparable<ThinkingActionBasic> {
	private String type;
	private String nodeCode;
	private String actionCode;
	private Integer level;
	private String name;
	private Map<String, String> properties = new HashMap<String, String>();

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int compareTo(ThinkingActionBasic o) {
		String thisCode = String.format("%d^%s", getLevel(), getNodeCode() + getActionCode());
		String otherCode = String.format("%d^%s", o.getLevel(), o.getNodeCode() + o.getActionCode());
		return thisCode.compareTo(otherCode);
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public String getNodeCode() {
		return nodeCode;
	}

	public void setNodeCode(String nodeCode) {
		this.nodeCode = nodeCode;
	}

	public String getActionCode() {
		return actionCode;
	}

	public void setActionCode(String actionCode) {
		this.actionCode = actionCode;
	}
}
