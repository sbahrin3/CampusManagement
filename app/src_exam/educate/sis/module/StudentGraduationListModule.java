package educate.sis.module;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.enrollment.entity.SubjectTransfer;
import educate.sis.exam.entity.FinalResult;
import educate.sis.exam.entity.FinalSubjectResult;
import educate.sis.exam.module.SpecialSubjectStatus;
import educate.sis.exam.module.SubjectStatus;
import educate.sis.struct.entity.CategoryCount;
import educate.sis.struct.entity.Graduate;
import educate.sis.struct.entity.GraduationRequirement;
import educate.sis.struct.entity.GraduationSubjectRequirement;
import educate.sis.struct.entity.LearningCentre;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.Session;
import educate.sis.struct.entity.Subject;
import lebah.portal.action.AjaxModule;

public class StudentGraduationListModule  extends AjaxModule  {
	
	String path = "apps/util/student_graduation/";
	private String vm = "default.vm";
	HttpSession session;
	DbPersistence db = new DbPersistence();
	Hashtable data = null;

	
	public String doAction() throws Exception {
		context.put("_formName", formName);
		session = request.getSession();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		context.put("dateFormat", dateFormat);
		context.put("numFormat", new DecimalFormat("#,###,###.00"));
		NumberFormat chformat = new DecimalFormat("###");
		NumberFormat gpaformat = new DecimalFormat("#.00");
		context.put("chformat", chformat);
		context.put("gpaformat", gpaformat);
		context.put("programUtil", new ProgramUtil());
		context.put("list_type", "");
		String command = request.getParameter("command");
		System.out.println(command);
		if ( null == command || "".equals(command)) start();
		else if ( "select_intakes".equals(command)) listIntakes();
		else if ( "list_students".equals(command)) listGraduationResult();
		else if ( "list_students2".equals(command)) listGraduationResult2();
		
		else if ( "approve_graduate".equals(command)) approveGraduate();
		else if ( "do_approve_graduate".equals(command)) doApproveGraduate();
		else if ( "save_clearance".equals(command)) saveClearance();
		
		else if ( "save_clearance2".equals(command)) saveClearance2();
		
		else if ( "edit_graduate".equals(command)) editGraduate();
		
		else if ( "edit_graduate2".equals(command)) editGraduate2();
		
		else if ( "remove_graduate".equals(command)) removeGraduate2();
		
		else if ( "list_all".equals(command)) listAllGraduates();
		else if ( "list_approved".equals(command)) listApproved();
		else if ( "list_disapproved".equals(command)) listDisapproved();
		
		else if ( "approve_graduate2".equals(command)) approveGraduate2();
		else if ( "show_transcript".equals(command)) showTranscript();
		
		else if ( "clearanceFinance".equals(command)) clearanceSpecific("finance");
		else if ( "clearanceStudentAffair".equals(command)) clearanceSpecific("studentAffair");
		else if ( "clearanceLibrary".equals(command)) clearanceSpecific("library");
		else if ( "clearanceFaculty".equals(command)) clearanceSpecific("faculty");
		
		else if ( "checkAll".equals(command)) checkAll();
		return vm;
	}
	
	private void listGraduationResult2() {
		
		listGraduationResult();
		
		vm = path + "list_students2.vm";
		
	}

	private void checkAll() throws Exception {
		vm = path + "check_all.vm";
		String dept = getParam("dept");
		String operation = getParam("operation");
		
		String programId = request.getParameter("program_id") != null ? request.getParameter("program_id") : "";
		String centreId = request.getParameter("centre_id") != null ? request.getParameter("centre_id") : "";
		String intakeId = request.getParameter("intake_id") != null ? request.getParameter("intake_id") : "";
		String listType= request.getParameter("listType") != null ? request.getParameter("listType") : "";
		context.put("listType", listType);
		
		//list of students in graduate
		String sql = "select f from Graduate f where (f.student.fakeStudent is null OR f.student.fakeStudent = '')";
		sql += " and f.student.program.id = '" + programId + "'";
		sql += " and f.student.intake.id = '" + intakeId + "' ";
		if ( !"".equals(centreId) ) sql += " and f.student.learningCenter.id = '" + centreId + "' ";
		if ( "approved".equals(listType) ) {
			sql += " and f.clearance = 1";
		}
		else if ( "disapproved".equals(listType) ) {
			sql += " and f.clearance = 0";
		}
		
		boolean clearance = "checkAll".equals(operation) ? true : false;
		
		List<Graduate> graduates = db.list(sql);
		db.begin();
		for ( Graduate g : graduates ) {
			if ( "finance".equals(dept)) g.setClearanceFinance(clearance);
			else if ( "library".equals(dept)) g.setClearanceLibrary(clearance);
			else if ( "studentAffair".equals(dept)) g.setClearanceStudentAffair(clearance);
			else if ( "faculty".equals(dept)) g.setClearanceFaculty(clearance);
		}
		db.commit();
		
		if ( "all".equals(listType)) listGraduates(true, true);
		else if ( "approved".equals(listType)) listGraduates(true);
		else if ( "disapproved".equals(listType)) listGraduates(false);
		
	}

	private void clearanceSpecific(String name) throws Exception {
		
		vm = path + "save_clearance2.vm";
		
		String graduateId = request.getParameter("graduate_id");
		Graduate graduate = (Graduate) db.find(Graduate.class, graduateId);
		context.put("graduate", graduate);

		db.begin();
		
		if ( "finance".equals(name)) {
			String clearFinance = getParam("checkFinance_" + graduateId);
			graduate.setClearanceFinance("Y".equals(clearFinance) ? true : false);
		}
		else if ( "library".equals(name)) {
			String clearLibrary = getParam("checkLibrary_" + graduateId);
			graduate.setClearanceLibrary("Y".equals(clearLibrary) ? true : false);
		} else if ( "studentAffair".equals(name)) {
			String clearStudentAffair = getParam("checkStudentAffair_" + graduateId);
			graduate.setClearanceStudentAffair("Y".equals(clearStudentAffair) ? true : false);
		} else if ( "faculty".equals(name)) {
			String clearFaculty = getParam("checkFaculty_" + graduateId);
			graduate.setClearanceFaculty("Y".equals(clearFaculty) ? true : false);
		}
		
		db.commit();
		
	}

	private void showTranscript() throws Exception {
		vm = path + "transcript.vm";
		String studentId = request.getParameter("student_id");
		Student student = (Student) db.find(Student.class, studentId);
		
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
		
	}

