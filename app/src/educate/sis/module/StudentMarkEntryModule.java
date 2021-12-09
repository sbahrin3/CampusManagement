package educate.sis.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import javax.servlet.http.HttpSession;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.enrollment.entity.StudentStatus;
import educate.enrollment.entity.StudentSubject;
import educate.sis.exam.entity.FinalResult;
import educate.sis.exam.entity.FinalSubjectResult;
import educate.sis.exam.entity.Grade;
import educate.sis.exam.entity.SubjectGrade;
import educate.sis.exam.entity.SubjectResultStatus;
import educate.sis.exam.module.SpecialSubjectStatus;
import educate.sis.struct.entity.Period;
import educate.sis.struct.entity.ProgramStructure;
import educate.sis.struct.entity.Session;
import educate.sis.struct.entity.Subject;
import educate.sis.struct.entity.SubjectReg;
import lebah.portal.action.AjaxModule;

public class StudentMarkEntryModule extends AjaxModule {
	
	String path = "apps/util/mark_entry/student/";
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
		else if ( "get_student_by_matric".equals(command)) getStudentByMatric();
		else if ( "next_semester".equals(command)) nextSemester();
		else if ( "prev_semester".equals(command)) prevSemester();
		else if ( "save_result".equals(command)) saveResult();
		else if ( "delete_result".equals(command)) deleteResult();
		return vm;
	}
	

	private void deleteResult() throws Exception {
		vm = path + "student_subjects.vm";
		String statusId = request.getParameter("student_status_id");
		StudentStatus status = (StudentStatus) db.find(StudentStatus.class, statusId);
		
		Student student = status.getStudent();
		Session session = status.getSession();
		
		String resultId = request.getParameter("result_id");
		
		System.out.println("delete result " + resultId);
		
		FinalSubjectResult subjectResult = (FinalSubjectResult) db.find(FinalSubjectResult.class, resultId);
		if ( subjectResult.getSubject() != null ) {
			Subject subject = subjectResult.getSubject();
			ResultEntryUtil.deleteResult(db, student, session, subject);
			ResultEntryUtil.updateResultGPA(db, student, session);
			ResultEntryUtil.calculateCGPA(db, student);
		}
		else {
			System.out.println("deleting..");
			db.begin();
			db.remove(subjectResult);
			db.commit();
		}
		displayStudentStatus(status);

	}

	private void saveResult() throws Exception {
		//vm = path + "list_subjects.vm";
		vm = path + "student_subjects.vm";
		String statusId = request.getParameter("student_status_id");
		StudentStatus studentStatus = (StudentStatus) db.find(StudentStatus.class, statusId);
		//find next
		Student student = studentStatus.getStudent();
		context.put("student", student);
		context.put("student_no", student.getMatricNo());
		
		FinalResult result = doSaveResult(studentStatus);
		ResultEntryUtil.calculateCGPA(db, result);
		ResultEntryUtil.calculateRestOfCGPA(db, student, result.getSession());
		String programLevelType = student.getProgram().getProgramLevelType();
		ResultEntryUtil.calculateStanding(db, student.getId(), programLevelType);
		
		
		statusId = request.getParameter("student_status_id");
		studentStatus = (StudentStatus) db.find(StudentStatus.class, statusId);
		//find next
		student = studentStatus.getStudent();
		context.put("student", student);
		context.put("student_no", student.getMatricNo());

		displayStudentStatus(studentStatus);
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
					else {
						System.out.println("Grade is null for " + r.getSubject().getCode());
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
						
						System.out.println("mark = " + m);
						System.out.println("marking id = " + markingId);
						System.out.println("grade = " + grade);
						
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
								if ( r.getSubject() != null ) {
									System.out.println("Grade is null for " + r.getSubject().getCode());
								}
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

	
	

	private void prevSemester() throws Exception {
		//vm = path + "list_subjects.vm";
		vm = path + "student_subjects.vm";
		String statusId = request.getParameter("student_status_id");
		StudentStatus status = (StudentStatus) db.find(StudentStatus.class, statusId);
		//find next
		Student student = status.getStudent();
		context.put("student", student);
		context.put("student_no", student.getMatricNo());
		StudentStatusUtil u = new StudentStatusUtil();
		List<StudentStatus> statuses = u.getStudentStatuses(student);
		int i = 0;
		StudentStatus prevStatus = null;
		for ( StudentStatus s : statuses ) {
			if ( s.getId().equals(status.getId())) break;
			i++;
		}
		if ( i > 0 ) prevStatus = statuses.get(i-1);
		if ( prevStatus != null ) {
			displayStudentStatus(prevStatus);
		}
	}

	private void nextSemester() throws Exception {
		//vm = path + "list_subjects.vm";
		vm = path + "student_subjects.vm";
		String statusId = request.getParameter("student_status_id");
		StudentStatus status = (StudentStatus) db.find(StudentStatus.class, statusId);
		//find next
		Student student = status.getStudent();
		context.put("student", student);
		context.put("student_no", student.getMatricNo());
		StudentStatusUtil u = new StudentStatusUtil();
		List<StudentStatus> statuses = u.getStudentStatuses(student);
		boolean getNext = false;
		StudentStatus nextStatus = null;
		int i = 0;
		for ( StudentStatus s : statuses ) {
			if ( getNext) {
				nextStatus = s;
				break;
			}
			if ( s.getId().equals(status.getId())) getNext = true;
			i++;
		}
		if ( nextStatus != null ) {
			displayStudentStatus(nextStatus);
		}
	}


	private void getStudentByMatric() throws Exception {
		vm = path + "list_subjects.vm";
		String studentNo = request.getParameter("student_no");
		context.put("student_no", studentNo);
		String sql = "select s from Student s where s.matricNo = '" + studentNo + "'";
		Student student = (Student) db.get(sql);
		if ( student != null ) {
			context.put("student", student);
			displayStudentStatus(student);
		}
		else context.remove("student");
		
		
		
	}
	
	private void displayStudentStatus(StudentStatus studentStatus) throws Exception {
		
		List<SubjectResultStatus> resultStatuses = db.list("select s from SubjectResultStatus s");
		context.put("statuses", resultStatuses);
		
		context.put("student_status", studentStatus);
		String sql;
		Student student = studentStatus.getStudent();
		
		List<Period> periods = student.getProgram().getPeriodScheme().getFunctionalPeriodList();
		context.put("periods", periods);
		
		String programId = student.getProgram().getId();
		String centreId = student.getLearningCenter().getId();
		String intakeId = student.getIntake().getId();
		sql = "select p from ProgramStructure p where p.program.id = '" + programId + "' " +
				"and p.learningCenter.id = '" + centreId + "' " +
						"and p.session.id = '" + intakeId + "'";
		ProgramStructure ps = (ProgramStructure) db.get(sql);
		if ( ps != null ) {
			context.put("programStructure", ps);
			ProgramUtil pu = new ProgramUtil();
			Set<SubjectReg> subjects = pu.getSubjectRegs(ps, studentStatus.getPeriod().getId());
			context.put("subjects", subjects);
			
		} else {
			context.remove("programStructure");
			context.remove("subjects");
		}
		
		getFinalResult(studentStatus);
		
		StudentStatusUtil u = new StudentStatusUtil();
		List<StudentStatus> statuses = u.getStudentStatuses(student);
		int i = 0;
		for ( StudentStatus s : statuses ) {
			if ( studentStatus.getId().equals(s.getId())) break;
			i++;
		}
		
		if ( i == 0 ) context.put("bof", true);
		else context.remove("bof");
		if ( i == statuses.size() - 1 ) context.put("eof", true);
		else context.remove("eof");
		
	}

	private void displayStudentStatus(Student student) throws Exception {
		
		List<SubjectResultStatus> resultStatuses = db.list("select s from SubjectResultStatus s");
		context.put("statuses", resultStatuses);
		
		String sql;
		StudentStatus studentStatus = getCurrentStudentStatus(student);
		
		if ( studentStatus == null ) return;
		
		List<Period> periods = student.getProgram().getPeriodScheme().getFunctionalPeriodList();
		context.put("periods", periods);
		
		String programId = student.getProgram().getId();
		String centreId = student.getLearningCenter().getId();
		String intakeId = student.getIntake().getId();
		sql = "select p from ProgramStructure p where p.program.id = '" + programId + "' " +
				"and p.learningCenter.id = '" + centreId + "' " +
						"and p.session.id = '" + intakeId + "'";
		ProgramStructure ps = (ProgramStructure) db.get(sql);
		if ( ps != null ) {
			context.put("programStructure", ps);
			ProgramUtil pu = new ProgramUtil();
			Set<SubjectReg> subjects = pu.getSubjectRegs(ps, studentStatus.getPeriod().getId());
			context.put("subjects", subjects);
			
		} else {
			context.remove("programStructure");
			context.remove("subjects");
		}
		
		getFinalResult(studentStatus);
	}

	/*
	private void deleteFinalResult(StudentStatus studentStatus) throws Exception {
		context.remove("subject_results");
		context.remove("final_result");
		String sql;
		//final subject result
		sql = "select r from FinalResult r " +
				" where r.student.id = '" + studentStatus.getStudent().getId() + "' " +
				" and r.session.id = '" + studentStatus.getSession().getId() + "' " +
				" and r.period.id = '" + studentStatus.getPeriod().getId() + "' ";
		FinalResult result = (FinalResult) db.get(sql);
		if ( result != null ) {
			System.out.println("removing the result");
			db.begin();
			db.remove(result);
			db.commit();
		}
		
	}
	*/

	private void getFinalResult(StudentStatus studentStatus) throws Exception {
		context.remove("subject_results");
		String sql;
		//final subject result
		sql = "select r from FinalResult r " +
				" where r.student.id = '" + studentStatus.getStudent().getId() + "' " +
				" and r.session.id = '" + studentStatus.getSession().getId() + "' " +
				" and r.period.id = '" + studentStatus.getPeriod().getId() + "' ";

		FinalResult result = (FinalResult) db.get(sql);
		if ( result == null ) {
			System.out.println("Final Result Empty.. need to create");
			result = createFinalResult(studentStatus);
		}
		getFinalResults(studentStatus, result);
	}


	private FinalResult createFinalResult(StudentStatus studentStatus) throws Exception {
		FinalResult result;
		
		String sql = "select r from FinalResult r " +
		" where r.student.id = '" + studentStatus.getStudent().getId() + "' " +
		" and r.session.id = '" + studentStatus.getSession().getId() + "' ";
		result = (FinalResult) db.get(sql);
		
		if ( result != null ) {

			List<FinalSubjectResult> finalSubjectResults = result.getSubjects();
			if ( finalSubjectResults.size() > 0 ) {
				db.begin();
				for ( FinalSubjectResult s : finalSubjectResults ) {
					db.remove(s);
				}
				db.commit();
			}
			
			db.begin();
			result.setPeriod(studentStatus.getPeriod());
			Set<StudentSubject> studentSubjects = studentStatus.getStudentSubjects();
			//result.setSubjects(new ArrayList<FinalSubjectResult>());
			for ( StudentSubject s : studentSubjects ) {
				FinalSubjectResult f = new FinalSubjectResult();
				f.setSubject(s.getSubject());
				result.addSubject(f);
			}
			db.commit();
		}
		else {
			db.begin();
			result = new FinalResult(studentStatus.getSession().getId(), studentStatus.getStudent().getId());
			result.setStudent(studentStatus.getStudent());
			result.setPeriod(studentStatus.getPeriod());
			result.setSession(studentStatus.getSession());
			result.setTime(new Date());
			result.setCreated(new Date());
			
			Set<StudentSubject> studentSubjects = studentStatus.getStudentSubjects();
			for ( StudentSubject s : studentSubjects ) {
				FinalSubjectResult f = new FinalSubjectResult();
				f.setSubject(s.getSubject());
				result.addSubject(f);
			}
			db.persist(result);
			db.commit();
		
		}
		return result;
	}

	private void getFinalResults(StudentStatus studentStatus, FinalResult result) throws Exception {
		context.put("final_result", result);
		Set<StudentSubject> studentSubjects = studentStatus.getStudentSubjects();
		List<String> subjects1 = new ArrayList<String>();
		List<String> subjects2 = new ArrayList<String>();
		List<FinalSubjectResult> subjectResults = result.getSubjects();
		for ( FinalSubjectResult r : subjectResults ) {
			if ( r.getSubject() != null ) subjects1.add(r.getSubject().getId());
		}
		
		//look for added subject
		if ( studentSubjects != null ) {
			db.begin();
			for ( StudentSubject s : studentSubjects ) {
				subjects2.add(s.getSubject().getId());
				if ( s.getSubject() != null ) {
					if ( !subjects1.contains(s.getSubject().getId())) {
						FinalSubjectResult f = new FinalSubjectResult();
						f.setSubject(s.getSubject());
						result.addSubject(f);
					}
				}
				else {
					System.out.println("Subject is NULL for " + s.getStudentStatus().getStudent().getMatricNo());
				}
			}
			db.commit();
		}
		
		//look for removed subject
		if ( subjectResults != null ) {
			db.begin();
			for ( FinalSubjectResult f : subjectResults ) {
				if ( f.getSubject() != null ) {
					if ( !subjects2.contains(f.getSubject().getId())) {
						f.setStatus("DELETED");
					}
				}
			}
			db.commit();
		}
		
		List<FinalSubjectResult> list = new ArrayList<FinalSubjectResult>();
		list.addAll(result.getSubjects());
		Collections.sort(list, new OrderByNameComparator());
		context.put("subject_results", list);
	}

	private void start() {
		vm = path + "start.vm";
		
	}
	
	private StudentStatus getCurrentStudentStatus(Student student) {
		StudentStatus studentStatus = null;
		context.remove("student_status");
		StudentStatusUtil pu = new StudentStatusUtil();
		try {
			studentStatus = pu.getCurrentStudentStatus(student);
			if ( studentStatus == null ) studentStatus = pu.getLastStudentStatus(student);
			if ( studentStatus != null ) {
				context.put("student_status", studentStatus);
			}
			else {
				context.remove("student_status");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return studentStatus;
	}
	

	
	static class OrderByCodeComparator extends educate.util.MyComparator {
		public int compare(Object o1, Object o2) {
			FinalSubjectResult r1 = (FinalSubjectResult) o1;
			FinalSubjectResult r2 = (FinalSubjectResult) o2;
			if ( r1.getSubject() == null || r2.getSubject() == null ) return 0;
			return r1.getSubject().getCode().compareTo(r2.getSubject().getCode());
		}
	}
	
	static class OrderByNameComparator implements Comparator {
		public int compare(Object o1, Object o2) {
			FinalSubjectResult r1 = (FinalSubjectResult) o1;
			FinalSubjectResult r2 = (FinalSubjectResult) o2;
			if ( r1.getSubject() == null || r2.getSubject() == null ) return 0;
			return r1.getSubject().getName().compareTo(r2.getSubject().getName());
		}

		@Override
		public Comparator reversed() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Comparator thenComparing(Comparator other) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Comparator thenComparing(Function keyExtractor,
				Comparator keyComparator) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Comparator thenComparing(Function keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Comparator thenComparingInt(ToIntFunction keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Comparator thenComparingLong(ToLongFunction keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Comparator thenComparingDouble(ToDoubleFunction keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		public static <T extends Comparable<? super T>> Comparator<T> reverseOrder() {
			// TODO Auto-generated method stub
			return null;
		}

		public static <T extends Comparable<? super T>> Comparator<T> naturalOrder() {
			// TODO Auto-generated method stub
			return null;
		}

		public static <T> Comparator<T> nullsFirst(
				Comparator<? super T> comparator) {
			// TODO Auto-generated method stub
			return null;
		}

		public static <T> Comparator<T> nullsLast(
				Comparator<? super T> comparator) {
			// TODO Auto-generated method stub
			return null;
		}

		public static <T, U> Comparator<T> comparing(
				Function<? super T, ? extends U> keyExtractor,
				Comparator<? super U> keyComparator) {
			// TODO Auto-generated method stub
			return null;
		}

		public static <T, U extends Comparable<? super U>> Comparator<T> comparing(
				Function<? super T, ? extends U> keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		public static <T> Comparator<T> comparingInt(
				ToIntFunction<? super T> keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		public static <T> Comparator<T> comparingLong(
				ToLongFunction<? super T> keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		public static <T> Comparator<T> comparingDouble(
				ToDoubleFunction<? super T> keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}


	}

	static class StatusComparator implements Comparator {

		public int compare(Object o1, Object o2) {
			StudentStatus s1 = (StudentStatus) o1;
			StudentStatus s2 = (StudentStatus) o2;
			if ( s1.getSession() == null || s2.getSession() == null ) return 0;
			if ( s1.getSession().getStartDate().before(s2.getSession().getStartDate())) return -1;
			else if ( s1.getSession().getStartDate().after(s2.getSession().getStartDate())) return 1;
			return 0;
		}

		@Override
		public Comparator reversed() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Comparator thenComparing(Comparator other) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Comparator thenComparing(Function keyExtractor,
				Comparator keyComparator) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Comparator thenComparing(Function keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Comparator thenComparingInt(ToIntFunction keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Comparator thenComparingLong(ToLongFunction keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Comparator thenComparingDouble(ToDoubleFunction keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		public static <T extends Comparable<? super T>> Comparator<T> reverseOrder() {
			// TODO Auto-generated method stub
			return null;
		}

		public static <T extends Comparable<? super T>> Comparator<T> naturalOrder() {
			// TODO Auto-generated method stub
			return null;
		}

		public static <T> Comparator<T> nullsFirst(
				Comparator<? super T> comparator) {
			// TODO Auto-generated method stub
			return null;
		}

		public static <T> Comparator<T> nullsLast(
				Comparator<? super T> comparator) {
			// TODO Auto-generated method stub
			return null;
		}

		public static <T, U> Comparator<T> comparing(
				Function<? super T, ? extends U> keyExtractor,
				Comparator<? super U> keyComparator) {
			// TODO Auto-generated method stub
			return null;
		}

		public static <T, U extends Comparable<? super U>> Comparator<T> comparing(
				Function<? super T, ? extends U> keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		public static <T> Comparator<T> comparingInt(
				ToIntFunction<? super T> keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		public static <T> Comparator<T> comparingLong(
				ToLongFunction<? super T> keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		public static <T> Comparator<T> comparingDouble(
				ToDoubleFunction<? super T> keyExtractor) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}

}
