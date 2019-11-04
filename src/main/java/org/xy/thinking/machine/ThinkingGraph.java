package org.xy.thinking.machine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.xy.model.KBSection;
import org.xy.model.ResultEnum;
import org.xy.model.ThinkingResult;
import org.xy.thinking.ThinkingDiagnosis;
import org.xy.thinking.ThinkingLayerBase;
import org.xy.thinking.def.KBLoader;
import org.xy.thinking.diagnosis.SectionDiagnosis;
import org.xy.thinking.diagnosis.SectionUtils;
import org.xy.thinking.mem.MemoryWrapper;


public class ThinkingGraph {
	private Map<String, ThinkingGraphNode> nodes = new HashMap<String, ThinkingGraphNode>();
	private String globalEvaluationRules = "";


	public String getGlobalEvaluationRules(boolean forceReload) {
		if (forceReload == false && globalEvaluationRules.length() > 0) {
			return globalEvaluationRules;
		}
		StringBuilder s = new StringBuilder();
		s.append("DKD|,^|GRAPH|2.0\n");
		s.append("GRAPH|G000|GRAPH_TRAVEL|disease_code~G000\n");
		for (String i : nodes.keySet()) {
			ThinkingGraphNode node = nodes.get(i);
			if (node.getEntryCriteria() != null && node.getEntryCriteria().length() > 0) {
				s.append(node.getEntryCriteria() + "\n");
			} else {
				// s.append("DI0|"+node.getCode()+"|rule~true,code~"+node.getCode()+"\n");
			}
			for (ThinkingGraphVector v : node.getLinks()) {
				if (v.getCriterialOfPassage() != null && v.getCriterialOfPassage().length() > 0) {
					s.append(v.getCriterialOfPassage() + "\n");
				} else {
					s.append("DI1|" + v.getCode() + "|rule~true,code~" + v.getCode() + "\n");
				}
			}
		}

		globalEvaluationRules = s.toString();

		return globalEvaluationRules;
	}

	public List<String> getGlobalPriorityNodeCodes() {
		List<String> codes = new ArrayList<String>();
		ThinkingGraphNode node = nodes.get("ROOT");
		if (node == null) {
			return codes;
		}
		String strCodes = node.getPriorityNodeCodes();
		if (strCodes != null && strCodes.length() > 0) {
			String[] splittedCodes = strCodes.split(",");
			for (String s : splittedCodes) {
				codes.add(s.trim());
			}
		}
		return codes;
	}

	public ThinkingGraphNode getNode(String code) {
		if(StringUtils.isNotBlank(code)) {
			return nodes.get(code);
		}
		return null;
	}

	public ThinkingGraphNode createNode(String code, String name) {
		ThinkingGraphNode new_node = new ThinkingGraphNode();
		new_node.setName(name);
		new_node.setCode(code);
		nodes.put(code, new_node);
		return new_node;
	}
	
	public void putNode(ThinkingGraphNode node) {
		if(StringUtils.isNotBlank(node.getCode())) {
			nodes.put(node.getCode(), node);
		}
	}
	public ThinkingGraphVector linkNode(String code1, String code2) {
		ThinkingGraphNode node1 = getNode(code1);
		ThinkingGraphNode node2 = getNode(code2);
		if (node1 != null && node2 != null)
			return linkNode(node1, node2);
		return null;
	}
	public ThinkingGraphVector linkNode(ThinkingGraphNode node1, ThinkingGraphNode node2) {
		ThinkingGraphVector v = new ThinkingGraphVector();
		v.setCodeFrom(node1.getCode());
		v.setCodeTo(node2.getCode());
		v.setFrom(node1);
		v.setTo(node2);
		node1.getLinks().add(v);
		return v;
	}
	
	public Map<String, ThinkingGraphNode> getNodes() {
		return nodes;
	}
	
}
