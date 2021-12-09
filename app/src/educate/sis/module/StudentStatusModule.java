/**
 * 
 */
package educate.sis.module;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import educate.db.DbPersistence;
import educate.enrollment.entity.StatusType;
import educate.enrollment.entity.Student;
import educate.enrollment.entity.StudentStatus;
import educate.enrollment.entity.WithdrawType;
import educate.sis.struct.entity.Period;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.Session;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

/**
 * @author Shamsul Bahrin bin Abd Mutalib
 *
 */
public class StudentStatusModule extends LebahModule {
	
	String path = "apps/util/student_statuses2";
	private String vm = "default.vm";
	HttpSession session;
	DbPersistence db = new DbPersistence();
	Hashtable data = null;

	
	public void preProcess() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		context.put("dateFormat", dateFormat);
		System.out.println(command);
	}
	
	@Override
	public String start() {
		return path + "/start.vm";
	}
	
	
	@Command("listStatuses")
	public String listStatuses() throws Exception {
		String matricNo = getParam("matricNo");
		Student student = (Student) db.get("select s from Student s where s.matricNo = '" + matricNo + "'");
		context.put("student", student);
		if ( student != null ) {
			displayStatuses(student);
		}
		return path + "/listStatuses.vm";
	}
	
	@Command("editStatus")
	public String editStatus() throws Exception {
		String statusId = getParam("studentStatusId");
		StudentStatus studentStatus = db.find(StudentStatus.class, statusId);
		Student student = studentStatus.getStudent();
		context.put("student", student);
		context.put("studentStatus", studentStatus);
		
		List<StatusType> types = db.list("select s from StatusType s order by s.sequence");
		context.put("types", types);
		List<WithdrawType> withdrawTypes = db.list("select s from WithdrawType s order by s.sequence");
		context.put("withdrawTypes", withdrawTypes);
		
		return path + "/editStatus.vm";
	}
	
	@Command("saveStatus")
	public String saveStatus() throws Exception {
		String statusId = getParam("studentStatusId");
		StudentStatus studentStatus = db.find(StudentStatus.class, statusId);
		Student student = studentStatus.getStudent();
		context.put("student", student);
	
		String typeId = getParam("statusId");
		StatusType type = db.find(StatusType.class, typeId);

		String _date = getParam("statusDate");
		
		String withdrawTypeId = getParam("withdrawTypeId");
		WithdrawType withdrawType = db.find(WithdrawType.class, withdrawTypeId);
		
		String remark = getParam("statusRemark");
		Date statusDate = !"".equals(_date) ? new SimpleDateFormat("dd-MM-yyyy").parse(_date) : studentStatus.getSession().getStartDate(); 

		db.begin();
		if ( "quit".equals(type.getId()) ) {
			studentStatus.setWithdrawType(withdrawType);
			studentStatus.setRemark(remark);
			studentStatus.setStatusDate(statusDate);
		} else {
			studentStatus.setWithdrawType(null);
			studentStatus.setRemark(remark);
			studentStatus.setStatusDate(statusDate);
		}
		db.commit();

		StudentStatusUtil util = new StudentStatusUtil();
		util.updateStatus(studentStatus, type);
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
				if ( studentStatus.getId().equals(s.getId())) {
					update = true;
				}
			}
			
		}
	
		displayStatuses(student);
		return path + "/listStatuses.vm";
	}
	
	
	@Command("updateCurrentStatus")
	public String updateCurrentStatus() throws Exception {
		String statusId = getParam("studentStatusId");
		StudentStatus studentStatus = db.find(StudentStatus.class, statusId);
		Student student = studentStatus.getStudent();
		context.put("student", student);
	
		String _date = getParam("statusDate");
		
		Date statusDate = !"".equals(_date) ? new SimpleDateFormat("dd-MM-yyyy").parse(_date) : studentStatus.getSession().getStartDate(); 

		StudentStatusUtil util = new StudentStatusUtil();
		
		studentStatus.setStatusDate(statusDate);
		util.updateCurrentStudentStatus(student, studentStatus);

		displayStatuses(student);
		return path + "/listStatuses.vm";
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
		int numberOfPeriods = periods.size();
		int numberOfStatuses = statuses.size();
		int totalCount = numberOfPeriods > numberOfStatuses ? numberOfPeriods : numberOfStatuses;
		
		StudentStatus currentStatus = u.getCurrentStudentStatus(student);
		context.put("currentStatus", currentStatus);

		//list of sessions started with student's intake date
		Hashtable param = new Hashtable();
		param.put("dateStart", student.getIntake().getStartDate());
		param.put("pathNo", student.getProgram().getLevel().getPathNo());
		List<Session> sessions = db.list("select s from Session s where s.pathNo = :pathNo and s.startDate >= :dateStart order by s.startDate", totalCount, param);
		context.put("sessions", sessions);
		
		//get status for each session
		//store student status for each session
		Map<String, StudentStatus> statusMap = new HashMap<String, StudentStatus>();
		int count = 0;
		List<Session> sessions2 = db.list("select s from Session s where s.pathNo = :pathNo and s.startDate >= :dateStart order by s.startDate desc", param);
		boolean hasDeferred = false;
		boolean hasQuit = false;
		for ( Session session : sessions2 ) {
			StudentStatus studentStatus =  pu.getStudentStatus(student, session);
			if ( studentStatus != null ) {
				if ( studentStatus.getType().getQuit() ) {
					hasQuit = true;
					studentStatus.setChangeable(false);
				}
				else if ( studentStatus.getType().getDefer() ) {
					hasDeferred = true;
					studentStatus.setChangeable(false);
				}
				else {
					if ( hasQuit ) {
						studentStatus.setChangeable(false);
					} else {
						if ( hasDeferred ) {
							studentStatus.setChangeable(false);
						} else {
							studentStatus.setChangeable(true);
						}
					}
				}
				statusMap.put(session.getId(), studentStatus);
				//count++;
			}
			//if ( count > totalCount ) break;
		}
		context.put("status_map", statusMap);
		context.put("isQuit", hasQuit);
	}
	
}
