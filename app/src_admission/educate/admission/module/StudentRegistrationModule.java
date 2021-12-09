package educate.admission.module;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.math.RandomUtils;

import educate.admission.entity.Applicant;
import educate.admission.entity.OnappCentreMapper;
import educate.admission.entity.OnappIntakeMapper;
import educate.admission.entity.OnappProgramMapper;
import educate.admission.entity.RegistrationSlipItem;
import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.enrollment.entity.StudentStatus;
import educate.sis.module.QuickRegisterUtil;
import educate.sis.module.StudentStatusUtil;
import educate.sis.registration.StudentData;
import educate.sis.struct.entity.LearningCentre;
import educate.sis.struct.entity.Period;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.Session;
import educate.sis.struct.entity.StudentAccomodation;
import educate.sis.struct.entity.StudentAccomodationStatus;
import educate.sis.struct.entity.StudyMode;
import educate.util.BirthPlacePartException;
import educate.util.ContainsOnlyNumbersException;
import educate.util.DatePartNotValidException;
import educate.util.MyKad;
import educate.util.NumberLengthException;
import lebah.portal.action.LebahModule;
import onapp.entity.ApplicantOnline;
import onapp.entity.OnappIntakeSession;
import onapp.entity.OnappLearningCentre;
import onapp.entity.OnappProgram;

public class StudentRegistrationModule extends LebahModule {
	
	String path = "admission/registration/";
	private String vm = path + "empty.vm";
	HttpSession session;
	DbPersistence db = new DbPersistence();
	String userId = "";
	
	public void preProcess() {
		context.put("path", path);
		session = request.getSession();
		userId = (String) request.getSession().getAttribute("_portal_login");
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy"));
		context.put("timeFormat", new SimpleDateFormat("hh:mm a"));	
		context.put("numFormat", new DecimalFormat("#,###,###.00"));
		context.remove("message");
		context.remove("error");
		context.remove("fee_structure_error");
		context.remove("matric_duplicate");
		context.remove("checkedItems");
		lebah.util.Util util = new lebah.util.Util();
		context.put("util", util);		
			
		
		context.remove("registerNew");
	}
	
	@Override
	public String start() {
		context.remove("applicant");
		prepareParams();
		return path + "start.vm";
	}


	@Override
	public String doCommand() throws Exception {
		System.out.println("register student - command=" + command);	
		if ( "get_applicant".equals(command)) getApplicant();
		else if ( "get_applicant_by_id".equals(command)) getApplicantById();
		else if ( "check_mykad".equals(command)) checkMyKad();
		else if ( "check_passport".equals(command)) checkPassport();
		else if ( "check_matricNo".equals(command)) checkMatricNo();
		else if ( "check_name".equals(command)) checkName();
		else if ( "list_intakes".equals(command)) listIntakes();
		
		else if ( "listEnrollmentIntakes".equals(command)) listEnrollmentIntakes();
		
		else if ( "confirmBeforeRegister".equals(command)) confirmBeforeRegister();
		
		else if ( "register_new_student".equals(command)) registerNewStudent();
		else if ( "register_done".equals(command)) registerDone();
		else if ( "print_slip".equals(command)) printSlip();
		else if ( "get_accomodation".equals(command)) getAccomodation();
		else if ( "select_accomodation".equals(command)) selectAccomodation();
		else if ( "save_documents".equals(command)) saveDocuments();
		
		else if ( "changeEntryLevel".equals(command)) changeEntryLevel();
		else if ( "saveEntryLevel".equals(command)) saveEntryLevel();
		
		else if ( "listApplicants".equals(command)) listApplicants();
		else if ( "listApplicants2".equals(command)) listApplicants2();
		else if ( "searchApplicants".equals(command)) searchApplicants();
		
		else if ( "getRegisterData".equals(command)) getRegisterData();
		else if ( "getRegisteredStudent".equals(command)) getRegisteredStudent();
		else if ( "getRegistrationSlip".equals(command)) getRegistrationSlip();
		else if ( "getMatricCard".equals(command)) getMatricCard();
		else if ( "uploadDocument".equals(command)) uploadFile();
		else if ( "getUpdateRegistration".equals(command)) getUpdateRegistration();
		else if ( "getUpdateIntakes".equals(command)) getUpdateIntakes();
		else if ( "saveUpdateRegistration".equals(command)) saveUpdateRegistration();
		else if ( "getUpdateAddtionalInfo".equals(command)) getUpdateAdditionalInfo();
		else if ( "saveAdditionalInfo".equals(command)) saveAdditionalInfo();
		return vm;
	}

	
	/**
	 * @throws Exception 
	 * 
	 */
	private void saveEntryLevel() throws Exception {
		String studentId = getParam("studentId");
		Student student = db.find(Student.class, studentId);
		context.put("student", student);
		
		String periodId = getParam("periodId");
		Period period = db.find(Period.class, periodId);
		Program program = student.getProgram();
		List<Period> periods = program.getPeriodScheme().getFunctionalPeriodList();
		Period firstPeriod = periods.get(0);
		if ( firstPeriod.getId().equals(period.getId())) {
			db.begin();
			student.setEntryLevel(null);
			db.commit();
		} else {
			db.begin();
			student.setEntryLevel(period);
			db.commit();
		}
		//int pathNo = program.getLevel().getPathNo();
		//List<Session> sessions = db.list("select s from Session s where s.pathNo = " + pathNo + " order by s.startDate");

		StudentStatusUtil u = new StudentStatusUtil();
		u.setUseSubjectTemp(false);
		u.cleanUpStudentStatuses(student);
		StudentStatus currentStatus = u.addStatus2(student, student.getIntake(), period, true);
		
		if ( student.getProgram().getIsNoneSessionType() ) {
			u.updateCurrentStudentStatus(student, currentStatus);
		}
		
		List<StudentStatus> statuses = u.getStudentStatuses(student);
		

		
		if ( statuses.size() > 0 ) {
			/*
			List<StudentStatus> statusesToRemove = new ArrayList<StudentStatus>();
			boolean doneRemove = false;
			for ( StudentStatus status : statuses ) {
				if ( period.getId().equals(status.getPeriod().getId())) {
					doneRemove = true;
				}
				if ( !doneRemove ) {
					statusesToRemove.add(status);
				}
			}
			for ( StudentStatus s : statusesToRemove ) {
				db.begin();
				student.getStatus().remove(s);
				statuses.remove(s);
				db.remove(s);
				db.commit();
			}
			*/
			
			
			context.put("statuses", statuses);
			context.put("status", statuses.get(0));
		} else {
			context.remove("statuses");
			context.remove("status");
		}
		
		vm = path + "updateEntryLevel.vm";
		
	}