	private void approveGraduate2() throws Exception {
		vm = path + "approveGraduate2.vm";
		
		String studentId = request.getParameter("student_id");
		Student student = (Student) db.find(Student.class, studentId);
		
		String check = getParam("approve_" + studentId);
		
		if ( "yes".equals(check)) {
		
			String sql = "select f from FinalResult f where f.student.id = '" + student.getId() + "' order by f.session.startDate desc";
			List<FinalResult> frs = db.list(sql);
			Session finalSession = frs.get(0).getSession();
			
			String remark = request.getParameter("remark");
			Graduate graduate = null;
			sql = "select g from Graduate g where g.student.id = '" + student.getId() + "'";
			graduate = (Graduate) db.get(sql);
			if ( graduate == null ) {
				db.begin();
				graduate = new Graduate();
				graduate.setSession(finalSession);
				graduate.setStudent(student);
				graduate.setRemark(remark);
				db.persist(graduate);
				db.commit();
			}
			else {
				db.begin();
				graduate.setSession(finalSession);
				graduate.setRemark(remark);
				db.commit();
			}
		}
		else {
//			String graduateId = request.getParameter("graduate_id");
//			Graduate graduate = (Graduate) db.find(Graduate.class, graduateId);
			Graduate graduate = (Graduate) db.get("select g from Graduate g where g.student.id = '" + studentId + "'");
			if ( graduate != null ) {
				db.begin();
				db.remove(graduate);
				db.commit();
			}
			
		}
	}
	



	private void listDisapproved() {
		listGraduates(false);
		
	}



	private void listApproved() {
		listGraduates(true);
		
	}



	private void listAllGraduates() {
		listGraduates();
		
	}

	private void saveClearance2() throws Exception {
		
		vm = path + "save_clearance2.vm";
		
		String graduateId = request.getParameter("graduate_id");
		Graduate graduate = (Graduate) db.find(Graduate.class, graduateId);
		context.put("graduate", graduate);
		
		String studentAffairRemark = getParam("student_affair_remark");
		String libraryRemark = getParam("library_remark");
		String financeRemark = getParam("finance_remark");
		String facultyRemark = getParam("faculty_remark");
		
		
		db.begin();
		
		graduate.setStudentAffairRemark(studentAffairRemark);
		graduate.setLibraryRemark(libraryRemark);
		graduate.setFinanceRemark(financeRemark);
		graduate.setFacultyRemark(facultyRemark);
		
		db.commit();
	}
	
	private void saveClearance3() throws Exception {
		
		vm = path + "save_clearance2.vm";
		
		String graduateId = request.getParameter("graduate_id");
		Graduate graduate = (Graduate) db.find(Graduate.class, graduateId);
		context.put("graduate", graduate);
		
		
		String clearFinance = request.getParameter("clearance_finance");
		String clearLibrary = request.getParameter("clearance_library");
		String clearStudentAffair = request.getParameter("clearance_student_affair");
		String clearFaculty = request.getParameter("clearance_faculty");
		String studentAffairRemark = request.getParameter("student_affair_remark");
		String libraryRemark = request.getParameter("library_remark");
		String financeRemark = request.getParameter("finance_remark");
		String facultyRemark = request.getParameter("faculty_remark");
		
		
		db.begin();
		
		graduate.setClearanceFinance("Y".equals(clearFinance) ? true : false);
		graduate.setClearanceLibrary("Y".equals(clearLibrary) ? true : false);
		graduate.setClearanceStudentAffair("Y".equals(clearStudentAffair) ? true : false);
		graduate.setClearanceFaculty("Y".equals(clearFaculty) ? true : false);
		
		graduate.setStudentAffairRemark(studentAffairRemark);
		graduate.setLibraryRemark(libraryRemark);
		graduate.setFinanceRemark(financeRemark);
		graduate.setFacultyRemark(facultyRemark);
		
		db.commit();
	}	

	private void saveClearance() throws Exception {
		String graduateId = request.getParameter("graduate_id");
		Graduate graduate = (Graduate) db.find(Graduate.class, graduateId);
		String clearFinance = request.getParameter("clearance_finance");
		String clearLibrary = request.getParameter("clearance_library");
		String clearStudentAffair = request.getParameter("clearance_student_affair");
		//String remark = request.getParameter("remark");
		String studentAffairRemark = request.getParameter("student_affair_remark");
		String libraryRemark = request.getParameter("library_remark");
		String financeRemark = request.getParameter("finance_remark");
		
		db.begin();
		
		graduate.setClearanceFinance("Y".equals(clearFinance) ? true : false);
		graduate.setClearanceLibrary("Y".equals(clearLibrary) ? true : false);
		graduate.setClearanceStudentAffair("Y".equals(clearStudentAffair) ? true : false);
		
		graduate.setStudentAffairRemark(studentAffairRemark);
		graduate.setLibraryRemark(libraryRemark);
		graduate.setFinanceRemark(financeRemark);
		
		
		//graduate.setRemark(remark);
		
		db.commit();

		listGraduationResult();
		
	}
	
	private void editGraduate2() {
		vm = path + "set_clearance2.vm";
		String graduateId = request.getParameter("graduate_id");
		Graduate graduate = (Graduate) db.find(Graduate.class, graduateId);
		context.put("graduate", graduate);
		context.put("student", graduate.getStudent());
		getParameters();
		
		String listType= request.getParameter("list_type") != null ? request.getParameter("list_type") : "";
		context.put("list_type", listType);
	}	
	
	private void editGraduate() {
		vm = path + "set_clearance.vm";
		String graduateId = request.getParameter("graduate_id");
		Graduate graduate = (Graduate) db.find(Graduate.class, graduateId);
		context.put("graduate", graduate);
		context.put("student", graduate.getStudent());
		getParameters();
		
		String listType= request.getParameter("list_type") != null ? request.getParameter("list_type") : "";
		context.put("list_type", listType);
	}

	private void removeGraduate() throws Exception {
		String graduateId = request.getParameter("graduate_id");
		Graduate graduate = (Graduate) db.find(Graduate.class, graduateId);
		if ( graduate != null ) {
			db.begin();
			db.remove(graduate);
			db.commit();
		}
		

		listGraduationResult();
	}
	
