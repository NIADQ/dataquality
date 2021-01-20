package application.base;

import java.util.ArrayList;
import java.util.List;

import application.ItemVO;
import application.chk.comm.InspectionChk;

public class Const {

	public static double defStageX = 0.0;
	public static double defStageY = 0.0;
	public static double defStageWidth = 1024.0;
	public static double defStageHeight = 768.0;
	
	public static String defInitialDirectory = "";

	public static final String PROPS = "prop.properties";
	
	public static final String mainCss = "application/New.css";
	public static final String mainCss2 = "application/New2.css";

	public static final String chkDialogFxml = "/application/ChkDialog.fxml";
	public static final String chkDialogSmallFxml = "/application/ChkDialogSmall.fxml";

	public static final String fontCbo_1 = "-fx-font: 13px \"Serif\";";
	public static final String fontTxt_1 = "-fx-prompt-text-fill: RED;";
	
	public static final String CHK_STRING 		= "0";
	public static final String CHK_AMT 			= "1";
	public static final String CHK_QTY 			= "2";
	public static final String CHK_RATE 		= "3";
	public static final String CHK_FLAG 		= "4";
	public static final String CHK_DATE 		= "5";
	public static final String CHK_NO 			= "6";
	public static final String CHK_DATEORD 		= "7";
	public static final String CHK_LOGIC 		= "8";
	public static final String CHK_CALC 		= "9";

	public static final String[][] itemType = {
			{CHK_STRING, 	CHK_STRING,		"1) 문자열"},
			{CHK_AMT, 		CHK_AMT,    	"2) 금액"},
			{CHK_QTY, 		CHK_QTY,    	"3) 수량"},
			{CHK_RATE, 		CHK_RATE,    	"4) 율"},
			{CHK_FLAG, 		CHK_FLAG+"-1", 	"5-1) 여부 > Y, N"},
			{CHK_FLAG, 		CHK_FLAG+"-2",  "5-2) 여부 > 여부값 지정"},
			{CHK_DATE, 		CHK_DATE+"-1",  "6-1) 날짜 > YYYY-MM-DD HH24:MI:SS"},
			{CHK_DATE, 		CHK_DATE+"-2",  "6-2) 날짜 > YYYY-MM-DD HH24:MI"},
			{CHK_DATE, 		CHK_DATE+"-3",  "6-3) 날짜 > YYYY-MM-DD HH24"},
			{CHK_DATE, 		CHK_DATE+"-4",  "6-4) 날짜 > MM-DD HH24:MI"},
			{CHK_DATE, 		CHK_DATE+"-5",  "6-5) 날짜 > HH24:MI:SS"},
			{CHK_DATE, 		CHK_DATE+"-6",  "6-6) 날짜 > YYYY-MM-DD"},
			{CHK_DATE, 		CHK_DATE+"-7",  "6-7) 날짜 > HH24:MI"},
			{CHK_DATE, 		CHK_DATE+"-8",  "6-8) 날짜 > YYYY-MM"},
			{CHK_DATE, 		CHK_DATE+"-9",  "6-9) 날짜 > MM-DD"},
			{CHK_DATE, 		CHK_DATE+"-10", "6-10) 날짜 > HH24"},
			{CHK_DATE, 		CHK_DATE+"-11", "6-11) 날짜 > YYYY"},
			{CHK_DATE, 		CHK_DATE+"-12", "6-12) 날짜 > DD"},
			{CHK_DATE, 		CHK_DATE+"-13", "6-13) 날짜 > MI"},
			{CHK_DATE, 		CHK_DATE+"-14", "6-14) 날짜 > MM"},
			{CHK_DATE, 		CHK_DATE+"-15", "6-15) 날짜 > SS"},
			{CHK_NO, 		CHK_NO+"-1",    "7-1) 번호 > 전화번호"},
			{CHK_NO, 		CHK_NO+"-2",    "7-2) 번호 > 우편번호"},
			{CHK_NO, 		CHK_NO+"-3",    "7-3) 번호 > 사업자번호"},
			{CHK_DATEORD, 	CHK_DATEORD,    "8) 시간순서 일관성"},
			{CHK_LOGIC, 	CHK_LOGIC,    	"9) 컬럼 간 논리관계 일관성"},
			{CHK_CALC, 		CHK_CALC+"-1",  "10-1) 계산식 > 산식"},
			{CHK_CALC, 		CHK_CALC+"-2",  "10-2) 계산식 > 합계"}
	};
	
	public static List<ItemVO> getItemCntsList() {
		List<ItemVO> list = new ArrayList<ItemVO>();
		int i=0;
		for (String[] arr : itemType) {
			ItemVO item = new ItemVO();
			item.setSeq(i++);
			item.setKey(arr[0]);
			item.setKey2(arr[1]);
			item.setVal(arr[2]);
			list.add(item);
		}
		return list;
	}
	public static ItemVO getKeyToItem(String key) {
		int i=0;
		for (String[] arr : itemType) {
			if (arr[1].equals(key)) {
				ItemVO item = new ItemVO();
				item.setSeq(i);
				item.setKey(arr[0]);
				item.setKey2(arr[1]);
				item.setVal(arr[2]);
				return item;
			}
			i++;
		}
		return null;
	}
	public static InspectionChk getInspectionChk(ItemVO itemVO) {
		for (String[] arr : itemType) {
			if (arr[1].equals(itemVO.getKey2())) {
				for (InspectionChk ichk : InspectionChk.values()) {
					if (itemVO.getKey().equals(ichk.getType())) return ichk;
				}
			}
		}
		return null;
	}
}
