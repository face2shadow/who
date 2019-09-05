package org.xy.thinking.db.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.xy.thinking.db.model.KDict;
import org.xy.thinking.db.model.KNode;

public interface KDictDao {
	@Select(" SELECT * FROM k_dict WHERE idk_dict = #{id}  ")
	@Results({
		@Result(property = "id", column = "id"),
		@Result(property = "type", column = "d_type"),
		@Result(property = "code", column = "d_code"),
		@Result(property = "name", column = "d_name")
	})
	public KDict selectById(Integer id);
	

	@Select("SELECT * FROM k_dict WHERE d_code = #{code}  ")
	@Results({
		@Result(property = "id", column = "id"),
		@Result(property = "type", column = "d_type"),
		@Result(property = "code", column = "d_code"),
		@Result(property = "name", column = "d_name")
	})
	public KDict selectByCode(String code);
//	public boolean isCodeExists(String code);
//	public boolean insert(KNode node);
//	public boolean update(KNode node);
}
