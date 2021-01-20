package application;

public class ResData {
	
	String org = "";
	String cng = "";
	String errMsg = "";
	String cngMsg = "";
	String typ = "";
	
	public String getTyp() {
		return typ;
	}
	public void setTyp(String typ) {
		this.typ = typ;
	}
	public String getOrg() {
		return org;
	}
	public void setOrg(String org) {
		this.org = org;
	}
	public String getCng() {
		return cng;
	}
	public void setCng(String cng) {
		this.cng = cng;
	}
	public String getErrMsg() {
		return errMsg;
	}
	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}
	public String getCngMsg() {
		return cngMsg;
	}
	public void setCngMsg(String cngMsg) {
		this.cngMsg = cngMsg;
	}
	public String toString() {
		return "{org:"+org+", cng:"+cng+", cngMsg:"+cngMsg+", errMsg:"+errMsg+"}";
	}
	
}
