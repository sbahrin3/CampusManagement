package educate.sis.module;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.enrollment.entity.StudentStatus;
import educate.sis.exam.entity.AchievementLevel;
import educate.sis.exam.entity.ExamResult;
import educate.sis.exam.entity.FinalResult;
import educate.sis.exam.entity.FinalSubjectResult;
import educate.sis.exam.entity.Grade;
import educate.sis.exam.entity.SessionResult;
import educate.sis.exam.entity.Standing;
import educate.sis.exam.entity.StandingRule;
import educate.sis.exam.entity.SubjectGrade;
import educate.sis.exam.entity.SubjectResult;
import educate.sis.exam.module.SpecialSubjectStatus;
import educate.sis.module.StudentMarkEntryModule.StatusComparator;
import educate.sis.struct.entity.Period;
import educate.sis.struct.entity.Session;
import educate.sis.struct.entity.Subject;

public class ResultEntryUtil {
	
	public static final String FAIL = "FAIL";
	public static final String CONDITIONAL_PASS = "CONDITIONAL_PASS";
	public static final String PASS = "PASS";
	public static final int USE_BETTER_GRADE = 2;
	public static final int USE_LATEST_RESULT = 1;
	
	//for the next version, these data shall comes from Grades Setup
	private static String[] nonNumericValues = {"A+", "A", "A-", "B+", "B", "B-", "C+", "C", "C", 
		"D+", "D", "D-", "E+", "E", "E-", "F" };
	
	

	
	/**
	 * This method shall replace the marking values with grade values.
	 * The marking values shall be removed, PERMANENTLY.
	 * @param db
	 * @param studentId
	 * @throws Exception
	 */
	
	public static void updateResultFromGrades(DbPersistence db, String studentId) throws Exception {
		String sql = "select r from FinalResult r where r.student.id = '" + studentId + "' order by r.session.startDate";
		List<FinalResult> results = db.list(sql);
		for ( FinalResult result : results ) {
			List<FinalSubjectResult> subjectResults = result.getSubjects();
			Hashtable<String, String> markTable = new Hashtable<String, String>();
			for ( FinalSubjectResult sr : subjectResults ) {
				String gradeLetter = sr.getGrade();
				if ( sr.getGrade() != null ) {
					markTable.put(sr.getId(), gradeLetter);
				}
			}
			ResultEntryUtil.saveResult(result, markTable);
		}
		
	}
	
	
	public static void saveResult(FinalResult result, Hashtable<String, String> markTable) throws Exception {
		ResultEntryUtil.saveResult(result, markTable, true);
	}
	
	
	public static void saveResult(FinalResult result, Hashtable<String, String> markTable, boolean calculateTranscript) throws Exception {
		DbPersistence db = new DbPersistence();
		doSaveResult(db, result, markTable);
		if ( calculateTranscript && result != null ) calculateTranscript(db, result);
	}

	public static void calculateTranscript(DbPersistence db, FinalResult result) throws Exception {
		ResultEntryUtil.calculateCGPA(db, result);
		ResultEntryUtil.calculateRestOfCGPA(db, result.getStudent(), result.getSession());
		String programLevelType = result.getStudent().getProgram().getProgramLevelType();
		ResultEntryUtil.calculateStanding(db, result.getStudent().getId(), programLevelType);
	}
	
	private static FinalResult doSaveResult(DbPersistence db, FinalResult result, Hashtable<String, String> markTable) throws Exception {
		double hours = 0.0;
		double point = 0.0;
		if ( result != null ) {
			for ( FinalSubjectResult r : result.getSubjects() ) {
				double mark = 0.0d;
				String m = markTable.get(r.getId()); // request.getParameter("mark_" + r.getId());
				int markValue = ResultEntryUtil.nonNumericalMark(m);
	
				if ( markValue < 0 ) {
					
					String markingId = r.getSubject().getMarkingGrade() != null ? r.getSubject().getMarkingGrade().getId() : "";
					Grade grade = !"".equals(markingId) ? ExamResultUtil.getGrade(markingId, m) : null;
					db.begin();
					if ( grade != null ) {
						r.setGrade(grade.getLetter());
						r.setGradePoint(grade.getPoint());
						r.setPoint(grade.getPoint() * r.getSubject().getCredithrs());
					}
					else System.out.println("Grade is null for " + r.getSubject().getCode());
					r.setTotalMark(markValue);
					r.setCreditHour(r.getSubject().getCredithrs());
					db.commit();
	
					hours += r.getSubject().getCredithrs();
					point += r.getPoint();
					
				}
				//or this is numerical
				else {
					try {
						mark = Double.parseDouble(m);
					} catch ( Exception e ) {}
				
	
					String markingId = r.getSubject().getMarkingGrade() != null ? r.getSubject().getMarkingGrade().getId() : "";
					Grade grade = !"".equals(markingId) ? ExamResultUtil.getGrade(markingId, mark) : null;
					
					db.begin();
					if ( grade != null ) {
						r.setGrade(grade.getLetter());
						r.setGradePoint(grade.getPoint());
						r.setPoint(grade.getPoint() * r.getSubject().getCredithrs());
					}
					else System.out.println("Grade is null for " + r.getSubject().getCode());
					r.setTotalMark(mark);
					r.setCreditHour(r.getSubject().getCredithrs());
					db.commit();
					
					hours += r.getSubject().getCredithrs();
					point += r.getPoint();
				}
				
				
			}
			
			db.begin();
			result.setCurrentHours(hours);
			if ( hours > 0 ) {
				double gpa = point / hours;
				result.setGpa(gpa);
				result.setCurrentPoints(point);
			}
			else {
				result.setGpa(0);
			}
			db.commit();
		}
		return result;
	}
	
