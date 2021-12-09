package educate.enrollment;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.enrollment.entity.StudentStatus;
import educate.enrollment.entity.StudentSubject;
import educate.sis.struct.entity.LearningCentre;
import educate.sis.struct.entity.Period;
import educate.sis.struct.entity.PeriodScheme;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.ProgramStructure;
import educate.sis.struct.entity.Session;
import educate.sis.struct.entity.Subject;
import educate.sis.struct.entity.SubjectPeriod;
import educate.sis.struct.entity.SubjectSection;
import lebah.db.Db;
import lebah.db.PersistenceManager;
import lebah.db.SQLRenderer;

public class StudentRegistrationUtil {
	private static String className = "educate.enrollment.StudentRegistrationUtil";

	public static void register(Student student, String programCode, String sessionCode) throws Exception {
		PersistenceManager pm = new PersistenceManager();

		List<Program> programs = pm.list("select p from Program p where p.code = '" + programCode + "'");
		Program program = null;
		if ( programs.size() == 1 ) program = programs.get(0);
		else throw new Exception("Can't get Program of Study!");
		//get intake session
		List<Session> list = pm.list("select s from Session s where s.code = '" + sessionCode + "'");
		Session intakeSession = null;
		if ( list.size() == 1 ) intakeSession = list.get(0);
		else throw new Exception("Can't get Intake Session");


		//matric number
		String prefix = programCode + "-" + sessionCode;
		String matricNo = MatricNumberUtil.getCurrentNumber(prefix, 4);
		student.setMatricNo(matricNo);
		student.setProgram(program);
		student.setIntake(intakeSession);
		Vector<Period> periodList = new Vector<Period>();
		PeriodScheme periodScheme = student.getProgram().getPeriodScheme();
		//get all period
		List<Period> periods = periodScheme.getFunctionalPeriodList();
		for ( Iterator<Period> it = periods.iterator(); it.hasNext(); ) {
			Period p = it.next();
			periodList.add(p);
		}

		Session intake = student.getIntake();
		//list all sessions begining from this intake
		Hashtable<String, Object> h = new Hashtable<String, Object>();
		h.put("dateStart", intake.getStartDate());
		Vector<Session> sessionList = new Vector<Session>();
		List<Session> sessions = pm.list("select s from Session s where s.startDate >= :dateStart order by s.startDate", h); 
		for ( Iterator<Session> it = sessions.iterator(); it.hasNext(); ) {
			Session session = it.next();
			sessionList.add(session);
		}	
		Set<StudentStatus> statusList = student.getStatus();
		//match period with session
		int i = 0;
		for ( Period p : periodList ) {
			Session s = sessionList.elementAt(i);
			StudentStatus status = new StudentStatus();
			status.setPeriod(p);
			status.setSession(s);
			statusList.add(status);
			i++;
		}		

		student.setStatus(statusList);
		pm.add(student);
		initializeSubjects(student);
		createPortalLogin(student.getMatricNo(), student.getMatricNo(), student.getBiodata().getName());
	}

	public static void registerById(Student student, String programId, String sessionId) throws Exception {
		PersistenceManager pm = new PersistenceManager();
		Program program = (Program) pm.find(Program.class, programId);
		Session intakeSession = (Session) pm.find(Session.class, sessionId);
		//matric number
		String prefix = program.getCode() + "-" + intakeSession.getCode();
		String matricNo = MatricNumberUtil.getCurrentNumber(prefix, 4);
		student.setMatricNo(matricNo);
		student.setProgram(program);
		student.setIntake(intakeSession);
		Vector<Period> periodList = new Vector<Period>();
		PeriodScheme periodScheme = student.getProgram().getPeriodScheme();
		//get all period
		List<Period> periods = periodScheme.getFunctionalPeriodList();
		for ( Iterator<Period> it = periods.iterator(); it.hasNext(); ) {
			Period p = it.next();
			periodList.add(p);
		}

		Session intake = student.getIntake();
		//list all sessions begining from this intake
		Hashtable<String, Object> h = new Hashtable<String, Object>();
		h.put("dateStart", intake.getStartDate());
		Vector<Session> sessionList = new Vector<Session>();
		List<Session> sessions = pm.list("select s from Session s where s.startDate >= :dateStart order by s.startDate", h); 
		for ( Iterator<Session> it = sessions.iterator(); it.hasNext(); ) {
			Session session = it.next();
			sessionList.add(session);
		}	
		Set<StudentStatus> statusList = student.getStatus();
		//match period with session
		int i = 0;
		for ( Period p : periodList ) {
			Session s = sessionList.elementAt(i);
			StudentStatus status = new StudentStatus();
			status.setPeriod(p);
			status.setSession(s);
			statusList.add(status);
			i++;
		}		

		student.setStatus(statusList);
		pm.add(student);
		initializeSubjects(student);
		createPortalLogin(student.getMatricNo(), student.getMatricNo(), student.getBiodata().getName());
	}

