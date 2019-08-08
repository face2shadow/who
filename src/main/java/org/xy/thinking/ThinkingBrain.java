package org.xy.thinking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xy.model.ResultEnum;
import org.xy.model.ThinkingResult;
import org.xy.model.Action;
import org.xy.model.ActionList;
import org.xy.model.KBBaseClass;
import org.xy.model.KBDelimeters;
import org.xy.model.KBFieldComponent;
import org.xy.model.KBLine;
import org.xy.model.KBLineField;
import org.xy.model.KBSection;
import org.xy.model.KBSectionDefinition;
import org.xy.thinking.def.KBDefinitionMap;
import org.xy.thinking.def.KBFile;
import org.xy.thinking.def.KBLoader;
import org.xy.thinking.mem.MemoryWrapper;
import org.xy.thinking.mem.MemoryWrapper.DSMData;
import org.xy.thinking.rule.ThinkingRule;

public class ThinkingBrain extends ThinkingLayerBase {
	private static final Logger log = LoggerFactory.getLogger(ThinkingBrain.class);
	public static final String DSM_TOPIC_CODE = "TOPIC_CODE";
	public static final String DSM_QUESTION_CODES = "QUESTION_CODES";
	public static final String DKD_TYPE = "SCE";
	public static final String DKD_DEFAULT_NAME = "SCE000";
	public static final String DKD_HEADER_ENTER = "ENTER";
	public static final String DKD_HEADER_GO = "GO";
	public static final String DKD_HEADER_ACT = "ACT";
	public static final String DKD_PROP_RULE = "rule";
	public static final String DKD_PROP_RULE_DEFAULT = "default";
	public static final String DKD_CMD_LOAD = "LOAD";
	//
	public static final int DKD_FIELD_HEAD = 0;
	public static final int DKD_FIELD_CODE = 1;
	public static final int DKD_FIELD_NAME = 2;
	public static final int DKD_FIELD_VALUE = 3;
	public static final int DKD_FIELD_RULE = 4;
	//
	public static final int SUCCESS = 0;
	public static final int FAILED = -1;
	public static final int DKD_NOT_FOUND = -2;
	public static final int FORBIDDEN_ENTER = -3;
	
    private static ThinkingBrain _instance = null;
    
    private ThinkingBrain() {
    	
    }
    public static ThinkingBrain getInstance() {
    	if (_instance == null) _instance = new ThinkingBrain();
    	return _instance;
    }
    public static void debug(String message) {
        log.debug(message);
    }

    private String getCurrentTopic(MemoryWrapper dsmInput) {
    	DSMData data = dsmInput.getData(DSM_TOPIC_CODE);
    	if (data == null) {
    		return null;
    	}
    	return data.getValue();
    }
    private void setCurrentTopic(String code, MemoryWrapper dsmInput) {
    	dsmInput.putData(DSM_TOPIC_CODE, "", code, "+");
    }
    private Action line2Action(int category, KBLine line) {
    	Action act = new Action();
    	act.setCategory(category);
    	if (line.get(DKD_FIELD_CODE)!= null) act.setCode(line.get(DKD_FIELD_CODE).toString());
    	if (line.get(DKD_FIELD_NAME)!= null) act.setName(line.get(DKD_FIELD_NAME).toString());
    	if (line.get(DKD_FIELD_VALUE)!= null) act.setValue(line.get(DKD_FIELD_VALUE).toString());
    	if (line.get(DKD_FIELD_RULE)!= null) act.setRule(line.get(DKD_FIELD_RULE).toString());
    	return act;
    }
    private HashMap<String, Action> getActions(KBSection section){
    	HashMap<String, Action> actions = new HashMap<String, Action>();

    	
    	for (KBLine line:section.getLines()) {
	    	if (line.get(0).toString().compareTo(DKD_HEADER_ACT)==0) {
				Action act = line2Action(ThinkingResult.ACT, line);
			
				actions.put(act.getCode(), act);
	    	}
    	}
    	return actions;
    }