	public static void calculateStandingByMatricNo(DbPersistence db, String matricNo, String programLevelType) throws Exception {
		String sql = "select r from FinalResult r where r.student.matricNo = '" + matricNo + "' order by r.session.startDate";
		List<FinalResult> results = db.list(sql);
		calculateStanding(db, results, programLevelType);
	}
	
	public static void calculateStanding(DbPersistence db, String studentId, String programLevelType) throws Exception {
		String sql = "select r from FinalResult r where r.student.id = '" + studentId + "' order by r.session.startDate";
		List<FinalResult> results = db.list(sql);
		calculateStanding(db, results, programLevelType);
	}
	

	public static void calculateStanding2(DbPersistence db, List<FinalResult> results) throws Exception {
		
		//to store previous standing
		Hashtable<Integer, String> standingList = new Hashtable<Integer, String>();
		int cnt = 0;
		boolean conditionalPassCount = false;
		for ( FinalResult result : results ) {

			//calculate only for current credit hours > 0
			if ( result.getCurrentHours() > 0 ) {
				cnt++;
				double cgpa = result.getCgpa();
				String standing = "";
				if ( cgpa >= 2.00 ) {
					standing = ResultEntryUtil.PASS;
					standingList.put(cnt, standing);
				}
				else if ( cgpa < 2.00 ) {
					if ( cnt > 1 ) {
						//get previous standing
						String prevStanding = standingList.get(cnt-1) != null ? standingList.get(cnt-1) : "";
						if ( conditionalPassCount ) {
							standing = ResultEntryUtil.FAIL;
						}
						else if ( ResultEntryUtil.CONDITIONAL_PASS.equals(prevStanding) ) {
							conditionalPassCount = true;
							standing = ResultEntryUtil.CONDITIONAL_PASS;
						}
						else {
							conditionalPassCount = false;
							standing = ResultEntryUtil.CONDITIONAL_PASS;
						}
					}
					else {
						standing = ResultEntryUtil.CONDITIONAL_PASS;
					}
					standingList.put(cnt, standing);
				}
				db.begin();
				//System.out.println(standing);
				result.setStanding(standing);
				db.commit();
			}
		}
	}
	
	
	/*
	 * 
	 * good standing (GS)
	 * probation 1 (P1)
	 * probation 2 (P1)
	 * weak standing (WS)
	 * dismissal (D)
	 * 
	 * 
	 * award dean's list (DL)
	 * high achiever (HA)
	 */
	public static void calculateStanding(DbPersistence db, List<FinalResult> results, String programLevelType) throws Exception {
		
		//read standing rules
		List<StandingRule> standingRules = db.list("select s from StandingRule s where s.programLevelType = '" + programLevelType + "' order by s.sequence");
		
		//to store previous standing
		Hashtable<Integer, String> standingList = new Hashtable<Integer, String>();
		int cnt = 0;

		for ( FinalResult result : results ) {
			//calculate only for current credit hours > 0
			if ( result.getCurrentHours() > 0 ) {
				cnt++;
				double cgpa = result.getCgpa();
				double gpa = result.getGpa();
				Standing standing = null;
				String prevStandingCode = cnt > 1 ? standingList.get(cnt-1) != null ? standingList.get(cnt-1) : "" : "";
				String comparePrevStandingCode = "";
				
				for ( StandingRule standingRule : standingRules ) {
					if ( "eqgt".equals(standingRule.getComparator()) ) {
						if ( cgpa >= standingRule.getCgpaValue() ) {
							
							if ( "lt".equals(standingRule.getComparator2()) ) {
								if ( gpa < standingRule.getGpaValue() ) {
									comparePrevStandingCode = standingRule.getPreviousStanding() != null ? standingRule.getPreviousStanding().getCode() : "";
									if ( comparePrevStandingCode.equals(prevStandingCode)) {
										standing = standingRule.getCurrentStanding();
									}									
								}
							}
							else if ( "eqgt".equals(standingRule.getComparator2()) ) {
								if ( gpa >= standingRule.getGpaValue() ) {
									comparePrevStandingCode = standingRule.getPreviousStanding() != null ? standingRule.getPreviousStanding().getCode() : "";
									if ( comparePrevStandingCode.equals(prevStandingCode)) {
										standing = standingRule.getCurrentStanding();
									}									
								}
							}							
							else {
								comparePrevStandingCode = standingRule.getPreviousStanding() != null ? standingRule.getPreviousStanding().getCode() : "";
								if ( comparePrevStandingCode.equals(prevStandingCode)) {
									standing = standingRule.getCurrentStanding();
								}
							}
						}
					}
					else if ( "lt".equals(standingRule.getComparator()) ) {
						if ( cgpa < standingRule.getCgpaValue()) {
							
							if ( "lt".equals(standingRule.getComparator2()) ) {
								if ( gpa < standingRule.getGpaValue() ) {
									comparePrevStandingCode = standingRule.getPreviousStanding() != null ? standingRule.getPreviousStanding().getCode() : "";
									if ( comparePrevStandingCode.equals(prevStandingCode)) {
										standing = standingRule.getCurrentStanding();
									}									
								}
							}
							else if ( "eqgt".equals(standingRule.getComparator2()) ) {
								if ( gpa >= standingRule.getGpaValue() ) {
									comparePrevStandingCode = standingRule.getPreviousStanding() != null ? standingRule.getPreviousStanding().getCode() : "";
									if ( comparePrevStandingCode.equals(prevStandingCode)) {
										standing = standingRule.getCurrentStanding();
									}									
								}
							}							
							else {
								comparePrevStandingCode = standingRule.getPreviousStanding() != null ? standingRule.getPreviousStanding().getCode() : "";
								if ( comparePrevStandingCode.equals(prevStandingCode)) {
									standing = standingRule.getCurrentStanding();
								}
							}
						}
					}
					standingList.put(cnt, standing != null ? standing.getCode() : "");
				}
				
				//ACHIEVEMENT LEVEL
				String programId = result.getStudent().getProgram().getId();
				List<AchievementLevel> achievementLevels = db.list("select a from AchievementLevel a where a.program.id = '" + programId + "' order by a.gpaValue desc");
				
				AchievementLevel achievementLevel = null;
				for ( AchievementLevel a : achievementLevels ) {
					if ( a.getGpaValue() > 0 ) {
						if ( result.getGpa() >= a.getGpaValue() ) {
							achievementLevel = a;
							break;
						}
					}
				}

				db.begin();
				result.setResultStanding(standing);
				result.setStanding(standing != null ? standing.getName() : "");
				result.setAchievementLevel(achievementLevel);
				db.commit();
				
				//System.out.println("standing = " + result.getStanding());
			}
		}
	}
	
