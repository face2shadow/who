package org.xy.model;

import java.io.Serializable;

public enum ResultEnum implements Serializable{
	Positive("+"), Negative("-"), SystemDontKnow("?"), UserDontKnow("*"), UserKeepSilence("x"), SystemShouldKnow("#"), SystemExcluded("~");
	private String value;
	ResultEnum(String s) {
		value = s;
	}
	String getValue() {
		return value;
	}
	public String toString() {
		return value;
	}
	public static boolean isPositive(ResultEnum e) {
		return e == Positive;
	}
	public static boolean isPositive(String s) {
		try {
			ResultEnum r = parse(s);
			return isPositive(r);
		} catch (Exception e) {			
			e.printStackTrace();
		}
		return false;
	}
	public static boolean isNegative(String s) {
		try {
			ResultEnum r = parse(s);
			return isNegative(r);
		} catch (Exception e) {			
			e.printStackTrace();
		}
		return false;
	}
	public static boolean isNegative(ResultEnum e) {
		return e == Negative;
	}
	public static boolean isSystemDontKnow(ResultEnum e) {
		return e == SystemDontKnow;
	}
	public static boolean isUserDontKnow(ResultEnum e) {
		return e == UserDontKnow;
	}
	public static boolean isUserKeepSilence(ResultEnum e) {
		return e == UserKeepSilence;
	}
	public static ResultEnum parse(String s) throws Exception {
		if (s.compareTo("+") == 0) return Positive;
		if (s.compareTo("-") == 0) return Negative;
		if (s.compareTo("?") == 0) return SystemDontKnow;
		if (s.compareTo("*") == 0) return UserDontKnow;
		if (s.compareTo("x") == 0) return UserKeepSilence;
		throw new Exception("Invalid enum value");
	}
}