	/**
	 * 
	 */
	private void changeEntryLevel() {
		String studentId = getParam("studentId");
		Student student = db.find(Student.class, studentId);
		context.put("student", student);
		
		Program program = student.getProgram();
		List<Period> periods = program.getPeriodScheme().getFunctionalPeriodList();
		context.put("periods", periods);
		
		/*
		StudentStatusUtil u = new StudentStatusUtil();
		List<StudentStatus> statuses = u.getStudentStatuses(student);
		if ( statuses.size() > 0 ) {
			context.put("statuses", statuses);
			context.put("status", statuses.get(0));
		} else {
			context.remove("statuses");
			context.remove("status");
		}
		*/
		
		vm = path + "changeEntryLevel.vm";
		
	}

	private void saveAdditionalInfo() throws Exception {
		String studentId = getParam("studentId");
		Student student = db.find(Student.class, studentId);
		context.put("student", student);
		db.begin();
		student.setInstitutionSPM(getParam("institutionSPM"));
		student.setInstitutionTypeSPM(getParam("institutionTypeSPM"));
		student.setInstitutionSTPM(getParam("institutionSTPM"));
		student.setInstitutionTypeSTPM(getParam("institutionTypeSTPM"));
		
		student.setStatusTag(getParam("statusTag"));
		
		db.commit();
		
		vm = path + "saveAdditionalInfo.vm";
	}

	private void getUpdateAdditionalInfo() {
		String studentId = getParam("studentId");
		Student student = db.find(Student.class, studentId);
		context.put("student", student);
		
		vm = path + "update_addtional_info.vm";
	}

	private void saveUpdateRegistration() throws Exception {
		String studentId = getParam("studentId");
		Student student = db.find(Student.class, studentId);
		context.put("student", student);
		
		String _createStatus = getParam("createStatus");
		boolean createStatus = "yes".equals(_createStatus) ? true : false;
		
		String displayName = getParam("updateDisplayName");
		
		String matricNo = getParam("updateMatricNo");
		String programId = getParam("updateProgramId");
		String intakeId = getParam("updateIntakeId");
		String centreId = getParam("updateCentreId");
		
		Program program = db.find(Program.class, programId);
		Session intake = db.find(Session.class, intakeId);
		LearningCentre centre = db.find(LearningCentre.class, centreId);
		
		QuickRegisterUtil.updateStudentRegistration(db, context, matricNo, displayName, student, program, intake, centre, createStatus, userId);
		
		int i = matricNo.lastIndexOf(" ");
		if ( i > 0 ) {
			String s = matricNo.substring(i+1);
			StudentData.updatePortalPassword(matricNo, s);
			db.begin();
			student.getBiodata().setEmailId(s);
			student.getBiodata().setEmailPassword(s);
			db.commit();
		}
		
		List<RegistrationSlipItem> checkedItems = db.list("select i from RegistrationSlipItem i where i.student.id = '" + student.getId() + "' order by i.sequence");
		context.put("checkedItems", checkedItems);
		
		vm = path + "registration.vm";
	}
	
