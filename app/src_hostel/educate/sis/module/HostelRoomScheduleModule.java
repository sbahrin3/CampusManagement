package educate.sis.module;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.http.HttpSession;

import educate.db.DbPersistence;
import educate.hostel.entity.Block;
import educate.hostel.entity.Building;
import educate.hostel.entity.Floor;
import educate.hostel.entity.Hostel;
import educate.hostel.entity.Room;
import educate.hostel.entity.RoomAllocation;
import lebah.portal.action.AjaxModule;
import lebah.util.DateUtil;

public class HostelRoomScheduleModule  extends AjaxModule {
	
	String path = "apps/hostel_room_schedule/";
	private String vm = path + "default.vm";
	HttpSession session;
	DbPersistence db = new DbPersistence();

	
	@Override
	public String doAction() throws Exception {
		context.put("_formName", formName);
		session = request.getSession();
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy"));
		context.put("timeFormat", new SimpleDateFormat("hh:mm a"));	
		DateUtil du = new DateUtil();
		context.put("du", du);
		String command = request.getParameter("command");
		System.out.println(command);
		if ( null == command || "".equals(command)) start();
		else if ( "list_buildings".equals(command)) listBuildings();
		else if ( "list_blocks".equals(command)) listBlocks();
		else if ( "list_floors".equals(command)) listFloors();
		else if ( "room_history".equals(command)) roomHistory();
		else if ( "room_info".equals(command)) roomInfo();
		return vm;
	}


	private void roomInfo() {
		vm = path + "room_info.vm";
		String roomId = request.getParameter("room_id");
		String _date = request.getParameter("date");
		Room r = db.find(Room.class, roomId);
		context.put("room", r);
		List<RoomAllocation> allocations = new ArrayList<RoomAllocation>();
		Hashtable h = new Hashtable();
		DateUtil du = new DateUtil();
		Date date = du.toDate(_date);
		h.put("date", date);
		//where checkout date is null
		String sql = "select ra from RoomAllocation ra where ra.room.id = '" + r.getId() + 
		"' and (ra.checkInDate <= :date and ra.checkOutDate is null)";
		allocations.addAll(db.list(sql, h));
		sql = "select ra from RoomAllocation ra where ra.room.id = '" + r.getId() + 
		"' and (ra.checkInDate <= :date and ra.checkOutDate >= :date)";
		allocations.addAll(db.list(sql, h));
		context.put("allocations", allocations);

	}


	private void listFloors() {
		vm = path + "select_floor.vm";
		String id = request.getParameter("select_block");
		Block o = db.find(Block.class, id);
		context.put("block", o);
		
	}


	private void listBlocks() {
		vm = path + "select_block.vm";
		String id = request.getParameter("select_building");
		Building o = db.find(Building.class, id);
		context.put("building", o);
		
	}


	private void listBuildings() {
		vm = path + "select_building.vm";
		String id = request.getParameter("select_hostel");
		Hostel o = db.find(Hostel.class, id);
		context.put("hostel", o);
		
	}


	private void start() {
		vm = path + "room_history.vm";
		String sql = "select h from Hostel h order by h.name";
		List<Hostel> hostels = db.list(sql);
		context.put("hostels", hostels);
		
	}


	private void roomHistory() {
		vm = path + "schedule.vm";
		
		String floorId = request.getParameter("select_floor");
		Floor floor = db.find(Floor.class, floorId);
		int month = 1, year = 2013;
		try {
			month = request.getParameter("select_month") != null ? Integer.parseInt(request.getParameter("select_month")) : 0;
			year = request.getParameter("select_year") != null ? Integer.parseInt(request.getParameter("select_year")) : 0;
		} catch ( Exception e ) { }
		
		for ( int i = 0; i < 6; i++ ) {
			createRoomHistory(floor, month + i, year, i+1);	
		}
		
	}


	private void createRoomHistory(Floor floor, int selectMonth, int selectYear, int cnt) {
		String dateIn = "01-" + ((selectMonth + 1) < 10 ? "0" +  Integer.toString(selectMonth + 1) : Integer.toString(selectMonth + 1)) + "-" + selectYear;
		Date checkInDate = new Date();
		if ( dateIn != null && !"".equals(dateIn)) {
			DateUtil du = new DateUtil();
			checkInDate = du.toDate(dateIn, "dd-MM-yyyy");
		}
		Calendar cal = new GregorianCalendar(); 
		cal.setTime(checkInDate);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int days = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		//month for display in the page
		int displayMonth = month + 1;
		String sql = "";
		List<Room> rooms = db.list("select r from educate.hostel.entity.Room r where r.unit.floor.id = '" + floor.getId() + "' order by r.unit.sequence, r.sequence");
		
		Hashtable h = new Hashtable();
		List<OccupiedRoomSlot> occupiedSlots = new ArrayList<OccupiedRoomSlot>();
		for ( Room r : rooms ) {
			OccupiedRoomSlot occupiedRoomSlot = new OccupiedRoomSlot(r);
			occupiedRoomSlot.setYear(year);
			occupiedRoomSlot.setMonth(month);
			//System.out.println("doing room " + r.getCode());
			for ( int i=1 ; i < days+1; i++ ) {
				Date date = new GregorianCalendar(year, month, i).getTime();
				h.put("date", date);
				//where checkout date is null
				sql = "select ra from RoomAllocation ra where ra.room.id = '" + r.getId() + 
				"' and (ra.checkInDate <= :date and ra.checkOutDate is null)";
				List<RoomAllocation> ras = db.list(sql, h);
				
				for ( RoomAllocation ral : ras ) {
					occupiedRoomSlot.addOccupier(new Occupier(ral.getStudent(), date, ral.getCheckInDate(), ral.getCheckInTime(), ral.getCheckOutDate(), ral.getCheckOutTime()));
				}
				//where checkoutdate is available
				sql = "select ra from RoomAllocation ra where ra.room.id = '" + r.getId() + 
				"' and (ra.checkInDate <= :date and ra.checkOutDate >= :date)";
				List<RoomAllocation> ras2 = db.list(sql, h);
				
				for ( RoomAllocation ral : ras2 ) {
					occupiedRoomSlot.addOccupier(new Occupier(ral.getStudent(), date, ral.getCheckInDate(), ral.getCheckInTime(), ral.getCheckOutDate(), ral.getCheckOutTime()));
				}

			}
			occupiedSlots.add(occupiedRoomSlot);
		}
		
		context.put("days_" + cnt, days);
		context.put("year_" + cnt, year);
		context.put("month_" + cnt, displayMonth > 9 ? "" + displayMonth : "0" + displayMonth);
		context.put("rooms_" + cnt, rooms);
		context.put("slots_" + cnt, occupiedSlots);

	}
	

}
