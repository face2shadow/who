package org.xy.model;

import java.util.ArrayList;

public class DiseaseEvidenceList extends ArrayList<DiseaseEvidence> {
	private String diseaseCode;
	public String getDiseaseCode() {
		return diseaseCode;
	}
	public void setDiseaseCode(String diseaseCode) {
		this.diseaseCode = diseaseCode;
	}
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (DiseaseEvidence e: this) {
			builder.append(String.format("%s %s=%d\n", getDiseaseCode(), e.getSymptomCode(),e.getLevel()));
		}
		return builder.toString();
	}
}
