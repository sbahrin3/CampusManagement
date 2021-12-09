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

public class CreateSampleInvoiceXLS implements IServlet {
	private DbPersistence db = new DbPersistence();
	
	@Override
	public void doService(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String filename = "result_example";
		response.setContentType("application/x-msdownload");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + ".xls\"");
	
		List<String> headers = new ArrayList<String>();
		headers.add("Matric No");
		headers.add("Session Code");
		headers.add("Invoice No");
		headers.add("Invoice Date");
		headers.add("Fee Code");
		headers.add("Description");
		headers.add("Amount");

		
		List<List> rows =  new ArrayList<List>();
		List cols = new ArrayList();
		cols.add("DBA/0006/0114/33");
		cols.add("JAN 14");
		cols.add("INV 01234");
		cols.add("11/02/2014");
		cols.add("TU");
		cols.add("TUITION FEE");
		cols.add(1300);
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
