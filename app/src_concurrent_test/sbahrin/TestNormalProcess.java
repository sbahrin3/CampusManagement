package sbahrin;

import java.sql.ResultSet;

import org.perf4j.StopWatch;

import lebah.db.Database;

public class TestNormalProcess {
	
public static void main(String[] args) throws Exception {
				
		System.out.println();
		System.out.println("Process: normal.");
		StopWatch stopWatch = new StopWatch();
		int result = 0;
		Database db = null;
		try {
			db = new Database("com.mysql.jdbc.Driver","jdbc:mysql://localhost:3307/erican_data?zeroDateTimeBehavior=convertToNull","root","");
			ResultSet rs = db.getStatement().executeQuery("select s.matricNo from enrl_student s");
			int cnt1 = 0;
			while ( rs.next() ) {
				cnt1++;
				System.out.println("side = 1 - " + cnt1);
			}
			
			ResultSet rs2 = db.getStatement().executeQuery("select s.matricNo from enrl_student s");
			int cnt2 = 0;
			while ( rs2.next() ) {
				cnt2++;
				System.out.println("side = 2 - " + cnt2);
			}
			result = cnt1 + cnt2;
		} finally {
			if ( db != null ) db.close();
		}
		
		System.out.println("Result normal process = " + result);
		stopWatch.stop();
		System.out.println("Elapsed time for normal process: " + stopWatch.getElapsedTime());
			
	}

}