	private void removeGraduate2() throws Exception {
		String graduateId = request.getParameter("graduate_id");
		Graduate graduate = (Graduate) db.find(Graduate.class, graduateId);
		if ( graduate != null ) {
			db.begin();
			db.remove(graduate);
			db.commit();
		}
		
		String listType= request.getParameter("listType") != null ? request.getParameter("listType") : "";

		if ( "all".equals(listType)) listGraduates(true, true);
		else if ( "approved".equals(listType)) listGraduates(true);
		else if ( "disapproved".equals(listType)) listGraduates(false);
	}	

	private void doApproveGraduate() throws Exception {
		vm = path + "set_clearance.vm";
		
		String type= request.getParameter("type") != null ? request.getParameter("type") : "";
		context.put("type", type);
		String sort = request.getParameter("sort") != null ? request.getParameter("sort") : "";
		context.put("sort", sort);
		
		String programId = request.getParameter("program_id") != null ? request.getParameter("program_id") : "";
		String centreId = request.getParameter("centre_id") != null ? request.getParameter("centre_id") : "";
		String intakeId = request.getParameter("intake_id") != null ? request.getParameter("intake_id") : "";
		String studyType = request.getParameter("study_type") != null ? request.getParameter("study_type") : "";
		
		Program program = (Program) db.find(Program.class, programId);
		LearningCentre centre = (LearningCentre) db.find(LearningCentre.class, centreId);
		Session intake = (Session) db.find(Session.class, intakeId);
		
		if ( program != null ) context.put("program", program);
		if ( centre != null ) context.put("centre", centre);
		if ( intake != null ) context.put("intake", intake);
		if ( studyType != null ) context.put("study_type", studyType);
		
		String creditHours = request.getParameter("credit_hours");
		String cgpa = request.getParameter("cgpa");
		
		context.put("credit_hours", creditHours);
		context.put("cgpa", cgpa);
		
		String studentId = request.getParameter("student_id");
		Student student = (Student) db.find(Student.class, studentId);
		String finalSessionId = request.getParameter("final_session_id");
		Session finalSession = (Session) db.find(Session.class, finalSessionId);
		
		String remark = request.getParameter("remark");
		String sql = "";
		Graduate graduate = null;
		sql = "select g from Graduate g where g.student.id = '" + student.getId() + "'";
		graduate = (Graduate) db.get(sql);
		if ( graduate == null ) {
			db.begin();
			graduate = new Graduate();
			graduate.setSession(finalSession);
			graduate.setStudent(student);
			graduate.setRemark(remark);
			db.persist(graduate);
			db.commit();
		}
		else {
			db.begin();
			graduate.setSession(finalSession);
			graduate.setRemark(remark);
			db.commit();
		}
		context.put("graduate", graduate);
		context.put("student", graduate.getStudent());
		//oleh sebab malas dahhh...
		//listStudents();
	}

	private void approveGraduate() {
		vm = path + "approve_graduate.vm";
		
		String type= request.getParameter("type") != null ? request.getParameter("type") : "";
		context.put("type", type);
		String sort = request.getParameter("sort") != null ? request.getParameter("sort") : "";
		context.put("sort", sort);
		
		String programId = request.getParameter("program_id") != null ? request.getParameter("program_id") : "";
		String centreId = request.getParameter("centre_id") != null ? request.getParameter("centre_id") : "";
		String intakeId = request.getParameter("intake_id") != null ? request.getParameter("intake_id") : "";
		String studyType = request.getParameter("study_type") != null ? request.getParameter("study_type") : "";
		
		Program program = (Program) db.find(Program.class, programId);
		LearningCentre centre = (LearningCentre) db.find(LearningCentre.class, centreId);
		Session intake = (Session) db.find(Session.class, intakeId);
		
		if ( program != null ) context.put("program", program);
		if ( centre != null ) context.put("centre", centre);
		if ( intake != null ) context.put("intake", intake);
		if ( studyType != null ) context.put("study_type", studyType);
		
		String creditHours = request.getParameter("credit_hours");
		String cgpa = request.getParameter("cgpa");
		
		context.put("credit_hours", creditHours);
		context.put("cgpa", cgpa);
		
		
		String studentId = request.getParameter("student_id");
		Student student = (Student) db.find(Student.class, studentId);
		context.put("student", student);
		Hashtable param = new Hashtable();
		param.put("date1", student.getIntake().getStartDate());
		param.put("date2", new Date());
		param.put("path_no", student.getProgram().getLevel().getPathNo());
		
		String sql = "";
		
	
		Map<String, FinalResult> resultMap = new HashMap<String, FinalResult>();
		context.put("result_map", resultMap);
		sql = "select f from FinalResult f where f.student.id = '" + student.getId() + "' order by f.session.startDate";
		List<FinalResult> frs = db.list(sql);
		
		for ( FinalResult fr : frs ) {
			resultMap.put(fr.getSession().getId(), fr);
		}
		if ( frs.size() > 0 ) {
			Session finalSession = frs.get(frs.size()-1).getSession();
			context.put("final_session", finalSession);
		}
		
		examTranscript(studentId);
		
	}

