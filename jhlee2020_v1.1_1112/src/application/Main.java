/*
    This file is part of Foobar.

    Foobar is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Foobar is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Foobar.  If not, see <https://www.gnu.org/licenses/>.
 */
package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.controlsfx.control.CheckComboBox;

import application.base.Const;
import application.util.PropertyUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class Main extends Application {

	private Properties prop;
	
	static Boolean check = false;
	
	private Stage window;
	private Stage stage;
	
	private Scene scene1,scene1_2,scene2,scene2_2,scene3,sceneThree;
	
	private TableView<ObservableList> resultTblView;
	private TableView<ObservableList> defTblView;
	
	private ObservableList<ObservableList> defData;
	private ObservableList<ObservableList> orgData;

	private ObservableList<String> defRow;

	private FileChooser fc;
	private File openFile;
	private String openFilePath;
	private String openFileNm;

	private List<String[]> dataList ;
	
	private List<String[]> orgDataList;
	private List<String[]> cngDataList;
	private List<String[]> cngMsgDataList;
	private List<String[]> errMsgDataList;
	private List<String[]> typDataList;
	
	private int comboNo;
	
	private List<ItemVO> comboitemSeqList ;
	
	private VBox vboxOne;
	private VBox vboxPage;
	private VBox vboxThree;
	
	private HBox testbox2;
	private HBox testbox3;
	
	private HBox s2hbox2;
	
	MainController mc = new MainController();

	// 건수 관련
	private Label lblRowCntVal;
	private Label lblColCntVal;
	
	// 시간순서일관성 관련 변수 
	private String strDateOrd_CompVal;
	private Label lblDateOrd_CompTxt;

	// 계산식 > 산식 관련 변수
	private String lblCalc_CompVal;
	private Label lblCalc_CompTxt;

	// 기관목록
	private List<OrgVO> orgList = new ArrayList<OrgVO>();
	private ObservableList<OrgVO> oblInspOrgList;
	private ComboBox<OrgVO> cboInspOrg; 
	// DB목록
	private List<OrgVO> dbList = new ArrayList<OrgVO>();
	private ObservableList<OrgVO> oblInspDbList;
	private ComboBox<OrgVO> cboInspDb;
	private TextField txtInspDbInput;
	
	private Button btnInspect; 
			
	// 포맷
	private DecimalFormat df = new DecimalFormat("#,###");

	// 컬럼 사이즈 정보 
	private List<Integer> colChrLen = new ArrayList<Integer>();

	private Alert getAlertOpen(String tit, String cnts, AlertType at) {
		Alert alert = new Alert(at);
		//alert.setTitle("주의");
		alert.setHeaderText(tit);
		alert.setContentText(cnts);
		return alert;
	}

	private int getLenWidth(int len) { 
		if (len > 300) return 200;
		else if (len > 100) return len * 2;
		else if (len > 80) return len * 3;
		else if (len > 60) return len * 4;
		else if (len > 40) return len * 5;
		else if (len > 20) return len * 6;
		else if (len > 10) return len * 7;
		else if (len > 6) return len * 8;
		else return len * 10;
	}
	
	private String getPropVal(String key) {
		if (prop == null) return "";
		return prop.getProperty(key);
	}

	/*
	 * 기관, DB 목록 로드
	 */
	private void loadOrgList() {
		// 기관목록 로드
		loadOrgList(getPropVal("org.list.file.name"));
		// DB목록 로드
		loadOrgList(getPropVal("db.list.file.name"));
	}

	/*
	 * 기관, DB 목록 로드
	 */
	private void loadOrgList(String fnm) {
		if (orgList == null) orgList = new ArrayList<OrgVO>();
		if (dbList == null) dbList = new ArrayList<OrgVO>();

		String fdir = getPropVal("org.list.file.dir");

		if (fnm.isEmpty()) return;

		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		try {
			boolean isOrgStart = false;
			boolean isDbStart = false;
			fis = new FileInputStream(((!fdir.isEmpty() ? fdir : "") + fnm));
			isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
            br = new BufferedReader(isr);
            String strLine;
            int orgIdx = 0;
            int dbIdx = 0;
            while((strLine=br.readLine()) != null){
            	if (strLine.isEmpty()) continue;
            	if (strLine.matches("^\\s*\\[org\\.list\\]\\s*$")) {
            		isOrgStart = true; // 기관목록 시작
            		isDbStart = false; // DB목록 중지
            		continue;
            	}
            	if (strLine.matches("^\\s*\\[db\\.list\\]\\s*$")) {
            		isDbStart = true; // DB목록 시작
            		isOrgStart = false; // 기관목록 중지
            		continue;
            	}
                
                Matcher mch = Pattern.compile("^\\s*\\[([0-9a-zA-Z\\_\\-]{1,})\\]\\[([^\\[\\]]{1,})\\]\\s*").matcher(strLine);
                if (mch.find()) {
                	if (isOrgStart) orgList.add(new OrgVO(orgIdx++, mch.group(1), mch.group(2)));
                	else if (isDbStart) dbList.add(new OrgVO(mch.group(1), dbIdx++, "", mch.group(2)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	try {
        		if (br != null) { br.close(); br = null; }
        		if (isr != null) { isr.close(); isr = null; }
        		if (fis != null) { fis.close(); fis = null; }
        	} catch (Exception fe) { 
        		fe.printStackTrace();
        	}
        }
	}

	/*
	 * DB콤보박스 셋팅
	 */
	private void setCboInspDb() {
		oblInspDbList.clear();
		oblInspDbList.add(new OrgVO(0, "", "직접입력"));
		if (dbList.size() > 0) {
		OrgVO orgVO = cboInspOrg.getSelectionModel().getSelectedItem();
			for (OrgVO dbVO : dbList) {
				if (orgVO.getKey().equals(dbVO.getGrp())) oblInspDbList.add(dbVO); 
			}
		}
		cboInspDb.getSelectionModel().selectFirst();
	}
	
	@Override
	public void start(Stage primaryStage) {
		// 프로퍼티 로드
		prop = new PropertyUtil().getProperty();
		
		// 기관목록 / DB목록 로드
		loadOrgList();
		
		stage = primaryStage;
		
		window = primaryStage;
		
		// Default TableView Create
		defTblView = new TableView<>();
		defTblView.setPlaceholder(new Label("선택된 CSV 파일이 없습니다."));
		defTblView.prefHeightProperty().bind(stage.heightProperty());
		defTblView.prefWidthProperty().bind(stage.widthProperty());

		// Result TableView Create
		resultTblView = new TableView<>();
		resultTblView.prefHeightProperty().bind(stage.heightProperty());
		resultTblView.prefWidthProperty().bind(stage.widthProperty());
		
		defData = FXCollections.observableArrayList(); 
		defRow = FXCollections.observableArrayList();
		defData.add(defRow);
		
		// 창크기 및 타이틀 설정
		window.getIcons().add(new Image(Main.class.getResourceAsStream("icon.jpg")));
		window.setTitle("");
		
		window.setWidth(Const.defStageWidth);
		window.setHeight(Const.defStageHeight);
		
		
		
		// ProgressBar1_2
		Text txtstate1_2 = new Text();
		txtstate1_2.setFont(Font.font(18));
		txtstate1_2.setFill(Color.BLUE);
		ProgressBar pBar1_2 = new ProgressBar(0);
		pBar1_2.indeterminateProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
				txtstate1_2.setText( "처리중입니다.");
			}
		});
		pBar1_2.progressProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
				System.out.println("TBAR:"+t1.doubleValue());
				if (t1.doubleValue() == 1) {
					txtstate1_2.setText("Work Done");
					txtstate1_2.setFill(Color.GREEN);
					window.setScene(scene1);
				}
			}
		});
		HBox hbox1_2 = new HBox(15);
		hbox1_2.getChildren().addAll(pBar1_2, txtstate1_2);
		hbox1_2.setPadding(new Insets(320,0,0,370));
		hbox1_2.setAlignment(Pos.CENTER);
		Group root1_2 = new Group();
    	root1_2.getChildren().addAll(hbox1_2);
    	scene1_2 = new Scene(root1_2);

    	
    	
    	
		// ProgressBar2_2
		Text txtstate2_2 = new Text();
		txtstate2_2.setFont(Font.font(18));
		txtstate2_2.setFill(Color.BLUE);
		ProgressBar pBar2_2 = new ProgressBar(0);
		pBar2_2.indeterminateProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
				txtstate2_2.setText( "처리중입니다.");
			}
		});
		pBar2_2.progressProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
				System.out.println("TBAR:"+t1.doubleValue());
				if (t1.doubleValue() == 1) {
					txtstate2_2.setText("Work Done");
					txtstate2_2.setFill(Color.GREEN);
					window.setScene(scene1);
				}
			}
		});
		HBox hbox2_2 = new HBox(15);
		hbox2_2.getChildren().addAll(pBar2_2, txtstate2_2);
		hbox2_2.setPadding(new Insets(320,0,0,370));
		hbox2_2.setAlignment(Pos.CENTER);
		Group root2_2 = new Group();
    	root2_2.getChildren().addAll(hbox2_2);
    	scene2_2 = new Scene(root2_2);

    	
    	
    	
		// 변수명 : textField 
		// 파일 열기 실행 후, 파일의 경로와 파일명을 보여준다.
		TextField openFileField = new TextField();
		openFileField.setDisable(true);
		openFileField.setMinWidth(300);
		
		//여부 컬럼의 데이터를 중복없이 보여주는 TextField
		TextField txf = new TextField();
		txf.setMinWidth(900);
		
		dataList = new ArrayList<String[]>();
		
		// 건수 표시
		//파일 선택하는 기능
		// All Files *.*은 모든 파일 
		// TEXT *.txt 등과 같은 형식으로 설정가능
		fc = new FileChooser();
		fc.getExtensionFilters().addAll(new ExtensionFilter("All Files", "*.*"));

		//버튼 
		Button browser = new Button("열기");
		browser.setPadding(new Insets(3, 3, 3, 3));
		browser.setId("allBtn");
		
		//버튼클릭 시, 이벤트 발생 후 내용 실행
		browser.setOnAction(e -> {
			try {
				try {
					if (Const.defInitialDirectory != null) {
						if (Const.defInitialDirectory.indexOf("/") > 0) {
							fc.setInitialDirectory(new File(Const.defInitialDirectory.substring(0, Const.defInitialDirectory.lastIndexOf("/"))));
						} else if (Const.defInitialDirectory.indexOf("\\") > 0) {
							fc.setInitialDirectory(new File(Const.defInitialDirectory.substring(0, Const.defInitialDirectory.lastIndexOf("\\"))));
						}
					}
				} catch(Exception fe) {
					fe.printStackTrace();
				} 

				//파일 열기창 오픈
				openFile = fc.showOpenDialog(window);
				if (openFile != null) {

					System.out.println("File-Size:::["+openFile.length()+"], Limit-Size:::["+((1024L * 1024L)*20)+"]");
					if (openFile.length() >= (1024L * 1024L)*20) {
						getAlertOpen("진단파일 용량 확인", "진단대상 파일의 적정크기는 작업자 PC메모리 및 성능 에 따라 차이는 있겠으나  20메가 이하를 권장합니다.", AlertType.WARNING).showAndWait();
					}

					openFileNm = openFile.getName();
					openFilePath = openFile.getPath();
					Const.defInitialDirectory = openFile.getPath();

					window.setTitle(getPropVal("main.title") + "  [ 파일명 : " + openFileNm + " ]");

					defTblView.getColumns().clear();
					defData.clear();
					defRow.clear();

					dataList.clear();

					openFileField.setText(openFilePath);
					openFileField.setDisable(true);
					browser.setDisable(true);

					Task task = taskCreator1_2();
					pBar1_2.progressProperty().unbind();
					pBar1_2.progressProperty().bind(task.progressProperty());
					new Thread(task).start();
					
					window.setScene(scene1_2);

				}
				
			}catch(Exception eev) {
				eev.printStackTrace();
			}
			
		});
		
		// 이전(첫화면으로 이동)
		Button prvBtn = new Button("이전");
		prvBtn.setPadding(new Insets(3, 3, 3, 3));
		prvBtn.setOnAction(event->{
			window.setScene(scene1);
		});
		
		// 행 번호 입력
		TextField headRowTxt = new TextField("0");
		headRowTxt.setMinWidth(300);
		headRowTxt.setStyle(Const.fontTxt_1);
		headRowTxt.setPromptText("시작행 은 필수입력입니다.");
		
		//Button 생성
		Button headerBtn = new Button("시작");
		headerBtn.setPadding(new Insets(5.5, 5.5, 5.5, 5.5));
		headerBtn.setOnAction(e -> {
			try {
				if (headRowTxt.getText().trim().isEmpty()) return;
				
				if (headRowTxt.getText().matches("[^0-9]")) {
					getAlertOpen("숫자만 입력가능", "시작행은 숫자만 입력해야 합니다.", AlertType.WARNING).showAndWait();
					return;
				}
				
		        if(Integer.parseInt(headRowTxt.getText()) + 1 >= dataList.size()) {
		        	getAlertOpen("행의 개수보다 시작행의 숫자가 큽니다.", "헤더 번호가 행의 개수보다 큽니다. 행의 개수를 체크하신 뒤 숫자를 선택해주세요.", AlertType.WARNING).showAndWait();
					return;
		        }
				
				comboNo = Integer.parseInt(headRowTxt.getText());

				check = true;
				
				defTblView.getColumns().clear();
				defData.clear();
				defRow.clear();

				dataList.clear();

		        btnInspect.setDisable(false);

				Task task = taskCreator2_2();
				pBar2_2.progressProperty().unbind();
				pBar2_2.progressProperty().bind(task.progressProperty());
				new Thread(task).start();
				
				window.setScene(scene2_2);

			}catch (Exception ee){
				ee.printStackTrace();
			}
		});
		
		Button homeBtn = new Button("홈");
    	homeBtn.setOnAction(evnet->{
    		window.setScene(scene1);
    	});
    	Button prvBtn2 = new Button("이전");
    	prvBtn2.setOnAction(e -> {
    		window.setScene(scene2);
    	});
		
		// Nodes
		Text txtstate = new Text();
		txtstate.setFont(Font.font(18));
		txtstate.setFill(Color.BLUE);
		
		// ProgressBar
		ProgressBar pBar = new ProgressBar(0);
		pBar.indeterminateProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
				txtstate.setText( (t1 ? "계산" : "처리" ) + " 중입니다.");
			}
		});
		
		pBar.progressProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
				if (t1.doubleValue() == 1) {
					txtstate.setText("Work Done");
					txtstate.setFill(Color.GREEN);
					window.setScene(scene2);
				}
			}
		});
		
		// ProgressIndicator
		ProgressIndicator pind = new ProgressIndicator(0);
		pind.indeterminateProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
				if (t1) {
					txtstate.setText("계산 중입니다.");
					txtstate.setFill(Color.BLUE);
				} else {
					txtstate.setText("처리 중입니다.");
				}
			}
		});
		
		Button btnNext = new Button("저장");
		
		Label lblRow = new Label("시  작  행");
		lblRow.setMinWidth(80);
		lblRow.setPadding(new Insets(5,0,0,0));

		Label lblBroser = new Label("파일 경로");
		lblBroser.setMinWidth(80);
		lblBroser.setPadding(new Insets(5,0,0,0));

		HBox subhbox = new HBox(5);
		subhbox.setPadding(new Insets(15, 0, 10, 0));
		subhbox.getChildren().addAll(lblBroser, openFileField, browser);
		
		HBox hbox3 = new HBox(5);
		hbox3.setPadding(new Insets(0, 0, 0, 0));
		hbox3.getChildren().addAll(lblRow, headRowTxt, headerBtn);

		VBox subvbox = new VBox(5);
		subvbox.setPadding(new Insets(0, 0, 0, 0));
		subvbox.getChildren().addAll(subhbox, hbox3);

		// 전체건수
		Label lblRowCnt = new Label("▶ 전체 건수");
		lblRowCntVal = new Label("0");
		lblRowCntVal.setId("lblCnt");
		lblRowCntVal.setAlignment(Pos.CENTER_RIGHT);
		lblRowCntVal.setMinWidth(150.0);
		HBox subhbox2 = new HBox(5);
		subhbox2.setPadding(new Insets(5, 5, 5, 10));
		subhbox2.getChildren().addAll(lblRowCnt, lblRowCntVal);

		// 전체 열수
		Label lblColCnt = new Label("▶ 전체 열수");
		lblColCntVal = new Label("0");
		lblColCntVal.setId("lblCnt");
		lblColCntVal.setAlignment(Pos.CENTER_RIGHT);
		lblColCntVal.setMinWidth(150.0);
		HBox subhbox3 = new HBox(5);
		subhbox3.setPadding(new Insets(5, 5, 5, 10));
		subhbox3.getChildren().addAll(lblColCnt, lblColCntVal);

		VBox subvbox4 = new VBox(5);
		subvbox4.setPadding(new Insets(0,0,0,0));
		subvbox4.setStyle("-fx-border-color: black; -fx-border-width: 1;");
		subvbox4.getChildren().addAll(subhbox2, subhbox3);

		//진단 버튼
		btnInspect = new Button("진단");
		btnInspect.setId("largeBtn");
		btnInspect.setPadding(new Insets(15, 15, 15, 15));
		btnInspect.setOnAction(event -> {
			if (check == false) {
				getAlertOpen("먼저 다시불러오기 기능을 먼저 실행해주세요.", "해당 기능은 다시불러오기 기능을 선택한 후 헤더명을 매칭시키고 이용합니다.", AlertType.WARNING);
			}else {
				headRowTxt.setDisable(true);
				headerBtn.setDisable(true);
				Task task = taskCreator(dataList.size()-1-comboNo);
				pBar.progressProperty().unbind();
				pBar.progressProperty().bind(task.progressProperty());
				pind.progressProperty().unbind();
				pind.progressProperty().bind(task.progressProperty());
				new Thread(task).start();
				
				window.setScene(scene3);

				//window.setScene(scene2);
			}
		});
		btnInspect.setDisable(true);

		Label tmpLabel = new Label("");
		tmpLabel.setStyle("-fx-font-size: 10;");
		VBox subvbox2 = new VBox(5);
		subvbox2.setPadding(new Insets(0,0,0,0));
		subvbox2.getChildren().addAll(tmpLabel, subvbox4);

		VBox subvbox3 = new VBox(5);
		subvbox3.setPadding(new Insets(25, 15, 20, 15));
		subvbox3.getChildren().addAll(btnInspect);

		// 재시작
		Button btnRestart = new Button( "재시작" );
		btnRestart.setId("largeBtn");
		btnRestart.setPadding(new Insets(15, 15, 15, 15));
		btnRestart.setOnAction( __ -> {
	      primaryStage.close();
	      Platform.runLater( () -> new Main().start( new Stage() ) );
	    } );
		VBox subvbox5 = new VBox(5);
		subvbox5.setPadding(new Insets(25, 15, 20, 15));
		subvbox5.getChildren().addAll(btnRestart);


		Label tmpLabel2 = new Label("");
		tmpLabel2.setMinWidth(20.0);
		Label tmpLabel3 = new Label("");
		tmpLabel3.setMinWidth(20.0);
		HBox hbox = new HBox(5);
		hbox.setPadding(new Insets(0, 0, 10, 0));
		hbox.setStyle("-fx-border-style: solid none solid none;-fx-border-width: 2;-fx-border-color: black;");
		hbox.getChildren().addAll(subvbox, tmpLabel2, subvbox2, tmpLabel3, subvbox3, subvbox5);

		
		// 하단 안내문구
		Label botinfo1 = new Label("화면설명");
		botinfo1.setPadding(new Insets(0, 0, 0, 0));
		botinfo1.setMaxHeight(Double.MAX_VALUE);
		botinfo1.setStyle("-fx-font-weight: bold;");
		botinfo1.setFont(Font.font("Arial", FontWeight.BOLD, 16));

		Label botinfo2 = new Label("1.  [열기] 버튼을 클릭해 CSV 파일을 선택합니다.");
		botinfo2.setPadding(new Insets(0, 0, 0, 0));
		botinfo2.setMaxHeight(Double.MAX_VALUE);
		
		Label botinfo3 = new Label("2.  선택된 파일의 진단 시작행을 입력 후 [시작] 버튼을 클릭합니다.");
		botinfo3.setPadding(new Insets(0, 0, 0, 0));
		botinfo3.setMaxHeight(Double.MAX_VALUE);

		Label botinfo4 = new Label("3.  컬럼 헤더 아래의 콤보박스에서 해당되는 데이터 타입인 품질진단기준을 선택합니다.");
		botinfo4.setPadding(new Insets(0, 0, 0, 0));
		botinfo4.setMaxHeight(Double.MAX_VALUE);
		
		Label botinfo5 = new Label("4.  [진단] 버튼을 클릭하여 진단을 실시합니다.");
		botinfo5.setPadding(new Insets(0, 0, 0, 0));
		botinfo5.setMaxHeight(Double.MAX_VALUE);

		VBox vboxInfo = new VBox();
		vboxInfo.getChildren().addAll(botinfo1, botinfo2, botinfo3, botinfo4, botinfo5);
		
		testbox2 = new HBox();
		testbox2.getChildren().addAll(vboxInfo);
		testbox2.setPadding(new Insets(20, 0, 10, 10));
		
		Label lblTmp = new Label("");
		lblTmp.setStyle("-fx-font-size: 5;");
		vboxOne = new VBox();
		vboxOne.getChildren().addAll(hbox, lblTmp, defTblView, testbox2);
		vboxOne.setPadding(new Insets(10, 10, 10, 10));
		vboxOne.setPrefSize(800, 600);
		
		Label s3Header = new Label("변경 및 오류 내역");
		s3Header.setMaxWidth(Double.MAX_VALUE);
		s3Header.setAlignment(Pos.CENTER);
		s3Header.setId("title");
		s3Header.setPadding(new Insets(0, 0, 10, 0));
		
		vboxThree = new VBox();
		vboxThree.getChildren().addAll(s3Header);
		vboxThree.setPadding(new Insets(10, 10, 10, 10));
		vboxThree.setPrefSize(800, 600);

		StackPane sp = new StackPane();
		sp.getChildren().addAll(vboxOne);
		sp.setStyle("-fx-background-color: #FFFFFF;");
		sp.autosize();
		scene1 = new Scene(sp);
		
		headerBtn.setId("allBtn");
		
		s2hbox2 = new HBox(5);
		s2hbox2.setPadding(new Insets(10,10,0,10));
		s2hbox2.isResizable();
		s2hbox2.setPrefSize(800, 600);
		//s2hbox2.autosize();
		
		btnNext.setAlignment(Pos.CENTER);
		btnNext.setId("allBtn");
		prvBtn.setId("allBtn");
		HBox nexthbox = new HBox(5);
		nexthbox.setPadding(new Insets(0,0,0,0));
		nexthbox.getChildren().addAll(btnNext, prvBtn);
		nexthbox.setAlignment(Pos.CENTER);
		
		HBox blank = new HBox(5);
		blank.setPadding(new Insets(0, 482, 0, 0));
		
		GridPane botgp = new GridPane();
		//botgp.setConstraints(blank,0,0);
		botgp.setConstraints(nexthbox,3,0);
		botgp.getChildren().addAll(nexthbox);
		botgp.setAlignment(Pos.CENTER);
		
		Label helpinfo3 = new Label("※ 원본의");
		Label helpinfo = new Label("초록색");
		helpinfo.setTextFill(Color.SEAGREEN);
		
		Label helpinfo2 = new Label("글자는 변경 전의 데이터를 나타내며, ");
		Label helpinfo4 = new Label("빨간색");
		helpinfo4.setTextFill(Color.RED);
		
		Label helpinfo5 = new Label("글자는 선택하신 데이터 타입으로 값 판단 여부가 불확실한 경우 입니다. (정비 불가)");
		
		HBox infobox = new HBox(5);
		infobox.setPadding(new Insets(15, 0, 0, 0));
		infobox.getChildren().addAll(helpinfo3, helpinfo, helpinfo2, helpinfo4, helpinfo5);
		infobox.setAlignment(Pos.BOTTOM_LEFT);
		
		Label help2info = new Label("변경의");
		Label help2info2 = new Label("파란색");
		help2info2.setTextFill(Color.BLUE);
		Label help2info3 = new Label("글자는 원본의 값에서 데이터 정비 후 변경된 값을 나타내며, ");
		Label help2info4 = new Label("빨간색");
		help2info4.setTextFill(Color.RED);
		
		Label help2info5 = new Label("글자는 선택하신 데이터 타입으로 값 판단 여부가 불확실한 경우 입니다. (정비 불가)");
		
		HBox infobox2 = new HBox(5);
		infobox2.setPadding(new Insets(0, 0, 0, 15));
		infobox2.getChildren().addAll(help2info, help2info2, help2info3, help2info4, help2info5);
		
		
		
		// 상단 버튼 및 기능
		// 이전
		Button btnInspPrev = new Button( "이전" );
		btnInspPrev.setId("largeBtn");
		btnInspPrev.setPadding(new Insets(5, 15, 5, 15));
		btnInspPrev.setOnAction(event->{
			window.setScene(scene1);
		});
		btnInspPrev.setMinHeight(59.0);
		btnInspPrev.setMaxHeight(59.0);
		VBox subvbox15 = new VBox(5);
		subvbox15.setPadding(new Insets(10, 15, 0, 5));
		subvbox15.getChildren().addAll(btnInspPrev);
		// 보고서 생성
		Button btnInspDoc = new Button( "보고서\n 생성" );
		btnInspDoc.setId("largeBtn");
		btnInspDoc.setPadding(new Insets(5, 15, 5, 15));
		VBox subvbox16 = new VBox(5);
		subvbox16.setPadding(new Insets(10, 15, 0, 5));
		subvbox16.getChildren().addAll(btnInspDoc);

		Label lblInspDate =new Label("진단 기간");
		lblInspDate.setMinWidth(80);
		lblInspDate.setPadding(new Insets(5,0,0,0));
		
		// 진단기간 시작일
		TextField txtInspDtSt = new TextField("2020-10-01");
		txtInspDtSt.setMinWidth(100);
		txtInspDtSt.setStyle(Const.fontTxt_1);
		Label lblInspDateMid =new Label(" ~ ");
		lblInspDateMid.setMinWidth(60);
		lblInspDateMid.setPadding(new Insets(5,0,0,20));
		// 진단기간 종료일
		TextField txtInspDtEd = new TextField("2020-12-31");
		txtInspDtEd.setMinWidth(100);
		txtInspDtEd.setStyle(Const.fontTxt_1);

		// 상단 진단기간 Hbox
		HBox hboxInspDate = new HBox(5);
		hboxInspDate.setPadding(new Insets(15, 0, 10, 0));
		hboxInspDate.getChildren().addAll(lblInspDate, txtInspDtSt, lblInspDateMid, txtInspDtEd);


		// 보고서저장 버튼
		btnInspDoc.setOnAction(event -> {
			FileChooser fcXls = new FileChooser();
			fcXls.getExtensionFilters().add(new FileChooser.ExtensionFilter("XLS files (*.xls)", "*.xls"));
			fcXls.setInitialFileName("개방파일 품질진단 종합결과");
			try {
				File inspDocFile = fcXls.showSaveDialog(window);
				String orgNm = cboInspOrg.getSelectionModel().getSelectedItem().getVal();
				String dbNm = cboInspDb.getSelectionModel().getSelectedIndex() == 0 ? txtInspDbInput.getText() : cboInspDb.getSelectionModel().getSelectedItem().getVal();
				if (inspDocFile != null) {
					if (mc.writeResultXls(inspDocFile, dataList, orgDataList, cngDataList, cngMsgDataList, errMsgDataList, typDataList, openFileNm, txtInspDtSt.getText(), txtInspDtEd.getText(), orgNm, dbNm)) {
						getAlertOpen("보고서 저장", "보고서가 저장되었습니다.", AlertType.INFORMATION).showAndWait();
					} else {
						getAlertOpen("보고서 저장", "보고서 저장중 오류가 발생했습니다.", AlertType.WARNING).showAndWait();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
	    } );

		
		Label lblInspOrg =new Label("기 관 명");
		lblInspOrg.setMinWidth(80);
		lblInspOrg.setPadding(new Insets(5,5,0,5));
		
		// 기관명 Combobox
		oblInspOrgList = FXCollections.observableArrayList(orgList);
		cboInspOrg = new ComboBox<OrgVO>(oblInspOrgList);
		cboInspOrg.setMinWidth(172);
		cboInspOrg.setMaxWidth(220);
		cboInspOrg.getSelectionModel().selectFirst();
		cboInspOrg.setOnAction(e -> {
			setCboInspDb();
		});
		cboInspOrg.setConverter(new StringConverter<OrgVO>() {
		    @Override
		    public String toString(OrgVO vo) {
		        return vo.getVal();
		    }
		    @Override
		    public OrgVO fromString(String val) {
		        return null;
		    }
		});		

		Label lblInspDb =new Label("DB명");
		lblInspDb.setMinWidth(60);
		lblInspDb.setPadding(new Insets(5,0,0,20));

		// DB명 Combobox
		oblInspDbList = FXCollections.observableArrayList(new ArrayList<OrgVO>());
		cboInspDb = new ComboBox<OrgVO>(oblInspDbList);
		cboInspDb.setMinWidth(172);
		cboInspDb.setMaxWidth(220);
		cboInspDb.setConverter(new StringConverter<OrgVO>() {
		    @Override
		    public String toString(OrgVO vo) {
		        return vo.getVal();
		    }
		    @Override
		    public OrgVO fromString(String val) {
		        return null;
		    }
		});		
		setCboInspDb(); // DB명 로드

		// DB명 직접입력
		txtInspDbInput = new TextField("");
		txtInspDbInput.setMinWidth(100);
		txtInspDbInput.setMaxWidth(150);
		txtInspDbInput.setStyle(Const.fontTxt_1);
		txtInspDbInput.setVisible(true);

		// DB명 콤보박스 이벤트
		cboInspDb.setOnAction(e -> {
			if (cboInspDb.getSelectionModel().getSelectedIndex() == 0) {
				txtInspDbInput.setVisible(true);
			} else {
				txtInspDbInput.setVisible(false);
			}
		});
		
		// 상단 진단기간 Hbox
		HBox hboxInspOrg = new HBox(5);
		hboxInspOrg.setPadding(new Insets(0, 0, 0, 0));
		hboxInspOrg.getChildren().addAll(lblInspOrg, cboInspOrg, lblInspDb, cboInspDb, txtInspDbInput);
		
		VBox vboxInspTmp = new VBox();
		vboxInspTmp.setPadding(new Insets(0, 0, 0, 0));
		vboxInspTmp.getChildren().addAll(hboxInspDate, hboxInspOrg);
		
		
		Label tmpLabel12 = new Label("");
		tmpLabel12.setMinWidth(30.0);
		Label tmpLabel13 = new Label("");
		tmpLabel13.setMinWidth(20.0);
		HBox hboxInspTop = new HBox(5);
		hboxInspTop.setPadding(new Insets(0, 0, 10, 0));
		hboxInspTop.setStyle("-fx-border-style: solid none solid none;-fx-border-width: 2;-fx-border-color: black;");
		hboxInspTop.getChildren().addAll(subvbox15, subvbox16, tmpLabel12, vboxInspTmp);


		
		// 하단 안내문구
		Label botinfo11 = new Label("화면설명");
		botinfo11.setPadding(new Insets(0, 0, 0, 0));
		botinfo11.setMaxHeight(Double.MAX_VALUE);
		botinfo11.setStyle("-fx-font-weight: bold;");
		botinfo11.setFont(Font.font("Arial", FontWeight.BOLD, 16));
		
		Label botinfo12 = new Label("1.  [이전] 버튼을 클릭하면, 직전 화면으로 전환됩니다.");
		botinfo12.setPadding(new Insets(0, 0, 0, 0));
		botinfo12.setMaxHeight(Double.MAX_VALUE);
		
		Label botinfo13 = new Label("2.  [보고서 생성] 버튼을 클릭하면 종합결과보고서(Excel)가 선택한 폴더에 생성됩니다.");
		botinfo13.setPadding(new Insets(0, 0, 0, 0));
		botinfo13.setMaxHeight(Double.MAX_VALUE);

		Label botinfo14 = new Label("3.  진단기관 및 기관명은 종합결과보고서(Excel)에 필요한 정보입니다.");
		botinfo14.setPadding(new Insets(0, 0, 0, 0));
		botinfo14.setMaxHeight(Double.MAX_VALUE);
		
		Label botinfo15 = new Label("4.  스프레드에 ");
		botinfo15.setPadding(new Insets(0, 0, 0, 0));
		botinfo15.setMaxHeight(Double.MAX_VALUE);
		Label botinfo16 = new Label("빨간색");
		botinfo16.setPadding(new Insets(0, 0, 0, 0));
		botinfo16.setMaxHeight(Double.MAX_VALUE);
		botinfo16.setTextFill(Color.RED);
		Label botinfo17 = new Label(" 글자는 품질진단기준에 위배되는 데이터 입니다.");
		botinfo17.setPadding(new Insets(0, 0, 0, 0));
		botinfo17.setMaxHeight(Double.MAX_VALUE);

		HBox hboxBotInfo = new HBox();
		hboxBotInfo.setPadding(new Insets(0, 0, 0, 0));
		hboxBotInfo.setMaxHeight(Double.MAX_VALUE);
		hboxBotInfo.getChildren().addAll(botinfo15, botinfo16, botinfo17);
		
		VBox vboxInfo2 = new VBox();
		vboxInfo2.getChildren().addAll(botinfo11, botinfo12, botinfo13, botinfo14, hboxBotInfo);

		testbox3 = new HBox();
		testbox3.getChildren().addAll(vboxInfo2);
		testbox3.setPadding(new Insets(20, 0, 10, 10));

		VBox vbox2 = new VBox();
		//vbox.getChildren().addAll(s1Header ,hbox,headerGP,tableview);
//		spane.setMaxWidth(vbox2.getWidth());
		//vbox2.setVgrow(s2hbox2, Priority.ALWAYS);
		vbox2.getChildren().addAll(hboxInspTop, s2hbox2, testbox3);
		vbox2.setPadding(new Insets(10, 10, 10, 10));
		vbox2.setPrefSize(800, 600);
		
		vbox2.setStyle("-fx-background-color: #FFFFFF;");
		vbox2.autosize();
		
		scene2 = new Scene(vbox2);
		
		HBox hbox2 = new HBox(15);
    	hbox2.getChildren().addAll(pBar, pind, txtstate);
    	hbox2.setPadding(new Insets(320,0,0,370));
		hbox2.setAlignment(Pos.CENTER);
		
		Group root2 = new Group();
    	root2.getChildren().addAll(hbox2);
		
    	scene3 = new Scene(root2);

    	
    	StackPane sp2 = new StackPane();
		sp2.getChildren().addAll(vboxThree);
		
		sp2.setStyle("-fx-background-color: #FFFFFF;");
	
		sp2.autosize();
    	sceneThree = new Scene(sp2);

    	ChangeListener<Number> stageSizeListener = ((observable, oldValue, newValue) -> {
    		Const.defStageHeight = stage.getHeight();
    		Const.defStageWidth = stage.getWidth();
    	});
        window.widthProperty().addListener(stageSizeListener);
        window.heightProperty().addListener(stageSizeListener); 
    
    	window.setScene(scene1);
    	window.setTitle(getPropVal("main.title"));
		scene1.getStylesheets().add(Const.mainCss);
		scene2.getStylesheets().add(Const.mainCss2);
		sceneThree.getStylesheets().add(Const.mainCss2);
		window.show();
		
		if (prop == null) getAlertOpen("프로퍼티 파일", "프로퍼티 파일이 존재하지 않습니다.", AlertType.WARNING).showAndWait();
	}
	
	public static void main(String[] args) {
		launch(args);
	}

    /**
     * @param pageIndex
     * @return
     */
    public VBox createPage() {
		comboitemSeqList = new ArrayList<ItemVO>();
		ObservableList<ItemVO> comboitemList = FXCollections.observableArrayList(Const.getItemCntsList());
		
	    VBox box = new VBox(5);

    	TableView page2TblView = new TableView();
        
		int coldiv = dataList.get(0).length >= 8 ? 8 : dataList.get(0).length;
		for (int j = 0; j < dataList.get(comboNo).length; j++) {
			
			final int k = j;
			TableColumn col2 = new TableColumn(dataList.get(comboNo)[j]);

			col2.setCellValueFactory(
					new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
						public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
							return new SimpleStringProperty(param.getValue().get(k).toString());
						}
					});

			comboitemSeqList.add(Const.getKeyToItem(Const.CHK_STRING)); // 최초 Default 로 문자열 셋팅
			if (j > 0) {
				ComboBox<ItemVO> cbb = new ComboBox<ItemVO>(comboitemList);
				TableColumn combcol2 = new TableColumn();
				combcol2.setGraphic(cbb);
				cbb.setOnAction(e -> {
					ItemVO itemVO = cbb.getSelectionModel().getSelectedItem();
					String key2 = itemVO.getKey2();
					if (key2.equals(Const.CHK_FLAG+"-2") || key2.equals(Const.CHK_DATEORD) || key2.equals(Const.CHK_LOGIC) || key2.startsWith(Const.CHK_CALC)) {
						createChkDialog(k, cbb, dataList.get(comboNo)[k]);
					} else {
						comboitemSeqList.set(k, itemVO);
					}
				});
				
				
				cbb.getSelectionModel().selectFirst();
	
				combcol2.setCellValueFactory(
						new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
							public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
								return new SimpleStringProperty(param.getValue().get(k).toString());
							}
						});
				combcol2.setSortable(false);
				col2.getColumns().add(combcol2);
				combcol2.prefWidthProperty().bind(page2TblView.widthProperty().divide(coldiv));
			}			
			if (colChrLen.size()+1 > j) col2.setPrefWidth(Double.valueOf(getLenWidth(colChrLen.get(j))+""));
			else col2.prefWidthProperty().bind(page2TblView.widthProperty().divide(coldiv));
			if (j == 0) col2.setMinWidth(40);
			page2TblView.getColumns().addAll(col2);

		}
		
		page2TblView.prefHeightProperty().bind(stage.heightProperty());
		page2TblView.prefWidthProperty().bind(stage.widthProperty());
		
		page2TblView.setItems(defData);

        box.getChildren().add(page2TblView);
	    return box;
	}

    private RadioButton getGrpRdo(String txt, String idx, String val, ToggleGroup grp, boolean sel) {
    	ChkVO chkVO = new ChkVO();
    	chkVO.setTxt(txt);
    	chkVO.setIdx(idx);
    	chkVO.setVal(val);
    	RadioButton rdo = new RadioButton();
        rdo.setText(txt);
        rdo.setUserData(chkVO);
        rdo.setToggleGroup(grp);
        rdo.setPadding(new Insets(0,15,0,5)); //top, right, bottom, left
        if (sel) rdo.setSelected(true);
        return rdo;
    }

    private void createChkDialog(int k, ComboBox<ItemVO> cbb, String itemTxt) {
    	try {
    		ItemVO itemVO = cbb.getSelectionModel().getSelectedItem();
    		if (itemVO.getKey2().equals(Const.CHK_FLAG+"-2")) createChkDialog_Flag(k, cbb, itemTxt);
    		else if (itemVO.getKey2().equals(Const.CHK_DATEORD)) createChkDialog_DateOrd(k, cbb, itemTxt);
    		else if (itemVO.getKey2().equals(Const.CHK_LOGIC)) createChkDialog_Logic(k, cbb, itemTxt);
    		else if (itemVO.getKey2().equals(Const.CHK_CALC + "-1")) createChkDialog_Calc(k, cbb, itemTxt);
    		else if (itemVO.getKey2().equals(Const.CHK_CALC + "-2")) createChkDialog_Calc2(k, cbb, itemTxt);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    /*
     * 여부값 지정 팝업
     */
    private void createChkDialog_Flag(int k, ComboBox<ItemVO> cbb, String itemTxt) throws Exception {
    	ItemVO itemVO = cbb.getSelectionModel().getSelectedItem();
		Stage dialog = new Stage(StageStyle.UNDECORATED);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(window);
        dialog.setTitle("확인"); // dialog title

        VBox vBox = (VBox) FXMLLoader.load(Class.forName("application.Main").getResource(Const.chkDialogSmallFxml));
		
        ((Label) vBox.lookup("#lblTit")).setText("※ 사용법");        
        ((Label) vBox.lookup("#lblInfo")).setText("1. 진단대상컬럼의 여부 유효값 입력       2. 확인");
		
		
        /*
         * Mid 컨텐츠 영역 시작
         */
        HBox hbxMidCnts = (HBox)vBox.lookup("#hbxMidCnts");

        // 비교 값 텍스트필드
		TextField txtFlagComp = new TextField("Y");
		txtFlagComp.setMinWidth(50);
		txtFlagComp.setStyle(Const.fontTxt_1);
		txtFlagComp.setPromptText("여부 값은 필수입력입니다.");
		
        // 비교 값 텍스트필드
		TextField txtFlagComp2 = new TextField("N");
		txtFlagComp2.setMinWidth(50);
		txtFlagComp2.setStyle(Const.fontTxt_1);
		txtFlagComp2.setPromptText("여부 값은 필수입력입니다.");

        // Default 비교 메시지 지정
		Label lblFlag_CompTxt = (Label) vBox.lookup("#lblCompTxt");
		lblFlag_CompTxt.setText("입력한 품질기준의 유효값은 '" + txtFlagComp.getText() + "' 과(와) '" + txtFlagComp2.getText() + "' 값만 유효하다");

		txtFlagComp.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {
            	lblFlag_CompTxt.setText("입력한 품질기준의 유효값은 '" + txtFlagComp.getText() + "' 과(와) '" + txtFlagComp2.getText() + "' 값만 유효하다");
            }
        });
		txtFlagComp.setOnKeyReleased(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {
            	lblFlag_CompTxt.setText("입력한 품질기준의 유효값은 '" + txtFlagComp.getText() + "' 과(와) '" + txtFlagComp2.getText() + "' 값만 유효하다");
            }
        });
		txtFlagComp2.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {
            	lblFlag_CompTxt.setText("입력한 품질기준의 유효값은 '" + txtFlagComp.getText() + "' 과(와) '" + txtFlagComp2.getText() + "' 값만 유효하다");
            }
        });
		txtFlagComp2.setOnKeyReleased(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {
            	lblFlag_CompTxt.setText("입력한 품질기준의 유효값은 '" + txtFlagComp.getText() + "' 과(와) '" + txtFlagComp2.getText() + "' 값만 유효하다");
            }
        });
        
        Label lblFirstTxt = new Label();
        lblFirstTxt.prefWidth(100.0);
        lblFirstTxt.setPadding(new Insets(5,15,5,15));
        lblFirstTxt.setText("선택컬럼의 유효값은");
        Label lblMidTxt = new Label();
        lblMidTxt.prefWidth(80.0);
        lblMidTxt.setPadding(new Insets(5,15,5,15));
        lblMidTxt.setText("과(와)");
        Label lblLastTxt = new Label();
        lblLastTxt.prefWidth(80.0);
        lblLastTxt.setPadding(new Insets(5,15,5,15));
        lblLastTxt.setText("이다");
        
        // 비교 체크박스
        HBox hbxRdoGrp = new HBox();
        hbxRdoGrp.prefWidth(250.0);
        hbxRdoGrp.prefHeight(39.0);
        hbxRdoGrp.setPadding(new Insets(5,15,5,15));
        
        hbxMidCnts.getChildren().addAll(lblFirstTxt, txtFlagComp, lblMidTxt, txtFlagComp2, lblLastTxt);
        /*
         * Mid 컨텐츠 영역 종료
         */
        
        // 확인 버튼
        Button btnConfirm = (Button) vBox.lookup("#btnConfirm");
        btnConfirm.setId("confirmBtn");
        btnConfirm.setOnAction(event -> {
        	if (txtFlagComp.getText().trim().isEmpty() || txtFlagComp2.getText().trim().isEmpty()) return;
        	ItemVO setItemVO = Const.getKeyToItem(Const.CHK_FLAG+"-2");
        	setItemVO.setOpt1(""+txtFlagComp.getText());
        	setItemVO.setOpt2(""+txtFlagComp2.getText());
        	comboitemSeqList.set(k, setItemVO);
        	
        	dialog.close();
        });
        
        // 취소 버튼
        Button btnCancel = (Button) vBox.lookup("#btnCancel");
        btnCancel.setId("cancelBtn");
        btnCancel.setOnAction(event -> {
        	cbb.getSelectionModel().selectFirst();
        	dialog.close();
        });

        
        Scene scene = new Scene(vBox);
        scene.getStylesheets().add(Const.mainCss);
        dialog.setScene(scene);
        dialog.setResizable(false);
        dialog.show();
        dialog.setX(window.getX() + (window.getWidth() > dialog.getWidth() ? (window.getWidth() - dialog.getWidth()) / 2 : 50));
        dialog.setY(window.getY()+250);
        
    }

    /*
     * 시간순서 일관성 팝업
     */
    private void createChkDialog_DateOrd(int k, ComboBox<ItemVO> cbb, String itemTxt) throws Exception {
    	ItemVO itemVO = cbb.getSelectionModel().getSelectedItem();
		Stage dialog = new Stage(StageStyle.UNDECORATED);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(window);
        dialog.setTitle("확인"); // dialog title

        VBox vBox = (VBox) FXMLLoader.load(Class.forName("application.Main").getResource(Const.chkDialogFxml));
		
        ((Label) vBox.lookup("#lblTit")).setText("※ 사용법");        
        ((Label) vBox.lookup("#lblInfo")).setText("1. 진단대상컬럼의 비교컬럼 선택       2. 크기 선택       3. 확인");
		
		
        /*
         * Mid 컨텐츠 영역 시작
         */
        HBox hbxMidCnts = (HBox)vBox.lookup("#hbxMidCnts");

        // 비교 콤보박스
        ComboBox headerCombo = new ComboBox();
        headerCombo.prefHeight(24.0);
        for (int i = 0; i < dataList.get(0).length; i++) {
        	if (i > 0) headerCombo.getItems().add(dataList.get(0)[i]);
        }
        headerCombo.setOnAction(e -> {
        	lblDateOrd_CompTxt.setText("지정한 품질기준은 '" + itemTxt + "' 컬럼은 '" + headerCombo.getSelectionModel().getSelectedItem() + "' 컬럼보다 " + strDateOrd_CompVal);
		});
        headerCombo.getSelectionModel().selectFirst();
        
        Label lblFirstTxt = new Label();
        lblFirstTxt.prefWidth(100.0);
        lblFirstTxt.setPadding(new Insets(5,15,5,15));
        lblFirstTxt.setText("선택컬럼은");
        Label lblMidTxt = new Label();
        lblMidTxt.prefWidth(80.0);
        lblMidTxt.setPadding(new Insets(5,15,5,15));
        lblMidTxt.setText("컬럼보다");
        
        // 비교 체크박스
        HBox hbxRdoGrp = new HBox();
        hbxRdoGrp.prefWidth(250.0);
        hbxRdoGrp.prefHeight(39.0);
        hbxRdoGrp.setPadding(new Insets(5,15,5,15));
        
        ToggleGroup rdoCompGrp = new ToggleGroup();
        hbxRdoGrp.getChildren().add(getGrpRdo(">=", "1", "크거나 같아야 한다", rdoCompGrp, true));
        hbxRdoGrp.getChildren().add(getGrpRdo(">", "2", "커야 한다", rdoCompGrp, false));
        hbxRdoGrp.getChildren().add(getGrpRdo("<=", "3", "작거나 같아야 한다", rdoCompGrp, false));
        hbxRdoGrp.getChildren().add(getGrpRdo("<", "4", "작아야 한다", rdoCompGrp, false));
        rdoCompGrp.selectedToggleProperty().addListener((obserableValue, old_toggle, new_toggle) -> {
            if (rdoCompGrp.getSelectedToggle() != null) {
            	ChkVO chkVO = (ChkVO)rdoCompGrp.getSelectedToggle().getUserData();
            	strDateOrd_CompVal = chkVO.getVal();
            	lblDateOrd_CompTxt.setText("지정한 품질기준은 '" + itemTxt + "' 컬럼은 '" + headerCombo.getSelectionModel().getSelectedItem() + "' 컬럼보다 " + strDateOrd_CompVal);
            }
        });
        
        hbxMidCnts.getChildren().addAll(lblFirstTxt, headerCombo, lblMidTxt, hbxRdoGrp);
        /*
         * Mid 컨텐츠 영역 종료
         */

        
        // Default 비교 메시지 지정
        lblDateOrd_CompTxt = (Label) vBox.lookup("#lblCompTxt");
        strDateOrd_CompVal = "크거나 같아야 한다";
        lblDateOrd_CompTxt.setText("지정한 품질기준은 '" + itemTxt + "' 컬럼은 '" + headerCombo.getSelectionModel().getSelectedItem() + "' 컬럼보다 " + strDateOrd_CompVal);
        
        // 확인 버튼
        Button btnConfirm = (Button) vBox.lookup("#btnConfirm");
        btnConfirm.setId("confirmBtn");
        btnConfirm.setOnAction(event -> {
        	ItemVO setItemVO = Const.getKeyToItem(Const.CHK_DATEORD);
        	setItemVO.setOpt1(""+headerCombo.getSelectionModel().getSelectedIndex());
        	setItemVO.setOpt2(""+((ChkVO)rdoCompGrp.getSelectedToggle().getUserData()).getIdx());
        	comboitemSeqList.set(k, setItemVO);
        	dialog.close();
        });
        
        // 취소 버튼
        Button btnCancel = (Button) vBox.lookup("#btnCancel");
        btnCancel.setId("cancelBtn");
        btnCancel.setOnAction(event -> {
        	cbb.getSelectionModel().selectFirst();
        	dialog.close();
        });

        
        Scene scene = new Scene(vBox);
        scene.getStylesheets().add(Const.mainCss);
        dialog.setScene(scene);
        dialog.setResizable(false);
        dialog.show();
        dialog.setX(window.getX() + (window.getWidth() > dialog.getWidth() ? (window.getWidth() - dialog.getWidth()) / 2 : 50));
        dialog.setY(window.getY()+250);
    }
    
    /*
     * 컬럼 간 논리관계 일관성 팝업
     */
    private void createChkDialog_Logic(int k, ComboBox<ItemVO> cbb, String itemTxt) throws Exception {
    	ItemVO itemVO = cbb.getSelectionModel().getSelectedItem();
		Stage dialog = new Stage(StageStyle.UNDECORATED);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(window);
        dialog.setTitle("확인"); // dialog title

        VBox vBox = (VBox) FXMLLoader.load(Class.forName("application.Main").getResource(Const.chkDialogFxml));
		
        ((Label) vBox.lookup("#lblTit")).setText("※ 사용법");        
        ((Label) vBox.lookup("#lblInfo")).setText("1. 진단대상컬럼에 조건 값 입력       2. 논리관계가 있는 날짜 컬럼 선택       3. 확인");
		
		
        /*
         * Mid 컨텐츠 영역 시작
         */
        HBox hbxMidCnts = (HBox)vBox.lookup("#hbxMidCnts");

        // 비교 값 텍스트필드
		TextField txtLogicComp = new TextField("Y");
		txtLogicComp.setMinWidth(50);
		txtLogicComp.setStyle(Const.fontTxt_1);
		txtLogicComp.setPromptText("조건 값은 필수입력입니다.");
		
        // 날짜컬럼 콤보박스
        ComboBox headerCombo = new ComboBox();
        headerCombo.prefHeight(24.0);
        headerCombo.setStyle(Const.fontCbo_1);
        for (int i = 0; i < dataList.get(0).length; i++) {
        	if (i > 0) headerCombo.getItems().add(dataList.get(0)[i]);
        }
        headerCombo.setOnAction(e -> {
        	lblDateOrd_CompTxt.setText("지정한 품질기준은 '" + itemTxt + "' 컬럼이 '" + txtLogicComp.getText() + "' (이)라면 " + headerCombo.getSelectionModel().getSelectedItem() + "' 컬럼은 반드시 날짜가 존재해야 한다 ");
		});
        headerCombo.getSelectionModel().selectFirst();

        Label lblFirstTxt = new Label();
        lblFirstTxt.minWidth(80.0);
        lblFirstTxt.setPadding(new Insets(5,10,5,10));
        lblFirstTxt.setText("선택컬럼이");
        Label lblMidTxt = new Label();
        lblMidTxt.minWidth(30.0);
        lblMidTxt.setPadding(new Insets(5,10,5,10));
        lblMidTxt.setText("라면");
        Label lblLastTxt = new Label();
        lblLastTxt.minWidth(180.0);
        lblLastTxt.setPadding(new Insets(5,10,5,10));
        lblLastTxt.setText("컬럼은 반드시 날짜가 존재해야 한다");        
        
        hbxMidCnts.getChildren().addAll(lblFirstTxt, txtLogicComp, lblMidTxt, headerCombo, lblLastTxt);
        /*
         * Mid 컨텐츠 영역 종료
         */

        // Default 비교 메시지 지정
        lblDateOrd_CompTxt = (Label) vBox.lookup("#lblCompTxt");
        lblDateOrd_CompTxt.setText("지정한 품질기준은 '" + itemTxt + "' 컬럼이 '" + txtLogicComp.getText() + "' (이)라면 " + headerCombo.getSelectionModel().getSelectedItem() + "' 컬럼은 반드시 날짜가 존재해야 한다 ");
        
        // 확인 버튼
        Button btnConfirm = (Button) vBox.lookup("#btnConfirm");
        btnConfirm.setId("confirmBtn");
        btnConfirm.setOnAction(event -> {
        	if (txtLogicComp.getText().trim().isEmpty()) return;
        	ItemVO setItemVO = Const.getKeyToItem(Const.CHK_LOGIC);
        	setItemVO.setOpt1(""+txtLogicComp.getText());
        	setItemVO.setOpt2(""+headerCombo.getSelectionModel().getSelectedIndex());
        	comboitemSeqList.set(k, setItemVO);
        	dialog.close();
        });
        
        // 취소 버튼
        Button btnCancel = (Button) vBox.lookup("#btnCancel");
        btnCancel.setId("cancelBtn");
        btnCancel.setOnAction(event -> {
        	cbb.getSelectionModel().selectFirst();
        	dialog.close();
        });

        
        Scene scene = new Scene(vBox);
        scene.getStylesheets().add(Const.mainCss);
        dialog.setScene(scene);
        dialog.setResizable(false);
        dialog.show();
        dialog.setX(window.getX() + (window.getWidth() > dialog.getWidth() ? (window.getWidth() - dialog.getWidth()) / 2 : 50));
        dialog.setY(window.getY()+250);
    }

    /*
     * 계산식 > 산식 팝업
     */
    private void createChkDialog_Calc(int k, ComboBox<ItemVO> cbb, String itemTxt) throws Exception {
    	ItemVO itemVO = cbb.getSelectionModel().getSelectedItem();
		Stage dialog = new Stage(StageStyle.UNDECORATED);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(window);
        dialog.setTitle("확인"); // dialog title

        VBox vBox = (VBox) FXMLLoader.load(Class.forName("application.Main").getResource(Const.chkDialogFxml));
		
        ((Label) vBox.lookup("#lblTit")).setText("※ 사용법");        
        ((Label) vBox.lookup("#lblInfo")).setText("1. 진단대상컬럼의 첫번째 비교컬럼 선택       2. 계산식 선택       3. 두번째 비교컬럼 선택       4. 확인");
		
		
        /*
         * Mid 컨텐츠 영역 시작
         */
        HBox hbxMidCnts = (HBox)vBox.lookup("#hbxMidCnts");

        // 비교 콤보박스
        ComboBox headerCombo = new ComboBox();
        headerCombo.prefHeight(24.0);
        for (int i = 0; i < dataList.get(0).length; i++) {
        	if (i > 0) headerCombo.getItems().add(dataList.get(0)[i]);
        }

        // 비교 콤보박스2
        ComboBox headerCombo2 = new ComboBox();
        headerCombo2.prefHeight(24.0);
        for (int i = 0; i < dataList.get(0).length; i++) {
        	if (i > 0) headerCombo2.getItems().add(dataList.get(0)[i]);
        }

        headerCombo.setOnAction(e -> {
        	lblCalc_CompTxt.setText("지정한 품질기준은 '" + itemTxt + "' 컬럼은 '" + headerCombo.getSelectionModel().getSelectedItem() + "' 컬럼과 '" + headerCombo2.getSelectionModel().getSelectedItem() + "' 컬럼의 '" + lblCalc_CompVal + "' 와 같다");
		});
        headerCombo2.setOnAction(e -> {
        	lblCalc_CompTxt.setText("지정한 품질기준은 '" + itemTxt + "' 컬럼은 '" + headerCombo.getSelectionModel().getSelectedItem() + "' 컬럼과 '" + headerCombo2.getSelectionModel().getSelectedItem() + "' 컬럼의 '" + lblCalc_CompVal + "' 와 같다");
		});
        headerCombo.getSelectionModel().selectFirst();
        headerCombo2.getSelectionModel().selectFirst();
        
        Label lblFirstTxt = new Label();
        lblFirstTxt.prefWidth(60.0);
        lblFirstTxt.setPadding(new Insets(5,10,5,10));
        lblFirstTxt.setText("선택컬럼은");
        Label lblMidTxt = new Label();
        lblMidTxt.prefWidth(50.0);
        lblMidTxt.setPadding(new Insets(5,10,5,10));
        lblMidTxt.setText("와 같다");
        
        // 비교 체크박스
        HBox hbxRdoGrp = new HBox();
        hbxRdoGrp.prefWidth(250.0);
        hbxRdoGrp.prefHeight(39.0);
        hbxRdoGrp.setPadding(new Insets(5,15,5,15));
        
        ToggleGroup rdoCompGrp = new ToggleGroup();
        hbxRdoGrp.getChildren().add(getGrpRdo("+", "1", "+", rdoCompGrp, true));
        hbxRdoGrp.getChildren().add(getGrpRdo("-", "2", "-", rdoCompGrp, false));
        hbxRdoGrp.getChildren().add(getGrpRdo("X", "3", "X", rdoCompGrp, false));
        hbxRdoGrp.getChildren().add(getGrpRdo("/", "4", "/", rdoCompGrp, false));
        rdoCompGrp.selectedToggleProperty().addListener((obserableValue, old_toggle, new_toggle) -> {
            if (rdoCompGrp.getSelectedToggle() != null) {
            	ChkVO chkVO = (ChkVO)rdoCompGrp.getSelectedToggle().getUserData();
            	lblCalc_CompVal = chkVO.getVal();
            	lblCalc_CompTxt.setText("지정한 품질기준은 '" + itemTxt + "' 컬럼은 '" + headerCombo.getSelectionModel().getSelectedItem() + "' 컬럼과 '" + headerCombo2.getSelectionModel().getSelectedItem() + "' 컬럼의 '" + lblCalc_CompVal + "' 와 같다");
            }
        });
        
        hbxMidCnts.getChildren().addAll(lblFirstTxt, headerCombo, hbxRdoGrp, headerCombo2, lblMidTxt);
        /*
         * Mid 컨텐츠 영역 종료
         */

        
        // Default 비교 메시지 지정
        lblCalc_CompTxt = (Label) vBox.lookup("#lblCompTxt");
        lblCalc_CompVal = "+";
        lblCalc_CompTxt.setText("지정한 품질기준은 '" + itemTxt + "' 컬럼은 '" + headerCombo.getSelectionModel().getSelectedItem() + "' 컬럼과 '" + headerCombo2.getSelectionModel().getSelectedItem() + "' 컬럼의 '" + lblCalc_CompVal + "' 와 같다");
        
        // 확인 버튼
        Button btnConfirm = (Button) vBox.lookup("#btnConfirm");
        btnConfirm.setId("confirmBtn");
        btnConfirm.setOnAction(event -> {
        	ItemVO setItemVO = Const.getKeyToItem(Const.CHK_CALC+"-1");
        	setItemVO.setOpt1(""+headerCombo.getSelectionModel().getSelectedIndex());
        	setItemVO.setOpt2(lblCalc_CompVal);
        	setItemVO.setOpt3(""+headerCombo2.getSelectionModel().getSelectedIndex());
        	comboitemSeqList.set(k, setItemVO);
        	dialog.close();
        });
        
        // 취소 버튼
        Button btnCancel = (Button) vBox.lookup("#btnCancel");
        btnCancel.setId("cancelBtn");
        btnCancel.setOnAction(event -> {
        	cbb.getSelectionModel().selectFirst();
        	dialog.close();
        });

        
        Scene scene = new Scene(vBox);
        scene.getStylesheets().add(Const.mainCss);
        dialog.setScene(scene);
        dialog.setResizable(false);
        dialog.show();
        dialog.setX(window.getX() + (window.getWidth() > dialog.getWidth() ? (window.getWidth() - dialog.getWidth()) / 2 : 50));
        dialog.setY(window.getY()+250);
    }

    /*
     * 계산식 > 합계 팝업
     */
    private void createChkDialog_Calc2(int k, ComboBox<ItemVO> cbb, String itemTxt) throws Exception {
    	ItemVO itemVO = cbb.getSelectionModel().getSelectedItem();
		Stage dialog = new Stage(StageStyle.UNDECORATED);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(window);
        dialog.setTitle("확인"); // dialog title

        VBox vBox = (VBox) FXMLLoader.load(Class.forName("application.Main").getResource(Const.chkDialogFxml));
		
        ((Label) vBox.lookup("#lblTit")).setText("※ 사용법");
        ((Label) vBox.lookup("#lblInfo")).setText("1. 진단대상컬럼의 비교컬럼 선택       2. 확인");
		
		
        /*
         * Mid 컨텐츠 영역 시작
         */
        HBox hbxMidCnts = (HBox)vBox.lookup("#hbxMidCnts");

        // 비교 콤보박스 
        ObservableList<ChkVO> chkVOList = FXCollections.observableArrayList();
        for (int i = 0; i < dataList.get(0).length; i++) {
        	if (i > 0) {
        		ChkVO chkVO = new ChkVO(dataList.get(0)[i], (i-1)+"", "", false);
        		chkVOList.add(chkVO);
        	}
        }
        CheckComboBox<ChkVO> headerCombo = new CheckComboBox<>();
        headerCombo.getItems().addAll(chkVOList);
        headerCombo.getCheckModel().getCheckedItems().addListener(new ListChangeListener() {
        	public void onChanged(ListChangeListener.Change change) {
    			if (lblCalc_CompTxt != null) {
    				lblCalc_CompTxt.setText("지정한 품질기준 '" + itemTxt + "' 컬럼 = " + (headerCombo.getCheckModel().getCheckedItems() != null && headerCombo.getCheckModel().getCheckedItems().size() > 0 ? headerCombo.getCheckModel().getCheckedItems().stream().map(c->"'"+c.getTxt()+"' 컬럼").collect(Collectors.joining(" + ")) : "") + " 이어야 한다");
    			}
        	}
        });
    
        headerCombo.setConverter(new StringConverter<ChkVO>() {
            @Override
            public String toString(ChkVO object) {
            	return object.getTxt();
            }
            @Override
            public ChkVO fromString(String string) {
                return null;
            }
        });
        headerCombo.setPrefWidth(220.0);
        headerCombo.setMaxWidth(220.0);
        headerCombo.getCheckModel().check(0);
        
        Label lblFirstTxt = new Label();
        lblFirstTxt.prefWidth(60.0);
        lblFirstTxt.setPadding(new Insets(5,10,5,10));
        lblFirstTxt.setText("선택컬럼은");
        Label lblMidTxt = new Label();
        lblMidTxt.prefWidth(50.0);
        lblMidTxt.setPadding(new Insets(5,10,5,10));
        lblMidTxt.setText("컬럼들의");
        Label lblCalcTxt = new Label();
        lblCalcTxt.prefWidth(50.0);
        lblCalcTxt.setPadding(new Insets(5,10,5,10));
        lblCalcTxt.setText("더하기 (+)");
        lblCalcTxt.setStyle("-fx-border-style: solid;-fx-border-width: 1;-fx-border-color: green; -fx-text-fill: WHITE; -fx-background-color:GREEN;");
        Label lblLastTxt = new Label();
        lblLastTxt.prefWidth(50.0);
        lblLastTxt.setPadding(new Insets(5,10,5,10));
        lblLastTxt.setText("와 같다");
        
        hbxMidCnts.getChildren().addAll(lblFirstTxt, headerCombo, lblMidTxt, lblCalcTxt, lblLastTxt);
        /*
         * Mid 컨텐츠 영역 종료
         */
        
        // Default 비교 메시지 지정
        lblCalc_CompTxt = (Label) vBox.lookup("#lblCompTxt");
        lblCalc_CompTxt.maxWidth(500.0);
        lblCalc_CompTxt.setText("지정한 품질기준 '" + itemTxt + "' 컬럼 = " + (headerCombo.getCheckModel().getCheckedItems() != null && headerCombo.getCheckModel().getCheckedItems().size() > 0 ? headerCombo.getCheckModel().getCheckedItems().stream().map(c->"'"+c.getTxt()+"' 컬럼").collect(Collectors.joining(" + ")) : "") + " 이어야 한다");
        
        // 확인 버튼
        Button btnConfirm = (Button) vBox.lookup("#btnConfirm");
        btnConfirm.setId("confirmBtn");
        btnConfirm.setOnAction(event -> {
        	ItemVO setItemVO = Const.getKeyToItem(Const.CHK_CALC+"-2");
        	List<String> optList = new ArrayList<String>();
        	for (ChkVO cvo : headerCombo.getCheckModel().getCheckedItems()) {
        		optList.add(cvo.getIdx());
        	}
        	setItemVO.setOptList(optList);
        	comboitemSeqList.set(k, setItemVO);
        	dialog.close();
        });
        
        // 취소 버튼
        Button btnCancel = (Button) vBox.lookup("#btnCancel");
        btnCancel.setId("cancelBtn");
        btnCancel.setOnAction(event -> {
        	cbb.getSelectionModel().selectFirst();
        	dialog.close();
        });

        
        Scene scene = new Scene(vBox);
        scene.getStylesheets().add(Const.mainCss);
        dialog.setScene(scene);
        dialog.setResizable(false);
        dialog.show();
        dialog.setX(window.getX() + (window.getWidth() > dialog.getWidth() ? (window.getWidth() - dialog.getWidth()) / 2 : 50));
        dialog.setY(window.getY()+250);
    }

	//진단결과데이터
	public VBox createPageResult() {
	    VBox box = new VBox(5);

	    resultTblView = new TableView();
		resultTblView.prefHeightProperty().bind(stage.heightProperty());
		resultTblView.prefWidthProperty().bind(stage.widthProperty());
        
		TableColumn col;
		int coldiv = dataList.get(0).length >= 8 ? 8 : dataList.get(0).length;
		for (int j = 0; j < orgDataList.get(comboNo).length; j++) {
			
			final int k = j;
			col = new TableColumn(dataList.get(comboNo)[j]);

			col.setCellValueFactory(
					new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
						public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
							return new SimpleStringProperty(param.getValue().get(k).toString());
						}
					});
			
			PseudoClass specialClass = PseudoClass.getPseudoClass("special");

				col.setCellFactory(tc -> new TextFieldTableCell<ObservableList, String>(TextFormatter.IDENTITY_STRING_CONVERTER) {
				    @Override
				    public void updateItem(String item, boolean empty) {
				        super.updateItem(item, empty);
				        boolean condition = empty;
				        if (!isEmpty()) {
		                    this.setTextFill(Color.BLACK);
		                    ObservableList list = getTableView().getItems().get(getIndex());
		                    if(!errMsgDataList.get(getIndex())[k].isEmpty()) {
		                    	this.setTextFill(Color.RED);
		                    }
		                    setText(item);
		                }
				        pseudoClassStateChanged(specialClass, condition);
				    }
				});

				col.setPrefWidth(180);
				col.setSortable(false);
				if (colChrLen.size()+1 > k) col.setPrefWidth(Double.valueOf(getLenWidth(colChrLen.get(k))+""));
				else col.prefWidthProperty().bind(resultTblView.widthProperty().divide(coldiv));

				resultTblView.getColumns().addAll(col);
		}

		resultTblView.setItems(orgData);
        box.getChildren().addAll(resultTblView);

	    return box;
	}
	
	public static ScrollBar getVerticalScrollbar(Node table) {
	    ScrollBar result = null;
	    for(Node n : table.lookupAll(".scroll-bar")) {
	        if(n instanceof ScrollBar) {
	            ScrollBar bar = (ScrollBar) n;
	            if(bar.getOrientation().equals(Orientation.VERTICAL)) {
	                result = bar;
	            }
	        }
	    }
	    return result;
	}	
	
	public static ScrollBar getHorizontalScrollbar(Node table) {
	    ScrollBar result = null;
	    for(Node n : table.lookupAll(".scroll-bar")) {
	        if(n instanceof ScrollBar) {
	            ScrollBar bar = (ScrollBar) n;
	            if(bar.getOrientation().equals(Orientation.HORIZONTAL)) {
	                result = bar;
	            }
	        }
	    }
	    return result;
	}
	    
	//Create a New Task
    private Task taskCreator(int seconds) {
    	return new Task() {
			@Override
			protected Object call() throws Exception {
				System.out.println("!!!!!!!!!!Inspect-RemoveStartDate:"+new SimpleDateFormat("HH:mm:ss").format(new Date()));

				s2hbox2.getChildren().clear();
				
				orgDataList = new ArrayList<String[]>();
				cngDataList = new ArrayList<String[]>();
				cngMsgDataList = new ArrayList<String[]>();
				errMsgDataList = new ArrayList<String[]>();
				typDataList = new ArrayList<String[]>();
				
				orgData = FXCollections.observableArrayList();
				ObservableList<String> orgRow = FXCollections.observableArrayList();
				
				List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
				
				System.out.println("!!!!!!!!!!Inspect-RemoveEndDate:"+new SimpleDateFormat("HH:mm:ss").format(new Date()));
				
				for(int i=0;i<dataList.get(0).length;i++) {
					Map<String, Object> map = new HashMap<String, Object>();
							map.put(i+"header",dataList.get(0)[i]);
							map.put(i+"datatype",comboitemSeqList.get(i));
							System.out.println("comboitemSeqList.get("+i+"):"+comboitemSeqList.get(i).toStringAll());
							list.add(map);
				}
				System.out.println("!!!!!!!!!!Inspect-listSetEndDate:"+new SimpleDateFormat("HH:mm:ss").format(new Date()));
				
				String fileName = openFileNm.substring(0,openFileNm.length()-4);
				ResInspect resInsp = null;
				try {
					resInsp = mc.FileInspection(openFilePath, comboNo, list);
					System.out.println("!!!!!!!!!!Inspect-InspectSetEndDate:"+new SimpleDateFormat("HH:mm:ss").format(new Date()));
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				
				int count = 0;
				System.out.println("!!!!!!!!!!Inspect-ReaderCsv2EndDate:"+new SimpleDateFormat("HH:mm:ss").format(new Date()));
				
				orgDataList = resInsp.getOriList();
				cngDataList = resInsp.getCngList();
				cngMsgDataList = resInsp.getMsgCngList();
				errMsgDataList = resInsp.getMsgErrList();
				typDataList = resInsp.getTypList();

				System.out.println("!!!!!!!!!!Inspect-DataListSetEndDate:"+new SimpleDateFormat("HH:mm:ss").format(new Date()));
				
				for (int i = 0; i < orgDataList.size(); i++) {
					orgRow = FXCollections.observableArrayList();
					for (int j = 0; j < orgDataList.get(i).length; j++) {
						orgRow.add(orgDataList.get(i)[j]);
					}
					orgData.add(orgRow);
				}
				System.out.println("!!!!!!!!!!Inspect-DataSetEndDate:"+new SimpleDateFormat("HH:mm:ss").format(new Date()));
				
				VBox pageResult = createPageResult();
				System.out.println("!!!!!!!!!!Inspect-createPageResultEndDate:"+new SimpleDateFormat("HH:mm:ss").format(new Date()));
				
		        //2019-08-02 setMaxWidth
				//pageResult.setMaxWidth(Double.MAX_VALUE);
		        //s2hbox2.setHgrow(pageResult, Priority.ALWAYS);
		        s2hbox2.getChildren().add(pageResult);
		        
		        for(int i=0;i<orgDataList.size();i++) {
//		        	Thread.sleep(1000);
					updateProgress(i+1, seconds);
		        }
		        
		        String filepath2 = openFilePath.replace(openFileNm, "");
		        filepath2 = filepath2.replace("\\", "/");
		        filepath2 = filepath2 + "temp";
		        String cngFile = filepath2 + "/" + fileName + "_temp.csv";
		        File file = new File(cngFile);
		        
		         System.out.println("경로"+filepath2);
		        if( file.exists() ){
		            if(file.delete()){
		            }else{
		            }
		        }else{
		        }
		        
		       File folderDelete = new File(filepath2);
		       folderDelete.delete();
		        
				return true;
			}
    	};
    }

    
	//Create a New Task1_2
    private Task taskCreator1_2() {
    	return new Task() {
			@Override
			protected Object call() throws Exception {
				System.out.println("!!!!!!!!!!OpenFile-ReaderStartDate:"+new SimpleDateFormat("HH:mm:ss").format(new Date()));

				colChrLen = new ArrayList<Integer>(); 
				
				//csv data를 읽어 컬렉션에 저장
				int idx = 0;
				List<String[]> readList = mc.readerCSV(openFilePath);
				for (String[] data : readList) {
					ArrayList<String> tmpList = new ArrayList<String>(Arrays.asList(data));
					tmpList.add(0, idx == 0 ? "NO" : idx+"");
					dataList.add(tmpList.toArray(new String[tmpList.size()]));
					idx++;
				}
			
				// 하나의 데이터를 row라는 리스트에 집어넣은뒤 하나의 row는 행이 되며, 그 행들을 data에 집어넣는다.
				for (int i = 1; i < dataList.size(); i++) {
					defRow = FXCollections.observableArrayList();
					for (int j = 0; j < dataList.get(i).length; j++) {
						defRow.add(dataList.get(i)[j]);
						int nowLen = dataList.get(i)[j].getBytes().length;
						if ((j+1) > colChrLen.size()) colChrLen.add(nowLen);
						else if (nowLen > colChrLen.get(j)) colChrLen.set(j, nowLen);
					}
					defData.add(defRow);
				}
				
				// 건수
				if (dataList.size() > 1) {
					lblRowCntVal.setText(df.format(dataList.size()-1)+"");
					lblColCntVal.setText(df.format(defRow.size())+"");
				}

				// coldiv 테이블에 몇개의 컬럼까지 보여줄지
				int coldiv = dataList.get(0).length >= 8 ? 8 : dataList.get(0).length; 
				for (int i = 0; i < dataList.get(0).length; i++) {
					final int j = i;
					
					//컬럼 i번째마다 dataList에서 컬럼명을 지정
					TableColumn col = new TableColumn(dataList.get(0)[i]);
					
					//테이블에 각 컬럼에 데이터 넣어서 보여주기
					col.setCellValueFactory(
							new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
								public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
									return new SimpleStringProperty(param.getValue().get(j).toString());
								}
							});
					
					//컬럼사이즈 조절
					int nowLen = dataList.get(0)[i].getBytes().length;
					if ((i+1) > colChrLen.size()) colChrLen.add(nowLen);
					else if (nowLen > colChrLen.get(i)) colChrLen.set(i, nowLen);
					
					if (colChrLen.size()+1 > i) col.setPrefWidth(Double.valueOf(getLenWidth(colChrLen.get(i))+""));
					else col.prefWidthProperty().bind(defTblView.widthProperty().divide(coldiv));
					if (i == 0) col.setMinWidth(40);
					
					//테이블에 컬럼을 넣기
					defTblView.getColumns().addAll(col);
				}

				//테이블에 data를 넣기
				defTblView.setItems(defData);

				System.out.println("!!!!!!!!!!OpenFile-ReaderEndDate:"+new SimpleDateFormat("HH:mm:ss").format(new Date()));

				updateProgress(1, 1);

				return true;
			}
    	};
    }
    
	//Create a New Task2_2
    private Task taskCreator2_2() {
    	return new Task() {
			@Override
			protected Object call() throws Exception {
				System.out.println("!!!!!!!!!!RowStart-ReaderStartDate:"+new SimpleDateFormat("HH:mm:ss").format(new Date()));

				ObservableList<ObservableList> comb = FXCollections.observableArrayList();
				comb.add(FXCollections.observableArrayList("A", "B", "C"));

				int idx = 0;
				for (String[] data : mc.readerCSV(openFilePath)) {
					ArrayList<String> tmpList = new ArrayList<String>(Arrays.asList(data));
					tmpList.add(0, idx == 0 ? "NO" : idx+"");
					dataList.add(tmpList.toArray(new String[tmpList.size()]));
					idx++;
				}
				
				for (int i = comboNo + 1; i < dataList.size(); i++) {
					defRow = FXCollections.observableArrayList();
					for (String item : dataList.get(i)) defRow.add(item);
					defData.add(defRow);
				}
				
				vboxOne.getChildren().remove(defTblView);
				vboxOne.getChildren().remove(testbox2);
				vboxOne.getChildren().remove(vboxPage);
				vboxPage = createPage();
				vboxOne.getChildren().addAll(vboxPage, testbox2);

				System.out.println("!!!!!!!!!!RowStart-ReaderEndDate:"+new SimpleDateFormat("HH:mm:ss").format(new Date()));

				updateProgress(1, 1);

				return true;
			}
    	};
    }

}
