package org.xy.thinking.diagnosis;

import java.util.ArrayList;
import java.util.List;

import org.xy.model.ResultEnum;
import org.xy.model.KBBaseClass;
import org.xy.model.KBLine;
import org.xy.model.KBLineField;
import org.xy.model.KBSection;
import org.xy.model.KBSectionDefinition;
import org.xy.thinking.mem.MemoryWrapper;
import org.xy.thinking.mem.MemoryWrapper.DSMData;

public class SectionDisease {
	private KBSection section = null;
	private List<SectionDiagnosis> diagnosisSections = new ArrayList<SectionDiagnosis>();
	
	public List<SectionDiagnosis> getDiagnosisSections(){
		return diagnosisSections;
	}
	public SectionDisease(KBSection section) {
		this.section = section;
	}
	public String getDiseaseName(){
		KBLine l = section.getDeclarationLine();
		if (l.get(2)!=null) {
			return l.get(2).toString();
		}
		return null;
	}
	public String getDiseaseCode(){
		return getDiseaseProp("disease_code");
	}
	public String getDiseaseProp(String prop){
		KBLine l = section.getDeclarationLine();
		return SectionUtils.getProperty(l, 3, prop);
	}
	
	public boolean checkDiseaseRestriction(MemoryWrapper dsm) {
		List<KBLine> age = section.getLines("AGE");
		List<KBLine> sex = section.getLines("SEX");
		boolean ret = false;
		if (age.size() == 0 && sex.size() == 0) {
			ret = true;
			return ret;
		}
		ret = false;
		for (KBLine l : age) {
			String ageRange = l.get(2).toString();

			if (ageRange != null && ageRange.length() > 0) {
				ResultEnum result = dsm.calculateAge(dsm.getData(DiagnosisConstants.AGE_M_CODE), // TODO The AGE must change to
																						// DSM name
						ageRange);
				ret = ret || ResultEnum.isPositive(result);
			}
		}

		for (KBLine l : sex) {
			String sexCode = l.get(1).toString();
			MemoryWrapper.DSMData data = dsm.getData(sexCode);
			if (data != null) {
				ret = ret || ResultEnum.isPositive(data.getFlag());
			}
		}
//		
//		if (ret == false) {
//			if (age.size() == 0 && sex.size()==0) {
//				ret = true;
//			}
//		}
		return ret;
	}

}