	public static void calculateCGPA(DbPersistence db, FinalResult currentResult) throws Exception {
		calculateCGPA(db, currentResult, false);
	}
	
	public static void calculateCGPA(DbPersistence db, FinalResult currentResult, boolean excludeGradeF) throws Exception {
		String sql;
		if ( currentResult != null ) {
			
			Date date = currentResult.getSession().getStartDate(); 
			Hashtable param = new Hashtable();
			param.put("date", date);
			sql = "select r from FinalResult r " +
			" where r.student.id = '" + currentResult.getStudent().getId() + "' ";
			sql += " and r.session.startDate <= :date order by r.session.startDate";
			List<FinalResult> results = db.list(sql, param);
			calculate(db, currentResult, results, excludeGradeF);
		}
		
	}
	
	private static void calculate(DbPersistence db, FinalResult currentResult, List<FinalResult> results) throws Exception {
		calculate(db, currentResult, results, false);
	}

	private static void calculate(DbPersistence db, FinalResult currentResult, List<FinalResult> results, boolean excludeGradeF) throws Exception {
		double cumPoint = 0.0;
		double cumHours = 0.0;
		double cgpa = 0.0;
		
		//FOR ALL RESULTS
		//gather all results beginning from start semester until current semester
		//but, discard duplicate subjects, by using a selection strategy
		//what strategy to use? 1 or 2?
		int useStrategy = ResultEntryUtil.USE_BETTER_GRADE;// ResultEntryUtil.USE_LATEST_RESULT;
		
		List<FinalSubjectResult> subjectResults = useStrategy == 1? useStrategyKeepLast(results) : useStrategyForBetterGrade(results);
		
		for ( FinalSubjectResult f : subjectResults ) {
			boolean calc = true;
			if ( f.getResultStatus() == null ) {
				if ( f.getSubject() != null ) {
					if ( f.getSubject().getExcludeGpa() == 0 && f.getExcludeGPA() == 0 )  {
						
						if ( excludeGradeF && "F".equals(f.getGrade()) ) calc = false;
						
						
						if ( calc ) {
							cumPoint += f.getPoint();
							cumHours += f.getCreditHour();
						}
						
					}
				}
			}
			else {
				
				if ( !f.getResultStatus().getExcludeGPA() ) {
					if ( f.getSubject() != null ) {
						if ( f.getSubject().getExcludeGpa() == 0 && f.getExcludeGPA() == 0 )  {
							if ( excludeGradeF && "F".equals(f.getGrade()) ) calc = false;
							
							
							if ( calc ) {
								cumPoint += f.getPoint();
								cumHours += f.getCreditHour();
							}
						}
					}
				}
			}
		}
		
		//gpa
		
		double point = 0.0;
		double hours = 0.0;
		double takenHours = 0.0;
		double gpa = 0.0;
		
		for ( FinalSubjectResult f : currentResult.getSubjects() ) {
			
			boolean taken = true;
			if ( excludeGradeF && "F".equals(f.getGrade()) ) {
				taken = false;
			}
			
			if ( taken ) takenHours += f.getCreditHour();
			
			
			boolean calc = true;
			if ( f.getResultStatus() == null ) {
				if ( f.getSubject() != null ) {
					if ( f.getSubject().getExcludeGpa() == 0 && f.getExcludeGPA() == 0 )  {
						
						if ( excludeGradeF && "F".equals(f.getGrade()) ) calc = false;
						
						
						if ( calc ) {
							point += f.getPoint();
							hours += f.getCreditHour();
						}
						
					}
				}
			}
			else {
				
				if ( !f.getResultStatus().getExcludeGPA() ) {
					if ( f.getSubject() != null ) {
						if ( f.getSubject().getExcludeGpa() == 0 && f.getExcludeGPA() == 0 )  {
							
							if ( excludeGradeF && "F".equals(f.getGrade()) ) calc = false;
							
							
							if ( calc ) {
								point += f.getPoint();
								hours += f.getCreditHour();
							}
						}
					}
				}
			}
		}
		
		
		cgpa = cumHours > 0 ? cumPoint / cumHours : 0;
		gpa = hours > 0 ? point / hours : 0;

		db.begin();
		
		currentResult.setGpa(gpa);
		currentResult.setCurrentHours(hours);
		currentResult.setCurrentPoints(point);
		
		currentResult.setTakenHours(takenHours);
		currentResult.setTotalHours(hours);

		currentResult.setCgpa(cgpa);
		currentResult.setCumulativeHours(cumHours);
		currentResult.setCumulativePoints(cumPoint);
		
		currentResult.setUpdated(false);
		
		db.commit();
		
	}

