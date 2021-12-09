package educate.sis.module;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpSession;

import educate.db.DbPersistence;
import educate.sis.struct.entity.Faculty;
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

public class SubjectRegistrationStructureModule extends AjaxModule {
	
	String path = "apps/util/program_structure/";
	private String vm = "default.vm";
	HttpSession session;
	DbPersistence db = new DbPersistence();
	Hashtable data = null;

	
	public String doAction() throws Exception {
		context.put("_formName", formName);
		session = request.getSession();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		context.put("dateFormat", dateFormat);
		context.put("programUtil", new ProgramUtil());
		String command = request.getParameter("command");
		System.out.println(command);
		if ( null == command || "".equals(command)) listPrograms();
		else if ( "list_intakes".equals(command)) listIntakes();
		else if ( "get_structure".equals(command)) getStructure();
		else if ( "get_structure_by_centre".equals(command)) getStructureByCentre();
		else if ( "create_structure_by_centre".equals(command)) createStructureByCentre();
		else if ( "add_subject".equals(command)) addSubject();
		else if ( "remove_subjects".equals(command)) removeSubjects();
		else if ( "save_subjects".equals(command)) saveSubjects();
		else if ( "add_subjects".equals(command)) addSubjects();
		else if ( "select_period".equals(command)) selectPeriod();
		else if ( "copy_from_centre".equals(command)) copyFromCentre();
		else if ( "copy_from_intake".equals(command)) copyFromIntake();
		else if ( "filter_by_faculty".equals(command)) filterByFaculty();
		else if ( "order_by".equals(command)) orderBy();
		else if ( "prev_period".equals(command)) prevPeriod();
		else if ( "next_period".equals(command)) nextPeriod();
		else if ( "allow_recurring".equals(command)) allowRecurring();
		
		return vm;
	}

	private void allowRecurring() {
		addSubject();
	}

	private void nextPeriod() {
		String current = request.getParameter("curr_period_id");
		String programId = request.getParameter("program_id");
		Program program = (Program) db.find(Program.class, programId);
		List<Period> periods = program.getPeriodScheme().getFunctionalPeriodList();
		boolean getnext = false;
		Period nextPeriod = null;
		for ( Period p : periods ) {
			if ( getnext ) {
				nextPeriod = p;
				break;
			}
			if ( p.getId().equals(current)) {
				getnext = true;
			}
		}
		addSubject(nextPeriod.getId());
	}

	private void prevPeriod() {
		String current = request.getParameter("curr_period_id");
		String programId = request.getParameter("program_id");
		Program program = (Program) db.find(Program.class, programId);
		List<Period> periods = program.getPeriodScheme().getFunctionalPeriodList();
		Period prevPeriod = null;
		for ( Period p : periods ) {
			if ( p.getId().equals(current)) {
				break;
			}
			prevPeriod = p;
		}
		addSubject(prevPeriod.getId());
	}

