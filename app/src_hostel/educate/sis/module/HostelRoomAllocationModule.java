package educate.sis.module;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.http.HttpSession;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.enrollment.entity.StudentStatus;
import educate.hostel.entity.Block;
import educate.hostel.entity.Building;
import educate.hostel.entity.CheckOutExtension;
import educate.hostel.entity.Floor;
import educate.hostel.entity.Hostel;
import educate.hostel.entity.Room;
import educate.hostel.entity.RoomAllocation;
import educate.hostel.entity.RoomTransferLog;
import educate.hostel.entity.Unit;
import lebah.portal.action.AjaxModule;
import lebah.util.DateUtil;

public class HostelRoomAllocationModule  extends AjaxModule {
	
	String path = "apps/hostel_room_allocation/";
	private String vm = path + "default.vm";
	HttpSession session;
	DbPersistence db = new DbPersistence();
	String userId = "";
	String studentId = "";
	boolean studentMode = false;

	
	public String doAction() throws Exception {
		
		userId = (String) request.getSession().getAttribute("_portal_login");
		
		if ( !"".equals(studentId)) {
			studentMode = true;
		}
		context.put("studentMode", studentMode);
		
		context.put("_formName", formName);
		session = request.getSession();
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy"));
		context.put("timeFormat", new SimpleDateFormat("hh:mm a"));	
		DateUtil du = new DateUtil();
		context.put("du", du);
		context.put("today", new Date());
		String command = request.getParameter("command");
		context.remove("check_in_success");
		
		System.out.println(command);
		if ( null == command || "".equals(command)) {
			start();
		}
		else if ( "list_students".equals(command)) listStudents();
		else if ( "filter_students".equals(command)) filterStudents();
		else if ( "student_info".equals(command)) studentInfo();
		else if ( "hostel_info".equals(command)) hostelInfo();
		
		else if ( "room_in".equals(command)) roomIn();
		else if ( "transfer_room".equals(command)) transferRoom();
		
		else if ( "check_vacancy".equals(command)) checkVacancy();
		else if ( "room_out".equals(command)) roomOut();
		else if ( "delete_out".equals(command)) deleteOut();
		else if ( "delete_room".equals(command)) deleteRoom();
		
		else if ( "list_hostels".equals(command)) listHostels();
		else if ( "list_buildings".equals(command)) listBuildings();
		else if ( "list_blocks".equals(command)) listBlocks();
		else if ( "list_floors".equals(command)) listFloors();
		else if ( "list_units".equals(command)) listUnits();
		else if ( "list_rooms".equals(command)) listRooms();
		
		else if ( "room_schedule".equals(command)) roomSchedule();
		else if ( "make_extension".equals(command)) makeExtension();
		
		else if ( "close".equals(command)) divClose();
		
		System.out.println(vm);
		return vm;
	}
	
	
	private void makeExtension() throws Exception {
		vm = path + "student_rooms_list.vm";
		
		String studentId = request.getParameter("student_id");
		Student student = (Student) db.find(Student.class, studentId);
		if ( student != null ) {
			String roomAllocationId = getParam("roomAllocationId");
			RoomAllocation roomAllocation = db.find(RoomAllocation.class, roomAllocationId);
			
			String extensionDate = getParam("ext_date_" + roomAllocationId);
			Date date = new SimpleDateFormat("dd-MM-yyyy").parse(extensionDate);
			String remark = getParam("extensionRemark_" + roomAllocationId);
			
			db.begin();
			CheckOutExtension ext = new CheckOutExtension();
			ext.setRoomAllocation(roomAllocation);
			ext.setCheckOutDate(date);
			ext.setExtensionRemark(remark);
			ext.setPreviousCheckOutDate(roomAllocation.getExpectedDateOut());			
			roomAllocation.getCheckOutExtensions().add(ext);
			roomAllocation.setExtension(true);
			roomAllocation.setExpectedDateOut(date);
			roomAllocation.setRealExpectedDateOut(date);
			roomAllocation.setExtensionRemark(remark);
			db.commit();
			
			listStudentRooms(student);
		}
	}