	public static void updateRegistration(String studentId, String programId, String sessionId) throws Exception {

		System.out.println("student = " + studentId);
		System.out.println("program_id = " + programId);
		System.out.println("session_id = " + sessionId);

		PersistenceManager pm = new PersistenceManager();
		Program program = (Program) pm.find(Program.class, programId);
		Session intakeSession = (Session) pm.find(Session.class, sessionId);


		Vector<Period> periodList = new Vector<Period>();
		PeriodScheme periodScheme = program.getPeriodScheme();
		//get all period
		List<Period> periods = periodScheme.getFunctionalPeriodList();
		for ( Iterator<Period> it = periods.iterator(); it.hasNext(); ) {
			Period p = it.next();
			periodList.add(p);
		}

		//list all sessions begining from this intake
		Hashtable<String, Object> h = new Hashtable<String, Object>();
		h.put("dateStart", intakeSession.getStartDate());
		Vector<Session> sessionList = new Vector<Session>();
		List<Session> sessions = pm.list("select s from Session s where s.startDate >= :dateStart order by s.startDate", h); 
		for ( Iterator<Session> it = sessions.iterator(); it.hasNext(); ) {
			Session session = it.next();
			sessionList.add(session);
		}	
		Set<StudentStatus> statusList = new HashSet<StudentStatus>();
		//match period with session
		int i = 0;
		for ( Period p : periodList ) {
			Session s = sessionList.elementAt(i);
			StudentStatus status = new StudentStatus();
			status.setPeriod(p);
			status.setSession(s);
			statusList.add(status);
			i++;
		}		

		Student student = (Student) pm.find(Student.class).whereId(studentId).forUpdate();



		try {
			student.setProgram(program);
			student.setIntake(intakeSession);
			student.setStatus(statusList);			
			pm.update();
		} catch ( Exception e ) {
			System.out.println("Rollback in StudentRegistrationUtil.updateRegistration()");
			pm.rollback();
		}

		initializeSubjects(student);
		createPortalLogin(student.getMatricNo(), student.getMatricNo(), student.getBiodata().getName());
	}		

	public static void registerDemo(String matricPrefix, Student student, String programId, String sessionId) throws Exception {


		PersistenceManager pm = new PersistenceManager();
		Program program = (Program) pm.find(Program.class, programId);
		Session intakeSession = (Session) pm.find(Session.class, sessionId);


		//matric number
		String prefix = matricPrefix;
		String matricNo = MatricNumberUtil.getCurrentNumber(prefix, 4);
		student.setMatricNo(matricNo);
		student.setProgram(program);
		student.setIntake(intakeSession);
		Vector<Period> periodList = new Vector<Period>();
		PeriodScheme periodScheme = student.getProgram().getPeriodScheme();
		//get all period
		List<Period> periods = periodScheme.getFunctionalPeriodList();
		for ( Iterator<Period> it = periods.iterator(); it.hasNext(); ) {
			Period p = it.next();
			periodList.add(p);
		}

		Session intake = student.getIntake();
		//list all sessions begining from this intake
		Hashtable<String, Object> h = new Hashtable<String, Object>();
		h.put("dateStart", intake.getStartDate());
		Vector<Session> sessionList = new Vector<Session>();
		List<Session> sessions = pm.list("select s from Session s where s.startDate >= :dateStart order by s.startDate", h); 
		for ( Iterator<Session> it = sessions.iterator(); it.hasNext(); ) {
			Session session = it.next();
			sessionList.add(session);
		}	
		Set<StudentStatus> statusList = student.getStatus();
		//match period with session
		int i = 0;
		for ( Period p : periodList ) {
			Session s = sessionList.elementAt(i);
			StudentStatus status = new StudentStatus();
			status.setPeriod(p);
			status.setSession(s);
			statusList.add(status);
			i++;
		}		

		student.setStatus(statusList);
		pm.add(student);
		initializeSubjects(student);
		createPortalLogin(student.getMatricNo(), student.getMatricNo(), student.getBiodata().getName());
	}	


