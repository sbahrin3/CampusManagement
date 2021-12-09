package migration;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import educate.admission.entity.Address;
import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.sis.general.entity.Gender;
import educate.sis.general.entity.Race;
import educate.sis.general.entity.State;
import educate.sis.module.StudentStatusUtil;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.Session;

public class ReadXL {
	
	public static void main(String[] args) throws Exception {
		
		DbPersistence db = new DbPersistence();
		
		FileInputStream file = new FileInputStream(new File("/Users/Admin/Documents/erican/erican_data.xls"));
		HSSFWorkbook workbook = new HSSFWorkbook(file);
		HSSFSheet sheet = workbook.getSheetAt(0);
		Iterator<Row> rowIterator = sheet.iterator();
		
		//clear student records
		db.begin();
		db.executeUpdate("delete from Student s");
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
			while(cellIterator.hasNext()) {
				cellNo++;
	            Cell cell = cellIterator.next();
	            switch ( cellNo ) {
	            case 2:
	            	//System.out.print("session: " + cell.getStringCellValue());
	            	String sessionId = "";
	            	if ( cell.getCellType() == Cell.CELL_TYPE_NUMERIC ) sessionId = Double.toString(cell.getNumericCellValue());
	            	else sessionId = cell.getStringCellValue();
	            	Session intake = db.find(Session.class, sessionId);
	            	student.setIntake(intake);
	            	break;
	            case 4:
	            	//System.out.print("program: " + cell.getStringCellValue());
	            	String programId = "";
	            	if ( cell.getCellType() == Cell.CELL_TYPE_NUMERIC ) programId = Double.toString(cell.getNumericCellValue());
	            	else programId = cell.getStringCellValue();
	            	Program program = db.find(Program.class, programId);
	            	student.setProgram(program);
	            	break;
	            case 5:
	            	//System.out.print("matric: " + cell.getStringCellValue());
	            	student.setMatricNo(cell.getStringCellValue().trim());
	            	break;
	            case 6:
	            	//System.out.print("name: " + cell.getStringCellValue());
	            	student.getBiodata().setName(cell.getStringCellValue().trim());
	            	break;
	            case 7:
	            	//System.out.print("icno: " + cell.getStringCellValue());
	            	student.getBiodata().setIcno(cell.getStringCellValue().trim());
	            	break;
	            case 8:
	            	//System.out.print("gender: " + cell.getStringCellValue());
	            	Gender gender = db.find(Gender.class, cell.getStringCellValue());
	            	student.getBiodata().setGender(gender);
	            	break;
	            case 9:
	            	//System.out.print("dob: " + cell.getStringCellValue());
	            	try {
	            		Date dob = new SimpleDateFormat("dd.mm.YYYY").parse(cell.getStringCellValue());
	            		student.getBiodata().setDob(dob);
	            	} catch ( Exception e ) { }
	            	break;
	            case 10:
	            	//System.out.print("phone: " + cell.getStringCellValue());
	            	student.getBiodata().setTelephoneNo(cell.getStringCellValue());
	            	break;
	            case 11:
	            	//System.out.print("race: " + cell.getStringCellValue());
	            	Race race = db.find(Race.class, cell.getStringCellValue());
	            	if ( race != null )
	            		student.getBiodata().setRace(race);
	            	break;
	            case 12:
	            	//System.out.print("address: " + cell.getStringCellValue());
	            	student.getAddress().setAddress1(cell.getStringCellValue());
	            	break;
	            case 13:
	            	//System.out.print("state: " + cell.getStringCellValue());
	            	String stateName = cell.getStringCellValue();
	            	State state = (State) db.get("select s from State s where s.name = '" + stateName + "'");
	            	if ( state != null ) student.getAddress().setState(state);
	            	break;
	            case 17:
	            	//System.out.print("nationality: " + cell.getStringCellValue());
	            	break;
	            default:
	            	//System.out.print(cellNo + ": " + cell.getStringCellValue());
	            }
	            
	            //System.out.print("\t\t");
	        }
			

	        
	        //find if this matricNo already exist in database
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
	        
			System.out.print(rowCount + ") ");
			try {
				System.out.println(student.getMatricNo() + "\t" + student.getBiodata().getName() + "\t" + student.getProgram().getName() + "\t" + student.getIntake().getName());
			} catch ( Exception e ) {
				e.printStackTrace();
			}
			//System.out.println("");
	        
		}
		file.close();
	}

}
