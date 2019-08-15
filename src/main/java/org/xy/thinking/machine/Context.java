package org.xy.thinking.machine;


import java.util.LinkedList;
import java.util.List;

import org.xy.thinking.db.model.KLink;
import org.xy.thinking.db.model.KNode;

public class Context {
	private List<KNode> nodes = new LinkedList<KNode>();
	private List<KLink> links = new LinkedList<KLink>();
	private KNode current = null;
	public List<KLink> getLinks() {
		return links;
	}
	public void setLinks(List<KLink> links) {
		this.links = links;
	}
	public List<KNode> getNodes() {
		return nodes;
	}
	public void setNodes(List<KNode> nodes) {
		this.nodes = nodes;
	}
	public KNode getCurrent() {
		return current;
	}
	public void setCurrent(KNode current) {
		this.current = current;
	}
}
