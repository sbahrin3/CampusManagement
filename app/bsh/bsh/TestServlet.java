/**
 * 
 */
package bsh;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import educate.sis.finance.entity.Payment;
import lebah.servlets.IServlet;

/**
 * @author Shamsul Bahrin bin Abd Mutalib
 *
 */
public class TestServlet implements IServlet {

	public void doService(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		
		List<Payment> payments = new ArrayList<Payment>();
		String date1 = "02-02-2016";
		String date2 = "03-02-2016";
		Hashtable h = new Hashtable();
		String sql = "select i from Payment i where i.createDate between :date1 and :date2 ";
		
	
		Interpreter i = new Interpreter();
		FileReader reader = new FileReader(Source.dir() + "bsh/myservlet.bsh");
		try {
			i.set("payments", payments);
			i.eval( reader );
		} catch (EvalError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