	public static void updateStudentStatus(String matricNo,int pathNo) throws Exception {
		PersistenceManager pm = new PersistenceManager();
		List<Student> list = pm.list("select s from Student s where s.matricNo = '" + matricNo + "'");
		Student student = list.get(0);
		if ( list.size() == 0 ) 
			throw new Exception("Student not found!");


		//session
		Session intake = student.getIntake();
		//list all sessions begining from this intake
		Hashtable<String, Object> h = new Hashtable<String, Object>();
		h.put("dateStart", intake.getStartDate());
		Vector<Session> sessionList = new Vector<Session>();
		List<Session> sessions = pm.list("select s from Session s where s.startDate >= :dateStart AND s.pathNo="+pathNo+" order by s.startDate", h);
		int totalNumberOfSession = sessions.size();
		System.out.println("["+className+"] Total Session = " + totalNumberOfSession);
		//student status
		Set<StudentStatus> studentStatusList = new HashSet<StudentStatus>();
		//periods
		PeriodScheme ps = student.getProgram().getPeriodScheme();
		List<Period> periods = ps.getFunctionalPeriodList();
		int totalNumberOfPeriod = periods.size();
		System.out.println("["+className+"] period size = "+periods.size());
		int i = 0;
		if (totalNumberOfSession < totalNumberOfPeriod) {
			System.out.println("["+className+"] Not enough sessions available for period structure.");
		} else {
			for ( Iterator<Period> itPeriods = periods.iterator(); itPeriods.hasNext(); ) {
				//System.out.println("["+className+"] session = "+i);

				Period period = itPeriods.next();
				if (period == null) {
					System.out.println("["+className+"] period is NULL.");
				} else {
					System.out.println("["+className+"] period = " + period.getParent().getName() + " - " + period.getName());
					//System.out.println("["+className+"] period [parent] = " + period.getParent().getName());
					//System.out.println("["+className+"] period [child] = " + period.getName());
					StudentStatus status = new StudentStatus();
					status.setPeriod(period);

					Session session = sessions.get(i);
					System.out.println("["+className+"] session id = "+session.getId() + ", session name = " + session.getName());
					//status.setSession(sessions.get(i));
					status.setSession(session);

					studentStatusList.add(status);
				}
				i++;
			}
			String id = student.getId();
			student = (Student) pm.find(Student.class).whereId(id).forUpdate();
			try{
				student.setStatus(studentStatusList);
				pm.update();
			}catch( Exception e ){
				pm.rollback();
			}
		}
	}




	public static void initializeSubjects(String matricNo) throws Exception {
		initializeSubjects(getStudent(matricNo));
	}

	public static void initializeSubjects2(String matricNo) throws Exception {
		initializeSubjects2(getStudent(matricNo));
	}

	public static void initializeSubjects(Student student) throws Exception {

		System.out.println("Initialize subjects for " + student.getBiodata().getName());

		PersistenceManager pm = new PersistenceManager();
		Program program = student.getProgram();
		Session intake = student.getIntake(); 

		List<ProgramStructure> psList = pm.list("select ps from ProgramStructure ps where ps.program.id = '" + program.getId() + "' and ps.session.id = '" + intake.getId() + "'");
		if ( psList.size() == 0 ) throw new Exception("Program Structure NOT AVAILABLE!");

		ProgramStructure ps = psList.get(0);
		Set<SubjectPeriod> subjectPeriodList = ps.getSubjectPeriod();

		Set<StudentStatus> studentStatusList = student.getStatus();
		//Set<StudentStatus> studentStatusList2 = new HashSet<StudentStatus>();

		System.out.println("student status list = " + studentStatusList.size());

		for ( Iterator<StudentStatus> it = studentStatusList.iterator(); it.hasNext(); ) {
			StudentStatus status = it.next();
			Period period = status.getPeriod();

			List<SubjectPeriod> listSP = pm.list("select s from SubjectPeriod s where s.programStructure.program.id = '" + program.getId() + 
					"' and s.programStructure.session.id = '" + intake.getId() + "' and s.period.id = '" + period.getId() + "'");

			if ( listSP.size() > 0 ) {
				SubjectPeriod sp = listSP.get(0);
				//status.setSubjects(sp.getSubjects());
				System.out.println((period.getParent() != null ? period.getParent().getName() : " ") + period.getName() + ", " + sp.getSubjects().size());
			}
			else {
				System.out.println(period.getName() + ", NOT DEFINED");
			}
		}

		String id = student.getId();

		student = (Student) pm.find(Student.class).whereId(id).forUpdate();
		System.out.println("found this student " + student.getBiodata().getName());
		try {

			for ( Iterator<StudentStatus> st = studentStatusList.iterator(); st.hasNext();) {
				StudentStatus sst = st.next();
				System.out.println(sst);
				Period p = sst.getPeriod();
				System.out.println(sst.getSession().getName() + ", " + p.getParent().getName() + "-" + p.getName());


			}


			student.setStatus(studentStatusList);
			pm.update();
		} catch ( Exception e ) {
			System.out.println("Rollback in StudentRegistrationUtil.initializeSubjects");
			pm.rollback();
		}
	}	

