package org.xy.thinking.machine;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.xy.model.KBLine;
import org.xy.model.KBSection;
import org.xy.thinking.diagnosis.SectionUtils;



public class GraphEvaluate {
	private KBSection section = null;
	protected HashMap<String, KBLine> lineMap = new HashMap<String, KBLine>();

	public GraphEvaluate( KBSection section) {
		this.section = section;
		buildLineMap();
	}
	public KBLine getLine(String id) {
		if (lineMap.containsKey(id)) return lineMap.get(id);
		return null;
	}

	private void buildLineMap() {
		for (KBLine line : section.getLines()) {
			String type = line.get(0).toString();
			if (type!=null && (type.compareToIgnoreCase("FEA") == 0 || type.compareToIgnoreCase("EXM") == 0 || type.compareToIgnoreCase("HIS") == 0)){
				String id = line.get(1).toString();
				if (id !=null && id.length() > 0) lineMap.put(id, line);
			}
		}
	}

	public List<KBLine> getConcludeRules(){
		HashSet<String> tags = new HashSet<String>();
		tags.add("DI0");tags.add("DI1");tags.add("DI2");tags.add("DI3");tags.add("DI4");
		tags.add("DI5");tags.add("DI6");tags.add("DI7");tags.add("DI8");tags.add("DI9");
		List<KBLine> lines = SectionUtils.getRules(section,tags);
		return lines;
	}
	public List<KBLine> getExcludeRules(){
		HashSet<String> tags = new HashSet<String>();
		tags.add("EXC");
		List<KBLine> lines = SectionUtils.getRules(section,tags);
		return lines;
	}
	public List<KBLine> getActionRules(){
		HashSet<String> tags = new HashSet<String>();
		tags.add("ACT");
		tags.add("TLK");
		tags.add("DSM");
		tags.add("ALERT");
		tags.add("ASK");
		List<KBLine> lines = SectionUtils.getRules(section,tags);
		return lines;
	}
}
