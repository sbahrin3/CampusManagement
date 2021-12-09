package educate.hostel.module;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import educate.db.DbPersistence;
import educate.hostel.entity.Hostel;
import educate.hostel.entity.Room;
import educate.hostel.entity.RoomAllocation;
import educate.sis.general.entity.Gender;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class StudentReportModule extends LebahModule {
	
	String path = "apps/hostel_report_students";
	DbPersistence db = new DbPersistence();
	
	public void preProcess() {
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy"));
	}
	

	@Override
	public String start() {
		context.put("hostels", db.list("select h from educate.hostel.entity.Hostel h order by h.sequence"));
		return path + "/start.vm";
	}
	
	@Command("getReport3")
	public String getReport3() throws Exception {
		String hostelId = getParam("hostelId");
		Hostel hostel = db.find(Hostel.class, hostelId);
		context.put("hostel", hostel);
		request.getSession().setAttribute("hostel", hostel);
		
		String genderId = getParam("genderId");
		Gender gender = db.find(Gender.class, genderId);
		String date1 = getParam("date1");
		if ( !"".equals(date1)) {
			Date dateFrom = null;
			try { dateFrom = new SimpleDateFormat("dd-MM-yyyy").parse(date1); } catch ( Exception e ) {}
			String sql = "";
			Hashtable h = new Hashtable();
			h.put("date1", dateFrom);
			
			sql = "select r from RoomAllocation r where :date1 between r.checkInDate and r.expectedDateOut ";
			if ( !"".equals(hostelId)) {
				sql += " and r.room.unit.floor.block.building.hostel.id = '" + hostelId + "' ";
			}
			if ( !"".equals(genderId)) {
				sql += " and r.student.biodata.gender.id = '" + gender.getId() + "' ";
			}
			sql += " order by r.room.unit.floor.block.building.sequence, r.room.unit.floor.block.sequence, r.room.unit.floor.sequence, r.room.unit.sequence, r.room.sequence";
			Map<String, List<RoomAllocation>> roomAlloc = new HashMap<String, List<RoomAllocation>>();
			context.put("roomAlloc", roomAlloc);
			request.getSession().setAttribute("roomAlloc", roomAlloc);
			
			List<RoomAllocation> allocs = db.list(sql, h);
			String roomId = "";
			List<RoomAllocation> list = null;
			for ( RoomAllocation a : allocs ) {
				if ( roomId.equals(a.getRoom().getId())) {
					list.add(a);
				} else {
					if ( !"".equals(roomId)) {
						roomAlloc.put(roomId, list);
					}
					roomId = a.getRoom().getId();
					list = new ArrayList<RoomAllocation>();
					list.add(a);
				}
			}
			if ( !"".equals(roomId)) {
				roomAlloc.put(roomId, list);
			}
			
			sql = "select r from educate.hostel.entity.Room r ";
			if ( !"".equals(hostelId)) {
				sql += " where r.unit.floor.block.building.hostel.id = '" + hostelId + "' ";
			}
			sql += " order by r.unit.floor.block.building.sequence, r.unit.floor.block.sequence, r.unit.floor.sequence, r.unit.sequence, r.sequence";
			List<Room> rooms = db.list(sql);
			context.put("rooms", rooms);
			request.getSession().setAttribute("rooms", rooms);
			
		}
		return path + "/getReport3.vm";
	}
	
	
	@Command("getReport2")
	public String getReport2() throws Exception {
		String hostelId = getParam("hostelId");
		Hostel hostel = db.find(Hostel.class, hostelId);
		context.put("hostel", hostel);
		request.getSession().setAttribute("hostel", hostel);
		
		String genderId = getParam("genderId");
		Gender gender = db.find(Gender.class, genderId);
		String date1 = getParam("date1");
		if ( !"".equals(date1)) {
			Date dateFrom = null;
			try { dateFrom = new SimpleDateFormat("dd-MM-yyyy").parse(date1); } catch ( Exception e ) {}
			String sql = "";
			Hashtable h = new Hashtable();
			h.put("date1", dateFrom);
			
			//sql = "select r from RoomAllocation r where (:date1 between r.checkInDate and r.expectedDateOut) or  (:date1 between r.checkInDate and r.checkOutDate)";
			sql = "select r from RoomAllocation r where :date1 between r.checkInDate and r.expectedDateOut ";
			if ( !"".equals(hostelId)) {
				sql += " and r.room.unit.floor.block.building.hostel.id = '" + hostelId + "' ";
			}
			if ( !"".equals(genderId)) {
				sql += " and r.student.biodata.gender.id = '" + gender.getId() + "' ";
			}
			sql += " order by r.room.unit.floor.block.building.sequence, r.room.unit.floor.block.sequence, r.room.unit.floor.sequence, r.room.unit.sequence, r.room.sequence";
			List<RoomAllocation> allocs = db.list(sql, h);
			context.put("allocs", allocs);
			request.getSession().setAttribute("allocs", allocs);
		}
		return path + "/getReport2.vm";
	}
	
	@Command("getReport")
	public String getReport() throws Exception {
		
		
		String hostelId = getParam("hostelId");
		Hostel hostel = db.find(Hostel.class, hostelId);
		context.put("hostel", hostel);
		request.getSession().setAttribute("hostel", hostel);
		
		String genderId = getParam("genderId");
		Gender gender = db.find(Gender.class, genderId);
		String date1 = getParam("date1");
		if ( !"".equals(date1)) {
			Date dateFrom = null;
			try { dateFrom = new SimpleDateFormat("dd-MM-yyyy").parse(date1); } catch ( Exception e ) {}
			String sql = "";
			Hashtable h = new Hashtable();
			h.put("date1", dateFrom);
			
			//sql = "select r from RoomAllocation r where (:date1 between r.checkInDate and r.expectedDateOut) or  (:date1 between r.checkInDate and r.checkOutDate)";
			sql = "select r from RoomAllocation r where :date1 between r.checkInDate and r.expectedDateOut ";
			if ( !"".equals(hostelId)) {
				sql += " and r.room.unit.floor.block.building.hostel.id = '" + hostelId + "' ";
			}
			if ( !"".equals(genderId)) {
				sql += " and r.student.biodata.gender.id = '" + gender.getId() + "' ";
			}
			sql += " order by r.student.biodata.name";
			List<RoomAllocation> allocs = db.list(sql, h);
			context.put("allocs", allocs);
			request.getSession().setAttribute("allocs", allocs);
		}
		return path + "/getReport.vm";
	}
	
	public static void main(String[] args) throws Exception {
		
		DbPersistence db = new DbPersistence();
		Hashtable h = new Hashtable();
		h.put("date1", new Date());
		
		String sql = "select r from RoomAllocation r where :date1 between r.checkInDate and r.expectedDateOut  and r.room.unit.floor.block.building.hostel.id = '1303202972035'  order by r.room.unit.floor.block.building.sequence, r.room.unit.floor.block.sequence, r.room.unit.floor.sequence, r.room.unit.sequence, r.room.sequence";

		Map<String, List<RoomAllocation>> roomAlloc = new HashMap<String, List<RoomAllocation>>();
		List<RoomAllocation> allocs = db.list(sql, h);
		String roomId = "";
		List<RoomAllocation> list = null;
		for ( RoomAllocation a : allocs ) {
			System.out.println(a.getRoom().getName() + ", " + a.getRoom().getCapacity());
			if ( roomId.equals(a.getRoom().getId())) {
				list.add(a);
			} else {
				if ( !"".equals(roomId)) {
					roomAlloc.put(roomId, list);
				}
				roomId = a.getRoom().getId();
				list = new ArrayList<RoomAllocation>();
				list.add(a);
			}
		}
		if ( !"".equals(roomId)) {
			roomAlloc.put(roomId, list);
		}
		
		
		
		
	}

}
