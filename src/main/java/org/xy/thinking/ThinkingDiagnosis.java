package org.xy.thinking;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xy.model.DiseaseEvidence;
import org.xy.model.DiseaseEvidenceList;
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
import org.xy.thinking.machine.GraphEvaluate;
import org.xy.thinking.machine.ThinkingActionList;
import org.xy.thinking.machine.UserInputOutputInterface;
import org.xy.thinking.mem.DSMData;
import org.xy.thinking.mem.MemoryWrapper;

public class ThinkingDiagnosis extends ThinkingLayerBase {
    private static final Logger log = LoggerFactory.getLogger(ThinkingDiagnosis.class);

    public static final String DKD_DEF_TYPE = "DIS";
    public static final String DKD_DIG_TYPE = "DIG";
	private SectionDisease disease ;
	private List<SectionDiagnosis> diagnosis = new ArrayList<SectionDiagnosis>();

    public static final String USER_WANT_SKIP_QUESTION = "SKIP";
	//private SectionDisease disease ;
	//private List<SectionDiagnosis> diagnosis = new ArrayList<SectionDiagnosis>();
    //疾病的发生率表
	public static HashMap<String, Integer> probTable = new HashMap<String, Integer>();
	//疾病代码到名字的对照表
	public static HashMap<String, String> nameTable = new HashMap<String, String>();
	//术语字典表 Code -> Name
	public static HashMap<String, String> dictTable = new HashMap<String, String>();
	//术语字典表 Name -> Code
	public static HashMap<String, String> dictNameTable = new HashMap<String, String>();
	//用户症状到主题的映射表  SymptomCode -> TopicCode
	public static HashMap<String, String> symTopicTable = new HashMap<String, String>();
	public static StringBuilder traceLog = new StringBuilder();
	public static int logLevel = 3;
	public static int DEBUG = 3;
	public static int INFO = 1;
	public static int WARNING = 2;
	public static UserInputOutputInterface userIO = null;
	public static String consoleEncoding = "UTF-8";
	//用于输出的方法
	public static void trace(String message) {
		traceLog.append(message+"\n");
	}
    public static void debug(String message) {
    	if (logLevel >= DEBUG)
    		System.out.println(message);
    }

    public static void info(String message) {
    	if (logLevel >= INFO)
    		System.out.println(message);
    }
    public static void warning(String message) {
    	if (logLevel >= WARNING)
    		System.out.println(message);
    }
    //
    /***
     * 生成可读的规则
     * @param rule
     * @return
     */
    public static String generateReadableRule(String rule) {
    	String newRule = rule;


    	for (String key: dictTable.keySet()) {
    		if (newRule.indexOf("["+key+"=")>0) {
    			newRule = newRule.replace("["+key+"=", "["+dictTable.get(key)+":");
    			//System.out.println(newRule);
    		}
    		if (newRule.indexOf("[!"+key+"=")>0) {
    			newRule = newRule.replace("[!"+key+"=", "["+dictTable.get(key)+":!");
    			//System.out.println(newRule);
    		}


			newRule = newRule.replace("+", "是");
			newRule = newRule.replace("-", "否");

			newRule = newRule.replace("!未知", "未知");
			newRule = newRule.replace("?", "未知");

			newRule = newRule.replace("!是", "否");
			newRule = newRule.replace("!否", "是");
    	}

    	return newRule;
    }

    public String getQuestion(String stageCode, String diseaseCode, String symptomCode) {
    	KBSection sectionStage = KBLoader.getDefinitions().get(stageCode, diseaseCode);
    	if (sectionStage == null) return null;
    	for (KBLine l: sectionStage.getLines("FEA")) {
    		if (l.get(1).toString().compareTo(symptomCode) == 0) {
    			String q = SectionUtils.getProperty(l, 3, "question");
    			if (q != null)
    				return q;
    		}
    	}
		return null;
    }
    /*
    public String getQuestionTopic(KBSection sectionStage, String symptomCode) {
    	KBLine line = sectionStage.getDeclarationLine();
    	if (line != null && line.get(0).toString().compareTo("EMR") ==0) {

        	for (KBLine l: sectionStage.getLines("EXT")) {
        		if (l.get(1).toString().compareTo(symptomCode) == 0) {
        			String q = SectionUtils.getProperty(l, 4, "topic");
        			if (q != null)
        				return q;
        		}
        	}
    		return null;
    	}
    	for (KBLine l: sectionStage.getLines("FEA")) {
    		if (l.get(1).toString().compareTo(symptomCode) == 0) {
    			String q = SectionUtils.getProperty(l, 3, "topic");
    			if (q != null)
    				return q;
    		}
    	}
		return null;
    }*/

