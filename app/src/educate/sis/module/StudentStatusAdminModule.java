package educate.sis.module;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import educate.enrollment.entity.WithdrawType;
import educate.sis.struct.entity.Period;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.Session;
import lebah.portal.action.AjaxModule;


public class StudentStatusAdminModule  extends AjaxModule {
	
	String path = "apps/util/student_statuses/";
	private String vm = "default.vm";
	HttpSession session;
	DbPersistence db = new DbPersistence();
	Hashtable data = null;
	
	@Override
	public String doAction() throws Exception {
		context.put("_formName", formName);
		session = request.getSession();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		context.put("dateFormat", dateFormat);
		context.put("programUtil", new ProgramUtil());
		String command = getParam("command");
		
		System.out.println(">>>>" + command);
		
		if ( null == command || "".equals(command)) start();
		else if ( "get_student_by_matric".equals(command)) getStudentByMatric();
		else if ( "delete_all".equals(command)) deleteAllStatus();
		else if ( "delete_status".equals(command)) deleteStatus();
		else if ( "add_status".equals(command)) addStatus();
		else if ( "update_status".equals(command)) updateStatus();
		else if ( "status_date".equals(command)) saveStudentStatus();
		else if ( "save_remark".equals(command)) saveStatusDateRemark();
		return vm;
	}

	private void deleteAllStatus() throws Exception {
		vm = path + "list_statuses.vm";
		String studentId = getParam("student_id");
		Student student = db.find(Student.class, studentId);
		context.put("student", student);

		if ( student != null ) {
			StudentStatusUtil u = new StudentStatusUtil();
			u.cleanUpStudentStatuses(student.getId());
			displayStatuses(student);
		}
		
	}

	private void updateStatus() throws Exception {
		vm = path + "student_status.vm";
		String studentId = getParam("student_id");
		Student student = db.find(Student.class, studentId);
		context.put("student", student);
		String statusId = getParam("status_id");
		StudentStatus status = db.find(StudentStatus.class, statusId);
		String typeId = getParam("status_" + status.getId());
		StatusType type = db.find(StatusType.class, typeId);
		
		StudentStatusUtil util = new StudentStatusUtil();
		util.updateStatus(status, type);
		
		displayStatuses(student);
	}

	private void addStatus() throws Exception {
		vm = path + "list_statuses.vm";
		String studentId = getParam("student_id");
		Student student = db.find(Student.class, studentId);
		context.put("student", student);
		String sessionId = getParam("session_id");
		Session session = db.find(Session.class, sessionId);
		
		StudentStatusUtil util = new StudentStatusUtil();
		util.setUseSubjectTemp(true);
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
		vm = path + "student_status.vm";
		String studentId = getParam("student_id");
		Student student = db.find(Student.class, studentId);
		context.put("student", student);
		
		String statusId = getParam("status_id");
		StudentStatus status = db.find(StudentStatus.class, statusId);
		
		StudentStatusUtil util = new StudentStatusUtil();
		util.deleteStatus(status);
		
		displayStatuses(student);
	}
	
	private void saveStudentStatus() throws Exception {
		vm = path + "student_status.vm";
		String studentId = getParam("student_id");
		Student student = db.find(Student.class, studentId);
		context.put("student", student);
		
		String statusId = getParam("status_id");
		StudentStatus status = db.find(StudentStatus.class, statusId);
		
		String typeId = getParam("status_" + status.getId());
		StatusType type = db.find(StatusType.class, typeId);

		
		String cnt = request.getParameter("status_cnt");
		String _date = getParam("status_date_" + cnt);
		
		String withdrawTypeId = getParam("withdraw_" + status.getId());
		WithdrawType withdrawType = db.find(WithdrawType.class, withdrawTypeId);
		
		String remark = getParam("remark_" + status.getId());
		Date statusDate = !"".equals(_date) ? new SimpleDateFormat("dd-MM-yyyy").parse(_date) : status.getSession().getStartDate(); 

		db.begin();
		if ( "quit".equals(type.getId()) ) {
			status.setWithdrawType(withdrawType);
			status.setRemark(remark);
			status.setStatusDate(statusDate);
		} else {
			status.setWithdrawType(null);
			status.setRemark(remark);
			status.setStatusDate(statusDate);
		}
		db.commit();
		
		
		StudentStatusUtil util = new StudentStatusUtil();
		util.updateStatus(status, type);
		
		//if status type is inactive, set the rest of status as inactive too
		if ( type.getQuit() ) {
			List<StudentStatus> statuses = util.getStudentStatuses(student);
			boolean update = false;
			for ( StudentStatus s : statuses ) {
				if ( update ) {
					util.updateStatus(s, type);
					db.begin();
					if ( "quit".equals(type.getId()) ) {
						s.setWithdrawType(withdrawType);
						s.setRemark(remark);
						s.setStatusDate(statusDate);
					} else {
						s.setWithdrawType(null);
						s.setRemark("");
						s.setStatusDate(null);
					}
					db.commit();
				}				
				if ( status.getId().equals(s.getId())) {
					update = true;
				}
			}
		}
		
		displayStatuses(student);
	}	
	
