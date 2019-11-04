package org.xy.thinking.rule;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xy.model.ResultEnum;
import org.xy.thinking.diagnosis.SectionUtils;
import org.xy.thinking.mem.DSMConstants;
import org.xy.thinking.mem.DSMData;
import org.xy.thinking.mem.MemoryWrapper;



public class ThinkMethod {

	private static final Logger log = LoggerFactory.getLogger(ThinkMethod.class);
	
	public String estimate(String name, MemoryWrapper mem,String parameters) throws Exception {
		for (Method m : this.getClass().getMethods()) {
			if (m.isAnnotationPresent(ThinkFunc.class)) {
				ThinkFunc tf = m.getAnnotation(ThinkFunc.class);
				if ("estimate".equals(tf.domain()) && tf.name().equals(name)) {
					return (String)m.invoke(this, new Object[] {mem,  parameters});
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

				if ("evaluate".equals(tf.domain()) && tf.name().equals(name)) {
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

	//判断dsm中是否存在指定的code，code对应的dsm值存在，不管flag是多少都返回positive
	//code
	//scope, code
	@ThinkFunc(domain="evaluate", name="exists")
	public ResultEnum dsmExist(MemoryWrapper mem,String parameters) throws Exception{
		List<String> paras = SectionUtils.retrieveParameters(parameters);
		String code = null;
		if (paras.size() == 1) {
			code = paras.get(0);
		}
		if (paras.size() == 2) {
			code = paras.get(0) +"/" + paras.get(1);
		}
		if (code != null) {
			DSMData data = mem.getData(code);
			if (data != null) return ResultEnum.Positive;
		}
		return ResultEnum.Negative;
	}
	
	//code, prop
	//scope, code, prop
//	@ThinkFunc(domain="evaluate", name="prop_exists")
//	public ResultEnum dsmPropExist(MemoryWrapper mem,String parameters) throws Exception{
//		List<String> paras = SectionUtils.retrieveParameters(parameters);
//		String code = null;
//		if (paras.size() == 2) {
//			code = paras.get(0) +"@" + paras.get(1);
//		}
//		if (paras.size() == 3) {
//			code = paras.get(0) +"/" + paras.get(1) +"@" + paras.get(2);			
//		}
//		if (code != null) {
//			MemoryWrapper.DSMData data = mem.getData(code);
//			if (data != null) return ResultEnum.Positive;
//		}
//		return ResultEnum.Negative;
//	}

	//code, flag
	//scope, code, flag
	@ThinkFunc(domain="evaluate", name="flag")
	public ResultEnum dsmFlag(MemoryWrapper mem,String parameters) throws Exception{
		List<String> paras = SectionUtils.retrieveParameters(parameters);
		String code = null;
		String flag = null;
		if (paras.size() == 2) {
			code = paras.get(0);
			flag = paras.get(1);
		}
		if (paras.size() == 3) {
			code = paras.get(0) +"/" + paras.get(1);
			flag = paras.get(2);
		}
		if (code != null && flag != null) {
			DSMData data = mem.getData(code);
			if (data != null) {
				if (flag.equals(data.getFlag())) return ResultEnum.Positive;
			}
		}
		return ResultEnum.Negative;
	}

	//code, prop, flag
	//scope, code, prop, flag
	@ThinkFunc(domain="evaluate", name="prop_flag")
	public ResultEnum dsmPropFlag(MemoryWrapper mem,String parameters) throws Exception{
		List<String> paras = SectionUtils.retrieveParameters(parameters);
		String code = null;
		String flag = null;
		if (paras.size() == 3) {
			code = paras.get(0) + "@" + paras.get(1);
			flag = paras.get(2);
		}
		if (paras.size() == 4) {
			code = paras.get(0) +"/" + paras.get(1) + "@" + paras.get(2);
			flag = paras.get(3);
		}
		if (code != null && flag != null) {
			DSMData data = mem.getData(code);
			if (data != null) {
				if (flag.equals(data.getFlag())) return ResultEnum.Positive;
			}
		}
		return ResultEnum.Negative;
	}
	
	//code, value
	//scope, code, value
	@ThinkFunc(domain="evaluate", name="equals")
	public ResultEnum equalsEvaluate(MemoryWrapper mem,String parameters) throws Exception{		
		List<String> paras = SectionUtils.retrieveParameters(parameters);
		String code = null; 
		String value = null;
		if (paras.size()==2) {
			code = paras.get(0);
			value = paras.get(1);
		}
		if (paras.size()==3) {
			code = paras.get(0) +"/" + paras.get(1);
			value = paras.get(2);
		}
		if (code != null && value != null) {
			DSMData data = mem.getData(code);
			if (data != null) {
				String value4compare = data.getValue();
				if (value4compare == null && "null".equals(value)) {
					return ResultEnum.Positive;
				}
				if (value4compare != null) {
					if (value4compare.equals(value)) {
						return ResultEnum.Positive;
					}
				}
			}
		}
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
				//$$$$List<String> words = SplitWord.ikCutWord(data1.getValue());
				//$$$$mem.putTempData("USER_SAY_TMP", "WORDS", SplitWord.list2string(words.toArray(), 5), "+");
				//$$$$data2 = mem.getData("USER_SAY_TMP");
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
					//$$$$ mem.putData("USER_CONTEXT", "WORDS", SplitWord.list2string(intersectWords, 10), "+");
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
							//$$$$mem.putData("USER_CONTEXT", "WORDS", SplitWord.list2string(intersectWords, 10), "+");
						}
						log.debug("SET CONTEXT: "+mem.getData("USER_CONTEXT").getValue());
						return ResultEnum.Positive;
					}
				}
			}
		}
		
		return ResultEnum.Negative;
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
			if (data1.getName().equals(pattern)) {
				return ResultEnum.Positive;
			} 
		}
		if (data2 != null) {
			if (data2.getName().equals(pattern)) {
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
	
	@ThinkFunc(domain="evaluate", name="topic")
	public ResultEnum topicEvaluate(MemoryWrapper mem,String parameters) throws Exception{
		String pattern = parameters;
		String patternValue = "";
		if (pattern.startsWith("'") || pattern.startsWith("\""))
			pattern = pattern.substring(1, pattern.length()-1);
		if (pattern.contains(":")) {
			patternValue = pattern.substring(pattern.indexOf(":")+1);
			pattern = pattern.substring(0, pattern.indexOf(":"));
		}
		
		DSMData data1 = mem.getData(DSMConstants.DSM_TOPIC_PATH);
		//DSMData data2 = mem.getData(ThinkingGraph.DSM_TOPIC_FINISHED);
		
		//if (data2 != null) {
		//	String topicList = data2.getValue();
		//	String[] parts = topicList.split("\\,");
		//	for (String t: parts) {
		//		if (t.compareTo(pattern)==0) {
		//			return ResultEnum.Negative;
		//		}
		//	}
		//}	
		if (data1 != null) {
			if (data1.getValue().equals(pattern)) {
				return ResultEnum.Positive;
			} 
		}
		return ResultEnum.SystemDontKnow;
	}
	

	@ThinkFunc(domain="evaluate", name="count_match")
	public ResultEnum countMatchEvaluate(MemoryWrapper mem,String parameters) throws Exception {
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
		for (String w : wait_4_count) {
			String key = w;
			if (key.startsWith("!")) {
				key = key.substring(1, key.length());
			}

			DSMData data = mem.getData("_"+key) == null ? mem.getData(key) : mem.getData("_"+key);

			if (data != null) {
				count = count + 1;
			}
		}
		if (count >= Integer.parseInt(para2)) {
			return ResultEnum.Positive;
		}
		return ResultEnum.SystemDontKnow;
	}
	
	@ThinkFunc(domain="estimate", name="count_match")
	public String countMatchEstimate(MemoryWrapper mem,String parameters) throws Exception {
		String parts[] = parameters.split(":");
		if (parts.length < 2) {
			return null;
		}
		String para1 = parts[0];
		String para2 = parts[1];
		if (para1.startsWith("'") || para1.startsWith("\""))
			para1 = para1.substring(1, para1.length()-1);
		String wait_4_count[] = para1.split("-");
		for (String w : wait_4_count) {
			String key = w;
			if (key.startsWith("!")) {
				key = key.substring(1, key.length());
			}

			DSMData data = mem.getData("_"+key) == null ? mem.getData(key) : mem.getData("_"+key);
			if (data == null) return w;

			if (ResultEnum.isSystemDontKnow(data.getResult())) return w;

		}
		return null;
	}
}