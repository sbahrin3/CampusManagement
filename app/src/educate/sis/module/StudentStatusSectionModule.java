package educate.sis.module;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import educate.db.DbPersistence;
import educate.enrollment.entity.StatusType;
import educate.enrollment.entity.Student;
import educate.enrollment.entity.StudentStatus;
import educate.sis.struct.entity.LearningCentre;
import educate.sis.struct.entity.Period;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.Session;
import educate.sis.struct.entity.SubjectSection;
import lebah.portal.action.AjaxModule;

public class StudentStatusSectionModule  extends AjaxModule {
	
	String path = "apps/util/student_status_section/";
	private String vm = "default.vm";
	HttpSession session;
	DbPersistence db = new DbPersistence();
	Hashtable data = null;
	private boolean isCreateAll = true; //create all status after the created status
	private boolean isDeleteAll = true; //delete all statuses after deleted selected status

	
	@Override
	public String doAction() throws Exception {
		context.put("_formName", formName);
		session = request.getSession();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		context.put("dateFormat", dateFormat);
		context.put("programUtil", new ProgramUtil());
		String command = request.getParameter("command");
		
		System.out.println(">>>>" + command);
		
		if ( null == command || "".equals(command)) start();
		else if ( "get_student_by_matric".equals(command)) getStudentByMatric();
		else if ( "delete_all".equals(command)) deleteAllStatus();
		else if ( "delete_status".equals(command)) deleteStatus();
		else if ( "add_status".equals(command)) addStatus();
		else if ( "update_status".equals(command)) updateStatus();
		else if ( "update_section".equals(command)) updateSection();
		
		getSections();
		
		return vm;
	}

	private void updateSection() throws Exception {
		vm = path + "empty.vm";
		String id = request.getParameter("status_id");
		StudentStatus studentStatus = db.find(StudentStatus.class, id);
		if ( studentStatus != null ) {
			String sectionId = request.getParameter("section_id_" + id);
			SubjectSection section = db.find(SubjectSection.class, sectionId);
			if ( section != null ) {
				db.begin();
				studentStatus.setSection(section);
				db.commit();
			}
		}
		else {
			System.out.println("StudentStatus is NULL.");
		}
		
	}

	private void getSections() {
		String studentId = request.getParameter("student_id");
		if ( studentId != null && !"".equals(studentId)) {
			Student student = db.find(Student.class, studentId);
			LearningCentre centre = student.getLearningCenter();
			
			List<SubjectSection> sections = db.list("select s from SubjectSection s where s.learningCentre.id = '" + centre.getId() + "'");
			context.put("sections", sections);
		}
		else {
			String studentNo = request.getParameter("student_no");
			String sql = "select s from Student s where s.matricNo = '" + studentNo + "'";
			Student student = (Student) db.get(sql);
			if ( student != null ) {
				LearningCentre centre = student.getLearningCenter();
				
				List<SubjectSection> sections = db.list("select s from SubjectSection s where s.learningCentre.id = '" + centre.getId() + "'");
				context.put("sections", sections);				
			}
			else
				context.remove("sections");
			
			
		}
		
	}

	private void deleteAllStatus() throws Exception {
		vm = path + "list_statuses.vm";
		String studentId = request.getParameter("student_id");
		Student student = db.find(Student.class, studentId);
		context.put("student", student);

		if ( student != null ) {
			StudentStatusUtil u = new StudentStatusUtil();
			u.cleanUpStudentStatuses(student.getId());
			displayStatuses(student);
		}
		
	}

	private void updateStatus() throws Exception {
		vm = path + "list_statuses.vm";
		String studentId = request.getParameter("student_id");
		Student student = db.find(Student.class, studentId);
		context.put("student", student);
		String statusId = request.getParameter("status_id");
		StudentStatus status = db.find(StudentStatus.class, statusId);
		String typeId = request.getParameter("status_" + status.getId());
		StatusType type = db.find(StatusType.class, typeId);
		
		StudentStatusUtil util = new StudentStatusUtil();
		util.updateStatus(status, type);
		
		displayStatuses(student);
	}

