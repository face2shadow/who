package org.xy.thinking.db.model;

import java.util.Date;

public class CaseSection {
	private int id;
	private String caseCode;
	private String code;
	private String name;
	private Date composed;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	public Date getComposed() {
		return composed;
	}
	public void setComposed(Date composed) {
		this.composed = composed;
	}
	public String getCaseCode() {
		return caseCode;
	}
	public void setCaseCode(String caseCode) {
		this.caseCode = caseCode;
	}
}