	public static void initializeSubjects2(Student student) throws Exception {
		System.out.println("Initialize subjects 2 for " + student.getBiodata().getName());
		PersistenceManager pm = new PersistenceManager();
		Program program = student.getProgram();
		Session intake = student.getIntake(); 
		LearningCentre learningCentre = student.getLearningCenter();
		System.out.println("Learning center "+learningCentre.getName());
		List<ProgramStructure> psList = pm.list("select ps from ProgramStructure ps where ps.program.id = '" + program.getId() + "' and ps.session.id = '" + intake.getId() + "' and ps.learningCenter.id='"+learningCentre.getId()+"'");
		if ( psList.size() == 0 ) 
			throw new Exception("Program Structure NOT AVAILABLE!");
		ProgramStructure ps = psList.get(0);
		Set<SubjectPeriod> subjectPeriodList = ps.getSubjectPeriod();
		Set<StudentStatus> studentStatusList = student.getStatus();
		System.out.println("student status list = " + studentStatusList.size());
		for ( Iterator<StudentStatus> it = studentStatusList.iterator(); it.hasNext(); ) {
			StudentStatus status = it.next();
			Period period = status.getPeriod();
			List<SubjectPeriod> listSP = pm.list("select s from SubjectPeriod s where s.programStructure.program.id = '" + program.getId() + 
					"' and s.programStructure.session.id = '" + intake.getId() + "' and s.period.id = '" + period.getId() + "' and s.programStructure.learningCenter.id ='"+learningCentre.getId()+"'");

			if ( listSP.size() > 0 ) {
				SubjectPeriod sp = listSP.get(0);

				Set<StudentSubject> l = new HashSet<StudentSubject>();
				for(Iterator<Subject> its = sp.getSubjects().iterator();its.hasNext(); ){
					StudentSubject studentSubjects = new StudentSubject();
					Subject sbj = its.next();
					System.out.println("subject registered "+sbj.getName());
					studentSubjects.setSubject(sbj);
					l.add(studentSubjects);
				}
				status.setStudentSubjects(l);



			}
			else {
				System.out.println(period.getName() + ", NOT DEFINED");
			}
		}
		String id = student.getId();
		student = (Student) pm.find(Student.class).whereId(id).forUpdate();
		System.out.println("found this student " + student.getBiodata().getName());
		try {
			for ( Iterator<StudentStatus> st = studentStatusList.iterator(); st.hasNext();) {
				StudentStatus sst = st.next();
				Period p = sst.getPeriod();
				System.out.println(sst.getSession().getName() + ", " + p.getParent().getName() + "-" + p.getName());


			}
			student.setStatus(studentStatusList);
			pm.update();
		} catch ( Exception e ) {
			System.out.println("Rollback in StudentRegistrationUtil.initializeSubjects");
			pm.rollback();
		}
	}

