package educate.sis.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpSession;

import educate.db.DbPersistence;
import educate.sis.struct.entity.CategoryCount;
import educate.sis.struct.entity.GraduationRequirement;
import educate.sis.struct.entity.GraduationSubjectRequirement;
import educate.sis.struct.entity.LearningCentre;
import educate.sis.struct.entity.Period;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.ProgramStructure;
import educate.sis.struct.entity.Session;
import educate.sis.struct.entity.Subject;
import educate.sis.struct.entity.SubjectCategory;
import educate.sis.struct.entity.SubjectPeriod;
import educate.sis.struct.entity.SubjectReg;
import lebah.portal.action.AjaxModule;

public class GraduationRequirementModule extends AjaxModule {
	
	String path = "apps/util/graduation_setup/";
	private String vm = "default.vm";
	HttpSession session;
	DbPersistence db = new DbPersistence();
	Hashtable data = null;
	

	
	public String doAction() throws Exception {
		context.put("path", path.substring(0, path.length()-1));
		context.put("_formName", formName);
		session = request.getSession();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		context.put("dateFormat", dateFormat);
		context.put("numFormat", new DecimalFormat("#,###,###.00"));
		context.put("programUtil", new ProgramUtil());
		String command = request.getParameter("command");

		System.out.println(command);
		
		if ( null == command || "".equals(command)) listPrograms();
		else if ( "list_intakes".equals(command)) listIntakes();
		else if ( "view".equals(command)) viewGraduationRequirement();
		else if ( "graduation_requirement".equals(command)) graduationRequirement();
		else if ( "save".equals(command)) save();
		return vm;
	}


	private void save() throws Exception {
		vm = path + "graduation.vm";
		String programId = request.getParameter("program_id");
		String intakeId = request.getParameter("intake_id");
		Program program = (Program) db.find(Program.class, programId);
		Session intake = (Session) db.find(Session.class, intakeId);
		String type = request.getParameter("type") != null ? request.getParameter("type") : "G";
		if ( "".equals(type) ) type = "G";		
		context.put("program", program);
		context.put("intake", intake);
		context.put("type", type);
		
		//category subject
		String sql = "select c from SubjectCategory c";
		List<SubjectCategory> categories = db.list(sql);
		context.put("categories", categories);
		session.setAttribute("categories", categories);
		
		double cgpa = 0.0;
		String minCGPA = request.getParameter("min_cgpa");
		try {
			cgpa = Double.parseDouble(minCGPA);
		} catch ( Exception e ) {}

		Hashtable param = new Hashtable();
		param.put("program", program);
		param.put("intake", intake);
		param.put("type", type);
		sql = "select g from GraduationRequirement g where g.program = :program and g.intake = :intake and g.type = :type";
		List<GraduationRequirement> list = db.list(sql, param);
		GraduationRequirement gr = null;
		if ( list.size() > 0 ) {
			gr = (GraduationRequirement) list.get(0);
			
			db.begin();
			for ( SubjectCategory c : categories ) {
				String count = request.getParameter("count_" + c.getId());
				CategoryCount categoryCount = new CategoryCount();
				categoryCount.setCategory(c);
				try {
					categoryCount.setCount(Integer.parseInt(count));
				} catch ( Exception e ) {
					categoryCount.setCount(0);
				}
				gr.addCategoryCount(categoryCount);
			}
			gr.setMinCGPA(cgpa);
			db.commit();
			context.put("grad", gr);
			
		} else {
			gr = new GraduationRequirement();
			gr.setProgram(program);
			gr.setIntake(intake);
			gr.setType(type);
			
			for ( SubjectCategory c : categories ) {
				String count = request.getParameter("count_" + c.getId());
				CategoryCount categoryCount = new CategoryCount();
				categoryCount.setCategory(c);
				try {
					categoryCount.setCount(Integer.parseInt(count));
				} catch ( Exception e ) {
					categoryCount.setCount(0);
				}
				gr.addCategoryCount(categoryCount);
			}
			gr.setMinCGPA(cgpa);
			db.begin();
			db.persist(gr);
			db.commit();
			context.put("grad", gr);
		}
		if ( gr != null ) {
			Set<CategoryCount> counts = gr.getCategoryCounts();
			Hashtable cats = new Hashtable();
			for ( CategoryCount c : counts ) {
				cats.put(c.getCategory().getId(), c.getCount());
			}
			context.put("cats", cats);
		}
		
		//saving subjects requirement
		//String centreCode = request.getParameter("centre_code");
		
		LearningCentre c = (LearningCentre) db.get("select c from LearningCentre c where c.mainCampus = 1");
		
		saveSubjectsRequirement(gr, program, intake, c);
		listSubjects(program, intake, c);
		
		Set<GraduationSubjectRequirement> subjects = gr.getSubjects();
		Hashtable subjectRequirement = new Hashtable();
		context.put("subject_requirements", subjectRequirement);
		for ( GraduationSubjectRequirement s : subjects ) {
			subjectRequirement.put(s.getSubject().getId(), s.getMark());
		}
		
	}

