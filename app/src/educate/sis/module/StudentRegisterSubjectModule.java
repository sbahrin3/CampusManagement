package educate.sis.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.enrollment.entity.StudentStatus;
import educate.enrollment.entity.StudentSubject;
import educate.sis.exam.entity.FinalResult;
import educate.sis.exam.entity.FinalSubjectResult;
import educate.sis.finance.entity.InvoiceItem;
import educate.sis.finance.module.AccountStatement;
import educate.sis.finance.module.AccountStatementUtil;
import educate.sis.finance.module.InvoiceUtil;
import educate.sis.struct.entity.LearningCentre;
import educate.sis.struct.entity.Period;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.ProgramStructure;
import educate.sis.struct.entity.Session;
import educate.sis.struct.entity.Subject;
import educate.sis.struct.entity.SubjectAddDropLog;
import educate.sis.struct.entity.SubjectReg;
import educate.sis.struct.entity.SubjectSection;
import lebah.portal.action.AjaxModule;

public class StudentRegisterSubjectModule extends AjaxModule {
	
	String path = "apps/util/subject_registration_student/";
	private String vm = "default.vm";
	HttpSession session;
	DbPersistence db = new DbPersistence();
	protected boolean studentMode = false;

	
	public String doAction() throws Exception {
		context.put("_formName", formName);
		session = request.getSession();
		
		if ( studentMode ) context.put("student_mode", true);
		else context.remove("student_mode");
		context.put("allowSubjectRegister", true);
		
		context.remove("pre_requisite_subjects");
		lebah.util.Util util = new lebah.util.Util();
		context.put("util", util);
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		context.put("dateFormat", dateFormat);
		context.put("numFormat", new DecimalFormat("#,###,###.00"));
		context.put("programUtil", new ProgramUtil());
		String command = request.getParameter("command");
		System.out.println(command);
		if ( command == null || "".equals(command)) start();
		else if ( "get_student".equals(command)) getStudent();
		else if ( "registered_subjects".equals(command)) registeredSubjects();
		else if ( "statement_account".equals(command)) statementAccount();
		
		else if ( "subject_register".equals(command)) subjectRegister();
		else if ( "subject_drop".equals(command)) subjectDrop();
		
		else if ( "cancel_subject_register".equals(command)) cancelSubjectRegister();
		else if ( "cancel_subject_drop".equals(command)) cancelSubjectDrop();
		
		else if ( "reg_status".equals(command)) registerStatus();
		else if ( "confirm_registration".equals(command)) confirmRegistration();
		
		else if ( "validate".equals(command)) validateSubjects();
		else if ( "doRegistration".equals(command)) doRegistration();
		
		return vm;
	}
	
	private void doRegistration() throws Exception {
		
		String id = request.getParameter("student_status_id");
		StudentStatus studentStatus = (StudentStatus) db.find(StudentStatus.class, id);
		
		db.begin();
		studentStatus.setSubjectsValidated(false);
		db.commit();
		
		getStudent();
	}

	private void validateSubjects() throws Exception {
		
		String id = request.getParameter("student_status_id");
		StudentStatus studentStatus = (StudentStatus) db.find(StudentStatus.class, id);
		context.put("student_status", studentStatus);
		context.put("student", studentStatus.getStudent());
		
		db.begin();
		studentStatus.setSubjectsValidated(true);
		db.commit();
		
		getListSubjectsValidated(studentStatus);
		
		vm = path + "list_subjects_registered.vm";
		
	}

	private void getListSubjectsValidated(StudentStatus studentStatus) {
		
		context.put("studentStatus", studentStatus);
		//list of subjects
		//separate between CORE and NON-CORE
		Student student = studentStatus.getStudent();
		String programId = student.getProgram().getId();
		String centreId = student.getLearningCenter().getId();
		String intakeId = student.getIntake().getId();
		String sql = "select p from ProgramStructure p where p.program.id = '" + programId + "' " +
				"and p.learningCenter.id = '" + centreId + "' " +
				"and p.session.id = '" + intakeId + "'";
		ProgramStructure ps = (ProgramStructure) db.get(sql);
		Map<String, SubjectReg> subjectMap = new HashMap<String, SubjectReg>();
		if ( ps != null ) {
			ProgramUtil pu = new ProgramUtil();
			Set<SubjectReg> subjectRegs = pu.getSubjectRegs(ps);
			for ( SubjectReg sr : subjectRegs ) {
				subjectMap.put(sr.getSubject().getId(), sr);
			}
		}
		
	
		List<SubjectReg> registeredSubjects = new ArrayList<SubjectReg>();
		context.put("registeredSubjects", registeredSubjects);
		
		Set<StudentSubject> studentSubjects = studentStatus.getStudentSubjects();
		for ( StudentSubject s : studentSubjects ) {
			Subject subject = s.getSubject();
			SubjectReg reg = subjectMap.get(subject.getId());
			registeredSubjects.add(reg);
		}
	}