	public static void createPortalLogin(String login, String password, String name) throws Exception {
		String sql = "";
		Connection conn = null;
		Db db = null;
		try {
			db = new Db();
			conn = db.getConnection();
			conn.setAutoCommit(false);			
			Statement stmt = db.getStatement();
			SQLRenderer r = new SQLRenderer();
			//look for login id
			//for student login id is the matric number
			boolean found = false;
			{
				sql = "select user_login from users where user_login = '" + login + "'";

				ResultSet rs = stmt.executeQuery(sql);
				if ( rs.next() ) found = true;
			}

			if ( !found ) {
				{
					r.add("user_login", login);
					r.add("user_password", lebah.util.PasswordService.encrypt(password));
					r.add("user_name", name);
					r.add("user_role", "student");
					sql = r.getSQLInsert("users");
					stmt.executeUpdate(sql);
				}

				//prepare portal item
				//-- portal's theme
				{
					//sql = "select css_name from user_css where user_login = 'student'";
					//ResultSet rs = stmt.executeQuery(sql);
					String css_name = "default.css";
					//if ( rs.next() ) css_name = rs.getString("css_name");
					sql = "insert into user_css (user_login, css_name) values ('" + login + "', '" + css_name + "')";

					stmt.executeUpdate(sql);
				}

			}
			else {
				r.add("user_name", name);
				r.update("user_login", login);	
				sql = r.getSQLUpdate("users");		
				stmt.executeUpdate(sql);	
			}
			conn.commit();
		} catch ( Exception ex ) {
			try {
				conn.rollback();
			} catch ( SQLException rollex ) {}
			throw ex;
		} finally {
			if ( db != null ) db.close();
		}
	}	

	public static void createPortalLogin(Student student) throws Exception {
		createPortalLogin(student.getMatricNo(), student.getMatricNo(), student.getBiodata().getName());
	}

	public static void deleteSubjectLMS(String student_id) throws Exception {
		Db db = null;
		String sql = "";
		try {
			db = new Db();
			Statement stmt = db.getStatement();
			sql = "delete from member_subject where member_id = '" + student_id + "'";
			stmt.executeUpdate(sql);

		} finally {
			if ( db != null ) db.close();
		}
	}
	public static void addSubjectLMS(StudentSubject subject, String student_id) throws Exception {

		Db db = null;
		String sql = "";
		try {
			db = new Db();
			
			doAddSubjectLMS(db, subject, subject.getSubject().getCode(), null, student_id);
			
			SubjectSection subjectSection = subject.getSection();
			if (subjectSection != null) {
				String lmsSubjectId = subject.getSubject().getCode()+"_"+ subjectSection.getCode();
				doAddSubjectLMS(db, subject, lmsSubjectId, subject.getSubject().getCode(), student_id);	
			}

		} finally {
			if ( db != null ) db.close();
		}
	}

