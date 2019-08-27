package org.xy.thinking.db.dao;

import org.apache.ibatis.annotations.Mapper;
import org.xy.thinking.db.model.KLink;

public interface KLinkDao {
	public KLink selectById(int id);
	public KLink selectByFromTo(KLink link);
	public void insert(KLink link);
	public void update(KLink link);
}