	private void viewGraduationRequirement() {
		vm = path + "graduation_view.vm";
		String programId = request.getParameter("program_id");
		String intakeId = request.getParameter("intake_id");
		
		String sql = "";
		String type = request.getParameter("type") != null ? request.getParameter("type") : "G";
		if ( "".equals(type) ) type = "G";
		Program program = (Program) db.find(Program.class, programId);
		Session intake = (Session) db.find(Session.class, intakeId);
		
		context.put("program", program);
		context.put("intake", intake);
	
		context.put("type", type);
		Hashtable param = new Hashtable();
		param.put("program", program);
		param.put("intake", intake);
		param.put("type", type);
		
		sql = "select g from GraduationRequirement g where g.program = :program and g.intake = :intake and g.type = :type";
		List<GraduationRequirement> list = db.list(sql, param);
		GraduationRequirement grad = null;
		
		if ( list.size() > 0 ) {
			grad = list.get(0);
			context.put("grad", grad);
			context.put("categories", grad.getCategoryCounts());
			
			Set<CategoryCount> cats = grad.getCategoryCounts();
			int total = 0;
			for ( CategoryCount cat : cats ) {
				total += cat.getCount();
			}
			
			if ( total > 0 ) context.put("has_credit_hours", true);
			else context.remove("has_credit_hours");
			
			if ( grad.getSubjects() != null ) {
				Set<GraduationSubjectRequirement> subjects = grad.getSubjects();
				
				System.out.println("grad req subjects = " + subjects.size());
				
				context.put("subjects", subjects);
			}
			else {
				context.remove("subjects");
			}
		}
		else {
			context.remove("grad");
			context.remove("subjects");
		}
		


		
	}

	private void graduationRequirement() {
		
		String programId = request.getParameter("program_id");
		String intakeId = request.getParameter("intake_id");
		Program program = (Program) db.find(Program.class, programId);
		Session intake = (Session) db.find(Session.class, intakeId);
		String type = request.getParameter("type") != null ? request.getParameter("type") : "G";
		if ( "".equals(type) ) type = "G";		
		context.put("program", program);
		context.put("intake", intake);
		context.put("type", type);
		
		//category subject
		String sql = "select c from SubjectCategory c";
		List<SubjectCategory> categories = db.list(sql);
		context.put("categories", categories);
		session.setAttribute("categories", categories);
		
		Hashtable param = new Hashtable();
		param.put("program", program);
		param.put("intake", intake);
		param.put("type", type);
		sql = "select g from GraduationRequirement g where g.program = :program and g.intake = :intake and g.type = :type";
		List<GraduationRequirement> list = db.list(sql, param);
		if ( list.size() > 0 ) {
			GraduationRequirement g = list.get(0);
			context.put("grad", g);
			Set<CategoryCount> counts = g.getCategoryCounts();
			Hashtable cats = new Hashtable();
			for ( CategoryCount c : counts ) {
				cats.put(c.getCategory().getId(), c.getCount());
			}
			context.put("cats", cats);
			
			Set<GraduationSubjectRequirement> subjects = g.getSubjects();
			
			System.out.println("graduation req subjects = " + subjects.size());
			
			Hashtable subjectRequirement = new Hashtable();
			context.put("subject_requirements", subjectRequirement);
			for ( GraduationSubjectRequirement s : subjects ) {
				if ( s.getSubject() != null && s.getMark() != null ) subjectRequirement.put(s.getSubject().getId(), s.getMark());
			}
		}
		else {
			context.remove("grad");
			context.remove("subject_requirements");
		}
		
		LearningCentre c = (LearningCentre) db.get("select c from LearningCentre c where c.mainCampus = 1");
		try {
			listSubjects(program, intake, c);
			context.remove("programStructureNotSet");
		} catch ( Exception e ) {
			context.put("programStructureNotSet", true);
		}
		
		vm = path + "graduation.vm";
		
	}


