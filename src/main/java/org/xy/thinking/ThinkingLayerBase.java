package org.xy.thinking;

import org.xy.model.ThinkingResult;
import org.xy.thinking.mem.MemoryWrapper;

public abstract class ThinkingLayerBase {
	public String getId() {
		return "DEFAULT";
	}

	public ThinkingResult think(MemoryWrapper dsmInput, ThinkingResult result, String sceneCode, String stageCode, String codes) throws Exception {
		return null;
	}
	
	public boolean canExit(MemoryWrapper dsmInput, ThinkingResult result) {
		return false;
	}
}
