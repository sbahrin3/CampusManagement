package educate.sis.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpSession;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.enrollment.entity.StudentStatus;
import educate.enrollment.entity.StudentSubject;
import educate.sis.exam.entity.FinalResult;
import educate.sis.exam.entity.FinalSubjectResult;
import educate.sis.exam.entity.Grade;
import educate.sis.exam.entity.SubjectResultStatus;
import educate.sis.struct.entity.Period;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.Session;
import educate.sis.struct.entity.Subject;
import educate.sis.struct.entity.SubjectSection;
import lebah.portal.action.AjaxModule;

public class SubjectMarkEntryModule  extends AjaxModule {
	
	String path = "apps/util/mark_entry/subject/";
	private String vm = "default.vm";
	HttpSession session;
	DbPersistence db = new DbPersistence();
	Hashtable data = null;

	
	public String doAction() throws Exception {
		context.put("_formName", formName);
		session = request.getSession();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		context.put("dateFormat", dateFormat);
		context.put("numFormat", new DecimalFormat("#,###,###.00"));
		context.put("programUtil", new ProgramUtil());
		String command = request.getParameter("command");
		System.out.println(command);
		if ( null == command || "".equals(command)) start();
		else if ( "list_subjects".equals(command)) listSubjects();
		else if ( "list_sections".equals(command)) listSections();
		else if ( "list_students_by_section".equals(command)) listStudentsBySection();
		else if ( "list_students".equals(command)) listStudents();
		else if ( "save_student_mark".equals(command)) saveStudentMark();
		else if ( "get_status_list".equals(command)) getStatusList();
		else if ( "set_status".equals(command)) setStatus();
		else if ( "set_status_null".equals(command)) setStatusNull();
		return vm;
	}
	
	private void setStatusNull() throws Exception {
		String periodId = request.getParameter("period_id");
		String sessionId = request.getParameter("session_id");
		String subjectId = request.getParameter("subject_id");
		String studentId = request.getParameter("student_id");
		String counter = request.getParameter("counter");
		context.put("cnt", counter);
		String bgcolor = request.getParameter("bgcolor");
		context.put("bgcolor", bgcolor);
		
		//before saving, check for result consistency
		Student student = (Student) db.find(Student.class, studentId);
		StudentStatus studentStatus = student.getStatus(sessionId);
		if ( studentStatus != null ) {
			checkFinalResult(studentStatus);
		} else {
			System.out.println("Student Status is NULL for " + student.getMatricNo());
			throw new Exception("Student Status is NULL for " + student.getMatricNo());
		}
		//consistency check
		
		String sql = "select s from FinalResult r join r.subjects s" +
		" where r.student.id = '" + studentId + "' " +
		" and r.session.id = '" + sessionId + "' " +
		" and r.period.id = '" + periodId + "' " +
		" and s.subject.id = '" + subjectId + "'";
		FinalSubjectResult result = (FinalSubjectResult) db.get(sql);
		
		db.begin();
		if ( result != null ) {
			result.setResultStatus(null);
			context.put("mark", result.getTotalMarkDisplay());
		}
		db.commit();
		
		context.put("student_id", studentId);
		context.put("subject_result", result);
		
		sql = "select r from FinalResult r " +
		" where r.student.id = '" + studentId + "' " +
		" and r.session.id = '" + sessionId + "' " +
		" and r.period.id = '" + periodId + "' ";
		FinalResult finalResult = (FinalResult) db.get(sql);
		
		//calculate gpa here
		double credithours = 0;
		double points = 0.0d;
		for ( FinalSubjectResult f : finalResult.getSubjects() ) {
			if ( f.getResultStatus() == null ) {
				points += f.getPoint();
				credithours += f.getCreditHour();
			}
			else {
				if ( !f.getResultStatus().getExcludeGPA() ) {
					points += f.getPoint();
					credithours += f.getCreditHour();
				}
			}
		}
		
		db.begin();
		finalResult.setCurrentHours(credithours);
		double gpa = points / credithours;
		finalResult.setGpa(gpa);
		finalResult.setUpdated(true);
		db.commit();
		
		//calculate CGPA each time mark saved
		ResultEntryUtil.calculateCGPA(db, finalResult);
		//calculate rest of cgpa
		ResultEntryUtil.calculateRestOfCGPA(db, finalResult.getStudent(), finalResult.getSession());
		String programLevelType = student.getProgram().getProgramLevelType();
		ResultEntryUtil.calculateStanding(db, student.getId(), programLevelType);

		vm = path + "div_student_mark.vm";
		
		
	}

