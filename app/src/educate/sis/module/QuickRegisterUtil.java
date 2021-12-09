package educate.sis.module;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import org.apache.velocity.VelocityContext;

import educate.admission.Application;
import educate.admission.entity.Address;
import educate.admission.entity.Applicant;
import educate.admission.entity.Biodata;
import educate.db.DbPersistence;
import educate.enrollment.entity.QuickRegisterLog;
import educate.enrollment.entity.Student;
import educate.sis.billing.InvoiceNoBean;
import educate.sis.finance.entity.Invoice;
import educate.sis.finance.entity.InvoiceItem;
import educate.sis.finance.module.FeeStructureNotAvailableException;
import educate.sis.finance.module.InvoiceUtil;
import educate.sis.general.entity.Country;
import educate.sis.general.entity.Gender;
import educate.sis.general.entity.Nationality;
import educate.sis.general.entity.State;
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
import onapp.entity.ApplicantOnline;
import onapp.entity.ApplicantStatus;
import onapp.entity.OnappCountry;
import onapp.entity.OnappState;

public class QuickRegisterUtil {

	
	public static Student studentRegistration(DbPersistence db, VelocityContext context, ApplicantOnline applicant, String matricPrefix,
			String icno, String passport, String name, String displayName, String programId,
			String intakeId, String centreId, String userId, String matricNo, String studyModeId) throws Exception {
		
		if ( matricNo != null ) matricNo = matricNo.trim();
		
		Program program = db.find(Program.class, programId);
		Session intake = db.find(Session.class, intakeId);
		LearningCentre centre = db.find(LearningCentre.class, centreId);
		StudyMode studyMode = null;
		if ( !"".equals(studyModeId))
			studyMode = db.find(StudyMode.class, studyModeId);
		else
			studyMode = (StudyMode) db.get("select m from StudyMode m where m.code = 'FT'");
		
		//if applicant exits get biodata reference from applicant
		Biodata biodata = null;
		biodata = new Biodata();
		
		biodata.setIcno(icno);
		biodata.setPassport(passport);
		biodata.setName(name);
		
		if ( !"".equals(displayName)) biodata.setDisplayName(displayName);
		else biodata.setDisplayName(name);
		
		try {
			MyKad mykad = new MyKad(icno);
			biodata.setIcno(mykad.getNumber());
			if ( mykad.getDateOfBirth() != null ) biodata.setDob(mykad.getDateOfBirth());
			if ( mykad.getGender() != null ) {
				Gender gender = (Gender) db.get("select g from Gender g where g.code = '" + mykad.getGender() + "'");
				if ( gender != null ) biodata.setGender(gender);
			}
		} catch ( ContainsOnlyNumbersException e) {
		} catch ( DatePartNotValidException e) {
		} catch ( NumberLengthException e ) {
		} catch ( BirthPlacePartException e ) {
		}
		
		
		//ADD NEW STUDENT TO STUDENT RECORDS
		StudentRegistrationUtil u = new StudentRegistrationUtil();
		Student student = null;
		if ( "".equals(matricNo)) {
			student = u.registerNewStudent(biodata, program, intake, centre, studyMode, matricPrefix);
		}
		else {
			student = u.registerNewStudentWithMatricNo(biodata, program, intake, centre, matricNo);
		}
		
		//assign back to matricNo
		matricNo = student.getMatricNo();
		
		//studyMode
		student.setStudyMode(studyMode);
		
		//check whether this student has applicant data
		if ( applicant != null ) {
			db.begin();
			student.setApplicant2(applicant);
			
			Address address = new Address();
			address.setAddress1(applicant.getPermanentAddressStreet());
			address.setAddress2(applicant.getPermanentAddressStreet2());
			address.setAddress3(applicant.getPermanentAddressCity());
			address.setPostcode(applicant.getPermanentAddressPostcode());
			
			OnappCountry onappCountry = applicant.getPermanentCountry();
			OnappState onappState = applicant.getPermanentState();
			if ( onappCountry != null ) {
				String countryCode = onappCountry.getCode();
				Country country = db.find(Country.class, countryCode);
				address.setCountry(country);
			}
			if ( onappState != null ) {
				String stateCode = onappState.getCode();
				State state = db.find(State.class, stateCode);
				address.setState(state);
			}
			
			OnappCountry onappNationality = applicant.getNationality();
			if ( onappNationality != null ) {
				String countryCode = onappNationality.getCode();
				Nationality nationality = db.find(Nationality.class, countryCode);
				student.getBiodata().setNationality(nationality);
			}
			
			student.setAddress(address);
			
			String nationalityType = applicant.getNationalityType() != null ? applicant.getNationalityType() : "";
			student.setStudentType("I".equals(nationalityType) ? "2" : "1");
			
			//photo
			if ( applicant.getPhotoFileName() != null ) student.setPhotoFileName(applicant.getPhotoFileName());
			db.commit();
			
			ApplicantStatus submitStatus = (ApplicantStatus) db.get("select s from ApplicantStatus s where s.code = 'E'");
			db.begin();
			applicant.setStatus(submitStatus);
			db.commit();
			
			
			

		}
		context.put("student", student);
		
		
		int i = matricNo.lastIndexOf(" ");
		if ( i > 0 ) {
			String s = matricNo.substring(i+1);
			StudentData.updatePortalPassword(matricNo, s);
			db.begin();
			student.getBiodata().setEmailId(s);
			student.getBiodata().setEmailPassword(s);
			db.commit();
		}
		
		//save/transfer instition SPM and STPM
		if ( applicant != null  ) {
			db.begin();
			student.setInstitutionSPM(applicant.getInstitutionSPM());
			student.setInstitutionSTPM(applicant.getInstitutionSTPM());
			student.setInstitutionTypeSPM(applicant.getInstitutionTypeSPM());
			student.setInstitutionTypeSTPM(applicant.getInstitutionTypeSTPM());
			
			String previousInstitutionName = "";
			previousInstitutionName = "".equals(previousInstitutionName) ? applicant.getInstitutionOther() : "";
			previousInstitutionName = "".equals(previousInstitutionName) ? applicant.getInstitutionIB() : "";
			previousInstitutionName = "".equals(previousInstitutionName) ? applicant.getInstitutionFoundation() : "";
			student.setPreviousInstitutionName(applicant.getInstitutionFoundation());
			
			String previousQualificationType = "";
			String previousQualificationName = "";
			
			previousQualificationName = applicant.getCgpaStudyField1();
			
			db.commit();
		}
		
		//create student's invoice
		createStudentInvoice(db, context, student);
		
		//do quick register log
		db.begin();
		QuickRegisterLog log = new QuickRegisterLog();
		log.setLogDate(new Date());
		log.setLogTime(new Date());
		log.setMatricNo(student.getMatricNo());
		log.setStudentId(student.getId());
		log.setStudentName(student.getBiodata().getName());
		log.setStudentICNo(student.getBiodata().getIcno());
		log.setStudentPassportNo(student.getBiodata().getPassport());
		log.setProgramCode(student.getProgram().getCode());
		log.setIntakeCode(student.getIntake().getCode());
		log.setCentreCode(student.getLearningCenter().getCode());
		log.setUserId(userId);
		db.persist(log);
		db.commit();
		
		return student;
	}
	