    //拿到DSM中已知症状对应的疾病，症状的等级也包括在内
    public Collection<DiseaseEvidenceList> getPreferDisease(MemoryWrapper dsmInput) {
    	HashMap<String, DiseaseEvidenceList> ret = new HashMap<String, DiseaseEvidenceList>();
    	DSMData exc_data = dsmInput.getData("EXCLUDED_LIST");
    	for (String key : KBLoader.getDefinitions().keySet()) {
    		KBSection section = KBLoader.getDefinitions().getByKey(key);
    		String[] parts = key.split("\\|");
    		if (parts.length<2) continue;
    		if (parts[0].compareTo("DIG") != 0 )continue;
    		if (exc_data != null) {
    			String codes = exc_data.getValue();
    			String[] manyCodes = codes.split("\\|");
    			boolean excluded = false;
    			for (String oneCode: manyCodes) {
    				if (oneCode.compareTo(parts[1]) == 0) {
    					//already excluded, skip
    					excluded = true;
    					break;
    				}
    			}
    			if (excluded) continue;
    		}
    		DiseaseEvidenceList list = null;
    		if (ret.containsKey(parts[1])) {
    			list=ret.get(parts[1]);
    		} else {
    			list = new DiseaseEvidenceList();
    			list.setDiseaseCode(parts[1]);
    		}
    		List<KBLine> lines = section.getLines("FEA");
    		for (KBLine l : lines) {
    			String code = l.get(1).toString();
    			if (dsmInput.getData(code) != null) {
    				DSMData data = dsmInput.getData(code);
    				if (data.getFlag().compareTo("+")==0) {
	    				DiseaseEvidence evd= new DiseaseEvidence();
	    				evd.setSymptomCode(code);
	    				String level = SectionUtils.getProperty(l, 3, "level");
	    				if (level != null)
	    					evd.setLevel(Integer.parseInt(level));
	    				else
	    					evd.setLevel(0);
	    				list.add(evd);
    				}
    			}
    		}
    		if (list.size()>0) ret.put(parts[1],list);
    	}
    	//for (DiseaseEvidenceList del : ret.values()) {
    	//	System.out.println(del);
    	//}
    	return ret.values();
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
		Set<String> hitCodes = SectionUtils.evaluateRules(dsm,patSurvey.getExcludeRules(), 2, "rule", "code");
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


	public static ThinkingActionList getActionListOfNode(MemoryWrapper dsmInput, String code, String topic) throws Exception {
		ThinkingActionList list = new ThinkingActionList();

        if (KBLoader.getDefinitions().containsKey("ACTION", topic) == false) {
      	   //debug("Definition file for topic "+topic+" wasn't found");
           return list;
        }
 		KBSection section = KBLoader.getDefinitions().get("ACTION", topic);
		GraphEvaluate diag = new GraphEvaluate(section);
		dsmInput.clearTemp();
		SectionUtils.refreshFeatureData(dsmInput,  section);
		Set<String> activatedAction = SectionUtils.evaluateRulesWithUnknown(dsmInput, diag.getActionRules(), 2, "rule", "id");
		for (String actionCode : activatedAction) {
			KBLine line = section.getLine(actionCode);
			String type = line.getStringValue(0);
			String lcode = line.getStringValue(1);
			String propertyValue = line.getStringValue(2);
			if (propertyValue == null)
				propertyValue = "";
			if (propertyValue != null) {
				list.addAction(1, type, code, lcode, lcode, propertyValue);
			}
		}

		return list;
	}
    
}