	/**
	 * In this strategy, when a subject result has been found already exists in the result list,
	 * remove the previous subject result, and put in the latest one.
	 */
	private static List<FinalSubjectResult> useStrategyKeepLast(List<FinalResult> results) {
		List<FinalSubjectResult> subjectResults = new ArrayList<FinalSubjectResult>();
		
		for ( FinalResult r : results ) {
			for ( FinalSubjectResult f : r.getSubjects() ) {
				checkExistsForDuplicate(subjectResults, f);
			}
		}
		return subjectResults;
	}
	
	/**
	 * In this strategy, if subject result has been found already exists in the subject result list,
	 * compare the marks, and put in the best marks. 
	 */
	private static List<FinalSubjectResult> useStrategyForBetterGrade(List<FinalResult> results) {
		List<FinalSubjectResult> subjectResults = new ArrayList<FinalSubjectResult>();
		
		for ( FinalResult r : results ) {
			for ( FinalSubjectResult f : r.getSubjects() ) {
				checkExistsForBetterGrade(subjectResults, f);
			}
		}
		

		return subjectResults;
	}
	
	private static void checkExistsForBetterGrade(List<FinalSubjectResult> subjectResults, FinalSubjectResult f) {

		int i = 0;
		boolean add = true;
		for ( FinalSubjectResult fi : subjectResults ) {
			if ( fi.getSubject() != null && f.getSubject() != null ) {
				if ( f.getSubject().getId().equals(fi.getSubject().getId())) {
					if (fi.getPoint() < f.getPoint() ) {
						subjectResults.remove(i);
					}
					else {
						
						add = false;
					}
					break;
				}
			}
			i++;
		}
		if ( add ) subjectResults.add(f);

	}	

	private static void checkExistsForDuplicate(List<FinalSubjectResult> subjectResults, FinalSubjectResult f) {
		int i = 0;
		for ( FinalSubjectResult fi : subjectResults ) {
			if ( fi.getSubject() != null && f.getSubject() != null ) {
				if ( f.getSubject().getId().equals(fi.getSubject().getId())) {
					subjectResults.remove(i);
					break;
				}
			}
			i++;
		}
		subjectResults.add(f);
	}	

	
	public static void calculateRestOfCGPA(DbPersistence db, Student student, Session session) throws Exception {
		calculateRestOfCGPA(db, student, session, false);
	}
	
	
	public static void calculateRestOfCGPA(DbPersistence db, Student student, Session session, boolean excludeGradeF) throws Exception {
		
		
		
		String sql;
		FinalResult result;
		
		Set<StudentStatus> statusSet = student.getStatus();
		List<StudentStatus> statusList = new ArrayList<StudentStatus>();
	
		if ( statusSet != null ) statusList.addAll(statusSet);
		Collections.sort(statusList, new StatusComparator());
		for ( StudentStatus st : statusList ) {
			//only do if status is defined
			if ( st.getPeriod() != null && !st.getType().getDefer() ) { //MUST NOT DEFER STATUS
				if ( st.getSession().getStartDate().after(session.getStartDate())) {
					sql = "select r from FinalResult r " +
					" where r.student.id = '" + st.getStudent().getId() + "' " +
					" and r.session.id = '" + st.getSession().getId() + "' " +
					" and r.period.id = '" + st.getPeriod().getId() + "' ";
					result = (FinalResult) db.get(sql);
					if ( result != null ) {
						calculateCGPA(db, result, excludeGradeF);
					}
				}
			}
		}
	}
	
