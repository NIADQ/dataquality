package application.chk;

import application.ItemVO;
import application.ResData;
import application.chk.comm.ChkAttribute;

public class ChkString implements ChkAttribute {
	
	@Override
	public ResData getChkResData(ItemVO itemVO) {
		// TODO Auto-generated method stub
		return chkItem(itemVO);
	}

	private ResData chkItem(ItemVO itemVO) {
		ResData resData = new ResData();
		resData.setOrg(itemVO.getNode());
		return resData;
	}
	
}
