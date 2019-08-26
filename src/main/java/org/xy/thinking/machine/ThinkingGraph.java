package org.xy.thinking.machine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.xy.model.KBSection;
import org.xy.model.ResultEnum;
import org.xy.model.ThinkingResult;
import org.xy.thinking.ThinkingDiagnosis;
import org.xy.thinking.ThinkingLayerBase;
import org.xy.thinking.def.KBLoader;
import org.xy.thinking.diagnosis.SectionDiagnosis;
import org.xy.thinking.diagnosis.SectionUtils;
import org.xy.thinking.mem.MemoryWrapper;



public class ThinkingGraph extends ThinkingLayerBase{
	private HashMap<String, ThinkingGraphNode> nodes = new HashMap<String, ThinkingGraphNode>();
	
	@Override
    public ThinkingResult think(MemoryWrapper dsmInput, ThinkingResult result, String sceneCode, String stageCode, String codes) throws Exception {

		//List<ThinkingGraphNode> enterPoints = new ArrayList<ThinkingGraphNode>();
		StringBuilder s = new StringBuilder();
		s.append("DKD|,^|GRAPH|2.0\n");
		s.append("GRAPH|G000|GRAPH_TRAVEL|disease_code~G000\n");
		for (String i: nodes.keySet()) {
			ThinkingGraphNode node = nodes.get(i);
			if (node.getContent()!=null && node.getContent().length()>0) {
				s.append("DI0|1|"+node.getContent()+",code~"+node.getCode()+"\n");
			} else {
				s.append("DI0|1|rule~"+node.getCode()+",code~"+node.getCode()+"\n");
			}
			for (ThinkingGraphVector v: node.getLinks()) {
				if (v.getRule()!=null && v.getRule().length()>0) {
					s.append("DI0|1|"+v.getRule()+",code~"+v.getCode()+"\n");
				}else {
					s.append("DI0|1|rule~"+v.getFrom().getCode()+",code~"+v.getCode()+"\n");
				}
			}
		}
		KBLoader.loadDKDFromString(s.toString(), 0);
		KBSection section = KBLoader.getDefinitions().get("GRAPH", "G000");
		SectionUtils.refreshExps(dsmInput, section);

		SectionDiagnosis diag = new SectionDiagnosis(section);
		List<String> enterPoints = SectionUtils.evaluateRules(dsmInput,diag.getConcludeRules(), 2, "rule", "code");
		ThinkingGraphVisited visited =  new ThinkingGraphVisited();
		ThinkingGraphVisited wait4Visit = new ThinkingGraphVisited();
		
		for (String pnt: enterPoints) {
			if (pnt.contains("_")) continue;
			if (nodes.containsKey(pnt)) {
				wait4Visit.add(nodes.get(pnt));
			}
		}

		travel(wait4Visit, visited, enterPoints, null);
//		//Now we got all positive nodes here
//		if (hitCodes.size()>0) {
//			log.debug(String.format("Patient Survey is not necessory, skipped"));
//			return ResultEnum.Negative;
//		}
		return null;
	}
	public ThinkingGraphNode getNode(String code) {
		if (nodes.containsKey(code)) {
			return nodes.get(code);
		}
		return null;
	}
	public ThinkingGraphNode createNode(String name, String code) {
		ThinkingGraphNode new_node = new ThinkingGraphNode();
		new_node.setName(name);
		new_node.setCode(code);
		nodes.put(code,  new_node);
		return new_node;
	}
	public ThinkingGraphVector linkNode(ThinkingGraphNode node1, ThinkingGraphNode node2) {
		ThinkingGraphVector v = new ThinkingGraphVector();
		v.setFrom(node1); v.setTo(node2);
		node1.getLinks().add(v);
		return v;
	}
	public void travel(ThinkingGraphVisited wait4visit, ThinkingGraphVisited visited, List<String> enabledCodes, ThinkingGraphTravelEvent event) {
		while (wait4visit.size()>0) {
			travelNode("", wait4visit.pop(),wait4visit,visited, enabledCodes, event);
		}
		
	}
	
	public void travelNode(String prefix, ThinkingGraphNode node, ThinkingGraphVisited wait4visit, ThinkingGraphVisited visited, List<String> enabledCodes, ThinkingGraphTravelEvent event) {
		if (visited.contains(node))
			return;
		if (event != null)
			event.enterNode(node);
		else 
			System.out.println(prefix + "visit " +node.getCode()+" "+ node.getName());

		visited.add(node);

		if (node.getConsistOf() != null) {
			String[] codes = node.getConsistOf().split("\\,");
			for (String tcd: codes) {
				if (nodes.containsKey(tcd)) {
					if (wait4visit.contains(nodes.get(tcd))==false) {
						wait4visit.add(nodes.get(tcd));
					}
				}
			}
		}
		
		for (ThinkingGraphVector v: node.getLinks()) {
			String cd = v.getCode();
			if (enabledCodes.contains(cd)) {
				if (visited.contains(v.getTo()))
					continue;
				System.out.println(prefix + "from "+node.getName()+" to " + v.getTo().getName());
				travelNode(prefix+"    ", v.getTo(), wait4visit, visited, enabledCodes, event);
			}
		}
		//System.out.println("end");
	}
	
	public static void main(String[] args) {
		ThinkingGraph g = new ThinkingGraph();
		ThinkingGraphNode n0 = g.createNode("n0","N000");
		ThinkingGraphNode n1 = g.createNode("n1","N001");
		ThinkingGraphNode n2 = g.createNode("n2","N002");
		ThinkingGraphNode n3 = g.createNode("n3","N003");
		ThinkingGraphNode n4 = g.createNode("n4","N004");
		ThinkingGraphNode n5 = g.createNode("n5","N005");
		n4.setConsistOf("N001,N005");
		g.linkNode(n0, n1).setRule("rule~N000");
		g.linkNode(n0, n2).setRule("rule~N000");
		g.linkNode(n1, n3);
		g.linkNode(n1, n4).setRule("rule~N004");
		g.linkNode(n4, n2);
		g.linkNode(n4, n5);
		//g.travel(null);
		
		MemoryWrapper dsmInput = new MemoryWrapper();
		ThinkingResult result = new ThinkingResult();
		try {
			dsmInput.putData("N000", "","","+");
			dsmInput.putData("N004", "","","+");
			g.think(dsmInput, result,  "", "","");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
