package org.xy.thinking.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
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
import org.xy.thinking.db.dao.QuestionLinkDao;
import org.xy.thinking.db.model.Answer;
import org.xy.thinking.db.model.Case;
import org.xy.thinking.db.model.CaseSection;
import org.xy.thinking.db.model.KDict;
import org.xy.thinking.db.model.KLink;
import org.xy.thinking.db.model.KNode;
import org.xy.thinking.db.model.QuestionLink;
import org.xy.thinking.service.CaseDatabaseService;
import org.xy.thinking.service.ThinkingDatabaseService;

@Service
public class CaseDatabaseServiceImpl implements CaseDatabaseService {

	@Autowired
	CaseDao caseDao;
	@Autowired
	CaseSectionDao sectionDao;
	@Autowired
	KeypointDao keypointDao;
	@Autowired
	AnswerDao answerDao;
	@Autowired
	QuestionLinkDao questionLinkDao;
	public String readCase(String caseCode) {
		StringBuilder content = new StringBuilder();
		content.append("DKD|,^|SCE|2.0\n");
				
		Case c = caseDao.selectByCode(caseCode);
		if (c == null) return "";
		List<CaseSection> sections = sectionDao.selectByCaseCode(caseCode);
		content.append(String.format("SCE|%s|%s|level~0\n", c.getCode(),c.getName()));
		if (sections == null) {
			return "";
		}
		for (CaseSection sec : sections) {
			List<Answer> answers = answerDao.selectByCaseSectionCode(caseCode, sec.getCode());
			
			for (Answer ans : answers) {
				content.append(String.format(
						"GO|%s|%s|act~%s,rule~user_say('%s')\n", 
						ans.getQuestionCode(),
						ans.getQuestionText(),
						ans.getAnswerCode(),
						ans.getQuestionPattern()));
			}
			for (Answer ans : answers) {
				content.append(String.format(
						"ACT|%s|NA|answer~'%s',question~%s,point~'%s'\n", 
						ans.getAnswerCode(),
						ans.getAnswerText(),
						ans.getQuestionCode(),
						ans.getKeypoints().replace(",", " ")));
			}
			HashMap<String, String> fromTos = new HashMap<String, String>();

			List<QuestionLink> links = questionLinkDao.selectByCaseSectionCode(caseCode, sec.getCode());
			
			for (QuestionLink l: links) {
				if (fromTos.containsKey(l.getQuestionCodeFrom())) {
					String value = fromTos.get(l.getQuestionCodeFrom());
					value = value + ",to~"+l.getQuestionCodeTo();
					fromTos.put(l.getQuestionCodeFrom(), value);
				} else {
					String value = "to~"+l.getQuestionCodeTo();
					fromTos.put(l.getQuestionCodeFrom(), value);
				}				
			}
			for (String key: fromTos.keySet()){

				content.append(String.format(
						"LNK|||from~%s,%s\n", 
						key,
						fromTos.get(key)));		
			}
		}
		return content.toString();
	}

}