	private void cancelSubjectDrop() {
		vm = path + "subject_list_item_registered.vm";
		String id = request.getParameter("subject_reg_id");
		StudentSubject s = (StudentSubject) db.find(StudentSubject.class, id);
		context.put("s", s);
		
		List<StudentSubject> listDropped = (List) session.getAttribute("_list_dropped");
		if ( listDropped == null ) {
			listDropped = new ArrayList<StudentSubject>();
			session.setAttribute("_list_dropped", listDropped);
		}
		context.put("list_dropped", listDropped);
		
		context.put("preRequisiteOK", true);
	
		listDropped.remove(s);
	}


	private void subjectDrop() {
		vm = path + "subject_list_item_registered.vm";
		String id = request.getParameter("subject_reg_id");
		
		StudentSubject ss = (StudentSubject) db.find(StudentSubject.class, id);
		context.put("s", ss);
		
		List<StudentSubject> listDropped = (List) session.getAttribute("_list_dropped");
		if ( listDropped == null ) {
			listDropped = new ArrayList<StudentSubject>();
			session.setAttribute("_list_dropped", listDropped);
		}
		context.put("list_dropped", listDropped);
		
		context.put("preRequisiteOK", true);
		
		if ( !listDropped.contains(ss)) listDropped.add(ss);
	}



	private void confirmRegistration() throws Exception {
		
		vm = path + "confirm_registration.vm";
		String id = request.getParameter("student_status_id");
		StudentStatus studentStatus = (StudentStatus) db.find(StudentStatus.class, id);
		context.put("student_status", studentStatus);
		context.put("student", studentStatus.getStudent());

		SubjectSection section = null;
		List<SubjectSection> list = db.list("select s from SubjectSection s where s.learningCentre.id = '" + studentStatus.getStudent().getLearningCenter().getId() + "'");
		if ( list.size() > 0 ) section = list.get(0);
		
		List<SubjectReg> listRegistered = (List) session.getAttribute("_list_registered");
		if ( listRegistered != null && listRegistered.size() > 0 ) {
			for ( SubjectReg sr : listRegistered ) {
				db.begin();
				StudentSubject studentSubject = new StudentSubject();
				studentSubject.setCategory(sr.getCategory());
				studentSubject.setSubject(sr.getSubject());
				if ( section != null ) studentSubject.setSection(section);
				if ( studentStatus.addStudentSubject(studentSubject) ) {
					studentSubject.setStudentStatus(studentStatus);
					db.persist(studentSubject);
				}
				db.commit();
			}
		}
		
		List<StudentSubject> listDropped = (List) session.getAttribute("_list_dropped");
		if ( listDropped != null && listDropped.size() > 0 ) {

			/*
			for ( StudentSubject ss : listDropped ) {
				db.begin();
				studentStatus.delStudentSubject(ss);
				db.remove(ss);
				db.commit();
			}
			*/
			
			db.begin();
			for ( StudentSubject ss : listDropped ) {
				studentStatus.getStudentSubjects().remove(ss);
				db.remove(ss);
			}
			db.commit();
			
		}
		//session.setAttribute("_subjects_before", listBefore);
		List<Subject> listBefore = (List) session.getAttribute("_subjects_before");
		
		context.put("original_subjects", listBefore);
		request.getSession().setAttribute("original_subjects", listBefore);
		
		List<Subject> listAfter = new ArrayList<Subject>();
		for ( StudentSubject s : studentStatus.getStudentSubjects() ) {
			listAfter.add(s.getSubject());
		}
		List<Subject> dropped_subjects = new ArrayList<Subject>();
		List<Subject> added_subjects = new ArrayList<Subject>();
		
		added_subjects.addAll(listAfter);
		if ( listBefore != null ) {
			for ( Subject s : listBefore ) {
				added_subjects.remove(s);
			}
		}
		context.put("added_subjects", added_subjects);
		request.getSession().setAttribute("added_subjects", added_subjects);
		
		dropped_subjects.addAll(listBefore);
		if ( listAfter != null ) {
			for ( Subject s : listAfter ) {
				dropped_subjects.remove(s);
			}
		}
		context.put("dropped_subjects", dropped_subjects);
		request.getSession().setAttribute("dropped_subjects", dropped_subjects);
		
		context.put("student_subjects", studentStatus.getStudentSubjects());
		
		
		//SAVE ADDDROP LOG
		String userid = session.getAttribute("_portal_login") != null ? (String) session.getAttribute("_portal_login") : "none";
		db.begin();
		SubjectAddDropLog log = new SubjectAddDropLog();
		log.setOriginalSubjects(new HashSet<Subject>());
		log.setDroppedSubjects(new HashSet<Subject>());
		log.setAddedSubjects(new HashSet<Subject>());
		log.setCurrentSubjects(new HashSet<Subject>());
		
		log.getOriginalSubjects().addAll(listBefore);
		log.getDroppedSubjects().addAll(dropped_subjects);
		log.getAddedSubjects().addAll(added_subjects);
		for ( StudentSubject s : studentStatus.getStudentSubjects()) {
			log.getCurrentSubjects().add(s.getSubject());
		}
		log.setUserId(userid);
		log.setStudent(studentStatus.getStudent());
		log.setSession(studentStatus.getSession());
		log.setPeriod(studentStatus.getPeriod());
		
		log.setDate(new Date());
		log.setTime(new Date());
		
		db.persist(log);
		db.commit();
		
		//OVERRIDE THIS METHOD
		getStudent();
		
	}



