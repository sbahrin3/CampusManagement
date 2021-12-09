package educate.sis.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpSession;

import educate.admission.Application;
import educate.admission.entity.Address;
import educate.admission.entity.Applicant;
import educate.admission.entity.PermanentAddress;
import educate.admission.entity.SpmResult;
import educate.admission.entity.StpmResult;
import educate.admission.entity.TertiaryEducation;
import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.enrollment.entity.StudentStatus;
import educate.sis.exam.entity.FinalResult;
import educate.sis.finance.entity.PaymentSchedule;
import educate.sis.finance.module.AccountStatement;
import educate.sis.finance.module.AccountStatementUtil;
import educate.sis.finance.module.PaymentUtil;
import educate.sis.finance.module.XPaymentScheduleItem;
import educate.sis.general.entity.Country;
import educate.sis.general.entity.Gender;
import educate.sis.general.entity.Nationality;
import educate.sis.general.entity.Race;
import educate.sis.general.entity.State;
import educate.sis.struct.entity.Session;
import lebah.portal.action.AjaxModule;
import lebah.util.DateUtil;

public class StudentInformationModule extends AjaxModule {
	
	String path = "apps/student_information/";
	private String vm = path + "default.vm";
	HttpSession session;
	DbPersistence db = new DbPersistence();
	protected boolean studentMode = false;

	
	public String doAction() throws Exception {
		
		if ( studentMode ){
			context.put("student_mode", studentMode);
			context.remove("admin_mode");
		}
		else {
			context.remove("student_mode");
			context.put("admin_mode", true);
		}
		
		session = request.getSession();
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy"));
		context.put("timeFormat", new SimpleDateFormat("hh:mm a"));	
		context.put("numFormat", new DecimalFormat("#,###,###.00"));
		DateUtil du = new DateUtil();
		context.put("du", du);
		String command = request.getParameter("command");
		System.out.println(command);
		if ( command == null || "".equals(command)) start();
		else if ( "list_students".equals(command)) listStudents();
		else if ( "filter_students".equals(command)) filterStudents();
		else if ( "student_info".equals(command)) studentInfo();
		
		else if ( "upload_photo".equals(command)) uploadPhoto();
		else if ( "save_photo".equals(command)) savePhoto();
		else if ( "edit_biodata".equals(command)) editBiodata();
		else if ( "save_biodata".equals(command)) saveBiodata();
		else if ( "change_nationality".equals(command)) changeNationality();
		
		else if ( "enrollment_biodata".equals(command)) enrollmentBiodata();
		else if ( "education_background".equals(command)) educationBackground();
		else if ( "working_experience".equals(command)) workingExperience();
		else if ( "academic_records".equals(command)) academicRecords();
		else if ( "finance_records".equals(command)) financeRecords();

		return vm;
	}
	
	private void financeRecords() throws Exception {
		
		vm = path + "acct_statement.vm";
		
		String studentId = request.getParameter("student_id");
		Student student = (Student) db.find(Student.class, studentId);
		context.put("student", student);
		AccountStatementUtil util = new AccountStatementUtil();
		AccountStatement acct = util.getAccountStatement(student);
		context.put("account_statement", acct);
				
		getPaymentSchedule(student.getMatricNo());
		
	}
	
	
	
	private void getPaymentSchedule(String matricNo) throws Exception {
		Student student = (Student) db.get("select s from Student s where s.matricNo = '" + matricNo + "'");
		context.put("student", student);
		StudentStatusUtil u = new StudentStatusUtil();
		StudentStatus studentStatus = u.getCurrentStudentStatus(student);
		context.put("studentStatus", studentStatus);
		
		PaymentSchedule paymentSchedule = (PaymentSchedule) db.get("select p from PaymentSchedule p where p.student.id = '" + student.getId() + "'");

		if ( paymentSchedule != null ) {
			context.put("paymentSchedule", paymentSchedule);		
			List<XPaymentScheduleItem> items = PaymentUtil.adjustedPaymentScheduleItems(paymentSchedule);
			context.put("scheduleItems", items);
		}
		
	}

	private void academicRecords() throws Exception {
		//vm = path + "academic_records.vm";
		String studentId = request.getParameter("student_id");
		Student student = (Student) db.find(Student.class, studentId);
		context.put("student", student);
		showExamTranscript(student);
		
	}
	
