package educate.sis.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.http.HttpSession;

import educate.admission.Application;
import educate.admission.entity.Applicant;
import educate.admission.entity.SpmResult;
import educate.admission.entity.StpmResult;
import educate.admission.entity.TertiaryEducation;
import educate.db.DbPersistence;
import educate.sis.general.entity.Country;
import educate.sis.general.entity.DegreeClass;
import educate.sis.general.entity.Gender;
import educate.sis.general.entity.GeneralExamGrade;
import educate.sis.general.entity.Nationality;
import educate.sis.general.entity.Race;
import educate.sis.general.entity.SchoolSubject;
import educate.sis.general.entity.State;
import educate.sis.general.entity.StudyLevel;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.Session;
import lebah.portal.action.AjaxModule;

public class ApplicantManagerModule extends AjaxModule {
	
	String path = "apps/util/applicant/";
	protected String vm = "default.vm";
	HttpSession session;
	DbPersistence db = new DbPersistence();
	protected boolean inChild = false;

	
	public String doAction() throws Exception {
		context.put("_formName", formName);
		session = request.getSession();
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy"));
		context.put("timeFormat", new SimpleDateFormat("hh:mm a"));	
		context.put("numFormat", new DecimalFormat("#,###,###.00"));	
		String command = request.getParameter("command");
		System.out.println(command);
		if ( !doAction2(command) ) {
			if ( null == command || "".equals(command)) start();	
			else if ( "break_down".equals(command)) statBreakDown();
			else if ( "list_by_program".equals(command)) listByProgram();
			else if ( "mark_incomplete".equals(command)) markIncomplete();
			else if ( "mark_deleted".equals(command)) sendToTrash();
			else if ( "mark_new".equals(command)) markNew();
			else if ( "go_to".equals(command)) goTo();
			else if ( "get_applicant".equals(command)) getApplicant();
			else if ( "save_spm".equals(command)) saveSPM();
			else if ( "get_spm".equals(command)) getSPM();
			else if ( "save_stpm".equals(command)) saveSTPM();
			else if ( "get_stpm".equals(command)) getSTPM();
			else if ( "edit_spm".equals(command)) editSPM();
			else if ( "edit_stpm".equals(command)) editSTPM();
			else if ( "add_spm".equals(command)) addSPM();
			else if ( "add_stpm".equals(command)) addSTPM();
			else if ( "delete_subject_spm".equals(command)) deleteSubjectSPM();
			else if ( "delete_subject_stpm".equals(command)) deleteSubjectSTPM();
			else if ( "edit_program_choices".equals(command)) editProgramChoices();
			else if ( "update_program_choices".equals(command)) updateProgramChoices();
			else if ( "edit_info".equals(command)) editInfo();
			else if ( "update_info".equals(command)) updateInfo();
			else if ( "edit_tertiary".equals(command)) editTertiary();
			else if ( "save_tertiary".equals(command)) saveTertiary();
			else if ( "get_tertiary".equals(command)) getTertiary();
			else if ( "add_tertiary".equals(command)) addTertiary();
			else if ( "delete_tertiary".equals(command)) deleteTertiary();
			else if ( "search_applicant".equals(command)) searchApplicant();
			
			else if ( "do_offer".equals(command)) doOffer();
			else if ( "offer_program".equals(command)) offerProgram();
			else if ( "offer_program_intake".equals(command)) offerProgramIntake();
			else if ( "offer_cancel".equals(command)) doOfferCancel();
			
			else if ( "change_status".equals(command)) changeStatus();
			else if ( "status_changed".equals(command)) statusChanged();
		}
		//waiting(1);
		return vm;
	}
	
	private void changeStatus() throws Exception {
		vm = path + "change_status_button.vm";
		String applicantId = request.getParameter("applicant_id");
		Applicant applicant = (Applicant) db.find(Applicant.class, applicantId);
		context.put("applicant", applicant);
		
		String changeTo = request.getParameter("change_to");
		System.out.println("Change application status: " + applicant.getBiodata().getName() + " to " + changeTo);
		
		db.begin();
		applicant.setStatus(changeTo);
		db.commit();
		
	}