	private void registerStatus() {
		vm = path + "reg_status.vm";
		
		List<SubjectReg> listRegistered = (List) session.getAttribute("_list_registered");
		context.put("list_registered", listRegistered);
		
		List<SubjectReg> listDropped = (List) session.getAttribute("_list_dropped");
		context.put("list_dropped", listDropped);
		
		
	}



	private void cancelSubjectRegister() {
		vm = path + "subject_list_item.vm";
		String id = request.getParameter("subject_reg_id");
		SubjectReg s = (SubjectReg) db.find(SubjectReg.class, id);
		context.put("s", s);
		
		List<SubjectReg> listRegistered = (List) session.getAttribute("_list_registered");
		if ( listRegistered == null ) {
			listRegistered = new ArrayList<SubjectReg>();
			session.setAttribute("_list_registered", listRegistered);
		}
		context.put("list_registered", listRegistered);
		
		context.put("preRequisiteOK", true);
		
		listRegistered.remove(s);
	
	}

	/*
	 * WITH PRE-REQUISITES CHECKS
	 */
	private void subjectRegister() throws Exception {
		vm = path + "subject_list_item.vm";
		String id = request.getParameter("subject_reg_id");
		SubjectReg s = (SubjectReg) db.find(SubjectReg.class, id);
		context.put("s", s);
		
		System.out.println("Subject select: " + s.getSubject().getName());
		
		List<SubjectReg> listRegistered = (List) session.getAttribute("_list_registered");
		if ( listRegistered == null ) {
			listRegistered = new ArrayList<SubjectReg>();
			session.setAttribute("_list_registered", listRegistered);
		}
		context.put("list_registered", listRegistered);
		
		//check for pre-requisite
		//get prerequisite
		String studentStatusId = getParam("student_status_id");
		StudentStatus studentStatus = db.find(StudentStatus.class, studentStatusId);
		
		System.out.println("get student status: " + studentStatus.getPeriod().getName());
		
		List<Subject> preqs = s.getSubject().getPrerequisites();
		
		System.out.println("Pre Requisites: " + preqs);
		
		boolean preqOK = true;
		if ( preqs.size() > 0 ) {
			//list all registered subjects for this student
			
			Session currentSession = studentStatus.getSession();
			System.out.println("currentSession: " + currentSession);
			Student student = studentStatus.getStudent();
			System.out.println("student: " + student);
			Set<StudentStatus> allStatus = student.getStatus();
			System.out.println("allStatus: " + allStatus);
			
			
			List<Subject> subjects = new ArrayList<Subject>();
			for ( StudentStatus st : allStatus ) {
				Set<StudentSubject> studentSubjects = st.getStudentSubjects();
				
				System.out.println("student subjects: " + studentSubjects);
				for ( StudentSubject ss : studentSubjects ) 
					subjects.add(ss.getSubject());
				
				if ( st.getSession().getId().equals(currentSession.getId())) {
					break;
				}
			}
			int cnt = 0;
			List<Subject> matches = new ArrayList<Subject>();
			for ( Subject preqSubject : preqs ) {
				boolean f = false;
				for ( Subject subject : subjects ) {
					if ( preqSubject.getId().equals(subject.getId())) {
						f = true;
						matches.add(subject);
						break;
					}
				}
				if ( f ) {
					cnt++;
				}
			}

			if ( preqs.size() == cnt ) {
				preqOK = true;
				context.remove("pre_requisite_subjects");
			}
			else {
				preqOK = false;
				List<Subject> preReqSubjects = new ArrayList<Subject>();
				context.put("pre_requisite_subjects", preReqSubjects);
				for ( Subject preqSubject : preqs ) {
					boolean found = false;
					for ( Subject subject : matches ) {
						if ( subject.getId().equals(preqSubject.getId()) ) {
							found = true;
							break;
						}
					}
					if ( !found ) preReqSubjects.add(preqSubject);
				}
			}
		}
		
		context.remove("prerequisiteSubjectFailed");
		System.out.println("preqOK: " + preqOK);
		if ( preqOK ) {
			//prequisites is ok, but now check for exam results of the prerequisites
			System.out.println("checking preq exam result");
			boolean preqPassed = true;
			try {
				preqPassed = isPrerequisiteSubjectsPassed(studentStatus, preqs);
			} catch ( Exception e ) {
				e.printStackTrace();
			}
			
			System.out.println("preqPassed: " + preqPassed);
			if ( preqPassed ) {
				if ( !listRegistered.contains(s) ) listRegistered.add(s);
			} else {
				preqOK = false;
				context.put("prerequisiteSubjectFailed", true);
			}
		
			
			
			
		}
		context.put("preRequisiteOK", preqOK);
		
		System.out.println("ok");
	}
	




