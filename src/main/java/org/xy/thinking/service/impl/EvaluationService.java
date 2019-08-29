package org.xy.thinking.service.impl;

import java.util.List;
import java.util.Set;

import org.wltea.analyzer.core.Lexeme;
import org.xy.thinking.bean.Evaluation;
import org.xy.thinking.bean.EvaluationNode;
import org.xy.thinking.bean.EvaluationResult;
import org.xy.utils.SplitWord;

public class EvaluationService {
	
	public String getKeyWord(String text) {
		return "";
	}
	public EvaluationResult calculateScore(Evaluation eva,String text) {
		EvaluationResult result=new EvaluationResult(text); 
		result.setType(eva.getType()); 
		Set<String> l=SplitWord.ikCutWordSet(result.getUserText());
		for(EvaluationNode d:eva.getList()) {
			double s_score=d.calculateSimilarity(l);
			if(s_score>0.0) {
				result.addFinalScore(d.getScore());
				l.removeAll(d.getIntersection());
				result.addRightNode(d); 
			} 
			else { 
				result.addWrongNode(d); 
			}
		}
		return result;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		EvaluationService es= new EvaluationService();
		String text="1、胸腔积液 2、气胸";
		//List<String> l=SplitWord.ikCutWord("临床诊断：（5分）\r\n" + 
		//		"右侧胸腔积液（2.5分）：结核性胸膜炎考虑（2.5分）");
		//System.out.println(l);
		Evaluation ev=new Evaluation("临床诊断");
		//ev.addNode(new EvaluationNode("右侧胸腔积液",2.5));
		//ev.addNode(new EvaluationNode("结核性胸膜炎考虑",2.5));
		EvaluationResult r=es.calculateScore(ev, text);
		System.out.println(r); 
		

	}

}
