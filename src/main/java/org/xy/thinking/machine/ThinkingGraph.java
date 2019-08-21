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

		List<ThinkingGraphNode> enterPoints = new ArrayList<ThinkingGraphNode>();
		StringBuilder s = new StringBuilder();
		s.append("DKD|,^|GRAPH|2.0\n");
		s.append("GRAPH|G000|GRAPH_TRAVEL|disease_code~G000\n");
		for (String i: nodes.keySet()) {
			ThinkingGraphNode node = nodes.get(i);
			s.append(node.getContent()+"\n");
		}
		KBLoader.loadDKDFromString(s.toString(), 0);
		KBSection section = KBLoader.getDefinitions().get("GRAPH", "G000");
		SectionUtils.refreshExps(dsmInput, section);

		SectionDiagnosis diag = new SectionDiagnosis(section);
//		List<String> enterPoints = SectionUtils.evaluteRules(dsmInput,diag.getConcludeRules(), 2, "rule", "code");
//		//Now we got all positive nodes here
//		if (hitCodes.size()>0) {
//			log.debug(String.format("Patient Survey is not necessory, skipped"));
//			return ResultEnum.Negative;
//		}
		return null;
	}
	
	public ThinkingGraphNode createNode(String name) {
		ThinkingGraphNode new_node = new ThinkingGraphNode();
		new_node.setName(name);

		return new_node;
	}
	public void linkNode(ThinkingGraphNode node1, ThinkingGraphNode node2) {
		ThinkingGraphVector v = new ThinkingGraphVector();
		v.setFrom(node1); v.setTo(node2);
		node1.getLinks().add(v);
	}
	public void travel(ThinkingGraphTravelEvent event) {
		ThinkingDiagnosis diag = new ThinkingDiagnosis();

		//travelNode(event, enterPoint, new ArrayList<ThinkingGraphNode>());
	}
	
	public void travelNode(ThinkingGraphTravelEvent event, ThinkingGraphNode node, List<ThinkingGraphNode> visited) {
		if (visited.contains(node))
			return;
		if (event != null)
			event.enterNode(node);
		else 
			System.out.println(node.getName());
		visited.add(node);
		for (ThinkingGraphVector v: node.getLinks()) {
			System.out.println("from "+node.getName()+" to " + v.getTo().getName());
			travelNode(event, v.getTo(), visited);
		}
		//System.out.println("end");
	}
	
	public static void main(String[] args) {
		ThinkingGraph g = new ThinkingGraph();
		ThinkingGraphNode n0 = g.createNode("n0");
		ThinkingGraphNode n1 = g.createNode("n1");
		ThinkingGraphNode n2 = g.createNode("n2");
		ThinkingGraphNode n3 = g.createNode("n3");
		ThinkingGraphNode n4 = g.createNode("n4");
		ThinkingGraphNode n5 = g.createNode("n5");
		g.linkNode(n0, n1);
		g.linkNode(n0, n2);
		g.linkNode(n1, n3);
		g.linkNode(n1, n4);
		g.linkNode(n4, n2);
		g.linkNode(n4, n5);
		g.travel(null);
	}
}
