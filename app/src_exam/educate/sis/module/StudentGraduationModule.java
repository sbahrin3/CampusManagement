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
import educate.enrollment.entity.Student;
import educate.sis.exam.entity.FinalSubjectResult;
import educate.sis.struct.entity.CategoryCount;
import educate.sis.struct.entity.GraduationRequirement;
import educate.sis.struct.entity.LearningCentre;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.ProgramStructure;
import educate.sis.struct.entity.Session;
import educate.sis.struct.entity.SubjectCategory;
import educate.sis.struct.entity.SubjectPeriod;
import educate.sis.struct.entity.SubjectReg;
import lebah.portal.action.AjaxModule;

public class StudentGraduationModule  extends AjaxModule  {
	
	String path = "apps/util/student_graduation/";
	protected String vm = "default.vm";
	HttpSession session;
	DbPersistence db = new DbPersistence();
	Hashtable data = null;
	List<SubjectVO> subjectsEarned = null;
	List<SubjectVO> allSubjects = null;

	
	public String doAction() throws Exception {
		context.put("_formName", formName);
		session = request.getSession();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		context.put("dateFormat", dateFormat);
		context.put("programUtil", new ProgramUtil());
		context.put("list_type", "");
		context.remove("student_view");
		String command = request.getParameter("command");
		System.out.println(command);
		if ( null == command || "".equals(command)) start();
		else if ( "get_student_by_matric".equals(command)) getStudentByMatricNo();
		else if ( "get_credit_earned".equals(command)) getCreditEarned();
		else if ( "get_credit_balance".equals(command)) getCreditBalance();
		else if ( "get_credit_total_earned".equals(command)) getCreditTotalEarned();
		else if ( "get_credit_total_balance".equals(command)) getCreditTotalBalance();
		
		else if ( "search_student".equals(command)) searchStudent();
		else if ( "find_student".equals(command)) findStudent();
		
		return vm;
	}

	private void findStudent() {
		vm = path + "search_student_list.vm";
		String namePattern = request.getParameter("name_pattern");
		String sql = "";
		sql = "select s from Student s where (s.fakeStudent is null OR s.fakeStudent = '') and s.biodata.name LIKE '%" + namePattern + "%'";
		List<Student> students = db.list(sql);
		context.put("students", students);
		
	}

	private void searchStudent() {
		vm = path + "search_student.vm";
		
	}

	protected void getCreditTotalBalance() throws Exception {
		
		
		
		vm = path + "subject_list.vm";
		context.put("list_type", "credit_total_balance");
		Student student = getStudent();
		List<SubjectVO> listSubjects = new ArrayList<SubjectVO>();
		for ( SubjectVO vo : allSubjects ) {
			if ( !subjectsEarned.contains(vo)) {
				listSubjects.add(vo);
			}
		}
		if ( listSubjects.size() > 0 ) {
			context.put("subject_list", listSubjects);
		} else {
			context.remove("subject_list");
		}
	}

	protected void getCreditTotalEarned() throws Exception {
		vm = path + "subject_list.vm";
		context.put("list_type", "credit_total_earned");
		Student student = getStudent();
		List<SubjectVO> subjectList = new ArrayList<SubjectVO>();
		if ( subjectsEarned.size() > 0 ) {
			context.put("subject_list", subjectsEarned);
		}
		else {
			context.remove("subject_list");
		}
		
	}

	protected void getCreditBalance() throws Exception {
		vm = path + "subject_list.vm";
		context.put("list_type", "credit_balance");
		Student student = getStudent();
		
		String categoryId = request.getParameter("category_id");
		SubjectCategory category = (SubjectCategory) db.find(SubjectCategory.class, categoryId);
		context.put("category", category);
		List<SubjectVO> listSubjects = new ArrayList<SubjectVO>();
		for ( SubjectVO vo : allSubjects ) {
			if ( vo.getCategory().getId().equals(categoryId)&& !subjectsEarned.contains(vo)) {
				listSubjects.add(vo);
			}
		}
		if ( listSubjects.size() > 0 ) {
			context.put("subject_list", listSubjects);
		} else {
			context.remove("subject_list");
		}
		
	}



	protected void getCreditEarned() throws Exception {
		vm = path + "subject_list.vm";
		context.put("list_type", "credit_earned");
		Student student = getStudent();
		String categoryId = request.getParameter("category_id");
		SubjectCategory category = (SubjectCategory) db.find(SubjectCategory.class, categoryId);
		context.put("category", category);
		List<SubjectVO> subjectList = new ArrayList<SubjectVO>();
		
		if ( subjectsEarned.size() > 0 ) {
			int i = 0;
			for ( SubjectVO vo : subjectsEarned ) {
				if ( vo.getCategory() != null && vo.getCategory().getId().equals(categoryId)) {
					//System.out.println(++i + ": " + vo.getSubject().getCode());
					subjectList.add(vo);
				}
			}
			context.put("subject_list", subjectList);
		}
		else {
			context.remove("subject_list");
		}
	}
	
	protected Student getStudent() throws Exception {
		
		String studentId = request.getParameter("student_id");
		Student student = (Student) db.find(Student.class, studentId);
		context.put("student", student);
		studentGraduation(student);
		return student;
	}

	
	protected void getStudentByMatricNo() throws Exception {
		vm = path + "student.vm";
		String matricNo = request.getParameter("student_no");
		String sql = "";
		sql = "select s from Student s where s.matricNo = '" + matricNo + "'";
		Student student = (Student) db.get(sql);
		if ( student == null ) {
			context.remove("student");
			return;
		}
		context.put("student", student);
		studentGraduation(student);
	}