	private Date parseDate(String dateTxt) {
		if ( dateTxt != null && !"".equals(dateTxt)) {
			try {
				return new SimpleDateFormat("dd-MM-yyyy").parse(dateTxt);
			} catch (ParseException e) {
				return null;
			}
		}
		return null;
	}
	
	

	
	private void checkVacancy() {
		vm = path + "list_rooms_vacancy.vm";
		
		String checkInMode = getParam("checkInMode");
		context.put("checkInMode", checkInMode);
		
		String roomId = request.getParameter("room_id");
		System.out.println("room id = " + roomId);
		Room room = (Room) db.find(Room.class, roomId);
		
		String dateIn = request.getParameter("date_in");
		Date checkInDate = new Date();
		if ( dateIn != null && !"".equals(dateIn)) {
			DateUtil du = new DateUtil();
			checkInDate = du.toDate(dateIn, "dd-MM-yyyy");
		}
		
		boolean available = roomAvailable(checkInDate, room);
		room.setAvailable(available);
		context.put("room", room);
	}




	private void roomSchedule() {
		vm = path + "schedule.vm";

		String dateIn = request.getParameter("date_in");
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
		context.put("days", days);
		context.put("year", year);
		//month for display in the page
		int month_ = month + 1;
		context.put("month", month_ > 9 ? "" + month_ : "0" + month_);
		int startDay = cal.get(Calendar.DAY_OF_WEEK);
		String sql = "";
		List<Room> rooms = db.list("select r from educate.hostel.entity.Room r order by r.floor.name");
		context.put("rooms", rooms);
		Hashtable h = new Hashtable();
		List<OccupiedRoomSlot> occupiedSlots = new ArrayList<OccupiedRoomSlot>();
		context.put("slots", occupiedSlots);
		for ( Room r : rooms ) {
			OccupiedRoomSlot occupiedRoomSlot = new OccupiedRoomSlot(r);
			occupiedRoomSlot.setYear(year);
			occupiedRoomSlot.setMonth(month);
			for ( int i=1 ; i < days+1; i++ ) {
				Date date = new GregorianCalendar(year, month, i).getTime();
				h.put("date", date);
				sql = "select ra from educate.hostel.entity.RoomAllocation ra where ra.room.id = '" + r.getId() + 
				"' and (ra.checkInDate <= :date and ra.checkOutDate is null)";
				List<RoomAllocation> ras = db.list(sql, h);
				
				for ( RoomAllocation ral : ras ) {
					occupiedRoomSlot.addOccupier(new Occupier(ral.getStudent(), date));
				}
			}
			for ( int i=1 ; i < days+1; i++ ) {
				Date date = new GregorianCalendar(year, month, i).getTime();
				h.put("date", date);
				sql = "select ra from educate.hostel.entity.RoomAllocation ra where ra.room.id = '" + r.getId() + 
				"' and (ra.checkInDate <= :date and ra.checkOutDate >= :date)";
				List<RoomAllocation> ras = db.list(sql, h);
				
				for ( RoomAllocation ral : ras ) {
					occupiedRoomSlot.addOccupier(new Occupier(ral.getStudent(), date));
				}
				
			}
			occupiedSlots.add(occupiedRoomSlot);
		}
		
	}


	private void listUnits() {
		vm = path + "list_units.vm";
		String floorId = request.getParameter("floor_id");
		Floor floor = (Floor) db.find(Floor.class, floorId);
		context.put("floor", floor);
		
		List<Unit> units = floor.getUnits();// unit.getRoomList();
		context.put("units", units);
		
		String studentId = request.getParameter("student_id");
		Student student = (Student) db.find(Student.class, studentId);
		context.put("student", student);
		
	}	


