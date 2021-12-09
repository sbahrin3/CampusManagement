package migration;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.enrollment.entity.StudentStatus;
import educate.sis.exam.entity.MarkingGrade;
import educate.sis.module.ResultEntryUtil;
import educate.sis.struct.entity.Session;
import educate.sis.struct.entity.Subject;


//ResultEntryUtil.saveResult(db, studentStatus, subject, mark);

public class ReadStudentResultXL {
	
	public static void sample(String[] args) throws Exception {
		
		DbPersistence db = new DbPersistence();
		
		String matricNo = "DBA/0006/0114/33";
		String sessionCode = "JAN 14";
		String subjectCode = "DBA3101";
		double mark = 65;
		
		saveResult(db, matricNo, sessionCode, subjectCode, mark);
		
		
	}
	
	public static void main(String[] args) throws Exception {
		
	
		
		DbPersistence db = new DbPersistence();
		
		FileInputStream file = new FileInputStream(new File("/Users/Admin/Documents/erican/exam_result.xls"));
		HSSFWorkbook workbook = new HSSFWorkbook(file);
		HSSFSheet sheet = workbook.getSheetAt(0);
		Iterator<Row> rowIterator = sheet.iterator();
		
		int rowCount = 0;
		while ( rowIterator.hasNext() ) {
			rowCount++;
			//if ( rowCount > 11 ) break;
			
			String matricNo = "";
			String sessionCode = "";
			String subjectCode = "";
			double mark = 0.0;
			
			Row row = rowIterator.next();
			Iterator<Cell> cellIterator = row.cellIterator();
			int cellNo = 0;

			while(cellIterator.hasNext()) {
				cellNo++;
	            Cell cell = cellIterator.next();
	            switch ( cellNo ) {
	            case 1:
	            	if ( cell.getCellType() == Cell.CELL_TYPE_NUMERIC ) matricNo = Double.toString(cell.getNumericCellValue());
	            	else matricNo = cell.getStringCellValue();
	            	break;
	            case 2:
	            	if ( cell.getCellType() == Cell.CELL_TYPE_NUMERIC ) sessionCode = Double.toString(cell.getNumericCellValue());
	            	else sessionCode = cell.getStringCellValue();
	            	break;
	            case 3:
	            	if ( cell.getCellType() == Cell.CELL_TYPE_NUMERIC ) subjectCode = Double.toString(cell.getNumericCellValue());
	            	else subjectCode = cell.getStringCellValue();
	            	break;
	            case 4:
	            	if ( cell.getCellType() == Cell.CELL_TYPE_NUMERIC ) mark = cell.getNumericCellValue();
	            	break;
	            default:
	            	//System.out.print(cellNo + ": " + cell.getStringCellValue());
	            }
	        }
			
			if ( !"".equals(matricNo)) {
				System.out.println(matricNo + ", " + sessionCode + ", " + subjectCode + ", " + mark);
				saveResult(db, matricNo, sessionCode, subjectCode, mark);

			}
	    
		}
		file.close();
		
		
	}
	


	private static void saveResult(DbPersistence db, String matricNo,
			String sessionCode, String subjectCode, double mark)
			throws Exception {
		
		Student student = (Student) db.get("select s from Student s where s.matricNo = '" + matricNo + "'");
		if ( student == null ) {
			System.out.println("Can't find student: " + matricNo);
			return;
		}
		Subject subject = (Subject) db.get("select s from Subject s where s.code = '" + subjectCode + "'");
		if ( subject == null ) {
			System.out.println("Can't find subject: " + subjectCode);
			return;
		}
		Session session = (Session) db.get("select s from Session s where s.code = '" + sessionCode + "'");
		if ( session == null ) {
			System.out.println("Can't find session: " + sessionCode);
			return;
		}
		StudentStatus studentStatus = (StudentStatus) db.get("select s from StudentStatus s where s.student.id = '" + student.getId() + "' and s.session.id = '" + session.getId() + "'");
		if ( studentStatus == null ) {
			System.out.println("Can't find student status: " + matricNo + ", " + sessionCode);
			return;
		}
		if ( subject.getMarkingGrade() == null ) {
			MarkingGrade markingGrade = (MarkingGrade) db.get("select g from MarkingGrade g where g.name = 'DEFAULT'");
			if ( markingGrade == null ) {
				System.out.println("Can't find Marking Grade Scheme: " + subjectCode);
				return;
			}
			subject.setMarkingGrade(markingGrade);
		}
		
		ResultEntryUtil.saveResult(db, studentStatus, subject, mark);
		System.out.println("Done: " + matricNo + ", " + studentStatus.getStudent().getBiodata().getName() + ": " + subjectCode + ", " + sessionCode + ", " + studentStatus.getPeriod().getName());
	}

}
