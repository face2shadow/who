package org.xy.thinking.machine;

import java.util.HashMap;

import org.xy.thinking.mem.MemoryWrapper;


public interface UserInputOutputInterface {
	//说一句话
	public void speak(String content);
	//问问题，第二个参数是选项
	public void ask(String questionId, String content, HashMap<String, String> optionsMap);
	//获取用户的答案
	public String getAnswer(String questionId, HashMap<String, String> optionsMap) ;
	//获取案例的数量
	public int getCaseCount();
	//获取下一个案例
	public boolean nextCase(int i, MemoryWrapper mem);
	//是否关闭会话
	public boolean isClosed();
	//消息通知
	//type=DIGNOSIS_RESULT
	public void notify(String type, String parameters);
}