	private void listRooms() {
		vm = path + "list_rooms.vm";
		
		String checkInMode = getParam("checkInMode");
		context.put("checkInMode", checkInMode);
		
		String unitId = request.getParameter("unit_id");
		Unit unit = (Unit) db.find(Unit.class, unitId);
		context.put("unit", unit);
		
		String dateIn = request.getParameter("date_in");
		Date checkInDate = parseDate(dateIn);
		
		if ( checkInDate == null ) checkInDate = new Date();
		
		context.put("check_in_date", checkInDate);
		List<Room> rooms = unit.getRooms();// unit.getRoomList();
		context.put("rooms", rooms);
		for ( Room room : rooms ) {
			boolean available = roomAvailable(checkInDate, room);
			room.setAvailable(available);
		}
		
		String studentId = request.getParameter("student_id");
		Student student = (Student) db.find(Student.class, studentId);
		
		boolean isCheckedIn = false;
		if ( student != null ) {
			Hashtable h = new Hashtable();
			h.put("date", checkInDate);
			String sql = "select r from RoomAllocation r where r.student.id = '" + student.getId() + "' and :date between r.checkInDate and r.expectedDateOut";
			List<RoomAllocation> allocs = db.list(sql, h);
			isCheckedIn = allocs.size() > 0;
		}
		context.put("isCheckedIn", isCheckedIn);
		
		
	}
	


	private void listFloors() {
		vm = path + "list_floors.vm";
		String blockId = request.getParameter("block_id");
		Block block = (Block) db.find(Block.class, blockId);
		context.put("block", block);
		
		String studentId = request.getParameter("student_id");
		Student student = (Student) db.find(Student.class, studentId);
		context.put("student", student);
		
	}

	private void listBlocks() {
		vm = path + "list_blocks.vm";
		String buildingId = request.getParameter("building_id");
		Building building = (Building) db.find(Building.class, buildingId);
		context.put("building", building);
		
		String studentId = request.getParameter("student_id");
		Student student = (Student) db.find(Student.class, studentId);
		context.put("student", student);
		
	}

	private void listBuildings() {
		vm = path + "list_buildings.vm";
		String hostelId = request.getParameter("hostel_id");
		Hostel hostel = (Hostel) db.find(Hostel.class, hostelId);
		context.put("hostel", hostel);
		
		
		
	}

	private void listHostels() {
		vm = path + "list_hostels.vm";
		List<Hostel> hostels = db.list("select h from Hostel h order by h.name");
		context.put("hostels", hostels);
		
	}

	private void deleteOut() throws Exception {
		vm = path + "student_rooms_list.vm";
		String studentId = request.getParameter("student_id");
		Student student = (Student) db.find(Student.class, studentId);
		if ( student != null ) {
			context.put("student", student);
			String allocId = request.getParameter("allocation_id");
			RoomAllocation alloc = (RoomAllocation) db.find(RoomAllocation.class, allocId);

			Date expectedDateOut = alloc.getRealExpectedDateOut();
			
			db.begin();
			alloc.setCheckOutDate(expectedDateOut);
			alloc.setExpectedDateOut(expectedDateOut);
			alloc.setRealExpectedDateOut(expectedDateOut);
			alloc.setCheckOutTime(null);
			alloc.setCheckedOut(false);
			db.commit();
			
			listStudentRooms(student);
		}
		else context.remove("student");
		
	}