	private void setStatus() throws Exception {
		String periodId = request.getParameter("period_id");
		String sessionId = request.getParameter("session_id");
		String subjectId = request.getParameter("subject_id");
		
		Subject subject = (Subject) db.find(Subject.class, subjectId);
		
		String studentId = request.getParameter("student_id");
		String counter = request.getParameter("counter");
		context.put("cnt", counter);
		String bgcolor = request.getParameter("bgcolor");
		context.put("bgcolor", bgcolor);
		String statusId = request.getParameter("status_id");
		SubjectResultStatus resultStatus = (SubjectResultStatus) db.find(SubjectResultStatus.class, statusId);
		
		//before saving, check for result consistency
		Student student = (Student) db.find(Student.class, studentId);
		StudentStatus studentStatus = student.getStatus(sessionId);
		if ( studentStatus != null ) {
			checkFinalResult(studentStatus);
		} else {
			System.out.println("Student Status is NULL for " + student.getMatricNo());
			throw new Exception("Student Status is NULL for " + student.getMatricNo());
		}
		//consistency check
		
		String sql = "select s from FinalResult r join r.subjects s" +
		" where r.student.id = '" + studentId + "' " +
		" and r.session.id = '" + sessionId + "' " +
		" and r.period.id = '" + periodId + "' " +
		" and s.subject.id = '" + subjectId + "'";
		FinalSubjectResult result = (FinalSubjectResult) db.get(sql);
		
		db.begin();
		if ( result != null ) {
			if ( resultStatus != null ) {
				result.setResultStatus(resultStatus);
				if ( resultStatus.getResetMark()) {
					result.setTotalMark(0.0d);
					String markingId = subject.getMarkingGrade().getId();
					Grade grade = ExamResultUtil.getGrade(markingId, 0.0d);
					if ( grade != null ) {
						result.setGrade(grade.getLetter());
						result.setGradePoint(grade.getPoint());
						result.setPoint(grade.getPoint() * result.getSubject().getCredithrs());
					}
					else {
						result.setGrade("ND");
						result.setGradePoint(0.0);
						result.setPoint(0.0);
					}
				}
			}
		}
		db.commit();
		
		context.put("student_id", studentId);
		context.put("subject_result", result);
		
		sql = "select r from FinalResult r " +
		" where r.student.id = '" + studentId + "' " +
		" and r.session.id = '" + sessionId + "' " +
		" and r.period.id = '" + periodId + "' ";
		FinalResult finalResult = (FinalResult) db.get(sql);
		
		
		//calculate gpa here
		double credithours = 0;
		double points = 0.0d;
		for ( FinalSubjectResult f : finalResult.getSubjects() ) {
			if ( f.getResultStatus() == null ) {
				points += f.getPoint();
				credithours += f.getCreditHour();
			}
			else {
				if ( !f.getResultStatus().getExcludeGPA() ) {
					points += f.getPoint();
					credithours += f.getCreditHour();
				}
			}
		}
		
		db.begin();
		finalResult.setCurrentHours(credithours);
		double gpa = points / credithours;
		finalResult.setGpa(gpa);
		finalResult.setUpdated(true);
		db.commit();
		
		
		//calculate CGPA each time mark saved
		ResultEntryUtil.calculateCGPA(db, finalResult);
		//calculate rest of cgpa
		ResultEntryUtil.calculateRestOfCGPA(db, finalResult.getStudent(), finalResult.getSession());
		String programLevelType = student.getProgram().getProgramLevelType();
		ResultEntryUtil.calculateStanding(db, student.getId(), programLevelType);
		

		vm = path + "div_student_mark.vm";

		
		
	}