	/*
	private void listResults() {
		vm = path + "list_results.vm";
		
		String type= request.getParameter("type") != null ? request.getParameter("type") : "";
		context.put("type", type);
		String sort = request.getParameter("sort") != null ? request.getParameter("sort") : "";
		context.put("sort", sort);
		
		
		String programId = request.getParameter("program_id") != null ? request.getParameter("program_id") : "";
		String centreId = request.getParameter("centre_id") != null ? request.getParameter("centre_id") : "";
		String intakeId = request.getParameter("intake_id") != null ? request.getParameter("intake_id") : "";
		String studyType = request.getParameter("study_type") != null ? request.getParameter("study_type") : "";
		
		if ( "".equals(programId) || "".equals(centreId) || "".equals(intakeId)) {
			start();
			return;
		}
		
		Program program = (Program) db.find(Program.class, programId);
		LearningCentre centre = (LearningCentre) db.find(LearningCentre.class, centreId);
		Session intake = (Session) db.find(Session.class, intakeId);
		
		if ( program != null ) context.put("program", program);
		if ( centre != null ) context.put("centre", centre);
		if ( intake != null ) context.put("intake", intake);
		if ( studyType != null ) context.put("study_type", studyType);
		
		String creditHours = request.getParameter("credit_hours");
		String cgpa = request.getParameter("cgpa");
		
		context.put("credit_hours", creditHours);
		context.put("cgpa", cgpa);
		String sql = "";
		String s = "";
		
		//total credit hrs per program
		Map<String, Long> programCredits = new HashMap<String, Long>();
		sql = "select g from GraduationRequirement g";
		List<GraduationRequirement> glist = db.list(sql);
		for ( GraduationRequirement g : glist ) {
			Set<CategoryCount> ccs = g.getCategoryCounts();
			long total = 0;
			for ( CategoryCount cc : ccs ) {
				total += cc.getCount();
			}
			programCredits.put(g.getProgram().getId(), total);
		}
		
		//list of students for credt transfer
		Map<String, Long> creditTransfer = new HashMap<String, Long>();
		sql = "select new educate.sis.module.CreditHrs(st.student.id, COUNT(s.credithrs)) from SubjectTransfer st join st.subject s group by st.student";
		List<CreditHrs> chlist = db.list(sql);
		for ( CreditHrs i : chlist ) {
			creditTransfer.put(i.getId(), i.getValue());
		}
		
		//list of students in graduate
		sql = "select f from Graduate f where (f.student.fakeStudent is null OR f.student.fakeStudent = '')";
		if ( !"".equals(programId) ) sql += " and f.student.program.id = '" + programId + "'";
		if ( !"".equals(intakeId) ) sql += " and f.student.intake.id = '" + intakeId + "' ";
		if ( !"".equals(centreId) ) sql += " and f.student.learningCenter.id = '" + centreId + "' ";
		List<Graduate> graduates = db.list(sql);
		if ( graduates.size() > 0 ) context.put("graduate_list", graduates);
		else context.remove("graduate_list");
		
		
		//list of students from finalresult
		sql = "select f from FinalResult f where (f.student.fakeStudent is null OR f.student.fakeStudent = '')";
		if ( !"".equals(programId) ) sql += " and f.student.program.id = '" + programId + "'";
		if ( !"".equals(intakeId) ) sql += " and f.student.intake.id = '" + intakeId + "' ";
		if ( !"".equals(centreId) ) sql += " and f.student.learningCenter.id = '" + centreId + "' ";
		
		if ( !"".equals(creditHours)) {
			s = sql.indexOf(" where ") > 0 ? " and " : " where ";
			sql += s + " f.totalHours >= " + creditHours;
		}
		
		if ( !"".equals(cgpa)) {
			s = sql.indexOf(" where ") > 0 ? " and " : " where ";
			sql += s + " f.cgpa >= " + cgpa;
		}
		
		//CHEAT CODE HERE... (kena check cgpa calculation semua dibuat dengan betul!)
		s = sql.indexOf(" where ") > 0 ? " and " : " where ";
		sql += s + " f.cgpa < 4.1"; //sebab ada dalam database cgpa > 5
		
		sql += " order by f.student, f.session.startDate desc";
		
		List<FinalResult> list = new ArrayList<FinalResult>();
		List<FinalResult> results = db.list(sql);
		String tempId = "";
		for ( FinalResult r : results ) {
			if ( !tempId.equals(r.getStudent().getId())) {
				tempId = r.getStudent().getId();
				list.add(r);
			}
		}
		
		List<Record> records = new ArrayList<Record>();
		context.put("records", records);
		if ( list.size() > 0 ) {
			context.put("student_list", list);
			
			for ( FinalResult r : list ) {
				if ( !inGraduateList(graduates, r.getStudent().getId())) {
					Record rec = new Record();
					rec.setStudentId(r.getStudent().getId());
					rec.setMatricNo(r.getStudent().getMatricNo());
					rec.setName(r.getStudent().getBiodata().getName());
					rec.setIntake(r.getStudent().getIntake().getName());
					rec.setFinalSemester(r.getSession().getName());
					rec.setDateFinalSemester(r.getSession().getStartDate());
					rec.setCreditEarned(r.getTotalHours());
					double ct = creditTransfer.get(r.getStudent().getId()) != null ? creditTransfer.get(r.getStudent().getId()) : 0.0d;
					rec.setCreditTransfered(ct);
					double total = r.getTotalHours() + ct;
					rec.setCreditTotalEarned(total);
					rec.setFinalSemesterGPA(r.getGpa());
					rec.setResultCGPA(r.getCgpa());
					double totalReq = 0.0;
					double creditNotEarned = 0.0;
					if ( r.getStudent().getProgram() != null ) {
						totalReq = programCredits.get(r.getStudent().getProgram().getId()) != null ?
								programCredits.get(r.getStudent().getProgram().getId()) : 0;
						creditNotEarned =  totalReq - rec.getCreditTotalEarned();
					}
					
					rec.setCreditNotEarned(creditNotEarned);
					
					records.add(rec);
				}
			}
			
			if ( "cgpa".equals(sort)) Collections.sort(records, new ComparatorByCGPA());
			else if ( "credit_earned".equals(sort)) Collections.sort(records, new ComparatorByCreditHours());
			else Collections.sort(records, new ComparatorByCGPA());
			
		}
		else context.remove("student_list");
		
	}
	*/
	
	private boolean inGraduateList(List<Graduate> graduates, String studentId) {
		for ( Graduate g : graduates ) {
			if ( g.getStudent().getId().equals(studentId)) {
				return true;
			}
		}
		return false;
	}

	private void listIntakes() {
		vm =  path + "select_program.vm";
		
		String type= request.getParameter("type") != null ? request.getParameter("type") : "";
		context.put("type", type);
		String sort = request.getParameter("sort") != null ? request.getParameter("sort") : "";
		context.put("sort", sort);
		
		String programId = request.getParameter("program_id") != null ? request.getParameter("program_id") : "";
		String centreId = request.getParameter("centre_id") != null ? request.getParameter("centre_id") : "";
		String intakeId = request.getParameter("intake_id") != null ? request.getParameter("intake_id") : "";
		String studyType = request.getParameter("study_type") != null ? request.getParameter("study_type") : "";
		
		Program program = (Program) db.find(Program.class, programId);
		LearningCentre centre = (LearningCentre) db.find(LearningCentre.class, centreId);
		Session intake = (Session) db.find(Session.class, intakeId);
		
		if ( program != null ) context.put("program", program);
		if ( centre != null ) context.put("centre", centre);
		if ( intake != null ) context.put("intake", intake);
		if ( studyType != null ) context.put("study_type", studyType);
		
		String creditHours = request.getParameter("credit_hours");
		String cgpa = request.getParameter("cgpa");
		
		context.put("credit_hours", creditHours);
		context.put("cgpa", cgpa);
		
		List<Program> programs = db.list("select p from Program p order by p.code");
		context.put("programs", programs);
		
		List<LearningCentre> centres = db.list("select c from LearningCentre c order by c.code");
		context.put("centres", centres);
		
		if ( program != null && program.getLevel() != null ) {
			List<Session> intakes = db.list("select s from Session s where s.pathNo = " + program.getLevel().getPathNo() + " order by s.startDate");
			context.put("intakes", intakes);
		} else
			context.remove("intakes");

	}

