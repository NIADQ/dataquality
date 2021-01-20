package application;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import application.base.Const;
import application.chk.comm.InspectionChk;
import application.util.FileUtil;
import javafx.collections.ObservableList;

public class MainController {
	
	private List<String[]> readerCSV(String p, boolean b) {
		FileUtil fu = new FileUtil();
		
		List<String[]> content = new ArrayList<String[]>();
		FileReader fr = null;
		FileInputStream is = null;
		InputStreamReader isr = null;
		CSVReader reader = null;
		try {
			if (b) {
				is = new FileInputStream(p);
				//isr = new InputStreamReader(is, "EUC-KR");
				isr = new InputStreamReader(is, fu.getFileCharset(p));
				reader = new CSVReader(isr);
				content = reader.readAll(); 
			} else {
				is = new FileInputStream(p);
				//isr = new InputStreamReader(is, "EUC-KR");
				isr = new InputStreamReader(is, fu.getFileCharset(p));
				reader = new CSVReader(isr);
				
				String[] s;
				int count =1;
				while((s = reader.readNext()) != null) {
					content.add(s);
					count++;
					//if(count>10001) break;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) { reader.close(); reader = null; }
				if (isr != null) { isr.close(); isr = null; }
				if (is != null) { is.close(); reader = null; }
				if (fr != null) { fr.close(); fr = null; }
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return content;
	}
	
	public List<String[]> readerCSV(String filePath) { 
		return readerCSV(filePath, false);
	}
	
	public List<String[]> readerCSV2(String filePath) { 
		return readerCSV(filePath, true);
	}
	
	public void writeCSV(File filefc, List<String[]> dlist, ObservableList<ObservableList> olist) throws Exception {
		FileWriter filewriter = null;
		CSVWriter writer = null;
		try {
			filefc.createNewFile();
			filewriter = new FileWriter(filefc);
			writer = new CSVWriter(filewriter);
			String dataRow[] = new String[dlist.get(0).length];
			for(int i=0;i<dlist.get(0).length;i++) {
				dataRow[i]=dlist.get(0)[i];
			}
			writer.writeNext(dataRow);
			
			for (ObservableList defl : olist) {
				for (int i = 0; i < defl.size(); i++) dataRow[i] = (String) defl.get(i);
				writer.writeNext(dataRow);
			}
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (writer != null) { writer.close(); writer = null; }
				if (filewriter != null) { filewriter.close(); filewriter = null; }
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	public boolean writeResultXls(File filefc, List<String[]> dataList, List<String[]> orgDataList, List<String[]> cngDataList, List<String[]> cngMsgDataList, List<String[]> errMsgDataList, List<String[]> typDataList, String fnm, String stDt, String edDt, String orgNm, String dbNm) throws Exception {
		FileOutputStream fos = null;
		boolean isSuccess = false;
		try {
			filefc.createNewFile();
			fos = new FileOutputStream(filefc);
			
	        XSSFWorkbook workbook = new XSSFWorkbook();
	        
	        XSSFSheet sheet1 = createSheet1(workbook, fnm, stDt, edDt, orgNm, dbNm, errMsgDataList, typDataList);
	        XSSFSheet sheet2 = createSheet2(workbook, fnm, dbNm, dataList, errMsgDataList, typDataList);
	        XSSFSheet sheet3 = createSheet3(workbook, fnm, dbNm, dataList, orgDataList, errMsgDataList, typDataList);

	        workbook.write(fos);
			
	        isSuccess = true;
	        
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null) { fos.close(); fos = null; }
			} catch (Exception fe) {
				fe.printStackTrace();
			}
		}
		return isSuccess;
	}

	private CellStyle getCellStyle(XSSFWorkbook workbook, short bgColor, short txColor, boolean isBorder, boolean isBig) {
		CellStyle cs = workbook.createCellStyle();
		//cs.setFillBackgroundColor(bgColor);
		cs.setFillForegroundColor(bgColor);  
		cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		cs.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		if (isBorder) {
			cs.setBorderRight(HSSFCellStyle.BORDER_THIN);
			cs.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			cs.setBorderTop(HSSFCellStyle.BORDER_THIN);
			cs.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		}
        Font ft = workbook.createFont();
        ft.setColor(txColor);
        ft.setFontName("고딕");
        if (isBig) {
        	ft.setBoldweight(Font.BOLDWEIGHT_BOLD);
        	ft.setFontHeightInPoints((short)14);
        } else {
        	ft.setFontHeightInPoints((short)10);
        }
        cs.setFont(ft);
		return cs;
	}
	
	/*
	 * 값진단 종합현황 시트 생성
	 */
	private XSSFSheet createSheet1(XSSFWorkbook workbook, String fnm, String stDt, String edDt, String orgNm, String dbNm, List<String[]> errLst, List<String[]> typLst) throws Exception {
		// 데이터 추출 Map<String(typ 대분류), Integer[컬럼수, 전체건수, 에러건수]> 
		Map<String, int[]> dmap = null;

		// 전체 데이터 건수
		int totColRow = typLst.size();
		// 전체 칼럼
		int totColCnt = typLst.get(typLst.size()-1).length-1;
		// 진단대상 칼럼
		int insColCnt = 0;

		// 유형 별 건수, 에러건수 구하기
		String typs = "";
		for (int i = 0; i < typLst.size(); i++) {
			String[] typArr = typLst.get(i);
			for (int j = 0; j < typArr.length; j++) {
				if (j > 0) {
					if (dmap == null) dmap = new HashMap<String, int[]>();
					typs = typArr[j].split("-")[0];
					if (dmap.containsKey(typs)) {
						dmap.get(typs)[1]++;
						dmap.get(typs)[2] = dmap.get(typs)[2] + (errLst.get(i)[j].isEmpty() ? 0 : 1);
					} else {
						int[] dint = {0, 1, (errLst.get(i)[j].isEmpty() ? 0 : 1)};
						dmap.put(typs, dint);
					}
				}
			}
		}
		// 유형 별 컬럼 수 구하기
		if (dmap != null) {
			int idx = 0;
			for (String ts : typLst.get(typLst.size()-1)) {
				if (idx > 0 && dmap.containsKey(ts.split("-")[0])) {
					//if (!ts.split("-")[0].equals(Const.CHK_STRING)) insColCnt++; //문자열 타입은 진단대상 컬럼(계) 에서 제외
					insColCnt++;
					dmap.get(ts.split("-")[0])[0]++;
				}
				idx++;
			}
		}
		
		
		XSSFSheet sheet = workbook.createSheet("값진단 종합현황");
        
        // width
        sheet.setColumnWidth(0, 7200);
        sheet.setColumnWidth(1, 4500);
        sheet.setColumnWidth(2, 4500);
        sheet.setColumnWidth(3, 4500);
        sheet.setColumnWidth(4, 4500);
        sheet.setColumnWidth(5, 4500);

        
        // Top Title Style
        CellStyle titleStyle = getCellStyle(workbook, HSSFColor.WHITE.index, HSSFColor.BLACK.index, false, true);
        titleStyle.setAlignment(CellStyle.ALIGN_CENTER);

        // Head Title Style
        CellStyle headStyle = getCellStyle(workbook, HSSFColor.BLACK.index, HSSFColor.WHITE.index, true, false);
        headStyle.setAlignment(CellStyle.ALIGN_CENTER);

        // Left Title Style
        CellStyle leftStyle = getCellStyle(workbook, HSSFColor.GREY_25_PERCENT.index, HSSFColor.BLACK.index, true, false);
        leftStyle.setAlignment(CellStyle.ALIGN_CENTER);

        // Left Title Style
        CellStyle leftStyle2 = getCellStyle(workbook, HSSFColor.GREY_25_PERCENT.index, HSSFColor.BLACK.index, true, false);
        leftStyle2.setAlignment(CellStyle.ALIGN_RIGHT);

        // Contents Left Align Style
        CellStyle cntsLStyle = getCellStyle(workbook, HSSFColor.WHITE.index, HSSFColor.BLACK.index, true, false);
        cntsLStyle.setAlignment(CellStyle.ALIGN_LEFT);

        // Contents Right Align Style
        CellStyle cntsRStyle = getCellStyle(workbook, HSSFColor.WHITE.index, HSSFColor.BLACK.index, true, false);
        cntsRStyle.setAlignment(CellStyle.ALIGN_RIGHT);

        // Contents No-Boader Right Align Style
        CellStyle cntsNBRStyle = getCellStyle(workbook, HSSFColor.WHITE.index, HSSFColor.BLACK.index, false, false);
        cntsNBRStyle.setAlignment(CellStyle.ALIGN_RIGHT);

        // 타이틀
        createXSSMergedCell(sheet.createRow(0), 0, 5, titleStyle, "개방데이터 값 진단 종합 현황");

        // 출력일
        createXSSMergedCell(sheet.createRow(1), 4, 5, cntsNBRStyle, "출력일 : " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        // 헤더 타이틀
        createXSSMergedCell(sheet.createRow(2), 0, 5, headStyle, "진단 데이터베이스 기본 정보");

        // 왼쪽 타이틀
        XSSFRow row_3 = sheet.createRow(3);
        XSSFCell row_3_col_1 = createXSSMergedCell(row_3, 0, 0, leftStyle, "진단기간");
        // 오른쪽 내용
        createXSSMergedCell(row_3, 1, 5, leftStyle, stDt + " ~ " + edDt);
        
        
        // 왼쪽 타이틀
        XSSFRow row_4 = sheet.createRow(4);
        createXSSMergedCell(row_4, 0, 0, leftStyle, "개방파일명(CSV)");
        // 오른쪽 내용
        createXSSMergedCell(row_4, 1, 5, cntsLStyle, fnm);
        
        // 왼쪽 타이틀
        XSSFRow row_5 = sheet.createRow(5);
        createXSSMergedCell(row_5, 0, 0, leftStyle, "기관명");
        // 오른쪽 내용
        createXSSMergedCell(row_5, 1, 5, cntsLStyle, orgNm);

        // 왼쪽 타이틀
        XSSFRow row_6 = sheet.createRow(6);
        createXSSMergedCell(row_6, 0, 0, leftStyle, "데이터베이스(DB)명");
        // 오른쪽 내용
        createXSSMergedCell(row_6, 1, 5, cntsLStyle, dbNm);
        
        // 공백
        sheet.createRow(7).setHeight((short)200);

        // 왼쪽 타이틀
        XSSFRow row_8 = sheet.createRow(8);
        createXSSMergedCell(row_8, 0, 0, headStyle, "");
        // 오른쪽 내용
        createXSSMergedCell(row_8, 1, 1, headStyle, "전체컬럼");
        createXSSMergedCell(row_8, 2, 2, headStyle, "진단대상 컬럼(계)");
        createXSSMergedCell(row_8, 3, 3, headStyle, "전체 데이터 건수");
        createXSSMergedCell(row_8, 4, 5, headStyle, "");

        // 왼쪽 타이틀
        XSSFRow row_9 = sheet.createRow(9);
        createXSSMergedCell(row_9, 0, 0, leftStyle, "진단규칙 설정 현황");
        // 오른쪽 내용
        createXSSMergedCell(row_9, 1, 1, cntsRStyle, totColCnt+"");
        createXSSMergedCell(row_9, 2, 2, cntsRStyle, insColCnt+"");
        createXSSMergedCell(row_9, 3, 3, cntsRStyle, totColRow+"");
        createXSSMergedCell(row_9, 4, 5, cntsRStyle, "");

        // 공백
        sheet.createRow(10).setHeight((short)200);

        // 왼쪽 타이틀
        XSSFRow row_11 = sheet.createRow(11);
        createXSSMergedCell(row_11, 0, 0, headStyle, "분석영역");
        // 오른쪽 내용
        createXSSMergedCell(row_11, 1, 1, headStyle, "검증유형");
        createXSSMergedCell(row_11, 2, 2, headStyle, "진단대상 컬럼");
        createXSSMergedCell(row_11, 3, 3, headStyle, "전체데이터");
        createXSSMergedCell(row_11, 4, 4, headStyle, "오류데이터");
        createXSSMergedCell(row_11, 5, 5, headStyle, "오류율(%)");

        
        // 도메인 목록 
        int rowCnt = 12;
        List<String[]> typList = getInspNameList(); // 진단유형 목록 (대분류)
        for(String[] ts : typList) {
        	XSSFRow tmpRow = sheet.createRow(rowCnt++);
        	createXSSMergedCell(tmpRow, 0, 0, leftStyle, "");
        	createXSSMergedCell(tmpRow, 1, 1, cntsLStyle, ts[1]);
        	createXSSMergedCell(tmpRow, 2, 2, cntsRStyle, dmap != null && dmap.containsKey(ts[0]) ? dmap.get(ts[0])[0] + "" : "0");
			createXSSMergedCell(tmpRow, 3, 3, cntsRStyle, dmap != null && dmap.containsKey(ts[0]) ? dmap.get(ts[0])[1] + "" : "0");
			//createXSSMergedCell(tmpRow, 3, 3, cntsRStyle, dmap != null && dmap.containsKey(ts[0]) ? (dmap.get(ts[0])[0]*totColRow)+"" : "0"); // 검증유형 진단대상 컬럼 수 * 전체데이터건수
			createXSSMergedCell(tmpRow, 4, 4, cntsRStyle, dmap != null && dmap.containsKey(ts[0]) ? dmap.get(ts[0])[2] + "" : "0");
			createXSSMergedCell(tmpRow, 5, 5, cntsRStyle, dmap != null && dmap.containsKey(ts[0]) ? new DecimalFormat("#.00").format((Math.round((Double.parseDouble(dmap.get(ts[0])[2]+"")/Double.parseDouble(dmap.get(ts[0])[1]+""))*10000.0)/100.0))+"" : ".00");
        }
		sheet.addMergedRegion(new CellRangeAddress(12, rowCnt-1, 0, 0));
		sheet.getRow(12).getCell(0).setCellValue("도메인");
		
        // 전체 
        XSSFRow row_tot = sheet.createRow(rowCnt);
        createXSSMergedCell(row_tot, 0, 1, leftStyle, "전체");
        // 오른쪽 내용
		int[] res = {0, 0, 0};
		if (dmap != null) {
			dmap.forEach((k, v) -> res[0] += v[0] );
			dmap.forEach((k, v) -> res[1] += v[1] );
			dmap.forEach((k, v) -> res[2] += v[2] );
		}
        createXSSMergedCell(row_tot, 2, 2, leftStyle2, res[0]+"");
        createXSSMergedCell(row_tot, 3, 3, leftStyle2, res[1]+"");
        createXSSMergedCell(row_tot, 4, 4, leftStyle2, res[2]+"");
        createXSSMergedCell(row_tot, 5, 5, leftStyle2, new DecimalFormat("#.00").format((Math.round((Double.parseDouble(res[2]+"")/Double.parseDouble(res[1]+""))*10000.0)/100.0))+"");
        
		
		//System.out.println("#####rowCnt:"+rowCnt);

        
        return sheet;
	}

	/*
	 * 도메인규칙 및 오류목록
	 */
	private XSSFSheet createSheet2(XSSFWorkbook workbook, String fnm, String dbNm, List<String[]> dataList, List<String[]> errLst, List<String[]> typLst) throws Exception {
		// 데이터 추출  String[컬럼명, 유형, 전체건수, 에러건수]> 
		List<String[]> dlist = new ArrayList<String[]>();
		String[] headers = dataList.get(0);
		
		// 전체 데이터 건수
		for (int i = 0; i < typLst.size(); i++) {
			String[] typArr = typLst.get(i);
			for (int j = 0; j < typArr.length; j++) {
				if (j > 0) {
					if (i==0) {
						String[] strs = {headers[j], typArr[j].split("-")[0], "1", String.valueOf(errLst.get(i)[j].isEmpty() ? 0 : 1)};
						dlist.add(strs);
					} else {
						String[] strs = dlist.get(j-1);
						strs[2] = String.valueOf(Integer.parseInt(strs[2]) + 1);
						strs[3] = String.valueOf(Integer.parseInt(strs[3]) + (errLst.get(i)[j].isEmpty() ? 0 : 1));
					}
				}
			}
		}

		
		XSSFSheet sheet = workbook.createSheet("도메인규칙 및 오류목록");
        
        // width
		sheet.setColumnWidth(0, 1500);
        sheet.setColumnWidth(1, 8000);
        sheet.setColumnWidth(2, 8000);
        sheet.setColumnWidth(3, 8000);
        sheet.setColumnWidth(4, 6000);
        sheet.setColumnWidth(5, 2800);
        sheet.setColumnWidth(6, 2800);
        sheet.setColumnWidth(7, 2800);
        sheet.setColumnWidth(8, 2800);

        
        // Top Title Style
        CellStyle titStyle = getCellStyle(workbook, HSSFColor.GREY_25_PERCENT.index, HSSFColor.BLACK.index, true, false);
        titStyle.setAlignment(CellStyle.ALIGN_CENTER);

        // Contents Left Align Style
        CellStyle cntsLStyle = getCellStyle(workbook, HSSFColor.WHITE.index, HSSFColor.BLACK.index, true, false);
        cntsLStyle.setAlignment(CellStyle.ALIGN_LEFT);

        // Contents Right Align Style
        CellStyle cntsRStyle = getCellStyle(workbook, HSSFColor.WHITE.index, HSSFColor.BLACK.index, true, false);
        cntsRStyle.setAlignment(CellStyle.ALIGN_RIGHT);

        // Contents Center Align Style
        CellStyle cntsCStyle = getCellStyle(workbook, HSSFColor.WHITE.index, HSSFColor.BLACK.index, true, false);
        cntsCStyle.setAlignment(CellStyle.ALIGN_CENTER);

        // 타이틀
        XSSFRow titRow = sheet.createRow(0);
        createXSSMergedCell(titRow, 0, 0, titStyle, "번호");
        createXSSMergedCell(titRow, 1, 1, titStyle, "개방파일명");
        createXSSMergedCell(titRow, 2, 2, titStyle, "개방데이터 명");
        createXSSMergedCell(titRow, 3, 3, titStyle, "컬럼명");
        createXSSMergedCell(titRow, 4, 4, titStyle, "검증유형");
        createXSSMergedCell(titRow, 5, 5, titStyle, "실행여부");
        createXSSMergedCell(titRow, 6, 6, titStyle, "전체건수");
        createXSSMergedCell(titRow, 7, 7, titStyle, "오류건수");
        createXSSMergedCell(titRow, 8, 8, titStyle, "오류율(%)");

        // 도메인 목록 
        int rowCnt = 1;
		for (String[] ts : dlist) {
			XSSFRow tmpRow = sheet.createRow(rowCnt);
			createXSSMergedCell(tmpRow, 0, 0, cntsLStyle, (rowCnt++)+"");
			createXSSMergedCell(tmpRow, 1, 1, cntsLStyle, fnm);
			createXSSMergedCell(tmpRow, 2, 2, cntsLStyle, dbNm);
			createXSSMergedCell(tmpRow, 3, 3, cntsLStyle, ts[0]);
			createXSSMergedCell(tmpRow, 4, 4, cntsLStyle, getInspName(ts[1]));
			createXSSMergedCell(tmpRow, 5, 5, cntsCStyle, ts[1].equals(Const.CHK_STRING) ? "" : "Y");
			createXSSMergedCell(tmpRow, 6, 6, cntsRStyle, ts[2]);
			createXSSMergedCell(tmpRow, 7, 7, cntsRStyle, ts[3]);
			createXSSMergedCell(tmpRow, 8, 8, cntsRStyle, new DecimalFormat("#.00").format((Math.round((Double.parseDouble(ts[3])/Double.parseDouble(ts[2]))*10000.0)/100.0))+"");
		}
        
        return sheet;
	}

	/*
	 * 검증유형별 오류데이터
	 */
	private XSSFSheet createSheet3(XSSFWorkbook workbook, String fnm, String dbNm, List<String[]> dataList, List<String[]> orgDataList, List<String[]> errLst, List<String[]> typLst) throws Exception {
		// 데이터 그룹핑 추출  Map<유형소분류|컬럼명|오류데이터 , String[유형소분류, 컬럼명, 오류데이터, 에러건수]> 
		Map<String, String[]> dmap = new HashMap<String, String[]>();
		String[] headers = dataList.get(0);
		// 전체 데이터 건수
        for (int i=0; i<orgDataList.size(); i++) {
        	for (int j=0; j<orgDataList.get(i).length; j++) {
        		if (j > 0 && !errLst.get(i)[j].isEmpty()) {
        			String key = typLst.get(i)[j] + "|" + headers[j] + "|" + orgDataList.get(i)[j];
        			if (dmap.containsKey(key)) {
        				dmap.get(key)[3] = String.valueOf(Integer.parseInt(dmap.get(key)[3]) + 1);
        			} else {
        				String[] strs = {typLst.get(i)[j], headers[j], orgDataList.get(i)[j], "1"};        				
        				dmap.put(key, strs);
        			}
        		}
        	}
		}
		
		XSSFSheet sheet = workbook.createSheet("검증유형별 오류데이터");
        
        // width
		sheet.setColumnWidth(0, 1500);
        sheet.setColumnWidth(1, 5000);
        sheet.setColumnWidth(2, 5000);
        sheet.setColumnWidth(3, 8000);
        sheet.setColumnWidth(4, 8000);
        sheet.setColumnWidth(5, 5800);
        sheet.setColumnWidth(6, 5000);
        sheet.setColumnWidth(7, 2800);

        
        // Top Title Style
        CellStyle titStyle = getCellStyle(workbook, HSSFColor.GREY_25_PERCENT.index, HSSFColor.BLACK.index, true, false);
        titStyle.setAlignment(CellStyle.ALIGN_CENTER);

        // Contents Left Align Style
        CellStyle cntsLStyle = getCellStyle(workbook, HSSFColor.WHITE.index, HSSFColor.BLACK.index, true, false);
        cntsLStyle.setAlignment(CellStyle.ALIGN_LEFT);

        // Contents Right Align Style
        CellStyle cntsRStyle = getCellStyle(workbook, HSSFColor.WHITE.index, HSSFColor.BLACK.index, true, false);
        cntsRStyle.setAlignment(CellStyle.ALIGN_RIGHT);

        // Contents Center Align Style
        CellStyle cntsCStyle = getCellStyle(workbook, HSSFColor.WHITE.index, HSSFColor.BLACK.index, true, false);
        cntsCStyle.setAlignment(CellStyle.ALIGN_CENTER);

        // 타이틀
        XSSFRow titRow = sheet.createRow(0);
        createXSSMergedCell(titRow, 0, 0, titStyle, "번호");
        createXSSMergedCell(titRow, 1, 1, titStyle, "검증유형");
        createXSSMergedCell(titRow, 2, 2, titStyle, "검증유형 상세");
        createXSSMergedCell(titRow, 3, 3, titStyle, "개방파일명");
        createXSSMergedCell(titRow, 4, 4, titStyle, "개방데이터 명");
        createXSSMergedCell(titRow, 5, 5, titStyle, "컬럼명");
        createXSSMergedCell(titRow, 6, 6, titStyle, "오류데이터");
        createXSSMergedCell(titRow, 7, 7, titStyle, "오류건수");

        // 오류 목록 
        List<ItemVO> typVoList = Const.getItemCntsList();
        int rowCnt = 1;
        for (String sky : dmap.keySet()) {
        	String[] strs = dmap.get(sky);
        	ItemVO vo = typVoList.stream().filter(v -> v.getKey2().equals(strs[0])).findFirst().get();
			
			XSSFRow tmpRow = sheet.createRow(rowCnt);
			createXSSMergedCell(tmpRow, 0, 0, cntsLStyle, (rowCnt++)+"");
			createXSSMergedCell(tmpRow, 1, 1, cntsLStyle, vo.getVal().split(">")[0]);
			createXSSMergedCell(tmpRow, 2, 2, cntsLStyle, vo.getVal());
			createXSSMergedCell(tmpRow, 3, 3, cntsLStyle, fnm);
			createXSSMergedCell(tmpRow, 4, 4, cntsLStyle, dbNm);
			createXSSMergedCell(tmpRow, 5, 5, cntsLStyle, strs[1]);
			createXSSMergedCell(tmpRow, 6, 6, cntsLStyle, strs[2]);
			createXSSMergedCell(tmpRow, 7, 7, cntsRStyle, strs[3]);
        }
        
        return sheet;
	}
	
	/*
	 * 셀 생성
	 */
	private XSSFCell createXSSMergedCell(XSSFRow row, int nStart, int nEnd, CellStyle cellStyle, String val) throws Exception {
	    XSSFCell returnCell = null;

	    for (int i = nStart; i <= nEnd; i++) {
	        XSSFCell cell = row.createCell(i);
	        if (i == nStart) returnCell = cell;
	        cell.setCellStyle(cellStyle);
	    }
	    row.setHeight((short)450);

	    // 셀병합
	    if (nEnd > nStart) row.getSheet().addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), nStart, nEnd));

	    returnCell.setCellValue(val);
	    
	    return returnCell;
	}
	
	private String getInspName(String typ) {
		for (InspectionChk ic : InspectionChk.values()) {
			if (typ.equals(ic.getType())) return ic.getDesc();
		}
		return typ;
	}

	private List<String[]> getInspNameList() {
		List<String[]> ls = new ArrayList<String[]>();
		for (InspectionChk ic : InspectionChk.values()) {
			//if (!Const.CHK_STRING.equals(ic.getType())) {
				String[] sr = {ic.getType(), ic.getDesc()};
				ls.add(sr);
			//}
		}
		return ls;
	}

	public ResInspect FileInspection(String fPath, int headerCnt, List<Map<String,Object>> list)
			throws Exception {
		ResInspect resInspect = new ResInspect();
		try {
			List<String[]> oriList = new ArrayList<String[]>();
			List<String[]> cngList = new ArrayList<String[]>();
			List<String[]> msgCngList = new ArrayList<String[]>();
			List<String[]> msgErrList = new ArrayList<String[]>();
			List<String[]> typList = new ArrayList<String[]>();
            int itr = 0;
            int idx = 0;
			for (String[] data : readerCSV2(fPath)) {
				String[] oriArrays = new String[data.length+1];
				String[] cngArrays = new String[data.length+1]; 
				String[] msgcngArrays = new String[data.length+1]; 
				String[] msgerrArrays = new String[data.length+1]; 
				String[] typArrays = new String[data.length+1];
				
				if (itr <= headerCnt) {
					itr++;
					continue;
				} else {
					oriArrays[0] = ""+(idx+1);
					cngArrays[0] = "";
					msgcngArrays[0] = "";
					msgerrArrays[0] = "";
					typArrays[0] = "";
					for(int i=0; i<data.length; i++) {
						ItemVO itemVO = (ItemVO)list.get(i+1).get((i+1)+"datatype");
						itemVO.setNode(data[i].trim().isEmpty() ? "" : data[i]);
						itemVO.setNodeRow(data);
						ResData resData = null;
						
						oriArrays[i+1] = "";
						cngArrays[i+1] = "";
						msgcngArrays[i+1] = "";
						msgerrArrays[i+1] = "";
						typArrays[i+1] = "";
						//if( itemVO.getNode().length() > 0) {
						ItemVO ivo = (ItemVO)list.get(i+1).get((i+1)+"datatype");
						InspectionChk ichk = Const.getInspectionChk(ivo);
						if (ichk != null) {
							resData = ichk.getChkResData(itemVO);
							oriArrays[i+1] = resData.getOrg();
							cngArrays[i+1] = resData.getCng();
							msgcngArrays[i+1] = resData.getCngMsg();
							msgerrArrays[i+1] = resData.getErrMsg();
						}
						typArrays[i+1] = ivo.getKey2();
					} 
					itr ++;
					idx++;
				}
				
				oriList.add(oriArrays);
				cngList.add(cngArrays);
				msgCngList.add(msgcngArrays);
				msgErrList.add(msgerrArrays);
				typList.add(typArrays);
			}
			
			resInspect.setOriList(oriList);
			resInspect.setCngList(cngList);
			resInspect.setMsgCngList(msgCngList);
			resInspect.setMsgErrList(msgErrList);
			resInspect.setTypList(typList);
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return resInspect;
	}
	
	public void writeChangeRow(String changeFileNm, List<String[]> data) {
		if (data.size() > 0 ) {
			FileWriter filewriter = null;
			CSVWriter writer = null;
			try {
				File filefc = new File(changeFileNm);
				filefc.createNewFile();
				filewriter = new FileWriter(filefc);
				writer = new CSVWriter(filewriter);
				//String [] dataRow = new String[data.get(0).length];
				for (String[] strs : data) writer.writeNext(strs);
				writer.flush();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (null != writer) { writer.close(); writer = null; }
					if (null != filewriter) { filewriter.close(); filewriter = null; }
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		} 
	}
	
}