	private void getStatusList() {
		//
		String studentId = request.getParameter("student_id");
		context.put("student_id", studentId);
		
		String cnt = request.getParameter("counter");
		String bgcolor = request.getParameter("bgcolor");
		
		context.put("cnt", cnt);
		context.put("bgcolor", bgcolor);
		
		List<SubjectResultStatus> statuses = db.list("select s from SubjectResultStatus s");
		context.put("statuses", statuses);
		
		vm = path + "list_statuses.vm";
		
	}

	private void listStudentsBySection() {
		String programId = request.getParameter("program_id") != null ? request.getParameter("program_id") : "";
		String sessionId = request.getParameter("session_id") != null ? request.getParameter("session_id") : "";
		String periodId = request.getParameter("period_id") != null ? request.getParameter("period_id") : "";
		String subjectId = request.getParameter("subject_id") != null ? request.getParameter("subject_id") : "";
		String sectionId = request.getParameter("section_id") != null ? request.getParameter("section_id") : "";

		SubjectSection subjectSection = !"".equals(sectionId) ? (SubjectSection) db.find(SubjectSection.class, sectionId) : null;
		if ( subjectSection != null ) context.put("section", subjectSection); else context.remove("section");
		
		String sql = "";
		sql = "select s from StudentStatus st join st.student s join st.studentSubjects ss " +
			"WHERE (s.fakeStudent is null OR s.fakeStudent = '') " +
			" and st.session.id = '" + sessionId + "' and st.period.id = '" + periodId + "' and s.program.id = '" + programId + "' " +
			" and ss.subject.id = '" + subjectId + "'";
		if ( !"".equals(sectionId) ) sql += " and ss.section.id = '" + sectionId + "'";
		sql += " and st.type.inActive = 0 ";
		sql += " order by s.biodata.name";
		
	
		List<Student> students = db.list(sql);
		context.put("students", students);
		//get results
		Hashtable result = new Hashtable();
		sql = "select r " +
		" from FinalResult f join f.subjects r where " +
		" r.subject.id = '" + subjectId + "' and f.session.id = '" + sessionId + "' and f.period.id = '" + periodId + "'";
		List<FinalSubjectResult> resultList = db.list(sql);
		for ( FinalSubjectResult r : resultList) {
			result.put(r.getParent().getStudent().getId(), r);
		}
		context.put("result", result);
		
		vm = path + "div_list_students.vm";

		
	}

	private void saveStudentMark() throws Exception {
		context.remove("error");
		
		String periodId = request.getParameter("period_id");
		String sessionId = request.getParameter("session_id");
		String subjectId = request.getParameter("subject_id");
		String studentId = request.getParameter("student_id");
		String counter = request.getParameter("counter");
		context.put("cnt", counter);
		String bgcolor = request.getParameter("bgcolor");
		context.put("bgcolor", bgcolor);
		String marks = request.getParameter("marks_" + counter);
		try {
			saveResult(studentId, sessionId, periodId, subjectId, marks);
		} catch ( Exception e ) {
			e.printStackTrace();
			context.put("error", e.getMessage());
		}
		
		String sql = "select s from FinalResult r join r.subjects s" +
		" where r.student.id = '" + studentId + "' " +
		" and r.session.id = '" + sessionId + "' " +
		" and r.period.id = '" + periodId + "' " +
		" and s.subject.id = '" + subjectId + "'";
		FinalSubjectResult result = (FinalSubjectResult) db.get(sql);
		
		context.put("mark", marks);
		context.put("student_id", studentId);
		context.put("subject_result", result);

		vm = path + "div_student_mark.vm";
		
	}
	
