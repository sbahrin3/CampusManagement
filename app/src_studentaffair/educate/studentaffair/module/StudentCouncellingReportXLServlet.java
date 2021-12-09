package educate.studentaffair.module;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import educate.db.DbPersistence;
import educate.poi.Excel1;
import educate.studentaffair.entity.StudentCouncelling;
import lebah.servlets.IServlet;

public class StudentCouncellingReportXLServlet implements IServlet {
	private DbPersistence db = new DbPersistence();
	
	@Override
	public void doService(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String filename = "councelling_report";
		response.setContentType("application/x-msdownload");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + ".xls\"");
	
	
		List<String> headers = new ArrayList<String>();
		headers.add("Name");
		headers.add("Matric No");
		headers.add("Date");
		headers.add("Issue");
	

		List<StudentCouncelling> records = (List<StudentCouncelling>) request.getSession().getAttribute("records");
		
		List<List> rows =  new ArrayList<List>();
		for ( StudentCouncelling r : records ) {
			List cols = new ArrayList();
			cols.add(r.getStudent().getBiodata().getName());
			cols.add(r.getStudent().getMatricNo());
			cols.add(new SimpleDateFormat("dd-MM-yyyy").format(r.getReferredDate()));
			cols.add(r.getIssue().getName());
			rows.add(cols);
		}
		
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