	private void roomOut() throws Exception {
		vm = path + "student_rooms_list.vm";
		String studentId = request.getParameter("student_id");
		Student student = (Student) db.find(Student.class, studentId);
		if ( student != null ) {
			context.put("student", student);
			String dateOut = request.getParameter("date_out");
			context.put("date_out", dateOut);
			DateUtil du = new DateUtil();
			Date checkOutDate = du.toDate(dateOut, "dd-MM-yyyy");
			
			String hourOut = request.getParameter("hour_out");
			String minOut = request.getParameter("min_out");
			String ampm = request.getParameter("ampm_out");
			
			String timeOut = (hourOut.length() == 1 ? "0" + hourOut : hourOut) + ":" + (minOut.length() == 1 ? "0" + minOut : minOut) + " " + ampm;
			
			String allocId = request.getParameter("alloc_id");
			RoomAllocation alloc = (RoomAllocation) db.find(RoomAllocation.class, allocId);
			
			Date expectedDateOut = alloc.getExpectedDateOut();
			
			db.begin();
			alloc.setRealExpectedDateOut(expectedDateOut);
			alloc.setExpectedDateOut(checkOutDate);
			alloc.setCheckOutDate(checkOutDate);
			alloc.setCheckOutTime(du.toDate(timeOut, "h:mm a"));
			alloc.setCheckedOut(true);
			db.commit();
			
			listStudentRooms(student);
		}
		else context.remove("student");
		
	}

	private void deleteRoom() throws Exception {
		vm = path +"student_rooms_list.vm";
		String allocId = request.getParameter("allocation_id");
		System.out.println("allocId=" + allocId);
		RoomAllocation alloc = (RoomAllocation) db.find(RoomAllocation.class, allocId);
		
		if (alloc.getTransfer() ) {
			RoomTransferLog transferLog = alloc.getTransferLog();
			if ( transferLog != null ) {
				
				RoomAllocation from = transferLog.getTransferFrom();
				db.begin();
				if ( from != null ) {
					from.setTransfer(false);
					from.setTransferLog(null);
				}
				db.remove(transferLog);
				db.commit();
			}
		}
		
		Student student = alloc.getStudent();
		db.begin();
		db.remove(alloc);
		db.commit();
		
		listStudentRooms(student);
	}
	
	private boolean roomAvailable(Date checkInDate, Room room) {
		int capacity = room.getCapacity();
		Hashtable h = new Hashtable();
		h.put("date", checkInDate);
		String sql = "select ra from RoomAllocation ra where ra.room.id = '" + room.getId() + 
		"' and (ra.checkInDate <= :date and ra.checkOutDate is null)";
		List<RoomAllocation> ras = db.list(sql, h);
		int num = ras.size();
		room.setRoomAllocations(new ArrayList<RoomAllocation>());
		for ( RoomAllocation ra : ras ) {
			room.getRoomAllocations().add(ra);
		}
		
		sql = "select ra from RoomAllocation ra where ra.room.id = '" + room.getId() + 
		"' and (ra.checkInDate <= :date and ra.checkOutDate >= :date)";
		ras = db.list(sql, h);
		num += ras.size();
		for ( RoomAllocation ra : ras ) {
			room.getRoomAllocations().add(ra);
		}		
		
		if ( num < capacity ) return true;
		else return false;
	}
	
