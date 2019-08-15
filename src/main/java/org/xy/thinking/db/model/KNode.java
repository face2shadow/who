package org.xy.thinking.db.model;

import org.xy.utils.SHA256Digest;

public class KNode {
	private int id;
	private String type;
	private String code;
	private String value;
	private String prop;
	private int caseId;
	private String hash;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getProp() {
		return prop;
	}
	public void setProp(String prop) {
		this.prop = prop;
	}
	public int getCaseId() {
		return caseId;
	}
	public void setCaseId(int caseId) {
		this.caseId = caseId;
	}
	public String getHash() {
		if (hash == null) {
			String content = String.format("%d %s %s %s", getId(),getCode(),getValue(),getProp());
			hash = SHA256Digest.digest(content);
		}
		return hash;
	}
	public void setHash(String hash) {
		this.hash = hash;
	}

	
}
