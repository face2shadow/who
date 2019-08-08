package org.xy.model;

import java.util.ArrayList;
import java.util.List;

public class KBSection {

	private KBLine declarationLine;
	private KBSectionDefinition headLine;
	private String fileType;


	private List<KBLine> lines = new ArrayList<KBLine>();



	public KBSectionDefinition getDefinition() {
		return headLine;
	}
	public List<KBLine> getLines() {
		return lines;
	}

	public KBLine getLine(String id) {


		for (KBLine b : lines) {
			if (b instanceof KBLine) {
				KBLine l = (KBLine)b;
				if (l.get(1) == null) continue;
				if (l.get(1).toString().compareTo(id)==0) {
					return b;
				}
			}
		}
		return null;
	}
	public List<KBLine> getLines(String tag){
		List<KBLine> lines = new ArrayList<KBLine>();
		for (KBLine b : lines) {
			if (b instanceof KBLine) {
				KBLine l = (KBLine)b;
				if (l.get(0).toString().compareTo(tag)==0) {
					lines.add(l);
				}
			}
		}
		return lines;
	}

	public void setDefinition(KBSectionDefinition header) {
		this.headLine = header;
	}

	public void setLines(List<KBLine> lines) {
		this.lines = lines;
	}


	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public KBLine getDeclarationLine() {
		return declarationLine;
	}

	public void setDeclarationLine(KBLine declarationLine) {
		this.declarationLine = declarationLine;
	}
	
	public void parseLine(String s) {
		KBLine l = new KBLine(headLine.getDelimeters(), s);
		String flag = l.get(0).toString();
		if (flag.compareTo(getFileType()) == 0) {
			this.setDeclarationLine(l);;
		}
		getLines().add(l);
	}
}
