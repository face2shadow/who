package org.xy.thinking.machine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.xy.model.KBLine;
import org.xy.model.KBSection;
import org.xy.thinking.ThinkingDiagnosis;
import org.xy.thinking.def.KBLoader;
import org.xy.thinking.diagnosis.SectionUtils;
import org.xy.thinking.mem.MemoryWrapper;


public class TopicManager {

	private static final String BLOCK_UNTIL_FINISHED = "BLOCK";
	
	private ThinkingGraph graph = new ThinkingGraph();
	String globalEvaluationRules = "";
	public void initialize() {
		//StringBuilder content = new StringBuilder();
		//content.append("DKD|,^|TOPIC|2.0\n");
		//content.append("TOPIC|TOPIC_LIST|TOPIC_LIST|code~ROOT\n");
		
		for (String key: KBLoader.getDefinitions().keySet()) {
			if (key.startsWith("ACTION|")) {
				KBSection sec = KBLoader.getDefinitions().getByKey(key);
				KBLine line = sec.getDeclarationLine();

				ThinkingGraphNode node = graph.createNode(line.get(1).toString(), line.get(2).toString());
				node.setActionContent(sec.toString());
				node.setLevel(100); //TOPIC
				node.setType("TOPIC");
				String lineContent = line.toString();
				if (lineContent.indexOf("rule~")>0){					
					lineContent = lineContent.replace("ACTION", "DI0") + ",code~" + line.get(1).toString();
					node.setEntryCriteria(lineContent);
				}
			}
		}
		for (String code: ThinkingDiagnosis.symTopicTable.keySet()) {
			ThinkingGraphNode node = graph.createNode(code, code);
			node.setLevel(10000); //TOPIC
			node.setType("SYMPTOM");
			node.setEntryCriteriaRule(code);			
		}
		for (String code: ThinkingDiagnosis.symTopicTable.keySet()) {
			String[] codes = ThinkingDiagnosis.symTopicTable.get(code).split(",");
			for (String c: codes) {
				graph.linkNode(code,  c);
			}
			
		}
		//graph.linkNode("4639", "T4639");
		KBLoader.loadDKDFromString(getGlobalEvaluationRules(false), 0);
		//KBLoader.loadDKDFromString(content.toString(), 0);		
	}
	public String getNextTopic(MemoryWrapper mem) throws Exception {

		ThinkingGraphVisited result = think(mem, null);
		if (result == null) return null;
		ThinkingGraphNode node = result.pop();
		while (node != null) {
			if (node.getType().compareTo("TOPIC")==0) {
				if (mem.getData("TOPIC_"+node.getCode())==null) {
					return node.getCode();	
				}				
			}
			node = result.pop();
		}
		//for (ThinkingGraphNode basic : result) {
			
		//}
		/*GraphEvaluate diag = new GraphEvaluate(KBLoader.getDefinitions().get("TOPIC", "TOPIC_LIST"));
		KBSection section = KBLoader.getDefinitions().get("TOPIC", "TOPIC_LIST");
		SectionUtils.refreshFeatureData(mem,  section);
		Set<String> activatedAction = SectionUtils.evaluateRules(mem, diag.getConcludeRules(), 3, "rule", "topic");
		if (activatedAction != null && activatedAction.size()>0) {
			for (String s : activatedAction) {
				if (mem.getData("TOPIC_"+s) == null) {
					return s;
				}
			}
		}*/
			
		return null;
//		for (String s : wait4Improve) {
//			result.clear();
//			addQuestion(result,  "C8888",s, "", 1);
//			askPatient("DIG",this, mem, result,"?");
//		}
	}

	public String getGlobalEvaluationRules(boolean forceReload) {
		if (forceReload == false && globalEvaluationRules.length() > 0) {
			return globalEvaluationRules;
		}
		StringBuilder s = new StringBuilder();
		s.append("DKD|,^|GRAPH|2.0\n");
		s.append("GRAPH|G000|GRAPH_TRAVEL|disease_code~G000\n");
		for (String i : graph.getNodes().keySet()) {
			ThinkingGraphNode node = graph.getNodes().get(i);
			if (node.getEntryCriteria() != null && node.getEntryCriteria().length() > 0) {
				s.append(node.getEntryCriteria() + "\n");
			} else {
				// s.append("DI0|"+node.getCode()+"|rule~true,code~"+node.getCode()+"\n");
			}
			for (ThinkingGraphVector v : node.getLinks()) {
				if (v.getCriterialOfPassage() != null && v.getCriterialOfPassage().length() > 0) {
					s.append(v.getCriterialOfPassage() + "\n");
				} else {
					s.append("DI1|" + v.getCode() +"|"+v.getCode()+ "|rule~true,code~" + v.getCode() + "\n");
				}
			}
		}

		globalEvaluationRules = s.toString();

		return globalEvaluationRules;
	}

