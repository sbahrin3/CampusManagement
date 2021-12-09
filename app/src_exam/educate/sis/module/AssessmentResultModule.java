package educate.sis.module;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import educate.admission.Application;
import educate.admission.entity.Applicant;
import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.enrollment.entity.StudentStatus;
import educate.enrollment.entity.StudentSubject;
import educate.sis.exam.entity.AssessmentResult;
import educate.sis.exam.entity.Coursework;
import educate.sis.exam.entity.CourseworkItem;
import educate.sis.exam.entity.ExamResult;
import educate.sis.exam.entity.FinalResult;
import educate.sis.exam.entity.FinalSubjectResult;
import educate.sis.exam.entity.Grade;
import educate.sis.exam.entity.MarkEntryPeriod;
import educate.sis.exam.entity.MarkingGrade;
import educate.sis.exam.entity.SessionResult;
import educate.sis.exam.entity.SubjectGrade;
import educate.sis.exam.entity.SubjectResult;
import educate.sis.exam.entity.SubjectResultStatus;
import educate.sis.exam.module.SpecialSubjectStatus;
import educate.sis.struct.entity.LearningCentre;
import educate.sis.struct.entity.Period;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.Session;
import educate.sis.struct.entity.Subject;
import educate.sis.struct.entity.SubjectSection;
import lebah.portal.action.AjaxModule;

public class AssessmentResultModule extends AjaxModule {
	
	
	private String path = "apps/util/assessment_result/";
	private String vm = path + "default.vm";
	private DbPersistence db = new DbPersistence();
	protected String teacherId = "";

	
	public String doAction() throws Exception {
		
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy"));
		context.put("timeFormat", new SimpleDateFormat("hh:mm a"));	
		
		context.put("numFormat", new DecimalFormat("#.00"));
		context.put("resultFormat", new DecimalFormat("##"));
		
		getMarksEntryPeriod();


		
		if ( !"".equals(teacherId)) context.put("teacher_mode", true);
		else context.remove("teacher_mode");
		
		context.put("teacherId", teacherId);
		
		String userId = (String) request.getSession().getAttribute("_portal_login");
		context.put("userId", userId);
		
		context.put("_formName", formName);
		request.getSession();
		
		String command = request.getParameter("command");
		System.out.println(command);
		

		if ( null == command || "".equals(command)) start();
		else if ( "list_subjects".equals(command)) listSubjects();
		else if ( "list_students".equals(command)) {
			if ( "".equals(teacherId)) {
				listStudents();
			}
			else {
				listStudents();
				//listStudentsByTeacher();
			}
		}
		else if ( "edit_coursework".equals(command)) editCoursework();
		else if ( "add_coursework".equals(command)) addCoursework();
		else if ( "save_coursework".equals(command)) updateCoursework();
		else if ( "delete_coursework_item".equals(command)) deleteCourseworkItem();
		else if ( "next_page".equals(command)) nextPage();
		else if ( "prev_page".equals(command)) prevPage();
		else if ( "goto_page".equals(command)) gotoPage();
		else if ( "get_assessment".equals(command)) getAssessment();
		else if ( "save_entry".equals(command)) saveAssessmentEntry2();
		
		else if ( "get_status_list".equals(command)) getStatusList();
		else if ( "set_status".equals(command)) {
			saveAssessmentEntry2();
		}
		else if ( "set_status_null".equals(command)) {
			setStatusNull();
		}

		else if ( "done".equals(command)) done();
		
		else if ( "student_info".equals(command)) studentInfo();
		else if ( "mark_entry_period".equals(command)) markEntryPeriod();
		else if ( "save_mark_entry_period".equals(command)) saveMarkEntryPeriod();
		
		else if ( "getSessions".equals(command)) getSessions();
		else if ( "getIntakes".equals(command)) getIntakes();
		
		else if ( "getAllSessions".equals(command)) getAllSessions();
		
		else if ( "helpMarkingDistribution".equals(command)) vm = path + "helpMarkingDistribution.vm";
		
		return vm;
	}
	
	private void getIntakes() {
		String programId = getParam("program_id");
		//Program program = db.find(Program.class, programId);
		//context.put("intakes", db.list("select s from Session s where s.pathNo = " + program.getLevel().getPathNo() + " order by s.startDate"));
		
		List<Session> sessions = db.list("select s.intake from StudentSubject sb Join sb.studentStatus st Join st.student s where st.student.program.id = '" + programId + "' group by s.intake order by s.intake.startDate");
		context.put("intakes", sessions);
		
		vm = path + "divIntakes.vm";
	}
	
	private void getSessions() {
		String programId = getParam("program_id");
		//Program program = db.find(Program.class, programId);
		//context.put("sessions", db.list("select x from Session x ORDER BY x.pathNo, x.startDate"));
		
		List<Session> sessions = db.list("select s from StudentSubject sb Join sb.studentStatus st Join st.session s where st.student.program.id = '" + programId + "' group by s order by s.startDate");
		context.put("sessions", sessions);
		
		vm = path + "divSessions.vm";
	}
	
	private void getAllSessions() {
		String programId = getParam("program_id");
		
		Program program = db.find(Program.class, programId);
		context.put("sessions", db.list("select x from Session x ORDER BY x.pathNo, x.startDate"));
		
		//List<Session> sessions_ = db.list("select s from StudentSubject sb Join sb.studentStatus st Join st.session s where st.student.program.id = '" + programId + "' group by s.id order by s.startDate");
		
		vm = path + "divSessions.vm";
	}	

	private void addCoursework() {
		// TODO Auto-generated method stub
		
	}

	private void deleteCourseworkItem() {
		// TODO Auto-generated method stub
		
	}

	private void saveCoursework() {
		// TODO Auto-generated method stub
		
	}

	private void editCoursework() throws Exception {
		String subjectId = request.getParameter("subject_id");
		String sessionId = request.getParameter("session_id");
		String centreId = request.getParameter("centre_id");
		String intakeId = request.getParameter("intake_id");
		String programId = getParam("program_id");
		
		String sql = "select c from Coursework c where c.subject.id = '" + subjectId + "' and c.session.id = '" + sessionId + "' and c.centre.id = '" + centreId + "'";
		Coursework coursework = (Coursework) db.get(sql);
		context.put("coursework", coursework);
		if ( coursework != null ) {
			//System.out.println("GOT COURSEWORK");
			context.put("cwItems", coursework.getCourseworkItems());
		}
		else {
			//System.out.println("COURSEWORK N/A");
			context.remove("cwItems");
		}
		
		vm = path + "edit_coursework.vm";
	}

	private void listSubjects() {
		String sessionId = getParam("session_id");
		String programId = getParam("program_id");
		String intakeId = getParam("intake_id");
		String sql = "";
		if ( "".equals(teacherId)) {
			sql = "select s from StudentSubject ss join ss.subject s " +
					"where ss.studentStatus.session.id = '" + sessionId + "' ";
			if ( !"".equals(intakeId))
				sql += " and ss.studentStatus.student.intake.id = '" + intakeId + "' ";
			
			sql += "and ss.studentStatus.student.program.id = '" + programId + "' " +
					"group by s order by s.code ";
		} 
		else {
			//sql = "select s from educate.sis.struct.entity.Teacher t join t.teacherSubjects ts join ts.subject s " +
			//"where t.userId = '" + teacherId + "'";
			sql = "select ts.subject from TeacherSubject ts where ts.teacher.userId = '" + teacherId + "'";
		}
		
		List<Subject> subjects = db.list(sql);
		
		context.put("subjects", subjects);
		vm = path + "divListSubjects.vm";
	}