	private void start() {
		vm =  path + "select_program.vm";
		List<Program> programs = db.list("select p from Program p order by p.code");
		context.put("programs", programs);
		List<LearningCentre> centres = db.list("select c from LearningCentre c order by c.code");
		context.put("centres", centres);
		context.remove("intakes");
	}
	


	
	private void getParameters() {
		
		String type= request.getParameter("type") != null ? request.getParameter("type") : "";
		context.put("type", type);
		String sort = request.getParameter("sort") != null ? request.getParameter("sort") : "";
		context.put("sort", sort);
		

		
		String programId = request.getParameter("program_id") != null ? request.getParameter("program_id") : "";
		String centreId = request.getParameter("centre_id") != null ? request.getParameter("centre_id") : "";
		String intakeId = request.getParameter("intake_id") != null ? request.getParameter("intake_id") : "";
		String studyType = request.getParameter("study_type") != null ? request.getParameter("study_type") : "";
		
		Program program = (Program) db.find(Program.class, programId);
		LearningCentre centre = (LearningCentre) db.find(LearningCentre.class, centreId);
		Session intake = (Session) db.find(Session.class, intakeId);
		
		if ( program != null ) context.put("program", program);
		if ( centre != null ) context.put("centre", centre);
		if ( intake != null ) context.put("intake", intake);
		if ( studyType != null ) context.put("study_type", studyType);
		
		String creditHours = request.getParameter("credit_hours");
		String cgpa = request.getParameter("cgpa");
		
		context.put("credit_hours", creditHours);
		context.put("cgpa", cgpa);
	}
	


