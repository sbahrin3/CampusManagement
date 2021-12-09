package howto;

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

public class DownloadXLServlet implements IServlet {
	private DbPersistence db = new DbPersistence();
	
	@Override
	public void doService(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		response.setContentType("application/x-msdownload");
		response.setHeader("Content-Disposition", "attachment; filename=\"students_list.xls\"");
	
		List<User> records = (List<User>) request.getSession().getAttribute("records");
		
		List<String> headers = new ArrayList<String>();
		headers.add("Matric");
		headers.add("IC/Passport");
		headers.add("User Id");
		headers.add("Name");
		headers.add("Program");

		List<List> rows =  new ArrayList<List>();
		for ( User i : records ) {
			List cols = new ArrayList();
			cols.add(i.getMatricNo());
			cols.add(i.getIcno());
			cols.add(i.getUserId());
			cols.add(i.getName());
			cols.add(i.getProgram());
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
	
	
	class User {
		
		String matricNo;
		String icno;
		String userId;
		String name;
		String program;
		public String getMatricNo() {
			return matricNo;
		}
		public void setMatricNo(String matricNo) {
			this.matricNo = matricNo;
		}
		public String getIcno() {
			return icno;
		}
		public void setIcno(String icno) {
			this.icno = icno;
		}
		public String getUserId() {
			return userId;
		}
		public void setUserId(String userId) {
			this.userId = userId;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getProgram() {
			return program;
		}
		public void setProgram(String program) {
			this.program = program;
		}
		
		
		
	}


}
