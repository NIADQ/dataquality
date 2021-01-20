package application;

import java.util.List;

public class ResInspect {
	private List<String[]> oriList;
	private List<String[]> cngList;
	private List<String[]> msgCngList;
	private List<String[]> msgErrList;
	private List<String[]> typList;
	
	public List<String[]> getOriList() {
		return oriList;
	}
	public void setOriList(List<String[]> oriList) {
		this.oriList = oriList;
	}
	public List<String[]> getCngList() {
		return cngList;
	}
	public void setCngList(List<String[]> cngList) {
		this.cngList = cngList;
	}
	public List<String[]> getMsgCngList() {
		return msgCngList;
	}
	public void setMsgCngList(List<String[]> msgCngList) {
		this.msgCngList = msgCngList;
	}
	public List<String[]> getMsgErrList() {
		return msgErrList;
	}
	public void setMsgErrList(List<String[]> msgErrList) {
		this.msgErrList = msgErrList;
	}
	public List<String[]> getTypList() {
		return typList;
	}
	public void setTypList(List<String[]> typList) {
		this.typList = typList;
	}
	
}
