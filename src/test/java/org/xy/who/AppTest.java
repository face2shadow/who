package org.xy.who;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.xy.model.ThinkingResult;
import org.xy.model.ThinkingResultItem;
import org.xy.thinking.ThinkingBrain;
import org.xy.thinking.ThinkingDiagnosis;
import org.xy.thinking.def.KBLoader;
import org.xy.thinking.mem.MemoryWrapper;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    //public ThinkingBrain initTest() {
	//	String path = "/Users/alex/Documents/personal documents/ssf/chat";
		//File file = new File(path);
		//File files[] = file.listFiles();

		//ThinkingBrain brain = ThinkingBrain.getInstance();
	//	KBLoader.loadDKDFromFileSystem(path);
	//	ThinkingBrain layer = ThinkingBrain.getInstance();
		//for (File f: files) {
		//	if (f.isDirectory()==false) 
		//	{				
		//		String filename = f.getPath();				
		//		layer.loadDKDFromString(readFile(filename),0);
	
		//		System.out.println(filename);
		//	}
		//}
	//	return layer;
   // }
    private static ThinkingBrain _instance = null;
    private ThinkingBrain getBrain() {
    	if (_instance == null) {
		ConfigurableApplicationContext ctx = SpringApplication.run(ThinkingBrain.class,new String[] {});
		_instance = ctx.getBean(ThinkingBrain.class);
	}
	return _instance;
    }
	/*
	 * 获取推荐的问题列表
	 */
	public void testGetRecommendQuestions() {
		//初始化环境
		ThinkingBrain layer = getBrain() ;
		//初始化内存管理对象
		MemoryWrapper dsm = new MemoryWrapper();
		//像内存中放入已经提过的问题
		dsm.putData("Q1",  "", "", "+");
		//初始化返回结果对象
		ThinkingResult result = new ThinkingResult();
		try {
			//调用推荐问题的方法，其中参数SCE001是指知识文件的文件名，如SCE001.txt
			layer.getRecommendQuestions("SCE001", dsm, result,3);
			for (ThinkingResultItem item: result) {
				System.out.println(item); //其中item.getValue()是文字提示 item.getCode()是问题的代码
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/*
	 * 获取问题的答案
	 */
	public void testGetResponse() {
		//初始化环境
		ThinkingBrain layer = getBrain() ;
		//初始化内存管理对象
		MemoryWrapper dsm = new MemoryWrapper();
		
		//已经支持了分词，所以这里模拟了用户说的几句话
		//String[] user_says = {"孕妇可不可以吃西瓜","没有糖尿病","没有3个月","然后呢？"};
		String[] user_says = {"患者的信息？","患者症状", "患者尿量","具体在哪个部位?","患者主诉是什么？","具体在哪个部位?","症状样式"};
		
		//String[] user_says = {"患者的主诉是什么？","患者 年龄", "患者 性别","具体在哪个部位?","患者主诉是什么？","具体在哪个部位?","症状样式"};
		String sceneCode = "SCE001";

		//初始化返回结果对象
		ThinkingResult result = new ThinkingResult();
		String lastString = ""; //CAUTION: BE AWARE OF USER's POS/NEG/OTHER response
		try {
			for (String s: user_says) {
				//调用推荐问题的方法，其中参数SCE001是指知识文件的文件名，如SCE001.txt、
				//参数3 是指限制了返回问题的最大数量
				layer.getRecommendQuestions(sceneCode, dsm, result,3);
				
				for (ThinkingResultItem item: result) {
					System.out.println("系统推荐的问题: "+item.getName());
				}
				System.out.println("----------------------------");
				//这里要把用户说的话放在dsm里面，传给系统
				dsm.putData("USER_SAY",  "", s , "+");
				System.out.println("您说: "+dsm.getData("USER_SAY"));
				//这里是调用了系统，获得回复
				layer.getResponse(sceneCode, dsm, result);
				//这里是打印回复的内容，一般只会回复一条结果
				for (ThinkingResultItem item: result) {
					String[] parts = item.getValue().split("\\,");//其中item.getValue()是文字提示 

					//文字提示是用^符号分割的三段，其中第一段是语法标记，一般为SAY，表示说话
					//第二段为代码段，可以代表此次说话内容对应的知识点，可以用于计分
					//第三段为说话的实际内容
					System.out.println("系统回复: "+parts[0]);
					if (parts.length>3){
						lastString = parts[3];
					} else {
						
					}
					System.out.println("----------------------------");
				}
				//System.out.println(dsm.getData("USER_CONTEXT").getValue());
				//System.out.println("----------------------------");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void testComparision() {
		//初始化环境
		ThinkingBrain layer = getBrain() ;
		//初始化内存管理对象
		MemoryWrapper dsm = new MemoryWrapper();
		
		//已经支持了分词，所以这里模拟了用户说的几句话
		//String[] user_says = {"孕妇可不可以吃西瓜","没有糖尿病","没有3个月","然后呢？"};
		
		String[] user_says = {"临床诊断 胸腔积液\n气胸\n患者 性别\n具体在哪个部位?\n患者主诉是什么？\n具体在哪个部位?\n症状样式"};
		String sceneCode = "SCE001-1";

		//初始化返回结果对象
		ThinkingResult result = new ThinkingResult();
		String lastString = ""; //CAUTION: BE AWARE OF USER's POS/NEG/OTHER response
		try {
			for (String s: user_says) {

				//这里是调用了系统，获得回复
				layer.compareKnowledge(sceneCode, s, result);
				//这里是打印回复的内容，一般只会回复一条结果
				for (ThinkingResultItem item: result) {
					String[] parts = item.getValue().split("\\,");//其中item.getValue()是文字提示 

					//文字提示是用^符号分割的三段，其中第一段是语法标记，一般为SAY，表示说话
					//第二段为代码段，可以代表此次说话内容对应的知识点，可以用于计分
					//第三段为说话的实际内容
					System.out.println("系统回复: "+parts[0]+item.getCategory());
					if (parts.length>3){
						lastString = parts[3];
					} else {
						
					}
					System.out.println("----------------------------");
				}
				//System.out.println(dsm.getData("USER_CONTEXT").getValue());
				//System.out.println("----------------------------");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	public void testReasoning() {
		ThinkingBrain layer = getBrain() ;
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
		
	}
}	
	/*
	public void testWork() throws Exception {
		ThinkingBrain layer = initTest();
		DKDDiagnosisResult result = new DKDDiagnosisResult();
		DSMWrapper dsm = new DSMWrapper();
		dsm.putData("Q_SYM","Q5678","孕妇能不能吃西瓜啊？","+");
		System.out.println("孕妇能不能吃西瓜啊？");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		while(true) {dsm.dumpDSM();
			layer.think(dsm, null);	
			for (String key: result.getQuestions().keySet()) {
				
				System.out.println(result.getQuestions().get(key).getCode()+":"+result.getQuestions().get(key).getText());
				String s = br.readLine();
				if (s.startsWith("y")) {
					dsm.putData(result.getQuestions().get(key).getCode(), "","","+");
				} else {
					dsm.putData(result.getQuestions().get(key).getCode(), "","","-");
				}
				
			}
			
			
		}
		//dsm.putData("Q5679","","","-");
		//dsm.putData("Q5680","","","+");
		//dsm.putData("TOPIC_CODE","","SCE005","+");
		//dsm.putData("TOPIC_STATE","","0","+");
		//layer.think(dsm, result, "SCE005");	
		//for (Action act : result.getActions()) {
		//	System.out.println(act.getHead()+" "+act.getValue());
		//}
		/*ThinkingRule rule1 = new ThinkingRule();
		ThinkingRule rule2 = new ThinkingRule();
		ThinkingRule rule3 = new ThinkingRule();
		ThinkingRule rule4 = new ThinkingRule();
		rule1.parse("1 and 2 and (3 or 4) and 5 and 6 and (7 or 8)");
		rule2.parse("1 and 2 and match('3-4':1)");
		rule3.parse("3 or 4 and 1 and 2");
		rule4.parse("(1 and 2 and (3 or 4)) and 5 and match('3-4':1)");
		rule2.dumps(null, "    ");
		dsm.putData("1","","","+");
		dsm.putData("2","","","-");
		dsm.putData("3","","","-");
		dsm.putData("4","","","+");
		System.out.println(rule1.eval(dsm));
		System.out.println(rule2.eval(dsm));
		System.out.println(rule3.eval(dsm));
		System.out.println(rule4.eval(dsm));*/
	//}*/

