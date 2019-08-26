package org.xy.thinking.diagnosis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.xy.model.ResultEnum;
import org.xy.model.KBBaseClass;
import org.xy.model.KBLine;
import org.xy.model.KBLineField;
import org.xy.model.KBRuleUnknownData;
import org.xy.model.KBSection;
import org.xy.thinking.mem.MemoryWrapper;
import org.xy.thinking.rule.ThinkingRule;

public abstract class SectionUtils {

	public static List<KBLine> getRules(KBSection section, HashSet<String> tags) {
		List<KBLine> lines = new ArrayList<KBLine>();
		for (KBBaseClass b : section.getLines()) {
			if (b instanceof KBLine) {
				KBLine l = (KBLine)b;
				if (tags.contains(l.get(0).toString())) {
					lines.add(l);
				}
			}
		}
		return lines;
	}
	public static String getFieldText(KBLine l, int pos) {
		if (l.get(pos) == null) return null;
		return l.get(pos).toString();
	}
	public static String getProperty(KBLine l, int pos, String propName){
		if (l.get(pos) == null) return null;
		KBBaseClass t = l.get(pos);
		if (t instanceof KBLineField) {
			KBLineField f = (KBLineField)t;
			return f.getNamed(propName);
		}
	
		return null;
	}

	public static void refreshExps(MemoryWrapper mem, KBSection section) throws Exception {

		for (KBLine l : section.getLines("EXP")) {
			mem.reasoning(l,true);
		}
	}
	public static void refreshFeatureData(MemoryWrapper mem, KBSection section) throws Exception {
		for (KBLine l: section.getLines("FEA")) {
			String key = l.get(1).toString();
			String target = l.get(2).toString();
			String expr = null;
			if (l.get(2).get(0).getType().compareToIgnoreCase("node")==0) {
				String tp = l.get(2).get(0).get(0).toString();
				if (tp.compareToIgnoreCase("AGE") == 0) {		
					expr = l.get(2).get(0).get(1).toString();
				}
			}
			MemoryWrapper.DSMData data = mem.getData(target);
			if (data == null) { //support both key and text name
				data = mem.getData(key);
				if (expr != null) {
					mem.putTempData(target,  
							"", 
							"",
							mem.calculateAge(mem.getData(DiagnosisConstants.AGE_M_CODE), expr).toString());
					data = mem.getData(target);
					
				}
			}
			if (data != null) {
				mem.putTempData("_"+key, data.getName(), data.getValue(), data.getFlag());
			}
		}
	
	}
	public static List<String> evaluateRules(MemoryWrapper mem, List<KBLine> lines, int rulePos, String ruleTag, String returnTag) throws Exception {
		List<String> codes = new ArrayList<String>();
		for (KBLine line : lines) {
			if (line.count() < rulePos)
				continue;
			String rule = line.get(rulePos).getNamed(ruleTag);
			String e_code = line.get(2).getNamed(returnTag);
			if (rule == null)
				continue;
			ThinkingRule expr = new ThinkingRule();
			expr.parse(rule);
			ResultEnum result = evaluteSingleRule(mem, line, rulePos, ruleTag);
			if (ResultEnum.isPositive(result) ) {
				codes.add(e_code);
			}
		}
		return codes;
	}
	public static ResultEnum evaluteSingleRule(MemoryWrapper mem, KBLine line, int rulePos, String ruleTag) throws Exception {
		
		if (line.count() < rulePos)
			return ResultEnum.Negative;
		String rule = line.get(rulePos).getNamed(ruleTag);
		if (rule == null)
			return ResultEnum.Negative;
		ThinkingRule expr = new ThinkingRule();
		expr.parse(rule);
		ResultEnum result = expr.eval(mem);
		return result;
		
	}
	public static KBRuleUnknownData estimateSingleRule(MemoryWrapper mem, KBLine line, int rulePos, String ruleTag) throws Exception {
		
		if (line.count() < rulePos)
			return null;
		String rule = line.get(rulePos).getNamed(ruleTag);
		if (rule == null)
			return null;
		ThinkingRule expr = new ThinkingRule();
		expr.parse(rule);
		return expr.estimate(mem, "", 0);
		
		
	}
	public static void copyLinesTo(KBSection src, KBSection dest, String tagName) {
		HashMap<String, KBLine> destMap = new HashMap<String, KBLine>();
		for (KBLine line: dest.getLines()) {

			if (line.get(0).toString().compareTo(tagName)==0) {
				if (line.get(1) != null) {
					destMap.put(line.get(1).toString(), line);
				}
			} 
		}
		for (KBLine line: src.getLines()) {
			if (line.get(0).toString().compareTo(tagName)==0) {
				if (line.get(1) != null) {
					if (destMap.containsKey(line.get(1).toString())) {
						continue;
					} else {
						dest.getLines().add(line);
					}
				}
			}
		}
	}
}
