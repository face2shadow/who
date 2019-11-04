package org.xy.thinking.rule;

import org.xy.model.KBRuleUnknownData;
import org.xy.model.ResultEnum;
import org.xy.thinking.mem.DSMData;
import org.xy.thinking.mem.MemoryWrapper;

public class ThinkingFact extends ThinkingUnit {
	public ThinkingFact() {
		super();
	}
	
	public String toString() {
		return "["+getValue()+"="+getResult()+"]";
	}
	
	public void dumps(ThinkingUnit parent, String prefix) {
		debug (String.format("%s %s %s" ,prefix,"VALUE", getValue()));
	}
	
	public ResultEnum eval(MemoryWrapper mem, String prefix) throws Exception {
		if (getValue().compareToIgnoreCase("true") == 0) {
			setResult(ResultEnum.Positive);
			return getResult();
		}
		String key = getValue();
		
		boolean reverse = false;
		if ("!".equals(key.substring(0, 1))) {
			reverse = true;
			key = key.substring(1, key.length());
		}
		DSMData data = mem.getData(prefix + key) == null ? mem.getData(key) : mem.getData(prefix + key);
		if (data == null){
			return ResultEnum.SystemDontKnow;
		}

		setKnownFacts(1);
		
		setResult(ResultEnum.parse(data.getFlag()));
		if (reverse && getResult()==ResultEnum.Positive) {
			setResult(ResultEnum.Negative);
		} else if (reverse && getResult()==ResultEnum.Negative) {
			setResult(ResultEnum.Positive);
		}
		return getResult();
	}
	
	@Override
	public KBRuleUnknownData estimate(MemoryWrapper mem, String prefix, int deepth) {
		if (ResultEnum.isSystemDontKnow(getResult()) ||  ResultEnum.isUserDontKnow(getResult())) {
			return new KBRuleUnknownData(deepth, getValue());
		}
		return null;
	}
}
