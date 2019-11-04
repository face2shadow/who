package org.xy.thinking.machine;

import java.util.ArrayList;
import java.util.Collections;

public class ThinkingActionList extends ArrayList<ThinkingActionBasic>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 9147047233051388658L;
	public static final int DKD_FIELD_HEAD = 0;
	public static final int DKD_FIELD_CODE = 1;
	public static final int DKD_FIELD_NAME = 2;
	public static final int DKD_FIELD_VALUE = 3;
	public static final int DKD_FIELD_RULE = 4;
	
	public static final String DKD_HEADER_ENTER = "ENTER";
	public static final String DKD_HEADER_GO = "GO";
	public static final String DKD_HEADER_ACT = "ACT";

	public static final String DKD_PROP_RULE_DEFAULT = "true";

	public void sort() {
		Collections.sort(this);
	}
    public void addAction(int level, String type, String nodeCode, String actionCode, String name, String propertyValue) {
		if (type != null && actionCode != null && name != null) {
					
			ThinkingActionBasic basic = new ThinkingActionBasic();
			basic.setLevel(level);basic.setType(type); basic.setName(name); basic.setActionCode(actionCode);
			basic.setNodeCode(nodeCode);
			propertyValue = propertyValue + ",";
			String propName="" , propValue="";
			boolean valueMode = false; int skipCount = 0;
			if (propertyValue != null) {
				for (int i=0;i<propertyValue.length();i++) {
					char c = propertyValue.charAt(i);
					if (skipCount == 0 && c == ',') {
						valueMode = false;
						if (propName.length()>0) {
							basic.getProperties().put(propName,  propValue);
						}
						propName = "";
						propValue = "";
						continue;
					}
					if (skipCount == 0 && c == '\\') {
						skipCount = 2;
					}
					if (skipCount == 0 && c =='~') {
						valueMode = !valueMode;
						continue;
					}
					if (valueMode) {
						propValue += c;
					} else {
						propName += c;
					}
					if (skipCount > 0) skipCount --;
				}
				
			}
			this.add(basic);
			sort();
		}
    }
    
}
