package application;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class ChkVO {
	private BooleanProperty checked = new SimpleBooleanProperty();
	private String txt;
	private String idx;
	private String val;

	public ChkVO() {
	}

	public ChkVO(String txt, String idx, String val, boolean checked) {
		this.txt = txt;
		this.idx = idx;
		this.val = val;
		this.checked.set(checked);
	}
	public String getTxt() {
		return txt;
	}
	public void setTxt(String txt) {
		this.txt = txt;
	}
	public String getIdx() {
		return idx;
	}
	public void setIdx(String idx) {
		this.idx = idx;
	}
	public String getVal() {
		return val;
	}
	public void setVal(String val) {
		this.val = val;
	}
	public BooleanProperty getChecked() {
		return checked;
	}
	public void setChecked(BooleanProperty checked) {
		this.checked = checked;
	}
	public String toString() {
		return "{txt:"+txt+", idx:"+idx+", val:"+val+", checked:"+checked.get()+"}";
	}
	
}