	private static void doAddSubjectLMS(Db db, StudentSubject subject, String lmsSubjectId, String parentSubjectId,  String student_id) throws SQLException {
		String sql;
		Statement stmt = db.getStatement();
		SQLRenderer r = new SQLRenderer();

		boolean found = false;
		//to make the subject appear in LMS, two tables need to be updated
		//subject & member_subject
		{
			found = false;
			{
				r.clear();
				r.add("subject_code");
				r.add("subject_id", lmsSubjectId);
				sql = r.getSQLSelect("subject");
				ResultSet rs = stmt.executeQuery(sql);	
				if ( rs.next() ) found = true;
			}
			if ( !found ) {
				//add data into table subject
				{
					r.clear();
					r.add("subject_id", lmsSubjectId);	
					//by default, classroom_id is the same as subject_id
					//classroom_id refer to subject_id
					//because sometime, a subject may use other subject as classroom
					r.add("classroom_id", lmsSubjectId);
					r.add("subject_code", subject.getSubject().getCode());
					r.add("subject_title", subject.getSubject().getName());
					//module id must be empty space (not null)
					r.add("module_id", "");
					r.add("subject_comment", "");
					r.add("subject_text", "");
					if ( parentSubjectId != null) {
						r.add("subjectGroup", subject.getSection().getName());
						r.add("is_group", 1);
						r.add("parent_id", parentSubjectId);
					}
					else {
						r.add("subjectGroup", "");
						r.add("is_group", 0);
						r.add("parent_id", "");
					}
					sql = r.getSQLInsert("subject");
					stmt.executeUpdate(sql);
				}

				//TODO: what if subject id already exist but with different title?

			} else {
				// if found then check its subject code
				// if different then update
				String subject_code = "";
				{
					r.clear();
					r.add("subject_id", lmsSubjectId);
					r.add("subject_code");
					sql = r.getSQLSelect("subject");
					ResultSet rs = stmt.executeQuery(sql);

					if ( rs.next()) {
						subject_code = rs.getString(1);
					}
				}

				//if ( !subject.getSection().getCode().equals(subject_code)) 
				{
					r.clear();
					r.update("subject_id", lmsSubjectId);
					r.add("subject_code", subject.getSubject().getCode());
					r.add("subject_title", subject.getSubject().getName());
				
			        if ( parentSubjectId != null ) {
			        	r.add("subjectGroup", subject.getSection().getName());
			        	r.add("is_group", 1);
			        	r.add("parent_id", parentSubjectId);
			        }
			        else {
			        	r.add("subjectGroup", "");
			        	r.add("is_group", 0);
			        	r.add("parent_id", "");
			        }
			        
					sql = r.getSQLUpdate("subject");
					stmt.executeUpdate(sql);
				}
			}

			{
				r.clear();
				r.add("subject_id");
				r.add("member_id", student_id);
				r.add("subject_id", lmsSubjectId);
				sql = r.getSQLSelect("member_subject");

				ResultSet rs = stmt.executeQuery(sql);
				if ( rs.next()) found = true;
				else found = false;

			}

			//add data into table member_subject
			if ( !found ){
				//then add
				r.clear();
				r.add("member_id", student_id);
				r.add("subject_id", lmsSubjectId);
				r.add("role", "learner");
				r.add("status", "active");
				r.add("module_id", "");
				sql = r.getSQLInsert("member_subject");
				//-
				stmt.executeUpdate(sql);
			}			
		}
	}
	public static void addSubjectLMS2_Deprecated(Subject subject, String student_id) throws Exception {

		//System.out.println("addSubjectLMS = " + subject.getCode());

		Db db = null;
		String sql = "";
		try {
			db = new Db();
			Statement stmt = db.getStatement();
			SQLRenderer r = new SQLRenderer();

			boolean found = false;

			//to make the subject appear in LMS, two tables need to be updated
			//subject & member_subject

			found = false;
			{
				r.clear();
				r.add("subject_code");
				r.add("subject_id", subject.getId());
				sql = r.getSQLSelect("subject");
				ResultSet rs = stmt.executeQuery(sql);	
				if ( rs.next() ) found = true;
			}

			if ( !found ) {

				//add data into table subject
				{
					r.clear();
					r.add("subject_id", subject.getId());	
					//by default, classroom_id is the same as subject_id
					//classroom_id refer to subject_id
					//because sometime, a subject may use other subject as classroom
					r.add("classroom_id", subject.getId());
					r.add("subject_code", subject.getCode());
					r.add("subject_title", subject.getName());
					//module id must be empty space (not null)
					r.add("module_id", "");
					r.add("subject_comment", "");
					r.add("subject_text", "");
					sql = r.getSQLInsert("subject");
					stmt.executeUpdate(sql);
				}

				//TODO: what if subject id already exist but with different title?

			} else {
				// if found then check its subject code
				// if different then update
				String subject_code = "";
				{
					r.clear();
					r.add("subject_id", subject.getId());
					r.add("subject_code");
					sql = r.getSQLSelect("subject");
					ResultSet rs = stmt.executeQuery(sql);

					if ( rs.next()) {
						subject_code = rs.getString(1);
					}
				}

				if ( !subject.getCode().equals(subject_code)) {
					r.clear();
					r.update("subject_id", subject.getId());
					r.add("subject_code", subject.getCode());
					r.add("subject_title", subject.getName());
					sql = r.getSQLUpdate("subject");
					stmt.executeUpdate(sql);
				}
			}

			{
				r.clear();
				r.add("subject_id");
				r.add("member_id", student_id);
				r.add("subject_id", subject.getId());
				sql = r.getSQLSelect("member_subject");

				ResultSet rs = stmt.executeQuery(sql);
				if ( rs.next()) found = true;
				else found = false;

			}

			//add data into table member_subject
			if ( !found ){
				//then add
				r.clear();
				r.add("member_id", student_id);
				r.add("subject_id", subject.getId());
				r.add("role", "learner");
				r.add("status", "active");
				r.add("module_id", "");
				sql = r.getSQLInsert("member_subject");
				//-
				stmt.executeUpdate(sql);
			}			

		} finally {
			if ( db != null ) db.close();
		}
	}	

