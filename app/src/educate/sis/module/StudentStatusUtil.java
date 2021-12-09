package educate.sis.module;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import educate.db.DbPersistence;
import educate.enrollment.entity.StatusType;
import educate.enrollment.entity.Student;
import educate.enrollment.entity.StudentStatus;
import educate.enrollment.entity.StudentSubject;
import educate.enrollment.entity.StudentSubjectTemp;
import educate.sis.struct.entity.Period;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.ProgramStructure;
import educate.sis.struct.entity.Session;
import educate.sis.struct.entity.Subject;
import educate.sis.struct.entity.SubjectPeriod;
import educate.sis.struct.entity.SubjectReg;

/**
 * 
 * @author Shamsul Bahrin Abd Mutalb
 *
 * @version 1.0
 */

public class StudentStatusUtil {
	
	private DbPersistence db = new DbPersistence();
	private boolean isCreateAll = true;
	private boolean isDeleteAll = true;
	private boolean useSubjectTemp = false;
	
	public void setUseSubjectTemp(boolean use) {
		useSubjectTemp = use;
	}
	
	public boolean getUseSubjectTemp() {
		return useSubjectTemp;
	}
	
	public List<StudentStatus> getStudentStatuses(Student student) {
		Set<StudentStatus> statuses = student.getStatus();
		List<StudentStatus> list = new ArrayList<StudentStatus>();
		if ( statuses != null ) {
			list.addAll(statuses);
			Collections.sort(list);
		}
		return list;
	}
	
	public StudentStatus getFirstStudentStatus(Student student) throws Exception {
		StudentStatus status = null;
		int i = 0;
		List<StudentStatus> statuses = getStudentStatuses(student);
		status = statuses.get(0);
		return status;
	}
	
	public StudentStatus getCurrentStudentStatus(Student student) throws Exception {
		if ( student.getProgram() == null ) {
			System.out.println("NULL: Student has no Program Of Study");
			return null;
		}
		if ( student.getProgram().getLevel() == null ) {
			System.out.println("NULL: Program of Study has no Level");
			return null;
		}
		
		if ( student.getProgram().getIsNoneSessionType() ) {
			return getCurrentStudentStatus2(student);
		}
		
		Session session = getCurrentSession(student.getProgram().getLevel().getPathNo());
		if ( session == null ) {
			System.out.println("Can not find current Study Session");
			return getLastStudentStatus(student);
		}
		return getStudentStatus(student, session);
	}
	
	public StudentStatus getCurrentStudentStatus2(Student student) {
		StudentStatus status = null;
		Set<StudentStatus> statuses = student.getStatus();
		for ( StudentStatus st : statuses ) {
			if ( st.getIsCurrent() ) {
				status = st;
				break;
			}
		}
		return status;
	}
	
	
	public StudentStatus getLastStudentStatus(Student student) throws Exception {
		Set<StudentStatus> statuses = student.getStatus();
		if ( statuses.size() > 0 ) {
			List<StudentStatus> list = new ArrayList<StudentStatus>();
			list.addAll(statuses);
			Collections.sort(list, new ComparatorByDate());
			StudentStatus status = list.get(0);
			status.setPastStatus(true);
			return status;
		}
		else {
			System.out.println("WARNING: Student Status not set for " + student.getMatricNo());
			return null;
		}
	}
	
	
	
	public StudentStatus getStudentStatus(Student student, Session session) throws Exception {
		Set<StudentStatus> statuses = student.getStatus();
		StudentStatus status = null;
		for( StudentStatus ss : statuses ){
			if( ss.getSession().getId().equals(session.getId()) ){
				status = ss;
				break;
			}
		}
		if ( status == null ) {
			System.out.println("Status is null... probably student has finished study... getting it last status");
			status = getLastStudentStatus(student);
		}
		return status;
	}
	
	public void createStatuses(Student student) throws Exception {
		ProgramUtil pu = new ProgramUtil();
		StudentStatus status = pu.getStudentStatus(student, student.getIntake());
		deleteStatus(status);
		addStatus(student, student.getIntake());
	}
	
