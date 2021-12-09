package educate.sis.finance.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpSession;

import educate.db.DbPersistence;
import educate.sis.finance.entity.FeeItem;
import educate.sis.finance.entity.FeePaymentType;
import educate.sis.finance.entity.FeeStructureItem;
import educate.sis.module.ProgramUtil;
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

public class ProgramFeeStructureModule  extends AjaxModule {
	
	String path = "apps/util/fee_structure/";
	private String vm = "default.vm";
	HttpSession session;
	DbPersistence db = new DbPersistence();
	List<FeeItem> fees;
	
	
	public String doAction() throws Exception {
		context.put("_formName", formName);
		session = request.getSession();
		context.put("dateFormat", new SimpleDateFormat("yyyy-MM-dd"));
		context.put("timeFormat", new SimpleDateFormat("hh:mm a"));	
		context.put("numFormat", new DecimalFormat("#,###,###.00"));	
		context.put("programUtil", new ProgramUtil());
		
		fees = db.list("select f from FeeItem f where f.feeType = 'SUBJECT' order by f.seq");
		context.put("fees", fees);
		
		String command = request.getParameter("command");
		System.out.println("command: " + command);
		if ( null == command || "".equals(command)) listPrograms();
		else if ( "list_intakes".equals(command)) listIntakes();
		else if ( "get_structure".equals(command)) getStructure();
		else if ( "get_structure_by_centre".equals(command)) getStructureByCentre();
		else if ( "add_subjects".equals(command)) addSubjects();
		else if ( "select_period".equals(command)) selectPeriod();
		else if ( "prev_period".equals(command)) prevPeriod();
		else if ( "next_period".equals(command)) nextPeriod();
		else if ( "save_fees".equals(command)) saveFees();
		return vm;
	}
	
	private void saveFees() throws Exception {
		
		
		String periodId = request.getParameter("period_id");
		Period period = (Period) db.find(Period.class, periodId);
		
		String programStructureId = request.getParameter("structure_id");
		ProgramStructure ps = (ProgramStructure) db.find(ProgramStructure.class, programStructureId);
		ProgramUtil pu = new ProgramUtil();

		Set<SubjectReg> regs = pu.getSubjectRegs(ps, periodId);

		for ( SubjectReg r : regs ) {
			for ( FeeItem fee : fees ) {
				
				String amount = request.getParameter(r.getId() + "_" + fee.getId());
				double amt = 0.0d;
				if ( amount != null ) {
					try {
						amt = Double.parseDouble(amount);
					} catch ( Exception e ) {}
				}
				
				db.begin();
				FeeStructureItem item = r.getFeeItem(fee.getId());
				if ( item == null ) {
					item = new FeeStructureItem();
					item.setFeeItem(fee);
					item.setAmount(amt);
					item.setPeriod(period);
					item.setFeePaymentType(FeePaymentType.PERIOD_BASED);
					r.addFeeItem(item);
				}
				else {
					item.setAmount(amt);
				}
				db.commit();
				
			}
		}
		
		addSubject();
		
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
	
	private void selectPeriod() {
		String periodId = request.getParameter("selected_period_id");
		addSubject(periodId);
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