	private void saveSubjects() throws Exception {
		vm = path + "subjects.vm";
		
		String structure_id = request.getParameter("structure_id");
		String programId = request.getParameter("program_id");
		String intakeId = request.getParameter("intake_id");
		String centreId = request.getParameter("centre_id");
		String periodId = request.getParameter("period_id");
		
		ProgramStructure programStructure = (ProgramStructure) db.find(ProgramStructure.class, structure_id);
		Program program = (Program) db.find(Program.class, programId);
		Session intake = (Session) db.find(Session.class, intakeId);
		LearningCentre centre = (LearningCentre) db.find(LearningCentre.class, centreId);
		Period period = (Period) db.find(Period.class, periodId);
		
		context.put("programStructure", programStructure);
		context.put("program", program);
		context.put("intake", intake);
		context.put("centre", centre);
		context.put("period", period);
		
		//List of Subjects
		
		List<Subject> subjects = listOfSubjects(programStructure);
		context.put("subjects", subjects);
		
		//List of Periods
		List<Period> periods = programStructure.getProgram().getPeriodScheme().getFunctionalPeriodList();
		context.put("periods", periods);
		
		//is current period first or last?
		int size = periods.size();
		if ( period.getId().equals(periods.get(0).getId())) context.put("position", "first");
		else if ( period.getId().equals(periods.get(size-1).getId())) context.put("position", "last");
		else context.put("position", "");
		
		//List of Schools
		List<Faculty> faculties = db.list("select f from Faculty f order by f.code");
		context.put("faculties", faculties);
		
		//List of Categories
		List<SubjectCategory> categories = db.list("select c from SubjectCategory c order by c.code");
		context.put("categories", categories);
		
		//put categories into hashtable for easy find
		Hashtable<String, SubjectCategory> category = new Hashtable<String, SubjectCategory>();
		for ( SubjectCategory sc : categories ) {
			category.put(sc.getId(), sc);
		}
		
		ProgramUtil pu = new ProgramUtil();
		Set<SubjectReg> regs = pu.getSubjectRegs(programStructure, periodId);
		
		db.begin();
		for ( SubjectReg reg : regs ) {
			String catId = request.getParameter(reg.getId());
			SubjectCategory sc = category.get(catId);
			reg.setCategory(sc);
		}
		db.commit();
	
	}

	private void orderBy() {
		addSubject();
		
	}

	private void filterByFaculty() {
		addSubject();
		
	}

	private void copyFromIntake() throws Exception {
		String otherIntakeId = request.getParameter("other_intake_id");
		String programId = request.getParameter("program_id");
		String intakeId = request.getParameter("intake_id");
		String centreId = request.getParameter("centre_id");
		String sql = "";
		sql = "select p from ProgramStructure p where p.program.id = '" + programId + "' " +
				"and p.learningCenter.id = '" + centreId + "' " +
						"and p.session.id = '" + otherIntakeId + "'";
		ProgramStructure psOther = (ProgramStructure) db.get(sql);
		
		sql = "select p from ProgramStructure p where p.program.id = '" + programId + "' " +
		"and p.learningCenter.id = '" + centreId + "' " +
				"and p.session.id = '" + intakeId + "'";
		ProgramStructure psCurrent = (ProgramStructure) db.get(sql);
		if ( psCurrent == null ) {
			Program program = (Program) db.find(Program.class, programId);
			Session intake = (Session) db.find(Session.class, intakeId);
			LearningCentre centre = (LearningCentre) db.find(LearningCentre.class, centreId);
			
			psCurrent = new ProgramStructure();
			psCurrent.setLearningCenter(centre);
			psCurrent.setProgram(program);
			psCurrent.setSession(intake);
			
			db.begin();
			db.persist(psCurrent);
			db.commit();
		}
		
		//do copy
		copyProgramStructure(psOther, psCurrent);
		
		getStructureByCentre();
	}

	private void copyFromCentre() throws Exception {
		String otherCentreId = request.getParameter("other_centre_id");
		String programId = request.getParameter("program_id");
		String intakeId = request.getParameter("intake_id");
		String centreId = request.getParameter("centre_id");
		String sql = "";
		sql = "select p from ProgramStructure p where p.program.id = '" + programId + "' " +
				"and p.learningCenter.id = '" + otherCentreId + "' " +
						"and p.session.id = '" + intakeId + "'";
		ProgramStructure psOther = (ProgramStructure) db.get(sql);
		
		sql = "select p from ProgramStructure p where p.program.id = '" + programId + "' " +
		"and p.learningCenter.id = '" + centreId + "' " +
				"and p.session.id = '" + intakeId + "'";
		ProgramStructure psCurrent = (ProgramStructure) db.get(sql);
		if ( psCurrent == null ) {
			Program program = (Program) db.find(Program.class, programId);
			Session intake = (Session) db.find(Session.class, intakeId);
			LearningCentre centre = (LearningCentre) db.find(LearningCentre.class, centreId);
			
			psCurrent = new ProgramStructure();
			psCurrent.setLearningCenter(centre);
			psCurrent.setProgram(program);
			psCurrent.setSession(intake);
			
			db.begin();
			db.persist(psCurrent);
			db.commit();
		}
		
		//do copy
		copyProgramStructure(psOther, psCurrent);
		
		getStructureByCentre();
	}

