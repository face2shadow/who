package org.xy.thinking.machine;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;


public class ThinkingGraphNode {
	/**
	 * DB Table
	 * 
	 * id BIGINT 
	 * level INTEGER 
	 * name VARCHAR(32) 
	 * code VARCHAR(32) 
	 * entryCriteria VARCHAR(500) 
	 * actionContent VARCHAR(500) 
	 * consistOf VARCHAR(170)
	 * priorityNodeCodes VARCHAR(170)
	 * 
	 * PRIMARY INDEX on id 
	 * UNIQUE INDEX ON code
	 */
	private Long id;
	private Integer level = 9; //level =0 表示是入口
	private String name;
	private String code;
	private String type = "";
	private String entryCriteria;
	private String actionContent;
	private String consistOf;
	private String priorityNodeCodes;
	private String version;
	private Integer status;
	private Date createTime;
	private Date updateTime;
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEntryCriteria() {
		return entryCriteria;
	}

	public void setEntryCriteria(String content) {
		this.entryCriteria = content+"\n";
		
	}
	public void setEntryCriteriaRule(String rule) {
		this.entryCriteria = "DI0|"+getCode()+"|"+getName()+"|rule~"+rule+",code~"+getCode()+"\n";
		
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getConsistOf() {
		return consistOf;
	}

	public void setConsistOf(String consistOf) {
		this.consistOf = consistOf;
	}

	public String getActionContent() {
		return actionContent;
	}

	public void setActionContent(String actionContent) {
		this.actionContent = actionContent;
	}

	public String getPriorityNodeCodes() {
		return priorityNodeCodes;
	}

	public void setPriorityNodeCodes(String priorityNodeCodes) {
		this.priorityNodeCodes = priorityNodeCodes;
	}
	
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

}