	public static void reCreateStudentInvoice(DbPersistence db, VelocityContext context, Student student) throws Exception {
		db.begin();
		db.executeUpdate("delete from Invoice i where i.student.id = '" + student.getId() + "' and i.session.id = '" + student.getIntake().getId() + "'");
		db.commit();
		createStudentInvoice(db, context, student);
	}

	public static void createStudentInvoice(DbPersistence db, VelocityContext context, Student student) throws Exception {
		
		Program program = student.getProgram();
		Session intake = student.getIntake();
		
		List<Period> periods = program.getPeriodScheme().getFunctionalPeriodList();
		Period period = periods.get(0);
		
		try {
			List<InvoiceItem> invoiceItems = new InvoiceUtil().getInvoiceItems(student, period); // new InvoiceUtil().getInvoiceItems(program, intake, period, centre);
			
			InvoiceNoBean ib = new InvoiceNoBean();
			String invoiceNo = ib.genInvoiceNo(student.getProgram().getCode(), ""); 
			db.begin();
			Invoice invoice = new Invoice();
			invoice.setStudent(student);
			invoice.setInvoiceNo(invoiceNo);
			invoice.setCreateDate(new Date());
			invoice.setCreateTime(new Date());
			invoice.setSession(intake);
			invoice.setInvoiceType(1); //primary
			for ( InvoiceItem i : invoiceItems ) {
				invoice.addInvoiceItem(i);
			}
			db.persist(invoice);
			db.commit();
			context.remove("fee_structure_error");
		} catch ( FeeStructureNotAvailableException e ) {
			context.put("fee_structure_error", true);
		}
	}	