	public void createOrUpdateStatuses(Student student) throws Exception {
		ProgramUtil pu = new ProgramUtil();
		Period currentPeriod = null;
		Session session = student.getIntake();
		boolean addSubjects = true;
		//if this session equals to student's intake, then this is the first semester
		if ( session.getId().equals(student.getIntake().getId())) {
			//then current period is the first period in list
			List<Period> periods = student.getProgram().getPeriodScheme().getFunctionalPeriodList();
			currentPeriod = periods.get(0);
		}
		else {
			currentPeriod = getCurrentPeriod(student, session);
		}

		if ( currentPeriod != null ) {
			StatusType type = pu.getDefaultStatus();
			if ( !isCreateAll ) { //do only for one
				updateStudentStatus(student, session, currentPeriod, type, pu);
			} else { //do for this and the rest of sessions
				
				List<Period> periods = student.getProgram().getPeriodScheme().getFunctionalPeriodList();
				//get all periods to add
				List<Period> periods2 = new ArrayList<Period>();
				boolean getNext = false;
				for ( Period p : periods ) {
					if ( p.getId().equals(currentPeriod.getId())) getNext = true;
					if ( getNext ) periods2.add(p);
				}
				//get all sessions to add
				Hashtable param = new Hashtable();
				param.put("thisDate", session.getStartDate());
				param.put("pathNo", student.getProgram().getLevel().getPathNo());
				List<Session> sessions2 = db.list("select s from Session s where s.pathNo = :pathNo and s.startDate >= :thisDate order by s.startDate", periods2.size(), param);
				
				int i = 0;
				for ( Session s : sessions2 ) {
					Period p = periods2.get(i);
					updateStudentStatus(student, s, p, type, pu);
					i++;
				}
				
				/*
				if ( addSubjects ) {
					for ( Session s : sessions2 ) {
						addStudentSubjects(student, s);
					}
				}
				*/
				
			}
		} else { //currentPeriod is NULL!!
			throw new Exception("Exception in StudentStatusUtil.addStatus() - currentPeriod is NULL!!!");
		}
	}
	
	public void generateStatus(Student student) throws Exception {
		//delete all status first
		db.begin();
		db.executeUpdate("delete from StudentStatus ss where ss.student.id = '" + student.getId() + "'");
		db.commit();
		addStatus(student);
	}
	
	public void addStatus(Student student) throws Exception {
		Session session = student.getIntake();
		addStatus(student, session);
	}
	
	public void addStatus(Student student, Session session) throws Exception {
		addStatus(student, session, false);
	}
	
	/*
	public void addStatus(Student student, Session session, boolean addSubjects) throws Exception {
		addStatus(student, session, null, addSubjects);
	}
	*/
	