	private void statusChanged() {
		vm = path + "list_applicants_item.vm";
		String id = request.getParameter("applicant_id");
		Applicant applicant = (Applicant) db.find(Applicant.class, id);
		context.put("applicant", applicant);
		
		String beforeStatus = request.getParameter("before_status");
		if ( !beforeStatus.equals(applicant.getStatus())) {
			context.put("status_changed", true);
		}
		else {
			context.remove("status_changed");
		}
		
	}
	
	private void offerProgramIntake() throws Exception {
		
		vm = path + "program_offered.vm";
		
		String applicantId = request.getParameter("applicant_id");
		Applicant applicant = (Applicant) db.find(Applicant.class, applicantId);
		context.put("applicant", applicant);
		String programId = request.getParameter("program_offered_id");
		Program program = (Program) db.find(Program.class, programId);
		String offerType = request.getParameter("offer_type");
		
		String intakeId = request.getParameter("intake_id");
		Session intake = (Session) db.find(Session.class, intakeId);
		
		System.out.println("OFFERED TO " + program.getName());
		
		db.begin();
		applicant.setProgramOffered(program);
		applicant.setOfferedDate(new Date());
		if ( "conditional".equals(offerType))
			applicant.setStatus(Application.CONDITIONAL_STATUS);
		else
			applicant.setStatus(Application.OFFERED_STATUS);
		
		applicant.setIntake(intake);
		db.commit();
		
	}

