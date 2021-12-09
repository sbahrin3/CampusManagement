package educate.enrollment.module;

import java.util.List;

import educate.db.DbPersistence;
import educate.enrollment.Result;

public class StatisticByProgram {
	
	public static void main(String[] args) {
		DbPersistence db = new DbPersistence();
		String sql = "SELECT new educate.enrollment.Result(s.program.code, s.program.name, COUNT(s)) " +
				"FROM Student s WHERE s.fakeStudent is null " +
				"GROUP BY s.program.code";
		List<Result> results = db.list(sql);
		for ( Result result : results ) {
			System.out.println(result.getKey() + " " + result.getLabel() + " = " + result.getCounter());
		}
		
		
	}

}
