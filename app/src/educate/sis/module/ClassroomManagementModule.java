package educate.sis.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.http.HttpSession;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.sis.struct.entity.Classroom;
import educate.sis.struct.entity.Period;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.Session;
import lebah.portal.action.AjaxModule;

public class ClassroomManagementModule  extends AjaxModule {
	
	String path = "apps/util/classroom/";
	private String vm = "default.vm";
	HttpSession session;
	DbPersistence db = new DbPersistence();

	
	public String doAction() throws Exception {
		context.put("_formName", formName);
		session = request.getSession();
		context.put("dateFormat", new SimpleDateFormat("yyyy-MM-dd"));
		context.put("timeFormat", new SimpleDateFormat("hh:mm a"));	
		context.put("numFormat", new DecimalFormat("#,###,###.00"));
		String command = request.getParameter("command");
		System.out.println(command);
		if ( null == command || "".equals(command)) start();	
		else if ( "list_students".equals(command)) listStudents();
		else if ( "create_classrooms".equals(command)) createClassrooms();
		else if ( "save_classrooms".equals(command)) saveClassrooms();
		else if ( "add_students".equals(command)) addStudents();
		else if ( "remove_students".equals(command)) removeStudents();
		else if ( "edit_classrooms".equals(command)) editClassrooms();
		else if ( "update_classrooms".equals(command)) updateClassrooms();
		else if ( "remove_classrooms".equals(command)) removeClassrooms();
		return vm;
	}



	private void removeClassrooms() throws Exception {
		
		String programId = request.getParameter("program_id") != null ? request.getParameter("program_id") : "";
		String sessionId = request.getParameter("session_id") != null ? request.getParameter("session_id") : "";
		String periodId = request.getParameter("period_id") != null ? request.getParameter("period_id") : "";
		
		String sql = "delete from Classroom c where c.program.id = '" + programId + "' " +
				"and c.session.id = '" + sessionId + "' " +
				"and c.period.id = '" + periodId + "'";
		db.begin();
		db.executeUpdate(sql);
		db.commit();
		
		listStudents();
		
	}



	private void updateClassrooms() throws Exception {
		
		String[] ids = request.getParameterValues("ids");
		String[] names = request.getParameterValues("names");
		int i = 0;
		for ( String id : ids ) {
			Classroom classroom = (Classroom) db.find(Classroom.class, id);
			if ( classroom != null ) {
				String name = names[i++];
				int k = name.indexOf("%20");
				if ( k > 0 ) {
					name = name.replaceAll("%20", " ");
				}
				db.begin();
				classroom.setName(name);
				db.commit();
			}
		}
		listStudents();
		
	}



	private void editClassrooms() {
		vm = path + "edit_classrooms.vm";
		String programId = request.getParameter("program_id") != null ? request.getParameter("program_id") : "";
		String sessionId = request.getParameter("session_id") != null ? request.getParameter("session_id") : "";
		String periodId = request.getParameter("period_id") != null ? request.getParameter("period_id") : "";
		
		Program program = !"".equals(programId) ? (Program) db.find(Program.class, programId) : null;
		Session session = !"".equals(sessionId) ? (Session) db.find(Session.class, sessionId) : null;
		Period period =  !"".equals(periodId) ? (Period) db.find(Period.class, periodId) : null;
		
		if ( program != null ) context.put("program", program); else context.remove("program");
		if ( session != null ) context.put("session", session); else context.remove("session");
		if ( period != null ) context.put("period", period); else context.remove("period");
		
		String sql = "select c from Classroom c where c.program = :program " +
		"and c.session = :session and c.period = :period " +
		"order by c.classroomOrder";
		Hashtable param = new Hashtable();
		param.put("session", session);
		param.put("period", period);
		param.put("program", program);
		List<Classroom> classrooms = db.list(sql, param);
		if ( classrooms.size() > 0 ) context.put("classrooms", classrooms);
		else context.remove("classrooms");
		
	}