    private List<Action> getAllGoCommands(KBSection section, MemoryWrapper dsmInput, boolean needValidate) throws Exception{
    	List<Action> goCommands = new ArrayList<Action>();
    	for (KBLine line: section.getLines()) {
	    	if (line.get(0).toString().compareTo(DKD_HEADER_GO)==0) {
	    		Action act = line2Action(Action.GO, line);
	    		
	    		if (needValidate == true) {
	    			String ruleContent = act.getRuleContent();
	        		if (ruleContent == null) continue;
	        		if (ruleContent.compareToIgnoreCase(DKD_PROP_RULE_DEFAULT)==0) {
	        			act.setEvaluateResult(ResultEnum.Positive);       
	        		} else {
		        		ThinkingRule rule = new ThinkingRule();
		        		rule.parse(ruleContent);
		        		ResultEnum r = rule.eval(dsmInput);
		        		act.setEvaluateResult(r);
	        		}	    			
	    		}
	    		goCommands.add(act);
				
	    	}
    	}
    	return goCommands;
    }
    private int doesAllowEnterDKD(KBSection section, MemoryWrapper dsmInput) throws Exception {
    	boolean allowEnter = false;
        int countOfEnterCondition = 0;

        for (KBLine line: section.getLines()) {
        	if (line.get(0).toString().compareToIgnoreCase(DKD_HEADER_ENTER)==0) {
        		countOfEnterCondition ++;
        		String ruleContent = line.get(1).getNamed(DKD_PROP_RULE);
	    		if (ruleContent == null) continue;
	    		if (ruleContent.compareToIgnoreCase(DKD_PROP_RULE_DEFAULT)==0) {
	    			allowEnter = true;
	    		} else {
		    		ThinkingRule rule = new ThinkingRule();
		    		rule.parse(ruleContent);
		    		ResultEnum r = rule.eval(dsmInput);
		    		if (ResultEnum.isPositive(r)) {
		    			allowEnter = true;	    			
		    		}    	
	    		}
        	}        
        }
        //如果没有任何准入条件限制，则默认准入
        if (countOfEnterCondition == 0) allowEnter = true;
        if (allowEnter) return SUCCESS;
        return FAILED;
    }
    private int performActions(String code, KBSection section, MemoryWrapper dsmInput, ThinkingResult result) throws Exception {
    	HashMap<String,Action> actions = getActions(section);
        List<Action> goCommands = getAllGoCommands(section, dsmInput, true);

    	
        for (Action line: goCommands) {
        	String actionCode = line.getName(); 
        	Action act = actions.get(actionCode);
        	if (ResultEnum.isSystemDontKnow(line.getEvaluateResult()) ||
        			ResultEnum.isNegative(line.getEvaluateResult())) {
        		continue;
        	}
    		KBLineField field = new KBLineField(section.getDefinition().getDelimeters(), act.getValue());
    		String newContent = "";
    		
    		for (KBBaseClass basic:field.getFields()) {
    			//if (askPresented) break;
    			if (basic instanceof KBFieldComponent) {
    				KBFieldComponent comp = (KBFieldComponent) basic;       				
    				String type = comp.get(0).toString();        				
    				if (type != null) {
					
    					if (type.compareToIgnoreCase(DKD_CMD_LOAD)==0) {
    		    			if (comp.get(1) != null ) {
    		        			String dkdCode = comp.get(1).toString();
    		        			if (dkdCode != null) {		        			
    			        	        return processThinkFile(dkdCode, dsmInput, result);
    		        			}
    		    			}
    					} else {
    						newContent = newContent + comp.toString() + ",";       
    					}
    				}
    			}
    		}
    		if (newContent.length()>1) {
    			newContent = newContent.substring(0, newContent.length()-1);
    			result.addItem(ThinkingResult.ACT, code, "", newContent);
        		//newAct.setContent(newContent);
        		//result.getActions().add(newAct);
    		}
    	}
		return SUCCESS;
    }
    /*
     * get recommended questions list
     */
    public int getRecommendQuestions(String code, MemoryWrapper dsmInput, ThinkingResult result) throws Exception {
    	if (! KBLoader.getDefinitions().containsKey(DKD_TYPE,code) ) {
    		log.debug("Knowledge was not found");
    		return DKD_NOT_FOUND;
    	}
    	KBSection file = KBLoader.getDefinitions().get(DKD_TYPE, code);
    	if (file == null) {
    		log.debug("DKD file cache was not found");
    		return DKD_NOT_FOUND;
    	}
    	if (doesAllowEnterDKD(file, dsmInput)==FAILED) {
    		log.debug("DKD file does not allow enter");
    		return FORBIDDEN_ENTER;
    	}
    	result.clear();
    	List<Action> goCommands = getAllGoCommands(file, dsmInput, false);
         
        for (Action act: goCommands) {
        	String factCode = act.getCode();
        	if (dsmInput.getData(factCode)!=null) {
        		continue;
        	} else {
        		result.add(act);
        	}
         	
        }
    	return SUCCESS;
    }
    public int getResponse(String code, MemoryWrapper dsmInput, ThinkingResult result) throws Exception {
    	if (! KBLoader.getDefinitions().containsKey(DKD_TYPE, code) ) {
    		log.debug("Knowledge was not found");
    		return DKD_NOT_FOUND;
    	}
    	KBSection file = KBLoader.getDefinitions().get(DKD_TYPE, code);
    	if (file == null) {
    		log.debug("DKD file cache was not found");
    		return DKD_NOT_FOUND;
    	}
    	if (doesAllowEnterDKD(file, dsmInput)==FAILED) {
    		log.debug("DKD file does not allow enter");
    		return FORBIDDEN_ENTER;
    	}
    	result.clear();
    	//List<Action> goCommands = getAllGoCommands(file, dsmInput, true);
         
    	
        //for (Action act: goCommands) {
        //	if (EnumResult.isPositive(act.getEvaluateResult())) {/
        //		result.add(act);
        //	}
        //}
        performActions(code, file, dsmInput, result);

        result.keepLastOne();
    	return SUCCESS;
    }
    
