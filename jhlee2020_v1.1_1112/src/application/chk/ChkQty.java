package application.chk;

import java.text.DecimalFormat;

import application.ItemVO;
import application.ResData;
import application.chk.comm.ChkAttribute;

public class ChkQty implements ChkAttribute {
	
	public ResData getChkResData(ItemVO itemVO) {
		// TODO Auto-generated method stub
		return chkItem(itemVO);
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
				if ( orgData.matches("^[0-9]{1,}$") ) {
					// 정상
					isCheck = true;
				} else {
					boolean isComma		 = orgData.indexOf(",") > 0; // 콤마형식

					//특수문자 제거
					compData = orgData.replaceAll("[^-\\.\\,0-9]", "");
					
					// 콤마 금액 형식인 경우
					if (isComma && compData.replaceAll("[^0-9]", "").matches("^.*[0-9]{1,}.*$")) {
						cngData = new DecimalFormat("#,###").format(Long.parseLong(compData.replaceAll("[^0-9]", "")));
					}

					if (cngData.isEmpty()) {
						if( orgData.matches(".*[0-9]{1,}.*")) {
							//숫자가 있으면 숫자만 추출
							cngData = compData.replaceAll("[^0-9]", "");
						}
					}
					//[변경메시지] 적용 
					cngMsg = "수량" 
							+ (isComma ? "_콤마" : "")
							+ ":형식변경::" + orgData + "::" + cngData;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (!isCheck || !cngMsg.isEmpty()) {
				errMsg = "수량_작업불가_항목";
			}
		}
		
		resData.setOrg(itemVO.getNode());
		resData.setCng(cngData);
		resData.setErrMsg(errMsg);
		resData.setCngMsg(cngMsg);
		
		return resData;
	}
	
}