	public static Student getStudent(String matricNo) throws Exception {
		PersistenceManager pm = new PersistenceManager();
		List<Student> list = pm.list("select s from Student s where s.matricNo = '" + matricNo + "'");
		if ( list.size() == 0 ) throw new Exception("No student");
		Student student = list.get(0);
		return student;
	}

	public static Session getSessionByDate(Date date) throws Exception {

		Session session = null;
		/*
		Date date = new GregorianCalendar(2008, Calendar.JULY, 25).getTime();
	    System.out.println(new SimpleDateFormat("dd-MM-yyyy").format(date));
		 */

		Hashtable h = new Hashtable();
		h.put("date", date);
		PersistenceManager pm = new PersistenceManager();
		List<Session> list = pm.list("select s from Session s where :date BETWEEN s.startDate AND s.endDate", h);
		if ( list.size() == 0 ) return null;
		session = list.get(0);
		return session;

	}
	public static Vector<Session> getSessionListByDate(Date date)throws Exception{
		Hashtable h = new Hashtable();
		h.put("date", date);
		Vector<Session> v = new Vector<Session>();
		PersistenceManager pm = new PersistenceManager();
		List<Session> list = pm.list("select s from Session s where :date BETWEEN s.startDate AND s.endDate", h);
		if ( list.size() > 0 ){
			v.addAll(list);
		}
		return v;
	}
	public static Session getSessionByDate(Date date,int pathNO) throws Exception {

		Session session = null;
		/*
		Date date = new GregorianCalendar(2008, Calendar.JULY, 25).getTime();
	    System.out.println(new SimpleDateFormat("dd-MM-yyyy").format(date));
		 */

		Hashtable h = new Hashtable();
		h.put("date", date);
		PersistenceManager pm = new PersistenceManager();
		List<Session> list = pm.list("select s from Session s where (:date BETWEEN s.startDate AND s.endDate) AND s.pathNo="+pathNO, h);
		if ( list.size() == 0 ) return null;
		session = list.get(0);
		return session;

	}

	public static Session getCurrentSession() throws Exception {
		Calendar cal = new GregorianCalendar();
		Date date = cal.getTime();
		return getSessionByDate(date);
	}
	public static Session getCurrentSession(int pathNo) throws Exception {
		Calendar cal = new GregorianCalendar();
		Date date = cal.getTime();
		return getSessionByDate(date,pathNo);
	}
	public static Period getCurrentPeriod(String matricNo) throws Exception {
		return getCurrentPeriod(getStudent(matricNo));
	}

	public static Period getCurrentPeriod(Student student) throws Exception {
		PersistenceManager pm = new PersistenceManager();
		Session currentSession = getCurrentSession();
		List<StudentStatus> studentStatusList = pm.list("select s from StudentStatus s where s.student.id = '" + student.getId() + 
				"' and s.session.id = '" + currentSession.getId() + "'");
		if ( studentStatusList.size() == 0 ) return null;
		StudentStatus status = studentStatusList.get(0);		
		Period period = status.getPeriod();
		return period;
	}

	public static Period getPeriod(Student student, Session session) throws Exception {
		PersistenceManager pm = new PersistenceManager();
		List<StudentStatus> studentStatusList = pm.list("select s from StudentStatus s where s.student.id = '" + student.getId() + 
				"' and s.session.id = '" + session.getId() + "'");
		if ( studentStatusList.size() == 0 ) return null;
		StudentStatus status = studentStatusList.get(0);		
		Period period = status.getPeriod();
		return period;
	}	


	public static Vector<Subject> getCurrentSubjectList(String matricNo) throws Exception {
		return getCurrentSubjectList(getStudent(matricNo));
	}


