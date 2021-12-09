package educate.hostel.module;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import educate.db.DbPersistence;
import educate.hostel.entity.RoomAllocation;

public class StudentReport {
	
	public static void main(String[] args) throws Exception {
		
		DbPersistence db = new DbPersistence();
		Date date1 = new Date();
		Hashtable h = new Hashtable();
		h.put("date1", new SimpleDateFormat("dd-MM-yyyy").parse("10-04-2013"));
		h.put("date2", new SimpleDateFormat("dd-MM-yyyy").parse("27-04-2013"));
		String sql = "select r from RoomAllocation r where :date1 >= r.checkInDate and :date2 <= r.checkOutDate order by r.student.biodata.name";
		List<RoomAllocation> roomAllocs = db.list(sql, h);
		System.out.println(roomAllocs.size());
		
	}

}
