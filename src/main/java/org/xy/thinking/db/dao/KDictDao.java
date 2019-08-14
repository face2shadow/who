package org.xy.thinking.db.dao;

import org.apache.ibatis.annotations.Param;
import org.xy.thinking.db.model.KDict;

public interface KDictDao {
	public KDict selectById(Integer id);
}