	public StudentStatus addStatus(Student student, Session session, boolean addSubjects) throws Exception {
		
		StudentStatus studentStatus = null;
		//what should be current period?
		Period currentPeriod = null;
		
		//Program
		Program program = student.getProgram();
		if ( program == null ) {
			throw new Exception("Exception in StudentStatusUtil.addStatus() - Program of Study is NULL!!!");
		}
		
		//if this session equals to student's intake, then this is the first semester
		if ( session.getId().equals(student.getIntake().getId())) {
			//then current period is the first period in list
			List<Period> periods = student.getProgram().getPeriodScheme().getFunctionalPeriodList();
			currentPeriod = periods.get(0);
		}
		else {
			currentPeriod = getCurrentPeriod(student, session);
		}
		
		//currentPeriod must not be null
		//
		if ( currentPeriod != null ) {
						
			ProgramUtil pu = new ProgramUtil();
			StatusType type = pu.getDefaultStatus();
			if ( !isCreateAll ) { //do only for one
				studentStatus = addNewStudentStatus(student, session, currentPeriod, type, pu);
			} else { //do for this and the rest of sessions
				
				List<Period> periods = student.getProgram().getPeriodScheme().getFunctionalPeriodList();
				//get all periods to add
				List<Period> periods2 = new ArrayList<Period>();
				//begin from current period
				boolean getNext = false;
				for ( Period p : periods ) {
					if ( p.getId().equals(currentPeriod.getId())) getNext = true;
					if ( getNext ) periods2.add(p);
				}
				
				//match periods to sessions
				Hashtable param = new Hashtable();
				param.put("thisDate", session.getStartDate());
				param.put("pathNo", student.getProgram().getLevel().getPathNo());
				List<Session> sessions2 = db.list("select s from Session s where s.pathNo = :pathNo and s.startDate >= :thisDate order by s.startDate", periods2.size(), param);
				
				int i = 0;
				for ( Session s : sessions2 ) {
					Period p = periods2.get(i);
					StudentStatus status = addNewStudentStatus(student, s, p, type, pu);
					if ( i == 0 ) {
						studentStatus = status;
					}
					i++;
				}
				
				
				//AUTOMATICALLY ADD SUBJECTS TO THIS SEMESTER
				
				if ( addSubjects ) {
					for ( Session s : sessions2 ) {
						addStudentSubjects(student, s);
					}
				}
				
				
			}
			
			//RESTORE STUDENT'S DATA IF EXIST OLD DATA
			restoreStudentData(student);
			
			return studentStatus;
			
			
		} else { //currentPeriod is NULL!!
			throw new Exception("Exception in StudentStatusUtil.addStatus() - currentPeriod is NULL!!!");
		}
	}
	
	
	public StudentStatus addStatus2(Student student, Session session, Period period, boolean addSubjects) throws Exception {
		
		StudentStatus studentStatus = null;
		//what should be current period?
		Period currentPeriod = null;
		
		currentPeriod = period;
		
		//currentPeriod must not be null
		//
		if ( currentPeriod != null ) {
			ProgramUtil pu = new ProgramUtil();
			StatusType type = pu.getDefaultStatus();
			if ( !isCreateAll ) { //do only for one
				studentStatus = addNewStudentStatus(student, session, currentPeriod, type, pu);
			} else { //do for this and the rest of sessions
				
				List<Period> periods = student.getProgram().getPeriodScheme().getFunctionalPeriodList();
				//get all periods to add
				List<Period> periods2 = new ArrayList<Period>();
				boolean getNext = false;
				for ( Period p : periods ) {
					if ( p.getId().equals(currentPeriod.getId())) getNext = true;
					if ( getNext ) periods2.add(p);
				}
				//get all sessions to add
				Hashtable param = new Hashtable();
				param.put("thisDate", session.getStartDate());
				param.put("pathNo", student.getProgram().getLevel().getPathNo());
				List<Session> sessions2 = db.list("select s from Session s where s.pathNo = :pathNo and s.startDate >= :thisDate order by s.startDate", periods2.size(), param);
				
				
				int i = 0;
				for ( Session s : sessions2 ) {
					Period p = periods2.get(i);
					StudentStatus status = addNewStudentStatus(student, s, p, type, pu);
					if ( i == 0 ) {
						studentStatus = status;
					}
					i++;
				}
			}
			
			//RESTORE STUDENT'S DATA IF EXIST OLD DATA
			restoreStudentData(student);
			
			return studentStatus;
			
			
		} else { //currentPeriod is NULL!!
			throw new Exception("Exception in StudentStatusUtil.addStatus() - currentPeriod is NULL!!!");
		}
		
	}


	private Period getCurrentPeriod(Student student, Session session) {
		
		System.out.println("Session = " + session.getCode());

		Period currentPeriod = null;
		//what is last session and last period?
		Session lastSession = null;
		Period lastPeriod = null;
		Hashtable param = new Hashtable();
		param.put("dateStart", student.getIntake().getStartDate());
		param.put("thisDate", session.getStartDate());
		param.put("pathNo", student.getProgram().getLevel().getPathNo());
		List<Session> sessions = db.list("select s from Session s where s.pathNo = :pathNo " +
				"and s.startDate >= :dateStart " +
				"and s.startDate < :thisDate " +
				"order by s.startDate DESC", 1, param);
		
		if ( sessions.size() == 1 ) {
			lastSession = sessions.get(0);  //the purpose for lastSession is to get last Period, so we can get what is current period
			//get last student status
			ProgramUtil pu = new ProgramUtil();
			StudentStatus status = pu.getStudentStatus(student, lastSession);
			lastPeriod = status.getPeriod();
			//what should be current period?
			List<Period> periods = student.getProgram().getPeriodScheme().getFunctionalPeriodList();
			boolean found = false;
			for ( Period p : periods ) {
				if ( found ) {
					currentPeriod = p;
					break;
				}
				if ( p.getId().equals(lastPeriod.getId())) {
					found = true;
				}
			}
		}
		return currentPeriod;
	}
	