	public static Date parseDate(String dateTxt) {
		if ( dateTxt != null && !"".equals(dateTxt)) {
			try {
				return new SimpleDateFormat("dd-MM-yyyy").parse(dateTxt);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	


	private void saveMarkEntryPeriod() throws Exception {
		vm = path + "mark_allow.vm";
		String startMarkDate = request.getParameter("start_mark_date");
		String endMarkDate = request.getParameter("end_mark_date");
		MarkEntryPeriod markEntryPeriod = db.find(MarkEntryPeriod.class, "current");
		db.begin();
		markEntryPeriod.setStartDate(parseDate(startMarkDate));
		markEntryPeriod.setEndDate(parseDate(endMarkDate));
		db.commit();
		
		
		context.put("markEntryPeriod", markEntryPeriod);
		boolean allowMarkEntry = false;
		Date today = new Date();
		if ( new SimpleDateFormat("dd-MM-yyyy").format(today).equals(new SimpleDateFormat("dd-MM-yyyy").format(markEntryPeriod.getStartDate())) ) {
			allowMarkEntry = true;
		}
		if ( !allowMarkEntry ) {
			if ( today.after(markEntryPeriod.getStartDate()) && today.before(markEntryPeriod.getEndDate())) {
				allowMarkEntry = true;
			}
		}
		if ( !allowMarkEntry ) {
			if ( new SimpleDateFormat("dd-MM-yyyy").format(today).equals(new SimpleDateFormat("dd-MM-yyyy").format(markEntryPeriod.getEndDate())) ) {
				allowMarkEntry = true;
			}
		}
		if ( allowMarkEntry ) context.put("allowMarkEntry", true);
		else context.remove("allowMarkEntry");

		
	}



	private void markEntryPeriod() {
		vm = path + "mark_entry_period.vm";
		
		MarkEntryPeriod m = db.find(MarkEntryPeriod.class, "current");
		context.put("markEntryPeriod", m);
		
		
	}



	private void studentInfo() throws Exception {
		String studentId = request.getParameter("student_info_id");
		Student student = db.find(Student.class, studentId);
		if ( student != null ) {
			context.put("student", student);
			StudentStatusUtil u = new StudentStatusUtil();
			StudentStatus studentStatus = u.getCurrentStudentStatus(student);
			if ( studentStatus == null ) context.remove("student_status");
			else context.put("student_status", studentStatus);
			
			Applicant applicant = student.getApplicant();
			if ( applicant == null ) {
				applicant = new Applicant();
				String refNo = new Application().genRefNo();
				applicant.setReferenceNo(refNo);
				if ( student.getBiodata() != null ) applicant.setBiodata(student.getBiodata());
				db.begin();
				student.setApplicant(applicant);
				db.commit();
			}
			
			if ( student.getBiodata() == null ) {
				if ( applicant.getBiodata() != null ) {
					db.begin();
					student.setBiodata(applicant.getBiodata());
					db.commit();
				}
			}
			context.put("applicant", student.getApplicant());
		}
		else {
			context.remove("student");
		}
		
		getExamTranscript(student);

		vm = path + "student_info.vm";
	}



	private void checkFinalResult(StudentStatus studentStatus) throws Exception {
		
		//System.out.println("check final result for " + studentStatus.getStudent().getMatricNo());
		
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
			
			sql = "select r from FinalResult r " +
			" where r.student.id = '" + studentStatus.getStudent().getId() + "' " +
			" and r.session.id = '" + studentStatus.getSession().getId() + "' ";
			result = (FinalResult) db.get(sql);
			
			if ( result != null ) {
				
				
				db.begin();
				result.setPeriod(studentStatus.getPeriod());
				Set<StudentSubject> studentSubjects = studentStatus.getStudentSubjects();
				for ( StudentSubject s : studentSubjects ) {
					FinalSubjectResult f = new FinalSubjectResult();
					f.setSubject(s.getSubject());
					result.addSubject(f);
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
			//System.out.println("Student Status is NULL for " + student.getMatricNo());
			throw new Exception("Student Status is NULL for " + student.getMatricNo());
		}
		//consistency check
		
		String sql = "select s from FinalResult r join r.subjects s" +
		" where r.student.id = '" + studentId + "' " +
		" and r.session.id = '" + sessionId + "' " +
		" and r.period.id = '" + periodId + "' " +
		" and s.subject.id = '" + subjectId + "'";
		FinalSubjectResult result = (FinalSubjectResult) db.get(sql);
		context.put("final_result", result);
		
		db.begin();
		if ( result != null ) {
			result.setResultStatus(null);
			context.put("mark", result.getTotalMarkDisplay());
		}
		db.commit();
		
		context.put("result_status", "");
		
		context.put("student_id", studentId);
		context.put("subject_result", result);
		
		saveAssessmentEntry2();
		
		vm = path + "status_saved.vm";
		
		
	}



	private void getStatusList() {
		//
		String studentId = request.getParameter("student_id");
		context.put("student_id", studentId);
		
		String periodId = request.getParameter("period_id");
		context.put("period_id", periodId);
		
		String studentStatusId = request.getParameter("student_status_id");
		context.put("student_status_id", studentStatusId);
		
		String cnt = request.getParameter("counter");
		String bgcolor = request.getParameter("bgcolor");
		
		context.put("cnt", cnt);
		context.put("bgcolor", bgcolor);
		
		List<SubjectResultStatus> statuses = db.list("select s from SubjectResultStatus s");
		context.put("statuses", statuses);
		
		vm = path + "list_statuses.vm";
		
	}

	
	private void done() {
		vm = path + "done.vm";
		
	}
	


	private Student saveResultStatus(String studentId, Subject subject, String sessionId, String periodId,
			SubjectResultStatus resultStatus) throws Exception {
		Student student = (Student) db.find(Student.class, studentId);
		StudentStatus studentStatus = student.getStatus(sessionId);
		if ( studentStatus != null ) {
			checkFinalResult(studentStatus);
		} else {
			//System.out.println("Student Status is NULL for " + student.getMatricNo());
			throw new Exception("Student Status is NULL for " + student.getMatricNo());
		}
		//consistency check
		
		String sql = "select s from FinalResult r join r.subjects s" +
		" where r.student.id = '" + studentId + "' " +
		" and r.session.id = '" + sessionId + "' " +
		" and r.period.id = '" + periodId + "' " +
		" and s.subject.id = '" + subject.getId() + "'";
		FinalSubjectResult result = (FinalSubjectResult) db.get(sql);
		context.put("final_result", result);
		
		db.begin();
		if ( result != null ) {
			if ( resultStatus != null ) {
				result.setResultStatus(resultStatus); //set result status
				
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
				
				//System.out.println(resultStatus.getName());
				//System.out.println(resultStatus.getExcludeGPA());
				
				if ( resultStatus.getExcludeGPA() ) {
					result.setExcludeGPA(1);
				}
				else {
					result.setExcludeGPA(0);
				}
			}
		}
		db.commit();
		
		context.put("student_id", studentId);
		context.put("subject_result", result);
		return student;
	}

	
	private void saveAssessmentEntry2() throws Exception {
		
		context.put("updated", true);
		
		String statusId = getParam("status_id");
		SubjectResultStatus resultStatus = null;
		boolean resetMark = false;
		if ( !"".equals(statusId)) {
			resultStatus = (SubjectResultStatus) db.find(SubjectResultStatus.class, statusId);
			resetMark = resultStatus.getResetMark();
			System.out.println("Reset Mark = " + resetMark);
		}
		
		
		
		String subjectId = request.getParameter("subject_id");
		Subject subject = null;
		subject = db.find(Subject.class, subjectId);	
		String studentStatusId = request.getParameter("student_status_id");
		StudentStatus studentStatus = (StudentStatus) db.find(StudentStatus.class, studentStatusId);
		
		//make a check on final result
		this.checkFinalResult(studentStatus);
		//---
		
		//GET COURSEWORK, and the ROUNDUP TOTAL
		String sql = "select c from Coursework c where c.subject.id = '" + subjectId + "' and c.session.id = '" + studentStatus.getSession().getId() + "' and c.centre.id = '" + studentStatus.getStudent().getLearningCenter().getId() + "'";
		Coursework coursework = (Coursework) db.get(sql);
		//boolean roundUpTotal = coursework != null ? coursework.getRoundUpTotal() : false;
		//context.put("roundUpTotal", roundUpTotal);
		
		//int roundType = 0; //0 - use default, 1 - round up, 2 - round down
		int roundType = coursework != null ? coursework.getRoundType() : 0;
		context.put("roundType", roundType);

		SubjectResult subjectResult = null;
		sql = "select su from ExamResult e JOIN e.sessions sr JOIN sr.subjects su " +
		"where sr.session.id = '" + studentStatus.getSession().getId() + "' " +
		"and su.subject.id = '" + subjectId + "' " +
		"and e.student.id = '" + studentStatus.getStudent().getId() + "'";
		List<SubjectResult> results = db.list(sql);
		if ( results.size() == 0 ) { //subjectResult not exist
			//System.out.println("subject result does not exist...");
			subjectResult = createSubjectResult(studentStatus, subject);
		}
		else {
			subjectResult = results.get(0);
		}
		
		//System.out.println("subjectResult " + subjectResult.getSubject().getCode());
		
		Set<AssessmentResult> asses = subjectResult.getAssessments();
		String[] assesIds = new String[asses.size()];
		context.put("mark_ids", assesIds);
		
		
		Hashtable<String, Double> marks = new Hashtable<String, Double>();
		context.put("marks", marks);
		double mark = 0.0d, totalMark = 0.0d;
		
		
		db.begin();
		int cnt = 0;
		if ( !resetMark ) {
			for ( AssessmentResult ass : asses ) {
				if ( ass.getCourseworkItem() != null ) {
					if ( ass.getCourseworkItem().getPercentage() > 0.0 ) {
						assesIds[cnt] = ass.getId();
						cnt++;
						try {
							mark = Double.parseDouble(request.getParameter(ass.getId()));
							System.out.println("mark = " + mark);
							marks.put(ass.getId(), mark);
							totalMark += mark;
						} catch ( Exception e ) {
							mark = 0.0d;
						}
						ass.setMarks(mark);
					}
				}
			}
		}
		
		//ROUNDUP TOTAL HAPPENED HERE
		
		//System.out.println("total mark = " + totalMark);
		//System.out.println(new Float(Math.round(totalMark)));
		//subjectResult.setTotalAssessment(roundUpTotal ? new Float(Math.round(totalMark)) : totalMark);

		double rounded = totalMark;
		
		if ( roundType == 1 ) {
			BigDecimal initialValue = new BigDecimal(totalMark);
			rounded = initialValue.setScale(0,BigDecimal.ROUND_HALF_UP).doubleValue();
		}
		else if ( roundType == 2 ) {
			BigDecimal initialValue = new BigDecimal(totalMark);
			rounded = initialValue.setScale(0,BigDecimal.ROUND_DOWN).doubleValue();
		}
		
		subjectResult.setTotalAssessment(rounded);
		
		//System.out.println("total = " + subjectResult.getTotalAssessment());
		
		db.commit();
		
		
		double finalMark = subjectResult.getPart1() + subjectResult.getPart2();
		double assessmentMark = subjectResult.getTotalAssessment();
		
		double subjectMark = finalMark + assessmentMark;
		
		//System.out.println("subject mark = " + subjectMark);
		
		//String studentId = subjectResult.getSession().getResult().getStudent().getId();
		context.put("student_id", studentStatus.getStudent().getId());
		//String sessionId = subjectResult.getSession().getSession().getId();
		//String periodId = subjectResult.getSession().getPeriod().getId();
		
		//set status here is status exist
		if ( resultStatus != null ) {
			//System.out.println("got result status = " + resultStatus.getName());
			saveResultStatus(studentStatus.getStudent().getId(), subject, studentStatus.getSession().getId(), studentStatus.getPeriod().getId(), resultStatus);
		}
		
		int hasResult = ResultEntryUtil.saveResult(db, studentStatus, subject, subjectMark);		
		
		
		sql = "select r from FinalResult r " +
				" where r.student.id = '" + studentStatus.getStudent().getId() + "' " +
				" and r.session.id = '" + studentStatus.getSession().getId() + "' " +
				" and r.period.id = '" + studentStatus.getPeriod().getId() + "' ";
		
		FinalResult fr = (FinalResult) db.get(sql);
		if ( fr == null) {
			//create final result
		} else {
			//System.out.println("FinalResult = " + fr);
			/*
			for ( FinalSubjectResult frs : fr.getSubjects() ) {
				System.out.println(frs.getSubject().getCode());
			}
			*/
		}
		
		
		sql = "select s from FinalResult r join r.subjects s" +
		" where r.student.id = '" + studentStatus.getStudent().getId() + "' " +
		" and r.session.id = '" + studentStatus.getSession().getId() + "' " +
		" and r.period.id = '" + studentStatus.getPeriod().getId() + "' " +
		" and s.subject.id = '" + subjectId + "'";
		FinalSubjectResult result = (FinalSubjectResult) db.get(sql);
				
		if ( result != null ) {
		
			context.put("final_result", result);
			if ( result.getResultStatus() != null ) {
				//context.put("result_status", result.getResultStatus().getCode());
				context.put("result_status", result.getResultStatus().getName());
			}
			else {
				context.remove("result_status");
			}
		}
		else {

			//System.out.println("final result is null...");
			context.remove("final_result");
			context.remove("result_status");
		}
		
		
		//calculate progress percentage
		String _markedResults = request.getParameter("marked_results");
		String _totalResults = request.getParameter("total_results");
		
		int markedResults = 0, totalResults = 0;
		try {
			markedResults = Integer.parseInt(_markedResults);
			totalResults = Integer.parseInt(_totalResults);
			
			if ( hasResult == 0 ) markedResults++;
			
			float percentProgress = ((float) markedResults/(float) totalResults)*100;
			
			
			context.put("marked_results", markedResults);
			context.put("total_results", totalResults);
			context.put("percent_progress", new DecimalFormat("#.00").format(percentProgress));
			
		} catch ( Exception e ) {
			
			e.printStackTrace();
			
		}
		
		vm = path + "marks_saved.vm";
		
	}
	
	
	private void saveAssessmentEntry() throws Exception {
		
		context.put("updated", true);
		
		String subjectId = request.getParameter("subject_id");
		Subject subject = null;
		subject = db.find(Subject.class, subjectId);	
		String studentStatusId = request.getParameter("student_status_id");
		StudentStatus studentStatus = (StudentStatus) db.find(StudentStatus.class, studentStatusId);

		SubjectResult subjectResult = null;
		String sql = "select su from ExamResult e JOIN e.sessions sr JOIN sr.subjects su " +
		"where sr.session.id = '" + studentStatus.getSession().getId() + "' " +
		"and su.subject.id = '" + subjectId + "' " +
		"and e.student.id = '" + studentStatus.getStudent().getId() + "'";
		List<SubjectResult> results = db.list(sql);
		if ( results.size() == 0 ) { //subjectResult not exist
			subjectResult = createSubjectResult(studentStatus, subject);
		}
		else {
			subjectResult = results.get(0);
		}
		
		Set<AssessmentResult> asses = subjectResult.getAssessments();
		String[] assesIds = new String[asses.size()];
		context.put("mark_ids", assesIds);
		
		
		Hashtable<String, Double> marks = new Hashtable<String, Double>();
		context.put("marks", marks);
		double mark = 0.0d, totalMark = 0.0d;
		db.begin();
		int cnt = 0;
		for ( AssessmentResult ass : asses ) {
			
			if ( ass.getCourseworkItem().getPercentage() > 0.0 ) {
				assesIds[cnt] = ass.getId();
				cnt++;
				try {
					mark = Double.parseDouble(request.getParameter(ass.getId()));
					marks.put(ass.getId(), mark);
					totalMark += mark;
				} catch ( Exception e ) {
					mark = 0.0d;
				}
				ass.setMarks(mark);
			}
		}
		
		//System.out.println("total mark = " + totalMark);
		//System.out.println(new Float(Math.round(totalMark)));
		
		subjectResult.setTotalAssessment(new Float(Math.round(totalMark)));
		db.commit();
		
		
		double finalMark = subjectResult.getPart1() + subjectResult.getPart2();
		double assessmentMark = subjectResult.getTotalAssessment();
		double subjectMark = finalMark + assessmentMark;
		
		String studentId = subjectResult.getSession().getResult().getStudent().getId();
		context.put("student_id", studentId);
		String sessionId = subjectResult.getSession().getSession().getId();
		String periodId = subjectResult.getSession().getPeriod().getId();
		
		int hasResult = ResultEntryUtil.saveResult(db, studentStatus, subject, subjectMark);
		
		sql = "select s from FinalResult r join r.subjects s" +
		" where r.student.id = '" + studentId + "' " +
		" and r.session.id = '" + sessionId + "' " +
		" and r.period.id = '" + periodId + "' " +
		" and s.subject.id = '" + subjectId + "'";
		
		//System.out.println(sql);

		FinalSubjectResult result = (FinalSubjectResult) db.get(sql);
		
		//System.out.println("final result = " + result);
		
		if ( result != null ) {
		
			context.put("final_result", result);
		}
		else {
			//System.out.println("final result is null...");
			context.remove("final_result");
		}
		
		
		//calculate progress percentage
		String _markedResults = request.getParameter("marked_results");
		String _totalResults = request.getParameter("total_results");
		
		int markedResults = 0, totalResults = 0;
		try {
			markedResults = Integer.parseInt(_markedResults);
			totalResults = Integer.parseInt(_totalResults);
			
			if ( hasResult == 0 ) markedResults++;
			
			float percentProgress = ((float) markedResults/(float) totalResults)*100;
			
			
			context.put("marked_results", markedResults);
			context.put("total_results", totalResults);
			context.put("percent_progress", new DecimalFormat("#.00").format(percentProgress));
			
		} catch ( Exception e ) {
			
			e.printStackTrace();
			
		}
		
		vm = path + "marks_saved.vm";
		
	}


	private void getAssessment() throws Exception {
		vm = path + "assessment.vm";
		context.put("updated", false);
		String subjectId = request.getParameter("subject_id");
		String sessionId = request.getParameter("session_id");
		String periodId = request.getParameter("period_id");
		String studentId = request.getParameter("student_id");
		String centreId = request.getParameter("centre_id");
		
		context.put("student_id", studentId);
		context.put("subject_id", subjectId);
		context.put("session_id", sessionId);
		context.put("period_id", periodId);
		//System.out.println(subjectId + ", " + sessionId + ", " + studentId + ", " + periodId);
		
		Subject subject = db.find(Subject.class, subjectId);	
		
		List<AssessmentResult> ars = assessmentResults(subjectId, sessionId, periodId, studentId, centreId);
		context.put("assessments", ars);
		
	}

	private void listStudents() throws Exception {
		vm = path + "list_students.vm";
		context.remove("section");
		
		String subjectId = getParam("subject_id");
		String sessionId = getParam("session_id");
		String intakeId = getParam("intake_id");
		String centreId = getParam("centre_id");
		String programId = getParam("program_id");
		context.put("program_id", programId);
		
		Program program = db.find(Program.class, programId);
		context.put("program", program);
		
		Subject subject = null;
		SubjectSection section = null;
		subject = db.find(Subject.class, subjectId);	
		
		//check marking grade is available for this subject
		MarkingGrade markingGrade = null;
		if ( subject != null ) markingGrade = subject.getMarkingGrade();
		if ( markingGrade == null ) {
			context.remove("markingGrade");
			System.out.println("  ===============================");
			System.out.println("  WARNING: Marking Grade is NULL");
			System.out.println("  ===============================");
			System.out.println("  Class: AssessmentResultModule.listStudents");
		}
		else {
			context.put("markingGrade", markingGrade);
		}

		context.put("subject", subject);
		context.put("subject_id", subject.getId());

		String sectionId = section != null ? section.getId() : "";
		context.put("section_id", sectionId);
		
		calculatePercentage(programId, sessionId, subjectId, intakeId, centreId, sectionId);
		
		Session session = db.find(Session.class, sessionId);
		context.put("session", session);
		
		LearningCentre centre = db.find(LearningCentre.class, centreId);
		context.put("centre_id", centreId);
		context.put("centre", centre);
		
		/*
		List<CourseworkItem> items = getOrCreateCourseworkItem(subject, session, centre);
		context.put("items", items);
		*/

		long totalCount = getNumberOfStudents(subject, programId, sessionId, intakeId, centreId, sectionId);
		
		listStudents(programId, subject, session, intakeId, centre, sectionId, 1, totalCount);
	}
	
	private void updateCoursework() throws Exception {
		vm = path + "list_students.vm";
		context.remove("section");

		
		String subjectId = getParam("subject_id");
		String sessionId = getParam("session_id");
		String intakeId = getParam("intake_id");
		String centreId = getParam("centre_id");
		String programId = getParam("program_id");

		
		LearningCentre centre = db.find(LearningCentre.class, centreId);
		context.put("centre", centre);
		Program program = db.find(Program.class, programId);
		context.put("program", program);
		
		String sql = "select c from Coursework c where c.subject.id = '" + subjectId + "' and c.session.id = '" + sessionId + "' and c.centre.id = '" + centreId + "'";
		Coursework coursework = (Coursework) db.get(sql);
		
		if ( coursework == null ) {
			//LearningCentre centre = db.find(LearningCentre.class, centreId);
			Session session = db.find(Session.class, sessionId);
			Subject subject = db.find(Subject.class, subjectId);
			
			db.begin();
			coursework = new Coursework();
			coursework.setCentre(centre);
			coursework.setSession(session);
			coursework.setSubject(subject);
			db.persist(coursework);
			db.commit();
		}
		
		//save coursework
		for ( int i = 1; i < 11; i++ ) {
			String id = getParam("cw_id_" + i);
			String code = getParam("cw_code_" + i);
			String name = getParam("cw_name_" + i);
			String percentage = getParam("cw_percentage_" + i);

			if ( !"".equals(id)) {
				if ( !"".equals(code) && !"".equals(name) && !"".equals(percentage)) {
					
					double pct = Double.parseDouble(percentage);
					CourseworkItem item = db.find(CourseworkItem.class, id);
					if ( pct > 0 ) {
						db.begin();
						item.setCode(code);
						item.setName(name);
						item.setPercentage(pct);
						db.commit();
					} else {
						db.begin();
						coursework.getItems().remove(item);
						db.remove(item);
						db.commit();
					}
				}
			} else {
				if ( !"".equals(code) && !"".equals(name) && !"".equals(percentage)) {
					
					CourseworkItem item = new CourseworkItem();
					double pct = Double.parseDouble(percentage);
					if ( pct > 0 ) {
					db.begin();
						item.setCode(code);
						item.setName(name);
						item.setPercentage(Double.parseDouble(percentage));
						coursework.addItem(item);
						db.commit();
					}
				}
			}
		}
		
		//ROUNDUP TOTAL?
		/*
		boolean roundUpTotal = !"".equals(getParam("roundUpTotal")) ? true : false;
		db.begin();
		coursework.setRoundUpTotal(roundUpTotal);
		db.commit();
		*/
		String roundType = getParam("roundType");
		
		db.begin();
		if ( "".equals(roundType)) {
			coursework.setRoundType(0);
		}
		else if ( "1".equals(roundType)) {
			coursework.setRoundType(1);
		}
		else if ( "2".equals(roundType)) {
			coursework.setRoundType(2);
		}
		else {
			coursework.setRoundType(0);
		}
		db.commit();
		//list students

		
		Subject subject = null;
		SubjectSection section = null;
		subject = db.find(Subject.class, subjectId);	
		context.put("subject", subject);
		context.put("subject_id", subject.getId());

		String sectionId = section != null ? section.getId() : "";
		context.put("section_id", sectionId);
		

		
		calculatePercentage(programId, sessionId, subjectId, intakeId, centreId, sectionId);
		
		Session session = db.find(Session.class, sessionId);
		context.put("session", session);
		//List<CourseworkItem> items = getCourseworkItem(subjectId, sessionId);
		List<CourseworkItem> items = coursework.getCourseworkItems();
		context.put("items", items);

		getNumberOfStudents(subject, programId, sessionId, intakeId, centreId, sectionId);
		
		int pageNum = 0;
		try {
			pageNum = Integer.parseInt(request.getParameter("page_num"));
		} catch ( Exception e ) {}
		
		long totalCount = 0l;
		try {
			totalCount = Long.parseLong(getParam("total_count"));
		} catch ( Exception e ) {}
		
		listStudents(programId, subject, session, intakeId, centre, sectionId, pageNum, totalCount);
	}	
	
	private void nextPage() throws Exception {
		vm = path + "list_students_page.vm";
		
		String subjectId = request.getParameter("subject_id");
		String sessionId = request.getParameter("session_id");
		String intakeId = request.getParameter("intake_id");
		String centreId = request.getParameter("centre_id");
		String programId = request.getParameter("program_id");
		
		Session session = db.find(Session.class, sessionId);
		LearningCentre centre = db.find(LearningCentre.class, centreId);
		
		Subject subject = null;
		SubjectSection section = null;
		subject = db.find(Subject.class, subjectId);	
		context.put("subject", subject);
		String sectionId = section != null ? section.getId() : "";
		
		int pageNum = 0;
		try {
			pageNum = Integer.parseInt(request.getParameter("page_num"));
			pageNum++;
		} catch ( Exception e ) {}
		
		long totalCount = 0l;
		try {
			totalCount = Long.parseLong(getParam("total_count"));
		} catch ( Exception e ) {}
		
		listStudents(programId, subject, session, intakeId, centre, sectionId, pageNum, totalCount);
		
	}
	
	
	private void prevPage() throws Exception {
		vm = path + "list_students_page.vm";
		
		String subjectId = request.getParameter("subject_id");
		String sessionId = request.getParameter("session_id");
		String intakeId = request.getParameter("intake_id");
		String centreId = request.getParameter("centre_id");
		String programId = request.getParameter("program_id");
		
		Session session = db.find(Session.class, sessionId);
		LearningCentre centre = db.find(LearningCentre.class, centreId);
		
		Subject subject = null;
		SubjectSection section = null;
		subject = db.find(Subject.class, subjectId);	
		context.put("subject", subject);
		String sectionId = section != null ? section.getId() : "";
		
		int pageNum = 0;
		try {
			pageNum = Integer.parseInt(request.getParameter("page_num"));
			pageNum--;
		} catch ( Exception e ) {}
		
		long totalCount = 0l;
		try {
			totalCount = Long.parseLong(getParam("total_count"));
		} catch ( Exception e ) {}
		
		listStudents(programId, subject, session, intakeId, centre, sectionId, pageNum, totalCount);
		
	}

	private void gotoPage() throws Exception {
		vm = path + "list_students_page.vm";
		
		String subjectId = request.getParameter("subject_id");
		String sessionId = request.getParameter("session_id");
		String intakeId = request.getParameter("intake_id");
		String centreId = request.getParameter("centre_id");
		String programId = request.getParameter("program_id");
		
		Session session = db.find(Session.class, sessionId);
		LearningCentre centre = db.find(LearningCentre.class, centreId);
		
		Subject subject = null;
		SubjectSection section = null;
		subject = db.find(Subject.class, subjectId);	
		context.put("subject", subject);
		String sectionId = section != null ? section.getId() : "";
		
		int pageNum = 0;
		try {
			pageNum = Integer.parseInt(request.getParameter("page"));
		} catch ( Exception e ) {}
		
		long totalCount = 0l;
		try {
			totalCount = Long.parseLong(getParam("total_count"));
		} catch ( Exception e ) {}
		
		listStudents(programId, subject, session, intakeId, centre, sectionId, pageNum, totalCount);		
	}

	private long getNumberOfStudents(Subject subject, String programId, String sessionId, String intakeId, String centreId, String sectionId) throws Exception {
		
		int pageSize = 10;
		try {
			pageSize = Integer.parseInt(request.getParameter("page_size"));
		} catch ( Exception e ) {}
		
		String sql = "select count(subject) from StudentStatus s JOIN s.studentSubjects subject " +
		"where (s.student.fakeStudent is null OR s.student.fakeStudent = '') "+ 
		"and s.type.inActive = 0 " +
		"and s.session.id = '" + sessionId + "' " +
		"and s.student.program.id = '" + programId + "' " +
		"and s.student.learningCenter.id = '" + centreId + "' ";
		if ( !"".equals(intakeId)) sql += " and s.student.intake.id = '" + intakeId + "' ";
		if ( !"".equals(sectionId)) sql += " and subject.section.id = '" + sectionId + "' ";
		sql += " and subject.subject.id = '" + subject.getId() + "' ";
		
		long size = (Long) db.get(sql);
		context.put("total_count", size);
		
		long numPages = size / pageSize;
		long more = size % pageSize;
		if ( more > 0 ) {
			numPages = numPages + 1;
		}
		context.put("total_page", (int) numPages);
		
		return size;
	}
	
	private void listStudents(String programId, Subject subject, Session session, String intakeId, LearningCentre centre, String sectionId, int pageNum, long totalCount) throws Exception {
		
		context.put("session_id", session.getId());
		context.put("intake_id", intakeId);
		context.put("subject_id", subject.getId());
		context.put("section_id", sectionId);
		context.put("program_id", programId);
		context.put("centre_id", centre.getId());
		
		String sessionId = session.getId();
		String centreId = centre.getId();
		
		List<CourseworkItem> items = getOrCreateCourseworkItem(subject, session, centre);
		context.put("items", items);
		
		//GET COURSEWORK, and the ROUNDUP TOTAL
		String sql = "select c from Coursework c where c.subject.id = '" + subject.getId() + "' and c.session.id = '" + sessionId + "' and c.centre.id = '" + centreId + "'";
		Coursework coursework = (Coursework) db.get(sql);
		context.put("roundType", coursework != null ? coursework.getRoundType() : 0);
		
		int pageSize = 10;
		try {
			pageSize = Integer.parseInt(request.getParameter("page_size"));
		} catch ( Exception e ) {}
		context.put("page_size", pageSize);
		
		
		long numPages = totalCount / pageSize;
		long more = totalCount % pageSize;
		if ( more > 0 ) {
			numPages = numPages + 1;
		}
		context.put("total_page", (int) numPages);
		
		
		int start = 0;
		if ( pageNum > 1 ) {
			start = (pageNum - 1) * pageSize;
		}
		
		context.put("page_num", pageNum);
		
		
		sql = "select subject from StudentStatus s JOIN s.studentSubjects subject " +
				"where (s.student.fakeStudent is null OR s.student.fakeStudent = '') "+ 
				"and s.type.inActive = 0 " + //do not includes inactive students
				"and s.session.id = '" + sessionId + "' " + 
				"and s.student.learningCenter.id = '" + centreId + "' " +
				"and s.student.program.id = '" + programId + "' ";
		if ( !"".equals(intakeId)) sql += " and s.student.intake.id = '" + intakeId + "' ";
		if ( !"".equals(sectionId)) sql += " and subject.section.id = '" + sectionId + "' ";
		sql += " and subject.subject.id = '" + subject.getId() + "' ";
		sql += "order by s.student.biodata.name";
		
		//System.out.println(sql);
		//System.out.println("list students for subject = " + subject.getCode());
		
		List<StudentSubject> list = db.list(sql, start, pageSize);
				
		Map<String, List<AssessmentResult>> resultMapper = new HashMap<String, List<AssessmentResult>>();
		context.put("results", resultMapper);
		Map<String, String> statusMapper = new HashMap<String, String>();
		context.put("status", statusMapper);
		Map<String, FinalSubjectResult> finalResultMapper = new HashMap<String, FinalSubjectResult>();
		context.put("final_result", finalResultMapper);
		
		for ( StudentSubject s : list ) {
			Hashtable h = getAssessmentResults(s.getStudentStatus(), subject, centre);
			List<AssessmentResult> results = (List<AssessmentResult>) h.get("assessment_results");
			String status = (String) h.get("result_status");
			FinalSubjectResult finalResult = (FinalSubjectResult) h.get("final_result");
			resultMapper.put(s.getStudentStatus().getId(), results);
			statusMapper.put(s.getStudentStatus().getId(), status);
			finalResultMapper.put(s.getStudentStatus().getId(), finalResult);
			
			//if ( finalResult != null ) System.out.println("total mark = " + finalResult.getTotalMark());
			
		}
		
		//System.out.println("size = " + list.size());
		context.put("student_list", list);
		

		
	}
	/*
	private void listStudentsNoPaging(Subject subject, String sessionId, String intakeId, String centreId, String sectionId) throws Exception {
		
		context.put("session_id", sessionId);
		context.put("intake_id", intakeId);
		context.put("subject_id", subject.getId());
		context.put("section_id", sectionId);
		
		String sql = "select subject from StudentStatus s JOIN s.studentSubjects subject " +
				"where (s.student.fakeStudent is null OR s.student.fakeStudent = '') "+ 
				"and s.type.inActive = 0 " + //do not includes inactive students
				"and s.session.id = '" + sessionId + "' " +
				"and s.student.learningCenter.id = '" + centreId + "' ";
		if ( !"".equals(intakeId)) sql += " and s.student.intake.id = '" + intakeId + "' ";
		if ( !"".equals(sectionId)) sql += " and subject.section.id = '" + sectionId + "' ";
		sql += " and subject.subject.id = '" + subject.getId() + "' " +
				"order by s.student.matricNo";
		
		//System.out.println(sql);
		
		List<StudentSubject> list = db.list(sql);
		
		Map<String, List<AssessmentResult>> resultMapper = new HashMap<String, List<AssessmentResult>>();
		context.put("results", resultMapper);
		Map<String, String> statusMapper = new HashMap<String, String>();
		context.put("status", statusMapper);
		Map<String, FinalSubjectResult> finalResultMapper = new HashMap<String, FinalSubjectResult>();
		context.put("final_result", finalResultMapper);
		
		LearningCentre centre = db.find(LearningCentre.class, centreId);
		for ( StudentSubject s : list ) {
			Hashtable h = getAssessmentResults(s.getStudentStatus(), subject, centre);
			List<AssessmentResult> results = (List<AssessmentResult>) h.get("assessment_results");
			String status = (String) h.get("result_status");
			FinalSubjectResult finalResult = (FinalSubjectResult) h.get("final_result");
			resultMapper.put(s.getStudentStatus().getId(), results);
			statusMapper.put(s.getStudentStatus().getId(), status);
			finalResultMapper.put(s.getStudentStatus().getId(), finalResult);
			
		}
		
		context.put("student_list", list);
	}
	*/

	

	void params3() {
		if ( "".equals(teacherId)) {
			context.put("subjects", db.list("select x from Subject x ORDER BY x.code"));
		} else {
			context.put("subjects", db.list("select s from educate.sis.struct.entity.Teacher t join t.teacherSubjects s where t.userId = '" + teacherId + "'"));
		}
		context.put("sessions", db.list("select x from Session x ORDER BY x.pathNo, x.startDate"));

	}
	
	private void start() throws Exception {
		vm = path + "select.vm";
		
		
		Session currentSession = new StudentStatusUtil().getCurrentSession(0);
		context.put("current_session", currentSession);
		context.put("programs",db.list("SELECT a from Program a order by a.code"));
		
		//System.out.println("teacher id = " + teacherId);
		
		if ( "".equals(teacherId)) {
			context.put("centres", db.list("select c from LearningCentre c order by c.code"));
		}
		else {
			context.put("centres", db.list("select c from educate.sis.struct.entity.Teacher t join t.centres c where t.userId = '" + teacherId + "' order by c.code"));
			//list of subjects by this teacher
			String sql = "select ts.subject from TeacherSubject ts where ts.teacher.userId = '" + teacherId + "'";
			context.put("subjects", db.list(sql));
		}
		
	}



	private void getMarksEntryPeriod() throws Exception {
		//teachers mark entry period
		MarkEntryPeriod markEntryPeriod = db.find(MarkEntryPeriod.class, "current");
		if ( markEntryPeriod == null ) {
			Date date = new Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.add(Calendar.MONTH, -1);
			db.begin();
			markEntryPeriod = new MarkEntryPeriod("current");
			markEntryPeriod.setStartDate(cal.getTime());
			markEntryPeriod.setEndDate(cal.getTime());
			db.persist(markEntryPeriod);
			db.commit();
		}
		context.put("markEntryPeriod", markEntryPeriod);
		boolean allowMarkEntry = false;
		Date today = new Date();
		if ( new SimpleDateFormat("dd-MM-yyyy").format(today).equals(new SimpleDateFormat("dd-MM-yyyy").format(markEntryPeriod.getStartDate())) ) {
			allowMarkEntry = true;
		}
		if ( !allowMarkEntry ) {
			if ( today.after(markEntryPeriod.getStartDate()) && today.before(markEntryPeriod.getEndDate())) {
				allowMarkEntry = true;
			}
		}
		if ( !allowMarkEntry ) {
			if ( new SimpleDateFormat("dd-MM-yyyy").format(today).equals(new SimpleDateFormat("dd-MM-yyyy").format(markEntryPeriod.getEndDate())) ) {
				allowMarkEntry = true;
			}
		}
		if ( allowMarkEntry ) context.put("allowMarkEntry", true);
		else context.remove("allowMarkEntry");
		
	}

	
	/*
	 * CREATING ASSESSMENT RESULTS BASED ON COURSEWORK ITEMS ARE HERE
	 */
	private List<AssessmentResult> assessmentResults(String subjectId, String sessionId, String periodId, String studentId, String centreId) throws Exception {
		
		List<AssessmentResult> ars = new ArrayList<AssessmentResult>();
		
		String sql = "";
		
		Student student = (Student) db.find(Student.class, studentId);
		Session session = (Session) db.find(Session.class, sessionId);
		Period period = (Period) db.find(Period.class, periodId);
		Subject subject = (Subject) db.find(Subject.class, subjectId);
		
		SubjectResult subjectResult = null;
		
		//get list of subjects
		sql = "select su from ExamResult e JOIN e.sessions sr JOIN sr.subjects su " +
				"where sr.session.id = '" + sessionId + "' " +
				"and su.subject.id = '" + subjectId + "' " +
				"and e.student.id = '" + studentId + "'";
		List<SubjectResult> results = db.list(sql);
		if ( results.size() == 0 ) { //subjectResult not exist
			//System.out.println("subjectResult not exist");
			subjectResult = createSubjectResult(student, session, period, subject);
		}
		else {
			subjectResult = results.get(0);
			//System.out.println("SubjectResult EXITS..." + subjectResult.getSession().getResult().getStudent().getBiodata().getName());
		}
		
		//get the coursework item for this subject
		subject = subjectResult.getSubject();  //
		LearningCentre centre = db.find(LearningCentre.class, centreId);
		List<CourseworkItem> cws = getOrCreateCourseworkItem(subject, session, centre);
			
		if ( cws.size() > 0 ){
			
			//look for assessmentResult
			Set<AssessmentResult> assessments = subjectResult.getAssessments();
			if ( assessments.size() == 0 ) {
				//System.out.println("assessment result not created.");
				db.begin();
				for ( CourseworkItem cw : cws ) {
					//System.out.println(cw.getName());
					AssessmentResult ar = new AssessmentResult();
					ar.setCourseworkItem(cw);
					ar.setSubjectResult(subjectResult);
					ar.setMarks(0.0d);
					subjectResult.addAssessment(ar);
				}
				db.commit();	
				assessments = subjectResult.getAssessments();
			}
			//
			{
				boolean f = false;
				for ( CourseworkItem cw : cws ) {
					f = false;
					for ( AssessmentResult ar : assessments ) {
						if ( ar.getCourseworkItem() != null && ar.getCourseworkItem().getId().equals(cw.getId())) {
							f = true;
							break;
						}
					}
					if ( !f ) {
						db.begin();
						//System.out.println(cw.getName());
						AssessmentResult ar = new AssessmentResult();
						ar.setCourseworkItem(cw);
						ar.setSubjectResult(subjectResult);
						ar.setMarks(0.0d);
						subjectResult.addAssessment(ar);
						db.commit();	
					}
				}
				
				//show all assessments
				for ( AssessmentResult ar : assessments ) {
					//System.out.println(ar.getSubjectResult().getSession().getResult().getStudent().getMatricNo() + ": " +  ar.getCourseworkItem().getName() + ": " + ar.getMarks());
					ars.add(ar);
				}
				Collections.sort(ars, new AssessmentSequenceComparator());
			}
			
		}
		return ars;
	}
	
	//private List<AssessmentResult> getAssessmentResults(StudentStatus studentStatus, Subject subject) throws Exception {
	private Hashtable getAssessmentResults(StudentStatus studentStatus, Subject subject, LearningCentre centre) throws Exception {
		List<AssessmentResult> ars = new ArrayList<AssessmentResult>();
		String sql = "";
		SubjectResult subjectResult = null;
		//get list of subjects
		sql = "select su from ExamResult e JOIN e.sessions sr JOIN sr.subjects su " +
				"where sr.session.id = '" + studentStatus.getSession().getId() + "' " +
				"and su.subject.id = '" + subject.getId() + "' " +
				"and e.student.id = '" + studentStatus.getStudent().getId() + "' " +
				"and e.student.learningCenter.id = '" + centre.getId() + "'";
		
		List<SubjectResult> results = db.list(sql);
		
		if ( results.size() == 0 ) {
			//need to create exam result for this student
			subjectResult = createSubjectResult(studentStatus.getStudent(), studentStatus.getSession(), studentStatus.getPeriod(), subject);
		}
		else {
			subjectResult = results.get(0);
		}
		
		//Assessment Result ialah Coursework Item
		//Setiap assessment result mengandungi maklumat coursework item
		
		//

		//
		
		if ( subjectResult != null ) {
			Session session = studentStatus.getSession();
			List<CourseworkItem> courseworkItems = getCourseworkItem(subject.getId(), session.getId(), centre.getId());

			Set<AssessmentResult> assessments = subjectResult.getAssessments();
			createAssessmentResults(subjectResult, courseworkItems, assessments);
			
			assessments = subjectResult.getAssessments();
			//show all assessments
			//only add item defined
			for ( CourseworkItem i : courseworkItems ) {
				for ( AssessmentResult ar : assessments ) {
					if ( ar.getCourseworkItem() != null ) {
						if ( i.getId().equals(ar.getCourseworkItem().getId())) {
							ars.add(ar);
							break;
						}
					}
				}
			}			
			Collections.sort(ars, new AssessmentSequenceComparator());
		}
		
		
		sql = "select s from FinalResult r join r.subjects s" +
		" where r.student.id = '" + studentStatus.getStudent().getId() + "' " +
		" and r.session.id = '" + studentStatus.getSession().getId() + "' " +
		" and r.period.id = '" + studentStatus.getPeriod().getId() + "' " +
		" and s.subject.id = '" + subject.getId() + "'";
		FinalSubjectResult result = (FinalSubjectResult) db.get(sql);
		
		Hashtable h = new Hashtable();
		h.put("assessment_results", ars);
		if ( result != null && result.getResultStatus() != null ) {
			//h.put("result_status", result.getResultStatus().getCode());
			h.put("result_status", result.getResultStatus().getName());
		}
		else h.put("result_status", "");
		
		if ( result != null ) h.put("final_result", result);

		return h;

	}

	private void createAssessmentResults(SubjectResult subjectResult, List<CourseworkItem> courseworkItems, Set<AssessmentResult> assessments) throws Exception {
		if ( courseworkItems != null && courseworkItems.size() > 0 ){
			//look for assessmentResult
			
			{
				boolean f = false;
				for ( CourseworkItem cw : courseworkItems ) {
					f = false;
					for ( AssessmentResult ar : assessments ) {
						if ( ar.getCourseworkItem() != null && ar.getCourseworkItem().getId().equals(cw.getId())) {
							f = true;
							break;
						}
					}
					if ( !f ) {
						db.begin();
						AssessmentResult ar = new AssessmentResult();
						ar.setCourseworkItem(cw);
						ar.setSubjectResult(subjectResult);
						ar.setMarks(0.0d);
						subjectResult.addAssessment(ar);
						db.persist(ar);
						db.commit();	
					}
				}

			}
		}
	}
	

	private List<CourseworkItem> getOrCreateCourseworkItem(Subject subject, Session session, LearningCentre centre) throws Exception {
		
		System.out.println("getCourseworkItem - subjectId = " + subject.getId() + ", session = " + session.getId());
		
		String sql;
		
		Coursework subjectCoursework = null;
		sql = "select c from Coursework c where c.subject.id = '" + subject.getId() + "' and c.session.id = '" + session.getId() + "' and c.centre.id = '" + centre.getId() + "'";
		List<Coursework> courseworks = db.list(sql);
		
		Set<CourseworkItem> cws = null;
		if ( courseworks.size() == 0 ) {

			cws = new HashSet<CourseworkItem>();
			db.begin();
			subjectCoursework = new Coursework();
			subjectCoursework.setSubject(subject);
			subjectCoursework.setSession(session);
			subjectCoursework.setItems(cws);
			db.persist(subjectCoursework);
			db.commit();
			
		}
		else {
			
			subjectCoursework = courseworks.get(0);
			cws = subjectCoursework.getItems();
			if ( cws.size() == 0 ) {
				//System.out.println("2: Coursework Item was not setup!");
			}
		}
		
		
		if ( cws.size() == 0 ) {
			
			//create coursework items
			if ( subject.getCourseworkScheme() != null ) {
				List<CourseworkItem> schemeCourseworkItems = subject.getCourseworkScheme().getCourseworkItems();
				for ( CourseworkItem item : schemeCourseworkItems ) {
					//db.begin();
					//subjectCoursework.addItem(item);
					//db.commit();
					
					//instead of use existing, create and copy from original
					db.begin();
					CourseworkItem cwi = new CourseworkItem();
					cwi.setCode(item.getCode());
					cwi.setName(item.getName());
					cwi.setPercentage(item.getPercentage());
					cwi.setSequence(item.getSequence());
					//cwi.setCourseworkScheme(item.getCourseworkScheme());
					subjectCoursework.addItem(cwi);
					db.commit();
					
				}
			}
			
		}

		return subjectCoursework.getCourseworkItems();
	}

	private List<CourseworkItem> getCourseworkItem(String subjectId, String sessionId, String centreId) throws Exception {
		String sql = "select c from Coursework c where c.subject.id = '" + subjectId + "' and c.session.id = '" + sessionId + "' and c.centre.id = '" + centreId + "'";
		Coursework coursework = (Coursework) db.get(sql);
		if ( coursework != null ) return coursework.getCourseworkItems();
		else return new ArrayList<CourseworkItem>();
	}

	private SubjectResult createSubjectResult(StudentStatus studentStatus, Subject subject) throws Exception {
		
		Student student = studentStatus.getStudent();
		Session session = studentStatus.getSession();
		Period period = studentStatus.getPeriod();
		
		return createSubjectResult(student, session, period, subject);
	}

	private SubjectResult createSubjectResult(Student student, Session session, Period period, Subject subject) throws Exception {
		String sql;
		SubjectResult subjectResult;
		SessionResult sessionResult;
		//check for sessionResult
		sql = "select sr from ExamResult e JOIN e.sessions sr " +
		"where sr.session.id = '" + session.getId() + "' " +
		"and e.student.id = '" + student.getId() + "'";
		List<SessionResult> sessions = db.list(sql);
		if ( sessions.size() == 0 ) { //sessionResult not exist
			//System.out.println("sessionResult not exist");
			sessionResult = createSessionResult(student, session, period);
		}
		else {
			sessionResult = sessions.get(0);
		}
		
		
		db.begin();
		subjectResult = new SubjectResult();
		subjectResult.setSession(sessionResult);
		
		//System.out.println("subject = " + subject.getName());
		
		subjectResult.setSubject(subject);
		sessionResult.addSubject(subjectResult);
		db.commit();
		
		return subjectResult;
	}
	

	private SessionResult createSessionResult(Student student, Session session, Period period) throws Exception {
		String sql;
		ExamResult examResult;
		SessionResult sessionResult;
		//check for examResult
		sql = "select e from ExamResult e " +
		"where e.student.id = '" + student.getId() + "'";
		List<ExamResult> exams = db.list(sql);
		if ( exams.size() == 0 ) { //examResult not exist
			//System.out.println("examResult not exist");
			db.begin();
			examResult = new ExamResult();
			examResult.setStudent(student);
			db.persist(examResult);
			db.commit();
		}
		else {
			examResult = exams.get(0); //there is only one examResult per student
			//System.out.println("ExamResult EXITS.. " + examResult.getStudent().getBiodata().getName());
		}
		
		db.begin();
		sessionResult = new SessionResult();
		sessionResult.setResult(examResult);
		sessionResult.setSession(session);
		sessionResult.setPeriod(period);
		examResult.addSession(sessionResult);
		db.commit();
		
		return sessionResult;
	}
	
	class AssessmentSequenceComparator extends educate.util.MyComparator {

		public int compare(Object o1, Object o2) {
			AssessmentResult r1 = (AssessmentResult) o1;
			AssessmentResult r2 = (AssessmentResult) o2;
			if ( r1.getCourseworkItem() != null )
				return r1.getCourseworkItem().getSequence() > r2.getCourseworkItem().getSequence() ? 1 : -1;
			return 0;
		}
		
	}
	
	private void calculatePercentage(String programId, String sessionId, String subjectId, String intakeId, String centreId, String sectionId) {
		
		String sql = "select subject from StudentStatus s JOIN s.studentSubjects subject " +
		"where (s.student.fakeStudent is null OR s.student.fakeStudent = '') "+ 
		"and s.type.inActive = 0 " + //do not includes inactive students
		"and s.session.id = '" + sessionId + "' " +
		"and s.student.learningCenter.id = '" + centreId + "' " +
		"and s.student.program.id = '" + programId + "'";
		if ( !"".equals(intakeId)) sql += " and s.student.intake.id = '" + intakeId + "' ";
		if ( !"".equals(sectionId)) sql += " and subject.section.id = '" + sectionId + "' ";
		sql += " and subject.subject.id = '" + subjectId + "' ";
		
		List<StudentSubject> list = db.list(sql);
		
		int total = list.size();
		int marked = 0;
		
		for ( StudentSubject studentSubject : list ) {
		
			sql = "select r from FinalResult r " +
			" where r.student.id = '" + studentSubject.getStudentStatus().getStudent().getId() + "' " +
			" and r.session.id = '" + studentSubject.getStudentStatus().getSession().getId() + "' " +
			" and r.period.id = '" + studentSubject.getStudentStatus().getPeriod().getId() + "' ";
			
			FinalResult f = (FinalResult) db.get(sql);
			if ( f != null ) {
				FinalSubjectResult s = f.getSubject(subjectId);
				if ( s != null && !"".equals(s.getGrade())) {
					marked++;
				}
			}
		}
		
		context.put("total_results", total);
		context.put("marked_results", marked);
		
		float percentProgress = ((float) marked/(float) total)*100;
		context.put("percent_progress", new DecimalFormat("#.00").format(percentProgress));
	}
	

	public void getExamTranscript(Student student) throws Exception {
		SessionUtil u = new SessionUtil();
		Session currentSession = u.getCurrentSession(student.getProgram().getLevel().getPathNo());
		context.put("current_session", currentSession);
		
		context.put("student", student);
		String sql = "select r from FinalResult r where r.student.id = '" + student.getId() + "' order by r.session.startDate";
		
		List<FinalResult> results = db.list(sql);
		if ( results.size() > 0 ) {
			context.put("results", results);
		}
		else {
			context.remove("results");
		}
	}	
	
	
	private void listStudentsByTeacher() throws Exception {
		vm = path + "list_students.vm";
		context.remove("section");
		
		System.out.println("list students by teacher...");
		
		String subjectId = request.getParameter("subject_id");
		String centreId = request.getParameter("centre_id");
		
		Subject subject = null;
		SubjectSection section = null;
		subject = db.find(Subject.class, subjectId);	

		context.put("subject", subject);
		context.put("subject_id", subject.getId());

		String sectionId = section != null ? section.getId() : "";
		context.put("section_id", sectionId);
		
		//session will be current session
		
		//calculatePercentage(sessionId, subjectId, intakeId, centreId, sectionId);
		
		//Session session = db.find(Session.class, sessionId);
		//context.put("session", session);
		
		LearningCentre centre = db.find(LearningCentre.class, centreId);
		context.put("centre_id", centreId);
		context.put("centre", centre);
		
		//List<CourseworkItem> items = getOrCreateCourseworkItem(subject, session, centre);
		//context.put("items", items);
		//getNumberOfStudents(subject, sessionId, intakeId, centreId, sectionId);	
		//listStudents(subject, sessionId, intakeId, centreId, sectionId, 1);
		
		
		//get list of students taking the subject within the current sessions
		List<Session> sessions = db.list("select s from StudentSubject sb Join sb.studentStatus st Join st.session s group by s.id order by s.startDate");

		
		
	}
	
	
	private FinalResult doSaveResult(StudentStatus status) throws Exception {
		
		//result_statuses
		List<SubjectResultStatus> resultStatuses = db.list("select s from SubjectResultStatus s");
		Map<String, SubjectResultStatus> resultStatusMap = new HashMap<String, SubjectResultStatus>();
		for ( SubjectResultStatus resultStatus : resultStatuses ) resultStatusMap.put(resultStatus.getId(), resultStatus);
		
		System.out.println(status.getSession().getName());
		System.out.println(status.getPeriod().getName());
		
		String sql;
		//final subject result
		sql = "select r from FinalResult r " +
				" where r.student.id = '" + status.getStudent().getId() + "' " +
				" and r.session.id = '" + status.getSession().getId() + "' " +
				" and r.period.id = '" + status.getPeriod().getId() + "' ";
		
		FinalResult result = (FinalResult) db.get(sql);
		
		double hours = 0.0;
		double point = 0.0;
		double taken = 0.0;
		
		for ( FinalSubjectResult r : result.getSubjects() ) {
			
			if ( r.getSubject() != null ) {
			
				StudentSubject studentSubject = status.getStudentSubject(r.getSubject());
				String subjectStatus = studentSubject != null ? studentSubject.getSubjectStatus() : "";
				
				double mark = 0.0d;
				
				String m = request.getParameter("mark_" + r.getId());
				String resultStatusId = request.getParameter("status_id_" + r.getId());
				
				SubjectResultStatus resultStatus = null;
				if ( !"".equals(resultStatusId)) resultStatus = resultStatusMap.get(resultStatusId);
				//if mark is non numerical
				int markValue = ResultEntryUtil.nonNumericalMark(m);
	
				if ( markValue < 0 ) {
					
					String markingId = r.getSubject().getMarkingGrade() != null ? r.getSubject().getMarkingGrade().getId() : "";
					
					//look for specific marking scheme
					Student student = status.getStudent();
					//Program program = student.getProgram();
					String programId = student.getProgram().getId();
					SubjectGrade subjectGrade = (SubjectGrade) db.get("select s from SubjectGrade s where s.program.id = '" + programId + "' and s.subject.id = '" + r.getSubject().getId() + "'");
					if ( subjectGrade != null ) {
						markingId = subjectGrade.getMarkingGrade() != null ? subjectGrade.getMarkingGrade().getId() : markingId;
					} 
					
					
					Grade grade = !"".equals(markingId) ? ExamResultUtil.getGrade(markingId, m) : null;
					db.begin();
					if ( grade != null ) {
						r.setGrade(grade.getLetter());
						r.setGradePoint(grade.getPoint());
						r.setPoint(grade.getPoint() * r.getSubject().getCredithrs());
					}
					else System.out.println("Grade is null for " + r.getSubject().getCode());
					
					r.setResultStatus(resultStatus); //can be null
					if ( resultStatus != null ) {
						if ( resultStatus.getResetMark() ) {
							r.setTotalMark(0.0d);
							grade = !"".equals(markingId) ? ExamResultUtil.getGrade(markingId, 0.0d) : null;
							r.setGrade(grade.getLetter());
							r.setGradePoint(grade.getPoint());
							r.setPoint(grade.getPoint() * r.getSubject().getCredithrs());
						}
						else {
							r.setTotalMark(markValue);
						}
					}
					else {
						r.setTotalMark(markValue);
					}
					r.setCreditHour(r.getSubject().getCredithrs());
					db.commit();
	
					
				}
				//or this is numerical
				else {
					try {
						mark = Double.parseDouble(m);
					} catch ( Exception e ) {}
				
					Grade grade = null;
					if ( r.getSubject() != null ) {
						
						String markingId = r.getSubject().getMarkingGrade() != null ? r.getSubject().getMarkingGrade().getId() : "";
						//look for specific marking scheme
						Student student = status.getStudent();
						//Program program = student.getProgram();
						String programId = student.getProgram().getId();
						SubjectGrade subjectGrade = (SubjectGrade) db.get("select s from SubjectGrade s where s.program.id = '" + programId + "' and s.subject.id = '" + r.getSubject().getId() + "'");
						
						if ( subjectGrade != null ) {
							markingId = subjectGrade.getMarkingGrade() != null ? subjectGrade.getMarkingGrade().getId() : markingId;
						} 
						grade = !"".equals(markingId) ? ExamResultUtil.getGrade(markingId, mark) : null;
						
						if ( SpecialSubjectStatus.isMatch(subjectStatus)) {
							
							db.begin();
							r.setGrade(subjectStatus);
							r.setGradePoint(0.0d);
							r.setPoint(0.0d);
							r.setSubjectStatus(subjectStatus);
							r.setExcludeGPA(1);
							r.setCreditHour(r.getSubject().getCredithrs());
							db.commit();	
							
						} else {
							db.begin();
							if ( grade != null ) {
								r.setGrade(grade.getLetter());
								r.setGradePoint(grade.getPoint());
								r.setPoint(grade.getPoint() * r.getSubject().getCredithrs());
							}
							else {
								r.setGrade("ND");
								r.setGradePoint(0.0D);
								r.setPoint(0.0D);
								if ( r.getSubject() != null ) System.out.println("Grade is null for " + r.getSubject().getCode());
								else System.out.println("Grade is null for NULL subject!");
							}
							
							
							r.setResultStatus(resultStatus); //can be null
							if ( resultStatus != null ) {
								if ( resultStatus.getResetMark() ) {
									r.setTotalMark(0.0d);
									grade = !"".equals(markingId) ? ExamResultUtil.getGrade(markingId, 0.0d) : null;
									r.setGrade(grade.getLetter());
									r.setGradePoint(grade.getPoint());
									r.setPoint(grade.getPoint() * r.getSubject().getCredithrs());
								}
								else {
									r.setTotalMark(mark);
								}
							}
							else {
								r.setTotalMark(mark);
							}
							
							r.setCreditHour(r.getSubject().getCredithrs());
							r.setSubjectStatus(subjectStatus);
							r.setExcludeGPA(0);						
							db.commit();
						
						}
					}
					
					if ( resultStatus == null ) {
						if ( r.getSubject().getExcludeGpa() == 0 && r.getExcludeGPA() == 0 ) {
							hours += r.getSubject().getCredithrs();
							point += r.getPoint();
						}
						taken += r.getSubject().getCredithrs();
					} else {
						if ( !resultStatus.getExcludeGPA()) {
							if ( r.getSubject().getExcludeGpa() == 0 && r.getExcludeGPA() == 0 ) {
								hours += r.getSubject().getCredithrs();
								point += r.getPoint();						
							}
							taken += r.getSubject().getCredithrs();
						}
					}
				}
			
			}
		}
		
		db.begin();
		result.setCurrentHours(hours);
		if ( hours > 0 ) {
			double gpa = point / hours;
			result.setGpa(gpa);
			result.setCurrentPoints(point);
			result.setCurrentHours(hours);
			result.setTakenHours(taken);
		}
		else {
			result.setGpa(0);
		}
		db.commit();
		return result;
	}
	
	
	public static void main(String[] args) throws Exception {
		//String sql = "select s from FinalResult r join r.subjects s where r.student.id = '1404116812633'  and r.session.id = '1401960688643'  and r.period.id = '1401960688596'  and s.subject.id = '1401960690270'";
		String sql = "select s from FinalResult r join r.subjects s where r.student.id = '1404116812633'  and r.session.id = '1401960688643'  and r.period.id = '1401960688596'  and s.subject.id = '1401960690270'";
		DbPersistence db = new DbPersistence();
		List<FinalResult> fr = db.list(sql);
		System.out.println(fr.size());
	}

	
}
