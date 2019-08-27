package org.xy.thinking.db.dao;

import org.xy.thinking.db.model.Case;

public interface CaseDao {

	public Case selectByCode(String code);
}