	private void addStudentSubjects(Student student, Session session) throws Exception {

		StudentStatus status = getStudentStatus(student, session);
		Period period = status.getPeriod();

		
		if ( student.getProgram() == null ) {
			System.out.println("CAN'T ADD SUBJECTS BECAUSE PROGRAM IS NULL");
			return;
		}
		if ( student.getIntake() == null ) {
			System.out.println("CAN'T ADD SUBJECTS BECAUSE INTAKE IS NULL");
			return;
		}
		if ( student.getLearningCenter() == null ) {
			System.out.println("CAN'T ADD SUBJECTS BECAUSE LEARNING CENTRE IS NULL");
			return;
		}
		
		//ADD SUBJECTS FROM TEMPORARY STUDENT SUBJECTS IF EXIST
		if ( useSubjectTemp ) {
			List<StudentSubjectTemp> temps = db.list("select t from StudentSubjectTemp t where t.student.id = '" + student.getId() + "'");
			for ( StudentSubjectTemp temp : temps ) {
				if ( temp.getPeriod().getId().equals(status.getPeriod().getId())) {
					
					db.begin();
					Subject s = temp.getSubject();
					StudentSubject ss = new StudentSubject();
					ss.setSubject(s);
					ss.setSubjectStatus("RG");
					ss.setSection(temp.getSection());
					status.getStudentSubjects().add(ss);
					ss.setStudentStatus(status);
					db.persist(ss);
					
					db.commit();
				}
			}
		}
		
		//ADD SUBJECTS FROM PROGRAM STRUCTURE
		if ( !useSubjectTemp ) {
			
			System.out.println("REGISTRATION: Adding Subjects from Program Structure:");
			
			Hashtable param = new Hashtable();
			param.put("program", student.getProgram());
			param.put("intake", student.getIntake());
			param.put("centre", student.getLearningCenter());
			
			List<ProgramStructure> pss = db.list("select ps from ProgramStructure ps where ps.program = :program " +
					" and ps.session = :intake and ps.learningCenter = :centre", param);
			ProgramStructure ps = null;
			if ( pss.size() > 0 ) ps = pss.get(0);
			
			if ( ps != null ) {
				//SubjectSection section = getSection(student.getLearningCenter().getId());
				
				SubjectPeriod subjectPeriod = ps.getSubjectPeriodByPeriodId(period.getId());
				if ( subjectPeriod != null ) {
					if ( status.getStudentSubjects() == null ) status.setStudentSubjects(new HashSet<StudentSubject>());
					Set<SubjectReg> subjectRegs = subjectPeriod.getSubjectRegs();
					for ( SubjectReg sr : subjectRegs ) {
						if ( sr.getCategory() != null ) {
							if ( sr.getCategory().getMandatory() ) {
								Subject s = sr.getSubject();
								if ( status.getStudentSubject(s) == null ) {
									System.out.println("adding subject " + s.getName());
									db.begin();
									
									
									StudentSubject ss = new StudentSubject();
									ss.setSubject(s);
									ss.setSubjectStatus("RG");
									//ss.setSection(section);
									status.getStudentSubjects().add(ss);
									ss.setStudentStatus(status);
									db.persist(ss);
									
									db.commit();
								}
								
							}
						}
					}
					
				}
			}
		}
	}
	
	/*
	public SubjectSection getSection(String centreId) throws Exception{
		SubjectSection section = null;
		List<SubjectSection> sections = db.list("SELECT a FROM SubjectSection a WHERE a.learningCentre.id='" + centreId + "'");
		if( sections.size() > 0 ) section = sections.get(0);
		return section;
	}
	*/
	public void updateStatus(StudentStatus status, StatusType type) throws Exception {
		Student student = status.getStudent();
		if ( !type.getDefer() ) {
			db.begin();
			status.setType(type);
			db.commit();
		}
		else if ( type.getDefer() ) {
			Period currentPeriod = status.getPeriod();
			Session session = status.getSession();
			List<Period> periods = new ArrayList<Period>(); //student.getProgram().getPeriodScheme().getFunctionalPeriodList();
			
			StudentStatusUtil u = new StudentStatusUtil();
			List<StudentStatus> statuses = u.getStudentStatuses(student);
			for ( StudentStatus s : statuses ) {
				periods.add(s.getPeriod());
			}
			
			//get all periods to add
			List<Period> periods2 = new ArrayList<Period>();
			boolean getNext = false;
			for ( Period p : periods ) {
				if ( p.getId().equals(currentPeriod.getId())) {
					periods2.add(p);
					getNext = true;
				}
				if ( getNext ) periods2.add(p);
			}
			//get all sessions to add
			Hashtable param = new Hashtable();
			param.put("thisDate", session.getStartDate());
			param.put("pathNo", student.getProgram().getLevel().getPathNo());
			List<Session> sessions2 = db.list("select s from Session s where s.pathNo = :pathNo and s.startDate > :thisDate order by s.startDate", periods2.size(), param);

			
			ProgramUtil pu = new ProgramUtil();
			//StatusType defaultType = pu.getDefaultStatus();
			//List<StudentStatus> statuses = pu.getStudentStatuses(student);
			
			
			getNext = false;
			int i = 0;
			if ( statuses != null ) {
				for (StudentStatus s : statuses ) {
					if ( s.getSession().getId().equals(session.getId())) getNext = true;
					if ( getNext ){
						Session nextSession = sessions2.get(i);
						i++;
						db.begin();
						s.setSession(nextSession);
						db.commit();
					}
				}
			}
			
			//for current deffered session
			addNewStudentStatus(student, session, currentPeriod, type, pu);

		}
	}
	
