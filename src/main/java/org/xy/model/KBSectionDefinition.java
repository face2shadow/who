package org.xy.model;

public class KBSectionDefinition extends KBBaseClass {
	private KBDelimeters header = new KBDelimeters();
	public KBSectionDefinition(String text) {
		setContent(text);
		parse();
	}
	private void parse() {
		String parts[] = getContent().split(header.C1);
		for (String s: parts) {
			getFields().add(new KBBaseClass(s));
		}
	}
}
