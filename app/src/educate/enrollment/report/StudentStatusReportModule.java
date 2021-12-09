package educate.enrollment.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.http.HttpSession;

import educate.db.DbPersistence;
import educate.enrollment.Result;
import educate.enrollment.StudentRegistrationUtil;
import educate.enrollment.entity.StatusType;
import educate.enrollment.entity.StudentStatus;
import educate.sis.struct.entity.Course;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.Session;
import lebah.portal.action.AjaxModule;

/**
 * 
 * @author Shamsul Bahrin Abd Mutalb
 * @version 1.0
 */
public class StudentStatusReportModule  extends AjaxModule  {
	
	private static Hashtable statusNameTable = new Hashtable();
	private final String path = "apps/util/student_status/";
	private String vm = "default.vm";
	HttpSession session;
	String query_NOT_IN = "";
	DbPersistence db = new DbPersistence();
	
	
	public String doAction() throws Exception {
		context.put("statusNameTable", statusNameTable);
		context.put("_formName", formName);
		
		Session currentSession = StudentRegistrationUtil.getCurrentSession();
		context.put("current_session", currentSession.getName());
		String command = request.getParameter("command");
		if ( command == null || "".equals(command)) doReport();
		else if ( "list_students".equals(command)) listStudents();
		else if ( "by_program".equals(command)) doReportByProgram();
		return vm;
	}
	
	private void doReportByProgram() {
		vm = path + "status_stat.vm";
		
		String programId = request.getParameter("program_id");
		Program program = (Program) db.find(Program.class, programId);
		context.put("program", program);
		
		List<StatusType> statusTypes = db.list("select s from StatusType s order by s.sequence");
		context.put("types_length", statusTypes.size());
		List<String> typeCodes = new ArrayList<String>();
		for ( StatusType stype : statusTypes ) {
			typeCodes.add(stype.getCode());
			statusNameTable.put(stype.getCode(), stype.getName());
		}
		statusNameTable.put("NULL", "NULL");
		typeCodes.add("NULL");
		context.put("status_types", typeCodes);
		Hashtable param = new Hashtable();
		param.put("date", new Date());
		
		Hashtable resultTable = new Hashtable();
		Hashtable programTable = new Hashtable();
		Hashtable intakeTable = new Hashtable();
		String sql = "";
		
		sql = "select st.student.intake " +
		"FROM StudentStatus st " +
		"WHERE (st.student.fakeStudent is null OR st.student.fakeStudent = '') " +
		"AND :date BETWEEN st.session.startDate AND st.session.endDate " +
		"AND st.student.program.id = '" + programId  + "' " +
		"GROUP BY st.student.intake.id ORDER BY st.student.intake.startDate";
		List<Session> intakes = db.list(sql, param);
		intakeTable.put(programId, intakes);
		for ( String code : typeCodes ) {
			statistic(resultTable, code, programId);
		}

		context.put("intakeTable", intakeTable);
		context.put("programTable", programTable);
		context.put("resultTable", resultTable);
		
		HttpSession session = request.getSession();
		session.setAttribute("intakeTable", intakeTable);
		session.setAttribute("programTable", programTable);
		session.setAttribute("resultTable", resultTable);
			
	}
	