	private void statementAccount() {
		vm = path + "statement.vm";
	
		String studentId = request.getParameter("student_id");
		Student student = (Student) db.find(Student.class, studentId);
		context.put("student", student);
		
		AccountStatementUtil util = new AccountStatementUtil();
		AccountStatement acct = util.getAccountStatement(student);
		
		context.put("account_statement", acct);
		
	}


	private void registeredSubjects() throws Exception {
		vm = path + "registered_subjects.vm";
		String id = request.getParameter("student_id");
		Student student = (Student) db.find(Student.class, id);
		context.put("student", student);
		StudentStatusUtil util = new StudentStatusUtil();
		List<StudentStatus> statusList = util.getStudentStatuses(student);
		context.put("status_list", statusList);
		StudentStatusUtil u = new StudentStatusUtil();
		StudentStatus currentStatus = u.getCurrentStudentStatus(student);
		context.put("student_status", currentStatus);
	}
	
	
	private void getExamResultStatus(Student student, StudentStatus currentStatus) throws Exception {
		StudentStatusUtil su = new StudentStatusUtil();
		List<StudentStatus> studentStatuses = su.getStudentStatuses(student);
		Session prevSession = null;
		for ( StudentStatus s : studentStatuses ) {
			if ( s.getSession().getId().equals(currentStatus.getSession().getId()))  break;
			prevSession = s.getSession();
		}
		
		context.remove("subject_failed");
		int cnt = 0;
		if ( prevSession != null ) {
			String sql = "select r from FinalResult r where r.student.id = '" + student.getId() + "' and r.session.id = '" + prevSession.getId() + "'";
			FinalResult result = (FinalResult) db.get(sql);
			
			if ( result != null ) {
				List<FinalSubjectResult> subjectResults = result.getSubjects();
				int totalSubjects = subjectResults.size();
				for ( FinalSubjectResult sr : subjectResults ) {
					if ( sr.getPoint() == 0.0 ) {
						cnt++;
					}
				}
				
				if ( cnt == totalSubjects ) {
					context.put("subject_failed", true);
				}
			}
			else {
				context.put("subject_failed", true);
			}
			
		}
	}
	