	public void deleteStatus(StudentStatus status) throws Exception {
		if ( status == null ) return;
		Student student = status.getStudent();
		Set<StudentStatus> statuses = student.getStatus();
		if ( isDeleteAll ) {
			//remove rest of statuses after this status
			System.out.println("remove rest of statuses.");
			List<StudentStatus> removeList = new ArrayList<StudentStatus>();
			if ( statuses != null ) {
	
				for ( StudentStatus s : statuses ) {
					System.out.println(s);
					if ( s.getSession().getStartDate().after(status.getSession().getStartDate())) {
						removeList.add(s);
					}
				}
				
				//copy to temporary student subjects
				//copyStudentSubjectsToTemp(student, removeList);
				
				System.out.println("delete from database");
				for ( StudentStatus s : removeList ) {
					db.begin();
					db.remove(s);
					db.commit();
					
					statuses.remove(s);
				}
			}
		}
		
		//remove selected status
		db.begin();
		db.remove(status);
		db.commit();
		
		statuses.remove(status);
	}
	
	
	private StudentStatus addNewStudentStatus(Student student, Session session, Period period, StatusType type, ProgramUtil pu) throws Exception {
		Set<StudentStatus> statuses = student.getStatus();
		db.begin();
		StudentStatus status = new StudentStatus();
		status.setStudent(student);
		status.setSession(session);
		status.setPeriod(period);
		status.setType(type);
		statuses.add(status);
		db.commit();
		
		return status;
	}
	
	private StudentStatus updateStudentStatus(Student student, Session session, Period period, StatusType type, ProgramUtil pu) throws Exception {
		StudentStatus status = pu.getStudentStatus(student, session);
		if ( status != null ) {
			db.begin();
			status.setSession(session);
			status.setPeriod(period);
			status.setType(type);
			db.commit();
		}
		else {
			Set<StudentStatus> statuses = student.getStatus();
			db.begin();
			status = new StudentStatus();
			status.setStudent(student);
			status.setSession(session);
			status.setPeriod(period);
			status.setType(type);
			statuses.add(status);
			db.commit();
		}
		
		return status;
	}
	
	public void updateCurrentStudentStatus(Student student, StudentStatus currentStatus) throws Exception {
		db.begin();
		//first remove current status flag from all statuses
		Set<StudentStatus> statuses = student.getStatus();
		
		for ( StudentStatus status : statuses ) {
			if ( status.getId().equals(currentStatus.getId())) {
				status.setIsCurrent(true);
			} else {
				status.setIsCurrent(false);
			}
		}
		db.commit();
	}
	
	public Session getSessionByDate(Date date, int pathNo) throws Exception {
		Session session = null;
		Hashtable h = new Hashtable();
		h.put("date", date);
		List<Session> list = db.list("select s from Session s where (:date BETWEEN s.startDate AND s.endDate) AND s.pathNo="+pathNo, h);
		if ( list.size() == 0 ) return null;
		session = list.get(0);
		return session;
	}
	
	public Session getCurrentSession(int pathNo) throws Exception {
		Calendar cal = new GregorianCalendar();
		Date date = cal.getTime();
		return getSessionByDate(date,pathNo);
	}	
	
	public void cleanUpStudentStatuses(Student student) throws Exception {
		Set<StudentStatus> statuses = student.getStatus();
		if ( statuses != null ) {
			List<StudentStatus> removeList = new ArrayList<StudentStatus>();
			for ( StudentStatus s : statuses ) {
				removeList.add(s);
			}
			copyStudentSubjectsToTemp(student, removeList);
			
			for ( StudentStatus s : removeList ) {
				db.begin();
				db.remove(s);
				db.commit();
				
				statuses.remove(s);
			}
			//perform 'manual' cleanup to remove all non-integrated data
			String sql = "delete from StudentStatus s where s.student.id = '" + student.getId() + "'";
			db.begin();
			db.executeUpdate(sql);
			db.commit();
		}
		
	}
	
