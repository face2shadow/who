package org.xy.model;

public class KBFieldComponent extends KBBaseClass {
	
	public KBFieldComponent(KBDelimeters hdr, String text) {
		super(hdr, text);		
		parse();
	}
	private void parse() {
		String delimeter = getDelimeters().C3;
		setType("node");
		if (getContent().indexOf(getDelimeters().C4)>=0) {
			delimeter = getDelimeters().C4;
			setType("prop");
		}
		String parts[] = getContent().split(delimeter);
		for (String s: parts) {
			getFields().add(new KBBaseClass(s));
		}
	}
}
