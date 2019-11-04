package org.xy.thinking.rule;

import org.xy.model.KBRuleUnknownData;
import org.xy.model.ResultEnum;
import org.xy.thinking.mem.DSMData;
import org.xy.thinking.mem.MemoryWrapper;

public class ThinkingFunc extends ThinkingUnit {
	private String parameters ;
	private ThinkMethod methods = new ThinkMethod();
	public String getParameters() {
		return parameters;
	}
	public void setParameters(String parameters) {
		this.parameters = parameters;
	}
	public ThinkingFunc(String name) {
		super();
		setValue(name);
	}

	public String toString() {
		return getValue() + "(" + parameters+")";
	}
	

	@Override
	public KBRuleUnknownData estimate(MemoryWrapper mem,String prefix, int deepth) throws Exception {
		String s = null;
		
		s = methods.estimate(getValue(),mem,getParameters());
		
		if (s == null) return null;
		return new KBRuleUnknownData(deepth, s);
	}
	@Override
	public ResultEnum eval(MemoryWrapper mem,String prefix) throws Exception {
		DSMData data = mem.getData(getIdString());
		if (data != null) {
			//System.out.println("Hit "+getIdString());
			setResult(ResultEnum.parse(data.getFlag()));
			return getResult();
		} 
		setResult(methods.evaluate(getValue(), mem, getParameters()));
		mem.putTempData(getIdString(), "", "", getResult().toString());
		//System.out.println("put " + getIdString());
		return getResult();
	}
	@Override
	public void dumps(ThinkingUnit parent, String prefix) {
		debug (String.format("%s %s %s %s" ,prefix,"NODE",this, this.getResult()));
		debug (String.format("%s %s %s" ,prefix,"FUNC",this)	);
		debug (String.format("%s %s %s" ,prefix,"PARA",this.getParameters()));
	}
}