	private void showExamTranscript(Student student) throws Exception {
		
		SessionUtil u = new SessionUtil();
		Session currentSession = u.getCurrentSession(student.getProgram().getLevel().getPathNo());
		context.put("current_session", currentSession);
		
		context.put("student", student);
		String sql = "select r from FinalResult r where r.student.id = '" + student.getId() + "' order by r.session.startDate";
		
		List<FinalResult> results = db.list(sql);
		if ( results.size() > 0 ) {
			context.put("results", results);
		}
		else {
			context.remove("results");
		}
		
		vm = path + "transcript.vm";
	}

	private void workingExperience() {
		vm = path + "working_experience.vm";
		
	}

	private void educationBackground() {
		vm = path + "education_background.vm";
		vm = path + "student_info_detail.vm";
		String studentId = request.getParameter("student_id");
		Student student = (Student) db.find(Student.class, studentId);
		context.put("student", student);
		
		Applicant applicant = student.getApplicant();
		if ( applicant != null ) {
			System.out.println("Get applicant data..");
			Set<SpmResult> spmResults = applicant.getSpmResults2();
			int spmYear = applicant.getSpmYear();
			Set<StpmResult> stpmResults = applicant.getStpmResults2();
			int stpmYear = applicant.getStpmYear();
			Set<TertiaryEducation> tertiaries = applicant.getTertiaries();
		}
		else {
			System.out.println("Applicant data is NULL... so creating new Applicant object!");
			applicant = new Applicant();
			
		}
	}

	private void enrollmentBiodata() throws Exception {
		vm = path + "student_info_detail.vm";
		String studentId = request.getParameter("student_id");
		Student student = (Student) db.find(Student.class, studentId);
		context.put("student", student);
		StudentStatusUtil u = new StudentStatusUtil();
		StudentStatus studentStatus = u.getCurrentStudentStatus(student);
		if ( studentStatus == null ) context.remove("student_status");
		else context.put("student_status", studentStatus);
	}

	private void changeNationality() {
		vm = path + "div_address1.vm";
		String studentId = request.getParameter("student_id");
		Student student = (Student) db.find(Student.class, studentId);
		context.put("student", student);
		
		String nationalityId = request.getParameter("nationality_id");
		Nationality nationality = (Nationality) db.find(Nationality.class, nationalityId);
		if ( student.getApplicant() != null ) {
			if ( "MALAYSIA".equals(nationality.getName()) ) student.getApplicant().setNationalityType("1");
			else student.getApplicant().setNationalityType("2");
		}
		
	}

	private void saveBiodata() throws Exception {
		vm = path + "div_biodata.vm";
		String studentId = request.getParameter("student_id");
		Student student = (Student) db.find(Student.class, studentId);
		if ( student != null ) {
			context.put("student", student);
			
			saveBiodata(student);
			
			updateApplicantBiodata(student);

			StudentStatusUtil u = new StudentStatusUtil();
			StudentStatus studentStatus = u.getCurrentStudentStatus(student);
			if ( studentStatus == null ) context.remove("student_status");
			else context.put("student_status", studentStatus);
		}
		else {
			context.remove("student");
		}
		
	}

