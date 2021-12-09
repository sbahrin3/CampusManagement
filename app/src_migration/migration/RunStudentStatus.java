package migration;

import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;

public class RunStudentStatus {
	
	public static void main(String[] args) throws Exception {
		runAll();
	}
	
	private static void runAll() throws Exception {
	    Logger logger = Logger.getLogger("RunStudentStatusLog");  
	    FileHandler fh;
	    
        fh = new FileHandler("/Users/Admin/Documents/erican/RunStudentStatus.log");  
        logger.addHandler(fh);
        SimpleFormatter formatter = new SimpleFormatter();  
        fh.setFormatter(formatter);  
        
		DbPersistence db = new DbPersistence();
		List<Student> students = db.list("select s from Student s");
		int i = 0;
		for ( Student s : students ) {
			i++;
			try {
				if ( s.getRecordFlag() == null || !s.getRecordFlag().equals("statusfixed")) {
					System.out.println(i + ") " + s.getMatricNo() + ", " + s.getBiodata().getName() + ", " + s.getProgram().getName());
					StudentStatusData.fixStudentStatus2(db, s);
					db.begin();
					s.setRecordFlag("statusfixed");
					db.commit();
				}
			} catch ( MatchingPeriodNotAvailableException e ) {
				logger.info(e.getMessage()); 
				System.out.println(e.getMessage());
			} catch (ProgramNotAvailableException e) {
				logger.info(e.getMessage()); 
				System.out.println(e.getMessage());
			} catch (StudentSessionsNotAvailableException e) {
				logger.info(e.getMessage()); 
				System.out.println(e.getMessage());
			}	
			
			long totalMem = Runtime.getRuntime().totalMemory();
			long freeMem = Runtime.getRuntime().freeMemory();
			
			if ( freeMem < 70000 ) {
				System.gc();
				Thread.sleep(1000);
			}
		}
	}

	private static void test() throws Exception {
		DbPersistence db = new DbPersistence();
		String studentId = "01-DPS 1209-2882";
		try {
			StudentStatusData.fixStudentStatus2(db, studentId);
		} catch ( MatchingPeriodNotAvailableException e ) {
			System.out.println(e.getMessage());
		} catch (ProgramNotAvailableException e) {
			System.out.println(e.getMessage());
		} catch (StudentSessionsNotAvailableException e) {
			System.out.println(e.getMessage());
		}
	}

}
