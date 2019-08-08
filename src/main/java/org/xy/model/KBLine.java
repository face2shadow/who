package org.xy.model;

public class KBLine extends KBBaseClass {
	public KBLine(KBDelimeters hdr, String content) {
		super(hdr, content);
		parse();
	}
	private void parse() {
		String parts[] = getContent().split(getDelimeters().C1);
		for (String s:parts) {
			KBLineField obj = new KBLineField(getDelimeters(), s);
			getFields().add(obj);
		}
	}
}
