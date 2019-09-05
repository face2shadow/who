package org.xy.thinking.rule;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xy.model.ResultEnum;
import org.xy.thinking.ThinkingBrain;
import org.xy.thinking.mem.MemoryWrapper;
import org.xy.thinking.mem.DSMData;
import org.xy.utils.SplitWord;


public class ThinkMethod {

	private static final Logger log = LoggerFactory.getLogger(ThinkMethod.class);
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

			DSMData data = mem.getData("_"+key) == null ? mem.getData(key) : mem.getData("_"+key);

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

			DSMData data = mem.getData("_"+key) == null ? mem.getData(key) : mem.getData("_"+key);
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
		DSMData data = mem.getData(para1);
		if (data == null || ResultEnum.isSystemDontKnow(data.getResult())) return para1;
		return null;
	}
	//判断dsm中是否存在指定的code，code对应的dsm值存在，不管flag是多少都返回positive
	@ThinkFunc(domain="evaluate", name="exists")
	public ResultEnum dsmExist(MemoryWrapper mem,String parameters) throws Exception{
		String para1 = parameters;
		if (para1.startsWith("'") || para1.startsWith("\""))
			para1 = para1.substring(1, para1.length()-1);

		DSMData data = mem.getData(para1);
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

    public boolean same (String[] arr1, String[] arr2){
    	if (arr1.length != arr2.length) return false;
        List<String> l = new LinkedList<String>();
                         
        for(String str:arr1){
            if(!l.contains(str)){
                l.add(str);
            }
        }
        boolean s = true;
        for(String str:arr2){
            if(!l.contains(str)){
                s = false;
                break;
            }
        }
        return s;
    }
    public String[] union (String[] arr1, String[] arr2){
        Set<String> hs = new HashSet<String>();
        for(String str:arr1){
            hs.add(str);
        }
        for(String str:arr2){
            hs.add(str);
        }
        String[] result={};
        return hs.toArray(result);
    }
	public String[] intersect(String[] arr1, String[] arr2){
        List<String> l = new LinkedList<String>();
        Set<String> common = new HashSet<String>();                  
        for(String str:arr1){
            if(!l.contains(str)){
                l.add(str);
            }
        }
        for(String str:arr2){
            if(l.contains(str)){
                common.add(str);
            }
        }
        String[] result={};
        return common.toArray(result);
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
		DSMData data1 = mem.getData("USER_SAY");

		if (data1 != null) {
			DSMData data2 = mem.getData("USER_SAY_TMP");
			if (data2 == null) {
				List<String> words = SplitWord.ikCutWord(data1.getValue());
				mem.putTempData("USER_SAY_TMP", "WORDS", SplitWord.list2string(words.toArray(), 5), "+");
				data2 = mem.getData("USER_SAY_TMP");
			}
			
			if (data2 != null) {
				String user_input = data2.getValue();
				DSMData data3 = mem.getData("USER_CONTEXT");
				String[] contextWords =  {};
				if (data3 != null) {
					contextWords = data3.getValue().split(" ") ;
				}
				String[] patternWords = pattern.split(" ");
				String[] userWords = user_input.split(" ");
				String[] intersectWords = intersect(patternWords, userWords);
				String[] intersectContextWords = intersect(contextWords, userWords);
				if (intersectWords.length == 0 && intersectContextWords.length == 0) {
					//mem.putData("USER_CONTEXT", "WORDS", "", "+");
					return ResultEnum.Negative;
				}
				if (intersectWords.length == patternWords.length) {
					if (same(intersectWords, contextWords)) {
						//mem.putData("USER_CONTEXT", "WORDS", "", "+");
						return ResultEnum.Negative;
					}
					mem.putData("USER_CONTEXT", "WORDS", SplitWord.list2string(intersectWords, 10), "+");
					return ResultEnum.Positive;
				} else {
					userWords = union(contextWords, userWords);
					intersectWords = intersect(patternWords, userWords);
					if (same(intersectWords, contextWords)) {
						//mem.putData("USER_CONTEXT", "WORDS", "", "+");
						return ResultEnum.Negative;
					}
					if (intersectWords.length == patternWords.length) {
						if (intersectWords.length > contextWords.length) {
							mem.putData("USER_CONTEXT", "WORDS", SplitWord.list2string(intersectWords, 10), "+");
						}
						log.debug("SET CONTEXT: "+mem.getData("USER_CONTEXT").getValue());
						return ResultEnum.Positive;
					}
				}
			}
		}
		
		return ResultEnum.Negative;
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
	@ThinkFunc(domain="evaluate", name="ask")
	public String askPatternEstimate(MemoryWrapper mem,String parameters) throws Exception{
		return null;
	}
	
}