	private void listSections() throws Exception {
		
		String programId = request.getParameter("program_id") != null ? request.getParameter("program_id") : "";
		String sessionId = request.getParameter("session_id") != null ? request.getParameter("session_id") : "";
		String periodId = request.getParameter("period_id") != null ? request.getParameter("period_id") : "";
		String subjectId = request.getParameter("subject_id") != null ? request.getParameter("subject_id") : "";
		
		Program program = !"".equals(programId) ? (Program) db.find(Program.class, programId) : null;
		Session session = !"".equals(sessionId) ? (Session) db.find(Session.class, sessionId) : null;
		Period period =  !"".equals(periodId) ? (Period) db.find(Period.class, periodId) : null;
		Subject subject = !"".equals(subjectId) ? (Subject) db.find(Subject.class, subjectId) : null;
		
		if ( program != null ) context.put("program", program); else context.remove("program");
		if ( session != null ) context.put("session", session); else context.remove("session");
		if ( period != null ) context.put("period", period); else context.remove("period");
		if ( subject != null ) context.put("subject", subject); else context.remove("subject");
		
		
		String sql = "select count(st) from StudentStatus st join st.studentSubjects ss join ss.section s " +
		"where st.student.program.id = '" + programId + " ' and st.session.id = '" + sessionId + "' " +
		"and st.period.id = '" + periodId + "' and ss.subject.id = '" + subjectId + "' and st.type.inActive = 0";
		
		List<Long> list = db.list(sql);
		if ( list.size() > 0 ) context.put("total_students", list.get(0));
		else context.put("total_students", 0);
		
		
		
		sql = "select new educate.sis.module.SectionGroup(s.id, s.code, count(s)) from StudentStatus st join st.studentSubjects ss join ss.section s " +
				"where st.student.program.id = '" + programId + "' and st.session.id = '" + sessionId + "' " +
				"and st.period.id = '" + periodId + "' and ss.subject.id = '" + subjectId + "' and st.type.inActive = 0" +
				" group by s";
		List<SectionGroup> groups = db.list(sql);
		context.put("groups", groups);
		
		vm = path + "list_sections.vm";
	}

	private void listStudents() throws Exception {
		
		
		String programId = request.getParameter("program_id") != null ? request.getParameter("program_id") : "";
		String sessionId = request.getParameter("session_id") != null ? request.getParameter("session_id") : "";
		String periodId = request.getParameter("period_id") != null ? request.getParameter("period_id") : "";
		String subjectId = request.getParameter("subject_id") != null ? request.getParameter("subject_id") : "";
		
		Program program = !"".equals(programId) ? (Program) db.find(Program.class, programId) : null;
		Session session = !"".equals(sessionId) ? (Session) db.find(Session.class, sessionId) : null;
		Period period =  !"".equals(periodId) ? (Period) db.find(Period.class, periodId) : null;
		Subject subject = !"".equals(subjectId) ? (Subject) db.find(Subject.class, subjectId) : null;
		
		if ( program != null ) context.put("program", program); else context.remove("program");
		if ( session != null ) context.put("session", session); else context.remove("session");
		if ( period != null ) context.put("period", period); else context.remove("period");
		if ( subject != null ) context.put("subject", subject); else context.remove("subject");
		
		Hashtable param = new Hashtable();
		param.put("session", session);
		param.put("period", period);
		param.put("program", program);
		param.put("subject", subject);
		
		String sql = "";
		sql = "select s from StudentStatus st join st.student s join st.studentSubjects ss " +
			"WHERE (s.fakeStudent is null OR s.fakeStudent = '') " +
			" and st.session = :session and st.period = :period and s.program = :program" +
			" and ss.subject = :subject ";
		sql += " and st.type.inActive = 0 ";
		sql += " order by s.biodata.name";
		System.out.println(sql);
		List<Student> students = db.list(sql, param);
		System.out.println(students.size());
		context.put("students", students);
		//get results
		Hashtable result = new Hashtable();
		Hashtable p = new Hashtable();
		p.put("session", session);
		p.put("period", period);
		p.put("subject", subject);
		sql = "select r " +
		" from FinalResult f join f.subjects r where " +
		" r.subject = :subject and f.session = :session and f.period = :period ";
		List<FinalSubjectResult> resultList = db.list(sql, p);
		for ( FinalSubjectResult r : resultList) {
			result.put(r.getParent().getStudent().getId(), r);
		}
		context.put("result", result);
		
		vm = path + "list_students.vm";
	}

