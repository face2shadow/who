package org.xy.who;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;

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
    public ThinkingBrain initTest() {
		String path = "/Users/alex/Documents/personal documents/ssf/chat";
		//File file = new File(path);
		//File files[] = file.listFiles();

		//ThinkingBrain brain = ThinkingBrain.getInstance();
		KBLoader.loadDKDFromFileSystem(path);
		ThinkingBrain layer = ThinkingBrain.getInstance();
		//for (File f: files) {
		//	if (f.isDirectory()==false) 
		//	{				
		//		String filename = f.getPath();				
		//		layer.loadDKDFromString(readFile(filename),0);
	
		//		System.out.println(filename);
		//	}
		//}
		return layer;
    }
    
	/*
	 * 获取推荐的问题列表
	 */
	public void testGetRecommendQuestions() {
		//初始化环境
		ThinkingBrain layer = initTest();
		//初始化内存管理对象
		MemoryWrapper dsm = new MemoryWrapper();
		//像内存中放入已经提过的问题
		dsm.putData("Q1",  "", "", "+");
		//初始化返回结果对象
		ThinkingResult result = new ThinkingResult();
		try {
			//调用推荐问题的方法，其中参数SCE001是指知识文件的文件名，如SCE001.txt
			layer.getRecommendQuestions("SCE001", dsm, result);
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
		ThinkingBrain layer = initTest();
		//初始化内存管理对象
		MemoryWrapper dsm = new MemoryWrapper();
		//把用户提问的问题代码放入内存对象，这里假设用户提的问题代码为Q2
		//后续会支持对自然文本解析和匹配，这里先用代码
		dsm.putData("USER_SAY_CODE",  "", "Q2", "+");

		//初始化返回结果对象
		ThinkingResult result = new ThinkingResult();
		try {
			//调用推荐问题的方法，其中参数SCE001是指知识文件的文件名，如SCE001.txt
			layer.getResponse("SCE001", dsm, result);
			for (ThinkingResultItem item: result) {
				System.out.println(item);//其中item.getValue()是文字提示 
				//文字提示是用^符号分割的三段，其中第一段是语法标记，一般为SAY，表示说话
				//第二段为代码段，可以代表此次说话内容对应的知识点，可以用于计分
				//第三段为说话的事迹内容
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void testReasoning() {
		ThinkingBrain layer = initTest();
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

