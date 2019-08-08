package org.xy.thinking.rule;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

import org.xy.model.ResultEnum;
import org.xy.thinking.mem.MemoryWrapper;
import org.xy.thinking.mem.MemoryWrapper.DSMData;


public class ThinkMethod {
	public String estimate(String name, MemoryWrapper mem,String parameters) throws Exception {
		for (Method m : this.getClass().getMethods()) {
			if (m.isAnnotationPresent(ThinkFunc.class)) {
				ThinkFunc tf = m.getAnnotation(ThinkFunc.class);
				if (tf.domain().compareTo("estimate") ==0 && tf.name().compareTo(name) ==0) {
					return (String)m.invoke(mem,  parameters);
				}
			}
		}
		return null;
	}
	public ResultEnum evaluate(String name, MemoryWrapper mem,String parameters) throws Exception {
		boolean reversed = false;
		if (name.startsWith("!")){
			name = name.substring(1);
			reversed = true;
		}
		for (Method m : this.getClass().getMethods()) {
			if (m.isAnnotationPresent(ThinkFunc.class)) {
				ThinkFunc tf = m.getAnnotation(ThinkFunc.class);				

				if (tf.domain().compareTo("evaluate") ==0 && tf.name().compareTo(name) ==0 ) {
					ResultEnum r = (ResultEnum)m.invoke(this, new Object[] {mem,  parameters});
					if (reversed == false)	return r;
					if (reversed == true) {
						if (ResultEnum.isPositive(r)) return ResultEnum.Negative;
						if (ResultEnum.isNegative(r)) return ResultEnum.Positive;
					}
				}
			}
		}
		return ResultEnum.SystemDontKnow;
	}
	//match funtion
	@ThinkFunc(domain="evaluate", name="match")
	public ResultEnum matchEvaluate(MemoryWrapper mem,String parameters) throws Exception {
		String parts[] = parameters.split(":");
		if (parts.length < 2) {
			return ResultEnum.SystemDontKnow;
		}
		String para1 = parts[0];
		String para2 = parts[1];
		if (para1.startsWith("'") || para1.startsWith("\""))
			para1 = para1.substring(1, para1.length()-1);
		String wait_4_count[] = para1.split("-");
		int count = 0;
		int sure_count = 0;
		for (String w : wait_4_count) {
			String key = w;
			boolean reverse = false;
			if (key.startsWith("!")) {
				reverse = true;
				key = key.substring(1, key.length());
			}

			MemoryWrapper.DSMData data = mem.getData("_"+key) == null ? mem.getData(key) : mem.getData("_"+key);

			if (data != null) {
				if (ResultEnum.isPositive(data.getResult()) || ResultEnum.isNegative(data.getResult()) || ResultEnum.isUserDontKnow(data.getResult())) {
					sure_count ++;
				}
				if (!reverse && ResultEnum.isPositive(data.getResult())) {
					count = count + 1;
				}if (reverse && ResultEnum.isNegative(data.getResult())) {
					count = count + 1;
				}
			}
		}
		if (count >= Integer.parseInt(para2)) {
			return ResultEnum.Positive;
		}
		if (sure_count >= wait_4_count.length) {
			return ResultEnum.Negative;
		}
		return ResultEnum.SystemDontKnow;
	}
	@ThinkFunc(domain="estimate", name="match")
	public String matchEstimate(MemoryWrapper mem,String parameters) throws Exception {
		String parts[] = parameters.split(":");
		if (parts.length < 2) {
			return null;
		}
		String para1 = parts[0];
		String para2 = parts[1];
		if (para1.startsWith("'") || para1.startsWith("\""))
			para1 = para1.substring(1, para1.length()-1);
		String wait_4_count[] = para1.split("-");
		int count = 0;
		int sure_count = 0;
		for (String w : wait_4_count) {
			String key = w;
			boolean reverse = false;
			if (key.startsWith("!")) {
				reverse = true;
				key = key.substring(1, key.length());
			}

			MemoryWrapper.DSMData data = mem.getData("_"+key) == null ? mem.getData(key) : mem.getData("_"+key);
			if (data == null) return w;

			if (ResultEnum.isSystemDontKnow(data.getResult())) return w;

		}
		return null;
	}
	@ThinkFunc(domain="estimate", name="exists")
	public String dsmExistEstimate(MemoryWrapper mem,String parameters) throws Exception {
		String para1 = parameters;
		if (para1.startsWith("'") || para1.startsWith("\""))
			para1 = para1.substring(1, para1.length()-1);
		MemoryWrapper.DSMData data = mem.getData(para1);
		if (data == null || ResultEnum.isSystemDontKnow(data.getResult())) return para1;
		return null;
	}
	//判断dsm中是否存在指定的code，code对应的dsm值存在，不管flag是多少都返回positive
	@ThinkFunc(domain="evaluate", name="exists")
	public ResultEnum dsmExist(MemoryWrapper mem,String parameters) throws Exception{
		String para1 = parameters;
		if (para1.startsWith("'") || para1.startsWith("\""))
			para1 = para1.substring(1, para1.length()-1);

		MemoryWrapper.DSMData data = mem.getData(para1);
		if (data != null) return ResultEnum.Positive;
		return ResultEnum.Negative;
	}
	//判断当前的时间范围是否符合条件，条件和age的范围一样，单位是小时
	@ThinkFunc(domain="evaluate", name="time_range")
	public ResultEnum hourRangeEvaluate(MemoryWrapper mem,String parameters) throws Exception{
		String para1 = parameters;
		if (para1.startsWith("'") || para1.startsWith("\""))
			para1 = para1.substring(1, para1.length()-1);

		LocalTime date = LocalTime.now();
		float n = date.getHour() + date.getMinute() / 60;
		return mem.calculateRange(n,para1);		
	}
	@ThinkFunc(domain="estimate", name="time_range")
	public String hourRangeEstimate(MemoryWrapper mem,String parameters) throws Exception{
		return null;	
	}
	//判断当前的日期范围是否符合条件，条件和age的范围一样，单位是月
	@ThinkFunc(domain="evaluate", name="month_range")
	public ResultEnum monthRangeEvaluate(MemoryWrapper mem,String parameters) throws Exception{
		String para1 = parameters;
		if (para1.startsWith("'") || para1.startsWith("\""))
			para1 = para1.substring(1, para1.length()-1);

		LocalDate date = LocalDate.now();
		float n = date.getMonthValue() + date.getDayOfMonth();
		return mem.calculateRange(n,para1);		
	}
	@ThinkFunc(domain="estimate", name="month_range")
	public String monthRangeEstimate(MemoryWrapper mem,String parameters) throws Exception{
		return null;	
	}
	//判断用户说的话是否符合某种模式，需要将用户说的话放在dsm里面
	@ThinkFunc(domain="evaluate", name="user_say")
	public ResultEnum userSayPatternEvaluate(MemoryWrapper mem,String parameters) throws Exception{
		String pattern = parameters;
		String patternValue = "";
		if (pattern.startsWith("'") || pattern.startsWith("\""))
			pattern = pattern.substring(1, pattern.length()-1);
		if (pattern.contains(":")) {
			patternValue = pattern.substring(pattern.indexOf(":")+1);
			pattern = pattern.substring(0, pattern.indexOf(":"));
		} else {
			patternValue = pattern;
		}
		DSMData data1 = mem.getData("USER_SAY_CODE");
		DSMData data2 = mem.getData("USER_SAY_CONTENT");


		if (data1 != null) {
			if (data1.getValue().compareTo(pattern) == 0) {
				return ResultEnum.Positive;
			} 
		}
		if (data2 != null) {
			if (data2.getName().compareTo(patternValue) == 0) {
				return ResultEnum.Positive;
			} 
		}	
		if (data1!=null || data2!=null) {
			return ResultEnum.SystemDontKnow;
		}
		
		return ResultEnum.SystemDontKnow;
	}
	@ThinkFunc(domain="estimate", name="user_say")
	public String userSayPatternEstimate(MemoryWrapper mem,String parameters) throws Exception{
		return null;	
	}
	//判断系统问的话是否符合某种模式，需要将系统问的话放在dsm里面
	@ThinkFunc(domain="evaluate", name="ask")
	public ResultEnum askPatternEvaluate(MemoryWrapper mem,String parameters) throws Exception{
		String pattern = parameters;
		String patternValue = "";
		if (pattern.startsWith("'") || pattern.startsWith("\""))
			pattern = pattern.substring(1, pattern.length()-1);
		if (pattern.contains(":")) {
			patternValue = pattern.substring(pattern.indexOf(":")+1);
			pattern = pattern.substring(0, pattern.indexOf(":"));
		}
		
		DSMData data1 = mem.getData("Q_DIS_SYM");
		DSMData data2 = mem.getData("Q_SYM");
		
		if (data1 != null) {
			if (data1.getName().compareTo(pattern) == 0) {
				return ResultEnum.Positive;
			} 
		}
		if (data2 != null) {
			if (data2.getName().compareTo(pattern) == 0) {
				return ResultEnum.Positive;
			} 
		}	
		if (data1!=null || data2!=null) {
			return ResultEnum.Negative;
		}
		//TODO run regex match for user say
		//TODO fetch user input from DSM
		return ResultEnum.SystemDontKnow;
	}
	@ThinkFunc(domain="estimate", name="ask")
	public String askPatternEstimate(MemoryWrapper mem,String parameters) throws Exception{
		return null;	
	}
}
