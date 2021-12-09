package migration.module;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import educate.db.DbPersistence;
import educate.poi.Excel1;
import lebah.servlets.IServlet;

public class CreateSamplePaymentXLS implements IServlet {
	private DbPersistence db = new DbPersistence();
	
	@Override
	public void doService(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String filename = "result_example";
		response.setContentType("application/x-msdownload");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + ".xls\"");
	
		List<String> headers = new ArrayList<String>();
		headers.add("Matric No");
		headers.add("Payment No");
		headers.add("Payment Date");
		headers.add("Description");
		headers.add("Payment Mode");
		headers.add("Check No");
		headers.add("Bank");
		headers.add("Amount");

		
		List<List> rows =  new ArrayList<List>();
		List cols = new ArrayList();
		cols.add("DBA/0006/0114/33");
		cols.add("RCT 01234");
		cols.add("11/03/2014");
		cols.add("Payment for Tuition Fee");
		cols.add("CHECK");
		cols.add("12345678");
		cols.add("CIMB");
		cols.add("500");
		rows.add(cols);
				
		ServletOutputStream os = response.getOutputStream();
		Excel1 x = new Excel1();
		try {
			x.createXLS(os, headers, rows);
		} catch (Exception e) {
			e.printStackTrace();
		}
		os.flush();
		os.close();
		
	}

}
