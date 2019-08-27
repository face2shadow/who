package org.xy.thinking.db.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.xy.thinking.db.model.Answer;

public interface AnswerDao {
	public List<Answer> selectByCaseSectionCode(@Param("caseCode") String caseCode, @Param("sectionCode") String sectionCode);
}
