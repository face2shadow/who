package org.xy.thinking.db.dao;

import org.xy.thinking.db.model.KDict;
import org.xy.thinking.db.model.KNode;

public interface KDictDao {
	public KDict selectById(Integer id);
	public KDict selectByCode(String code);
	public boolean isCodeExists(String code);
	public boolean insert(KNode node);
	public boolean update(KNode node);
}
