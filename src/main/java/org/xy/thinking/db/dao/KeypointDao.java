package org.xy.thinking.db.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.xy.thinking.db.model.Keypoint;

public interface KeypointDao {
	public List<Keypoint> selectByCaseSectionCode(@Param("caseCode") String caseCode, @Param("sectionCode")String sectionCode);
}