	public HashMap<String, String> optionsParse(String content){
		HashMap<String, String> map = new HashMap<String, String>();
		String[] parts = content.split("\\]");
		for (String s: parts) {
			s = s.substring(1);
			s = s + "^NA+";
			String[] comps = s.split("\\^");
			map.put(comps[0], comps[1]);
		}
		return map;
	}

	public HashMap<String, String> parseOptionValues(String option_value){
		HashMap<String, String> data = new HashMap<String, String>();
		if (option_value.compareTo("NA")!=0) {
			String option_key = "";
			for (int i=0;i<option_value.length();i++) {
				String s = option_value.substring(i,i+1);
				if (s.compareTo("\\")==0) {
					option_key += option_value.substring(i+1,i+2);
					i++;
					continue;
				}
				if (s.compareTo("+") == 0 || s.compareTo("-") == 0) {
					data.put(option_key,s);
					option_key = "";
				} else {
					option_key += option_value.substring(i,i+1);
				}										
			}										
		} 
		return data;
	}
	public String formatUserSelectedOption(HashMap<String,String> optionsMap, String questionType, boolean excludeOthers, String userInput) {
		String all_negative_option = "DENIED_ALL";
		String all_positive_option = "CONFIRM_ALL";
		boolean all_positive = false;
		boolean all_negative = false;
		String [] input_option_array = userInput.split(" ");
		List<String> inputOptionArray = new ArrayList<String>();
		HashMap<String, String> selected = new HashMap<String, String>();
		//change user input into array
		for (String option : input_option_array) {
			if (!inputOptionArray.contains(option)) inputOptionArray.add(option);
		}
		//find 'all negative' and 'all positive' option in user's input
		for (String key : optionsMap.keySet()) {
			if (inputOptionArray.contains(key) && optionsMap.get(key).compareTo(all_positive_option+"+") == 0) {
				all_positive = true;
			}
			if (inputOptionArray.contains(key) && optionsMap.get(key).compareTo(all_negative_option+"+") == 0) {
				all_negative = true;
			}
		}
		
		//if 'all negative' or 'all positive'
		if (all_positive || all_negative) {
			for (String key: optionsMap.keySet()) {
				String options = optionsMap.get(key);
				HashMap<String, String> optionValues = parseOptionValues(options);
				for (String optionKey: optionValues.keySet()) {
					if (optionKey.compareTo(all_positive_option)==0 || optionKey.compareTo(all_negative_option)==0 ) {
						continue;
					}
					if (all_negative) {
						selected.put(optionKey,  optionValues.get(optionKey).compareTo("+")==0?"-":"+");
					} else {
						selected.put(optionKey,  optionValues.get(optionKey));
					}
				}
			}
			String s = "";
			for (String key: selected.keySet()) {
				s += key + selected.get(key);
			}
			return s;
		}
		String userSelectedOptionValues = "";
		String excludedOptionValues = "";
		for (String key: optionsMap.keySet()) {
			
			if (inputOptionArray.contains(key)) {
				userSelectedOptionValues += optionsMap.get(key);
			} if (inputOptionArray.contains(key+"-")) {
				if (optionsMap.get(key).endsWith("+")) {
					userSelectedOptionValues += optionsMap.get(key).replace("+", "-");
				}
				if (optionsMap.get(key).endsWith("-")) {
					userSelectedOptionValues += optionsMap.get(key).replace("-", "+");
				}
			}else {
				if (excludeOthers) {
					if (optionsMap.get(key).compareTo(all_negative_option+"+") == 0 || optionsMap.get(key).compareTo(all_positive_option+"+") == 0) {
						continue;
					} else {
						excludedOptionValues +=optionsMap.get(key);
					}
				}
			}
		}

		HashMap<String, String> selectedOptionMap = parseOptionValues(userSelectedOptionValues);
		HashMap<String, String> excludedOptionMap = parseOptionValues(excludedOptionValues);
		
		for (String key: selectedOptionMap.keySet()) {
			selected.put(key, selectedOptionMap.get(key));
		}
		for (String key: excludedOptionMap.keySet()) {
			selected.put(key, excludedOptionMap.get(key).compareTo("+")==0?"-":"+");
		}
		userSelectedOptionValues = "";
		for (String key: selected.keySet()) {
			userSelectedOptionValues += key+selected.get(key);
		}
		return userSelectedOptionValues;
	}
	public HashMap<String,String> filterOptions(MemoryWrapper mem, ThinkingActionBasic question){

		HashMap<String,String> optionsMap = optionsParse(question.getProperties().get("options"));
		List<String> wait4Remove = new ArrayList<String>();
		for (String key : optionsMap.keySet()) {
			HashMap<String, String> optionValues = parseOptionValues(optionsMap.get(key));
			HashMap<String, String> optionValuesRemained = new HashMap<String, String>();
			for (String optionKey: optionValues.keySet()) {
				if (mem.getData(optionKey)==null) {
					optionValuesRemained.put(optionKey, optionValues.get(optionKey));
				}
			}
			if (optionValuesRemained.size()>0) {
				String newValue = "";
				for (String optionKey: optionValuesRemained.keySet()) {
					newValue += optionKey+optionValuesRemained.get(optionKey);
				}							
				optionsMap.put(key, newValue);
			} else {
				wait4Remove.add(key);
			}						
		}
		for (String key : wait4Remove) {
			optionsMap.remove(key);
		}
		return optionsMap;
	}
	public void setQuestionStatus(MemoryWrapper mem, String topic, String questionCode, String flag) {
		mem.putData(topic+"_"+questionCode,"","",flag);
	}
	public boolean isTopicExisted(String topic) {
		if (topic == null) return false;
        if (KBLoader.getDefinitions().containsKey("ACTION", topic) == false) {
       	   ThinkingDiagnosis.debug("Definition file for topic "+topic+" wasn't found");
            return false;
         }
        return true;
	}
	public boolean isQuestionAsked(MemoryWrapper mem, String topic, String questionCode) {
		if (mem.getData(topic+"_"+questionCode)==null) return false;
		return true;
	}
    public String getTopic(KBSection sectionStage, String symptomCode) {
    	if (ThinkingDiagnosis.symTopicTable.containsKey(symptomCode)) {
    		return ThinkingDiagnosis.symTopicTable.get(symptomCode);
    	}
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
    }
	public void setTopicStatus(MemoryWrapper dsm, String topic, String flag) {
		dsm.putData("TOPIC_"+topic, "",	"", flag);
	}
	
