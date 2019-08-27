package org.xy.thinking.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xy.thinking.db.dao.AnswerDao;
import org.xy.thinking.db.dao.CaseDao;
import org.xy.thinking.db.dao.CaseSectionDao;
import org.xy.thinking.db.dao.KDictDao;
import org.xy.thinking.db.dao.KLinkDao;
import org.xy.thinking.db.dao.KNodeDao;
import org.xy.thinking.db.dao.KeypointDao;
import org.xy.thinking.db.model.Answer;
import org.xy.thinking.db.model.Case;
import org.xy.thinking.db.model.CaseSection;
import org.xy.thinking.db.model.KDict;
import org.xy.thinking.db.model.KLink;
import org.xy.thinking.db.model.KNode;
import org.xy.thinking.service.ThinkingDatabaseService;

@Service
public class ThinkingDatabaseServiceImpl implements ThinkingDatabaseService {
	@Autowired
	private KDictDao dictDao;
	@Autowired
	private KNodeDao nodeDao;
	@Autowired
	private KLinkDao linkDao;
	@Override
	public KDict selectDictById(int id) {
		return dictDao.selectById(id);
	}
	@Override
	public KDict selectDictByCode(String code) {
		return dictDao.selectByCode(code);
	}
	@Override
	public KNode selectNodeByCode(String code) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean insertOrUpdate(KNode node) {
		KNode oldNode = nodeDao.selectByDigest(node);//add type
		if (oldNode != null) {
			node.setId(oldNode.getId());
			nodeDao.update(node);
		} else {
			nodeDao.insert(node);
		}
		return true;
	}
	@Override
	public boolean link(KNode fromNode, KNode toNode) {
		KLink l = new KLink();
		l.setFrom(fromNode.getId());
		l.setTo(toNode.getId());
		l.setType("DKD");
		KLink oldLink = linkDao.selectByFromTo(l);
		if (oldLink == null) {
			linkDao.insert(l);
		}
		return false;
	}

}