	private void listGraduationResult() {
		vm = path + "list_results.vm";
		
		String type= request.getParameter("type") != null ? request.getParameter("type") : "";
		context.put("type", type);
		String sort = request.getParameter("sort") != null ? request.getParameter("sort") : "";
		context.put("sort", sort);

		String programId = request.getParameter("program_id") != null ? request.getParameter("program_id") : "";
		String centreId = request.getParameter("centre_id") != null ? request.getParameter("centre_id") : "";
		String intakeId = request.getParameter("intake_id") != null ? request.getParameter("intake_id") : "";
		String studyType = request.getParameter("study_type") != null ? request.getParameter("study_type") : "";
		
		if ( "".equals(programId) || "".equals(intakeId)) {
			start();
			return;
		}
		
		Program program = (Program) db.find(Program.class, programId);
		LearningCentre centre = (LearningCentre) db.find(LearningCentre.class, centreId);
		Session intake = (Session) db.find(Session.class, intakeId);
		
		if ( program != null ) context.put("program", program);
		if ( centre != null ) context.put("centre", centre);
		if ( intake != null ) context.put("intake", intake);
		if ( studyType != null ) context.put("study_type", studyType);
		
		Hashtable param = new Hashtable();
		param.put("program", program);
		param.put("intake", intake);
		param.put("type", studyType);
		String sql = "select g from GraduationRequirement g where g.program = :program and g.intake = :intake and g.type = :type";
		List<GraduationRequirement> graduationRequirementList = db.list(sql, param);
		GraduationRequirement grad = null;
		Set<GraduationSubjectRequirement> requiredSubjects = null;
		
		Hashtable<String, String> subjectMark = new Hashtable<String, String>();
		Hashtable<String, Double> subjectPoint = new Hashtable<String, Double>();
		if ( graduationRequirementList.size() > 0 ) {
			grad = graduationRequirementList.get(0);
			requiredSubjects = grad.getSubjects();
			for ( GraduationSubjectRequirement s : requiredSubjects ) {
				subjectMark.put(s.getSubject().getId(), s.getMark());
				subjectPoint.put(s.getSubject().getId(), s.getPoint());
			}
		}
		else {
			context.put("error_message", "Graduation Requirement Not Set!");
			return;
		}


		String cgpa = grad.getMinCGPA() > 0.0 ? Double.toString(grad.getMinCGPA()) : "";
		
		Set<CategoryCount> cats = grad.getCategoryCounts();
		int totalCreditHours = 0;
		for ( CategoryCount cat : cats ) {
			totalCreditHours += cat.getCount();
		}
		String creditHours = totalCreditHours > 0 ? Integer.toString(totalCreditHours) : "";
		
		context.put("credit_hours", creditHours);
		context.put("cgpa", cgpa);
		if ( requiredSubjects != null ) context.put("required_subjects", requiredSubjects);
		else context.remove("required_subjects");
		String s = "";
		
	
		String listType= request.getParameter("list_type") != null ? request.getParameter("list_type") : "";
		context.put("list_type", listType);
		//list of students in graduate
		sql = "select f from Graduate f where (f.student.fakeStudent is null OR f.student.fakeStudent = '')";
		sql += " and f.student.program.id = '" + programId + "'";
		sql += " and f.student.intake.id = '" + intakeId + "' ";
		if ( !"".equals(centreId) ) sql += " and f.student.learningCenter.id = '" + centreId + "' ";
		
		context.put("list_type", "all");
		if ( "approved".equals(listType) ) {
			sql += " and f.clearance = 1";
			context.put("list_type", "approved");
		}
		else if ( "disapproved".equals(listType) ) {
			sql += " and f.clearance = 0";
			context.put("list_type", "disapproved");
		}
		List<Graduate> graduates = db.list(sql);
		if ( graduates.size() > 0 ) context.put("graduate_list", graduates);
		else context.remove("graduate_list");
		
		if ( "".equals(listType) || "students".equals(listType)) {
			//list of students from finalresult
			sql = "select f from FinalResult f where (f.student.fakeStudent is null OR f.student.fakeStudent = '')";
			sql += " and f.student.program.id = '" + programId + "'";
			sql += " and f.student.intake.id = '" + intakeId + "' ";
			if ( !"".equals(centreId) ) sql += " and f.student.learningCenter.id = '" + centreId + "' ";
			
			if ( !"".equals(creditHours)) {
				s = sql.indexOf(" where ") > 0 ? " and " : " where ";
				sql += s + " f.totalHours >= " + creditHours;
			}
			
			if ( !"".equals(cgpa)) {
				s = sql.indexOf(" where ") > 0 ? " and " : " where ";
				sql += s + " f.cgpa >= " + cgpa;
			}
			
			//CHEAT CODE HERE... (kena check cgpa calculation semua dibuat dengan betul!)
			s = sql.indexOf(" where ") > 0 ? " and " : " where ";
			sql += s + " f.cgpa < 4.1"; //sebab ada dalam database cgpa > 5
			
			sql += " order by f.student, f.session.startDate desc";
			
	
			List<FinalResult> finalResults = db.list(sql);
			List<FinalResult> finalResultList = new ArrayList<FinalResult>();		
			String tempId = "";
			for ( FinalResult r : finalResults ) {
				if ( !tempId.equals(r.getStudent().getId())) {
					tempId = r.getStudent().getId();
					finalResultList.add(r);
				}
			}
			
			program.getLevel();
			List<Record> records = new ArrayList<Record>();
			context.put("records", records);
			
	
			
			if ( graduationRequirementList.size() > 0 ) {
				context.put("student_list", graduationRequirementList);
	
	
				for ( FinalResult r : finalResultList ) {
					if ( !inGraduateList(graduates, r.getStudent().getId())) {
					
						//need to check against required subjects
						
						boolean addToRecord = true;
	
							
						int numOfRequiredSubjects = requiredSubjects.size();
						int numOfMatchSubjects = 0;
						Map<String, Integer> specialStatusMap = new HashMap<String, Integer>();
						
						for ( SubjectStatus ss : SpecialSubjectStatus.getListOfStatus() ) {
							specialStatusMap.put(ss.getCode(), new Integer(0));
						}
						
						sql = "select r from FinalResult r where r.student.id = '" + r.getStudent().getId() + "' order by r.session.startDate";
						List<FinalResult> studentFinalResults = db.list(sql);
						
						double specialCreditHrs = 0;
	
						
						for ( FinalResult sfr : studentFinalResults ) {
							for ( FinalSubjectResult subjectResult : sfr.getSubjects() ) {
								//special subject status
								if ( SpecialSubjectStatus.isMatch(subjectResult.getSubjectStatus())) {
									
									specialCreditHrs += subjectResult.getCreditHour();
	
									for ( SubjectStatus ss : SpecialSubjectStatus.getListOfStatus() ) {
		
										if ( ss.getCode().equals(subjectResult.getSubjectStatus())) {
											Integer c = specialStatusMap.get(ss.getCode());
											if ( c == null ) {
												specialStatusMap.put(ss.getCode(), new Integer(subjectResult.getCreditHour()));
											}
											else {
												int c2 = c.intValue() + subjectResult.getCreditHour();
												specialStatusMap.put(ss.getCode(), new Integer(c2));
											}
										}
									}
									
								}
								
								if ( requiredSubjects != null && requiredSubjects.size() > 0 ) {
									Subject subject = subjectResult.getSubject();
									String grade = subjectResult.getGrade();
									double point = subjectResult.getGradePoint();
									String requiredMark = subjectMark.get(subject.getId());
									if ( grade != null ) {
										if ( requiredMark != null ) {
											if ( grade.equals(requiredMark)) {
												numOfMatchSubjects++;
											}
											else if ( subjectResult.getGradePoint() >= subjectPoint.get(subject.getId())) {
												numOfMatchSubjects++;
											}
										}
									}
								}
									
		
							}
						
						}

						if ( requiredSubjects != null && requiredSubjects.size() > 0 ) {
							if ( numOfMatchSubjects < numOfRequiredSubjects ) {
								addToRecord = false;
							}
						}
						
						double creditEarned = r.getTotalHours() + specialCreditHrs;
						
						if ( addToRecord ) {
							Record rec = new Record();
							rec.setStudentId(r.getStudent().getId());
							rec.setMatricNo(r.getStudent().getMatricNo());
							rec.setName(r.getStudent().getBiodata().getName());
							rec.setIntake(r.getStudent().getIntake().getName());
							rec.setFinalSemester(r.getSession().getName());
							
							rec.setFinalSemesterName(r.getPeriod().getName());
							rec.setFinalSemesterSession(r.getSession().getName());
							
							rec.setDateFinalSemester(r.getSession().getStartDate());
							rec.setCreditEarned(r.getTotalHours());
							
							//double ct = creditTransfer.get(r.getStudent().getId()) != null ? creditTransfer.get(r.getStudent().getId()) : 0.0d;
							rec.setCreditTransfered(specialCreditHrs);
							
							rec.setSpecialStatusMap(specialStatusMap);
							
							double total = r.getTotalHours() + specialCreditHrs;
							rec.setCreditTotalEarned(total);
							rec.setFinalSemesterGPA(r.getGpa());
							rec.setResultCGPA(r.getCgpa());
		
							double totalReq = 0.0;
							double creditNotEarned = 0.0;
							totalReq = totalCreditHours;
							creditNotEarned =  totalReq - rec.getCreditTotalEarned();
							rec.setCreditNotEarned(creditNotEarned);
							
							records.add(rec);
						
						}
						
					}
				}
				
				if ( "cgpa".equals(sort)) Collections.sort(records, new ComparatorByCGPA());
				else if ( "credit_earned".equals(sort)) Collections.sort(records, new ComparatorByCreditHours());
				else if ( "final_semester".equals(sort))  Collections.sort(records, new ComparatorByFinalSemester());
				else if ( "name".equals(sort)) Collections.sort(records, new ComparatorByName());
				else if ( "matricNo".equals(sort)) Collections.sort(records, new ComparatorByMatricNo());
				else Collections.sort(records, new ComparatorByName());
				
	
				
			}
			else context.remove("student_list");
		
		}
		
	}	
	
	private void listGraduates() {
		listGraduates(true, true);
	}
	
	private void listGraduates(boolean approved) {
		listGraduates(approved, false);
	}
	
