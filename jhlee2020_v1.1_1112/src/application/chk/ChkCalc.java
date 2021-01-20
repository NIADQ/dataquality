package application.chk;

import java.util.List;

import application.ItemVO;
import application.ResData;
import application.base.Const;
import application.chk.comm.ChkAttribute;

public class ChkCalc implements ChkAttribute {
	
	@Override
	public ResData getChkResData(ItemVO itemVO) {
		// TODO Auto-generated method stub
		if ((Const.CHK_CALC + "-1").equals(itemVO.getKey2())) return chkItem(itemVO);
		return chkItem2(itemVO);
	}

	private ResData chkItem(ItemVO itemVO) {
		ResData resData = new ResData();
		boolean isCheck = false;
		String orgData = "";
		String cngData = "";
		String errMsg = "";
		String cngMsg =""; 

		try {
			// 선택 Data
			orgData = itemVO.getNode();
			
			// 진단대상 조건 선택컬럼 값 1
			String compData1 = itemVO.getNodeRow()[Integer.parseInt(itemVO.getOpt1())];
			
			// 진단대상 조건 선택컬럼 값들의 연산문자
			String calcType =itemVO.getOpt2();
					
			// 진단대상 조건 선택컬럼 값 2
			String compData2 = itemVO.getNodeRow()[Integer.parseInt(itemVO.getOpt3())];
			
			if (orgData.trim().length() == 0) orgData = "0";
			if (compData1.trim().length() == 0) compData1 = "0";
			if (compData2.trim().length() == 0) compData2 = "0";
			
			if (orgData.matches(".*[^0-9]{1,}.*")) {
				cngMsg = "계산식_산식_선택컬럼 숫자 타입 아님";
			} else if (compData1.matches(".*[^0-9]{1,}.*")) {
				cngMsg = "계산식_산식_첫번쩨 비교컬럼 숫자 타입 아님";
			} else if (compData2.matches(".*[^0-9]{1,}.*")) {
				cngMsg = "계산식_산식_두번쩨 비교컬럼 숫자 타입 아님";
			} else {
				// 진단시작
				if (calcType.equals("+")) {
					if (Long.parseLong(orgData) == (Long.parseLong(compData1) + Long.parseLong(compData2))) {
						// 정상
						isCheck = true;
					} else {
						cngMsg = "계산식_산식_진단대상컬럼("+orgData+") == 첫번째 비교컬럼("+compData1+") + 두번쩨 비교컬럼("+compData2+") 틀림";
					} 
				} else if (calcType.equals("-")) {
					if (Long.parseLong(orgData) == (Long.parseLong(compData1) - Long.parseLong(compData2))) {
						// 정상
						isCheck = true;
					} else {
						cngMsg = "계산식_산식_진단대상컬럼("+orgData+") == 첫번째 비교컬럼("+compData1+") - 두번쩨 비교컬럼("+compData2+") 틀림";
					}
				} else if (calcType.equals("X")) {
					if (Long.parseLong(orgData) == (Long.parseLong(compData1) * Long.parseLong(compData2))) {
						// 정상
						isCheck = true;
					} else {
						cngMsg = "계산식_산식_진단대상컬럼("+orgData+") == 첫번째 비교컬럼("+compData1+") * 두번쩨 비교컬럼("+compData2+") 틀림";
					}
				} else if (calcType.equals("/")) {
					if (Long.parseLong(orgData) == (Long.parseLong(compData1) / Long.parseLong(compData2))) {
						// 정상
						isCheck = true;
					} else {
						cngMsg = "계산식_산식_진단대상컬럼("+orgData+") == 첫번째 비교컬럼("+compData1+") / 두번쩨 비교컬럼("+compData2+") 틀림";
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (!isCheck || !cngMsg.isEmpty()) {
				errMsg = "계산식_산식_작업불가_항목";
			}
		}
		
		resData.setOrg(itemVO.getNode());
		resData.setCng(cngData);
		resData.setErrMsg(errMsg);
		resData.setCngMsg(cngMsg);
		
		return resData;
		
	}

	private ResData chkItem2(ItemVO itemVO) {
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
			
			if (orgData.trim().length() == 0) orgData = "0";
			
			long csum = 0;
			if (orgData.matches(".*[^0-9].*")) {
				cngMsg = "계산식_합계_선택컬럼("+orgData+") 숫자 타입 아님";
			} else {
				// 진단대상 조건 선택컬럼들 값
				List<String> optList = itemVO.getOptList();
				if (optList != null && optList.size() > 0) {
					// Validate
					for (String idx : optList) {
						compData = itemVO.getNodeRow()[Integer.parseInt(idx)];
						if (compData.trim().length() == 0) compData = "0";
						if (compData.matches(".*[^0-9]{1,}.*")) {
							cngMsg = "계산식_합계_합계대상컬럼("+compData+") 숫자 타입 아님";
							break;
						} else {
							csum += Long.parseLong(compData);
						}
					}
				} else {
					cngMsg = "계산식_합계_합계대상컬럼 없음";
				}
			}

			if (cngMsg.isEmpty()) {
				if (Long.parseLong(orgData) == csum) {
					// 정상
					isCheck = true;
				} else {
					cngMsg = "계산식_합계_합계("+orgData+") == 합계대상컬럼합계("+csum+") 틀림";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (!isCheck || !cngMsg.isEmpty()) {
				errMsg = "계산식_산식_작업불가_항목";
			}
		}
		
		resData.setOrg(itemVO.getNode());
		resData.setCng(cngData);
		resData.setErrMsg(errMsg);
		resData.setCngMsg(cngMsg);
		
		return resData;
	}

}
