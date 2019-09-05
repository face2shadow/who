package org.xy.thinking.mem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xy.model.KBBaseClass;
import org.xy.model.KBLine;
import org.xy.model.ResultEnum;
import org.xy.model.ThinkingResult;
import org.xy.model.ThinkingResultItem;
import org.xy.thinking.ThinkingDiagnosis;
import org.xy.thinking.def.KBFile;
import org.xy.thinking.def.KBLoader;
import org.xy.thinking.diagnosis.DiagnosisConstants;
import org.xy.thinking.diagnosis.SectionDisease;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryWrapper {
    private static final Logger log = LoggerFactory.getLogger(MemoryWrapper.class);
	private HashMap<String, DSMData> tmp = new HashMap<String, DSMData>();
	private HashMap<String, DSMData> data = new HashMap<String, DSMData>();	
	private Map<String, String> differentials;

	public boolean hasKeyStartsWith(String start) {
		for (String key: data.keySet()) {
			if (key.startsWith(start)) return true;
		}
		for (String key: tmp.keySet()) {
			if (key.startsWith(start)) return true;
		}
		return false;
	}
	public Map<String, String> getDifferentials() {
		if (differentials == null) differentials = new HashMap<>();
		return differentials;
	}
	public void setDifferentials(Map<String, String> differentials) {
		this.differentials = differentials;
	}

	public void reasoning(KBLine l, Boolean putInTemp) throws Exception {
		String key = l.get(1).toString();
		List<String> andRule = new ArrayList<String>();
		KBBaseClass and = l.get(2);
		if (and != null && and.toString().length() > 0) {
			for (KBBaseClass o : and.getFields()) {
				if (o.toString().length() > 0)
					andRule.add(o.toString());
			}
		}
		List<String> orRule = new ArrayList<String>();
		KBBaseClass or = l.get(3);
		if (or != null && or.toString().length() > 0) {
			for (KBBaseClass o : or.getFields()) {
				if (o.toString().length() > 0)
					orRule.add(o.toString());
			}
		}
		reasoning(key, andRule, orRule, putInTemp);
	}
	public void reasoning(String key, List<String> andRule, List<String> orRule, Boolean putInTemp) throws Exception {
		String k = key;
		if (key.startsWith("!")) {
			k = key.substring(1);
		}
		DSMData dataKey = getData(k);
		if (dataKey != null &&
				!(ResultEnum.isSystemDontKnow(dataKey.getResult()) || ResultEnum.isUserKeepSilence(dataKey.getResult()))) {
			//we already know the result
			if (ResultEnum.isUserDontKnow(dataKey.getResult())) {
				//pass
			} else {
				return;
			}
		}
		boolean v = false;
		if (andRule.size() > 0) {
			v = true;
			for (String cond : andRule) {
				String c = cond;
				if (cond.startsWith("!")) {
					c = cond.substring(1);
				}
				String parts[] = c.split("%");
				if (parts.length > 1) {
					DSMData data = getData(parts[0]);
					if (data != null) {
						if (parts[1].startsWith("RNG") || parts[1].startsWith("SIZ")) {
							String value = data.getValue();
							String expr = parts[1].substring(4);
							float n = convertStringToFloat(value);
							v = v && ResultEnum.isPositive(calculateRange(n, expr));
						}
					} else {
						v = false;
						return;
					}
				} else {
					DSMData data = getData(c);
					if (data != null) {
						if (cond.startsWith("!") && ResultEnum.isNegative(data.getResult())) {
							v = v && ResultEnum.isNegative(data.getResult());
						} else if (ResultEnum.isSystemDontKnow(data.getResult())) {
							v = false;
							return;
						} else {
							v = v && ResultEnum.isPositive(data.getResult());
						}
					} else {
						v = false;
						return;
					}
				}
			}
		} else if (orRule.size() > 0) {
			v = false;
			String c = null;
			for (String cond : orRule) {
				c = cond;
				if (cond.startsWith("!")) {
					c = cond.substring(1);
				}
				DSMData data = getData(c);
				if (data != null && ResultEnum.isPositive(data.getResult())) {
					v = v || ResultEnum.isPositive(data.getResult());
				}
			}
		}
		if (v == false) return;

		if (key.startsWith("!")) {
			v = !v;
			key = key.substring(1);
		}
		if (putInTemp) {
			putTempData("_" + key, "", "", v ? "+" : "-");
		} else {
			putData(key, "", "", v ? "+" : "-");
		}
	}
	public void clearTemp() {
		tmp.clear();
	}
	public void deleteData(String key) {
		if (data.containsKey(key)) {
			data.remove(key);
		}
		if (tmp.containsKey(key)) {
			tmp.remove(key);
		}
	}
	public void putTempData(String key, String name, String value, String flag) {
		DSMData data = new DSMData();
		data.setFlag(flag);
		data.setKey(key);
		data.setName(name);
		data.setValue(value);
		putData(data, 1);
	}
	public void putData(String key, String name, String value, String flag) {
		DSMData data = new DSMData();
		data.setFlag(flag);
		data.setKey(key);
		data.setName(name);
		data.setValue(value);
		putData(data, 0);
	}
	public void putData(DSMData para, int target) {
		if (data.containsKey(para.getKey())) { 
			DSMData d = getData(para.getKey());
			d.setValue(para.getValue());
			d.setFlag(para.getFlag());
		} else {
			if (target == 0)
				data.put(para.getKey(), para);
			else
				tmp.put(para.getKey(), para);
		}		
	}
	public DSMData getData(String key) {
		if (tmp.containsKey(key)) return tmp.get(key);
		if (data.containsKey(key)) return data.get(key);
		return null;
	}
	public HashMap<String, DSMData> findData(String prefix) {
		HashMap<String, DSMData> temp = new HashMap<String, DSMData>();
		tmp.forEach((k, v) -> {
			if (k.startsWith(prefix))
				temp.put(k, v);
		});
		data.forEach((k, v) -> {
			if (k.startsWith(prefix))
				temp.put(k, v);
		});
		return temp.size() == 0 ? null : temp;
	}
	public float convertStringToFloat(String number) {
		float n = 0.0f;
		try {			
			n = Integer.parseInt(number);			
		} catch (NumberFormatException exp) {
			return n;
		}
		return n;
	}
	public float convertAgeToMonth(String age) {
		float n = 0.0f;
		if (age == null || age.length() == 0) return n;
		try {
			String unit = age.substring(age.length()-1);
			if (unit.length()==0 || (unit.compareTo("m")!=0 && unit.compareTo("d")!=0 && unit.compareTo("y")!=0) ) {
				unit = "m";
				age = age + unit;
			} 
			if (unit.compareTo("m") == 0) {
				age = age.substring(0,  age.length()-1);
				n = Integer.parseInt(age);
			}
			if (unit.compareToIgnoreCase("y")==0) {
				age = age.substring(0,  age.length()-1);
				n = Integer.parseInt(age) * 12;
			} else if (unit.compareToIgnoreCase("d")==0){
				age = age.substring(0,  age.length()-1);
				n = Integer.parseInt(age) / 30;
			}
		} catch (NumberFormatException exp) {
			return n;
		}
		return n;
	}
	public ResultEnum calculateRange(float n, String expression) {		
		String parts[] = expression.split("-");
		boolean r1=false,r2=false;
		for (String p: parts) {
			if (p.startsWith("[")) {
				float d1 = convertAgeToMonth(p.substring(1));
				if (n >= d1 || d1 == 0.0f) {
					r1 = true;
				}
			}
			if (p.startsWith("(")) {
				float d1 = convertAgeToMonth(p.substring(1));
				if (n > d1 || d1 == 0.0f) {
					r1 = true;
				}
			}
			if (p.endsWith("]")) {
				float d1 = convertAgeToMonth(p.substring(0,p.length()-1));

				if (n <= d1 || d1 == 0.0f) {
					r2 = true;
				}
			}
			if (p.endsWith(")")) {
				float d1 = convertAgeToMonth(p.substring(0,p.length()-1));
				if (n < d1 || d1 == 0.0f) {
					r2 = true;
				}
			}
		}
		return r1 && r2?ResultEnum.Positive:ResultEnum.Negative;
	}
	public ResultEnum calculateAge(DSMData age, String expression) {
		if (age == null || age.getValue().length()==0) return ResultEnum.SystemDontKnow;		
		String s = age.getValue();
		float n = convertAgeToMonth(s);
		if (n == 0) return ResultEnum.SystemDontKnow;
		return calculateRange(n, expression);
	}
	
	public void loadGeneralKnowledge() throws Exception {
		for (String key: KBLoader.getDefinitions().keySet()) {
			if (key.startsWith("GKB")) {
				List<KBLine> lines = KBLoader.getDefinitions().get(key, null).getLines("EXP");		
				for (KBLine l : lines) {
					reasoning(l, false);
				}
			}
		}
	}
	public void dumpTemp() {
		for (String key: tmp.keySet()) {
			log.debug(String.format("%-4s\t%-12s\t%-10s\t%-2s", key, tmp.get(key).getName(), tmp.get(key).getValue(), tmp.get(key).getFlag()));
		}
	}
	public void dumpDSM() {
		for (String key: data.keySet()) {
			log.debug(String.format("%-4s\t%-12s\t%-10s\t%-2s", key, data.get(key).getName(), data.get(key).getValue(), data.get(key).getFlag()));
		}
	}
	public boolean isCodeInExcludedList(ThinkingResult result, String e_code) {
		ThinkingResultItem item = result.getItem(ThinkingResult.MEMORY, "EXCLUDED");
		if (item == null) return false;
		String parts[] = item.getValue().split("\\,");
		if (parts == null || parts.length==0) return false;
		for (String s: parts) {
			if (s.trim().compareToIgnoreCase(e_code)==0)
				return true;
		}
		return false;		
	}
//	public void addToExcludedList(DKDDiagnosisResult dr, String e_code, String e_name) {
//		dr.add(EnumResult.SystemExcluded,e_code, e_name, "");
//		if (dr.getPositives().containsKey(e_code))
//			dr.getPositives().remove(e_code);
//	}
}
