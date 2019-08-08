package org.xy.model;

import java.util.ArrayList;

@SuppressWarnings("serial")
public class ActionList extends ArrayList<Action> {
/*
	public boolean lastActionIsAsk() {
    	if (isEmpty() == false) {
    		Action act = get(size()-1);
    		if (act.getHead().compareToIgnoreCase("ASK")==0) {
    			return true;
    		}
    	}
    	return false;
	}*/
	@Override
	public boolean add(Action act) {
		//if (lastActionIsAsk() == true) return false;
		return super.add(act);
	}
	public void keepLastOne() {
		if (this.size()>1) {
			this.removeRange(0, this.size()-1);
		}
	}
}