	private void updateApplicantBiodata(Student student) throws Exception {
		Applicant applicant = student.getApplicant();
		System.out.println("applicant = " + applicant);
		
		String nationalityType = student.getNationalityType();
		try {
		if ( applicant != null ) {
			db.begin();
			applicant.setBiodata(student.getBiodata());
			db.commit();
		}
		else {
			System.out.println("create applicant.");
			applicant = new Applicant();
			applicant.setReferenceNo(lebah.db.UniqueID.getUID());
			applicant.setStatus(Application.REGISTERED_STATUS);
			applicant.setNationalityType(nationalityType);
			System.out.println("applicant = " + applicant);
			db.begin();
			db.persist(applicant);
			db.commit();		}
		
			db.begin();
			applicant.setBiodata(student.getBiodata());
			if ( student.getAddress() != null ) applicant.setAddress(student.getAddress());
			if ( student.getPermanentAddress() != null ) applicant.setPermanentAddress(student.getPermanentAddress());
			student.setApplicant(applicant);
			db.commit();
		
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		
	}

	private void saveBiodata(Student student) throws Exception {
		String name = request.getParameter("name");
		String icno = request.getParameter("icno");
		String passport = request.getParameter("passport");
		String dateOfBirth = request.getParameter("date_of_birth"); //dd-mm-yyy
		
		String nationalityId = request.getParameter("nationality_id");
		Nationality nationality = null;
		String nationalityType = "2";
		if ( "".equals(icno) && !"".equals(passport)) {
			
			if ( "".equals(nationalityId)) {
				nationalityType = "2";
			}
			else {
				nationality = (Nationality) db.find(Nationality.class, nationalityId);
				if ( nationality.getName().equals("MALAYSIA")) nationalityType = "1";
			}
		}
		else {
			nationalityType = "1";
			nationality = (Nationality) db.find(Nationality.class, "MY");
			if ( nationality != null) student.getBiodata().setNationality(nationality);
		}
		
		System.out.println("nationality type = " + nationalityType);
		
		String genderId = request.getParameter("gender_id");
		Gender gender = (Gender) db.find(Gender.class, genderId);
		String raceId = request.getParameter("race_id");
		Race race = (Race) db.find(Race.class, raceId);
		String telephone = request.getParameter("telephone");
		String mobileNo = request.getParameter("mobile_no");
		String email = request.getParameter("email");
		String email2 = request.getParameter("email2");
		String address1 = request.getParameter("address1");
		String address2 = request.getParameter("address2");
		String city = request.getParameter("city");
		String postcode = request.getParameter("postcode");
		String address3 = request.getParameter("address3");
		String address4 = request.getParameter("address4");
		State state = null;
		if ( "1".equals(nationalityType)) {
			String stateId = request.getParameter("state_id");
			if ( stateId != null ) state = (State) db.find(State.class, stateId);
		}
		Country country = null;
		if ( "2".equals(nationalityType)) {
			String countryId = request.getParameter("country_id");
			if ( countryId != null ) country = (Country) db.find(Country.class, countryId);
		}
		String permaddress1 = request.getParameter("perm_address1");
		String permaddress2 = request.getParameter("perm_address2");
		String permcity = request.getParameter("perm_city");
		String permpostcode = request.getParameter("perm_postcode");
		String permaddress3 = request.getParameter("perm_address3");
		String permaddress4 = request.getParameter("perm_address4");
		State permstate = null;
		if ( "1".equals(nationalityType))  {
			String permstateId = request.getParameter("perm_state_id");
			if ( permstateId != null ) permstate = (State) db.find(State.class, permstateId);
		}
		Country permcountry = null;
		if ( "2".equals(nationalityType)) {
			String permcountryId = request.getParameter("perm_country_id");
			if ( permcountryId != null ) permcountry = (Country) db.find(Country.class, permcountryId);
		}
		
		String studentType = getParam("studentType"); //1, 2
		
		
		//fakeStudent
		String fakeStudent = getParam("fakeStudent");
		
		db.begin();
		student.setStudentType(studentType);
		student.getBiodata().setName(name);
		student.getBiodata().setIcno(icno);
		student.getBiodata().setPassport(passport);
		student.getBiodata().setDob(dateOfBirth);
		student.getBiodata().setGender(gender);
		student.getBiodata().setNationality(nationality);
		if ( student.getApplicant() != null ) student.getApplicant().setNationalityType(nationalityType);
		student.getBiodata().setRace(race);
		student.getBiodata().setTelephoneNo(telephone);
		student.getBiodata().setMobileNo(mobileNo);
		student.getBiodata().setEmail(email);
		student.getBiodata().setEmail2(email2);
		
		if ( student.getAddress() == null ) student.setAddress(new Address());
		if ( student.getPermanentAddress() == null ) student.setPermanentAddress(new PermanentAddress());
		
		student.getAddress().setAddress1(address1);
		student.getAddress().setAddress2(address2);
		if ( "1".equals(nationalityType)) student.getAddress().setCity(city);
		if ( "1".equals(nationalityType)) student.getAddress().setPostcode(postcode);
		if ( "1".equals(nationalityType)) student.getAddress().setState(state);
		if ( "2".equals(nationalityType)) student.getAddress().setAddress3(address3);
		if ( "2".equals(nationalityType)) student.getAddress().setAddress4(address4);
		if ( "2".equals(nationalityType)) student.getAddress().setCountry(country);
		student.getPermanentAddress().setAddress1(permaddress1);
		student.getPermanentAddress().setAddress2(permaddress2);
		if ( "1".equals(nationalityType)) student.getPermanentAddress().setCity(permcity);
		if ( "1".equals(nationalityType)) student.getPermanentAddress().setPostcode(permpostcode);
		if ( "1".equals(nationalityType)) student.getPermanentAddress().setState(permstate);
		if ( "2".equals(nationalityType)) student.getPermanentAddress().setAddress3(permaddress3);
		if ( "2".equals(nationalityType)) student.getPermanentAddress().setAddress4(permaddress4);
		if ( "2".equals(nationalityType)) student.getPermanentAddress().setCountry(permcountry);
		
		
		if ( "hide".equals(fakeStudent)) {
			student.setFakeStudent("hide");
		} else {
			student.setFakeStudent(null);
		}

		db.commit();

	}

	private void editBiodata() throws Exception {
		vm = path + "div_biodata_edit.vm";
		String studentId = request.getParameter("student_id");
		Student student = (Student) db.find(Student.class, studentId);
		if ( student != null ) {
			context.put("student", student);
			StudentStatusUtil u = new StudentStatusUtil();
			StudentStatus studentStatus = u.getCurrentStudentStatus(student);
			if ( studentStatus == null ) context.remove("student_status");
			else context.put("student_status", studentStatus);
			
			context.put("genders", db.list("SELECT a FROM educate.sis.general.entity.Gender a"));
			context.put("countries", db.list("SELECT a from educate.sis.general.entity.Country a"));
			context.put("nationalities", db.list("SELECT a from educate.sis.general.entity.Nationality a"));
			context.put("states", db.list("SELECT a from educate.sis.general.entity.State a"));
			context.put("races", db.list("SELECT a from educate.sis.general.entity.Race a"));
		}
		else {
			context.remove("student");
		}
	}

	public void saveAvatar(String userId, String avatarName) {
		try {
			lebah.db.Db database = new lebah.db.Db();
			String sql = "update users set avatar = '" + avatarName + "' where user_login = '" + userId + "'";
			database.getStatement().executeUpdate(sql);
		} catch ( Exception e ) {
			System.out.println(e.getMessage());
		}
	}
	
	private void savePhoto() throws Exception {
		vm = path + "div_photo.vm";
		String studentId = request.getParameter("student_id");
		Student student = (Student) db.find(Student.class, studentId);
		context.put("student", student);
		List<String> images =(List) request.getSession().getAttribute("uploaded_files");
		String imgName = null;
		String avatarName = null;
		if ( images.size() > 0 ) {
			imgName = images.get(0);
			avatarName = imgName.substring(0, imgName.lastIndexOf(".")) + "_avatar" + imgName.substring(imgName.lastIndexOf("."));
			//lebah.repository.Thumbnail.create(imgName, imgName, 200, 160, 100);
			//lebah.repository.Thumbnail.create(imgName, avatarName, 50, 40, 100);
			saveAvatar(student.getMatricNo(), avatarName);
			db.begin();
			student.setPhotoFileName(imgName);
			student.setAvatarFileName(avatarName);
			db.commit();
		}
		
	}

	private void uploadPhoto() {
		vm = path + "upload_photo.vm";
		
	}

	private void studentInfo() throws Exception {
		vm = path + "student_info.vm";
	
		String matricNo = "";
		if ( studentMode ) {
			matricNo = (String) session.getAttribute("_portal_login");
		}
		else {
			matricNo = request.getParameter("matric_no");
		}
		
		
		Student student = (Student) db.get("select s from Student s where s.matricNo = '" + matricNo + "'");
		
		if ( student != null ) {
			context.put("student", student);
			StudentStatusUtil u = new StudentStatusUtil();
			StudentStatus studentStatus = u.getCurrentStudentStatus(student);
			if ( studentStatus == null ) context.remove("student_status");
			else context.put("student_status", studentStatus);
			
			/*
			Applicant applicant = student.getApplicant();
			if ( applicant == null ) {
				applicant = new Applicant();
				String refNo = new Application().genRefNo();
				applicant.setReferenceNo(refNo);
				if ( student.getBiodata() != null ) applicant.setBiodata(student.getBiodata());
				db.begin();
				student.setApplicant(applicant);
				db.commit();
			}
			
			if ( student.getBiodata() == null ) {
				if ( applicant.getBiodata() != null ) {
					db.begin();
					student.setBiodata(applicant.getBiodata());
					db.commit();
				}
			}

			*/
			
			context.put("applicant", student.getApplicant());
		}
		else {
			context.remove("student");
		}

	}
	
	private void filterStudents() {
		vm = path + "filter_students.vm";
		String filterName = request.getParameter("filter_name");
		String sql = "select s from Student s WHERE (s.fakeStudent is null OR s.fakeStudent = '') and s.biodata.name like '%" + filterName + "%'";
		List<Student> students = db.list(sql);
		context.put("students", students);
	}
	
	private void listStudents() {
		vm = path + "students.vm";
		
	}

	private void start() throws Exception {
		vm = path + "start.vm";
		if ( studentMode ) {
			studentInfo();
		}
	}

}
