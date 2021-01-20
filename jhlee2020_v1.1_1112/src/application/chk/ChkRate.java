package application.chk;

import java.text.DecimalFormat;

import application.ItemVO;
import application.ResData;
import application.chk.comm.ChkAttribute;

public class ChkRate implements ChkAttribute {
	
	//정상패턴
	private static final String[] regexs = {
			"(-?[0-9]{1,}\\.?[0-9]*)" //정상 패턴 : EX : -12.99 , -12 , 12.99, 12 
	};
	
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
				if ( orgData.matches("^" + regexs[0] + "$") ) {
					// 정상
					isCheck = true;
				} else {
					//정상 패턴 아닌 경우 오류패턴 확인
					boolean isPercent	 = orgData.matches(".*[\\%]{1,}.*");			// 퍼센트 여부
					boolean isBracket	 = orgData.matches(".*[\\(\\)\\[\\])]{1,}.*");	// 괄호 여부 (중괄호, 대괄호)
					boolean isBlank		 = orgData.matches(".*[\\s]{1,}.*");			// 공백 여부
					boolean isAsterisk	 = orgData.matches(".*[\\*]{1,}.*");			// 별표 여부
					boolean isDecimal	 = orgData.matches("^\\..*");					// 소수점 오류 여부
					boolean isHyphen	 = orgData.matches(".{1,}\\-.*");				// 하이픈 오류 여부
					boolean isComma		 = orgData.indexOf(",") > 0; // 콤마형식
					
					//특수문자 제거
					compData = orgData.replaceAll("[^-\\.\\,0-9]", "");
					
					// 콤마 금액 형식인 경우
					if (isComma && compData.replaceAll("[^0-9]", "").matches("^.*[0-9]{1,}.*$")) {
						cngData = new DecimalFormat("#,###").format(Long.parseLong(compData.replaceAll("[^0-9]", "")));
					}

					if (cngData.isEmpty()) {
						if( compData.matches("^" + regexs[0] + "$") ) {
							//재시도 해서 맞으면 [변경값] 적용
							cngData = compData;
						} else if (orgData.matches(".*[0-9]{1,}.*")) {
							//재시도 해도 안맞으면 숫자가 있으면 숫자만 추출
							cngData = compData.replaceAll("[^0-9]", "");
						}
					}
					//[변경메시지] 적용 
					cngMsg = "율" 
							+ (isPercent ? "_퍼센트" : "") 
							+ (isBracket ? "_괄호" : "") 
							+ (isBlank ? "_공백" : "") 
							+ (isAsterisk ? "_별표" : "") 
							+ (isDecimal ? "_소수점" : "")
							+ (isComma ? "_콤마" : "")
							+ (isHyphen ? "_하이픈" : "") 
							+ ":형식변경::" + orgData + "::" + cngData;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (!isCheck || !cngMsg.isEmpty()) {
				errMsg = "율_작업불가_항목";
			}
		}
		
		resData.setOrg(itemVO.getNode());
		resData.setCng(cngData);
		resData.setErrMsg(errMsg);
		resData.setCngMsg(cngMsg);
		
		return resData;
	}
	
}