	private void copyProgramStructure(ProgramStructure psOther, ProgramStructure psCurrent) throws Exception {
		ProgramUtil pu = new ProgramUtil();
		List<Period> periods = psCurrent.getProgram().getPeriodScheme().getFunctionalPeriodList();
		db.begin();
		for ( Period period : periods ) {
			SubjectPeriod spCurrent = pu.getSubjectPeriod(psCurrent, period.getId());
			if ( spCurrent == null ) {
				spCurrent = new SubjectPeriod();
				spCurrent.setPeriod(period);
				psCurrent.addSubjectPeriod(spCurrent);
			} else {
				Set<Subject> list = new HashSet<Subject>();
				list.addAll(spCurrent.getSubjects());
				if ( list != null ) {
					for ( Subject s : list ) {
						spCurrent.delete(s);
					}
				}
			}
		}
		db.commit();
		//now do copy
		
		if ( psOther != null ) {
			db.begin();
			for ( Period period : periods ) {
				SubjectPeriod spCurrent = pu.getSubjectPeriod(psCurrent, period.getId());
				SubjectPeriod spOther = pu.getSubjectPeriod(psOther, period.getId());
				if ( spOther != null ) {
					Set<SubjectReg> list = new HashSet<SubjectReg>();
					if ( spOther.getSubjects() != null ) {
						Set<SubjectReg> regs = spOther.getSubjectRegs();
						for ( SubjectReg r : regs ) {
							SubjectReg n = new SubjectReg();
							n.setCategory(r.getCategory());
							n.setSubject(r.getSubject());
							n.setSubjectPeriod(spCurrent);
							list.add(n);
						}
						spCurrent.setSubjectRegs(list);
					}
				}
			}
			db.commit();
		}
	}

	private void selectPeriod() {
		String periodId = request.getParameter("selected_period_id");
		addSubject(periodId);
	}

	private void addSubjects() throws Exception {
		String[] subjectIds = request.getParameterValues("subject_ids");
		if ( subjectIds != null ) {
			Set<Subject> subjects = new HashSet<Subject>();
			for ( String id : subjectIds ) {
				Subject subject = (Subject) db.find(Subject.class, id);
				if ( subject != null ) {
					subjects.add(subject);
				}
			}
			String periodId = request.getParameter("period_id");
			String structure_id = request.getParameter("structure_id");
			ProgramStructure programStructure = (ProgramStructure) db.find(ProgramStructure.class, structure_id);
			SubjectPeriod sp = new ProgramUtil().getSubjectPeriod(programStructure, periodId);
			if ( sp == null ) {
				sp = new SubjectPeriod();
				Period period = (Period) db.find(Period.class, periodId);
				sp.setPeriod(period);
				sp.setProgramStructure(programStructure);
				for ( Subject s : subjects ) {
					sp.addSubject(s);
				}
				db.begin();
				programStructure.addSubjectPeriod(sp);
				db.commit();
			}else if ( sp != null ) {
				db.begin();
				for ( Subject s : subjects ) {
					sp.addSubject(s);
				}
				db.commit();
			}
		}
		addSubject();
	}

	private void removeSubjects() throws Exception {
		String[] subjectIds = request.getParameterValues("remove_ids");
		if ( subjectIds != null ) {
			Set<Subject> subjects = new HashSet<Subject>();
			for ( String id : subjectIds ) {
				Subject subject = (Subject) db.find(Subject.class, id);
				if ( subject != null ) {
					subjects.add(subject);
				}
			}
			String periodId = request.getParameter("period_id");
			String structure_id = request.getParameter("structure_id");
			ProgramStructure programStructure = (ProgramStructure) db.find(ProgramStructure.class, structure_id);
			SubjectPeriod sp = new ProgramUtil().getSubjectPeriod(programStructure, periodId);
			if ( sp != null ) {
				db.begin();
				for ( Subject s : subjects ) {
					sp.delete(s);
				}
				db.commit();
			}
		}
		
		addSubject();
	}
	