	private boolean isPrerequisiteSubjectsPassed(StudentStatus studentStatus, List<Subject> preqs) throws Exception {
		List<Subject> failedSubjects = new ArrayList<Subject>();
		context.put("failed_pre_requisite_subjects", failedSubjects);
		StudentStatusUtil su = new StudentStatusUtil();
		List<StudentStatus> studentStatuses = su.getStudentStatuses(studentStatus.getStudent());
		Session prevSession = null;
		for ( StudentStatus s : studentStatuses ) {
			if ( s.getSession().getId().equals(studentStatus.getSession().getId()))  break;
			prevSession = s.getSession();
		}
		if ( prevSession != null ) {
			String sql = "select r from FinalResult r where r.student.id = '" + studentStatus.getStudent().getId() + "' and r.session.id = '" + prevSession.getId() + "'";
			FinalResult result = (FinalResult) db.get(sql);
			List<FinalSubjectResult> subjectResults = result.getSubjects();
			int cnt = 0;
			for ( FinalSubjectResult sr : subjectResults ) {
				if ( preqs.contains(sr.getSubject())) {
					if ( sr.getPoint() == 0.0 ) {
						cnt++;
						failedSubjects.add(sr.getSubject());
					}
				}
			}
			if ( cnt > 0 ) return false;
		}
		return true;
	}
	
	private void getSubjectList(Student student, StudentStatus currentStatus) throws Exception {
		String programId = student.getProgram().getId();
		String centreId = student.getLearningCenter().getId();
		String intakeId = student.getIntake().getId();
		String sql = "select p from ProgramStructure p where p.program.id = '" + programId + "' " +
					"and p.learningCenter.id = '" + centreId + "' " +
					"and p.session.id = '" + intakeId + "'";
		ProgramStructure ps = (ProgramStructure) db.get(sql);
		
		Set<SubjectReg> subjectRegs = new HashSet<SubjectReg>();
		
		System.out.println("Program Structure: " + ps.getProgram().getName());
		
		if ( ps != null ) {
			context.put("program_structure", ps);
			ProgramUtil pu = new ProgramUtil();
			Set<SubjectReg> regs = pu.getSubjectRegs(ps); //pu.getSubjectRegs(ps, currentStatus.getPeriod().getId());
			for ( SubjectReg r : regs ) {
				if ( r.getOffered() ) {
					subjectRegs.add(r);
					System.out.println("Offered: " + r.getSubject().getName());
				} else {
					System.out.println("NOT Offered: " + r.getSubject().getName());
				}
			}
			
			
			context.put("subject_list", subjectRegs);
			
			//make mapper for subjects
			Map<String, SubjectReg> subjectRegMapper = new HashMap<String, SubjectReg>();
			context.put("subjectRegMapper", subjectRegMapper);
			for ( SubjectReg s : subjectRegs ) {
				subjectRegMapper.put(s.getSubject().getId(), s);
			}
			
		} else {
			context.remove("program_structure");
			context.remove("subject_list");
		}
		
		Set<SubjectReg> addlist = new HashSet<SubjectReg>();
		Set<StudentSubject> studentSubjects = currentStatus.getStudentSubjects();
		
		if ( subjectRegs != null ) {
			addlist.addAll(subjectRegs);
			
			if ( studentSubjects != null ) {
				for ( SubjectReg reg : subjectRegs ) {
					for ( StudentSubject ss : studentSubjects ) {
						if ( reg.getSubject().getId().equals(ss.getSubject().getId())) {
							addlist.remove(reg);
						}
					}
				}
			}
		}
		
		System.out.println("add list");
		for ( SubjectReg r : addlist ) {
			System.out.println(r.getSubject().getName());
		}
		
		Set<StudentSubject> droplist = currentStatus.getStudentSubjects();
		
		//to match StudentSubject in droplist with SubjectReg from ProgramStructure
		context.put("preRequisiteOK", true);
		context.put("add_list", addlist);
		context.put("drop_list", droplist);
		
		List<Subject> listBefore = new ArrayList<Subject>();
		for ( StudentSubject s : droplist ) {
			listBefore.add(s.getSubject());
		}
		session.setAttribute("_subjects_before", listBefore);
		session.setAttribute("_list_registered", new ArrayList<SubjectReg>());
		session.setAttribute("_list_dropped", new ArrayList<SubjectReg>());
	}



