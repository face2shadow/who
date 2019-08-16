package org.xy.thinking.machine;

import java.util.ArrayList;
import java.util.List;

public class Teleport {

	public static final String SYSTEM_NAME ="TELEPORT";
	private ContextManagement ctxMgmt = new ContextManagement();
	private List<TeleGate> gates = new ArrayList<TeleGate>();
	private String output = "";
	public void init() {
		gates.add(new SentenceClassification());
		gates.add(new PurposeDetection());
		gates.add(new EmotionDetection());
		gates.add(new SceneStrategy());
	}
	public void send(String msg) {
		for (TeleGate gate : gates) {
			gate.send(this,  msg);
		}
	}
	public ContextManagement getCtxMgmt() {
		return ctxMgmt;
	}
	public void setCtxMgmt(ContextManagement ctxMgmt) {
		this.ctxMgmt = ctxMgmt;
	}
	public List<TeleGate> getGates() {
		return gates;
	}
	public void setGates(List<TeleGate> gates) {
		this.gates = gates;
	}
	public String getOutput() {
		return output;
	}
	public void setOutput(String output) {
		this.output = output;
	}
}