	private void addSubject() {
		addSubject("");
	}

	private void addSubject(String selectedPeriodId) {
		vm = path + "subjects.vm";
		
		String structure_id = request.getParameter("structure_id");
		String programId = request.getParameter("program_id");
		String intakeId = request.getParameter("intake_id");
		String centreId = request.getParameter("centre_id");
		String periodId = !"".equals(selectedPeriodId) ? selectedPeriodId : request.getParameter("period_id");
		
		ProgramStructure programStructure = (ProgramStructure) db.find(ProgramStructure.class, structure_id);
		Program program = (Program) db.find(Program.class, programId);
		Session intake = (Session) db.find(Session.class, intakeId);
		LearningCentre centre = (LearningCentre) db.find(LearningCentre.class, centreId);
		Period period = (Period) db.find(Period.class, periodId);
		
		context.put("programStructure", programStructure);
		context.put("program", program);
		context.put("intake", intake);
		context.put("centre", centre);
		context.put("period", period);
		
		//List of Subjects
		
		List<Subject> subjects = listOfSubjects(programStructure);
		context.put("subjects", subjects);
		
		//List of Periods
		List<Period> periods = programStructure.getProgram().getPeriodScheme().getFunctionalPeriodList();
		context.put("periods", periods);
		
		//is current period first or last?
		int size = periods.size();
		if ( period.getId().equals(periods.get(0).getId())) context.put("position", "first");
		else if ( period.getId().equals(periods.get(size-1).getId())) context.put("position", "last");
		else context.put("position", "");
		
		
		
		//List of Schools
		List<Faculty> faculties = db.list("select f from Faculty f order by f.code");
		context.put("faculties", faculties);
		
		//List of Categories
		List<SubjectCategory> categories = db.list("select c from SubjectCategory c order by c.code");
		context.put("categories", categories);
		
		
	}

	private List<Subject> listOfSubjects(ProgramStructure programStructure) {
		
		String allowRecurring = request.getParameter("allow_recurring") != null ? request.getParameter("allow_recurring") : "no";
		context.put("allow_recurring", allowRecurring);

		String facultyId = request.getParameter("faculty_id") != null ? request.getParameter("faculty_id") : "";
		String orderBy = request.getParameter("order_by") != null ? request.getParameter("order_by") : "";
		if ( "".equals(orderBy)) {
			orderBy = request.getParameter("ordered_by") != null ? request.getParameter("ordered_by") : "";
		}
		context.put("faculty_id", facultyId);
		context.put("order_by", orderBy);
		ProgramUtil pu = new ProgramUtil();
		Set<Subject> list = pu.getSubjects(programStructure);
		String sql = "";
		if ( list.size() > 0 && "no".equals(allowRecurring)) {
			String query_NOT_IN = "('";
			for ( Subject ps : list ) {
				String exclude = ps.getId();
				query_NOT_IN += exclude + "', '";	
			}
			query_NOT_IN = query_NOT_IN.substring(0, query_NOT_IN.length()-3) + ")";
			sql = "select s from Subject s where ";
			if ( !"".equals(facultyId))
				sql += "s.faculty.id = '" + facultyId + "' and s.id NOT IN " +  query_NOT_IN + "";
			else
				sql += "s.id NOT IN " +  query_NOT_IN + "";
			if ( "name".equals(orderBy))
				sql += " order by s.name";
			else
				sql += " order by s.code";
		}
		else {
			sql = "select s from Subject s ";
			if ( !"".equals(facultyId))
				sql += "where s.faculty.id = '" + facultyId + "'";
			if ( "name".equals(orderBy))
				sql += " order by s.name";
			else
				sql += " order by s.code";			
		}
		List<Subject> subjects = db.list(sql);
		return subjects;
	}

