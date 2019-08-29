package org.xy.thinking.bean;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.wltea.analyzer.core.Lexeme;
import org.xy.utils.SplitWord;

public class EvaluationNode {
	private String id;
	private String keypoints;
	private String text;
	private String originText;
	private double score;
	private Set<String> intersection=new LinkedHashSet<String>();//存放交集词语

	private Set<String> sets=new LinkedHashSet<String>();//相似度计算特征
	public EvaluationNode(String id, String text,double score, String keypoints) {
		List<Lexeme> segs=SplitWord.ikCutWordLexeme(text);
		originText = text;
		StringBuffer str=new StringBuffer();
		for(Lexeme seg:segs) {
			str.append(seg.getLexemeText()).append(" ");
			sets.add(seg.getLexemeText());
		}
		this.text=str.substring(0,(str.length()-1)<0?0:(str.length()-1)); 
		this.score=score;
		this.id = id;
		this.keypoints = keypoints;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	public Set<String> getIntersection() {
		return intersection;
	}
//	public Lexeme getWordInfo(String word) {
//		Lexeme lex=null;
//		if(this.words.containsKey(word)) {
//			lex=this.words.get(word);
//		}
//		return lex;
//	}
	public double calculateSimilarity(Set<String> b) {
		//double score=0.0;
		intersection.clear();
		int num=0;
		Set<String> all=new HashSet<String>();
		all.addAll(this.sets);
		all.addAll(b);
		for(String d:b) {
			if(this.sets.contains(d)) {
				intersection.add(d);
				num++;
			}
		}

		return (double)num/all.size();
	}
	@Override
	public String toString() {
		return "EvaluationNode [text=" + text + ", score=" + score + "]";
	}
	public String getKeypoints() {
		return keypoints;
	}
	public void setKeypoints(String keypoints) {
		this.keypoints = keypoints;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getOriginText() {
		return originText;
	}
	public void setOriginText(String originText) {
		this.originText = originText;
	}
}