	private void getStudent() throws Exception {
		vm = path + "student.vm";
		
		context.remove("list_registered");
		context.remove("list_dropped");
		
		String studentNo = "";
		if ( studentMode ) {
			vm = path + "start2.vm";
			studentNo = (String) session.getAttribute("_portal_login");
		}
		else {
			studentNo = request.getParameter("student_no");
		}
		context.put("student_no", studentNo);
		Student student = (Student) db.get("select s from Student s where s.matricNo = '" + studentNo + "'");
		if ( student != null ) {
			context.put("student", student);
			StudentStatusUtil u = new StudentStatusUtil();
			StudentStatus currentStatus = u.getCurrentStudentStatus(student);
			context.put("student_status", currentStatus);
			if ( currentStatus != null ) {
				
				boolean subjectsValidated = currentStatus.getSubjectsValidated();
				context.put("subjectsValidated", subjectsValidated);
				
				if ( subjectsValidated ) {
					System.out.println("getListSubjecsValidated");
					getListSubjectsValidated(currentStatus);
				} 
				else {
					System.out.println("getListSubjects");
					//previous semester exam result
					getExamResultStatus(student, currentStatus);
					
					//get list of subjects
					getSubjectList(student, currentStatus);
					
					//get outstanding amount
					AccountStatementUtil util = new AccountStatementUtil();
					AccountStatement acct = util.getAccountStatement(student);
					double balance = acct.getTotalBalance();
					context.put("outstanding_amount", balance);	
					
					//
					Program program = student.getProgram();
					Date date = new Date();
					
					Calendar cal = new GregorianCalendar();
					cal.setTime(program.getStartDate() != null ? program.getStartDate() : new Date());
					//cal.add(Calendar.DAY_OF_YEAR,-1);
					Date startDate = cal.getTime();
					
					Calendar cal2 = new GregorianCalendar();
					cal2.setTime(program.getEndDate() != null ? program.getEndDate() : new Date());
					//cal2.add(Calendar.DAY_OF_YEAR,1);
					Date endDate = cal2.getTime();
	
					boolean allow = false;
					allow = date.after(startDate) && date.before(endDate);
					context.put("allowSubjectRegister", allow);
				
				}
				
			}
			else {
				context.remove("student_status");
			}
			

			
			
		}
		else context.remove("student");
		
	}
	

	private void start() throws Exception {
		vm = path + "start.vm";
		if ( studentMode ) {
			getStudent();
		}
	}
	
	private void getParameters(Student student, StudentStatus studentStatus) {
		
		List<Period> periods = student.getProgram().getPeriodScheme().getFunctionalPeriodList();
		context.put("periods", periods);
		
		String programId = student.getProgram().getId();
		String centreId = student.getLearningCenter().getId();
		String intakeId = student.getIntake().getId();
		String sql = "select p from ProgramStructure p where p.program.id = '" + programId + "' " +
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
		
		List<SubjectSection> sections = db.list("select s from SubjectSection s order by s.code");
		context.put("sections", sections);
		
	}
	
	public static void main(String... args) throws Exception {
		DbPersistence db = new DbPersistence();
		String id = "1264755113273";
		Student student = (Student) db.find(Student.class, id);
		
		Session intake = student.getIntake();
		Program program = student.getProgram();
		LearningCentre centre = student.getLearningCenter();
		
		StudentStatusUtil util = new StudentStatusUtil();
		List<StudentStatus> statusList = util.getStudentStatuses(student);
		StudentStatus currentStatus = util.getCurrentStudentStatus(student);
		for ( StudentStatus s : statusList ) {
			Period period = s.getPeriod();
			List<InvoiceItem> invoiceItems = new InvoiceUtil().getInvoiceItems(student, period);
			if ( s.getId().equals(currentStatus.getId())) break;
		}

	}

}