	private void removeStudents() throws Exception {
		String classroomId = request.getParameter("classroom_id");
		String[] studentIds = request.getParameterValues("student_ids");
		if ( studentIds != null ) {
			Classroom classroom = (Classroom) db.find(Classroom.class, classroomId);
			List<Student> students = new ArrayList<Student>();
			for ( String studentId : studentIds ) {
				Student student = (Student) db.find(Student.class, studentId);
				students.add(student);
			}
			db.begin();
			for ( Student student : students ) {
				classroom.removeStudent(student);
			}
			db.commit();
		}
		listStudents();
		
	}

	private void addStudents() throws Exception {
		
		String classroomId = request.getParameter("classroom_id");
		String[] studentIds = request.getParameterValues("selected_students");
		if ( studentIds != null ) {
			Classroom classroom = (Classroom) db.find(Classroom.class, classroomId);
			List<Student> students = new ArrayList<Student>();
			for ( String studentId : studentIds ) {
				Student student = (Student) db.find(Student.class, studentId);
				students.add(student);
			}
			int capacity = classroom.getCapacity();
			db.begin();
			int i = 0;
			for ( Student student : students ) {
				i++;
				if ( i > capacity ) break;
				classroom.addStudent(student);
			}
			db.commit();
		}
		listStudents();
		
	}

	private void saveClassrooms() throws Exception {
		
		String programId = request.getParameter("program_id");
		String sessionId = request.getParameter("session_id");
		String periodId = request.getParameter("period_id");
		
		Program program = (Program) db.find(Program.class, programId);
		Session session = (Session) db.find(Session.class, sessionId);
		Period period = (Period) db.find(Period.class, periodId);

		String classroomSize = request.getParameter("classroom_size");
		int size = Integer.parseInt(classroomSize);
		
		String[] names = request.getParameterValues("classrooms");
		int i = 0;
		for ( String name : names ) {
			i++;
			db.begin();
			Classroom classroom = new Classroom();
			classroom.setSession(session);
			classroom.setPeriod(period);
			classroom.setProgram(program);
			classroom.setClassroomOrder(i);
			classroom.setCapacity(size);
			classroom.setName(name);
			db.persist(classroom);
			db.commit();
		}
		
		
		listStudents();
		
	}

	private void createClassrooms() {
		String totalStudents = request.getParameter("total_students");
		String classroomSize = request.getParameter("classroom_size");
		
		double total = Double.parseDouble(totalStudents);
		double size = Double.parseDouble(classroomSize);
		
		int numOfClassroom = (int) (total / size);
		numOfClassroom = total % size > 0 ? numOfClassroom + 1 : numOfClassroom;

		context.put("classroom_size", classroomSize);
		context.put("num_of_classroom", numOfClassroom);
		
		listStudents();
		
	}

	private void listStudents() {
		vm = path + "students.vm";
		String programId = request.getParameter("program_id");
		String sessionId = request.getParameter("session_id");
		String periodId = request.getParameter("period_id");
		
		Program program = (Program) db.find(Program.class, programId);
		Session session = (Session) db.find(Session.class, sessionId);
		Period period = (Period) db.find(Period.class, periodId);
		
		if ( program != null ) context.put("program", program); else context.remove("program");
		if ( session != null ) context.put("session", session); else context.remove("session");
		if ( period != null ) context.put("period", period); else context.remove("period");
		
		Hashtable param = new Hashtable();
		param.put("session", session);
		param.put("period", period);
		param.put("program", program);
		String sql = "select s from Student s join s.status st " +
				"WHERE (s.fakeStudent is null OR s.fakeStudent = '') " +
				"and st.session = :session and st.period = :period " +
				"and s.program = :program order by s.biodata.name";
		List<Student> students = db.list(sql, param);
		context.put("all_students", students);
		
		//is there classrooms?
		sql = "select c from Classroom c where c.program = :program " +
				"and c.session = :session and c.period = :period " +
				"order by c.classroomOrder";
		List<Classroom> classrooms = db.list(sql, param);
		if ( classrooms.size() > 0 ) context.put("classrooms", classrooms);
		else context.remove("classrooms");
		
		List<Student> list = new ArrayList();
		list.addAll(students);
		
		if ( classrooms.size() > 0 ) {
			for ( Classroom classroom : classrooms ) {
				for ( Student student : classroom.getStudents() ) {
					list.remove(student);
				}
			}
		}
		context.put("students", list);
		
		
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
	}

}
