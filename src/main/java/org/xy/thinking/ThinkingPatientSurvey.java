package org.xy.thinking;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xy.model.KBLine;
import org.xy.model.KBRuleUnknownData;
import org.xy.model.KBSection;
import org.xy.model.ResultEnum;
import org.xy.model.ThinkingResult;
import org.xy.model.ThinkingResultItem;
import org.xy.thinking.def.KBLoader;
import org.xy.thinking.diagnosis.SectionPatientSurvey;
import org.xy.thinking.diagnosis.SectionUtils;
import org.xy.thinking.mem.MemoryWrapper;

public class ThinkingPatientSurvey  extends ThinkingLayerBase {
    private static final Logger log = LoggerFactory.getLogger(ThinkingDiagnosis.class);
    public static final String DKD_DEF_TYPE = "SYS";
    public static final String DKD_DIG_TYPE = "DIG";
	
	public ThinkingResult think(MemoryWrapper dsmInput, ThinkingResult result, String codes) throws Exception {
       result.clear();
       if (KBLoader.getDefinitions().containsKey(DKD_DEF_TYPE, codes) == false) {
    	   log.debug("Definition file for "+codes+" wasn't found");
           return result;
       }
       if (KBLoader.getDefinitions().containsKey(DKD_DIG_TYPE, codes) == false) {
    	   log.debug("Reasoning file for "+codes+" wasn't found");
           return result;
       }
       SectionUtils.refreshExps(dsmInput, KBLoader.getDefinitions().get(DKD_DEF_TYPE, codes));
       
       if (KBLoader.getDefinitions().containsKey(DKD_DIG_TYPE, codes)) {
            //result.setStage(STAGE_PAT_SURVEY_IN_PROCESS);
            log.debug("============================PAT_SURVERY=================================");
            ResultEnum r = process(KBLoader.getDefinitions().get(DKD_DIG_TYPE, codes), dsmInput, result);
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
		SectionPatientSurvey patSurvey = new SectionPatientSurvey(section);
		SectionUtils.refreshFeatureData(dsm,  section);
		String hitCode = SectionUtils.evaluteRules(dsm,patSurvey.getExcludeRules(), 2, "rule", "code");
		if (hitCode != null) {
			log.debug(String.format("Patient Survey is not necessory, skipped"));
			return ResultEnum.Negative;
		}

		boolean allNegative = true;
		for (KBLine line: patSurvey.getConcludeRules()) {
			ResultEnum er = SectionUtils.evaluteSingleRule(dsm,  line,  2, "rule");
			if (ResultEnum.isSystemDontKnow(er)) {
				allNegative &= false;
				KBRuleUnknownData estimate_data = SectionUtils.estimateSingleRule(dsm, line, 2, "rule");
				KBLine fea = section.getLine(estimate_data.getData());
				if (fea != null) {
					double score = estimate_data.getRankValue();
					addQuestion(result,  code, fea.get(2).getContent(), "", score);
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
