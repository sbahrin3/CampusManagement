package migration.module;

import java.util.List;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.enrollment.entity.StudentStatus;
import educate.sis.general.entity.GradsLevel;
import educate.sis.struct.entity.PeriodScheme;
import educate.sis.struct.entity.Program;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;
import migration.StudentStatusData;

public class FixStudentStatusModule extends LebahModule {
	
	private DbPersistence db = new DbPersistence();
	private String path = "admission/migration";
	
	public void preProcess() {
		System.out.println(command);
	}

	@Override
	public String start() {
		// TODO Auto-generated method stub
		return path + "/start.vm";
	}
	
	@Command("listStudentStatus")
	public String listStudentStatus() throws Exception {
		String matricNo = getParam("matricNo");
		Student student = (Student) db.get("select s from Student s where s.matricNo = '" + matricNo + "'");
		context.put("student", student);
		
		Program program = student.getProgram();
		if ( program != null ) {
			PeriodScheme periodScheme = program.getPeriodScheme();
			if ( periodScheme == null ) {
				List<PeriodScheme> periodSchemes = db.list("select p from PeriodScheme p");
				context.put("periodSchemes", periodSchemes);
			}
		}
		
		List<StudentStatus> statuses = db.list("select ss from StudentStatus ss where ss.student.id = '" + student.getId() + "' order by ss.session.startDate");
		context.put("statuses", statuses);
		
		return path + "/listStudentStatus.vm";
	}
	
	@Command("setProgramPath")
	public String setProgramPath() throws Exception {
		String studentId = getParam("studentId");
		Student student = db.find(Student.class, studentId);
		context.put("student", student);
		List<StudentStatus> statuses = db.list("select ss from StudentStatus ss where ss.student.id = '" + student.getId() + "' order by ss.session.startDate");
		context.put("statuses", statuses);
		
		String sessionPathNo = getParam("sessionPathNo");
		GradsLevel g = db.find(GradsLevel.class, sessionPathNo);
		if ( g != null ) {
			db.begin();
			g.setName(getParam("sessionPathNo"));
			db.commit();
		} 
		else {
			db.begin();
			g = new GradsLevel();
			g.setId(sessionPathNo);
			g.setCode(sessionPathNo);
			g.setName(getParam("sessionPathNo"));
			g.setPathNo(Integer.parseInt(sessionPathNo));
			db.persist(g);
			db.commit();
		}
		
		Program program = student.getProgram();
		if ( program != null ) {
			db.begin();
			program.setLevel(g);
			db.commit();
		}
		
		if ( program != null ) {
			PeriodScheme periodScheme = program.getPeriodScheme();
			if ( periodScheme == null ) {
				List<PeriodScheme> periodSchemes = db.list("select p from PeriodScheme p");
				context.put("periodSchemes", periodSchemes);
			}
		}
	
		return path + "/listStudentStatus.vm";
	}	
	
	@Command("setPeriodScheme")
	public String setPeriodScheme() throws Exception {
		String studentId = getParam("studentId");
		Student student = db.find(Student.class, studentId);
		context.put("student", student);
		
		String periodSchemeId = getParam("periodSchemeId");
		PeriodScheme periodScheme = db.find(PeriodScheme.class, periodSchemeId);
		
		Program program = student.getProgram();
		db.begin();
		program.setPeriodScheme(periodScheme);
		db.commit();
		
		List<StudentStatus> statuses = db.list("select ss from StudentStatus ss where ss.student.id = '" + student.getId() + "' order by ss.session.startDate");
		context.put("statuses", statuses);
		return path + "/listStudentStatus.vm";
	}
	
	@Command("fixStudentStatus")
	public String fixStudentStatus() throws Exception {
		String studentId = getParam("studentId");
		Student student = db.find(Student.class, studentId);
		context.put("student", student);
		
		StudentStatusData.fixStudentStatus(db, studentId);
		
		List<StudentStatus> statuses = db.list("select ss from StudentStatus ss where ss.student.id = '" + student.getId() + "' order by ss.session.startDate");
		context.put("statuses", statuses);
		return path + "/listStudentStatus.vm";
	}

}
