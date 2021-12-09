package educate.hostel.module;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import educate.db.DbPersistence;
import educate.hostel.entity.RoomAllocation;
import educate.poi.Excel1;
import lebah.servlets.IServlet;

public class StudentReportXL implements IServlet {
	private DbPersistence db = new DbPersistence();
	
	@Override
	public void doService(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String filename = "hostel_report3";
		response.setContentType("application/x-msdownload");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + ".xls\"");
	
		List<String> headers = new ArrayList<String>();
//		headers.add("Room");
//		headers.add("Name");
//		headers.add("Matric");
//		headers.add("Gender");
//		headers.add("Check In");
//		headers.add("Check Out");

	
		List<RoomAllocation> allocs = (List<RoomAllocation>) request.getSession().getAttribute("allocs");
		List<List> rows =  new ArrayList<List>();
		String s = "";
		String floor = "";
		String unit = "";
		String room = "";
		
		List cols = new ArrayList();

		cols.add("Name");
		cols.add("Matric");
		cols.add("Gender");
		cols.add("Date In");
		cols.add("Date Out");	
		cols.add("Floor");
		cols.add("Unit");
		cols.add("Room");
		rows.add(cols);
		
		for ( RoomAllocation alloc : allocs ) {

			cols = new ArrayList();

			cols.add(alloc.getStudent().getBiodata().getName());
			cols.add(alloc.getStudent().getMatricNo());
			cols.add(alloc.getStudent().getBiodata().getGender() != null ? alloc.getStudent().getBiodata().getGender().getCode() : "");
			cols.add(alloc.getCheckInDate());
			cols.add(alloc.getExpectedDateOut());
			cols.add(alloc.getRoom().getUnit().getFloor().getCode());
			cols.add(alloc.getRoom().getUnit().getCode());
			cols.add(alloc.getRoom().getCode());			
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
