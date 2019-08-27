package org.xy.thinking.db.model;

import java.util.Date;

public class QuestionLink {
	private int id;
	private String questionCodeFrom;
	private String questionCodeTo;
	private Date composed;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getQuestionCodeFrom() {
		return questionCodeFrom;
	}
	public void setQuestionCodeFrom(String questionCodeFrom) {
		this.questionCodeFrom = questionCodeFrom;
	}
	public String getQuestionCodeTo() {
		return questionCodeTo;
	}
	public void setQuestionCodeTo(String questionCodeTo) {
		this.questionCodeTo = questionCodeTo;
	}
	public Date getComposed() {
		return composed;
	}
	public void setComposed(Date composed) {
		this.composed = composed;
	}
}