	public void cleanUpStudentStatuses(String studentId) throws Exception {
		
		Student student = db.find(Student.class, studentId);
		cleanUpStudentStatuses(student);
		
	}

	public void copyStudentSubjectsToTemp(Student student, List<StudentStatus> statuses) throws Exception {
		
		List<StudentSubjectTemp> temps = db.list("select t from StudentSubjectTemp t where t.student.id = '" + student.getId() + "'");
		
		db.begin();
		//db.executeUpdate("delete from StudentSubjectTemp t where t.student.id = '" + student.getId() + "'");
		for ( StudentStatus s : statuses ) {
			Set<StudentSubject> studentSubjects = s.getStudentSubjects();
			for ( StudentSubject studentSubject : studentSubjects ) {
				
				boolean found = false;
				for ( StudentSubjectTemp t : temps ) {
					if ( t.getSubject().getId().equals(studentSubject.getSubject().getId()) && t.getPeriod().getId().equals(studentSubject.getStudentStatus().getPeriod().getId())) {
						found = true;
						break;
					}
				}
				
				if ( !found ) {
					StudentSubjectTemp temp = new StudentSubjectTemp();
					temp.setPeriod(s.getPeriod());
					temp.setCategory(studentSubject.getCategory());
					//temp.setClassroomSections(studentSubject.getClassroomSections());
					temp.setSection(studentSubject.getSection());
					temp.setStatus(studentSubject.getStatus());
					temp.setStudent(student);
					temp.setSubject(studentSubject.getSubject());
					temp.setSubjectStatus(studentSubject.getSubjectStatus());
					db.persist(temp);
				}
			}
		}
		db.commit();
	}
	
	public void restoreStudentData(Student student) throws Exception {
		
		Map<String, Session> sessionLookup = new HashMap<String, Session>();
		
		Set<StudentStatus> studentStatuses = student.getStatus();
		for ( StudentStatus studentStatus : studentStatuses ) {
			sessionLookup.put(studentStatus.getPeriod().getCode(), studentStatus.getSession());
		}
		
		
		for ( StudentStatus studentStatus : studentStatuses ) {
			List<StudentSubjectTemp> studentSubjectTemps = db.list("select s from StudentSubjectTemp s where s.student.id = '" + student.getId() + "' and s.period.id = '" + studentStatus.getPeriod().getId() + "'");
			db.begin();
			for ( StudentSubjectTemp t : studentSubjectTemps ) {
				StudentSubject su = new StudentSubject();
				su.setCategory(t.getCategory());
				//su.setClassroomSections(t.getClassroomSections());
				su.setSection(t.getSection());
				su.setStatus(t.getStatus());
				su.setStudentStatus(studentStatus);
				su.setSubject(t.getSubject());
				su.setSubjectStatus(t.getSubjectStatus());
				studentStatus.getStudentSubjects().add(su);
			}
			db.commit();
		}
		
	}
	

	public static void main(String[] args) throws Exception {
		StudentStatusUtil u = new StudentStatusUtil();
		DbPersistence db = new DbPersistence();
		List<Student> students = db.list("select s from Student s");
		for ( Student student : students ) {
			if ( student.getFlag() == 0 ) {
				System.out.print(">>" + student.getMatricNo());
				if ( student.getProgram() != null ) {
					System.out.println(", " + student.getProgram().getName());
					u.cleanUpStudentStatuses(student.getId());
					Session session = student.getIntake();
					StudentStatusUtil util = new StudentStatusUtil();
					util.addStatus(student, session);
					System.out.println("Statuses created.");
					System.out.println("");
					
					db.begin();
					student.setFlag(1);
					db.commit();
				} else {
					System.out.println(">>NULL FOR " + student.getMatricNo());
				}
			}
		}
	
	}
	
	
	static class ComparatorByDate implements Comparator {

		public int compare(Object o1, Object o2) {
			StudentStatus s1 = (StudentStatus) o1;
			StudentStatus s2 = (StudentStatus) o2;
			try {
				return s2.getSession().getStartDate().compareTo(s1.getSession().getStartDate());
			} catch ( Exception e ) {
				return 0;
			}
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
