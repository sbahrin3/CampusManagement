/**
 * 
 */
package bsh;

import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.util.List;

import educate.db.DbPersistence;
import educate.sis.finance.entity.Payment;

/**
 * @author Shamsul Bahrin bin Abd Mutalib
 *
 */
public class TestBsh {
	
	public static void main(String[] args) throws Exception {
		
		DbPersistence db = new DbPersistence();
		
		String date1 = "02-12-2015";
		String date2 = "02-12-2015";
		Hashtable h = new Hashtable();
		h.put("date1", new SimpleDateFormat("dd-MM-yyyy").parseObject(date1));
		h.put("date2", new SimpleDateFormat("dd-MM-yyyy").parseObject(date2));
		String sql = "select i from Payment i where i.createDate between :date1 and :date2 ";
		List<Payment> payments = db.list(sql,h);
		String testValue = "...";
	
		Interpreter i = new Interpreter();
		FileReader reader = new FileReader(Source.dir() + "bsh/myservlet.bsh");
		try {
			i.set("payments", payments);
			i.set("testValue", testValue);
			i.eval( reader );
		} catch (EvalError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		testValue = (String) i.get("testValue");
		System.out.println("testValue = " + testValue);
		
	}

}
