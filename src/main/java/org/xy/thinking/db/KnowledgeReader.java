package org.xy.thinking.db;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.xy.model.KBLine;
import org.xy.model.KBSection;
import org.xy.model.ThinkingResult;
import org.xy.model.ThinkingResultItem;
import org.xy.thinking.ThinkingBrain;
import org.xy.thinking.ThinkingDiagnosis;
import org.xy.thinking.db.dao.AnswerDao;
import org.xy.thinking.db.dao.CaseDao;
import org.xy.thinking.db.dao.CaseSectionDao;
import org.xy.thinking.db.dao.KeypointDao;
import org.xy.thinking.db.model.Answer;
import org.xy.thinking.db.model.Case;
import org.xy.thinking.db.model.CaseSection;
import org.xy.thinking.db.model.KNode;
import org.xy.thinking.db.model.Keypoint;
import org.xy.thinking.def.KBDefinitionMap;
import org.xy.thinking.def.KBLoader;
import org.xy.thinking.machine.ThinkingGraph;
import org.xy.thinking.machine.ThinkingGraphNode;
import org.xy.thinking.mem.MemoryWrapper;
import org.xy.thinking.service.CaseDatabaseService;
import org.xy.thinking.service.ThinkingDatabaseService;

@SpringBootApplication
@ComponentScan("org.xy.thinking")
public class KnowledgeReader {
	@Autowired
	CaseDatabaseService service;
	

	public ThinkingResult testGetRecommendQuestions() {
		//初始化环境
		ThinkingBrain layer = ThinkingBrain.getInstance();
		//初始化内存管理对象
		MemoryWrapper dsm = new MemoryWrapper();
		//像内存中放入已经提过的问题
		dsm.putData("Q1",  "", "", "+");
		//初始化返回结果对象
		ThinkingResult result = new ThinkingResult();
		try {
			System.out.println("22222");
			//调用推荐问题的方法，其中参数SCE001是指知识文件的文件名，如SCE001.txt
			layer.getRecommendQuestions("SCE001", dsm, result,3);
			for (ThinkingResultItem item: result) {
				System.out.println(item); //其中item.getValue()是文字提示 item.getCode()是问题的代码
			}
			System.out.println(result+"     33333");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	/*
	 * 获取问题的答案
	 */
	public ThinkingResult testGetResponse() {
		//初始化环境
		ThinkingBrain layer = ThinkingBrain.getInstance();
		//初始化内存管理对象
		MemoryWrapper dsm = new MemoryWrapper();
		
		//已经支持了分词，所以这里模拟了用户说的几句话
		String[] user_says = {"患者的信息？","患者症状", "患者尿量","具体在哪个部位?","患者主诉是什么？","具体在哪个部位?","症状样式"};
		String sceneCode = "SCE001";

		//初始化返回结果对象
		ThinkingResult result = new ThinkingResult();
		try {
			for (String s: user_says) {
				//调用推荐问题的方法，其中参数SCE001是指知识文件的文件名，如SCE001.txt、
				//参数3 是指限制了返回问题的最大数量
				layer.getRecommendQuestions(sceneCode, dsm, result,3);
				
				for (ThinkingResultItem item: result) {
					System.out.println("系统推荐的问题: "+item.getValue());
				}
				System.out.println("----------------------------");
				//这里要把用户说的话放在dsm里面，传给系统
				dsm.putData("USER_SAY",  "", s, "+");
				System.out.println("您说: "+dsm.getData("USER_SAY"));
				//这里是调用了系统，获得回复
				layer.getResponse(sceneCode, dsm, result);
				//这里是打印回复的内容，一般只会回复一条结果
				for (ThinkingResultItem item: result) {
					String[] parts = item.getValue().split("\\^");//其中item.getValue()是文字提示 

					//文字提示是用^符号分割的三段，其中第一段是语法标记，一般为SAY，表示说话
					//第二段为代码段，可以代表此次说话内容对应的知识点，可以用于计分
					//第三段为说话的实际内容
					System.out.println("系统回复: "+item.getValue());

					System.out.println("----------------------------");
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	public ThinkingResult testReasoning() {
		ThinkingBrain layer = ThinkingBrain.getInstance();
		ThinkingDiagnosis diagnosis = new ThinkingDiagnosis();
		ThinkingResult result = new ThinkingResult();
		MemoryWrapper dsm = new MemoryWrapper();
		dsm.putData("Q_SYM","Q5678","孕妇能不能吃西瓜啊？","+");
		System.out.println("孕妇能不能吃西瓜啊？");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		
			dsm.dumpDSM();
			try {
				result = diagnosis.think(dsm, result,"DIS","DIG", "SYS000");
			System.out.println(result.toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return result;
		
	}
	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(KnowledgeReader.class,args);
		KnowledgeReader w = ctx.getBean(KnowledgeReader.class);
	
		String content = w.service.readCase("SCE001");
		System.out.println(content);
		
		KBLoader.loadDKDFromString(content, 0);
		w.testGetResponse();
	}
}