	private void listGraduates(boolean approved, boolean all) {
		vm = path + "list_graduates2.vm";
		
		String type= request.getParameter("type") != null ? request.getParameter("type") : "";
		context.put("type", type);
		String sort = request.getParameter("sort") != null ? request.getParameter("sort") : "";
		context.put("sort", sort);
		
		String programId = request.getParameter("program_id") != null ? request.getParameter("program_id") : "";
		String centreId = request.getParameter("centre_id") != null ? request.getParameter("centre_id") : "";
		String intakeId = request.getParameter("intake_id") != null ? request.getParameter("intake_id") : "";
		String studyType = request.getParameter("study_type") != null ? request.getParameter("study_type") : "";
		
		if ( "".equals(programId) || "".equals(centreId) || "".equals(intakeId)) {
			start();
			return;
		}
		
		Program program = (Program) db.find(Program.class, programId);
		LearningCentre centre = (LearningCentre) db.find(LearningCentre.class, centreId);
		Session intake = (Session) db.find(Session.class, intakeId);
		
		
		if ( program != null ) context.put("program", program);
		if ( centre != null ) context.put("centre", centre);
		if ( intake != null ) context.put("intake", intake);
		if ( studyType != null ) context.put("study_type", studyType);
		
		Hashtable param = new Hashtable();
		param.put("program", program);
		param.put("intake", intake);
		//list of students in graduate
		String sql = "select f from Graduate f where (f.student.fakeStudent is null OR f.student.fakeStudent = '')";
		sql += " and f.student.program.id = '" + programId + "'";
		sql += " and f.student.intake.id = '" + intakeId + "' ";
		sql += " and f.student.learningCenter.id = '" + centreId + "' ";
		
		if ( all ) {
			context.put("list_type", "all");
		}
		else if ( approved ) {
			sql += " and f.clearance = 1";
			context.put("list_type", "approved");
		}
		else if ( !approved ) {
			sql += " and f.clearance = 0";
			context.put("list_type", "disapproved");
		}
		else {
			context.put("list_type", "all");
		}
		
		List<Graduate> graduates = db.list(sql);
		if ( graduates.size() > 0 ) context.put("graduate_list", graduates);
		else context.remove("graduate_list");
		
	}
	
	
	
	private void examTranscript(String studentId) {
		
		Student student = (Student) db.find(Student.class, studentId);
		context.put("student", student);
		String sql = "select r from FinalResult r where r.student.id = '" + student.getId() + "' order by r.session.startDate";
		
		List<FinalResult> results = db.list(sql);
		if ( results.size() > 0 ) {
			context.put("results", results);
		}
		else {
			context.remove("results");
		}
		
		//credit transfer
		sql = "select st from SubjectTransfer st where st.student.id = '" + studentId + "'";
		List<SubjectTransfer> transfers = db.list(sql);
		context.put("subject_transfers", transfers);
		
	}
	
	public static class ComparatorByCGPA extends educate.util.MyComparator {
		public int compare(Object o1, Object o2) {
			Record r1 = (Record) o1;
			Record r2 = (Record) o2;
			if ( r1.getResultCGPA() > r2.getResultCGPA() ) return -1;
			else if ( r1.getResultCGPA() < r2.getResultCGPA() ) return 1;
			return 0;
		}
	}
	
	public static class ComparatorByCreditHours extends educate.util.MyComparator {

		public int compare(Object o1, Object o2) {
			Record r1 = (Record) o1;
			Record r2 = (Record) o2;
			if ( r1.getCreditTotalEarned() > r2.getCreditTotalEarned()) return -1;
			else if ( r1.getCreditTotalEarned() < r2.getCreditTotalEarned()) return 1;
			return 0;
		}
		
	}
	
	public static class ComparatorByFinalSemester extends educate.util.MyComparator {

		public int compare(Object o1, Object o2) {
			Record r1 = (Record) o1;
			Record r2 = (Record) o2;
			if ( r1.getDateFinalSemester().after(r2.getDateFinalSemester())) return 1;
			else if ( r1.getDateFinalSemester().before(r2.getDateFinalSemester())) return -1;
			return 0;
		}
		
	}
	
	public static class ComparatorByMatricNo extends educate.util.MyComparator {

		public int compare(Object o1, Object o2) {
			Record r1 = (Record) o1;
			Record r2 = (Record) o2;
			return r1.getMatricNo().compareTo(r2.getMatricNo());
		}
		
	}	
	
	public static class ComparatorByName extends educate.util.MyComparator {

		public int compare(Object o1, Object o2) {
			Record r1 = (Record) o1;
			Record r2 = (Record) o2;
			return r1.getName().compareTo(r2.getName());
		}
		
	}	
	
