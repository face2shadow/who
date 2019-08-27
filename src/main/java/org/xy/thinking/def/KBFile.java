package org.xy.thinking.def;

import java.util.ArrayList;
import java.util.List;

import org.xy.model.KBSection;
import org.xy.model.KBSectionDefinition;


public class KBFile {

	private List<KBSection> sections = new ArrayList<KBSection>();

	private long lastUpdate = 0;
	private long timeStamp;
	public long getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(long lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
	public List<KBSection> getSections(){
		return sections;
	}

	public void parse(String content) {
		String lines[] = content.split("\n");
		String flag = "";
		KBSectionDefinition def = null;
		KBSection section = null;
		for (String s : lines) {
			s = s.trim();
			if (s.startsWith("DKD")) {
				if (section != null) {
					sections.add(section);
				}
				section = null;
				def = new KBSectionDefinition(s);
				if (def.get(2) == null) {
					//the file type does not provided
					return;
				}
				section = new KBSection();
				section.setFileType(def.get(2).toString());
				section.setDefinition(def);
			} else {
				if (s.length() > 0 && s.startsWith("#") == false && def != null && section != null) {
					section.parseLine(s);
				}
			}
		}
		if (section != null) {
			sections.add(section);
		}
		section = null;
	}


}
