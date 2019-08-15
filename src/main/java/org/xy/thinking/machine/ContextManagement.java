package org.xy.thinking.machine;

import java.util.LinkedList;
import java.util.List;

public class ContextManagement {
	private List<Context> contexts = new LinkedList<Context>();
	private static int MAX_CONTEXT_SIZE = 10;
	public ContextManagement() {
		push(new Context());
	}
	public void push(Context ctx) {
		if (contexts.contains(ctx)) {
			contexts.remove(ctx);
		}
		
		contexts.add(ctx);
		if (contexts.size() > MAX_CONTEXT_SIZE) {
			contexts.remove(0);
		}
	}
	public Context current() {
		if (contexts.size()>0) {
			return contexts.get(contexts.size()-1);
		}
		return null;
	}
	public Context pop() {
		if (contexts.size()>1) {
			Context ctx = contexts.get(contexts.size()-1);
			contexts.remove(ctx);
			return ctx;
		} else {
			if (contexts.size()>0) return contexts.get(0);
		}
		return null;
	}
}
