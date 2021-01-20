package application.chk;

import application.ItemVO;
import application.ResData;
import application.chk.comm.ChkAttribute;

public class ChkLogic implements ChkAttribute {

	@Override
	public ResData getChkResData(ItemVO itemVO) {
		// TODO Auto-generated method stub
		return chkItem(itemVO);
	}

	private ResData chkItem(ItemVO itemVO) {
		ResData resData = new ResData();
		boolean isCheck = false;
		String orgData = "";
		String compData = "";
		String cngData = "";
		String errMsg = "";
		String cngMsg =""; 

		try {
			// 선택 Data
			orgData = itemVO.getNode();
			
			// 진단대상 조건 값
			compData = itemVO.getOpt1();
			
			cngData = orgData;
			
			// 선택값 과 조건 값이 동일하면 진단 시작
			if (orgData.equals(compData)) {
				// 진단대상 조건 날짜 컬럼 값 
				String dateVal = itemVO.getNodeRow()[Integer.parseInt(itemVO.getOpt2())];
				
				// 날짜 검증
				String[] reg = chkItemNext(dateVal);
				if (reg == null) {
					cngMsg = "컬럼 간 논리관계 일관성_논리관계가 있는 컬럼 날짜("+itemVO.getOpt2()+") 타입 아님";
				} else {
					// 유효 날짜 체크
					if (ChkDate.isDateChk(dateVal, reg)) {
						// 정상
						isCheck = true;
					} else {
						cngMsg = "컬럼 간 논리관계 일관성_날짜("+itemVO.getOpt2()+") 유효범위 오류";
					}
				}
			} else {
				// 정상
				isCheck = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (!isCheck || !cngMsg.isEmpty()) {
				errMsg = "컬럼 간 논리관계 일관성_작업불가_항목";
			}
		}
		
		resData.setOrg(itemVO.getNode());
		resData.setCng(cngData);
		resData.setErrMsg(errMsg);
		resData.setCngMsg(cngMsg);
		
		return resData;
	}
	
	private String[] chkItemNext(String paramData) throws Exception {
		//정상 패턴 확인
		for(String[] reg : ChkDate.regexs) {
			if ( paramData.matches("^" + reg[2] + "$") ) return reg;
		}
		return null;
	}
	
}
