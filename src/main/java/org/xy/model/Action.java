package org.xy.model;

public class Action extends ThinkingResultItem{
	public static final int NA = 0;
	public static final int GO = 1;
	public static final int ACT = 2;
	
	private ResultEnum evaluateResult = ResultEnum.SystemDontKnow;
	
/*	
	private String id;
	private String content;
	boolean processed = false;
	
	public boolean isProcessed() {
		return processed;
	}
	public void setProcessed(boolean processed) {
		this.processed = processed;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getHead() {
		String[] parts = content.split("\\^");
		if (parts.length > 0) 
			return parts[0];
		return content;
	}
	public String getValue() {		
		String[] parts = content.split("\\^");
		if (parts.length > 1) {
			String rest = "";
			for (int i=1;i<parts.length;i++) {
				rest = rest + parts[i] + "^";
			}
			if (rest.endsWith("^")) {
				return rest.substring(0, rest.length()-1);
			}
			return rest;
		} 
		return parts[0];
	}
	public int getActionType() {
		if (content == null) return NA;
		String[] parts = content.split("\\^");
		if (parts[0].compareToIgnoreCase("SAY") ==0 || parts[0].compareToIgnoreCase("ASK") == 0) {
			return ACT;
		}
		if (parts[0].compareToIgnoreCase("MEM") ==0 ) {
			return ACT;
		}
		if (parts[0].compareToIgnoreCase("ACT") ==0 ) {
			return ACT;
		}
		return NA;
	}
*/

	public ResultEnum getEvaluateResult() {
		return evaluateResult;
	}

	public void setEvaluateResult(ResultEnum evaluateResult) {
		this.evaluateResult = evaluateResult;
	}


	@Override
	public String toString() {
		String s = String.format("category=%d, code=%s, name=%s, value=%s",  
				getCategory(), getCode(), getName(), getValue());
		return s;
	}
}