	private void getUpdateIntakes() {
		String programId = getParam("updateProgramId");
		Program program = db.find(Program.class, programId);
		if ( program != null ) {
			if ( program.getLevel() == null ) System.out.println("WARNING: Program " + program.getCode() + " " + program.getName() + " has NO LEVEL!");
			int pathNo = program.getLevel() != null ? program.getLevel().getPathNo() : 0;
			List<Session> intakes = db.list("select s from Session s where s.pathNo = " + pathNo + " order by s.startDate");
			context.put("intakes", intakes);
			try {
				Session currentSession = new StudentStatusUtil().getCurrentSession(pathNo);
				context.put("current_session", currentSession);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}		
		vm = path + "divUpdateIntakes.vm";
		
	}

	private void getUpdateRegistration() {
		String studentId = getParam("studentId");
		Student student = db.find(Student.class, studentId);
		context.put("student", student);
		context.put("current_session", student.getIntake());
		Program program = student.getProgram();
		context.put("program", program);
		LearningCentre centre = student.getLearningCenter();
		context.put("centre", centre);
		if ( program != null ) {
			if ( program.getLevel() == null ) System.out.println("WARNING: Program " + program.getCode() + " " + program.getName() + " has NO LEVEL!");
			int pathNo = program.getLevel() != null ? program.getLevel().getPathNo() : 0;
			List<Session> intakes = db.list("select s from Session s where s.pathNo = " + pathNo + " order by s.startDate");
			context.put("intakes", intakes);
		}
		List<Program> programs = db.list("SELECT a from Program a order by a.level.levelOrder, a.code");
		context.put("programs",programs);
		
		List<LearningCentre> centres = db.list("select c from LearningCentre c order by c.code");
		context.put("centres", centres);
		
		//allow update program
		int cnt = 0;
		cnt = db.list("select f from FinalResult f where f.student.id = '" + student.getId() + "'").size();
		context.put("allowChangeProgram", cnt == 0);
		
		vm = path + "update_registration.vm";
	}

	private void checkMatricNo() {
		String matricNo = getParam("matricNo2");
		List<Student> students = db.list("select s from Student s where s.matricNo = '" + matricNo + "'");
		if ( students.size() > 0 ) {
			context.put("gotMatricNo", true);
			context.put("message", "matric_no_already_exists");
			context.put("error", true);
			vm = path + "message.vm";
			return;
		}
		else {
			context.remove("gotMatricNo");
			context.put("message", "matric_no_ok");
		}
		vm = path + "message.vm";
	}

	private void getMatricCard() {
		String studentId = getParam("studentId");
		Student student = db.find(Student.class, studentId);
		context.put("student", student);
		int randomNo = RandomUtils.nextInt();
		context.put("randomNo", randomNo);
		vm = path + "getMatricCard.vm";
		
	}

	private void getRegistrationSlip() throws Exception {
		
		String studentId = getParam("studentId");
		Student student = db.find(Student.class, studentId);
		context.put("student", student);
		
		db.begin();
		db.executeUpdate("delete from RegistrationSlipItem i where i.student.id = '" + studentId + "'");
		db.commit();
		
		
		String[] verifications = request.getParameterValues("verifications");
		if ( verifications != null ) {
			db.begin();
			List<String> verificationList = new ArrayList<String>();
			context.put("verificationList", verificationList);
			int i = 0;
			for ( String s : verifications ) {
				i++;
				RegistrationSlipItem item = new RegistrationSlipItem();
				item.setStudent(student);
				item.setItemName(s);
				item.setSequence(i);
				db.persist(item);
			}
			db.commit();
		} else {
			context.remove("verificationList");
		}
		
		vm = path + "getRegistrationSlip.vm";
		
	}

	private void getRegisterData() {
		prepareParams();
		
		String applicantId = getParam("applicantId");
		ApplicantOnline applicant = db.find(ApplicantOnline.class, applicantId);
		context.put("applicant", applicant);
		context.put("icno", applicant.getIcno());
		context.put("passport", applicant.getPassport());
		context.put("name", applicant.getName());
		
		
		//check if icno already exits in registered students
		String icno = applicant.getIcno();
		checkDuplicateICNo(icno);
		
		
		OnappProgram onappProgram = applicant.getOfferedProgram();
		Program program = null;
		if ( onappProgram != null ) {
			OnappProgramMapper p = (OnappProgramMapper) db.get("select m from OnappProgramMapper m where m.onappProgram.id = '" + onappProgram.getId() + "'");
			program = p != null ? p.getProgram() : null;
			context.put("program", program);
		} else context.remove("program");
		
		OnappIntakeSession onappIntake = applicant.getIntake();
		if ( onappIntake != null ) {
			OnappIntakeMapper i = (OnappIntakeMapper) db.get("select m from OnappIntakeMapper m where m.onappIntake.id = '" + onappIntake.getId() + "'");
			Session intake =  i != null ? i.getIntake() : null;
			context.put("intake", intake);
		} else context.remove("intake");
		
		OnappLearningCentre onappCentre = applicant.getCentre();
		if ( onappCentre != null ) {
			OnappCentreMapper c = (OnappCentreMapper) db.get("select m from OnappCentreMapper m where m.onappCentre.id = '" + onappCentre.getId() + "'");
			LearningCentre centre =  c != null ? c.getCentre(): null;
			context.put("centre", centre);
		} else context.remove("centre");
		
//		if ( program != null ) {
//			if ( program.getLevel() == null ) System.out.println("WARNING: Program " + program.getCode() + " " + program.getName() + " has NO LEVEL!");
//			int pathNo = program.getLevel() != null ? program.getLevel().getPathNo() : 0;
//			
//			List<Session> intakes = db.list("select s from Session s where s.pathNo = " + pathNo + " order by s.startDate");
//			context.put("intakes", intakes);
//			try {
//				Session currentSession = new StudentStatusUtil().getCurrentSession(pathNo);
//				context.put("current_session", currentSession);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
		
		vm = path + "entry_data.vm";
	}

	private void checkDuplicateICNo(String icno) {
		
		context.put("message", "");
		context.put("error", false);
		
		if (db.list("select s from Student s where s.biodata.icno = '" + icno + "'").size() > 0) {
			context.put("message", "mykad_already_exists");
			context.put("error", true);
			return;
		}
		
		if ( icno.indexOf("-") > 0 ) {
			String s = icno.replaceAll("-", "");
			if (db.list("select s from Student s where s.biodata.icno = '" + s + "'").size() > 0) {
				context.put("message", "mykad_already_exists");
				context.put("error", true);
				return;
			}
		}
		
		if ( icno.indexOf("-") < 0 ) {
			if ( icno.length() == 12 ) {
				String icleft = icno.substring(0, 6);
				String icmiddle = icno.substring(6,8);
				String icright = icno.substring(8);
				String s = icleft + "-" + icmiddle + "-" + icright;
				if (db.list("select s from Student s where s.biodata.icno = '" + s + "'").size() > 0) {
					context.put("message", "mykad_already_exists");
					context.put("error", true);
					return;
				}
			}
		}
	}

	private String get(String name) {
		return request.getParameter(name) != null ? request.getParameter(name) : "";
	}
	
	private boolean getBoolean(String name) {
		return request.getParameter(name) != null ? "1".equals(request.getParameter(name)) ? true : false : false;
	}
	
	
	private void saveDocuments() throws Exception {
		
		vm = path + "empty.vm";
		
		String studentId = request.getParameter("student_id");
		Student student = (Student) db.find(Student.class, studentId);
		
		Applicant applicant = student.getApplicant();
		if ( applicant != null ) {
			db.begin();
			System.out.println(getBoolean("submitIC"));
			applicant.setSubmitIC(getBoolean("submitIC"));
			applicant.setSubmitBirthCert(getBoolean("submitBirthCert"));
			applicant.setSubmitDiploma(getBoolean("submitDiploma"));
			applicant.setSubmitPhotos(getBoolean("submitPhotos"));
			applicant.setSubmitSijilBerhenti(getBoolean("submitSijilBerhenti"));
			applicant.setSubmitSPM(getBoolean("submitSPM"));
			applicant.setSubmitSTPM(getBoolean("submitSTPM"));
			applicant.setSubmitMoney(getBoolean("submitMoney"));
			applicant.setSubmitMUET(getBoolean("submitMUET"));
			db.commit();
		}
		
	}

	private void selectAccomodation() throws Exception {
		String accomodationId = request.getParameter("accomodation_id");
		StudentAccomodation accomodation = (StudentAccomodation) db.find(StudentAccomodation.class, accomodationId);
		context.put("accomodation", accomodation);
		
		String studentId = request.getParameter("student_id");
		Student student = (Student) db.find(Student.class, studentId);
		
		//check whether accomodation for this student exist for this
		
		List<StudentAccomodationStatus> list = db.list("select s from StudentAccomodationStatus s where s.student.id = '" + student.getId() + "'");// and s.accomodation.id = '" + accomodation.getId() + "'");
		
		if ( list.size() == 0 ) {
			db.begin();
			StudentAccomodationStatus s = new StudentAccomodationStatus();
			s.setAccomodation(accomodation);
			s.setStudent(student);
			s.setDate(new Date());
			s.setExpired(false);
			s.setTime(new Date());
			db.persist(s);
			db.commit();
		}
		else if ( list.size() == 1 ) {
			StudentAccomodationStatus s = list.get(0);
			db.begin();
			s.setAccomodation(accomodation);
			s.setDate(new Date());
			s.setExpired(false);
			s.setTime(new Date());
			db.commit();
		}
		else if ( list.size() > 1 ) { //should not be in the first place
			for ( StudentAccomodationStatus s : list ) {
				db.begin();
				db.remove(s);
				db.commit();
			}
			db.begin();
			StudentAccomodationStatus s = new StudentAccomodationStatus();
			s.setAccomodation(accomodation);
			s.setStudent(student);
			s.setDate(new Date());
			s.setExpired(false);
			s.setTime(new Date());
			db.persist(s);
			db.commit();			
		}
		
		vm = path + "set_accomodation.vm";
	}

	private void getAccomodation() {
		String studentId = request.getParameter("student_id");
		Student student = (Student) db.find(Student.class, studentId);
		context.put("student", student);
		QuickRegisterUtil.getAccomodation(db, context, student);
		vm = path + "accomodation.vm";
	}


	private void getApplicantById() {
		
		String id = request.getParameter("applicant_id");
		Applicant applicant = (Applicant) db.find(Applicant.class, id);
		context.put("applicant", applicant);
		
		if ( "REGISTERED".equals(applicant.getStatus())) {
			System.out.println("registered..");
			Student student = (Student) db.get("select s from Student s where s.applicant.id = '" + applicant.getId() + "'");
			context.put("student", student);
			
			//accomodation
			StudentAccomodationStatus accomodationStatus = (StudentAccomodationStatus) db.get("select s from StudentAccomodationStatus s where s.student.id = '" + student.getId() + "' and s.expired = 0 order by s.date desc");
			if ( accomodationStatus != null ) {
				StudentAccomodation accomodation = accomodationStatus.getAccomodation();
				context.put("accomodation", accomodation);
			}
			else {
				context.remove("accomodation");
			}
			
			vm = path + "registration.vm";
			return;
		}
		
		String programId = "";
		if ( applicant.getProgramOffered() != null ) programId = applicant.getProgramOffered().getId();
		else if ( applicant.getChoice1() != null ) programId = applicant.getChoice1().getId();
		
		context.put("program_id", programId);
		Program program = (Program) db.find(Program.class, programId);
		
		if ( program.getLevel() == null ) System.out.println("WARNING: Program " + program.getCode() + " " + program.getName() + " has NO LEVEL!");
		
		int pathNo = program.getLevel() != null ? program.getLevel().getPathNo() : 0;
		
		List<Session> intakes = db.list("select s from Session s where s.pathNo = " + pathNo + " order by s.startDate");
		context.put("intakes", intakes);
		
		try {
			Session currentSession = new StudentStatusUtil().getCurrentSession(pathNo);
			context.put("current_session", currentSession);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		prepareParams();
		vm = path + "entry.vm";

	}
	
	private void getApplicant() {
		
		String findNo = request.getParameter("find_no");
		
		if ( !"".equals(findNo)) {

			Applicant applicant = null;
			
			//look for reference no first
			applicant = (Applicant) db.get("select a from Applicant a where a.referenceNo = '" + findNo + "'");
			if ( applicant != null ) {
				context.put("applicant", applicant);
			} else {
				
				List<Applicant> applicants = db.list("select a from Applicant a where a.biodata.icno = '" + findNo + "' order by a.applyDate DESC");
				if ( applicants.size() > 1 ) {
					context.put("applicants", applicants);
				} else if ( applicants.size() == 1 ) {
					applicant = applicants.get(0);
					context.put("applicant", applicant);
				} else if ( applicants.size() == 0 ) {
					applicants = db.list("select a from Applicant a where a.biodata.passport = '" + findNo + "' order by a.applyDate DESC");
					if ( applicants.size() == 1 ) {
						applicant = applicants.get(0);
						context.put("applicant", applicant);
					} else if ( applicants.size() > 1 ) {
						context.put("applicants", applicants);
					}
				}
				
				if ( applicants.size() > 1 ) {
					vm = path + "find_applicants.vm";
					return;
				} else if ( applicants.size() == 0 ) {
					vm = path + "no_data.vm";
					return;
				}
			}
	
			if ( applicant == null ) context.remove("applicant");
			else context.put("applicant", applicant);
			
			if ( applicant != null ) {
				
				if ( "REGISTERED".equals(applicant.getStatus())) {
					System.out.println("registered..");
					Student student = (Student) db.get("select s from Student s where s.applicant.id = '" + applicant.getId() + "'");
					context.put("student", student);
					
					//accomodation
					
					QuickRegisterUtil.getAccomodation(db, context, student);
					
					vm = path + "registration.vm";
					return;
				}
				
				String programId = "";
				if ( applicant.getProgramOffered() != null ) programId = applicant.getProgramOffered().getId();
				else if ( applicant.getChoice1() != null ) programId = applicant.getChoice1().getId();
				
				context.put("program_id", programId);
				Program program = (Program) db.find(Program.class, programId);
				
				if ( program.getLevel() == null ) System.out.println("WARNING: Program " + program.getCode() + " " + program.getName() + " has NO LEVEL!");
				
				int pathNo = program.getLevel() != null ? program.getLevel().getPathNo() : 0;
				
				List<Session> intakes = db.list("select s from Session s where s.pathNo = " + pathNo + " order by s.startDate");
				context.put("intakes", intakes);
				
				try {
					Session currentSession = new StudentStatusUtil().getCurrentSession(pathNo);
					context.put("current_session", currentSession);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else {
				context.remove("intakes");
				

				
				
			}
		} else {
			context.remove("applicant");
			context.remove("intakes");
		}
		
		prepareParams();
		vm = path + "entry.vm";
		
	}



	private void checkName() {
		// 
		String name = request.getParameter("name");
		String gender = request.getParameter("mykad_gender");
		if ( gender != null && !"".equals(gender)) {
			boolean ischeck = false;
			String g = "";
			if ( name.toLowerCase().indexOf(" bin ") > 0 || name.toLowerCase().indexOf(" b. ") > 0 	|| name.toLowerCase().indexOf(" b ") > 0) { 
				g = "M";
				ischeck = true;
			}
			else if ( name.toLowerCase().indexOf(" binti ") > 0 || name.toLowerCase().indexOf(" bt. ") > 0 || name.toLowerCase().indexOf(" bte. ") > 0
					|| name.toLowerCase().indexOf(" bt ") > 0 || name.toLowerCase().indexOf(" bte ") > 0) { 
				g = "F";
				ischeck = true;
			}
			else if ( name.toLowerCase().indexOf(" a/l ") > 0 ) { 
				g = "M";	
				ischeck = true;
			}
			else if ( name.toLowerCase().indexOf(" a/p ") > 0 ) { 
				g = "F";
				ischeck = true;
			}
			
			if ( ischeck ) {
				if ( !"".equals(gender) && !gender.equals(g)) {
					context.put("message", "name_gender_not_match");
					context.put("error", true);
					vm = path + "message.vm";
					return;
				}
			}
		}
		
		context.put("message", "name_ok");
		context.put("error", false);
		vm = path + "message.vm";
	}



	private void printSlip() {
		// TODO Auto-generated method stub
		String studentId = request.getParameter("student_id");
		Student student = (Student) db.find(Student.class, studentId);
		context.put("student", student);
		
		vm = path + "print_slip.vm";
		
	}

	private void registerDone() {
		prepareParams();
		
		vm = path + "entry.vm";
		
	}
	
	private void confirmBeforeRegister() throws Exception {
		String registerProgramId = getParam("registerProgramId");
		Program program = db.find(Program.class, registerProgramId);
		context.put("registerProgram", program);
		vm = path + "confirmRegisterProgram.vm";
	}

	private void registerNewStudent() throws Exception {

		//is there applicant info?
		String applicantId = request.getParameter("applicant_id");

		ApplicantOnline applicant = null;
		if ( applicantId != null ) {
			applicant = (ApplicantOnline) db.find(ApplicantOnline.class, applicantId);
		}
		
		String matricPrefix = request.getParameter("matric_prefix");
		if ( matricPrefix == null ) matricPrefix = "";
		
		String matricNo = getParam("matricNo2");
		//confirm that this matricNo does not exist before adding new student
		if ( ((Long) db.get("select count(s) from Student s where s.matricNo = '" + matricNo + "'") ) == 0 ) { 
			
			context.put("matric_duplicate", false);
			
			String icno = getParam("icno");
			String passport = getParam("passport");
			String name = getParam("name");
			
			String displayName = getParam("displayName");
			String programId = getParam("registerProgramId"); 
			
			String studyModeId = getParam("studyModeId");
			
			String intakeId = getParam("intake_id");
			String centreId = getParam("centre_id");
			String userId = request.getSession().getAttribute("_portal_login") != null ? (String) request.getSession().getAttribute("_portal_login") : "";
			
			Student student = QuickRegisterUtil.studentRegistration(db, context, applicant, matricPrefix, icno, passport, name, displayName, programId, intakeId, centreId, userId, matricNo, studyModeId);

			StudentStatusUtil u = new StudentStatusUtil();
			
			List<StudentStatus> statuses = u.getStudentStatuses(student);
			if ( statuses.size() > 0 ) {
				context.put("statuses", statuses);
				context.put("status", statuses.get(0));
			} else {
				context.remove("statuses");
				context.remove("status");
			}
			
			
			context.put("student", student);
			context.put("registerNew", true);
			context.put("registration_success", false);
			vm = path + "registration.vm";
		}
		else {
			context.put("matric_duplicate", true);
			context.put("registration_success", false);
			vm = path + "registration_failed.vm";
		}
		
	}


	private void listIntakes() {
		
		String programId = request.getParameter("program_id");
		if ( "".equals(programId) ) {
			context.remove("intakes");
			vm = path + "list_intakes.vm";
			return;
		}
		
		context.put("program_id", programId);
		Program program = (Program) db.find(Program.class, programId);
		
		if ( program.getLevel() == null ) {
			System.out.println("WARNING: Program " + program.getCode() + " " + program.getName() + " has NO LEVEL!");
		}
		
		int pathNo = program.getLevel() != null ? program.getLevel().getPathNo() : 0;
		
		List<Session> intakes = db.list("select s from Session s where s.pathNo = " + pathNo + " order by s.startDate");
		context.put("intakes", intakes);
		
		try {
			Session currentSession = new StudentStatusUtil().getCurrentSession(pathNo);
			context.put("current_session", currentSession);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		vm = path + "list_intakes.vm";
		
	}
	
	private void listEnrollmentIntakes() {
		
		String sessionId = getParam("intake_id");
		Session session = db.find(Session.class, sessionId);
		context.put("session", session);
		
		vm = path + "list_batches.vm";
		
	}
	
	private void checkPassport() {
		String passport = request.getParameter("passport");
		if (db.list("select s from Student s where s.biodata.passport = '" + passport + "'").size() > 0) {
			context.put("message", "passport_already_exists");
			context.put("error", true);
			vm = path + "message.vm";
			return;
		}
		
		context.put("message", "passport_ok");
		vm = path + "message.vm";
	}

	private void checkMyKad() {
		// 
		String icno = request.getParameter("icno");
		if ( "".equals(icno.trim())) {
			context.put("message", "empty");
			context.put("error", true);
			vm = path + "message.vm";
			return;
		}
		
		//if all pass.. check for existence in records
		
		if (db.list("select s from Student s where s.biodata.icno = '" + icno + "'").size() > 0) {
			context.put("message", "mykad_already_exists");
			context.put("error", true);
			vm = path + "message.vm";
			return;
		}
		
		if ( icno.indexOf("-") > 0 ) {
			String s = icno.replaceAll("-", "");
			if (db.list("select s from Student s where s.biodata.icno = '" + s + "'").size() > 0) {
				context.put("message", "mykad_already_exists");
				context.put("error", true);
				vm = path + "message.vm";
				return;
			}
		}
		
		if ( icno.indexOf("-") < 0 ) {
			if ( icno.length() == 12 ) {
				String icleft = icno.substring(0, 6);
				String icmiddle = icno.substring(6,8);
				String icright = icno.substring(8);
				String s = icleft + "-" + icmiddle + "-" + icright;
				if (db.list("select s from Student s where s.biodata.icno = '" + s + "'").size() > 0) {
					context.put("message", "mykad_already_exists");
					context.put("error", true);
					vm = path + "message.vm";
					return;
				}
			}
		}
		
		
		MyKad mykad;
		try {
			mykad = new MyKad(icno);
		} catch (ContainsOnlyNumbersException e) {
			context.put("message", "mykad_contains_non_numbers");
			context.put("error", true);
			vm = path + "message.vm";
			return;
		} catch (DatePartNotValidException e) {
			context.put("message", "mykad_date_not_valid");
			context.put("error", true);
			vm = path + "message.vm";
			return;
		} catch ( NumberLengthException e ) {
			context.put("message", "mykad_number_of_characters_not_valid");
			context.put("error", true);
			vm = path + "message.vm";
			return;
		} catch ( BirthPlacePartException e ) {
			context.put("message", "mykad_birth_place_not_valid");
			context.put("error", true);
			vm = path + "message.vm";
			return;
		}
	

		
		if ( mykad != null ) {
			context.put("mykad", mykad);
		}
		else context.remove("mykad");
		
		context.put("message", "mykad_ok");
		
		vm = path + "message.vm";
		
	}



	private void prepareParams() {
		List<Program> programs = db.list("SELECT a from Program a order by a.level.levelOrder, a.code");
		context.put("programs",programs);
		
		List<LearningCentre> centres = db.list("select c from LearningCentre c order by c.code");
		context.put("centres", centres);
		
		List<StudyMode> studyModes = db.list("select f from StudyMode f order by f.name");
		if ( studyModes.size() == 0 ) {
			try {
				db.begin();
				StudyMode ft = new StudyMode();
				ft.setCode("FT");
				ft.setName("FULL TIME");
				ft.setMatricCode("FT");
				db.persist(ft);
				db.commit();
				
				db.begin();
				StudyMode pt = new StudyMode();
				pt.setCode("PT");
				pt.setName("PART TIME");
				pt.setMatricCode("PT");
				db.persist(pt);
				db.commit();
			} catch ( Exception e ) {
				e.printStackTrace();
			}
			studyModes = db.list("select f from StudyMode f order by f.name");
		}
		context.put("studyModes", studyModes);
		
		context.remove("intakes");
	}
	
	private void listApplicants() throws Exception {
		List<ApplicantOnline> applicants = db.list("select a from ApplicantOnline a where a.status.code = 'A' and a.icno not like 'X-%' order by a.id");
		context.put("applicants", applicants);		
		vm = path + "listApplicants.vm";
	}
	
	private void listApplicants2() throws Exception {
		String applicantStatus = getParam("applicantStatus");
		if ( "".equals(applicantStatus)) applicantStatus = "A";
		List<ApplicantOnline> applicants = db.list("select a from ApplicantOnline a where a.status.code = '" + applicantStatus + "' and a.icno not like 'X-%' order by a.id");
		context.put("applicants", applicants);		
		vm = path + "listApplicants2.vm";
	}
	
	private void searchApplicants() throws Exception {
		String name = getParam("applicantName");
		List<ApplicantOnline> applicants = db.list("select a from ApplicantOnline a where a.name like '%" + name + "%' and a.icno not like 'X-%' order by a.id");
		context.put("applicants", applicants);		
		vm = path + "listApplicants2.vm";
	}
	
	private void getRegisteredStudent() throws Exception {
		String matricNo = getParam("matricNo");
		Student student = (Student) db.get("select s from Student s where s.matricNo = '" + matricNo + "'");
		context.put("student", student);
		
		if ( student != null ) {
			StudentStatusUtil u = new StudentStatusUtil();
			StudentStatus status = u.getFirstStudentStatus(student);
			context.put("status", status);
			
			List<RegistrationSlipItem> checkedItems = db.list("select i from RegistrationSlipItem i where i.student.id = '" + student.getId() + "' order by i.sequence");
			context.put("checkedItems", checkedItems);
		} else {
			context.remove("student");
			context.remove("status");
		}
		
		
		vm = path + "registration.vm";
	}
	
	
	private void uploadFile() throws Exception {
		
		String uploadDir = "/uploadDir/";
		try {
			
			ResourceBundle rb = ResourceBundle.getBundle("files");
			uploadDir = rb.getString("uploadDir");
			if ( !uploadDir.endsWith("/") ) uploadDir += "/";
		
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		
		String divUploadStatusName = getParam("divUploadStatusName");
		System.out.println("divUploadStatusName=" + divUploadStatusName);
		
		context.put("divUploadStatusName", divUploadStatusName);
		String documentId = getParam("documentId");
		
		File dir = new File(uploadDir);
		if ( !dir.exists() ) dir.mkdir();
		DiskFileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		List items = upload.parseRequest(request);
		Iterator itr = items.iterator();
		List<FileItem> files = new ArrayList<FileItem>();
		while (itr.hasNext()) {
			FileItem item = (FileItem)itr.next();
			if ((!(item.isFormField())) && (item.getName() != null) && (!("".equals(item.getName())))) {
				if ( documentId.equals(item.getFieldName())) {
					files.add(item);
				}
			}
		}
		for ( FileItem item : files ) {
			String fileName = item.getName();
			String savedName = uploadDir + fileName;
			context.put("serverFileName", savedName);
			item.write(new File(savedName));
			
			if ( "photo".equals(documentId)) { 
				System.out.println("uploaded photo!");
				
				String avatarName = savedName.substring(0, savedName.lastIndexOf(".")) + "_avatar" + savedName.substring(savedName.lastIndexOf("."));
				lebah.repository.Thumbnail.create(savedName, savedName, 200, 160, 100);
				lebah.repository.Thumbnail.create(savedName, avatarName, 50, 40, 100);
				context.put("avatarName", avatarName);
				
				String studentId = getParam("studentId");
				if ( !"".equals(studentId)) {
					Student student = db.find(Student.class, studentId);
					db.begin();
					try {
						InputStream is1 = new FileInputStream(savedName);
						student.setPhoto(IOUtils.toByteArray(is1));
						student.setPhotoFileName(savedName);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}

					try {
						InputStream is2 = new FileInputStream(avatarName);
						student.setAvatar(IOUtils.toByteArray(is2));
						student.setAvatarFileName(avatarName);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					db.commit();
				}
				break;
			}
		}
		vm = path + "uploaded.vm";
	}
	
	
	public static void main(String[] args) throws Exception {
		
		String matricNo = "01-DOSH 1308-4102";
		int i = matricNo.lastIndexOf(" ");
		String s = matricNo.substring(i+1);
		System.out.println(s);
	}


}