	public static Vector<Subject> getCurrentSubjectList(Student student) throws Exception {
		Vector<Subject> subjectList = new Vector();
		PersistenceManager pm = new PersistenceManager();
		int path = student.getProgram().getLevel().getPathNo();
		List<StudentStatus> studentStatusList = pm.list("select s from StudentStatus s where s.student.id = '" + student.getId() + 
				"' and s.session.id = '" + getCurrentSession(path).getId() + "'");
		if ( studentStatusList.size() == 0 ) return new Vector();
		StudentStatus status = studentStatusList.get(0);
		Set<StudentSubject> subjects = status.getStudentSubjects();
		for(Iterator<StudentSubject>it = subjects.iterator();it.hasNext();){
			Subject sb = it.next().getSubject();
			subjectList.add(sb);
		}
		return subjectList;
	}
	public static Vector<StudentSubject> getCurrentStudentSubjectList(String matricNo)throws Exception{
		return getCurrentStudentSubjectList(getStudent(matricNo));
	}
	public static Vector<StudentSubject> getCurrentStudentSubjectList(Student student)throws Exception{
		Vector<StudentSubject> studentSubjectList = null;
		//PersistenceManager pm = new PersistenceManager();
		DbPersistence db = new DbPersistence();

		String session_id = getCurrentSession(student.getProgram().getLevel().getPathNo()).getId();
		System.out.println("CURRENT SESSION ID IS " + session_id);

		String query = "SELECT s FROM StudentStatus s WHERE " +
		"s.student.id = '" + student.getId() + "' and " +
		"s.session.id = '" + session_id + "'";
		List<StudentStatus> studentStatusList = db.list(query);
		if(studentStatusList.size() == 0) { 
			System.err.println("[StudentRegistrationUtil.getCurrentStudentSubjectList] no student status found.");
			return new Vector<StudentSubject>();
		}
		StudentStatus status = studentStatusList.get(0);
		//StudentStatus status = null;
		if (status == null) {
			System.err.println("[StudentRegistrationUtil.getCurrentStudentSubjectList] studentStatus is NULL");
			return new Vector<StudentSubject>();
		}
		
		Set<StudentSubject> subjects = status.getStudentSubjects();
		
		//check if got subject_section
		
		SubjectSection section = null;
		List<SubjectSection> sections = db.list("select s from SubjectSection s order by s.sequence");
		if ( sections.size() > 0 ) {
			section = sections.get(0);
		}
		
		for ( StudentSubject s : subjects ) {
			System.out.println("Section is " + s.getSection());
			if ( s.getSection() == null ) {
				db.begin();
				s.setSection(section);
				db.commit();
			}
		}
		
		System.out.println("No. of subjects = " + subjects.size());
		
		if (subjects == null) {
			System.err.println("[StudentRegistrationUtil.getCurrentStudentSubjectList] subjects is NULL");
			return new Vector<StudentSubject>();
		}
		if (subjects.isEmpty()) {
			System.err.println("[StudentRegistrationUtil.getCurrentStudentSubjectList] subjects is empty");
			return new Vector<StudentSubject>();			
		}
		studentSubjectList = new Vector<StudentSubject>();
		studentSubjectList.addAll(subjects);
		return studentSubjectList;
	}

	public static Vector<StudentSubject> getNextStudentSubjectList(String matricNo)throws Exception{
		return getNextStudentSubjectList(getStudent(matricNo));
	}
	public static Vector<StudentSubject> getNextStudentSubjectList(Student student)throws Exception{
		Vector<StudentSubject> studentSubjectList = new Vector<StudentSubject>();
		PersistenceManager pm = new PersistenceManager();
		//System.err.println("getCurrentSession().getId() "+getCurrentSession().getId());
		List<StudentStatus> studentStatusList = pm.list("select s from StudentStatus s where s.student.id = '" + student.getId() + 
				"' and s.session.id = '" + getCurrentSession().getId() + "'");
		if ( studentStatusList.size() == 0 ) 
			return new Vector();
		StudentStatus status = studentStatusList.get(0);
		Set<StudentSubject> subjects = status.getStudentSubjects();
		studentSubjectList.addAll( subjects );
		return studentSubjectList;
	}

	public static StudentStatus getCurrentStudentStatus(String matricNo) throws Exception{
		return getCurrentStudentStatus(getStudent(matricNo));
	}
	public static StudentStatus getCurrentStudentStatus(Student student) throws Exception{
		PersistenceManager pm = new PersistenceManager();
		Session curr_ses = getCurrentSession(student.getProgram().getLevel().getPathNo());
		Set<StudentStatus> statuses = student.getStatus();
		StudentStatus status = null;
		for( StudentStatus ss : statuses ){
			if( ss.getSession().equals(curr_ses) ){
				status = ss;
				break;
			}
		}
		return status;
	}

	public static int calculateAge(Date date){
		Calendar input = new GregorianCalendar();
		input.setTime(date);
		Calendar now = Calendar.getInstance();
		int age =  now.get(Calendar.YEAR) - input.get(Calendar.YEAR);
		input.add(Calendar.YEAR, age);
		if(now.before(input)){
			age--;
		}
		return age;
	}
}