	public static Student studentRegistration(DbPersistence db, VelocityContext context, Applicant applicant, String matricPrefix,
			String icno, String passport, String name, String programId,
			String intakeId, String centreId, String userId, String studyModeId) throws Exception {
		
		Program program = db.find(Program.class, programId);
		Session intake = db.find(Session.class, intakeId);
		LearningCentre centre = db.find(LearningCentre.class, centreId);
		StudyMode studyMode = db.find(StudyMode.class, studyModeId);
		
		//if applicant exits get biodata reference from applicant
		Biodata biodata = null;
		if ( applicant != null ) {
			biodata = applicant.getBiodata();
		}
		else {
			biodata = new Biodata();
		}
		
		biodata.setIcno(icno);
		biodata.setPassport(passport);
		biodata.setName(name);
		
		try {
			MyKad mykad = new MyKad(icno);
			biodata.setIcno(mykad.getNumber());
			if ( mykad.getDateOfBirth() != null ) biodata.setDob(mykad.getDateOfBirth());
			if ( mykad.getGender() != null ) {
				Gender gender = (Gender) db.get("select g from Gender g where g.code = '" + mykad.getGender() + "'");
				if ( gender != null ) biodata.setGender(gender);
			}
		} catch ( ContainsOnlyNumbersException e) {
		} catch ( DatePartNotValidException e) {
		} catch ( NumberLengthException e ) {
		} catch ( BirthPlacePartException e ) {
		}
		
		StudentRegistrationUtil u = new StudentRegistrationUtil();
		Student student = u.registerNewStudent(biodata, program, intake, centre, studyMode, matricPrefix);
		
		//check whether this student has applicant data
		if ( applicant != null ) {
			//new Application().updateStatus(applicant.getId(), Application.REGISTERED_STATUS);
			
			db.begin();
			applicant.setStatus(Application.REGISTERED_STATUS);
			student.setApplicant(applicant);
			if ( applicant.getAddress() != null ) student.setAddress(applicant.getAddress());
			if ( applicant.getPermanentAddress() != null ) student.setPermanentAddress(applicant.getPermanentAddress());
			//photo
			if ( applicant.getPhotoFileName() != null ) student.setPhotoFileName(applicant.getPhotoFileName());
			db.commit();
		} else {
			if ( icno != null && !"".equals(icno)) {
				applicant = (Applicant) db.get("select a from Applicant a where a.biodata.icno = '" + icno + "'");
			} else {
				applicant = (Applicant) db.get("select a from Applicant a where a.biodata.passport = '" + passport + "'");
			}
			if(applicant != null){
				new Application().updateStatus(applicant.getId(), Application.REGISTERED_STATUS);
				db.begin();
				student.setApplicant(applicant);
				if ( applicant.getAddress() != null ) student.setAddress(applicant.getAddress());
				if ( applicant.getPermanentAddress() != null ) student.setPermanentAddress(applicant.getPermanentAddress());
				db.commit();
			}
		}
		
		context.put("student", student);
		
		//create student's invoice
		List<Period> periods = program.getPeriodScheme().getFunctionalPeriodList();
		Period period = periods.get(0);
		
		try {
			List<InvoiceItem> invoiceItems = new InvoiceUtil().getInvoiceItems(student, period); // new InvoiceUtil().getInvoiceItems(program, intake, period, centre);
			
			InvoiceNoBean ib = new InvoiceNoBean();
			String invoiceNo = ib.genInvoiceNo(student.getProgram().getCode(), ""); 
			db.begin();
			Invoice invoice = new Invoice();
			invoice.setStudent(student);
			invoice.setInvoiceNo(invoiceNo);
			invoice.setCreateDate(new Date());
			invoice.setCreateTime(new Date());
			invoice.setSession(intake);
			invoice.setInvoiceType(1); //primary
			for ( InvoiceItem i : invoiceItems ) {
				System.out.println(i.getDescription());
				invoice.addInvoiceItem(i);
			}
			db.persist(invoice);
			db.commit();
			context.remove("fee_structure_error");
		} catch ( FeeStructureNotAvailableException e ) {
			context.put("fee_structure_error", true);
		}
		
		//do quick register log
		db.begin();
		QuickRegisterLog log = new QuickRegisterLog();
		log.setLogDate(new Date());
		log.setLogTime(new Date());
		log.setMatricNo(student.getMatricNo());
		log.setStudentId(student.getId());
		log.setStudentName(student.getBiodata().getName());
		log.setStudentICNo(student.getBiodata().getIcno());
		log.setStudentPassportNo(student.getBiodata().getPassport());
		log.setUserId(userId);
		db.persist(log);
		db.commit();
		
		return student;
	}
	
	public static void getClassroom(DbPersistence db, VelocityContext context, Student student) {
		
	}
	