	private void listGraduationResultTypeGeneral(String sort, Program program, LearningCentre centre, Session intake) {
		Hashtable param = new Hashtable();
		param.put("program", program);
		param.put("intake", intake);
		String sql = "select g from GraduationRequirement g where g.program = :program and g.intake = :intake";
		List<GraduationRequirement> graduationRequirementList = db.list(sql, param);
		GraduationRequirement grad = null;
		Set<GraduationSubjectRequirement> requiredSubjects = null;
		
		Hashtable<String, String> subjectMark = new Hashtable<String, String>();
		Hashtable<String, Double> subjectPoint = new Hashtable<String, Double>();
		if ( graduationRequirementList.size() > 0 ) {
			grad = graduationRequirementList.get(0);
			requiredSubjects = grad.getSubjects();
			for ( GraduationSubjectRequirement s : requiredSubjects ) {
				subjectMark.put(s.getSubject().getId(), s.getMark());
				subjectPoint.put(s.getSubject().getId(), s.getPoint());
			}
		}
		else {

			context.put("error_message", "Graduation Requirement Not Set!");
			return;
		}


		String cgpa = grad.getMinCGPA() > 0.0 ? Double.toString(grad.getMinCGPA()) : "";
		
		Set<CategoryCount> cats = grad.getCategoryCounts();
		int totalCreditHours = 0;
		for ( CategoryCount cat : cats ) {
			totalCreditHours += cat.getCount();
		}
		String creditHours = totalCreditHours > 0 ? Integer.toString(totalCreditHours) : "";
		
		context.put("credit_hours", creditHours);
		context.put("cgpa", cgpa);
		if ( requiredSubjects != null ) context.put("required_subjects", requiredSubjects);
		else context.remove("required_subjects");
		String s = "";
		
	
		//list of students for credt transfer
//		Map<String, Long> creditTransfer = new HashMap<String, Long>();
//		sql = "select new educate.sis.module.CreditHrs(st.student.id, COUNT(s.credithrs)) from SubjectTransfer st join st.subject s group by st.student";
//		List<CreditHrs> chlist = db.list(sql);
//		for ( CreditHrs i : chlist ) {
//			creditTransfer.put(i.getId(), i.getValue());
//		}
//		

		
		String listType= request.getParameter("list_type") != null ? request.getParameter("list_type") : "";
		context.put("list_type", listType);
		//list of students in graduate
		sql = "select f from Graduate f where (f.student.fakeStudent is null OR f.student.fakeStudent = '')";
		sql += " and f.student.program.id = '" + program.getId() + "'";
		sql += " and f.student.intake.id = '" + intake.getId() + "' ";
		sql += " and f.student.learningCenter.id = '" + centre.getId() + "' ";
		
		context.put("list_type", "all");
		if ( "approved".equals(listType) ) {
			sql += " and f.clearance = 1";
			context.put("list_type", "approved");
		}
		else if ( "disapproved".equals(listType) ) {
			sql += " and f.clearance = 0";
			context.put("list_type", "disapproved");
		}
		List<Graduate> graduates = db.list(sql);
		if ( graduates.size() > 0 ) context.put("graduate_list", graduates);
		else context.remove("graduate_list");

		
		//list of students from finalresult
		sql = "select f from FinalResult f where (f.student.fakeStudent is null OR f.student.fakeStudent = '')";
		sql += " and f.student.program.id = '" + program.getId() + "'";
		sql += " and f.student.intake.id = '" + intake.getId() + "' ";
		sql += " and f.student.learningCenter.id = '" + centre.getId() + "' ";
		if ( !"".equals(creditHours)) {
			s = sql.indexOf(" where ") > 0 ? " and " : " where ";
			sql += s + " f.totalHours >= " + creditHours;
		}
		if ( !"".equals(cgpa)) {
			s = sql.indexOf(" where ") > 0 ? " and " : " where ";
			sql += s + " f.cgpa >= " + cgpa;
		}
		//CHEAT CODE HERE... (kena check cgpa calculation semua dibuat dengan betul!)
		s = sql.indexOf(" where ") > 0 ? " and " : " where ";
		sql += s + " f.cgpa < 4.1"; //sebab ada dalam database cgpa > 5
		sql += " order by f.student, f.session.startDate desc";
		
		List<FinalResult> finalResultList = new ArrayList<FinalResult>();
		List<FinalResult> results = db.list(sql);
		String tempId = "";
		for ( FinalResult r : results ) {
			if ( !tempId.equals(r.getStudent().getId())) {
				tempId = r.getStudent().getId();
				finalResultList.add(r);
			}
		}
		
		program.getLevel();
		List<Record> records = new ArrayList<Record>();
		context.put("records", records);
		if ( graduationRequirementList.size() > 0 ) {
			context.put("student_list", graduationRequirementList);
			
			for ( FinalResult r : finalResultList ) {
				if ( !inGraduateList(graduates, r.getStudent().getId())) {
					//need to check against required subjects
					boolean addToRecord = true;
					
					if ( requiredSubjects != null && requiredSubjects.size() > 0 ) {
						
						int numOfRequiredSubjects = requiredSubjects.size();
						int numOfMatchSubjects = 0;

					
						sql = "select s from FinalResult f join f.subjects s where f.student.id = '" + r.getStudent().getId() + "'";
						List<FinalSubjectResult> subjectResults = db.list(sql);
						for ( FinalSubjectResult subjectResult : subjectResults ) {
							Subject subject = subjectResult.getSubject();
							String grade = subjectResult.getGrade();
							double point = subjectResult.getGradePoint();
							String requiredMark = subjectMark.get(subject.getId());
							if ( grade != null ) {
								if ( requiredMark != null ) {

									if ( grade.equals(requiredMark)) {
										//System.out.println("mark matches");
										numOfMatchSubjects++;
									}
									else if ( subjectResult.getGradePoint() >= subjectPoint.get(subject.getId())) {
										//System.out.println("grade is more " + subjectResult.getGradePoint() + ", " + subjectPoint.get(subject.getId()));
										numOfMatchSubjects++;
									}
								}
							}
						}
						//System.out.println("subjects required match = " + numOfMatchSubjects + ", " + numOfRequiredSubjects);
						if ( numOfMatchSubjects < numOfRequiredSubjects ) {
							addToRecord = false;
						}
					}
					
					if ( addToRecord ) {
						Record rec = new Record();
						rec.setStudentId(r.getStudent().getId());
						rec.setMatricNo(r.getStudent().getMatricNo());
						rec.setName(r.getStudent().getBiodata().getName());
						rec.setIntake(r.getStudent().getIntake().getName());
						rec.setFinalSemester(r.getSession().getName());
						
						rec.setDateFinalSemester(r.getSession().getStartDate());
						
						rec.setCreditEarned(r.getTotalHours());
						double total = r.getTotalHours();
						//double ct = creditTransfer.get(r.getStudent().getId()) != null ? creditTransfer.get(r.getStudent().getId()) : 0.0d;
						//rec.setCreditTransfered(ct);
						//total += + ct;
						rec.setCreditTotalEarned(total);
						rec.setFinalSemesterGPA(r.getGpa());
						rec.setResultCGPA(r.getCgpa());
	
						double totalReq = 0.0;
						double creditNotEarned = 0.0;
						totalReq = totalCreditHours;
						creditNotEarned =  totalReq - rec.getCreditTotalEarned();
						rec.setCreditNotEarned(creditNotEarned);
						
						records.add(rec);
					
					}
					
				}
			}
			
			if ( "cgpa".equals(sort)) Collections.sort(records, new ComparatorByCGPA());
			else if ( "credit_earned".equals(sort)) Collections.sort(records, new ComparatorByCreditHours());
			else if ( "final_semester".equals(sort))  Collections.sort(records, new ComparatorByFinalSemester());
			else Collections.sort(records, new ComparatorByCGPA());
			

			
		}
		else context.remove("student_list");
	}
	
	
	public static void main(String[] args) throws Exception {
		DbPersistence db = new DbPersistence();
		String sql = "select r from FinalResult r where r.student.id = '1370132444003' order by r.session.startDate";
		List<FinalResult> results = db.list(sql);
		for ( FinalResult r : results ) {
			for ( FinalSubjectResult sr : r.getSubjects() ) {
				if ( SpecialSubjectStatus.isMatch(sr.getSubjectStatus()) ) {
					System.out.println(sr.getSubjectStatus() + " - " + sr.getSubject().getCredithrs());
				}
			}
			
		}
		
	}

}
