package educate.sis.module;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import educate.db.DbPersistence;
import educate.sis.struct.entity.LearningCentre;
import educate.sis.struct.entity.Period;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.ProgramStructure;
import educate.sis.struct.entity.Session;
import educate.sis.struct.entity.SubjectReg;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class SubjectRegistrationOfferingModule extends LebahModule {
	
	private DbPersistence db = new DbPersistence();
	private String path = "admission/subject_offering";

	@Override
	public String start() {
		params();
		return path + "/start.vm";
	}
	
	@Command("getIntakes")
	public String getIntakes() throws Exception {
		String programId = (String) getParam("programId");
		listSessions(programId);
		return path + "/divIntakes.vm";
	}
	
	private void params() {
		List<Program> programs = db.list("SELECT a from Program a order by a.code");
		context.put("programs",programs);
		
		if ( context.get("programId") != null ) {
			String programId = (String) context.get("programId");
			listSessions(programId);
		}
		
		List<LearningCentre> centres = db.list("SELECT a from LearningCentre a");
		context.put("centres", centres);
	}
	
	private void listSessions(String programId) {
		Program program = (Program) db.find(Program.class, programId);
		if ( program.getLevel() != null ) {
			List<Session> intakes = db.list("select s from Session s where s.pathNo = " + program.getLevel().getPathNo() + " order by s.startDate");
			context.put("intakes", intakes);
			
			Hashtable param = new Hashtable();
			param.put("date", new Date());
			param.put("path_no", program.getLevel().getPathNo());
			List list = db.list("select s from Session s where :date between s.startDate and s.endDate and s.pathNo = :path_no", param);
			if ( list.size() > 0 ) {
				Session currentSession = (Session) list.get(0);
				context.put("currentSession", currentSession);
				context.put("intakeId", currentSession.getId());
			}
			else {
				context.remove("currentSession");
			}
		}
		else {
			context.remove("intakes");
		}

	}
	
	@Command("listSubjects")
	public String listSubjects() throws Exception {
		String programId = getParam("programId");
		String intakeId = getParam("intakeId");
		String centreId = getParam("centreId");
		
		String sql = "";
		sql = "select p from ProgramStructure p where p.program.id = '" + programId + "' " +
				"and p.learningCenter.id = '" + centreId + "' " +
						"and p.session.id = '" + intakeId + "'";
		
		ProgramStructure ps = (ProgramStructure) db.get(sql);
		if ( ps != null ) {
			List<Period> periods = ps.getProgram().getPeriodScheme().getFunctionalPeriodList();
			context.put("periods", periods);
		
			List<SubjectReg> subjectRegs = new ArrayList<SubjectReg>();
			context.put("subjectRegs", subjectRegs);
			for ( Period p : periods ) {
				Set<SubjectReg> sets = ps.getSubjectRegisters(p.getId());
				if ( sets != null ) {
					for ( SubjectReg sr :  sets ) {
						subjectRegs.add(sr);
					}
				}
			}
		}
		return path + "/listSubjects.vm";
	}
	
	@Command("toggleOffered")
	public String toggleOffered() throws Exception {
		String subjectRegId = getParam("subjectRegId");
		SubjectReg subjectReg = db.find(SubjectReg.class, subjectRegId);
		context.put("subjectReg", subjectReg);
		boolean offered = subjectReg.getOffered();
		db.begin();
		subjectReg.setOffered(!offered);
		db.commit();
		return path + "/offered.vm";
	}
	
	@Command("selectAll")
	public String selectAll() throws Exception {
		String programId = getParam("programId");
		String intakeId = getParam("intakeId");
		String centreId = getParam("centreId");
		
		String sql = "";
		sql = "select p from ProgramStructure p where p.program.id = '" + programId + "' " +
				"and p.learningCenter.id = '" + centreId + "' " +
						"and p.session.id = '" + intakeId + "'";
		
		ProgramStructure ps = (ProgramStructure) db.get(sql);
		if ( ps != null ) {
			List<Period> periods = ps.getProgram().getPeriodScheme().getFunctionalPeriodList();
			context.put("periods", periods);
		
			List<SubjectReg> subjectRegs = new ArrayList<SubjectReg>();
			context.put("subjectRegs", subjectRegs);
			for ( Period p : periods ) {
				Set<SubjectReg> sets = ps.getSubjectRegisters(p.getId());
				if ( sets != null ) {
					db.begin();
					for ( SubjectReg sr :  sets ) {
						sr.setOffered(true);
						subjectRegs.add(sr);
					}
					db.commit();
				}
			}
		}
		return path + "/listSubjects.vm";
	}
	
	@Command("unSelectAll")
	public String unSelectAll() throws Exception {
		String programId = getParam("programId");
		String intakeId = getParam("intakeId");
		String centreId = getParam("centreId");
		
		String sql = "";
		sql = "select p from ProgramStructure p where p.program.id = '" + programId + "' " +
				"and p.learningCenter.id = '" + centreId + "' " +
						"and p.session.id = '" + intakeId + "'";
		
		ProgramStructure ps = (ProgramStructure) db.get(sql);
		if ( ps != null ) {
			List<Period> periods = ps.getProgram().getPeriodScheme().getFunctionalPeriodList();
			context.put("periods", periods);
		
			List<SubjectReg> subjectRegs = new ArrayList<SubjectReg>();
			context.put("subjectRegs", subjectRegs);
			for ( Period p : periods ) {
				Set<SubjectReg> sets = ps.getSubjectRegisters(p.getId());
				if ( sets != null ) {
					db.begin();
					for ( SubjectReg sr :  sets ) {
						sr.setOffered(false);
						subjectRegs.add(sr);
					}
					db.commit();
				}
			}
		}
		return path + "/listSubjects.vm";
	}
	
	public static void main(String[] args) throws Exception {
		String sql = "select p from ProgramStructure p where p.program.id = '1401960688660' and p.learningCenter.id = 'CY' and p.session.id = '1401960688640'";
	
		DbPersistence db = new DbPersistence();
		ProgramStructure ps = (ProgramStructure) db.get(sql);
		ps.getSubjectPeriodList();
	
	}

}
