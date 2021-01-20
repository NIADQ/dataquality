package application;

import java.util.ArrayList;
import java.util.List;

public class ItemVO {
	
	private int seq = 0;
	private String key = "";
	private String key2 = "";
	private String val = "";
	private String node = ""; // 선택된 컬럼 값
	private String[] nodeRow = null; // 선택된 컬럼의 Row 데이터 
	private String opt1 = ""; // 진단 값1
	private String opt2 = ""; // 진단 값2
	private String opt3 = ""; // 진단 값3
	private List<String> optList = new ArrayList<String>(); // 진단 값 목록
	
	public String getKey() {
		return key;
	}
	public String[] getNodeRow() {
		return nodeRow;
	}
	public void setNodeRow(String[] nodeRow) {
		this.nodeRow = nodeRow;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getVal() {
		return val;
	}
	public void setVal(String val) {
		this.val = val;
	}
	public String getKey2() {
		return key2;
	}
	public void setKey2(String key2) {
		this.key2 = key2;
	}
	public int getSeq() {
		return seq;
	}
	public void setSeq(int seq) {
		this.seq = seq;
	}
	public String getOpt1() {
		return opt1;
	}
	public void setOpt1(String opt1) {
		this.opt1 = opt1;
	}
	public String getOpt2() {
		return opt2;
	}
	public void setOpt2(String opt2) {
		this.opt2 = opt2;
	}
	public String getOpt3() {
		return opt3;
	}
	public void setOpt3(String opt3) {
		this.opt3 = opt3;
	}
	public List<String> getOptList() {
		return optList;
	}
	public void setOptList(List<String> optList) {
		this.optList = optList;
	}
	public String getNode() {
		return node;
	}
	public void setNode(String node) {
		this.node = node;
	}
	public String toString() { // Main Controller 의 setCellValueFactory 에서 toString 으로 텍스트를 조회하고 있어 유지...
		return getVal();
	}
	public String toStringAll() {
		return "{seq:"+seq+", key:"+key+", key2:"+key2+", val:"+val+", node:"+node+", nodeRow:"+(nodeRow == null ? null : nodeRow.toString())+", opt1:"+opt1+", opt2:"+opt2+", opt3:"+opt3+", optList:"+optList.toString()+"}";
	}
	
}
