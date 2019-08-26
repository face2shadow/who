package org.xy.thinking.machine;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;

public class ThinkingGraphVisited {
	class VisitRecord {
		private ThinkingGraphNode node;
		private long timestamp;
		public VisitRecord(ThinkingGraphNode n) {
			updateTimestamp();
			node = n;
		}
		public long getTimestamp() {
			return timestamp;
		}
		public void setTimestamp(long timestamp) {
			this.timestamp = timestamp;
		}
		public ThinkingGraphNode getNode() {
			return node;
		}
		public void setNode(ThinkingGraphNode node) {
			this.node = node;
		}
		public void updateTimestamp() {
			timestamp = System.currentTimeMillis();
			
		}
	}
	private HashMap<String, VisitRecord> records = new HashMap<String, VisitRecord>();
	private LinkedList<VisitRecord> nodeq = new LinkedList<VisitRecord>();
	
	public boolean contains(ThinkingGraphNode node) {
		if (records.containsKey(node.getCode())) return true;
		return false;
	}
	public void add(ThinkingGraphNode node) {
		records.put(node.getCode(), new VisitRecord(node));
		nodeq.offer(records.get(node.getCode()));
	}
	public int size() {
		return nodeq.size();
	}
	public ThinkingGraphNode pop() {
		VisitRecord rec = nodeq.poll();
		if (rec != null) {
			if (records.containsKey(rec.getNode().getCode()))
				records.remove(rec.getNode().getCode());
			return rec.getNode();
		}
		return null;
	}
}
