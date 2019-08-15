package org.xy.thinking.service;

import org.xy.thinking.db.model.KDict;
import org.xy.thinking.db.model.KNode;


public interface ThinkingDatabaseService {
	public KDict selectDictById(int id);
	public KDict selectDictByCode(String code);
	public KNode selectNodeByCode(String code);
	public boolean insertOrUpdate(KNode node) ;
	public boolean link(KNode fromNode, KNode toNode) ;
}
