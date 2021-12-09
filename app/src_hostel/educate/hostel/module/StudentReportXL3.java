package educate.hostel.module;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import educate.db.DbPersistence;
import educate.hostel.entity.Room;
import educate.hostel.entity.RoomAllocation;
import educate.poi.Excel1;
import lebah.servlets.IServlet;

public class StudentReportXL3 implements IServlet {
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

		List<Room> rooms = (List<Room>) request.getSession().getAttribute("rooms");
		Map<String, List<RoomAllocation>> roomAlloc = (Map<String, List<RoomAllocation>>) request.getSession().getAttribute("roomAlloc");
		
		List<List> rows =  new ArrayList<List>();
		String s = "";
		String floor = "";
		String unit = "";
		String room = "";
		
		List cols = new ArrayList();
		for ( Room i : rooms ) {
			
			if ( !i.getUnit().getId().equals(unit)) {
				unit = i.getUnit().getId();
				
				cols = new ArrayList();
				cols.add("");
				rows.add(cols);
				
				cols = new ArrayList();
				cols.add("Building: " + i.getUnit().getFloor().getBlock().getBuilding().getCode() + ", Block: " + i.getUnit().getFloor().getBlock().getCode() + ", Floor: " + i.getUnit().getFloor().getCode());
				rows.add(cols);
				
				cols = new ArrayList();
				cols.add("Unit");
				cols.add("Room");
				cols.add("Name");
				cols.add("Matric");
				cols.add("Gender");
				cols.add("Date In");
				cols.add("Date Out");
				rows.add(cols);

				
			}
			
			List<RoomAllocation> list = roomAlloc.get(i.getId());
			if ( list != null ) {
				for ( RoomAllocation alloc : list ) {
					cols = new ArrayList();
					cols.add(i.getUnit().getName());
					cols.add(i.getCode());
					cols.add(alloc.getStudent().getBiodata().getName());
					cols.add(alloc.getStudent().getMatricNo());
					cols.add(alloc.getStudent().getBiodata().getGender() != null ? alloc.getStudent().getBiodata().getGender().getCode() : "");
					cols.add(alloc.getCheckInDate());
					cols.add(alloc.getExpectedDateOut());
					rows.add(cols);
				}
				int vacant = i.getCapacity() - list.size();		
				if ( vacant > 0 ) {
					for ( int v = 0; v < vacant; v++ ) {
						cols = new ArrayList();
						cols.add(i.getUnit().getName());
						cols.add(i.getCode());
						cols.add("");
						rows.add(cols);
					}
				}
			} else {
				int vacant = i.getCapacity();
				for ( int v = 0; v < vacant; v++ ) {
					cols = new ArrayList();
					cols.add(i.getUnit().getName());
					cols.add(i.getCode());
					cols.add("");
					rows.add(cols);
				}
			}
			

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