    private int processThinkFile(String code, MemoryWrapper dsmInput, ThinkingResult result) throws Exception {
    	if (! KBLoader.getDefinitions().containsKey(DKD_TYPE, code) ) {
    		log.debug("Knowledge was not found");
    		return DKD_NOT_FOUND;
    	}
    	KBSection file = KBLoader.getDefinitions().get(DKD_TYPE,code);
    	if (file == null) {
    		log.debug("DKD file cache was not found");
    		return DKD_NOT_FOUND;
    	}
    	if (doesAllowEnterDKD(file, dsmInput)==FAILED) {
    		log.debug("DKD file does not allow enter");
    		return FORBIDDEN_ENTER;
    	}
    	HashMap<String, Action> actions = getActions(file);
    	
    	setCurrentTopic(code, dsmInput);
        
    	performActions(code, file, dsmInput, result);
    	
        return SUCCESS;
    }
    @Override
    public ThinkingResult think(MemoryWrapper dsmInput, ThinkingResult result, String sceneCode, String stageCode, String codes) throws Exception {
    	dsmInput.clearTemp();
    	
    	if (result == null) result = new ThinkingResult();
    	
        ActionList list = new ActionList();
        String topicCode = getCurrentTopic(dsmInput);
       
        if (topicCode != null) {
        	codes = topicCode;
        }
        if (codes == null || codes.length() == 0) {
        	processThinkFile(DKD_DEFAULT_NAME, dsmInput, result);
        } else {
        	KBDelimeters hdr = new KBDelimeters();
        	String[] codesList = codes.split(hdr.C2);
        	for (String cd : codesList) {
        		processThinkFile(cd, dsmInput, result);
        	}
        }
//        for (String key : dreResult.getQuestions().keySet()) {
//        	DKDQuestion2 q = (DKDQuestion2) dreResult.getQuestions().get(key);
//        	String diseaseCode = q.getDiseaseCode();
//        	String symCode = q.getCode();
//        	dsmInput.putTempData(DSM_QUESTION_CODES, "", diseaseCode+"^"+symCode, "+");
//        	dsmInput.putTempData("Q_SYM", "", symCode, "+");
//        }
       /*
        result.getActions().keepLastOne();
        if (result.getActions().size()>0) {
        	dreResult.getQuestions().clear();
        	
        	Action act = result.getActions().get(0);
        	String c = act.getContent();
        	String parts[] = c.split("\\,");
        	for (String qstr  : parts) {
        		String qstr_parts[]= qstr.split("\\^");
        		String dis_code = qstr_parts[0];
        		String sym_code = "";
        		String text = "";
        		if (qstr_parts.length>1) {
        			sym_code = qstr_parts[1];
        		}
        		if (qstr_parts.length>2) {
        			text = qstr_parts[2];
        		}
//        		DKDQuestion2 q = new DKDQuestion2("FEA", sym_code, text,"", "?", 1);
//        		q.setDiseaseCode(dis_code);
//    	        dreResult.getQuestions().put(q.toString(),q);
        	}
        	//System.out.println(act.getContent());
        	
	        
//			q.setDiseaseCode(diseaseCode);
//			if (questionList.containsKey(q.toString())) {
//				if (questionList.get(q.toString()).getScore() < q.getScore())
//					questionList.get(q.toString()).setScore(q.getScore());
//				questionList.get(q.toString()).increaseRefCount();
//			} else {
//				questionList.put(q.toString(), q);
//			}
        }
        */
        return result;
    }
//    public void loadDKDByCode(String code) {
//        if (code == null) return;
//        //code = "C000,T000," + code;
//        DKDSmart.debug("Load DKD for Code:" + code);
//        final String dkdPrefix = "PLAZAWORM-DKD/";
//        String[] files = code.split(",");
//        try {
//            for (int i = 0; i < files.length; i++) {
//                Map<String, Object> file = TairUtil.getMap(dkdPrefix + files[i]);
//                if (file != null && definitions.get(files[i]) == null)
//                    loadDKDFromString((String) file.get("value"), (Integer) file.get("mDate"));
//                else if (file != null && definitions.get(files[i]) != null && (Integer) file.get("mDate") > definitions.get(files[i]).getTimeStamp())
//                    loadDKDFromString((String) file.get("value"), (Integer) file.get("mDate"));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


}
