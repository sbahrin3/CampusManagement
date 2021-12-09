/**
 * 
 */
package educate.sis.servlets;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bsh.EvalError;
import bsh.Interpreter;
import bsh.Source;
import educate.poi.Excel1;
import educate.sis.finance.entity.Payment;
import lebah.servlets.IServlet;

/**
 * @author Shamsul Bahrin bin Abd Mutalib
 *
 */
public class PaymentReportXLServlet2  implements IServlet {

	public void doService(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
	
			String filename = "payment_report";
			response.setContentType("application/x-msdownload");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + ".xls\"");
			
			ServletOutputStream os = response.getOutputStream();
			
			List<Payment> payments = (List<Payment>) request.getSession().getAttribute("payment_report");
			
			
			try {
				
				List<String> headers = new ArrayList<String>();
				List<List> rows =  new ArrayList<List>();
				
				Interpreter ir = new Interpreter();
				FileReader reader = new FileReader(Source.dir() + "bsh/paymentList.bsh");
			
				ir.set("payments", payments);
				ir.set("headers", headers);
				ir.set("rows", rows);
				ir.eval(reader);
				
				headers = (List<String>) ir.get("headers");
				rows = (List<List>) ir.get("rows");
				Excel1 x = new Excel1();

				x.createXLS(os, headers, rows);
				
			} catch (EvalError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			os.flush();
			os.close();
		
	}


}