	private void listSubjects(Program program, Session intake, LearningCentre centre) throws Exception {
		String sql;
		ProgramUtil pu = new ProgramUtil();
		context.put("pu", pu);
		
		Hashtable p = new Hashtable();
		p.put("program", program);
		p.put("session", intake);
		p.put("centre", centre);
		sql = "select p from ProgramStructure p where p.program = :program " +
				"and p.session = :session and p.learningCenter = :centre";
		List<ProgramStructure> psList = db.list(sql, p);
		if ( psList.size() == 0 ) throw new Exception("Program Structure was not set.");
		ProgramStructure ps = null;
		if ( psList.size() > 0 ) ps = (ProgramStructure) db.list(sql, p).get(0);
		context.remove("subjects");
		if ( ps != null ) {
			List<Period> periods = ps.getProgram().getPeriodScheme().getFunctionalPeriodList();
			context.put("periods", periods);
			Set<Subject> subjects = new HashSet<Subject>();
			context.put("subjects", subjects);
			for ( Period period : periods ) {
				SubjectPeriod subjectPeriod = pu.getSubjectPeriod(ps, period.getId());
				if ( subjectPeriod != null ) {
					Set<SubjectReg> regs = subjectPeriod.getSubjectRegs();
					for ( SubjectReg r : regs ) {
						subjects.add(r.getSubject());
					}
				}
			}

		}
		
	}
	
	private void saveSubjectsRequirement(GraduationRequirement gr, Program program, Session intake, LearningCentre centre) throws Exception {
		String sql;
		ProgramUtil pu = new ProgramUtil();
		context.put("pu", pu);
		
		Hashtable p = new Hashtable();
		p.put("program", program);
		p.put("session", intake);
		p.put("centre", centre);
		sql = "select p from ProgramStructure p where p.program = :program " +
				"and p.session = :session and p.learningCenter = :centre";
		ProgramStructure ps = (ProgramStructure) db.list(sql, p).get(0);
		if ( ps != null ) {
			List<Period> periods = ps.getProgram().getPeriodScheme().getFunctionalPeriodList();
			//context.put("periods", periods);
			Hashtable<String, Set<SubjectReg>> subjectReg = new Hashtable<String, Set<SubjectReg>>();
			//context.put("subjectReg", subjectReg);
			
			db.begin();
			for ( Period period : periods ) {
				//System.out.println(period.getName());
				SubjectPeriod subjectPeriod = pu.getSubjectPeriod(ps, period.getId());
				if ( subjectPeriod != null ) {
					//subjectReg.put(period.getId(), subjectPeriod.getSubjectRegs());
					Set<SubjectReg> subjects = subjectPeriod.getSubjectRegs();
					for (SubjectReg s : subjects ) {
						//if ( "CORE".equals(s.getCategory().getName())) {
							
							String mark = request.getParameter("mark_" + s.getSubject().getId());
							
							gr.addSubjectRequirement(s.getSubject(), mark);
						//}
					}
				}
			}
			db.commit();
		
		}
	}	


	private void listPrograms() {
		//vm = path + "list_programs.vm";
		vm = path + "start.vm";
		context.remove("intakes");
		params();
	}
	private void params() {
		List<Program> programs = db.list("SELECT a from Program a order by a.code");
		context.put("programs",programs);
		session.setAttribute("programs", programs);
		
		List<LearningCentre> centres = db.list("select c from LearningCentre c order by c.code");
		context.put("centres", centres);
		session.setAttribute("centres", centres);
		
	}
	
	private void listIntakes() {
		vm = path + "list_programs.vm";
		String programId = request.getParameter("program_id");
		if ( "".equals(programId)) {
			listPrograms();
			return;
		}
		context.put("program_id", programId);
		Program program = (Program) db.find(Program.class, programId);
		List<Session> intakes = db.list("select s from Session s where s.pathNo = " + program.getLevel().getPathNo() + " order by s.startDate");
		context.put("intakes", intakes);
		session.setAttribute("intakes", intakes);
		
		Hashtable param = new Hashtable();
		param.put("date", new Date());
		param.put("path_no", program.getLevel().getPathNo());
		List list = db.list("select s from Session s where :date between s.startDate and s.endDate and s.pathNo = :path_no", param);
		if ( list.size() > 0 ) {
			Session currentSession = (Session) list.get(0);
			context.put("current_session", currentSession);
			context.put("intake_id", currentSession.getId());
		}
		else {
			context.remove("current_session");
			context.remove("intake_id");
		}
		
		params();
		
	}

}
