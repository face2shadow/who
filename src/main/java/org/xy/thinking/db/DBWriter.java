package org.xy.thinking.db;

import org.xy.model.KBSection;
import org.xy.thinking.def.KBDefinitionMap;
import org.xy.thinking.def.KBLoader;

public class DBWriter {
	public void saveToDB() {
		KBDefinitionMap map = KBLoader.getDefinitions();
		
		for (String key: map.keySet()) {
			KBSection section = map.getByKey(key);
		}
	}
}
