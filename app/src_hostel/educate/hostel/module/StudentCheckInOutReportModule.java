package educate.hostel.module;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import educate.db.DbPersistence;
import educate.hostel.entity.Hostel;
import educate.hostel.entity.RoomAllocation;
import educate.sis.general.entity.Gender;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class StudentCheckInOutReportModule  extends LebahModule {
	
	String path = "apps/hostel_report_students2";
	DbPersistence db = new DbPersistence();
	
	public void preProcess() {
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy"));
	}
	

	@Override
	public String start() {
		context.put("hostels", db.list("select h from educate.hostel.entity.Hostel h order by h.sequence"));
		return path + "/start.vm";
	}
	
	@Command("getReport")
	public String getReport() throws Exception {
		
		String hostelId = getParam("hostelId");
		Hostel hostel = db.find(Hostel.class, hostelId);
		context.put("hostel", hostel);
		String genderId = getParam("genderId");
		Gender gender = db.find(Gender.class, genderId);
		String date1 = getParam("date1");
		String date2 = getParam("date2");
		if ( !"".equals(date1)) {
			Date dateFrom = null;
			try { dateFrom = new SimpleDateFormat("dd-MM-yyyy").parse(date1); } catch ( Exception e ) {}
			Date dateTo = null;
			try { dateTo = new SimpleDateFormat("dd-MM-yyyy").parse(date2); } catch ( Exception e ) {}
			String sql = "";
			Hashtable h = new Hashtable();
			h.put("date1", dateFrom);
			h.put("date2", dateTo);
			
			sql = "select r from RoomAllocation r where r.checkInDate between :date1 and :date2 ";
			if ( !"".equals(hostelId)) {
				sql += " and r.room.unit.floor.block.building.hostel.id = '" + hostelId + "' ";
			}
			if ( !"".equals(genderId)) {
				sql += " and r.student.biodata.gender.id = '" + gender.getId() + "' ";
			}
			sql += " order by r.student.biodata.name";
			List<RoomAllocation> allocs = db.list(sql, h);
			context.put("allocs", allocs);
		}
		return path + "/getReport.vm";
	}
	
	@Command("getReport2")
	public String getReport2() throws Exception {
		
		String hostelId = getParam("hostelId");
		Hostel hostel = db.find(Hostel.class, hostelId);
		context.put("hostel", hostel);
		String genderId = getParam("genderId");
		Gender gender = db.find(Gender.class, genderId);
		String date1 = getParam("date1");
		String date2 = getParam("date2");
		if ( !"".equals(date1)) {
			Date dateFrom = null;
			try { dateFrom = new SimpleDateFormat("dd-MM-yyyy").parse(date1); } catch ( Exception e ) {}
			Date dateTo = null;
			try { dateTo = new SimpleDateFormat("dd-MM-yyyy").parse(date2); } catch ( Exception e ) {}
			String sql = "";
			Hashtable h = new Hashtable();
			h.put("date1", dateFrom);
			h.put("date2", dateTo);
			
			sql = "select r from RoomAllocation r where r.expectedDateOut between :date1 and :date2 ";
			if ( !"".equals(hostelId)) {
				sql += " and r.room.unit.floor.block.building.hostel.id = '" + hostelId + "' ";
			}
			if ( !"".equals(genderId)) {
				sql += " and r.student.biodata.gender.id = '" + gender.getId() + "' ";
			}
			sql += " order by r.student.biodata.name";
			List<RoomAllocation> allocs = db.list(sql, h);
			context.put("allocs", allocs);
		}
		return path + "/getReport.vm";
	}

	
}