	private void listSubjects() {
		vm = path + "list_subjects.vm";
		String programId = request.getParameter("program_id") != null ? request.getParameter("program_id") : "";
		String sessionId = request.getParameter("session_id") != null ? request.getParameter("session_id") : "";
		String periodId = request.getParameter("period_id") != null ? request.getParameter("period_id") : "";
		
		Program program = !"".equals(programId) ? (Program) db.find(Program.class, programId) : null;
		Session session = !"".equals(sessionId) ? (Session) db.find(Session.class, sessionId) : null;
		Period period =  !"".equals(periodId) ? (Period) db.find(Period.class, periodId) : null;
		
		if ( program != null ) context.put("program", program); else context.remove("program");
		if ( session != null ) context.put("session", session); else context.remove("session");
		if ( period != null ) context.put("period", period); else context.remove("period");
		
		Hashtable param = new Hashtable();
		param.put("program", program);
		param.put("session", session);
		param.put("period", period);
		
		String sql = "";

		sql = "select distinct s from StudentStatus st join st.studentSubjects ss join ss.subject s " +
			  "where st.student.program = :program " +
			  "and st.session = :session " +
			  "and st.period = :period " +
			  "order by s.name";
		List<Subject> subjects = db.list(sql, param);
		if ( subjects.size() > 0 ) {
			context.put("subjects", subjects);
		}
		else context.remove("subjects");
		
		
	}

	private void start() {
		// TODO Auto-generated method stub
		vm = path + "select.vm";
		String programId = request.getParameter("program_id") != null ? request.getParameter("program_id") : "";
		String sessionId = request.getParameter("session_id") != null ? request.getParameter("session_id") : "";
		String periodId = request.getParameter("period_id") != null ? request.getParameter("period_id") : "";
		
		Program program = !"".equals(programId) ? (Program) db.find(Program.class, programId) : null;
		Session session = !"".equals(sessionId) ? (Session) db.find(Session.class, sessionId) : null;
		Period period =  !"".equals(periodId) ? (Period) db.find(Period.class, periodId) : null;
		
		if ( program != null ) context.put("program", program); else context.remove("program");
		if ( session != null ) context.put("session", session); else context.remove("session");
		if ( period != null ) context.put("period", period); else context.remove("period");
		
		List<Program> programs = db.list("select p from Program p order by p.code");
		context.put("programs", programs);
		
		List<Session> sessions = null;
		if ( program != null ) {
			sessions = db.list("select s from Session s where s.pathNo = " + program.getLevel().getPathNo() + " order by s.startDate");
			context.put("sessions", sessions);
		}
		else context.remove("sessions");
		
		List<Period> periods = null;
		if ( program != null ) {
			periods = program.getPeriodScheme().getFunctionalPeriodList();
			context.put("periods", periods);
		}
		else context.remove("periods");
		
//		List<LearningCentre> centres = db.list("select c from LearningCentre c order by c.code");
//		context.put("centres", centres);
		
	}
	
