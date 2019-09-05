package org.xy.model;

import org.xy.thinking.mem.DSMData;
import org.xy.thinking.mem.MemoryWrapper;

public class KBFunction {

	public static String match0(MemoryWrapper mem,String parameters) throws Exception {
		String parts[] = parameters.split(":");
		if (parts.length < 2) {
			return null;
		}
		String para1 = parts[0];
		String para2 = parts[1];
		if (para1.startsWith("'") || para1.startsWith("\""))
			para1 = para1.substring(1, para1.length()-1);
		String wait_4_count[] = para1.split("-");
		int count = 0;
		int sure_count = 0;
		for (String w : wait_4_count) {
			String key = w;
			boolean reverse = false;
			if (key.startsWith("!")) {
				reverse = true;
				key = key.substring(1, key.length());
			}

			DSMData data = mem.getData("_"+key) == null ? mem.getData(key) : mem.getData("_"+key);
			if (data == null) return w;
			
			if (ResultEnum.isSystemDontKnow(data.getResult())) return w;
				
		}
		return null;
	}
	public static String diag_exists0(MemoryWrapper mem,String parameters) throws Exception {
		String para1 = parameters;
		if (para1.startsWith("'") || para1.startsWith("\""))
			para1 = para1.substring(1, para1.length()-1);
		DSMData data = mem.getData(para1);
		if (data == null || ResultEnum.isSystemDontKnow(data.getResult())) return para1;
		return null;
	}
	public static ResultEnum diag_exists(MemoryWrapper mem,String parameters) throws Exception{
		String para1 = parameters;
		if (para1.startsWith("'") || para1.startsWith("\""))
			para1 = para1.substring(1, para1.length()-1);
		
		
		if (mem.hasKeyStartsWith("C")||mem.hasKeyStartsWith("T")) {
			return ResultEnum.Positive;
		}
		return ResultEnum.SystemDontKnow;			
	}
	public static ResultEnum match(MemoryWrapper mem,String parameters) throws Exception {
		String parts[] = parameters.split(":");
		if (parts.length < 2) {
			return ResultEnum.SystemDontKnow;
		}
		String para1 = parts[0];
		String para2 = parts[1];
		if (para1.startsWith("'") || para1.startsWith("\""))
			para1 = para1.substring(1, para1.length()-1);
		String wait_4_count[] = para1.split("-");
		int count = 0;
		int sure_count = 0;
		for (String w : wait_4_count) {
			String key = w;
			boolean reverse = false;
			if (key.startsWith("!")) {
				reverse = true;
				key = key.substring(1, key.length());
			}

			DSMData data = mem.getData("_"+key) == null ? mem.getData(key) : mem.getData("_"+key);

			if (data != null) {
				if (ResultEnum.isPositive(data.getResult()) || ResultEnum.isNegative(data.getResult()) || ResultEnum.isUserDontKnow(data.getResult())) {
					sure_count ++;
				}
				if (!reverse && ResultEnum.isPositive(data.getResult())) {
					count = count + 1;					
				}if (reverse && ResultEnum.isNegative(data.getResult())) {
					count = count + 1;
				}
			}
		}
		
		if (count >= Integer.parseInt(para2)) {
			return ResultEnum.Positive;
		}
		if (sure_count >= wait_4_count.length) {
			return ResultEnum.Negative;
		}
		return ResultEnum.SystemDontKnow;

	}
}
