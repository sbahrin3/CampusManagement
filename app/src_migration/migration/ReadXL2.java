package migration;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import educate.admission.entity.Address;
import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.hostel.entity.Room;
import educate.hostel.entity.RoomAllocation;
import educate.sis.module.StudentStatusUtil;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.Session;
import lebah.util.DateUtil;

public class ReadXL2 {
	
	public static void main(String[] args) throws Exception {
		
		DbPersistence db = new DbPersistence();
		
		FileInputStream file = new FileInputStream(new File("/Users/Admin/Documents/utp-hostel/utp.xls"));
		HSSFWorkbook workbook = new HSSFWorkbook(file);
		HSSFSheet sheet = workbook.getSheetAt(0);
		Iterator<Row> rowIterator = sheet.iterator();
		
		//clear student records
		db.begin();
		db.executeUpdate("delete from Student s");
		db.commit();
		
		db.begin();
		db.executeUpdate("delete from RoomAllocation r");
		db.commit();
		
		int rowCount = 0;
		int matricCounter = 0;
		while ( rowIterator.hasNext() ) {
			rowCount++;
			//if ( rowCount > 11 ) break;
			
			Row row = rowIterator.next();
			Iterator<Cell> cellIterator = row.cellIterator();
			int cellNo = 0;
			
			
			Student student = new Student();
			student.setAddress(new Address());
			Room room = null;
			
			while(cellIterator.hasNext()) {
				cellNo++;
	            Cell cell = cellIterator.next();
	            switch ( cellNo ) {
	            case 1:
	            	String matricNo = "";
	            	if ( cell.getCellType() == Cell.CELL_TYPE_NUMERIC ) matricNo = Double.toString(cell.getNumericCellValue());
	            	else matricNo = cell.getStringCellValue();
	            	student.setMatricNo(matricNo.trim());
	            	break;
	            case 2:
	            	//System.out.print("name: " + cell.getStringCellValue());
	            	student.getBiodata().setName(cell.getStringCellValue().trim());
	            	break;
	            case 3:
	            	String icno = "";
	            	if ( cell.getCellType() == Cell.CELL_TYPE_NUMERIC ) icno = Double.toString(cell.getNumericCellValue());
	            	else icno = cell.getStringCellValue();
	            	student.getBiodata().setIcno(icno.trim());
	            	break;
	            case 4:
	            	String roomCode = "";
	            	if ( cell.getCellType() == Cell.CELL_TYPE_NUMERIC ) roomCode = Double.toString(cell.getNumericCellValue());
	            	else roomCode = cell.getStringCellValue();
	            	room = (Room) db.get("select r from educate.hostel.entity.Room r where r.code = '" + roomCode + "'");
	            	break;
	            case 5:
	            	String programId = "";
	            	if ( cell.getCellType() == Cell.CELL_TYPE_NUMERIC ) programId = Double.toString(cell.getNumericCellValue());
	            	else programId = cell.getStringCellValue();
	            	Program program = db.find(Program.class, programId);
	            	student.setProgram(program);
	            	break;
	            	
	            	
	            case 6:
	            	//System.out.print("session: " + cell.getStringCellValue());
	            	String sessionId = "";
	            	if ( cell.getCellType() == Cell.CELL_TYPE_NUMERIC ) sessionId = Double.toString(cell.getNumericCellValue());
	            	else sessionId = cell.getStringCellValue();
	            	Session intake = db.find(Session.class, sessionId);
	            	student.setIntake(intake);
	            	break;
            default:
	            	//System.out.print(cellNo + ": " + cell.getStringCellValue());
	            }
	            
	            //System.out.print("\t\t");
	        }
			

			if ( !"".equals(student.getMatricNo())) {
		        matricCounter = saveData(db, matricCounter, student);
		        Calendar c1 = Calendar.getInstance();
		        c1.set(Calendar.YEAR, 2014);
		        c1.set(Calendar.MONTH, 0);
		        c1.set(Calendar.DATE, 1);
		        
		        Calendar c2 = Calendar.getInstance();
		        c2.set(Calendar.YEAR, 2014);
		        c2.set(Calendar.MONTH, 11);
		        c2.set(Calendar.DATE, 31);
		        
		        if ( room != null ) {
		        	roomIn(db, student, room, c1.getTime(), c2.getTime());
		        }
		        
				System.out.print(rowCount + ") ");
				try {
					System.out.print(student.getMatricNo() + "\t" + student.getBiodata().getName() + "\t" + student.getProgram().getName() + "\t" + student.getIntake().getName());
					if ( room != null )
						System.out.println("\tRoom: " + room.getCode());
					else
						System.out.println("");
				} catch ( Exception e ) {
					e.printStackTrace();
				}
				
			}
	        
		}
		file.close();
	}

	private static int saveData(DbPersistence db, int matricCounter, Student student) throws Exception {
		int size = db.list("select s from Student s where s.matricNo = '" + student.getMatricNo() + "'").size();
		if ( size == 0 ) {
			//ok
			db.begin();
			db.persist(student);
			db.commit();
		}
		else {
			//this matricNo already exist
			matricCounter++;
			String matricNo = student.getMatricNo() + "-" + matricCounter;
			student.setMatricNo(matricNo);
			db.begin();
			db.persist(student);
			db.commit();
		}

		if ( student.getIntake() != null && student.getProgram() != null ) {
			StudentStatusUtil u = new StudentStatusUtil();
			u.generateStatus(student);
		}
		return matricCounter;
	}

	private static void roomIn(DbPersistence db, Student student, Room room, Date checkInDate, Date expectedDateOut) throws Exception {
		
		if ( student != null ) {
			DateUtil du = new DateUtil();
			//boolean available = roomAvailable(db, checkInDate, room);
			boolean available = true;

			if ( available ) {
				String hourIn = "12";
				String minIn = "00";
				String ampm = "pm";
				
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
			}
		}
		
	}
	
	private boolean roomAvailable(DbPersistence db, Date checkInDate, Room room) {
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
}
