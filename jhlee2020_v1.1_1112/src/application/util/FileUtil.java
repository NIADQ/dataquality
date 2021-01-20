package application.util;

import java.io.File;
import java.io.FileInputStream;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;

public class FileUtil {
	
	public String getFileCharset(String p) {
		String charset = "ISO-8859-1"; //Default chartset, put whatever you want
		FileInputStream fin = null;
		try {
			File file = new File(p);

			byte[] fileContent = null;
			fin = new FileInputStream(file.getPath());
			fileContent = new byte[(int) file.length()];
			fin.read(fileContent);

			byte[] data =  fileContent;

			CharsetDetector detector = new CharsetDetector();
			detector.setText(data);

			CharsetMatch cm = detector.detect();

			if (cm != null) {
			    int confidence = cm.getConfidence();
			    System.out.println("Encoding: " + cm.getName() + " - Confidence: " + confidence + "%");
			    //Here you have the encode name and the confidence
			    //In my case if the confidence is > 50 I return the encode, else I return the default value
			    if (confidence > 50) {
			        charset = cm.getName();
			    }
			}		
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fin != null) { fin.close(); fin = null; }
			} catch (Exception se) {
				se.printStackTrace();
			}
		}
		System.out.println("File-Charset:"+charset);
		return charset;
	}

}