	private void offerProgram() throws Exception {
		//vm = path + "program_offered.vm";
		vm = path + "select_intakes.vm";
		String applicantId = request.getParameter("applicant_id");
		Applicant applicant = (Applicant) db.find(Applicant.class, applicantId);
		context.put("applicant", applicant);
		String choiceId = request.getParameter("choice_id");
		Program program = (Program) db.find(Program.class, choiceId);
		context.put("program", program);
		
		System.out.println("program offered = " + program.getName());
		
		String offerType = request.getParameter("offer_type");
		context.put("offer_type", offerType);
		int pathNo = program.getLevel().getPathNo();
		List<Session> intakes = db.list("select s from Session s where s.pathNo = " + pathNo + " order by s.startDate");
		context.put("intakes", intakes);
		
		try {
			Session currentSession = new StudentStatusUtil().getCurrentSession(pathNo);
			context.put("current_session", currentSession);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
//		db.begin();
//		applicant.setProgramOffered(program);
//		applicant.setOfferedDate(new Date());
//		if ( "conditional".equals(offerType))
//			applicant.setStatus(Application.CONDITIONAL_STATUS);
//		else
//			applicant.setStatus(Application.OFFERED_STATUS);
//		db.commit();
		
	}

	private void doOfferCancel() {
		vm = path + "change_status_button.vm";
		String applicantId = request.getParameter("applicant_id");
		Applicant applicant = (Applicant) db.find(Applicant.class, applicantId);
		context.put("applicant", applicant);		
		
	}

	private void doOffer() {
		vm = path + "make_offer.vm";
		String applicantId = request.getParameter("applicant_id");
		Applicant applicant = (Applicant) db.find(Applicant.class, applicantId);
		context.put("applicant", applicant);
		Program choice1 = applicant.getChoice1();
		if ( choice1 != null ) context.put("choice1", choice1); else context.remove("choice1");
		Program choice2 = applicant.getChoice2();
		if ( choice2 != null ) context.put("choice2", choice2); else context.remove("choice2");
		Program choice3 = applicant.getChoice3();
		if ( choice3 != null ) context.put("choice3", choice3); else context.remove("choice3");	
		
		String offerType = request.getParameter("offer_type");
		context.put("offer_type", offerType);
		
	}

	protected boolean doAction2(String command) throws Exception {
		return false;
	}
	
	protected void searchApplicant() {
		vm = path + "search_result.vm";
		String searchName = request.getParameter("search_name");
		String sql = "";
		if ( !"".equals(searchName)) {
			sql = "select a from Applicant a where a.biodata.name LIKE '%" + searchName + "%' order by a.biodata.name";
			List<Applicant> applicants = db.list(sql);
			context.put("applicants", applicants);
		}
		else {
			context.remove("applicants");
		}
		
	}

	protected void deleteTertiary() throws Exception {
		vm = path + "edit_tertiary_education.vm";
		String applicantId = request.getParameter("applicant_id");
		Applicant applicant = (Applicant) db.find(Applicant.class, applicantId);
		context.put("applicant", applicant);
		
		String tertiaryId = request.getParameter("tertiary_id");
		TertiaryEducation tertiary = (TertiaryEducation) db.find(TertiaryEducation.class, tertiaryId);
		db.begin();
		applicant.getTertiaries().remove(tertiary);
		db.commit();
		
	}

	protected void addTertiary() throws Exception {
		vm = path + "edit_tertiary_education.vm";
		String applicantId = request.getParameter("applicant_id");
		Applicant applicant = (Applicant) db.find(Applicant.class, applicantId);
		context.put("applicant", applicant);
		
		String institution = request.getParameter("institution");
		String specialisation = request.getParameter("specialisation");
		String level_id = request.getParameter("level_id");
		StudyLevel level = (StudyLevel) db.find(StudyLevel.class, level_id);
		String tertiary_year = request.getParameter("tertiary_year");
		String tertiary_cgpa = request.getParameter("tertiary_cgpa");
		String class_id = request.getParameter("class_id");
		DegreeClass degreeClass = (DegreeClass) db.find(DegreeClass.class, class_id);
		
		TertiaryEducation tertiary = new TertiaryEducation();
		tertiary.setInstituition(institution);
		tertiary.setSpecialisation(specialisation);
		tertiary.setLevel(level);
		int year = 0;
		try {
			year = Integer.parseInt(tertiary_year);
		} catch ( Exception e ) {}
		double cgpa = 0.0d;
		try {
			cgpa = Double.parseDouble(tertiary_cgpa);
		} catch ( Exception e ) {}
		tertiary.setYear(year);
		tertiary.setCgpa(cgpa);
		tertiary.setDegreeClass(degreeClass);
		
		
		db.begin();
		applicant.getTertiaries().add(tertiary);
		db.commit();
	}

	protected void getTertiary() {
		vm = path + "tertiary_education.vm";
		String applicantId = request.getParameter("applicant_id");
		Applicant applicant = (Applicant) db.find(Applicant.class, applicantId);
		context.put("applicant", applicant);
		
	}

	protected void saveTertiary() throws Exception {
		vm = path + "tertiary_education.vm";
		String applicantId = request.getParameter("applicant_id");
		Applicant applicant = (Applicant) db.find(Applicant.class, applicantId);
		context.put("applicant", applicant);
		


		
	}

	protected void editTertiary() {
		vm = path + "edit_tertiary_education.vm";
		String applicantId = request.getParameter("applicant_id");
		Applicant applicant = (Applicant) db.find(Applicant.class, applicantId);
		context.put("applicant", applicant);
		
		context.put("study_levels",db.list("SELECT a from StudyLevel a"));
		context.put("degree_classes",db.list("SELECT a FROM DegreeClass a"));
		
		
		
	}

	protected void updateInfo() throws Exception {
		vm = path + "applicant_info.vm";
		String applicantId = request.getParameter("applicant_id");
		Applicant applicant = (Applicant) db.find(Applicant.class, applicantId);
		context.put("applicant", applicant);
		
		String name = request.getParameter("name");
		String gender_id = request.getParameter("gender_id");
		String icno = request.getParameter("icno");
		String passport = request.getParameter("passport");
		String nationality_id = request.getParameter("nationality_id");
		String race_id = request.getParameter("race_id");
		String dob = request.getParameter("dob");
		String address1 = request.getParameter("address1");
		String address2 = request.getParameter("address2");
		String address3 = request.getParameter("address3");
		String address4 = request.getParameter("address4");
		String poscode = request.getParameter("poscode");
		String state_id = request.getParameter("state_id");
		String country_id = request.getParameter("country_id");
		
		String paddress1 = request.getParameter("paddress1");
		String paddress2 = request.getParameter("paddress2");
		String paddress3 = request.getParameter("paddress3");
		String paddress4 = request.getParameter("paddress4");
		String pposcode = request.getParameter("pposcode");
		String pstate_id = request.getParameter("pstate_id");
		String pcountry_id = request.getParameter("pcountry_id");
		
		String telephoneNo = request.getParameter("telephoneNo");
		String mobileNo = request.getParameter("mobileNo");
		String email = request.getParameter("email");
		
		
		
		db.begin();
		applicant.getBiodata().setName(name);
		
		if ( gender_id != null && !"".equals(gender_id)) {
			Gender gender = (Gender) db.find(Gender.class, gender_id);
			applicant.getBiodata().setGender(gender);
		}
		applicant.getBiodata().setIcno(icno);
		applicant.getBiodata().setPassport(passport);
		applicant.getBiodata().setDob(dob);
		
		if ( nationality_id != null && !"".equals(nationality_id)) {
			Nationality nationality = (Nationality) db.find(Nationality.class, nationality_id);
			applicant.getBiodata().setNationality(nationality);
		}
		
		if ( race_id != null && !"".equals(race_id)) {
			Race race = (Race) db.find(Race.class, race_id);
			applicant.getBiodata().setRace(race);
		}
		
		applicant.getAddress().setAddress1(address1);
		applicant.getAddress().setAddress2(address2);
		applicant.getAddress().setAddress3(address3);
		applicant.getAddress().setAddress4(address4);
		applicant.getAddress().setPostcode(poscode);
		
		if ( state_id != null && !"".equals(state_id)) {
			State state = (State) db.find(State.class, state_id);
			applicant.getAddress().setState(state);
		}
		if ( country_id != null && !"".equals(country_id)) {
			Country country = (Country) db.find(Country.class, country_id);
			applicant.getAddress().setCountry(country);
		}
		
		applicant.getPermanentAddress().setAddress1(paddress1);
		applicant.getPermanentAddress().setAddress2(paddress2);
		applicant.getPermanentAddress().setAddress3(paddress3);
		applicant.getPermanentAddress().setAddress4(paddress4);
		applicant.getPermanentAddress().setPostcode(pposcode);
		
		if ( pstate_id != null && !"".equals(pstate_id)) {
			State pstate = (State) db.find(State.class, pstate_id);
			applicant.getPermanentAddress().setState(pstate);
		}
		
		if ( pcountry_id != null && !"".equals(pcountry_id)) {
			Country pcountry = (Country) db.find(Country.class, pcountry_id);
			applicant.getPermanentAddress().setCountry(pcountry);
		}
		applicant.getBiodata().setTelephoneNo(telephoneNo);
		applicant.getBiodata().setMobileNo(mobileNo);
		applicant.getBiodata().setEmail(email);
		db.commit();
		
	}

	protected void editInfo() {
		vm = path + "edit_applicant_info.vm";
		String applicantId = request.getParameter("applicant_id");
		Applicant applicant = (Applicant) db.find(Applicant.class, applicantId);
		context.put("applicant", applicant);
		
		//select list parameters
		context.put("nationalities", db.list("SELECT a from Nationality a order by a.name"));
		context.put("races", db.list("SELECT a from Race a"));
		context.put("states", db.list("SELECT a from State a") );
		context.put("countries", db.list("SELECT a from Country a"));		
		
	}

	protected void updateProgramChoices() throws Exception {
		vm = path + "program_choices.vm";
		String applicantId = request.getParameter("applicant_id");
		Applicant applicant = (Applicant) db.find(Applicant.class, applicantId);
		context.put("applicant", applicant);
		
		String choice1 = request.getParameter("choice1");
		String choice2 = request.getParameter("choice2");
		String choice3 = request.getParameter("choice3");
		
		db.begin();
		if ( !"".equals(choice1)) {
			Program program = (Program) db.find(Program.class, choice1);
			applicant.setChoice1(program);
		}
		
		if ( !"".equals(choice2)) {
			Program program = (Program) db.find(Program.class, choice2);
			applicant.setChoice2(program);
		}
		
		if ( !"".equals(choice3)) {
			Program program = (Program) db.find(Program.class, choice3);
			applicant.setChoice3(program);
		}
		db.commit();
		
	}

	protected void editProgramChoices() {
		vm = path + "edit_program_choices.vm";
		String applicantId = request.getParameter("applicant_id");
		Applicant applicant = (Applicant) db.find(Applicant.class, applicantId);
		context.put("applicant", applicant);
		
		context.put("programs", db.list("select x from Program x order by x.code"));
		
		
		
	}

	protected void addSTPM() throws Exception {
		vm = path + "edit_applicant_stpm_result.vm";
		
		//get GeneralExamGrade list
		String sql = "select g from GeneralExamGrade g";
		List<GeneralExamGrade> gradeList = db.list(sql);
		Hashtable<String, GeneralExamGrade> glist = new Hashtable<String, GeneralExamGrade>();
		for ( GeneralExamGrade g : gradeList ) {
			glist.put(g.getName(), g);
		}
		
		String applicantId = request.getParameter("applicant_id");
		Applicant applicant = (Applicant) db.find(Applicant.class, applicantId);
		context.put("applicant", applicant);
		
		String subjectId = request.getParameter("stpm_subject_id");
		String grade = request.getParameter("stpm_grade");
		
		if ( "".equals(subjectId) || "".equals(grade)) {
			return;
		}
		
		SchoolSubject subject = (SchoolSubject) db.find(SchoolSubject.class, subjectId);
		
		StpmResult result = new StpmResult();
		result.setSubject(subject);
		GeneralExamGrade generalExamGrade = glist.get(grade);
		if ( generalExamGrade != null ) {
			result.setGeneralExamGrade(generalExamGrade);
			
			db.begin();
			applicant.getStpmResults().add(result);
			db.commit();
			
		}
		
	}
	
	protected void deleteSubjectSTPM() throws Exception {
		vm = path + "edit_applicant_stpm_result.vm";
		String applicantId = request.getParameter("applicant_id");
		Applicant applicant = (Applicant) db.find(Applicant.class, applicantId);
		context.put("applicant", applicant);
		
		String stpmId = request.getParameter("stpm_id");
		StpmResult stpmResult = (StpmResult) db.find(StpmResult.class, stpmId);
		
		db.begin();
		applicant.getStpmResults().remove(stpmResult);
		db.commit();
		
	}

	protected void deleteSubjectSPM() throws Exception {
		vm = path + "edit_applicant_spm_result.vm";
		String applicantId = request.getParameter("applicant_id");
		Applicant applicant = (Applicant) db.find(Applicant.class, applicantId);
		context.put("applicant", applicant);
		
		String spmId = request.getParameter("spm_id");
		SpmResult spmResult = (SpmResult) db.find(SpmResult.class, spmId);
		
		db.begin();
		applicant.getSpmResults().remove(spmResult);
		db.commit();
		
	}

	protected void addSPM() throws Exception {
		vm = path + "edit_applicant_spm_result.vm";
		String applicantId = request.getParameter("applicant_id");
		Applicant applicant = (Applicant) db.find(Applicant.class, applicantId);
		context.put("applicant", applicant);
		
		String subjectId = request.getParameter("spm_subject_id");
		String grade = request.getParameter("spm_grade");
		
		if ( "".equals(subjectId) || "".equals(grade)) {
			return;
		}
		
		SchoolSubject subject = (SchoolSubject) db.find(SchoolSubject.class, subjectId);
		
		SpmResult result = new SpmResult();
		result.setSubject(subject);
		result.setGrade(grade);
		
		db.begin();
		applicant.getSpmResults().add(result);
		db.commit();
		
		
	}

	protected void editSTPM() {
		vm = path + "edit_applicant_stpm_result.vm";
		String applicantId = request.getParameter("applicant_id");
		Applicant applicant = (Applicant) db.find(Applicant.class, applicantId);
		context.put("applicant", applicant);
		selectParameters();
		
	}

	protected void editSPM() {
		vm = path + "edit_applicant_spm_result.vm";
		String applicantId = request.getParameter("applicant_id");
		Applicant applicant = (Applicant) db.find(Applicant.class, applicantId);
		context.put("applicant", applicant);
		selectParameters();
		
	}

	protected void saveSTPM() throws Exception {
		vm = path + "applicant_stpm_result.vm";
		
		//get GeneralExamGrade list
		String sql = "select g from GeneralExamGrade g";
		List<GeneralExamGrade> gradeList = db.list(sql);
		Hashtable<String, GeneralExamGrade> glist = new Hashtable<String, GeneralExamGrade>();
		for ( GeneralExamGrade g : gradeList ) {
			glist.put(g.getName(), g);
		}
		
		String applicantId = request.getParameter("applicant_id");
		Applicant applicant = (Applicant) db.find(Applicant.class, applicantId);
		context.put("applicant", applicant);
		List<StpmResult> stpmResults = applicant.getStpmResults();
		db.begin();
		for ( StpmResult r : stpmResults ) {
			String grade = request.getParameter("stpm_grade_" + r.getId());
			if ( grade != null ) {
				GeneralExamGrade generalExamGrade = glist.get(grade);
				if ( generalExamGrade != null ) {
					r.setGeneralExamGrade(generalExamGrade);
				}
			}
		}
		db.commit();		
	}

	protected void saveSPM() throws Exception {
		vm = path + "applicant_spm_result.vm";
		String applicantId = request.getParameter("applicant_id");
		Applicant applicant = (Applicant) db.find(Applicant.class, applicantId);
		context.put("applicant", applicant);
		List<SpmResult> spmResults = applicant.getSpmResults();
		db.begin();
		for ( SpmResult r : spmResults ) {
			String grade = request.getParameter("spm_grade_" + r.getId());
			if ( grade != null ) {
				r.setGrade(grade);
			}
		}
		db.commit();
	}
	
	protected void getSPM() throws Exception {
		vm = path + "applicant_spm_result.vm";
		reloadApplicant();
	}
	
	protected void getSTPM() throws Exception {
		vm = path + "applicant_stpm_result.vm";
		reloadApplicant();
	}
	
	protected void reloadApplicant() throws Exception {
		String applicantId = request.getParameter("applicant_id");
		Applicant applicant = (Applicant) db.find(Applicant.class, applicantId);
		context.put("applicant", applicant);
	}	
	

	protected void getApplicant() {
		vm = path + "applicant_detail.vm";
		String applicantId = request.getParameter("applicant_id");
		Applicant applicant = (Applicant) db.find(Applicant.class, applicantId);
		context.put("applicant", applicant);
		
		
		String programId = request.getParameter("program_id") != null ? request.getParameter("program_id") : "";
		
		if ( !"".equals(programId)) {
			Program program = (Program) db.find(Program.class, programId);
			context.put("program", program);
		}
		
		String status = request.getParameter("status");
		context.put("status", status);
		
		selectParameters();
		
		String back = request.getParameter("back");
		context.put("back", back);
		
		context.put("change_status", true);
		
		context.put("change_statuses", new Application().getChangeStatuses());
		
	}



	protected void goTo() {
		String status = request.getParameter("go_to_status");
		listByProgram(status);
		
	}

	protected void markNew() throws Exception {
		String[] applicantIds = request.getParameterValues("applicant_ids");
		List<Applicant> applicants = new ArrayList<Applicant>();
		for ( String applicantId : applicantIds ) {
			Applicant applicant = (Applicant) db.find(Applicant.class, applicantId);
			applicants.add(applicant);
		}
		db.begin();
		for ( Applicant applicant : applicants ) {
			applicant.setStatus("NEW");
		}
		db.commit();
		listByProgram();
		
	}

	protected void sendToTrash() throws Exception {
		String[] applicantIds = request.getParameterValues("applicant_ids");
		List<Applicant> applicants = new ArrayList<Applicant>();
		for ( String applicantId : applicantIds ) {
			Applicant applicant = (Applicant) db.find(Applicant.class, applicantId);
			applicants.add(applicant);
		}
		db.begin();
		for ( Applicant applicant : applicants ) {
			applicant.setStatus("DELETED");
		}
		db.commit();
		listByProgram();
		
	}

	protected void markIncomplete() throws Exception {
		String[] applicantIds = request.getParameterValues("applicant_ids");
		List<Applicant> applicants = new ArrayList<Applicant>();
		for ( String applicantId : applicantIds ) {
			Applicant applicant = (Applicant) db.find(Applicant.class, applicantId);
			applicants.add(applicant);
		}
		db.begin();
		for ( Applicant applicant : applicants ) {
			applicant.setStatus("INCOMPLETE");
		}
		db.commit();
		listByProgram();
		
	}
	
	protected void listByProgram() {
		listByProgram("");
	}

	protected void listByProgram(String status) {
		vm = path + "list_applicants.vm";
		status = !"".equals(status) ? status : request.getParameter("status");
		String programId = request.getParameter("program_id");
		
		Program program = (Program) db.find(Program.class, programId);
		context.put("program", program);
		
		context.put("status", status);
		String sql = "select a " +
		" from Applicant a where a.status = '" + status + "' " +
		" and a.choice1.id = '" + program.getId() + "'";
		
		List<Applicant> applicants = db.list(sql);
		context.put("applicants", applicants);
		
		Application application = new Application();
		String[] statuses = application.getStatuses();
		context.put("statuses", statuses);
		
	}

	protected void statBreakDown() {
		vm = path + "stat_programs.vm";
		String status = request.getParameter("status");
		System.out.println("Status = " + status);
		context.put("status", status);
		String sql = "select new educate.sis.module.ApplicantResult(a.choice1.id, a.choice1.code, a.choice1.name, count(a))" +
		" from Applicant a where a.status = '" + status + "' " +
		" group by a.choice1";
		
		List<ApplicantResult> list = db.list(sql);
		List<Hashtable> resultList = new ArrayList<Hashtable>();
		context.put("resultList", resultList);
		for ( ApplicantResult result : list ) {
			Hashtable h = new Hashtable();
			h.put("program_id", result.getProgramId());
			h.put("program_code", result.getProgramCode());
			h.put("program_name", result.getProgramName());
			h.put("total", result.getTotal());
			resultList.add(h);
		}

	}

	protected void start() {
		vm = path + "statistic.vm";
		Application application = new Application();
		String[] statuses = application.getStatuses();
		context.put("statuses", statuses);
		String sql = "";
		
		Hashtable stat = new Hashtable();
		
		for ( String status : statuses ) {
			sql = "select count(a) from Applicant a where a.status = '" + status + "'";
			Long c = (Long) db.get(sql);
			stat.put(status, c);
		}
		context.put("stat", stat);
	}
	

	public static void waiting (int n){
		long t0, t1;
        t0 =  System.currentTimeMillis();
        do{
            t1 = System.currentTimeMillis();
        } 
        while ((t1 - t0) < (n * 1000));
    }
	
	protected void selectParameters() {
		context.put("spm_subjects", db.list("SELECT a from SchoolSubject a WHERE a.stype='SPM' order by a.name"));
		context.put("stpm_subjects", db.list("SELECT a from SchoolSubject a WHERE a.stype='STPM' order by a.name"));
	}

}
