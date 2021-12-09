package educate.sis.module;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import educate.db.DbPersistence;
import educate.poi.Excel1;
import educate.sis.struct.entity.Graduate;
import lebah.servlets.IServlet;

public class StudentGraduationListReportXL implements IServlet {
	private DbPersistence db = new DbPersistence();
	
	@Override
	public void doService(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String filename = "graduates_list";
		response.setContentType("application/x-msdownload");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + ".xls\"");
		
		
		List<Graduate> graduates = (List<Graduate>) request.getSession().getAttribute("graduates");
		long attendRehearsal = (Long) request.getSession().getAttribute("attendRehearsal");
		long notAttendRehearsal = (Long) request.getSession().getAttribute("notAttendRehearsal");
		long attendCeremony = (Long) request.getSession().getAttribute("attendCeremony");
		long notAttendCeremony = (Long) request.getSession().getAttribute("notAttendCeremony");
		int total = (Integer) request.getSession().getAttribute("total");
	
		List<String> headers = new ArrayList<String>();
		headers.add("Matric");
		headers.add("Name");
		headers.add("IC No");
		headers.add("Rehearsal");
		headers.add("Ceremony");
		
		List<List> rows =  new ArrayList<List>();
		List cols = new ArrayList();
		for ( Graduate i : graduates ) {
			cols = new ArrayList();
			cols.add(i.getStudent().getMatricNo());
			cols.add(i.getStudent().getBiodata().getName());
			cols.add(i.getStudent().getBiodata().getName());
			cols.add(i.getAttendRehearsal() ? "Y" : "");
			cols.add(i.getAttendCeremony() ? "Y" : "");
			rows.add(cols);
		}
		
		cols = new ArrayList();
		cols.add("");
		cols.add("");
		cols.add("");
		cols.add("");
		cols.add("");
		rows.add(cols);
		
		cols = new ArrayList();
		cols.add("");
		cols.add("");
		cols.add("Attend");
		cols.add((int) attendRehearsal);
		cols.add((int) attendCeremony);
		rows.add(cols);
		
		cols = new ArrayList();
		cols.add("");
		cols.add("");
		cols.add("Not Attend");
		cols.add((int) notAttendRehearsal);
		cols.add((int) notAttendCeremony);
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