	public static void calculateAllCGPA(DbPersistence db, Student student) throws Exception {
		String sql;
		FinalResult result;
		Set<StudentStatus> statusSet = student.getStatus();
		List<StudentStatus> statusList = new ArrayList<StudentStatus>();
		if ( statusSet != null ) {
			statusList.addAll(statusSet);
			Collections.sort(statusList, new StatusComparator());
			for ( StudentStatus st : statusList ) {
				if ( st.getSession() != null ) {
					sql = "select r from FinalResult r " +
					" where r.student.id = '" + st.getStudent().getId() + "' " +
					" and r.session.id = '" + st.getSession().getId() + "' " +
					" and r.period.id = '" + st.getPeriod().getId() + "' ";
					result = (FinalResult) db.get(sql);
					calculateCGPA(db, result);
				}
			}
		}
	}	
	
	public static void calculateCGPA(DbPersistence db, Student student) throws Exception {
		calculateAllCGPA(db, student);
	}
	
	public static void calculateCGPA(Student student) throws Exception {
		calculateAllCGPA(new DbPersistence(), student);
	}
	
	public static void updateResultGPA(DbPersistence db, Student student, Session session) throws Exception {
		StudentStatus st = student.getStatus(session.getId());
		if ( st != null ) {
			double hours = 0.0;
			double point = 0.0;
			String sql = "select r from FinalResult r " +
			" where r.student.id = '" + st.getStudent().getId() + "' " +
			" and r.session.id = '" + st.getSession().getId() + "' " +
			" and r.period.id = '" + st.getPeriod().getId() + "' ";
			FinalResult result = (FinalResult) db.get(sql);
			List<FinalSubjectResult> subjects = result.getSubjects();
			for ( FinalSubjectResult r : subjects ) {
				if ( r.getSubject() != null ) {
					hours += r.getSubject().getCredithrs();
					point += r.getPoint();
				}
			}
			db.begin();
			result.setCurrentHours(hours);
			double gpa = point / hours;
			result.setGpa(gpa);
			db.commit();
		}
	}
	
	public static void updateResultGPA(Student student, Session session) throws Exception {
		updateResultGPA(new DbPersistence(), student, session);
	}
	
	public static void deleteResult(DbPersistence db, Student student, Session session, Subject subject) throws Exception {
		StudentStatus st = student.getStatus(session.getId());
		if ( st != null ) {
			String sql = "select r from FinalResult r " +
			" where r.student.id = '" + st.getStudent().getId() + "' " +
			" and r.session.id = '" + st.getSession().getId() + "' " +
			" and r.period.id = '" + st.getPeriod().getId() + "' ";
			FinalResult result = (FinalResult) db.get(sql);
			List<FinalSubjectResult> subjects = result.getSubjects();
			int i = 0;
			FinalSubjectResult subjectResult = null;
			for ( FinalSubjectResult r : subjects ) {
				if ( r.getSubject() != null ) {
					if ( r.getSubject().getId().equals(subject.getId()) ) {
						subjectResult = r;
						break;
					}
				}
				i++;
			}
			if ( subjectResult != null ) {
				db.begin();
				result.removeSubject(subjectResult);
				db.commit();
			}

		}
	}
	
	public static void deleteResult(Student student, Session session, Subject subject) throws Exception {
		deleteResult(new DbPersistence(), student, session, subject);
	}
	
	/*
	 * This is the RIGHT method in use
	 */
	public static int saveResult(DbPersistence db, StudentStatus studentStatus, Subject subject, double mark) throws Exception {
		return saveResult(db, studentStatus, subject, mark, "", true);
	}
	
	public static int saveResultOnly(DbPersistence db, StudentStatus studentStatus, Subject subject, double mark) throws Exception {
		return saveResult(db, studentStatus, subject, mark, "", false);
	}	
	
	public static int saveResult(DbPersistence db, StudentStatus studentStatus, Subject subject, String subjectStatus) throws Exception {
		return saveResult(db, studentStatus, subject, 0.0d, subjectStatus, true);
	}
	
	public static int saveResult(DbPersistence db, StudentStatus studentStatus, Subject subject, double mark, String subjectStatus) throws Exception {
		return saveResult(db, studentStatus, subject, mark, subjectStatus, false);
	}
	
