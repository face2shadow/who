package org.xy.model;

public class KBLineField extends KBBaseClass {
	public KBLineField(KBDelimeters hdr, String text) {
		super(hdr, text);
		parse();
	}
	private void parse() {
		String parts[] = getContent().split(getDelimeters().C2);
		for (String s:parts) {
			KBFieldComponent obj = new KBFieldComponent(getDelimeters(), s);
			getFields().add(obj);
		}
	}

}
