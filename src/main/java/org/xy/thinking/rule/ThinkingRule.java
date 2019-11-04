package org.xy.thinking.rule;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xy.model.KBRuleUnknownData;
import org.xy.model.ResultEnum;
import org.xy.thinking.mem.DSMData;
import org.xy.thinking.mem.MemoryWrapper;


public class ThinkingRule extends ThinkingUnit {
	private ThinkingUnit root = null;
	@Override 
	public int getHeightValue() {
		return 1;
	}
	@Override 
	public int getHeightDifference() {
		return 0;
	}
	@Override 
	public String toString() {
		return "("+getRoot().toString()+")";
	}
	private ThinkingUnit getRoot() {
		if (root != null) return root;
		return root;
	}

	@Override
	public KBRuleUnknownData estimate(MemoryWrapper mem,String prefix, int deepth) throws Exception {
		ThinkingUnit root = getRoot();
		if (root != null) {
			KBRuleUnknownData data = root.estimate(mem, prefix, deepth); //BUG solved
			if (data.getData().startsWith("!")) {
				data.setData(data.getData().substring(1));
			}
			data.setRankValue((double) root.getKnownFacts() / (double) getTotalFacts());
			return data;
		}
		return null;
	}

	public ResultEnum eval(MemoryWrapper mem) throws Exception {
		return eval(mem, "_");
	}
	@Override
	public ResultEnum eval(MemoryWrapper mem, String prefix) throws Exception {
		if (prefix == null) prefix = "_";
		

		ThinkingUnit root = getRoot();
		if (root != null) {
			String id = root.getIdString();
			if ("true".equals(id)) {
				setResult(ResultEnum.Positive);
				return getResult();
			} else if ("false".equals(id)) {
				setResult(ResultEnum.Negative);
				return getResult();
			}
			DSMData data = mem.getData(root.getIdString());
			if (data != null) {
				setResult(ResultEnum.parse(data.getFlag()));
				this.setKnownFacts(root.getKnownFacts());
			} else {
				setResult(root.eval(mem, prefix));
				this.setKnownFacts(0);
				if (ResultEnum.isNegative(getResult()) || ResultEnum.isPositive(getResult())){
					mem.putTempData(root.getIdString(), "", "", getResult().toString());
				}
			}
		} 
		return getResult();
	}
	@Override
	public void dumps(ThinkingUnit parent, String prefix) {
		debug(String.format("%s %s %s %s %s", prefix, "EXPR", "", getIdString(), this.getResult().toString()));
		ThinkingUnit root = getRoot();
		if (root != null)
			root.dumps(root, prefix + "\t");
	}

	private String nextClosure(String rule, int start, int n) {
		String rule1 = "";
		int j = n;
		for (int i = start; i < rule.length(); i++) {
			char c = rule.charAt(i);
			rule1 = rule1 + rule.charAt(i);
			if (c == '(')
				j = j - 1;
			if (c == ')')
				j = j + 1;
			if (j == 0) {
				rule1 = rule1.substring(0, rule1.length() - 1);
				break;
			}
		}
		return rule1;
	}
	private void addNode(ThinkingUnit node) {
		if (root == null) {
			root = node;
			//currentNode = node;
			return;
		} 
		node.setLeft(root.getRight());
		root.setRight(node);
	
		//System.out.println("Dif:"+root.getHeightDifference());
		if (root.getHeightDifference()<0) {
			ThinkingUnit tmp = root.getRight();
			root.setRight(tmp.getLeft());
			tmp.setLeft(root);
			root = tmp;
			tmp.setParent(null);
		}
	}
	private ThinkingUnit buildNode(ThinkingUnit current, ThinkingUnit node) {
		if (current instanceof ThinkingFact ||  (current instanceof ThinkingRule) ||  (current instanceof ThinkingFunc)) {
			if ((node instanceof ThinkingAnd) ||  (node instanceof ThinkingOr) ) {
				if (node.getLeft() == null) {
					node.setLeft(current);
				} 
//				if (current.getParent() == null) {
//					addLeft(node, current);
//					//root = node;
//					//node.setLeft(current);
//					//current.setParent(node);
//				} else {
//					addLeft(node, current.getParent());
//					
//					//node.setLeft(current.getParent());
//					//current.getParent().setParent(node);
//				}
				
				return node;
			} else {
				
			}
		} else {
			//addRight(current,node);
			current.setRight(node);
			//node.setParent(current);
			addNode(current);
			return null;
		}
		System.out.println("Cant reach here");
		return null;
	}
	public void parse(String rule) throws Exception {
		//rule = rule + " and true";
		HashSet<String> reserved_words = new HashSet<String>();
		ThinkMethod mobj = new ThinkMethod();
		for (Method m : mobj.getClass().getMethods()) {
			if (m.isAnnotationPresent(ThinkFunc.class)) {
				ThinkFunc tf = m.getAnnotation(ThinkFunc.class);
				//if ("evaluate".equals(tf.domain())) {
					reserved_words.add(tf.name());
					reserved_words.add("!"+tf.name());
				//}
			}
		}
		String regEx = "(and)|(or)|(\\!?[a-z0-9A-Z\\_,']+)|(\\s+)|(\\!?\\()|(\\))";
		int start = 0;
		Pattern pattern = Pattern.compile(regEx);
		Matcher matcher = pattern.matcher(rule);
		matcher.useTransparentBounds(true).useAnchoringBounds(false);
		ThinkingUnit prev = null;
		while (start < rule.length()) {
			matcher.region(start, rule.length());
			if (matcher.lookingAt() == false) {
				try {
				start = matcher.end();				
				continue;
				} catch (IllegalStateException exp) {
					throw new Exception("Invalid rule "+rule);
				}
			}
			if (matcher.start() < 0) continue;
			start = matcher.end();

			for (int i = 0; i < matcher.groupCount(); i++) {
				String vname = matcher.group(i + 1);
				if (vname != null) {
					ThinkingUnit n = null;
					switch (i) {
					case 0: {
						ThinkingAnd fact = new ThinkingAnd();
						//addExpr(fact);
						n = fact;
					}
						break;
					case 1: {
						ThinkingOr fact = new ThinkingOr();
						//addExpr(fact);
						n = fact;
					}
						break;
					case 2: {

						if (reserved_words.contains(vname)) {
							ThinkingFunc func = new ThinkingFunc(vname);
							String rule1 = nextClosure(rule, start, 0);
							if (rule1.contains(":") && rule1.split(":").length > 1) {
								String numberOfFact = rule1.split(":")[1];
								int numberOfFact_int = 1;
								
								try { numberOfFact_int = Integer.parseInt(numberOfFact); } catch(NumberFormatException e) {numberOfFact_int = 1;}
								setTotalFacts(getTotalFacts() + numberOfFact_int);
							}
							func.setParameters(rule1.substring(1));
							//addExpr(func);
							start = start + rule1.length(); // 左括号要计算在内
							n = func;
						} else {
							ThinkingFact fact = new ThinkingFact();
							setTotalFacts(getTotalFacts() + 1);
							fact.setValue(vname);
							n = fact;
						}
					}
						break;
					case 4: {
						String rule1 = nextClosure(rule, start, -1);
						ThinkingRule fact = new ThinkingRule();
						fact.parse(rule1);
						setTotalFacts(getTotalFacts()+fact.getTotalFacts());
						start = start + rule1.length();
						//addExpr(fact);
						n = fact;
					}
					break;
					}

					if (prev == null) {
						prev = n;
					} else if (n != null){	
						prev = buildNode(prev, n);						
					}
				}
			}
		}
		if (prev != null) {
			addNode(prev);
		}
	}
}
