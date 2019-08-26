package org.xy.thinking.bean;

import java.util.ArrayList;
import java.util.List;


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
public class Evaluation {
	private String type;//1 临床诊断 2 诊断依据 3 查体 4 辅助检查
	private List<EvaluationNode> list = new ArrayList<EvaluationNode>();
	public List<EvaluationNode> getList() {
		return list;
	}
	public String getType() {
		return type;
	}
    public Evaluation(String type) {
    	this.type=type;
    }

	public void addNode(EvaluationNode list) {
		this.list.add(list); 
	}
	
	public double calucateScore(String text) {
		return 0.0;
	}

}
