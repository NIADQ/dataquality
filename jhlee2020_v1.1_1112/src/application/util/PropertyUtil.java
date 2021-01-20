package application.util;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Properties;

import application.base.Const;

public class PropertyUtil {

	public Properties getProperty() {
		FileInputStream fis = null;
		InputStreamReader isr = null;
		Properties prop = null;
		try {
			fis = new FileInputStream(Const.PROPS);
            if (fis != null) {
	            isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
	            prop = new Properties();
	            prop.load(isr);
	            System.out.println("[DEBUG]Property [" + Const.PROPS + "] File Load");
	            prop.forEach((key, value) -> System.out.println("[DEBUG]{\"" + key + "\":\"" + value + "\"}"));
	            //System.out.println("[DEBUG]TestPropItem:"+prop.getProperty("main.lblFile"));
            } else {
                System.out.println("[ERROR]Property [" + Const.PROPS + "] File is Not Find");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
        	try {
        		if (isr != null) { isr.close(); isr = null; }
        		if (fis != null) { fis.close(); fis = null; }
        	} catch (Exception fe) { 
        		fe.printStackTrace();
        	}
        }
		return prop;
	}
}