	private void addStatus() throws Exception {
		vm = path + "list_statuses.vm";
		String studentId = request.getParameter("student_id");
		Student student = db.find(Student.class, studentId);
		context.put("student", student);
		String sessionId = request.getParameter("session_id");
		Session session = db.find(Session.class, sessionId);
		
		StudentStatusUtil util = new StudentStatusUtil();
		util.addStatus(student, session);
		
		displayStatuses(student);
	}



	private void addNewStudentStatus(Student student, Session session, Period period, StatusType type, ProgramUtil pu) throws Exception {
		Set<StudentStatus> statuses = student.getStatus();
		db.begin();
		StudentStatus status = new StudentStatus();
		status.setStudent(student);
		status.setSession(session);
		status.setPeriod(period);
		status.setType(type);
		statuses.add(status);
		db.commit();
	}
	
	private void updateStudentStatus(Student student, Session session, Period period, StatusType type, ProgramUtil pu) throws Exception {
		StudentStatus status = pu.getStudentStatus(student, session);
		if ( status != null ) {
			db.begin();
			status.setSession(session);
			status.setPeriod(period);
			status.setType(type);
			db.commit();
		}
		else {
			System.out.println("Create new...");
			Set<StudentStatus> statuses = student.getStatus();
			db.begin();
			status = new StudentStatus();
			status.setStudent(student);
			status.setSession(session);
			status.setPeriod(period);
			status.setType(type);
			statuses.add(status);
			System.out.println("Added: " + status.getSession().getName() + ", " + status.getPeriod().getName());
			db.commit();
		}
	}
	

	private void deleteStatus() throws Exception {
		vm = path + "list_statuses.vm";
		String studentId = request.getParameter("student_id");
		Student student = db.find(Student.class, studentId);
		context.put("student", student);
		
		String statusId = request.getParameter("status_id");
		StudentStatus status = db.find(StudentStatus.class, statusId);
		
		StudentStatusUtil util = new StudentStatusUtil();
		util.deleteStatus(status);
		
		displayStatuses(student);
	}

	private void getStudentByMatric() {
		vm = path + "list_statuses.vm";
		String studentNo = request.getParameter("student_no");
		context.put("student_no", studentNo);
		String sql = "select s from Student s where s.matricNo = '" + studentNo + "'";
		Student student = (Student) db.get(sql);
		if ( student != null ) context.put("student", student);
		else context.remove("student");
		
		if ( student != null ) {
			displayStatuses(student);
		}
		
	}

	private void displayStatuses(Student student) {
		
		System.out.println("statuses = " + student.getStatus());
		
		ProgramUtil pu = new ProgramUtil();
		Program program = student.getProgram();
		List<Period> periods = program.getPeriodScheme().getFunctionalPeriodList();
		List<StudentStatus> statuses = pu.getStudentStatuses(student);
		context.put("statuses", statuses);
		
		int numberOfPeriods = periods.size();
		int numberOfStatuses = statuses.size();
		int totalCount = numberOfPeriods > numberOfStatuses ? numberOfPeriods : numberOfStatuses;
		
		Hashtable param = new Hashtable();
		param.put("dateStart", student.getIntake().getStartDate());
		param.put("pathNo", student.getProgram().getLevel().getPathNo());
		List<Session> sessions = db.list("select s from Session s where s.pathNo = :pathNo and s.startDate >= :dateStart order by s.startDate", totalCount, param);
				context.put("sessions", sessions);
		
		//get status for each session
		
		//store student status for each session
		Map<String, StudentStatus> statusMap = new HashMap<String, StudentStatus>();
		int count = 0;
		for ( Session session : sessions ) {
			StudentStatus studentStatus = pu.getStudentStatus(student, session);
			if ( studentStatus != null ) {
				statusMap.put(session.getId(), studentStatus);
				count++;
			}
			if ( count > totalCount ) break;
			
		}
		context.put("status_map", statusMap);
		
		List<StatusType> types = db.list("select s from StatusType s order by s.category");
		context.put("types", types);
	}

	private void start() {
		vm = path + "list_statuses.vm";
		
	}

}