	public ThinkingGraphVisited think(MemoryWrapper dsmInput, ThinkingGraphTravelEvent event) throws Exception {
		//KBLoader.loadDKDFromString(getGlobalEvaluationRules(false), 0);
		KBSection section = KBLoader.getDefinitions().get("GRAPH", "G000");
		SectionUtils.refreshExps(dsmInput, section);

		GraphEvaluate diag = new GraphEvaluate(section);
		Set<String> enterPoints = SectionUtils.evaluateRules(dsmInput, diag.getConcludeRules(), 3, "rule", "code");
		Set<String> excludedPoints = SectionUtils.evaluateRules(dsmInput, diag.getExcludeRules(), 3, "rule", "code");
		ThinkingGraphVisited visited = new ThinkingGraphVisited();
		ThinkingGraphVisited topicWait4Visit = new ThinkingGraphVisited();
		ThinkingGraphVisited wait4Visit = new ThinkingGraphVisited();

		Map<String, ThinkingGraphNode> nodes = graph.getNodes();
		//保持主题代码
		for (String pnt : enterPoints) {
			if (pnt.contains("->"))
				continue;
			if (!excludedPoints.contains(pnt)) {
				ThinkingGraphNode gNode = nodes.get(pnt);
				if(gNode != null) {
					if (gNode.getLevel() == 10000) { //10000是主题入口
						topicWait4Visit.add(gNode);
					} else if (gNode.getLevel() == 0) {
						wait4Visit.add(gNode);
					}
				}
			}
		}
		if (topicWait4Visit.size() > 0) {
			ThinkingActionList result = travel(nodes, dsmInput, topicWait4Visit, visited, enterPoints, excludedPoints, event);
			if (result.size() == 0) {
				//clearTopic(sessionId, dsmInput);
			} else {
				return visited;
			}
		}
		//保持主题代码结束

		ThinkingActionList result = travel(nodes, dsmInput, wait4Visit, visited, enterPoints, excludedPoints, event);
		Collections.sort(result);
		return visited;
	}
	
