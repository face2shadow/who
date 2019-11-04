package org.xy.thinking.rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xy.model.KBRuleUnknownData;
import org.xy.model.ResultEnum;
import org.xy.thinking.mem.MemoryWrapper;



public abstract class ThinkingUnit  {
    private static final Logger log = LoggerFactory.getLogger(ThinkingUnit.class);
	private ThinkingUnit parent, left, right;

	private int totalFacts, knownFacts;
	private String value;
	public int getHeightValue() {
		int leftV = left!=null?left.getHeightValue():0;
		int rightV = right!=null?right.getHeightValue():0;
		return 1 + Math.max(leftV, rightV);
	}
	public int getHeightDifference() {
		int leftV = left!=null?left.getHeightValue():0;
		int rightV = right!=null?right.getHeightValue():0;
		return leftV - rightV;
	}
	private ResultEnum result = ResultEnum.SystemDontKnow;
	public ThinkingUnit() {
		value = null;
	}

	public String getIdString() {
		String s = toString();
		/*if (s == null) return "";
		List<String> arr = new ArrayList<String>();
		for (String tmp: s.split(" ")) {
			arr.add(tmp);
		}
		Collections.sort(arr);
		s = String.join(" ", arr);*/
		return s;
	}
	public String toString() {
		return getValue();
	}
	public void debug(String str) {
		log.debug(str);
	}

	public KBRuleUnknownData estimate(MemoryWrapper mem,String prefix,int deepth) throws Exception {
		return null;
	}
	public ResultEnum eval(MemoryWrapper mem, String prefix) throws Exception {
		return result;
	}
	public ThinkingUnit getParent() {
		return parent;
	}
	public void setParent(ThinkingUnit parent) {
		this.parent = parent;
	}
	public ThinkingUnit getLeft() {
		return left;
	}
	public void setLeft(ThinkingUnit left) {
		this.left = left;
		left.parent = this;
	}
	public ThinkingUnit getRight() {
		return right;
	}
	public void setRight(ThinkingUnit right) {
		this.right = right;
		right.parent = this;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

	public ResultEnum getResult() {
		return result;
	}
	public void setResult(ResultEnum result) {
		this.result = result;
	}	
	public void dumps(ThinkingUnit parent, String prefix) {
		
	}
	public int getTotalFacts() {
		return totalFacts;
	}
	public void setTotalFacts(int totalFacts) {
		this.totalFacts = totalFacts;
	}
	public int getKnownFacts() {
		return knownFacts;
	}
	public void setKnownFacts(int knownFacts) {
		this.knownFacts = knownFacts;
	}
	public ThinkingUnit getEstimateSide(String s) {
		ThinkingUnit t = null;
		if ("L".equals(s)) {
			t = getLeft();
		} else if ("R".equals(s)) {
			t = getRight();
		} else if ("F".equals(s)){
			t = getLeft();
		}
		return t;
	}
}