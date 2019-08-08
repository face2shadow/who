package org.xy.thinking.def;

import java.util.HashMap;
import java.util.Set;

import org.xy.model.KBSection;

public class KBDefinitionMap{
	private HashMap<String, KBSection> sectionsMap = new HashMap<String, KBSection> ();
	private static final long serialVersionUID = 1L;

	public KBSection get(String type, String code) {
		if (code == null) return sectionsMap.get(type);
		return sectionsMap.get(type+"|"+code);
	}
	public KBSection put(String type, String code, KBSection section) {
		return sectionsMap.put(type+"|"+code, section);
	}
	public boolean containsKey(String type, String code) {
		return sectionsMap.containsKey(type+"|"+code);
	}
	public Set<String> keySet(){
		return sectionsMap.keySet();
	}
//	@Override
//	public boolean containsKey(Object key) {
//        if (super.containsKey(key)) {
//        	DKDSection f = get(key);
//        	long ts = f.getLastUpdate();
//        	if ((System.currentTimeMillis() - ts) > 7200000L){
//        		remove(key);
//        		return false;
//        	}
//        	return true;
//        } else {
//        	return super.containsKey(key);
//        }
//    }
}