	public static void getAccomodation(DbPersistence db, VelocityContext context, Student student) {
		List<StudentAccomodation> accomodations = db.list("select a from StudentAccomodation a order by a.name");
		context.put("accomodations", accomodations);
		
		String genderId = null;
		if ( student.getBiodata().getGender() != null )
			genderId = student.getBiodata().getGender().getId();
		else 
			genderId = student.getApplicant() != null && student.getApplicant().getBiodata() != null ? student.getApplicant().getBiodata().getGender().getId() : null;

		if ( genderId != null ) {
			List<Hashtable> accomodationList = new ArrayList<Hashtable>();
			context.put("accomodations", accomodationList);
			for ( StudentAccomodation accomodation : accomodations ) {
				Hashtable h = new Hashtable();
				h.put("accomodation", accomodation);
				h.put("numberOfPlace", getNumberOfPlace(db, accomodation, genderId));
				accomodationList.add(h);
			}
		}
		else {
			context.remove("accomodations");
			System.out.println("Gender not defined. Can't get accomodation quota.");
		}
		
		StudentAccomodationStatus accomodationStatus = (StudentAccomodationStatus) db.get("select s from StudentAccomodationStatus s where s.student.id = '" + student.getId() + "' and s.expired = 0 order by s.date desc");
		if ( accomodationStatus != null ) {
			StudentAccomodation accomodation = accomodationStatus.getAccomodation();
			context.put("accomodation", accomodation);
		}
		else {
			context.remove("accomodation");
		}
	}
	
	private static int getNumberOfPlace(DbPersistence db, StudentAccomodation accomodation, String genderId) {
		List<StudentAccomodationStatus> students = db.list("select s from StudentAccomodationStatus s where s.expired = 0 and s.student.biodata.gender.id = '" + genderId + "' and s.accomodation.id = '" + accomodation.getId() + "' order by s.date desc, s.time desc");
		int quota = 0;
		if ( "L".equals(genderId)) {
			quota = accomodation.getQuotaMale();
		} else {
			quota = accomodation.getQuotaFemale();
		}
		int listOfStudents = students.size();
		return quota - listOfStudents;
	}
	
	public static void updateStudentRegistration(DbPersistence db, VelocityContext context, String matricNo, Student student, Program program, Session intake, LearningCentre centre, boolean createStatus, String userId) throws Exception {
		updateStudentRegistration(db, context, matricNo, "", student, program, intake, centre, createStatus, userId);
	}
	
	public static void updateStudentRegistration(DbPersistence db, VelocityContext context, String matricNo, String displayName, Student student, Program program, Session intake, LearningCentre centre, boolean createStatus, String userId) throws Exception {
		
		//force create student status and invoice
		if ( matricNo != null ) matricNo = matricNo.trim();
		//get before update
		String xProgramId = student.getProgram() != null ? student.getProgram().getId() : "";
		String xIntakeId = student.getIntake() != null ? student.getIntake().getId() : "";
		String xCentreId = student.getLearningCenter() != null ? student.getLearningCenter().getId() : "";
		
		context.remove("matric_duplicate");
		if ( matricNo != null && !"".equals(matricNo)) {
			//check if matric no duplicate
			Student checkStudent = (Student) db.get("select s from Student s where s.matricNo = '" + matricNo + "' and s.id <> '" + student.getId() + "'");
			if ( checkStudent == null ) {
				student.setMatricNo(matricNo);
			} else {
				context.put("matric_duplicate", true);
			}
		}
		
		student.setProgram(program);
		student.setIntake(intake);
		student.setLearningCenter(centre);
		
		if ( !"".equals(displayName)) student.getBiodata().setDisplayName(displayName);

		//if change in program or change in intake then recreate student status and invoice
		if ( !program.getId().equals(xProgramId) || !intake.getId().equals(xIntakeId) ) {
			StudentStatusUtil u = new StudentStatusUtil();
			u.cleanUpStudentStatuses(student.getId());		
			u.addStatus(student, student.getIntake(), true);
			reCreateStudentInvoice(db, context, student);
		}
		else {
			if ( createStatus ) {
				StudentStatusUtil u = new StudentStatusUtil();
				u.cleanUpStudentStatuses(student.getId());		
				u.addStatus(student, student.getIntake(), true);
				reCreateStudentInvoice(db, context, student);
			}
		}
		
		//do quick register log
		db.begin();
		QuickRegisterLog log = new QuickRegisterLog();
		log.setLogDate(new Date());
		log.setLogTime(new Date());
		log.setMatricNo(student.getMatricNo());
		log.setStudentId(student.getId());
		log.setStudentName(student.getBiodata().getName());
		log.setStudentICNo(student.getBiodata().getIcno());
		log.setStudentPassportNo(student.getBiodata().getPassport());
		log.setProgramCode(student.getProgram().getCode());
		log.setIntakeCode(student.getIntake().getCode());
		log.setCentreCode(student.getLearningCenter().getCode());
		log.setUpdateRegister(true);
		log.setUserId(userId);
		db.persist(log);
		db.commit();	
	}


}
