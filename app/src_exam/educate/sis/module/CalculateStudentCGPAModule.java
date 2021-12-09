package educate.sis.module;

import java.util.List;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.sis.exam.entity.FinalResult;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class CalculateStudentCGPAModule extends LebahModule {
	
	private DbPersistence db = new DbPersistence();
	private String path = "apps/calculate_cgpa";

	@Override
	public String start() {
		return path + "/start.vm";
	}
	
	@Command("calculate")
	public String calculate() throws Exception {
		String matricNo = getParam("matricNo");
		Student student = (Student) db.get("select s from Student s where s.matricNo = '" + matricNo + "'");
		context.put("student", student);
		
		ResultEntryUtil.calculateResultCGPA(db, student);
		
		String sql = "select r from FinalResult r where r.student.id = '" + student.getId() + "' order by r.session.startDate";
		List<FinalResult> results = db.list(sql);
		context.put("results", results);
		
		return path + "/calculate.vm";
	}


	
	

}