	private void createStructureByCentre() throws Exception {
		vm = path + "program_structure.vm";
		String programId = request.getParameter("program_id");
		String intakeId = request.getParameter("intake_id");
		String centreId = request.getParameter("centre_id");
		
		Program program = (Program) db.find(Program.class, programId);
		Session intake = (Session) db.find(Session.class, intakeId);
		LearningCentre centre = (LearningCentre) db.find(LearningCentre.class, centreId);
		
		context.put("program", program);
		context.put("intake", intake);
		context.put("centre", centre);
		
		ProgramStructure structure = new ProgramStructure();
		structure.setLearningCenter(centre);
		structure.setProgram(program);
		structure.setSession(intake);
		
		db.begin();
		db.persist(structure);
		db.commit();
		
		context.put("programStructure", structure);
		
		List<Period> periods = structure.getProgram().getPeriodScheme().getFunctionalPeriodList();
		context.put("periods", periods);
		
		List<LearningCentre> centres = db.list("SELECT a from LearningCentre a where a.id NOT IN ('" + centre.getId() + "')");
		context.put("centres", centres);
		
		Hashtable param = new Hashtable();
		param.put("path_no", program.getLevel().getPathNo());
		List intakes = db.list("select s from Session s where s.pathNo = :path_no order by s.startDate", param);
		context.put("intakes", intakes);
		
	}

	private void getStructureByCentre() throws Exception {
		vm = path + "program_structure.vm";
		String programId = request.getParameter("program_id");
		String intakeId = request.getParameter("intake_id");
		String centreId = request.getParameter("centre_id");
		
		Program program = (Program) db.find(Program.class, programId);
		Session intake = (Session) db.find(Session.class, intakeId);
		LearningCentre centre = (LearningCentre) db.find(LearningCentre.class, centreId);
		
		context.put("program", program);
		context.put("intake", intake);
		context.put("centre", centre);
		
		String sql = "";
		sql = "select p from ProgramStructure p where p.program.id = '" + programId + "' " +
				"and p.learningCenter.id = '" + centreId + "' " +
						"and p.session.id = '" + intakeId + "'";
		ProgramStructure ps = (ProgramStructure) db.get(sql);
		
		if ( ps == null ) {
			ps = new ProgramStructure();
			ps.setLearningCenter(centre);
			ps.setProgram(program);
			ps.setSession(intake);
			
			db.begin();
			db.persist(ps);
			db.commit();
		}
		
		context.put("programStructure", ps);
		
		List<Period> periods = ps.getProgram().getPeriodScheme().getFunctionalPeriodList();
		context.put("periods", periods);
		
		List<LearningCentre> centres = db.list("SELECT a from LearningCentre a where a.id NOT IN ('" + centre.getId() + "')");
		context.put("centres", centres);
		
		Hashtable param = new Hashtable();
		param.put("path_no", program.getLevel().getPathNo());
		List intakes = db.list("select s from Session s where s.pathNo = :path_no order by s.startDate", param);
		context.put("intakes", intakes);
		
		
	}

	private void getStructure() {
		vm = path + "list_program_structures.vm";
		String programId = request.getParameter("program_id");
		String intakeId = request.getParameter("intake_id");
		
		Program program = (Program) db.find(Program.class, programId);
		Session intake = (Session) db.find(Session.class, intakeId);
		
		context.put("program", program);
		context.put("intake", intake);
		
		if ( !"".equals(intakeId)) {
			List<LearningCentre> centres = db.list("SELECT a from LearningCentre a");
			context.put("centres", centres);
		}
		
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

	private void listPrograms() {
		vm = path + "list_programs.vm";
		context.remove("intakes");
		params();
	}
	
	
	private void params() {
		List<Program> programs = db.list("SELECT a from Program a order by a.code");
		context.put("programs",programs);
		session.setAttribute("programs", programs);
	}


}
