package org.xy.thinking.db.model;

import java.util.Date;

public class Answer {
	private int id;
	private String questionCode;
	private String answerCode;
	private String questionText;
	private String answerText;
	private String keypoints;
	private String questionPattern;
	private Date composed;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getQuestionCode() {
		return questionCode;
	}
	public void setQuestionCode(String questionCode) {
		this.questionCode = questionCode;
	}
	public String getAnswerCode() {
		return answerCode;
	}
	public void setAnswerCode(String answerCode) {
		this.answerCode = answerCode;
	}
	public String getQuestionText() {
		return questionText;
	}
	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}
	public String getAnswerText() {
		return answerText;
	}
	public void setAnswerText(String answerText) {
		this.answerText = answerText;
	}
	public String getKeypoints() {
		return keypoints;
	}
	public void setKeypoints(String keypoints) {
		this.keypoints = keypoints;
	}
	public Date getComposed() {
		return composed;
	}
	public void setComposed(Date composed) {
		this.composed = composed;
	}
	public String getQuestionPattern() {
		return questionPattern;
	}
	public void setQuestionPattern(String questionPattern) {
		this.questionPattern = questionPattern;
	}
}