	private synchronized void saveResult(String studentId, String sessionId, String periodId, String subjectId, String mark) throws Exception {
		
		//before saving, check for result consistency
		
		Student student = (Student) db.find(Student.class, studentId);
		StudentStatus studentStatus = student.getStatus(sessionId);
		if ( studentStatus != null ) {
			checkFinalResult(studentStatus);
		} else {
			System.out.println("Student Status is NULL for " + student.getMatricNo());
			throw new Exception("Student Status is NULL for " + student.getMatricNo());
		}
		
		//
		

		Subject subject = (Subject) db.find(Subject.class, subjectId);
		String markingId = subject.getMarkingGrade().getId();
		if ( markingId != null ) {
			String sql;
			//final subject result
			sql = "select r from FinalResult r " +
					" where r.student.id = '" + studentId + "' " +
					" and r.session.id = '" + sessionId + "' " +
					" and r.period.id = '" + periodId + "' ";
			FinalResult result = (FinalResult) db.get(sql);
	
			if ( result != null ) {
				double hours = 0.0;
				double point = 0.0;
				//
				FinalSubjectResult r = result.getSubject(subjectId);
				if ( r != null ) {
					
					//CHECK IF THIS IN NON-NUMERICAL VALUE 
					int markValue = ResultEntryUtil.nonNumericalMark(mark);
					if ( markValue < 0 ) {
						Grade grade = !"".equals(markingId) ? ExamResultUtil.getGrade(markingId, mark) : null;
						
						db.begin();
						if ( grade != null ) {
							r.setGrade(grade.getLetter());
							r.setGradePoint(grade.getPoint());
							r.setPoint(grade.getPoint() * r.getSubject().getCredithrs());
						}
						else {
							System.out.println("Grade is null for " + r.getSubject().getCode());
							throw new Exception("This grade does not exists.");
						}
						r.setTotalMark(markValue);
						r.setCreditHour(r.getSubject().getCredithrs());
						db.commit();
						
						
					}
					//this is real mark value
					else {
						double dmark = 0.0d;
						try {
							dmark = Double.parseDouble(mark);
						} catch ( Exception e ) {}
						//Grade grade = Grading.lookup(mark, r.getSubject().getId());
						Grade grade = ExamResultUtil.getGrade(markingId, dmark);
						db.begin();
						if ( grade != null ) {
							r.setGrade(grade.getLetter());
							r.setGradePoint(grade.getPoint());
							r.setPoint(grade.getPoint() * r.getSubject().getCredithrs());
						}
						else {
							r.setGrade("ND");
							r.setGradePoint(0.0);
							r.setPoint(0.0);
						}
						r.setTotalMark(dmark);
						r.setCreditHour(r.getSubject().getCredithrs());
						db.commit();
						

					
					}
					
					//calculate gpa here
					double credithours = 0;
					double points = 0.0d;
					for ( FinalSubjectResult f : result.getSubjects() ) {
						if ( f.getResultStatus() == null ) {
							points += f.getPoint();
							credithours += f.getCreditHour();
						}
						else {
							if ( !f.getResultStatus().getExcludeGPA() ) {
								points += f.getPoint();
								credithours += f.getCreditHour();
							}
						}
					}
					
					db.begin();
					result.setCurrentHours(credithours);
					double gpa = points / credithours;
					result.setGpa(gpa);
					result.setUpdated(true);
					db.commit();
					
					//calculate CGPA each time mark saved
					ResultEntryUtil.calculateCGPA(db, result);
					//calculate rest of cgpa
					ResultEntryUtil.calculateRestOfCGPA(db, result.getStudent(), result.getSession());
					String programLevelType = student.getProgram().getProgramLevelType();
					ResultEntryUtil.calculateStanding(db, student.getId(), programLevelType);
					
				} else {
					System.out.println("Cannot find FinalResult.");
				}
			}
			else {
				//subject not in final result, so need to create
				System.out.println("Subject not in final result");
			}
			
			//calculate cgpa
		
		}
		else {
			System.out.println("MaringGrade not available for " + subject.getCode());
		}

		

	}
	
