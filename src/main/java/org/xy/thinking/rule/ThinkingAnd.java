package org.xy.thinking.rule;

import java.util.HashMap;

import org.xy.model.KBRuleUnknownData;
import org.xy.model.ResultEnum;
import org.xy.thinking.mem.DSMData;
import org.xy.thinking.mem.MemoryWrapper;



public class ThinkingAnd extends ThinkingOr {
//	@Override 
//	public String getIdString() {
//		String s = toString();
//		if (s == null) return "";
//		List<String> arr = new ArrayList<String>();
//		for (String tmp: s.split(" ")) {
//			arr.add(tmp);
//		}
//		Collections.sort(arr);
//		return String.join(" ", arr);
//	}
	@Override
	public String toString() {
		String s = getLeft() == null ? "?" : getLeft().toString();
		s = s + " and ";
		if (getRight() == null) {
			s += "?";
		} else {
			s += getRight().toString();
		}

		return s;
	}

	@Override
	public ResultEnum eval(MemoryWrapper mem, String prefix) throws Exception {
		DSMData data = mem.getData(getIdString());
		if (data != null) {
			// System.out.println("Hit "+getIdString());
			setResult(ResultEnum.parse(data.getFlag()));
			return getResult();
		}
		HashMap<String, String> tran_tbl = new HashMap<String, String>();
		tran_tbl.put("++", "+");
		tran_tbl.put("+-", "-");
		tran_tbl.put("--", "-");
		tran_tbl.put("-+", "-");
		tran_tbl.put("?+", "?");
		tran_tbl.put("?-", "-");
		tran_tbl.put("-?", "-");
		tran_tbl.put("+?", "?");
		tran_tbl.put("??", "?");
		tran_tbl.put("*+", "?");
		tran_tbl.put("*-", "-");
		tran_tbl.put("+*", "?");
		tran_tbl.put("-*", "-");
		tran_tbl.put("*?", "?");
		tran_tbl.put("?*", "?");
		tran_tbl.put("**", "?");
		ResultEnum a = getLeft().eval(mem, prefix);
		ResultEnum b = getRight().eval(mem, prefix);
		if (a == null || b == null)
			throw new Exception("ASTand a is null or b is null");

		setKnownFacts(getLeft().getKnownFacts() + getRight().getKnownFacts());

		String s = a.toString() + b.toString();
		setResult(ResultEnum.parse(tran_tbl.get(s)));
		if (ResultEnum.isNegative(getResult()) || ResultEnum.isPositive(getResult())){
			mem.putTempData(getIdString(), "", "", getResult().toString());
		}
		// System.out.println("put " + getIdString());
		return getResult();
	}
	
	@Override
	public KBRuleUnknownData estimate(MemoryWrapper mem,String prefix, int deepth) throws Exception {
		HashMap<String, String> tran_tbl = new HashMap<String,String>();
		tran_tbl.put("++","V");
		tran_tbl.put("+-","V");		
		tran_tbl.put("--","V");
		tran_tbl.put("-+","V");		
		tran_tbl.put("?+","L");
		tran_tbl.put("?-","V");		
		tran_tbl.put("-?","V");
		tran_tbl.put("+?","R");
		tran_tbl.put("??","F");

		tran_tbl.put("*+", "L");
		tran_tbl.put("*-", "V");
		tran_tbl.put("+*", "R");
		tran_tbl.put("-*", "V");
		tran_tbl.put("*?", "L");
		tran_tbl.put("?*", "L");
		tran_tbl.put("**", "L");
		
		ResultEnum a = getLeft().eval(mem, prefix);
		ResultEnum b = getRight().eval(mem, prefix);
		if (a == null || b == null)
			throw new Exception("ASTor a is null or b is null");
		String s = a.toString() + b.toString();		
		s = tran_tbl.get(s);
		if ("V".equals(s)) {
			return null;
		}
		ThinkingUnit t = getEstimateSide(s);
		if (t == null) return null;
		if (t instanceof ThinkingFact) {
			return t.estimate(mem, prefix, deepth);
		} else {
			return t.estimate(mem, prefix,deepth+1);
		}
	}
}
