package educate.sis.module;

import java.io.FileOutputStream;
import java.io.PrintStream;
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
import educate.sis.struct.entity.PeriodScheme;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.ProgramStructure;
import educate.sis.struct.entity.Session;
import educate.sis.struct.entity.Subject;
import educate.sis.struct.entity.SubjectCategory;
import educate.sis.struct.entity.SubjectPeriod;
import educate.sis.struct.entity.SubjectReg;
import lebah.portal.action.AjaxModule;

public class ProgramStructureModule_Delete extends AjaxModule {
	private String className  = getClass().getName(); 
	String path = "apps/util/graduation/";
	private String vm = "default.vm";
	HttpSession session;
	DbPersistence db = new DbPersistence();
	Hashtable data = null;
	String userId = "";
	String logDate = "";
	String remoteAddr = "";
	
	
	static {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
			FileOutputStream os = new FileOutputStream("program_structure." + dateFormat.format(new Date()) + "." + lebah.db.UniqueID.get() + ".log.txt");
			PrintStream ps = new PrintStream(os);
			System.setErr(ps);
		} catch ( Exception e ) {}
	}
	

	@Override
	public String doAction() throws Exception {
		logDate = new SimpleDateFormat().format(new Date());
		remoteAddr = request.getRemoteAddr();
		
		userId = (String) request.getSession().getAttribute("_portal_login");
		context.put("_formName", formName);
		session = request.getSession();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		context.put("dateFormat", dateFormat);
		context.put("programUtil", new ProgramUtil());
		String command = request.getParameter("command");
		System.out.println("["+className+"] command = "+command);
		
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
		System.err.println("[" + logDate + ", " + remoteAddr + ", " + userId + "] - ADD SUBJECTS: select ALLOW RECURRING SUBJECTS");
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
		
		for ( Period period : periods ) {
			SubjectPeriod spCurrent = pu.getSubjectPeriod(psCurrent, period.getId());
			if ( spCurrent == null ) {
				db.begin();
				spCurrent = new SubjectPeriod();
				spCurrent.setPeriod(period);
				psCurrent.addSubjectPeriod(spCurrent);
				db.commit();
			} else {
				Set<SubjectReg> list = new HashSet<SubjectReg>();
				list.addAll(spCurrent.getSubjectRegs());
				if ( list != null ) {
					for ( SubjectReg s : list ) {
						db.begin();
						spCurrent.delete(s);
						db.remove(s);
						db.commit();
					}
				}
			}
		}

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
			} else if ( sp != null ) {
				
				//do logging
				System.err.println("[" + logDate + ", " + remoteAddr + ", " + userId + "] - ADD SUBJECTS: " + sp.getProgramStructure().getProgram().getCode() + " " + sp.getProgramStructure().getProgram().getName() + ", " + (sp.getPeriod() != null ? sp.getPeriod().getName() : "null"));
				for ( Subject s2 : subjects ) {
					System.err.println("[" + logDate + ", " + remoteAddr + ", " + userId + "] adding subject " + s2.getCode() + " " + s2.getName());
				}
				
				if ( sp.getSubjectRegs() != null && sp.getSubjectRegs().size() > 0 ) {
					for ( SubjectReg subjectReg : sp.getSubjectRegs() ) {
						Subject s2 = subjectReg.getSubject();
						if ( subjects.contains(s2) ) {
							System.err.println("[" + logDate + ", " + remoteAddr + ", " + userId + "] is purposely adding DUPLICATE SUBJECT of " + s2.getCode() + " " + s2.getName());
						}
					}
				}
				
				
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
		String[] subjectRegIds = request.getParameterValues("remove_ids");
//		String[] subjectIds = request.getParameterValues("remove_ids");
		if (subjectRegIds != null) {
			
			String periodId = request.getParameter("period_id");
			String structure_id = request.getParameter("structure_id");
			ProgramStructure programStructure = (ProgramStructure) db.find(ProgramStructure.class, structure_id);
			if (programStructure == null) {
				throw new Exception("["+className+".removeSubject] ProgramStructure entity not found.");
			}
			
			String pql = "SELECT e FROM SubjectPeriod e " +
					"WHERE e.period.id = ?1 AND e.programStructure.id = ?2";
			javax.persistence.EntityManager em = db.getEntityManager();
			javax.persistence.Query query = em.createQuery(pql);
			query.setParameter(1, periodId);
			query.setParameter(2, programStructure.getId());
			SubjectPeriod subjectPeriod = null;
			try {
				subjectPeriod = (SubjectPeriod) query.getSingleResult();
			} catch (javax.persistence.NonUniqueResultException ex) {
				throw new Exception("["+className+".removeSubject] More than one SubjectPeriod entity found. Unable to determine correct entity.");
			} catch (javax.persistence.NoResultException ex) {
				throw new Exception("["+className+".removeSubject] SubjectPeriod entity not found.");
			}
			
			//DO LOGGING
			System.err.println("[" + logDate + ", " + remoteAddr + ", " + userId + "] - REMOVE SUBJECTS: " + subjectPeriod.getProgramStructure().getProgram().getCode() + " " + subjectPeriod.getProgramStructure().getProgram().getName() + ", " + subjectPeriod.getPeriod().getName());
			
			//List<SubjectReg> subjectRegs = new java.util.ArrayList<SubjectReg>();
			for (String subjectRegId : subjectRegIds) {
				SubjectReg subjectReg = (SubjectReg) db.find(SubjectReg.class, subjectRegId);
				if (subjectReg != null) {
					//DO LOGGING
					System.err.println("[" + logDate + ", " + remoteAddr + ", " + userId + "] : removing subject " + subjectReg.getSubject().getCode() + " " + subjectReg.getSubject().getName());
					db.begin();
					subjectPeriod.delete(subjectReg);
					db.remove(subjectReg);
					//subjectRegs.add(subjectReg);
					db.commit();
					
				}
			}
//		if ( subjectIds != null ) {
//			Set<Subject> subjects = new HashSet<Subject>();
//			for ( String id : subjectIds ) {
//				System.out.println("["+className+".removeSubjects] subjectId = "+id);
//				Subject subject = (Subject) db.find(Subject.class, id);
//				if ( subject != null ) {
//					subjects.add(subject);
//				}
//			}
			
//			String periodId = request.getParameter("period_id");
//			String structure_id = request.getParameter("structure_id");
//			ProgramStructure programStructure = (ProgramStructure) db.find(ProgramStructure.class, structure_id);
//			SubjectPeriod sp = new ProgramUtil().getSubjectPeriod(programStructure, periodId);
//			if ( sp != null ) {
//				db.begin();
// ====================				
//				for ( Subject s : subjects ) {
//					sp.delete(s);
//				}
// ====================
//				for (SubjectReg subjectReg : subjectRegs) {
//					sp.delete(subjectReg);
//				}
//				db.commit();
//			}
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
//	Add by Shaiful Nizam, 2010-03-05.
				if (ps != null) {
					String exclude = ps.getId();
					query_NOT_IN += exclude + "', '";
				}
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
		
		//System.out.println("["+className+".getStructureByCentre] programId = "+programId);
		//System.out.println("["+className+".getStructureByCentre] intakeId = "+intakeId);
		//System.out.println("["+className+".getStructureByCentre] centreId = "+centreId);
		
		Program program = (Program) db.find(Program.class, programId);
		Session intake = (Session) db.find(Session.class, intakeId);
		LearningCentre centre = (LearningCentre) db.find(LearningCentre.class, centreId);
		
		System.err.println("[" + logDate + ", " + remoteAddr + "] " + userId + ": open PROGRAM STRUCTURE for " + program.getCode() + " " + program.getName() + ", " + intake.getName() + ", " + centre.getName());
		
		context.put("program", program);
		context.put("intake", intake);
		context.put("centre", centre);
		
		String sql = "";
		sql = "select p from ProgramStructure p where p.program.id = '" + programId + "' " +
				"and p.learningCenter.id = '" + centreId + "' " +
						"and p.session.id = '" + intakeId + "'";
		ProgramStructure ps = (ProgramStructure) db.get(sql);
		
		//System.out.println("["+className+".getStructureByCentre] programStructure Id = "+ps.getId());
		
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
		
		//System.out.println("["+className+".getStructureByCentre] program = "+ps.getProgram().getCode());
		
		PeriodScheme periodScheme = ps.getProgram().getPeriodScheme();
		//System.out.println("["+className+".getStructureByCentre] periodScheme Id = "+periodScheme.getId());
		
//		java.util.Set<SubjectPeriod> subjectPeriodList = ps.getSubjectPeriod();
//		System.out.println("["+className+".getStructureByCentre] num of SubjectPeriod = "+subjectPeriodList.size());
//		
//		List<Period> periods = new java.util.ArrayList<Period>();
//		for (SubjectPeriod subjectPeriod : subjectPeriodList) {
//			Period period = subjectPeriod.getPeriod(); 
//			System.out.println("["+className+".getStructureByCentre] period = "+period.getId());
//			periods.add(period);
//		}
		
		List<Period> periods = periodScheme.getFunctionalPeriodList();
		//System.out.println("["+className+".getStructureByCentre] num of periods = "+periods.size());
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
	
//	/**
//	 * This method is to check if the subject in the program structure for a particular period
//	 * has been registered by student. If there are no registration it will return true else
//	 * false.
//	 * @param periodId
//	 * @param subject
//	 * @return true if subject has no registration else false.
//	 * @throws Exception
//	 */
//	private boolean isSubjectNotRegistered(String periodId, Subject subject) throws Exception {
//		boolean flag = true;
//		String pql = "SELECT DISTINCT e1 FROM StudentStatus e1, IN(e1.studentSubjects) e2 " +
//				"WHERE e1.period.id = ?1 AND e2.subject.id = ?2 AND e1.student.fakeStudent IS NULL";
//		javax.persistence.EntityManager em = db.getEntityManager();
//		javax.persistence.Query query = em.createQuery(pql);
//		query.setParameter(1, periodId);
//		query.setParameter(2, subject.getId());
//		try {
//			List<StudentStatus> resultList = query.getResultList();
//			for (StudentStatus studentStatus : resultList) {
//				educate.enrollment.entity.Student student = studentStatus.getStudent();
//				System.out.println("["+className+".isSubjectRegistered] student = "+student.getMatricNo()+" - "+student.getBiodata().getName());
//			}
//			flag = false;
//		} catch (javax.persistence.NoResultException ex) {
//			flag = true;
//		}
//		return flag;
//	}
}
