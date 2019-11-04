package org.xy.thinking.db;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.xy.model.KBLine;
import org.xy.model.KBSection;
import org.xy.model.ThinkingResult;
import org.xy.thinking.db.model.KNode;
import org.xy.thinking.def.KBDefinitionMap;
import org.xy.thinking.def.KBLoader;
import org.xy.thinking.machine.ThinkingGraph;
import org.xy.thinking.machine.ThinkingGraphNode;
import org.xy.thinking.mem.MemoryWrapper;
import org.xy.thinking.service.ThinkingDatabaseService;

@SpringBootApplication
@ComponentScan("org.xy.thinking")
public class DBWriter {
	@Autowired
	ThinkingDatabaseService service;
	public void saveToDB() {
		KBDefinitionMap map = KBLoader.getDefinitions();
		
		for (String key: map.keySet()) {
			KBSection section = map.getByKey(key);
			KBLine line = section.getDeclarationLine();
			KNode node = new KNode();
			node.setType(line.get(0).toString());

			node.setCode(line.get(1).toString());
			node.setValue(line.get(2).toString());
			if (line.get(3)!=null) {
				node.setProp(line.get(3).toString());
			}
			
			
			//service.insertOrUpdate(node);
			System.out.println("insert section "+node.getCode()+" "+node.getValue());
			for (KBLine l : section.getLines()) {
				if (l != line) {
					KNode node1 = new KNode();

					node1.setType(l.get(0).toString());
					node1.setCode(l.get(1).toString());
					node1.setValue(l.get(2).toString());
					if (l.get(3)!=null) {
						node1.setProp(l.get(3).toString());
					}
					if (l.get(4)!=null) {
						node1.setProp(node1.getProp()+"|"+l.get(4).toString());
					}
					if (l.get(5)!=null) {
						node1.setProp(node1.getProp()+"|"+l.get(4).toString());
					}
					if (l.get(6)!=null) {
						node1.setProp(node1.getProp()+"|"+l.get(4).toString());
					}
					System.out.println("insert subnode "+node1.getCode()+" "+node1.getValue());
					//service.insertOrUpdate(node1);
					if (node1.getId()>0) {
						//service.link(node1,  node);
						System.out.println("link "+node1.getId()+" to "+ node.getId());
					}
				}
			}
		}
	
	}
	
	public ThinkingGraph generateGraph() {
		KBDefinitionMap map = KBLoader.getDefinitions();

		ThinkingGraph g = new ThinkingGraph();

		for (String key: map.keySet()) {
			KBSection section = map.getByKey(key);
			KBLine line = section.getDeclarationLine();
			if (line.get(0).toString().compareTo("DIS")==0) {
				ThinkingGraphNode node = g.createNode(line.get(2).toString(),line.get(1).toString());
				System.out.println("insert section "+node.getCode()+" "+node.getName());
				for (KBLine l : section.getLines()) {
					if (l != line) {
						if (l.get(0).toString().compareTo("SYM")==0) {

							ThinkingGraphNode subnode = g.createNode(l.get(2).toString(),l.get(1).toString());
							
							System.out.println("insert subnode "+subnode.getCode()+" "+subnode.getName());
							g.linkNode(subnode, node);
						}
						if (l.get(0).toString().compareTo("EXP")==0) {
							String node_id = l.get(1).toString();
							String andcodes = l.get(2).toString();
							String orcodes = l.get(3).toString();
							ThinkingGraphNode targetNode = g.getNode(node_id);
							if (orcodes.length()>0) {
								String[] parts = orcodes.split(",");
								for (String s: parts) {
									ThinkingGraphNode anotherNode = g.getNode(s);
									if (anotherNode == targetNode) {
										System.out.println("Loop detected");
									} else {
										g.linkNode(anotherNode, targetNode);
									}
								}
							}
						}
					}
				}
			} else {
				continue;
			}			
			
		}
		return g;
	}
	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(DBWriter.class,args);
		DBWriter w = ctx.getBean(DBWriter.class);
	
		String path = "/Users/alex/Documents/AI/LTConverted";
		KBLoader.loadDKDFromFileSystem(path);
		//w.saveToDB();
		ThinkingGraph g = w.generateGraph();
		MemoryWrapper dsmInput = new MemoryWrapper();
		ThinkingResult result = new ThinkingResult();
		try {
			dsmInput.putData("1493", "","","+");
			//dsmInput.putData("4559", "","","+");
			//g.think(dsmInput, result,  "", "","");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
