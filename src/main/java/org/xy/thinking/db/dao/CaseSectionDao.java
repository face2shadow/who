package org.xy.thinking.db.dao;


import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.xy.thinking.db.model.CaseSection;

public interface CaseSectionDao {
	public List<CaseSection> selectByCaseCode(String code);
}