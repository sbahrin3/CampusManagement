package educate.sis.module;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import educate.db.DbPersistence;
import educate.enrollment.entity.StatusType;
import educate.enrollment.entity.StudentStatus;
import educate.poi.Excel1;
import lebah.servlets.IServlet;

public class StudentStatusReportXLServlet implements IServlet {
	private DbPersistence db = new DbPersistence();
	
	@Override
	public void doService(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		
		String statusId = request.getParameter("statusId");
		StatusType statusType = db.find(StatusType.class, statusId);

		String filename = "student_" + statusType.getName();
		response.setContentType("application/x-msdownload");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + ".xls\"");
		
		List<String> headers = new ArrayList<String>();
		headers.add("Matric");
		headers.add("Name");
		headers.add("Semester");
		headers.add("Program");
		if ( "quit".equals(statusId)) headers.add("Withdraw");
		headers.add("Remark");
		headers.add("Date");
		
		List<StudentStatus> students = (List<StudentStatus>) request.getSession().getAttribute("students");
		
	
		List<List> rows =  new ArrayList<List>();
		for ( StudentStatus i : students ) {
			List cols = new ArrayList();
			cols.add(i.getStudent().getMatricNo());
			cols.add(i.getStudent().getBiodata().getName());
			cols.add(i.getPeriod().getName());
			cols.add(i.getStudent().getProgram().getName());
			if ( "quit".equals(statusId)) cols.add( i.getWithdrawType() != null ? i.getWithdrawType().getName() : "");
			cols.add(i.getRemark());
			cols.add(i.getStatusDate() != null ? new SimpleDateFormat("dd-MM-yyyy").format(i.getStatusDate()) : "");
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
