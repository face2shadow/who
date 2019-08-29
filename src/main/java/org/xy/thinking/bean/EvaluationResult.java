package org.xy.thinking.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wltea.analyzer.core.Lexeme;
import org.xy.utils.SplitWord;


/*
2.临床诊断及依据（15分）
（1）临床诊断：（5分）
右侧胸腔积液（2.5分）：结核性胸膜炎考虑（2.5分）。
（2）诊断依据：（10分）
1）青年男性，亚急性病程。（2分）
2）干咳3周加重伴胸闷1周。（2分）
3）长期吸烟200年支，户外工作环境。（2分）
(3) 查体：T：37.5℃，P：90次／min，R：24次／min，BP 120／90mmHg。
右侧胸部呼吸活动度减低，触觉语颤明显消失，气管左偏，叩诊实音，左侧呼吸音正常，
右侧呼吸音消失，未闻及干湿性啰音。（2分）
(4) 辅助检查：血常规WBC7.5×109/L,N 76%,CRP：25g/L。
生化提示：肝肾功能、电解质、心肌酶等正常。胸片：提示右侧大量胸腔积液。
心电图：窦性心律;血气提示：呼吸性碱中毒伴肺泡动脉氧分压差增高。（2分）

*/
public class EvaluationResult {
	private String type;//1 临床诊断 2 诊断依据 3 查体 4 辅助检查
	private String userText;//用户输入答案
	private Map<String,Lexeme> words=new HashMap<String,Lexeme>();//存词性
	private List<EvaluationNode> rightNode = new ArrayList<EvaluationNode>();//正确知识点
	private List<EvaluationNode> wrongNode = new ArrayList<EvaluationNode>();//错误知识点
	private double finalScore=0.0;
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		for (EvaluationNode node : rightNode) {
			s.append(String.format("code~%s,id~%s,text~'%s',keypoints~'%s',score~%f\n", type, node.getId(), node.getOriginText(), node.getKeypoints(), node.getScore()));
		}
		for (EvaluationNode node : wrongNode) {
			s.append(String.format("code~%s,id~%s,text~'%s',keypoints~'%s',score~%f\n", type, node.getId(), node.getOriginText(), node.getKeypoints(), node.getScore()));
		}
		return s.toString();
	}
	public double getFinalScore() {
		return finalScore;
	}
	public void addFinalScore(double finalScore) {
		this.finalScore += finalScore;
	}
	public EvaluationResult( String text) {
		List<Lexeme> segs=SplitWord.ikCutWordLexeme(text);
		StringBuffer str=new StringBuffer();
		for(Lexeme seg:segs) {
			words.put(seg.getLexemeText(), seg);
			str.append(seg.getLexemeText()).append(" ");
		}
		this.userText=str.substring(0,(str.length()-1)<0?0:(str.length()-1)); 
	}
	public String getUserText() {
		return userText;
	}

	public void setType(String type) {
		this.type = type;
	}
	public List<EvaluationNode> getList() {
		return rightNode;
	}
	public String getType() {
		return type;
	}
 
	public void addRightNode(EvaluationNode list) {
		this.rightNode.add(list); 
	}
	public void addWrongNode(EvaluationNode list) {
		this.wrongNode.add(list); 
	}
	public List<EvaluationNode> getRightNode() {
		return rightNode;
	}
	public void setRightNode(List<EvaluationNode> rightNode) {
		this.rightNode = rightNode;
	}
	public List<EvaluationNode> getWrongNode() {
		return wrongNode;
	}
	public void setWrongNode(List<EvaluationNode> wrongNode) {
		this.wrongNode = wrongNode;
	}
}
