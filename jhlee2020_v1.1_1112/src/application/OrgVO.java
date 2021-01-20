package application;

public class OrgVO {
	private long idx;
	private String key;
	private String val;
	private String grp;

	public OrgVO(long idx, String key, String val) {
		this.idx = idx;
		this.key = key;
		this.val = val;
	}

	public OrgVO(String grp, long idx, String key, String val) {
		this.grp = grp;
		this.idx = idx;
		this.key = key;
		this.val = val;
	}

	public long getIdx() {
		return idx;
	}

	public void setIdx(long idx) {
		this.idx = idx;
	}

	public String getKey() {
		return key;
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

	public String getGrp() {
		return grp;
	}

	public void setGrp(String grp) {
		this.grp = grp;
	}

	public String toString() {
		return "{idx:"+idx+", key:"+key+", val:"+val+", grp:"+grp+"}";
	}
	
}