	private void transferRoom() throws Exception {
		vm = path + "student_rooms_list.vm";
		String studentId = request.getParameter("student_id");
		Student student = (Student) db.find(Student.class, studentId);
		if ( student != null ) {
			String allocId = request.getParameter("transferAllocId");
			RoomAllocation transferAlloc = (RoomAllocation) db.find(RoomAllocation.class, allocId);
			Room roomFrom = transferAlloc.getRoom();
			Date expectedDateOut = transferAlloc.getExpectedDateOut();
			
			
			String roomId = request.getParameter("room_id");
			Room room = (Room) db.find(Room.class, roomId);
			if ( !roomFrom.getId().equals(room.getId())) {

				context.put("student", student);
				String dateIn = request.getParameter("date_in");
				context.put("date_in", dateIn);
				DateUtil du = new DateUtil();
				Date checkInDate = du.toDate(dateIn, "dd-MM-yyyy");
				
				Calendar c = Calendar.getInstance();
				c.setTime(checkInDate);
				c.add(Calendar.DATE, -1);
				Date checkOutDate = c.getTime();
				

				//boolean available = roomAvailable(checkInDate, room);
				boolean available = true;
				context.put("room", room);
				
				if ( available ) {
					String hourIn = request.getParameter("hour_" + roomId);
					String minIn = request.getParameter("min_" + roomId);
					String ampm = request.getParameter("ap_" + roomId);
					
					String timeIn = (hourIn.length() == 1 ? "0" + hourIn : hourIn) + ":" + (minIn.length() == 1 ? "0" + minIn : minIn) + " " + ampm;
					String transferRemark = getParam("transferRemark_" + allocId);

					db.begin();
					

					
					//check out from previos room
					transferAlloc.setCheckOutDate(checkOutDate);
					transferAlloc.setCheckOutTime(du.toDate(timeIn, "h:mm a"));
					transferAlloc.setRealExpectedDateOut(expectedDateOut);
					transferAlloc.setExpectedDateOut(checkOutDate);
					transferAlloc.setCheckedOut(true);
					transferAlloc.setTransfer(true);
					transferAlloc.setTransferFrom(true);

					//check in to new room
					RoomAllocation alloc = new RoomAllocation();
					alloc.setCheckInDate(checkInDate);
					alloc.setCheckInTime(du.toDate(timeIn, "h:mm a"));
					alloc.setExpectedDateOut(expectedDateOut);
					alloc.setCheckOutDate(expectedDateOut);
					alloc.setRealExpectedDateOut(expectedDateOut);
					alloc.setRoom(room);
					alloc.setStudent(student);
					alloc.setTransfer(true);
					alloc.setTransferTo(true);
					
					RoomTransferLog transferLog = new RoomTransferLog();
					transferLog.setTransferDate(checkInDate);
					transferLog.setTransferTime(du.toDate(timeIn, "h:mm a"));
					transferLog.setTransferFrom(transferAlloc);
					transferLog.setTransferTo(alloc);
					transferLog.setTransferRemark(transferRemark);
					transferLog.setUserId(userId);
					
					transferAlloc.setTransferLog(transferLog);
					alloc.setTransferLog(transferLog);
					
					db.persist(alloc);
					db.persist(transferLog);
					db.commit();
					
					context.put("check_in_success", "yes");
				}
				else {
					context.put("check_in_success", "no");
				}
	
			}
			listStudentRooms(student);
		}
		else context.remove("student");
	}

	private void roomIn() throws Exception {
		vm = path + "student_rooms_list.vm";
		String studentId = request.getParameter("student_id");
		Student student = (Student) db.find(Student.class, studentId);
		if ( student != null ) {
			String roomId = request.getParameter("room_id");
			Room room = (Room) db.find(Room.class, roomId);
			
			context.put("student", student);
			String dateIn = request.getParameter("date_in");
			context.put("date_in", dateIn);
			DateUtil du = new DateUtil();
			Date checkInDate = du.toDate(dateIn, "dd-MM-yyyy");
			
			
			String expDateOut_ = request.getParameter("expected_checkout_date_" + roomId);
			Date expectedDateOut = du.toDate(expDateOut_, "dd-MM-yyyy");
			System.out.println("expected date out = " + expectedDateOut);
			
			boolean available = roomAvailable(checkInDate, room);
			context.put("room", room);
			
			if ( available ) {
				String hourIn = request.getParameter("hour_" + roomId);
				String minIn = request.getParameter("min_" + roomId);
				String ampm = request.getParameter("ap_" + roomId);
				
				String timeIn = (hourIn.length() == 1 ? "0" + hourIn : hourIn) + ":" + (minIn.length() == 1 ? "0" + minIn : minIn) + " " + ampm;
				
				
				
				db.begin();
				RoomAllocation alloc = new RoomAllocation();
				alloc.setCheckInDate(checkInDate);
				alloc.setCheckInTime(du.toDate(timeIn, "h:mm a"));
				alloc.setExpectedDateOut(expectedDateOut);
				alloc.setRealExpectedDateOut(expectedDateOut);
				alloc.setCheckOutDate(expectedDateOut);
				alloc.setRoom(room);
				alloc.setStudent(student);
				db.persist(alloc);
				db.commit();
				
				context.put("check_in_success", "yes");
			}
			else {
				context.put("check_in_success", "no");
			}
			
			listStudentRooms(student);
		}
		else context.remove("student");
		
	}

