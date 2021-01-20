package application;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Test {

	public static void main(String[] args) {
		try {
			System.out.println("Percent_(1%3):"+(1%4));
			System.out.println("Percent_(2%3):"+(2%4));
			System.out.println("Percent_(3%3):"+(3%4));
			System.out.println("Percent_(4%3):"+(4%4));
			System.out.println("Percent_(5%3):"+(5%4));
			System.out.println("Percent_(6%3):"+(6%4));
			System.out.println("Percent_(7%3):"+(7%4));
			System.out.println("Percent_(8%3):"+(8%4));
			System.out.println("Percent_(9%3):"+(9%4));
			
			
			String datestr = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(Calendar.getInstance().getTime());
			System.out.println(datestr);
	
			Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse("2020-10-20");
			Date date2 = new SimpleDateFormat("yyyy-MM-dd").parse("2020-11-10");
			long diff = TimeUnit.DAYS.convert(Math.abs(date2.getTime() - date1.getTime()), TimeUnit.MILLISECONDS);
			//System.out.println("###:::"+diff);
			
			Calendar cal = Calendar.getInstance();
	        cal.setTime(date1);
	        cal.add(Calendar.DATE, 10);
	        date1 = cal.getTime();
			diff = TimeUnit.DAYS.convert(Math.abs(date2.getTime() - date1.getTime()), TimeUnit.MILLISECONDS);

			Map<String, int[]> imap = new HashMap<String, int[]>();
			int[] a1 = {1,5,4};  imap.put("1", a1);
			int[] a2 = {2,2,2};  imap.put("2", a2);
			int[] a3 = {3,1,2};  imap.put("4", a3);
			int[] res = {0, 0, 0};
			imap.forEach((k, v) -> res[0] += v[0] );
			imap.forEach((k, v) -> res[1] += v[1] );
			imap.forEach((k, v) -> res[2] += v[2] );

			//System.out.println("###:::res[0]:"+res[0]+", res[1]:"+res[1]+", imap[2]:"+res[2]);
			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
