package org.xy.thinking.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xy.thinking.db.dao.KDictDao;
import org.xy.thinking.db.model.KDict;
import org.xy.thinking.service.ThinkingDatabaseService;

@Service
public class ThinkingDatabaseServiceImpl implements ThinkingDatabaseService {
	@Autowired
	private KDictDao dictDao;
	@Override
	public KDict selectById(int id) {
		return dictDao.selectById(id);
	}

}
