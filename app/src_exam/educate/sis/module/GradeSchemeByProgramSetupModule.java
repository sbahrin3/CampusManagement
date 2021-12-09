package educate.sis.module;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import educate.db.DbPersistence;
import educate.sis.exam.entity.MarkingGrade;
import educate.sis.exam.entity.SubjectGrade;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.ProgramStructure;
import educate.sis.struct.entity.Subject;
import educate.sis.struct.entity.SubjectPeriod;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class GradeSchemeByProgramSetupModule extends LebahModule {
	
	String path = "apps/util/grade_setup_by_program";
	DbPersistence db = new DbPersistence();	
	
	public void preProcess() {
		System.out.println(command);
	}

	@Override
	public String start() {
		List<Program> programs = db.list("select p from Program p order by p.name");
		context.put("programs", programs);
		return path + "/start.vm";
	}
	
	@Command("back")
	public String back() {
		List<Program> programs = db.list("select p from Program p order by p.name");
		context.put("programs", programs);		
		return path + "/select_params.vm";
	}
	
	@Command("getProgram")
	public String getProgram() throws Exception {
		String programId = getParam("programId");
		Program program = db.find(Program.class, programId);
		context.put("program", program);
		
		//list all subjects assign to this program
		List<ProgramStructure> list = db.list("select p from ProgramStructure p where p.program.id = '" + programId + "'");
		
		Set<Subject> subjects = new HashSet<Subject>();
		context.put("subjects", subjects);
		for ( ProgramStructure p : list ) {
			Set<SubjectPeriod> subjectPeriods = p.getSubjectPeriod();
			for ( SubjectPeriod sp : subjectPeriods ) {
				Set<Subject> ss = sp.getSubjects();
				for ( Subject s : ss ) {
					subjects.add(s);
				}
			}
		}	
		
		
		//list all marking grade
		List<MarkingGrade> markingGrades = db.list("select g from MarkingGrade g");
		context.put("markingGrades", markingGrades);
		
		//list subjectGrade by Program
		List<SubjectGrade> subjectGrades = db.list("select s from SubjectGrade s where s.program.id = '" + programId + "'");
		
		Set<Subject> removedSubjects = new HashSet<Subject>();
		context.put("removedSubjects", removedSubjects);
		
		Map<String, MarkingGrade> gradeMap = new HashMap<String, MarkingGrade>();
		context.put("gradeMap", gradeMap);
		for ( SubjectGrade s : subjectGrades ) {
			gradeMap.put(s.getSubject().getId(), s.getMarkingGrade());
			//add subjects if exists in subjectGrades but not in programStructure
			if ( !subjects.contains(s.getSubject())) {
				removedSubjects.add(s.getSubject());
			}
		}

		return path + "/scheme_program.vm";
	}
	
	@Command("updateGrade")
	public String updateGrade() throws Exception {
		String subjectId = getParam("subjectId");
		Subject subject = db.find(Subject.class, subjectId);
		String programId = getParam("programId");
		Program program = db.find(Program.class, programId);
		String gradeId = getParam("grade_" + subjectId);
		MarkingGrade markingGrade = db.find(MarkingGrade.class, gradeId);
		
		//find subject grade for this subject and program
		SubjectGrade subjectGrade = (SubjectGrade) db.get("select s from SubjectGrade s where s.program.id = '" + programId + "' and s.subject.id = '" + subjectId + "'");
		if ( subjectGrade == null ) {
			subjectGrade = new SubjectGrade();
			subjectGrade.setProgram(program);
			subjectGrade.setSubject(subject);
			db.begin();
			subjectGrade.setMarkingGrade(markingGrade);
			db.persist(subjectGrade);
			db.commit();
		}
		else {
			db.begin();
			subjectGrade.setMarkingGrade(markingGrade);
			db.commit();
		}
		
		
		return path + "/empty.vm";
	}
	
	@Command("removeSubject")
	public String removeSubject() throws Exception {
		String subjectId = getParam("subjectId");
		String programId = getParam("programId");
		SubjectGrade subjectGrade = (SubjectGrade) db.get("select s from SubjectGrade s where s.program.id = '" + programId + "' and s.subject.id = '" + subjectId + "'");
		db.begin();
		db.remove(subjectGrade);
		db.commit();
		return getProgram();
	}
	
	@Command("assignAll")
	public String assignAll() throws Exception {
		
		String programId = getParam("programId");
		Program program = db.find(Program.class, programId);
		String gradeId = getParam("marking_all");
		MarkingGrade markingGrade = db.find(MarkingGrade.class, gradeId);
		
		String[] subjectIds = request.getParameterValues("subjectIds");
		if ( subjectIds != null ) {
			for ( String subjectId : subjectIds ) {
				
				//find subject grade for this subject and program
				SubjectGrade subjectGrade = (SubjectGrade) db.get("select s from SubjectGrade s where s.program.id = '" + programId + "' and s.subject.id = '" + subjectId + "'");
				if ( subjectGrade == null ) {
					Subject subject = db.find(Subject.class, subjectId);
					subjectGrade = new SubjectGrade();
					subjectGrade.setProgram(program);
					subjectGrade.setSubject(subject);
					db.begin();
					subjectGrade.setMarkingGrade(markingGrade);
					db.persist(subjectGrade);
					db.commit();
				}
				else {
					db.begin();
					subjectGrade.setMarkingGrade(markingGrade);
					db.commit();
				}
				
			}
		}
	
		return getProgram();
	}
	


}
