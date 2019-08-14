package org.xy.thinking.service;

import org.xy.thinking.db.model.KDict;


public interface ThinkingDatabaseService {
	public KDict selectById(int id);
}