	private void divClose() {
		vm = path + "none.vm";
	}



	private void studentInfo() throws Exception {
		vm = path + "student_info.vm";
		
		String matricNo = request.getParameter("matric_no");
		String sql = "select s from Student s where s.matricNo = '" + matricNo + "'";
		Student student = (Student) db.get(sql);
		if ( student != null ) {
			context.put("student", student);
			StudentStatusUtil u = new StudentStatusUtil();
			StudentStatus studentStatus = u.getCurrentStudentStatus(student);
			context.put("student_status", studentStatus);
			listStudentRooms(student);
		}
		else {
			context.remove("student");
		}

	}
	

	private void studentInfo2() throws Exception {
		
		//vm = path + "student_info.vm";
		
		String matricNo = studentId;
		
		String sql = "select s from Student s where s.matricNo = '" + matricNo + "'";
		Student student = (Student) db.get(sql);
		if ( student != null ) {
			context.put("student", student);
			StudentStatusUtil u = new StudentStatusUtil();
			StudentStatus studentStatus = u.getCurrentStudentStatus(student);
			context.put("student_status", studentStatus);
			listStudentRooms(student);
		}
		else {
			context.remove("student");
		}
	}

	
	
	

	private void listStudentRooms(Student student) {

		String sql;
		sql = "select r from educate.hostel.entity.RoomAllocation r where r.student.id = '" + student.getId() + "' order by r.checkInDate desc, r.checkInTime desc";
		List<RoomAllocation> allocs = db.list(sql);
		context.put("allocs", allocs);
	}

	private void filterStudents() {
		vm = path + "filter_students.vm";
		String filterName = request.getParameter("filter_name");
		String sql = "select s from Student s WHERE (s.fakeStudent is null OR s.fakeStudent = '') and s.biodata.name like '%" + filterName + "%'";
		List<Student> students = db.list(sql);
		context.put("students", students);
	}

	private void listStudents() {
		vm = path + "students.vm";
		
	}

	private void start() {
		vm = path + "start.vm";
		List<Hostel> hostels = db.list("select h from Hostel h order by h.sequence");
		context.put("hostels", hostels);
		
		if ( !"".equals(studentId)) {
			try {
				studentInfo2();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	private void hostelInfo() {
		String type = request.getParameter("type");
		if ( "hostel".equals(type)) {
			vm = path + "hostels.vm";
			List<Hostel> hostels = db.list("select h from Hostel h order by h.name");
			context.put("hostels", hostels);	
		}
		else if ( "building".equals(type)) {
			vm = path + "buildings.vm";
			String hostelId = request.getParameter("hostel_id");
			Hostel hostel = (Hostel) db.find(Hostel.class, hostelId);
			context.put("hostel", hostel);
		}
		else if ( "block".equals(type)) {
			vm = path + "blocks.vm";
			String buildingId = request.getParameter("building_id");
			Building building = (Building) db.find(Building.class, buildingId);
			context.put("building", building);
			
		}
		else if ( "floor".equals(type)) {
			vm = path + "floors.vm";
			String blockId = request.getParameter("block_id");
			Block block = (Block) db.find(Block.class, blockId);
			context.put("block", block);
			
		}
		else if ( "room".equals(type)) {
			vm = path + "rooms.vm";
			String floorId = request.getParameter("floor_id");
			Floor floor = (Floor) db.find(Floor.class, floorId);
			context.put("floor", floor);
		}
	}

}
