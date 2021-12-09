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

public class CreateSampleStudentXLS implements IServlet {
	private DbPersistence db = new DbPersistence();
	
	@Override
	public void doService(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String filename = "result_example";
		response.setContentType("application/x-msdownload");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + ".xls\"");
	
		List<String> headers = new ArrayList<String>();
		headers.add("Session Code");
		headers.add("Program Code");
		headers.add("Matric No");
		headers.add("Name");
		headers.add("ICNo");
		headers.add("Gender");
		headers.add("Race Code");
		headers.add("Birth Date (dd.mm.yyyy)");
		headers.add("Phone");
		headers.add("Address");
		headers.add("State Code");
		headers.add("Nationality Code");

		List<List> rows =  new ArrayList<List>();
		List cols = new ArrayList();
		cols.add("");
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
