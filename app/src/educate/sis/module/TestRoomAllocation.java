package educate.sis.module;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import educate.db.DbPersistence;
import educate.hostel.entity.Room;
import educate.hostel.entity.RoomAllocation;
import lebah.util.DateUtil;

public class TestRoomAllocation {
	
	public static void main(String[] args) throws Exception {
		
		DbPersistence db = new DbPersistence();
		
		int year = 2010;
		int month = Calendar.JUNE;
		
		Calendar cal = new GregorianCalendar(year, month, 1); 
		int days = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		int startDay = cal.get(Calendar.DAY_OF_WEEK);
		
		String sql = "";


		DateUtil du = new DateUtil();
		
		Map<Room, Date> roomMap = new HashMap<Room, Date>();
		
		List<Room> rooms = db.list("select r from educate.hostel.entity.Room r");
		Hashtable h = new Hashtable();
		List<OccupiedRoomSlot> slots = new ArrayList<OccupiedRoomSlot>();
		
		for ( Room r : rooms ) {
			OccupiedRoomSlot occupiedSlot = new OccupiedRoomSlot(r);
			for ( int i=1 ; i < days+1; i++ ) {
				Date date = new GregorianCalendar(year, month, i).getTime();
				h.put("date", date);
				sql = "select ra from RoomAllocation ra where ra.room.id = '" + r.getId() + 
				"' and (ra.checkInDate <= :date and ra.checkOutDate is null)";
				List<RoomAllocation> ras = db.list(sql, h);
				RoomAllocation ra = ras.size() > 0 ? (RoomAllocation) ras.get(0) : null;	
				if ( ra != null ) {
					occupiedSlot.addDay(date);
					occupiedSlot.addOccupier(new Occupier(ra.getStudent(), date));
				}
			}
			
			for ( int i=1 ; i < days+1; i++ ) {
				Date date = new GregorianCalendar(year, month, i).getTime();
				h.put("date", date);
				sql = "select ra from RoomAllocation ra where ra.room.id = '" + r.getId() + 
				"' and (ra.checkInDate <= :date and ra.checkOutDate >= :date)";
				List<RoomAllocation> ras = db.list(sql, h);
				RoomAllocation ra = ras.size() > 0 ? (RoomAllocation) ras.get(0) : null;	
				if ( ra != null ) {
					
					occupiedSlot.addDay(date);
					occupiedSlot.addOccupier(new Occupier(ra.getStudent(), date));
				}
			}
			
			slots.add(occupiedSlot);
		}
		
		for ( OccupiedRoomSlot slot : slots ) {
			System.out.println(slot.getRoom().getName());
//			List<Occupier> occupiers = slot.getOccupiers();
//			if ( occupiers != null ) {
//				for ( Occupier o : occupiers ) {
//					System.out.println(du.toString(o.getDate(), "dd-MM-yyy") + " - " + o.getStudent().getBiodata().getName());
//				}
//			}
			
			for ( int i=1; i < days+1; i++ ) {
				Occupier o = slot.getOccupant(year, month, i);
				if ( o != null ) {
					System.out.println(o.getStudent().getBiodata().getName());
				}
			}
			
		}
	}

}