	private void doReport() {
		vm = path + "report.vm";
		
		List<StatusType> statusTypes = db.list("select s from StatusType s order by s.sequence");
		context.put("types_length", statusTypes.size());
		List<String> typeCodes = new ArrayList<String>();
		for ( StatusType stype : statusTypes ) {
			typeCodes.add(stype.getCode());
			statusNameTable.put(stype.getCode(), stype.getName());
		}
		statusNameTable.put("NULL", "NULL");
		typeCodes.add("NULL");
		context.put("status_types", typeCodes);
		Hashtable param = new Hashtable();
		param.put("date", new Date());
		
		Hashtable resultTable = new Hashtable();
		Hashtable programTable = new Hashtable();
		Hashtable intakeTable = new Hashtable();
		String sql = "";
		sql = "select c from Course c ORDER BY c.name";
		List<Course> courses = db.list(sql);
		context.put("courses", courses);
		for ( Course c : courses ) {
			List<Program> programList = new ArrayList<Program>();
			sql = "select p from Program p WHERE p.course.id = '" + c.getId() + "'";
			List<Program> programs = db.list(sql);
			for ( Program p : programs ) {
				
				sql = "select st.student.intake " +
				"FROM StudentStatus st " +
				"WHERE (st.student.fakeStudent is null OR st.student.fakeStudent = '') " +
				"AND :date BETWEEN st.session.startDate AND st.session.endDate " +
				"AND st.student.program.id = '" + p.getId()  + "' " +
				"GROUP BY st.student.intake.id ORDER BY st.student.intake.startDate";
				List<Session> intakes = db.list(sql, param);
				intakeTable.put(p.getId(), intakes);
				for ( String code : typeCodes ) {
					statistic(resultTable, code, p.getId());
				}
				programList.add(p);
			}
			programTable.put(c.getId(), programList);
		}
		context.put("intakeTable", intakeTable);
		context.put("programTable", programTable);
		context.put("resultTable", resultTable);
		
		HttpSession session = request.getSession();
		session.setAttribute("courses", courses);
		session.setAttribute("intakeTable", intakeTable);
		session.setAttribute("programTable", programTable);
		session.setAttribute("resultTable", resultTable);
			
	}
	
	private void statistic(Hashtable resultTable, String code, String programId) {
		Hashtable param = new Hashtable();
		param.put("date", new Date());
		String sql;
		sql = "select new educate.enrollment.Result(" +
				"st.student.intake.id, " +
				"st.student.intake.code, " +
				"st.student.intake.name, " +
				"COUNT(st)" +
				") " +
			"FROM StudentStatus st " +
			"WHERE (st.student.fakeStudent is null OR st.student.fakeStudent = '') " +
			"AND :date BETWEEN st.session.startDate AND st.session.endDate ";
		if ( "NULL".equals(code))
			sql += "AND st.type is null ";
		else
			sql += "AND st.type.code = '" + code + "' ";
		sql += "AND st.student.program.id = '" + programId  + "' " +
			"GROUP BY st.student.intake.id ORDER BY st.student.intake.startDate";

		List<Result> results = db.list(sql, param);
		for ( Result res : results ) {
			resultTable.put(programId + "_" + res.getKey() + "_" + code, res);
		}
	}
	
	private void listStudents() {
		vm = path + "list_students.vm";
		String programId = request.getParameter("program_id");
		String intakeId = request.getParameter("intake_id");
		String code = request.getParameter("status");
		
		Program program = (Program) db.find(Program.class, programId);
		Session intake = (Session) db.find(Session.class, intakeId);
		String statusName = (String) statusNameTable.get(code);
		
		System.out.println(program.getName());
		System.out.println(intake.getName());
		System.out.println(code);
		
		context.put("program", program);
		context.put("intake", intake);
		context.put("statusName", statusName);
		
		Hashtable param = new Hashtable();
		param.put("date", new Date());
//		String sql = "select st FROM Student s JOIN s.status st " +
//		"WHERE (s.fakeStudent is null OR s.fakeStudent = '') " +
//		"AND :date BETWEEN st.session.startDate AND st.session.endDate ";
//		if ( "NULL".equals(status))
//			sql += "AND st.type is null ";
//		else
//			sql += "AND st.type.code = '" + status + "' ";
//		sql += "AND s.program.id = '" + programId + "' " +
//		"AND s.intake.id = '" + intakeId + "' " +
//		"order by s.biodata.name";
		
		
		String sql;
		sql = "select st FROM StudentStatus st " +
			"WHERE (st.student.fakeStudent is null OR st.student.fakeStudent = '') " +
			"AND :date BETWEEN st.session.startDate AND st.session.endDate ";
		if ( "NULL".equals(code))
			sql += "AND st.type is null ";
		else
			sql += "AND st.type.code = '" + code + "' ";
		sql += "AND st.student.program.id = '" + programId  + "' " +
				"AND st.student.intake.id = '" + intakeId + "' " +
			"ORDER BY st.student.biodata.name";
		
		List<StudentStatus> recs = db.list(sql, param);
		context.put("records", recs);
	}
	


}
