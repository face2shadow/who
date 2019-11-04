package org.xy.thinking.machine;



import java.util.Date;

public class ThinkingGraphVector {
	/**
	 * DB Table
	 * 
	 * id BIGINT 
	 * weight INTEGER 
	 * codeFrom VARCHAR(32) 
	 * codeTo VARCHAR(32)
	 * relationship VARCHAR(32) 
	 * criterialOfPassage VARCHAR(500)
	 * 
	 * PRIMARY INDEX on id 
	 * INDEX ON codeFrom, codeTo 
	 * INDEX ON relationship
	 */
	private ThinkingGraphNode from;
	private ThinkingGraphNode to;
	private String relationship;
	private String codeFrom;
	private String codeTo;
	private String criterialOfPassage;
	private Long id;
	private String type;
	private String version;
	private Integer status;
	private Date createTime;
	private Date updateTime;
	private Integer weight;

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

	public String getCriterialOfPassage() {
		return criterialOfPassage;
	}

	public void setCriterialOfPassage(String rule) {
		this.criterialOfPassage = rule;
	}

	public void setCriterialRuleOfPassage(String rule) {
		this.criterialOfPassage = "DI1|"+getCode()+"|rule~"+rule+",code~"+getCode()+"\n";
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return String.format("%s->%s", getCodeFrom(), getCodeTo());
	}

	public String getRelationship() {
		return relationship;
	}

	public void setRelationship(String relationship) {
		this.relationship = relationship;
	}
	
	public String getCodeFrom() {
		return codeFrom;
	}
	
	public void setCodeFrom(String codeFrom) {
		this.codeFrom = codeFrom;
	}
	
	public String getCodeTo() {
		return codeTo;
	}
	
	public void setCodeTo(String codeTo) {
		this.codeTo = codeTo;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
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
