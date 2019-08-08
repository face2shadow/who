package org.xy.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class KBBaseClass {
	private String content ="";
	private String type = "text";
	private KBDelimeters header = new KBDelimeters();
	public KBDelimeters getDelimeters() {
		return header;
	}
	public boolean compare(int n, String t) {
		String s = get(n).toString();
		if (s != null) {
			return s.compareTo(t) == 0;
		}
		return false;
	}
	public void setDelimeters(KBDelimeters header) {
		this.header = header;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	private List<KBBaseClass> fields = new ArrayList<KBBaseClass>();
	public KBBaseClass(String text) {
		content = text;
	}	
	public KBBaseClass(KBDelimeters hdr, String text) {
		content = text;
		header = hdr;
	}
	public KBBaseClass() {
		
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public List<KBBaseClass> getFields() {
		return fields;
	}
	public void setFields(List<KBBaseClass> fields) {
		this.fields = fields;
	}
	public KBBaseClass get(int i) {
		if (fields.size() > i) {
			return fields.get(i);
		}
		return null;
	}
	public String getNamed(String name) {
		for (KBBaseClass b: getFields()) {
			if (b.getFields().size()>1) {
				if (b.getFields().get(0).toString().trim().compareTo(name) == 0)
					return b.getFields().get(1).toString().trim();
			}
		}
		return null;
	}
	public int count() {
		return fields.size();
	}
	public String toString() {
		return content;
	}
//	public DKDLine getLine(String id) {
//		return null;
//	}
//	public DKDLine getLineByCode(String code) {
//		for (DKDBasic b : getFields()) {
//			if (b instanceof DKDLine) {
//				DKDLine l = (DKDLine)b;
//				if (l.get(1).toString().compareTo(code)==0) {
//					return l;
//				}
//			}
//		}
//		return null;
//	}
	public void addLine(KBLine line) {
		getFields().add(line);
	}
	public String getDiseaseName(){
		return null;
	}
	public String getDiseaseCode(){
		return null;
	}
	public String getDiseaseProp(String prop){
		return null;
	}
	public String getTalkCodes(String code) {
		for (KBBaseClass b : this.getFields()) {
			if (b instanceof KBLine) {
				KBLine l = (KBLine)b;
				if (l.get(0).toString().compareTo("TLK") == 0) {
					if (l.get(1).toString().compareTo(code) == 0) {
						return l.get(2).toString();
					}
				}
			}
		}
		return code;
	}
	public String getTalks(){
		List<KBLine> lines = new ArrayList<KBLine>();
		for (KBBaseClass b : getFields()) {
			if (b instanceof KBLine) {
				KBLine l = (KBLine)b;
				if(l.get(0).toString().compareTo("TLK") == 0 && (l.get(1).toString().compareTo("BEFORE") == 0 || l.get(1).toString().compareTo("AFTER") == 0)) {
					lines.add(l);
				}
			}
		}
		StringBuilder builder = new StringBuilder();
		for (KBLine l : lines) {
			builder.append(l.toString() + "\n");
		}
		return builder.toString();
	}
}