	public void calculateCGPA(StudentStatus status, FinalResult result) throws Exception {
		Date date = status.getSession().getStartDate();
		Hashtable param = new Hashtable();
		param.put("date", date);
		String sql = "select r from FinalResult r " +
		" where r.student.id = '" + status.getStudent().getId() + "' " +
		" and r.session.startDate <= :date order by r.session.startDate";
		List<FinalResult> results = db.list(sql, param);
		double cumPoint = 0.0;
		double cumHours = 0.0;
		double cgpa = 0.0;
		for ( FinalResult r : results ) {
			//System.out.println(r.getPeriod().getName());
			for ( FinalSubjectResult f : r.getSubjects() ) {
				//System.out.println(f.getSubject().getCode() + ", " + f.getPoint() + ", " + f.getCreditHour());
				cumPoint += f.getPoint();
				cumHours += f.getCreditHour();
			}
		}
		cgpa = cumPoint / cumHours;
		db.begin();
		result.setCgpa(cgpa);
		result.setTotalHours(cumHours);
		db.commit();
	}
	
	private void checkFinalResult(StudentStatus studentStatus) throws Exception {
		String sql;
		//final subject result
		sql = "select r from FinalResult r " +
				" where r.student.id = '" + studentStatus.getStudent().getId() + "' " +
				" and r.session.id = '" + studentStatus.getSession().getId() + "' " +
				" and r.period.id = '" + studentStatus.getPeriod().getId() + "' ";
		FinalResult result = (FinalResult) db.get(sql);
		
		if ( result != null ) {
			Set<StudentSubject> studentSubjects = studentStatus.getStudentSubjects();
			List<String> subjects1 = new ArrayList<String>();
			List<FinalSubjectResult> subjectResults = result.getSubjects();
			if ( subjectResults != null ) {
				for ( FinalSubjectResult r : subjectResults ) {
					if ( r.getSubject() != null ) subjects1.add(r.getSubject().getId());
				}
			}

			db.begin();
			for ( StudentSubject s : studentSubjects ) {
				if ( s.getSubject() != null ) {
					if ( !subjects1.contains(s.getSubject().getId())) {
						FinalSubjectResult f = new FinalSubjectResult();
						f.setSubject(s.getSubject());
						result.addSubject(f);
					}
				}
				else {
					System.out.println("No subject for " + s.getStudentStatus().getStudent().getMatricNo());
				}
			}
			db.commit();

		}
		else {
			db.begin();
			FinalResult finalResult = new FinalResult(studentStatus.getSession().getCode(), studentStatus.getStudent().getMatricNo());
			finalResult.setStudent(studentStatus.getStudent());
			finalResult.setPeriod(studentStatus.getPeriod());
			finalResult.setSession(studentStatus.getSession());
			finalResult.setTime(new Date());
			finalResult.setCreated(new Date());
			
			Set<StudentSubject> studentSubjects = studentStatus.getStudentSubjects();
			for ( StudentSubject s : studentSubjects ) {
				FinalSubjectResult f = new FinalSubjectResult();
				f.setSubject(s.getSubject());
				finalResult.addSubject(f);
			}
			db.persist(finalResult);
			db.commit();
		}
	}
	
	public static void main(String[] args) {
		String sql = "select f from FinalResult f join f.student s join s.status st join st.studentSubjects ss " +
				"WHERE (s.fakeStudent is null OR s.fakeStudent = '')  and st.session.id = '2010/2011-M' " +
				"and st.period.id = 'SEMESTERS-SEM 1' and s.program.id = '13'  and ss.subject.id = 'ENG2013' " +
				"and ss.section.id = 'A' and f.session.id = '2010/2011-M' and f.period.id = 'SEMESTERS-SEM 1' order by s.biodata.name";
		
		DbPersistence db = new DbPersistence();
		List<FinalResult> results = db.list(sql);
		System.out.println(results.size());
	
	}
}
