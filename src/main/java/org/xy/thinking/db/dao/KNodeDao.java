package org.xy.thinking.db.dao;

import org.apache.ibatis.annotations.Mapper;
import org.xy.thinking.db.model.KNode;

public interface KNodeDao {
	public KNode selectById(int id);
	public KNode selectByCode(KNode node);
	public KNode selectByDigest(KNode node);
	public void insert(KNode node);
	public void update(KNode node);
}
