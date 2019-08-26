package org.xy.thinking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xy.model.KBLine;
import org.xy.model.KBRuleUnknownData;
import org.xy.model.KBSection;
import org.xy.model.ResultEnum;
import org.xy.model.ThinkingResult;
import org.xy.model.ThinkingResultItem;
import org.xy.thinking.def.KBFile;
import org.xy.thinking.def.KBDefinitionMap;
import org.xy.thinking.def.KBLoader;
import org.xy.thinking.diagnosis.DiagnosisConstants;
import org.xy.thinking.diagnosis.SectionDiagnosis;
import org.xy.thinking.diagnosis.SectionDisease;
import org.xy.thinking.diagnosis.SectionPatientSurvey;
import org.xy.thinking.diagnosis.SectionUtils;
import org.xy.thinking.mem.MemoryWrapper;

public class ThinkingDiagnosis extends ThinkingLayerBase {
    private static final Logger log = LoggerFactory.getLogger(ThinkingDiagnosis.class);

    public static final String DKD_DEF_TYPE = "DIS";
    public static final String DKD_DIG_TYPE = "DIG";
	private SectionDisease disease ;
	private List<SectionDiagnosis> diagnosis = new ArrayList<SectionDiagnosis>();
    public static void debug(String message) {
    	log.info(message);
    }

    @Override
    public ThinkingResult think(MemoryWrapper dsmInput, ThinkingResult result, String sceneCode, String stageCode, String codes) throws Exception {
        result.clear();
        if (KBLoader.getDefinitions().containsKey(sceneCode, codes) == false) {
     	   log.debug("Definition file for "+codes+" wasn't found");
            return result;
        }
        if (KBLoader.getDefinitions().containsKey(stageCode, codes) == false) {
     	   log.debug("Reasoning file for "+codes+" wasn't found");
            return result;
        }
        SectionUtils.refreshExps(dsmInput, KBLoader.getDefinitions().get(sceneCode, codes));
        SectionDisease disease = new SectionDisease(KBLoader.getDefinitions().get(stageCode, codes));
        if (disease.checkDiseaseRestriction(dsmInput) == false) {
            log.debug("Disease restriction for patient sex or age, skipped");
        	result.setResult(ResultEnum.Negative);
        	return result;
        }
        if (KBLoader.getDefinitions().containsKey(stageCode, codes)) {
             //result.setStage(STAGE_PAT_SURVEY_IN_PROCESS);
        	KBSection sectionScene = KBLoader.getDefinitions().get(sceneCode, codes);
        	KBSection sectionStage = KBLoader.getDefinitions().get(stageCode, codes);
        	SectionUtils.copyLinesTo(sectionScene, sectionStage, "SYM");
            log.debug("Start diagnosis for disease "+ codes);	
            ResultEnum r = process(sectionStage, dsmInput, result);
            result.setResult(r);
         } 	
 		return result;
    }


	private ResultEnum process(KBSection section, MemoryWrapper dsm, ThinkingResult result) throws Exception {
		String code = SectionUtils.getFieldText(section.getDeclarationLine(), 1);
		String name = SectionUtils.getFieldText(section.getDeclarationLine(), 2);
		if (code != null)
			log.debug("================Starting Reasoning for [" + code + "]===================");
		log.debug("===============================DSM====================================");
		dsm.dumpDSM();
		log.debug("======================================================================");
		if (dsm.isCodeInExcludedList(result, code)) {
			log.debug(String.format("Pattent Survey was excluded, %s, %s", name, code));
			return ResultEnum.Negative;
		}
		dsm.clearTemp();
		SectionDiagnosis patSurvey = new SectionDiagnosis(section);
		SectionUtils.refreshFeatureData(dsm,  section);
		List<String> hitCodes = SectionUtils.evaluateRules(dsm,patSurvey.getExcludeRules(), 2, "rule", "code");
		if (hitCodes.size()>0) {
			log.debug(String.format("Patient Survey is not necessory, skipped"));
			return ResultEnum.Negative;
		}

		boolean allNegative = true;
		for (KBLine line: patSurvey.getConcludeRules()) {
			ResultEnum er = SectionUtils.evaluteSingleRule(dsm,  line,  3, "rule");
			if (ResultEnum.isSystemDontKnow(er)) {
				allNegative &= false;
				KBRuleUnknownData estimate_data = SectionUtils.estimateSingleRule(dsm, line, 3, "rule");
				KBLine fea = section.getLine(estimate_data.getData());
				if (fea != null) {
					double score = estimate_data.getRankValue();
					addQuestion(result,  code, fea.get(1).getContent(), fea.get(2).getContent(), score);
				}
			}
		}
		if (allNegative) {
			return ResultEnum.Negative;
		}
		return ResultEnum.SystemDontKnow;
	}
	private void addQuestion(ThinkingResult result, String sectionCode, String questionCode, String questionValue, double questionRank) {
		ThinkingResultItem item = new ThinkingResultItem();
		item.setCategory(ThinkingResult.ASK);
		item.setCode(sectionCode);
		item.setName(questionCode);
		item.setValue(questionValue);
		item.setRankValue(questionRank);
		result.add(item);
	}


    
}
