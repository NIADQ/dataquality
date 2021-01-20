package application.chk;

import application.ItemVO;
import application.ResData;
import application.base.Const;
import application.chk.comm.ChkAttribute;

public class ChkFlag implements ChkAttribute {
	
	//정상패턴
	private static final String[][] regexs = {
			{"Y", ".*([yY]{1,}).*"},
			{"N", ".*([nN]{1,}).*"}
	};

	@Override
	public ResData getChkResData(ItemVO itemVO) {
		// TODO Auto-generated method stub
		if ((Const.CHK_FLAG + "-1").equals(itemVO.getKey2())) return chkItem(itemVO);
		return chkItem2(itemVO);
	}

	private ResData chkItem(ItemVO itemVO) {
		ResData resData = new ResData();
		boolean isCheck = false;
		String orgData = itemVO.getNode();
		String compData = "";
		String cngData = "";
		String errMsg = "";
		String cngMsg =""; 

		try {
			if (orgData.trim().length() == 0) {
				// 정상
				isCheck = true;
			} else {
				//정상 패턴 확인
				for (String[] reg : regexs) {
					if (orgData.matches(reg[1])) {
						compData = reg[0];
						if (orgData.toUpperCase().equals(compData)) {
							// 정상
							isCheck = true;
						} else {
							cngMsg = "여부_값 변경::" + orgData + "::" + compData;
						}
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (!isCheck || !cngMsg.isEmpty()) {
				errMsg = "여부_판단불가";
			}
		}
		
		resData.setOrg(itemVO.getNode());
		resData.setCng(cngData);
		resData.setErrMsg(errMsg);
		resData.setCngMsg(cngMsg);
		
		//System.out.println("###resData:"+resData.toString());
		return resData;
	}
	
	private ResData chkItem2(ItemVO itemVO) {
		ResData resData = new ResData();
		boolean isCheck = false;
		String orgData = itemVO.getNode();
		String cngData = "";
		String errMsg = "";
		String cngMsg =""; 

		try {
			if (orgData.trim().length() == 0) {
				// 정상
				isCheck = true;
			} else {
				//정상 패턴 확인
				if (orgData.toUpperCase().equals(itemVO.getOpt1().toUpperCase()) || orgData.toUpperCase().equals(itemVO.getOpt2().toUpperCase())) {
					// 정상
					isCheck = true;
				} else {
					cngMsg = "여부_판단불가::" + orgData + "::" + itemVO.getOpt1()+"/"+itemVO.getOpt2();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (!isCheck || !cngMsg.isEmpty()) {
				errMsg = "여부_판단불가";
			}
		}
		
		resData.setOrg(itemVO.getNode());
		resData.setCng(cngData);
		resData.setErrMsg(errMsg);
		resData.setCngMsg(cngMsg);
		
		//System.out.println("###resData:"+resData.toString());
		return resData;
	}

}
