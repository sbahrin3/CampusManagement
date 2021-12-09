/**
 * 
 */
package bsh;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import educate.poi.Excel1;
import lebah.servlets.IServlet;

/**
 * @author Shamsul Bahrin bin Abd Mutalib
 *
 */
public class XLServlet implements IServlet {

	public void doService(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
	
		String bshfile = request.getParameter("bsh"); //"paymentList.bsh";
		String filename = request.getParameter("filename");
		if ( filename == null ) filename = "rpt" + lebah.db.UniqueID.get();
		response.setContentType("application/x-msdownload");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + ".xls\"");
		
		ServletOutputStream os = response.getOutputStream();

		try {
			Interpreter ir = new Interpreter();
			FileReader reader = new FileReader(Source.dir() + "bsh/" + bshfile + ".bsh");
		
			ir.set("request", request);
			ir.eval(reader);
			
			List<String> headers = (List<String>) ir.get("headers");
			List<List> rows = (List<List>) ir.get("rows");
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
