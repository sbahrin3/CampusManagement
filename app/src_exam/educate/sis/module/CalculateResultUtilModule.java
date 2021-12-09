package educate.sis.module;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import educate.sis.module.StudentMarkEntryModule.OrderByNameComparator;
import lebah.db.Db;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class CalculateResultUtilModule extends LebahModule {
	
	String path = "exam_util";
	DbPersistence db = new DbPersistence();

	@Override
	public String start() {
		// TODO Auto-generated method stub
		return path + "/start.vm";
	}
	
	@Command("getStudents")
	public String getStudents() throws Exception {
		
		String sql = "SELECT distinct(st.student_id) " +
		"FROM student_course s, student_marks_total m, " +
		"student_status st, intake_batch b " +
		"where st.batch_id = b.intake_session " +
		"and st.session_id = b.session_id " +
		"and st.student_id = m.student_id " +
		"and m.period_id = b.period_id " +
		"and m.student_id = s.student_id " +
		"and s.intake_session = b.intake_session " +
		"order by m.student_id, m.subject_id";

		List<String> studentIds = new ArrayList<String>();
		Db database = null;
		try {
			database = new Db();
			ResultSet rs = database.getStatement().executeQuery(sql);
			while ( rs.next() ) {
				studentIds.add(rs.getString(1));
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			if ( database != null ) database.close();
		}		
		
		return path + "/getStudents.vm";
	}
	
	@Command("runUtil")
	public String runUtil() throws Exception {
		
		
		String sql = "SELECT distinct(st.student_id) " +
				"FROM student_course s, student_marks_total m, " +
				"student_status st, intake_batch b " +
				"where st.batch_id = b.intake_session " +
				"and st.session_id = b.session_id " +
				"and st.student_id = m.student_id " +
				"and m.period_id = b.period_id " +
				"and m.student_id = s.student_id " +
				"and s.intake_session = b.intake_session " +
				"order by m.student_id, m.subject_id";
		
		List<String> studentIds = new ArrayList<String>();
		Db database = null;
		try {
			database = new Db();
			ResultSet rs = database.getStatement().executeQuery(sql);
			while ( rs.next() ) {
				studentIds.add(rs.getString(1));
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			if ( database != null ) database.close();
		}
		
		System.out.println(studentIds.size());

		StudentStatusUtil su = new StudentStatusUtil();
		int counter = 0;
		for ( String studentId : studentIds ) {
			counter++;
			Student s = db.find(Student.class, studentId);
			if ( s != null ) {
				if ( s.getFlag() == 101 ) {
					System.out.println();
					System.out.println(counter + ") " + s.getMatricNo() + " " + s.getBiodata().getName());
					System.out.println("Flag indicator exist.");
				}
				else {
					System.out.println();
					System.out.println(counter + ") " + s.getMatricNo() + " " + s.getBiodata().getName());
					List<StudentStatus> statuses = su.getStudentStatuses(s);
					for ( StudentStatus studentStatus : statuses ) {
						FinalResult result = doSaveResult(db, studentStatus);
						ResultEntryUtil.calculateCGPA(db, result);
						ResultEntryUtil.calculateRestOfCGPA(db, s, result.getSession());
						String programLevelType = s.getProgram().getProgramLevelType();
						ResultEntryUtil.calculateStanding(db, s.getId(), programLevelType);
					}
					
					db.begin();
					s.setFlag(101);
					db.commit();
				
				}
				
			} else {
				System.out.println(studentId + " is null.");
			}
			//Thread.sleep(1000);
		}
		
		return "/runUtil.vm";
	}
	
	
	private static FinalResult doSaveResult(DbPersistence db, StudentStatus status) throws Exception {
		
		
		getFinalResult(db, status);
		
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
				//String m = request.getParameter("mark_" + r.getId());
				String m = Double.toString(r.getTotalMark());
				
				//System.out.println(r.getSubject().getCode() + " = " + m);
				
				String resultStatusId = ""; //request.getParameter("status_id_" + r.getId());
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
	
	private static void getFinalResult(DbPersistence db, StudentStatus studentStatus) throws Exception {
		String sql;
		//final subject result
		sql = "select r from FinalResult r " +
				" where r.student.id = '" + studentStatus.getStudent().getId() + "' " +
				" and r.session.id = '" + studentStatus.getSession().getId() + "' " +
				" and r.period.id = '" + studentStatus.getPeriod().getId() + "' ";

		FinalResult result = (FinalResult) db.get(sql);
		if ( result == null ) {
			System.out.println("Final Result Empty.. need to create");
			result = createFinalResult(db, studentStatus);
		}
		getFinalResults(db, studentStatus, result);
	}
	
	private static FinalResult createFinalResult(DbPersistence db, StudentStatus studentStatus) throws Exception {
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

	private static void getFinalResults(DbPersistence db, StudentStatus studentStatus, FinalResult result) throws Exception {
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
	}

}
