package application.chk;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import application.ItemVO;
import application.ResData;
import application.chk.comm.ChkAttribute;

public class ChkDateOrd implements ChkAttribute {

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
			// 선택 헤더 Data
			orgData = itemVO.getNode();
			
			// 진단대상 비교 Data
			compData = itemVO.getNodeRow()[Integer.parseInt(itemVO.getOpt1())];
			
			// 둘 다 입력 안된 경우 정상(체크안함)
			if (orgData.trim().length() == 0 && compData.trim().length() == 0) {
				// 정상
				isCheck = true;
			} else {
				
				// 선택 헤더 Data 정상 패턴 확인, 유효 날짜 체크
				String[] orgDataReg = chkItemNext(orgData);
				boolean isOrgDateChk = orgDataReg != null ? ChkDate.isDateChk(orgData, orgDataReg) : false;
				
				// 진단대상 비교 Data 정상 패턴 확인, 유효 날짜 체크
				String[] compDataReg = chkItemNext(compData);
				boolean isCompDateChk = compDataReg != null ? ChkDate.isDateChk(compData, compDataReg) : false;

				// 선택 헤더 Data Check 
				if (orgData.trim().length() > 0 && orgDataReg == null) {
					cngMsg = "시간순서일관성_지정 컬럼 날짜("+orgData+") 타입 아님";
				} else if (orgData.trim().length() > 0 && !isOrgDateChk) {
					cngMsg = "시간순서일관성_지정 컬럼 날짜("+orgData+") 유효범위 오류";
				}

				if (cngMsg.isEmpty()) {
					// 진단대상 배교 Data Check 
					if (compData.trim().length() > 0 && compDataReg == null) {
						cngMsg = "시간순서일관성_비교대상 컬럼 날짜("+compData+") 타입 아님";
					} else if (compData.trim().length() > 0 && !isCompDateChk) {
						cngMsg = "시간순서일관성_비교대상 컬럼 날짜("+compData+") 유효범위 오류";
					}
				}

				if (cngMsg.isEmpty()) {
					// 둘다 입력이 된 경우 날짜 타입 체크
					if (orgData.trim().length() > 0 && compData.trim().length() > 0 && !orgDataReg[0].equals(compDataReg[0])) { 
						cngMsg = "시간순서일관성_지정 컬럼 날짜 ("+orgData+") 타입 과 비교대상 컬럼 날짜 ("+compData+") 타입 틀림";
					}
				}

				if (cngMsg.isEmpty()) {
					// 선택 헤더 Data 가 입력 안된 경우 진단대상 비교 Data 와 동일한 패턴으로 9999-12-31 설정
					if (orgData.trim().length() == 0 && compData.trim().length() > 0) {
						orgDataReg = null;
						orgDataReg = compDataReg;
						orgData = new SimpleDateFormat(orgDataReg[1]).format(getMaxDate());
					}
					// 진단대상 비교 Data 가 입력 안된 경우 선택 헤더 Data 와 동일한 패턴으로 9999-12-31 설정
					if (compData.trim().length() == 0 && orgData.trim().length() > 0) {
						compDataReg = null;
						compDataReg = orgDataReg;
						compData = new SimpleDateFormat(compDataReg[1]).format(getMaxDate());
					}
					
					// 선택 비교구분자 (1:">=":"크거나 같아야 한다" , 2:">":"커야 한다" , 3:"<=":"작거나 같아야 한다" , 4:"<":"작아야 한다")
					int rdoIdx = Integer.parseInt(itemVO.getOpt2());
					
					// 지정컬럼, 비교대상컬럼 날짜비교 시작
					long orgVal = Long.parseLong(orgData.replaceAll("[^0-9]", ""));
					long cngVal = Long.parseLong(compData.replaceAll("[^0-9]", ""));
					if (rdoIdx == 1) { // 크거나 같으면 정상
						if (orgVal >= cngVal) {
							// 정상
							isCheck = true;
						} else {
							cngMsg = "시간순서일관성_지정컬럼("+orgData+") >= 비교대상 컬럼("+compData+") 크거나 같아야 함";
						}
					} else if (rdoIdx == 2) { // 크면 정상
						if (orgVal > cngVal) {
							// 정상
							isCheck = true;
						} else {
							cngMsg = "시간순서일관성_지정컬럼("+orgData+") > 비교대상 컬럼("+compData+") 커야 함";
						}
					} else if (rdoIdx == 3) { // 작거나 같으면 정상
						if (orgVal <= cngVal) {
							// 정상
							isCheck = true;
						} else {
							cngMsg = "시간순서일관성_지정컬럼("+orgData+") <= 비교대상 컬럼("+compData+") 작거나 같아야 함";
						}
					} else if (rdoIdx == 4) { // 작으면 정상
						if (orgVal < cngVal) {
							// 정상
							isCheck = true;
						} else {
							cngMsg = "시간순서일관성_지정컬럼("+orgData+") < 비교대상 컬럼("+compData+") 작아야 함";
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (!isCheck || !cngMsg.isEmpty()) {
				errMsg = "시간순서일관성_작업불가_항목";
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

	private Date getMaxDate() {
		try {
			return new SimpleDateFormat("yyyyMMddHHmmss").parse("99991231235959");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
}