	private void saveStatusDateRemark() throws Exception {
		vm = path + "save_remark.vm";
		String studentId = getParam("student_id");
		Student student = db.find(Student.class, studentId);
		context.put("student", student);
		
		String statusId = getParam("status_id");
		StudentStatus status = db.find(StudentStatus.class, statusId);
		
		
		String cnt = request.getParameter("status_cnt");
		String _date = getParam("status_date_" + cnt);
		
		String remark = getParam("remark_" + status.getId());
		Date statusDate = !"".equals(_date) ? new SimpleDateFormat("dd-MM-yyyy").parse(_date) : status.getSession().getStartDate(); 

		String withdrawTypeId = getParam("withdraw_" + status.getId());
		WithdrawType withdrawType = db.find(WithdrawType.class, withdrawTypeId);
		
		db.begin();

		status.setWithdrawType(withdrawType);
		status.setRemark(remark);
		status.setStatusDate(statusDate);
	
		db.commit();
		

	}
	
	

	private void getStudentByMatric() throws Exception {
		vm = path + "list_statuses.vm";
		String studentNo = getParam("student_no");
		context.put("student_no", studentNo);
		String sql = "select s from Student s where s.matricNo = '" + studentNo + "'";
		Student student = (Student) db.get(sql);
		if ( student != null ) context.put("student", student);
		else context.remove("student");
		
		if ( student != null ) {
			displayStatuses(student);
		}
		
	}

	private void displayStatuses(Student student) throws Exception {
		
		Period entryLevel = student.getEntryLevel();
		
		ProgramUtil pu = new ProgramUtil();
		Program program = student.getProgram();
		List<Period> periods = new ArrayList<Period>();
		List<Period> periods2 = program.getPeriodScheme().getFunctionalPeriodList();
		if ( entryLevel != null ) {
			boolean begin = false;
			for ( Period p : periods2 ) {
				if ( p.getId().equals(entryLevel.getId())) begin = true;
				if ( begin ) periods.add(p);
			}
		} else {
			periods = periods2;
		}
		
		StudentStatusUtil u = new StudentStatusUtil();
		List<StudentStatus> statuses = u.getStudentStatuses(student); // pu.getStudentStatuses(student);
		context.put("statuses", statuses);
		
		int numberOfPeriods = periods.size();
		int numberOfStatuses = statuses.size();
		int totalCount = numberOfPeriods > numberOfStatuses ? numberOfPeriods : numberOfStatuses;

		
		Hashtable param = new Hashtable();
		param.put("dateStart", student.getIntake().getStartDate());
		param.put("pathNo", student.getProgram().getLevel().getPathNo());
		List<Session> sessions = db.list("select s from Session s where s.pathNo = :pathNo and s.startDate >= :dateStart order by s.startDate", totalCount, param);
		//List<Session> sessions = db.list("select s from Session s where s.pathNo = :pathNo and s.startDate >= :dateStart order by s.startDate", param);
		context.put("sessions", sessions);
		
		//get status for each session
		
		//store student status for each session
		Map<String, StudentStatus> statusMap = new HashMap<String, StudentStatus>();
		int count = 0;
		for ( Session session : sessions ) {
			System.out.println("session = " + session.getName());
			StudentStatus studentStatus =  pu.getStudentStatus(student, session);
			if ( studentStatus != null ) {
				statusMap.put(session.getId(), studentStatus);
				count++;
			}
			if ( count > totalCount ) break;
			
		}
		context.put("status_map", statusMap);
		
		List<StatusType> types = db.list("select s from StatusType s order by s.sequence");
		context.put("types", types);
		
		List<WithdrawType> withdrawTypes = db.list("select s from WithdrawType s order by s.sequence");
		context.put("withdrawTypes", withdrawTypes);
		
	}

	private void start() {
		vm = path + "start.vm";
		
	}

}