	private ThinkingActionList travel(Map<String, ThinkingGraphNode> nodes, MemoryWrapper dsmInput, ThinkingGraphVisited wait4visit,
			ThinkingGraphVisited visited, Set<String> entryNodeCodes, Set<String> excludedNodeCodes, ThinkingGraphTravelEvent event) {
		ThinkingActionList result = new ThinkingActionList();
		while (wait4visit.size() > 0) {
			try {
				travelNode(nodes, dsmInput, result, "", wait4visit.pop(), wait4visit, visited, entryNodeCodes, excludedNodeCodes, event);
			} catch (Exception e) {
				ThinkingDiagnosis.warning(e.getMessage());
			}
		}
		return result;
	}
	
	private void travelNode(Map<String, ThinkingGraphNode> nodes, MemoryWrapper dsmInput, ThinkingActionList result, String prefix, ThinkingGraphNode node,
			ThinkingGraphVisited wait4visit, ThinkingGraphVisited visited, Set<String> entryNodeCodes, Set<String> excludedNodeCodes, ThinkingGraphTravelEvent event) throws Exception {
		if (visited.contains(node))
			return;
		if (event != null)
			event.enterNode(node);
		//else
		//	ThinkingDiagnosis.debug(String.format("%s visit %s %s", prefix, node.getCode(), node.getName()));

		visited.add(node);

		ThinkingActionList actionList = ThinkingDiagnosis.getActionListOfNode(dsmInput, "GRAPH",node.getCode());

		if (actionList != null && !actionList.isEmpty() && BLOCK_UNTIL_FINISHED.equals(node.getType())) {
			result.addAll(actionList);
			return;
		}

		if (actionList != null && actionList.size() > 0) {
			result.addAll(actionList);
		}

		if (node.getConsistOf() != null) {
			String[] codes = node.getConsistOf().split("\\,");
			for (String tcd : codes) {
				ThinkingGraphNode gNode = nodes.get(tcd);
				if (gNode != null && !wait4visit.contains(gNode)) {
					wait4visit.add(gNode);
				}
			}
		}

		for (ThinkingGraphVector v : node.getLinks()) {
			String cd = v.getCode();
			boolean allowPass = false;
			if (v.getCriterialOfPassage() != null && v.getCriterialOfPassage().length() > 0) {
				if (entryNodeCodes.contains(cd))
					allowPass = true;
			} else {
				allowPass = true;
			}

			if (allowPass == true) {
				// the line is enabled
				if (visited.contains(v.getTo())) // already visited, skip
					continue;

				if (excludedNodeCodes.contains(v.getCodeTo())) // in excluded list, skip
					continue;

				if (v.getTo().getEntryCriteria() != null && v.getTo().getEntryCriteria().length() > 0) {
					if (!entryNodeCodes.contains(v.getCodeTo())) // not in entry list, skip
						continue;
				}

				//ThinkingDiagnosis.debug(String.format("%s from %s to %s", prefix, node.getName(), v.getTo().getName()));
				travelNode(nodes, dsmInput, result, prefix + "    ", v.getTo(), wait4visit, visited, entryNodeCodes, excludedNodeCodes, event);
			}
		}
	}
	
	public ThinkingActionList getActionListOfNode(MemoryWrapper dsmInput, ThinkingGraphNode node) throws Exception {
		ThinkingActionList list = new ThinkingActionList();

		StringBuilder s = new StringBuilder();
		s.append("DKD|,^|ACTION|2.0\n");
		//s.append("ACTION|" + node.getCode() + "|ACTION_" + node.getCode() + "|disease_code~G000\n");
		s.append(node.getActionContent());

		KBLoader.loadDKDFromString(s.toString(), 0);
		KBSection section = KBLoader.getDefinitions().get("ACTION", node.getCode());
		GraphEvaluate diag = new GraphEvaluate(section);
		Set<String> activatedAction = SectionUtils.evaluateRules(dsmInput, diag.getActionRules(), 3, "rule", "code");
		for (String actionCode : activatedAction) {
			KBLine line = section.getLine(actionCode);
			String type = line.getStringValue(0);
			String code = line.getStringValue(1);
			String name = line.getStringValue(2);
			String propertyValue = line.getStringValue(3);
			if (propertyValue == null)
				propertyValue = "";
			if (propertyValue != null) {
				list.addAction(node.getLevel(), type, node.getCode(), code, name, propertyValue);
			}
		}
		return list;
	}	
}
