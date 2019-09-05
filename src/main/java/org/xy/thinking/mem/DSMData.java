package org.xy.thinking.mem;

import java.io.Serializable;

import org.xy.model.ResultEnum;

public class DSMData  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String key, name, value;
	private String flag;
	public String toString() {
		return getValue();
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public ResultEnum getResult() throws Exception {
		return ResultEnum.parse(flag);
	}
}