package org.xy.thinking.diagnosis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.xy.model.KBLine;
import org.xy.model.KBSection;
import org.xy.thinking.mem.MemoryWrapper;

public class SectionPatientSurvey {

	private KBSection section = null;
	protected HashMap<String, KBLine> lineMap = new HashMap<String, KBLine>();
	
	public SectionPatientSurvey(KBSection section) {
		this.section = section;
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
}
