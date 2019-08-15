package org.xy.thinking.machine;

import java.util.LinkedList;
import java.util.List;

import org.xy.thinking.ThinkingBrain;
import org.xy.thinking.ThinkingLayerBase;
import org.xy.thinking.db.model.KNode;

public class SceneStrategy  implements TeleGate {
	
	
	public static final String CURRENT_SCENE ="CURRENT_SCENE";
	

	private List<ThinkingLayerBase> scenes = new LinkedList<ThinkingLayerBase>();
	private static int MAX_CONTEXT_SIZE = 10;
	public SceneStrategy() {
		push(ThinkingBrain.getInstance());
	}
	public void push(ThinkingLayerBase ctx) {
		if (scenes.contains(ctx)) {
			scenes.remove(ctx);
		}
		
		scenes.add(ctx);
		if (scenes.size() > MAX_CONTEXT_SIZE) {
			scenes.remove(0);
		}
	}
	public ThinkingLayerBase current() {
		if (scenes.size()>0) {
			return scenes.get(scenes.size()-1);
		}
		return null;
	}
	public ThinkingLayerBase pop() {
		if (scenes.size()>1) {
			ThinkingLayerBase ctx = scenes.get(scenes.size()-1);
			scenes.remove(ctx);
			return ctx;
		} else {
			if (scenes.size()>0) return scenes.get(0);
		}
		return null;
	}
	@Override
	public void send(Teleport tele, String msg) {
		tele.getCtxMgmt().current();
		KNode node = new KNode();
		node.setCode(CURRENT_SCENE);
		node.setCaseId(0);
		node.setType(tele.SYSTEM_NAME);
		node.setValue(current().getId());
	}
}