	protected void studentGraduation(Student student) throws Exception {
		
		subjectsEarned = new ArrayList<SubjectVO>();
		
		Session intake = student.getIntake();
		Hashtable param = new Hashtable();
		param.put("date1", intake.getStartDate());
		param.put("date2", new Date());
		param.put("path_no", student.getProgram().getLevel().getPathNo());
		String sql = "select s from Session s where s.startDate between :date1 and :date2 and s.pathNo = :path_no";
		List<Session> sessions = db.list(sql, param);
		context.put("sessions", sessions);
		
		//category subject
		sql = "select c from SubjectCategory c";
		List<SubjectCategory> categories = db.list(sql);
		context.put("categories", categories);
		Hashtable countByCat = new Hashtable();
		for ( SubjectCategory cat : categories ) {
			countByCat.put(cat.getId(), 0);
		}
		countByCat.put("null", 0);
		context.put("category_count", countByCat);
		
		
		//count total of credits from final exam result data
		String key = "";
		
		Hashtable sessionSubjects = new Hashtable();
		context.put("subjects_earned", subjectsEarned);
		
		Map<String, SubjectCategory> catMapping = subjectCategoryMapping(db, student.getProgram(), student.getIntake(), student.getLearningCenter());
		
		System.out.println("Subjects EARNED.");
		ProgramUtil pu = new ProgramUtil();
		for ( Session session : sessions ) {
			Set<FinalSubjectResult> subjects = pu.getResults(student, session);
			
			System.out.println("Final Subjects Result...");
			System.out.println(subjects.size());
			
			sessionSubjects.put(session.getId(), subjects);
			for ( FinalSubjectResult s : subjects ) {
				SubjectCategory subjectCategory = catMapping.get(s.getSubject().getId());
				key = subjectCategory != null ? subjectCategory.getId() : "null";
				subjectsEarned.add(new SubjectVO(s.getSubject(), session, subjectCategory));

				if ( countByCat.get(key) != null ) {
					int i = (Integer) countByCat.get(key);
					if ( s.getGradePoint() > 0 ) {
						i = i + s.getSubject().getCredithrs();
						countByCat.put(key, i);
					}
				} else {
					countByCat.put(key, 0);
				}
			}
		}
		
		//get graduation requirement
		getGraduationRequirement(student.getStudyType(), student.getProgram(), student.getIntake(), countByCat);

		
	}
	
	protected void start() {
		vm = path + "student.vm";
		context.remove("student");
	}
	
	protected void getGraduationRequirement(String type, Program program, Session intake, Hashtable<String, Integer> studentCount) {
		if ( type == null || "".equals(type)) type = "G";
		Hashtable param = new Hashtable();
		param.put("program", program);
		param.put("intake", intake);
		param.put("type", type);
		String sql = "select g from GraduationRequirement g where g.program = :program and g.intake = :intake and g.type = :type";
		List<GraduationRequirement> list = db.list(sql, param);
		Hashtable<String, Integer> cats = new Hashtable<String, Integer>();
		Hashtable<String, Integer> catBalance = new Hashtable<String, Integer>();
		GraduationRequirement g = null;
		int totalCreditRequired = 0;
		int totalCreditEarned = 0;
		int totalCreditBalance = 0;
		if ( list.size() > 0 ) {
			g = list.get(0);
			context.put("graduation", g);
			Set<CategoryCount> counts = g.getCategoryCounts();
			for ( CategoryCount c : counts ) {
				cats.put(c.getCategory().getId(), c.getCount());
				int creditEarned = studentCount.get(c.getCategory().getId());
				int creditBalance = c.getCount() - creditEarned;
				catBalance.put(c.getCategory().getId(), creditBalance);
				totalCreditRequired += c.getCount();
				totalCreditEarned += creditEarned;
				totalCreditBalance += creditBalance;
			}
		}
		context.put("cats", cats);
		context.put("balance", catBalance);
		context.put("totalCreditRequired", totalCreditRequired);
		context.put("totalCreditEarned", totalCreditEarned);
		context.put("totalCreditBalance", totalCreditBalance);

		
	}
	
	protected Map<String, SubjectCategory> subjectCategoryMapping(DbPersistence db, Program program, Session intake, LearningCentre centre) throws Exception {
		
		allSubjects = new ArrayList<SubjectVO>();
		
		ProgramStructure ps = (ProgramStructure) db.get("select p from ProgramStructure p " +
				"where p.session.id = '" + intake.getId() + "' and p.program.id = '" + program.getId() + "'" +
				" and p.learningCenter.id = '" + centre.getId() + "'");
		
		
		Set<SubjectPeriod> sps = ps.getSubjectPeriod();
		Map<String, SubjectCategory> cats = new HashMap<String, SubjectCategory>();
		for ( SubjectPeriod sp : sps ) {
			Set<SubjectReg> regs = sp.getSubjectRegs();
			
			for ( SubjectReg r : regs ) {
				if ( r.getCategory() != null && r.getSubject() != null ) {
					allSubjects.add(new SubjectVO(r.getSubject(), r.getCategory()));
					cats.put(r.getSubject().getId(), r.getCategory());
				}
			}
		}
		return cats;
	}
	
	protected static void main(String[] args) throws Exception {
		DbPersistence db = new DbPersistence();
		String matricNo = "E60109080008";
		String sql = "";
		sql = "select s from Student s where s.matricNo = '" + matricNo + "'";
		Student student = (Student) db.get(sql);
		Program program = student.getProgram();
		Session intake = student.getIntake();
		LearningCentre centre = student.getLearningCenter();
		Hashtable param = new Hashtable();
		param.put("date1", intake.getStartDate());
		param.put("date2", new Date());
		param.put("path_no", student.getProgram().getLevel().getPathNo());
		sql = "select s from Session s where s.startDate between :date1 and :date2 and s.pathNo = :path_no";
		List<Session> sessions = db.list(sql, param);
		
		
	}

}