	public static int saveResult(DbPersistence db, StudentStatus studentStatus, Subject subject, double mark, String subjectStatus, boolean calculateGPA) throws Exception {
		
		String studentId = studentStatus.getStudent().getId();
		String sessionId = studentStatus.getSession().getId();
		String periodId = studentStatus.getPeriod().getId();
		String subjectId = subject.getId();
		
		Student student = studentStatus.getStudent();
		Session session = studentStatus.getSession();
		Period period = studentStatus.getPeriod();
		
		String sql = "";
		int hasGrade = 1;
		//	

		//default marking scheme
		String markingId = subject.getMarkingGrade().getId();
		
		//look for specific marking scheme
		String programId = student.getProgram().getId();
		SubjectGrade subjectGrade = (SubjectGrade) db.get("select s from SubjectGrade s where s.program.id = '" + programId + "' and s.subject.id = '" + subjectId + "'");
		if ( subjectGrade != null ) {
			markingId = subjectGrade.getMarkingGrade() != null ? subjectGrade.getMarkingGrade().getId() : markingId;
		} 
		if ( markingId != null ) {
			//final subject result
			sql = "select r from FinalResult r " +
					" where r.student.id = '" + studentId + "' " +
					" and r.session.id = '" + sessionId + "' " +
					" and r.period.id = '" + periodId + "' ";
			FinalResult result = (FinalResult) db.get(sql);
						
			if ( result == null ) {
				hasGrade = 0;
				db.begin();
				result = new FinalResult(session.getId(), student.getId());
				result.setStudent(student);
				result.setPeriod(period);
				result.setSession(session);
				result.setTime(new Date());
				result.setCreated(new Date());

				FinalSubjectResult f = new FinalSubjectResult();
				f.setSubject(subject);
				result.addSubject(f);

				db.persist(result);
				db.commit();

			}
			
			if ( result != null ) {

				FinalSubjectResult r = result.getSubject(subjectId);
				
				if ( r == null ) {
					hasGrade = 0;

					db.begin();
					FinalSubjectResult f = new FinalSubjectResult();
					f.setSubject(subject);
					result.addSubject(f);
					db.persist(f);
					db.commit();
					
					r = result.getSubject(subjectId);
				}
				
				if ( r != null ) {
					
					//System.out.println("subject = " + subject.getCode());
					//System.out.println("set exclude gpa = " + subject.getExcludeGpa());
					
					System.out.println("Subject Status = " + subjectStatus);
					if ( SpecialSubjectStatus.isMatch(subjectStatus)) { 
						System.out.println("This is special subject status");
						db.begin();
						r.setGrade(subjectStatus);
						r.setGradePoint(0.0d);
						r.setPoint(0.0d);
						r.setSubjectStatus(subjectStatus);
						r.setCreditHour(r.getSubject().getCredithrs());
						r.setExcludeGPA(1);
						db.commit();
						//System.out.println("set exclude GPA = 1" );
					} else {
						if ( r.getGrade() == null || "".equals(r.getGrade())) hasGrade = 0;
						Grade grade = ExamResultUtil.getGrade(markingId, mark);
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
						r.setTotalMark(mark);
						r.setCreditHour(r.getSubject().getCredithrs());
						r.setSubjectStatus(subjectStatus);
						
						if ( r.getResultStatus() != null ) {
							
							//System.out.println("Got Result Status = " + r.getResultStatus().getName());
							//System.out.println("exclude cgpa = " + r.getResultStatus().getExcludeGPA());
							
							if ( r.getResultStatus().getExcludeGPA() ) {
								r.setExcludeGPA(1);
							}
							else {
								r.setExcludeGPA(0);
							}
						}
						else {
							r.setExcludeGPA(0);
						}
						db.commit();
						//System.out.println("set exclude gpa = 0");
					}
					
					//System.out.println(subject.getCode() + " result exclude gpa = " + r.getExcludeGPA());
					
					calculateResultGPA(db, result, calculateGPA);

					if ( calculateGPA ) {
						//calculate CGPA each time mark saved
						ResultEntryUtil.calculateCGPA(db, result);
						//calculate rest of cgpa
						ResultEntryUtil.calculateRestOfCGPA(db, student, result.getSession());
						//CALCULATE STANDING
						String programLevelType = student.getProgram().getProgramLevelType();
						ResultEntryUtil.calculateStanding(db, student.getId(), programLevelType);
					}
					
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
		
		return hasGrade;
		

	}


	public static void calculateResultGPA(DbPersistence db, FinalResult result, boolean calculateGPA) throws Exception {
		//calculate GPA
		double hours = 0.0;
		double point = 0.0;
		double taken = 0.0;
//		System.out.println(result.getSession().getName());
		if ( result != null ) {
			for ( FinalSubjectResult sr : result.getSubjects() ) {
				if ( sr.getResultStatus() != null ) {
					if ( !sr.getResultStatus().getExcludeGPA() ) {
						if ( sr.getSubject().getExcludeGpa() == 0 && sr.getExcludeGPA() == 0 ) {
							hours += sr.getCreditHour();
							point += sr.getPoint();
						}
					}
				}
				else {
					if ( sr.getSubject().getExcludeGpa() == 0 && sr.getExcludeGPA() == 0 ) {
						hours += sr.getCreditHour();
						point += sr.getPoint();
					}
				}
				taken += sr.getCreditHour();
			}
			
			double gpa = hours > 0 ? point / hours : 0;
			
			db.begin();
			result.setCurrentPoints(point);
			result.setCurrentHours(hours);
			result.setTakenHours(taken);
			result.setGpa(gpa);
			result.setUpdated(true);
			if ( calculateGPA ) result.setNeedCalculateCGPA(false);
			else result.setNeedCalculateCGPA(true);
			db.commit();
		}
	}
	
	public static void calculateResultCGPA(DbPersistence db, Student student) throws Exception {
		Set<StudentStatus> statusSet = student.getStatus();
		List<StudentStatus> statusList = new ArrayList<StudentStatus>();
		if ( statusSet != null ) {
			statusList.addAll(statusSet);
			Collections.sort(statusList, new StatusComparator());
			for ( StudentStatus st : statusList ) {
				if ( st.getSession() != null ) {
					String sql = "select r from FinalResult r " +
					" where r.student.id = '" + st.getStudent().getId() + "' " +
					" and r.session.id = '" + st.getSession().getId() + "' " +
					" and r.period.id = '" + st.getPeriod().getId() + "' ";
					FinalResult result = (FinalResult) db.get(sql);
					if ( !result.getUpdated() ) {
						ResultEntryUtil.calculateResultGPA(db, result, true);
					}
				} else {
					System.out.println("ResultEntryUtil: session is null " + st.getStudent().getMatricNo());
				}
			}
		} else {
			System.out.println("Result is null");
		}
		
		ResultEntryUtil.calculateAllCGPA(db, student);
	}	

	public static int nonNumericalMark(String m) {
		//if ( "A+".equals(m) ) return -1;
		int i = 0;
		int cnt = 0;
		for ( String s : nonNumericValues ) {
			cnt++;
			if ( s.equals(m)) {
				i = cnt;
				break;
			}
		}
		
		return -i;
	}

	public static String nonNumericalDisplay(double mark) {
		String s = "";
		//if ( mark == -1 ) return "A+";
		int i = (int) (-1 * mark);
		s = nonNumericValues[i-1];
		return s;
	}	
	
	public static void recalculateResult(DbPersistence db, String matricNo) throws Exception {
		List<Student> list = db.list("select s from Student s where s.matricNo = '" + matricNo + "'");
		if ( list.size() == 0 ) {
			System.out.println("Student " + matricNo + " NOT FOUND!");
			return;
		}
		Student student = (Student) list.get(0);
		System.out.println("Name: " + student.getBiodata().getName());
		recalculateResult(db, student);
	}

	
	public static void recalculateResult(DbPersistence db, Student student) throws Exception {
		
		recalculateResult(db, student, false);
		
	}
	
	public static void recalculateResult(DbPersistence db, Student student, boolean excludeGradeF) throws Exception {
		StudentStatusUtil u = new StudentStatusUtil();
		List<StudentStatus> statuses = u.getStudentStatuses(student);
		for (StudentStatus status : statuses) {
			if ( status.getPeriod() != null ) {
				
				//if ( !status.getType().getDefer() ) { //MUST NOT DEFER STATUS
					
					FinalResult result = ResultEntryUtil.recalculateResult(db, status, excludeGradeF);
					if ( result != null ) {
						//CALCULATE GPA FOR CURRENT SEMESTER
						ResultEntryUtil.calculateCGPA(db, result, excludeGradeF);
						//RECALCULATE CGPA FOR ALL SEMESTERS
						ResultEntryUtil.calculateRestOfCGPA(db, student, result.getSession(), excludeGradeF);
						//CALCULATE STANDING
						String programLevelType = student.getProgram().getProgramLevelType();
						ResultEntryUtil.calculateStanding(db, student.getId(), programLevelType);
					}
				//}
				
			} else {
				System.out.println(student.getMatricNo() + ": Period is null!");
			}
		}
	}
	
	public static void recalculateResult(String matricNo) throws Exception {
		DbPersistence db = new DbPersistence();
		ResultEntryUtil.recalculateResult(db, matricNo);
	}
	
	public static FinalResult recalculateResult(DbPersistence db, StudentStatus status) throws Exception {
		
		return recalculateResult(db, status, false);
		
	}
	
	public static FinalResult recalculateResult(DbPersistence db, StudentStatus status, boolean excludeGradeF) throws Exception {
		
		System.out.println("recalculate result for " + status.getStudent().getBiodata().getName());
		
		if ( status.getType().getDefer() ) {
			
			String sql = "select r from FinalResult r " +
					" where r.student.id = '" + status.getStudent().getId() + "' " +
					" and r.session.id = '" + status.getSession().getId() + "' " +
					" and r.period.id = '" + status.getPeriod().getId() + "' ";
			FinalResult result = (FinalResult) db.get(sql);
			if ( result != null ) {
				db.begin();
				db.remove(result);
				db.commit();
			}
			
			System.out.println("this is defer status.. return null");
			
			return null;
		}
		else {
			String sql;
			//final subject result
			sql = "select r from FinalResult r " +
					" where r.student.id = '" + status.getStudent().getId() + "' " +
					" and r.session.id = '" + status.getSession().getId() + "' " +
					" and r.period.id = '" + status.getPeriod().getId() + "' ";
			FinalResult result = (FinalResult) db.get(sql);
	
			Student student = status.getStudent();
			
			if ( result != null ) {
				
				double hours = 0.0;
				double point = 0.0;
				
				for ( FinalSubjectResult r : result.getSubjects() ) {
									
					double mark = r.getTotalMark();
					
					String markingId = "";
					Grade grade = null;
					String programId = student.getProgram().getId();
					sql = "select s from SubjectGrade s where s.program.id = '" + programId + "' and s.subject.id = '" + r.getSubject().getId() + "'";
					SubjectGrade subjectGrade = (SubjectGrade) db.get(sql);
					if ( subjectGrade != null ) {
						System.out.println("MARKING GRADE BY PROGRAM = ");
						markingId = subjectGrade.getMarkingGrade() != null ? subjectGrade.getMarkingGrade().getId() : markingId;
						grade = !"".equals(markingId) ? ExamResultUtil.getGrade(markingId, mark) : null;
					} else {
						System.out.println("MARKING GRADE BY PROGRAM FOR " + r.getSubject().getCode() + " IS NULL!!.  Revert to DEFAULT MARKING GRADE BY SUBJECT. ");
						markingId = r.getSubject().getMarkingGrade() != null ? r.getSubject().getMarkingGrade().getId() : "";
						grade = !"".equals(markingId) ? ExamResultUtil.getGrade(markingId, mark) : null;
					}
					if ( grade == null ) {
						//STILL NULL... NEED TO DO SOMETHING
						System.out.println("grade for is null for" + r.getSubject().getCode() + ", now getting DEFAULT marking grade");
						grade = ExamResultUtil.getDefaultGrade(mark);
					}
					
					if ( grade != null ) {
						db.begin();
						r.setGrade(grade.getLetter());
						r.setGradePoint(grade.getPoint());
						r.setPoint(grade.getPoint() * r.getSubject().getCredithrs());
						r.setTotalMark(mark);
						r.setCreditHour(r.getSubject().getCredithrs());
						db.commit();
					}
					
					boolean calculateGPA = true;
					if ( r.getResultStatus() != null ) {
						
						db.begin();
						if ( r.getResultStatus().getExcludeGPA() ) {
							r.setExcludeGPA(1);
							calculateGPA = false;
						}
						else {
							r.setExcludeGPA(0);
							calculateGPA = true;
						}
						db.commit();
					
					}
					
					if ( SpecialSubjectStatus.isMatch(r.getSubjectStatus()))  {
						db.begin();
						r.setExcludeGPA(1);
						r.setGrade(r.getSubjectStatus());
						calculateGPA = false;
						db.commit();
					}
						
					
					if ( excludeGradeF && "F".equals(r.getGrade())) {
						calculateGPA = false;
					}
					
					
					
					if ( calculateGPA ) {
						hours += r.getSubject().getCredithrs();
						point += r.getPoint();
						
					} else {
						//System.out.println("skip gpa calculation for " + r.getSubject().getCode());
					}
					
				}

				
				db.begin();
				result.setCurrentHours(hours);
				if ( hours > 0 ) {
					double gpa = point / hours;
					result.setGpa(gpa);
				}
				else {
					result.setGpa(0);
				}
				db.commit();
				
				
				
				return result;
			}
			else {
				System.out.println("Result is null for " + status.getSession().getName());
				return null;
			}
		}

	}
	

	/*
	 * CREATE SUBJECT RESULT when UPDATING SUBJECT STATUS TO CREDIT TRANSFER
	 */
	public static SubjectResult createSubjectResult(DbPersistence db, StudentStatus studentStatus, Subject subject) throws Exception {
		
		SubjectResult subjectResult = null;
		String sql = "select su from ExamResult e JOIN e.sessions sr JOIN sr.subjects su " +
		"where sr.session.id = '" + studentStatus.getSession().getId() + "' " +
		"and su.subject.id = '" + subject.getId() + "' " +
		"and e.student.id = '" + studentStatus.getStudent().getId() + "'";
		List<SubjectResult> results = db.list(sql);
		if ( results.size() == 0 ) { //subjectResult not exist
			Student student = studentStatus.getStudent();
			Session session = studentStatus.getSession();
			Period period = studentStatus.getPeriod();
			
			subjectResult = createSubjectResult(db, student, session, period, subject);			
		}
		else {
			subjectResult = results.get(0);
		}	
		return subjectResult;
	}
	
	private static SubjectResult createSubjectResult(DbPersistence db, Student student, Session session, Period period, Subject subject) throws Exception {
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
			sessionResult = createSessionResult(db, student, session, period);
		}
		else {
			sessionResult = sessions.get(0);
		}
		
		
		db.begin();
		subjectResult = new SubjectResult();
		subjectResult.setSession(sessionResult);
		subjectResult.setSubject(subject);
		sessionResult.addSubject(subjectResult);
		db.commit();
		
		return subjectResult;
	}	
	
	private static SessionResult createSessionResult(DbPersistence db, Student student, Session session, Period period) throws Exception {
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
	
	/*
	 * CREATING EXAM SUBJECT DATA
	 */
	public static void createExamSubject(DbPersistence db, StudentStatus studentStatus, Subject subject) throws Exception {
		
		String sql = "select s from FinalResult r join r.subjects s" +
		" where r.student.id = '" + studentStatus.getStudent().getId() + "' " +
		" and r.session.id = '" + studentStatus.getSession().getId() + "' " +
		" and r.period.id = '" + studentStatus.getPeriod().getId() + "' " +
		" and s.subject.id = '" + subject.getId() + "'";

		FinalSubjectResult result = (FinalSubjectResult) db.get(sql);
		
	}
	
}
