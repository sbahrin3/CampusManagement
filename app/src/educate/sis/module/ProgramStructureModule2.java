package educate.sis.module;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpSession;

import educate.admission.module.ActivityLogger;
import educate.db.DbPersistence;
import educate.enrollment.entity.StudentStatus;
import educate.enrollment.entity.StudentSubject;
import educate.sis.struct.entity.Faculty;
import educate.sis.struct.entity.LearningCentre;
import educate.sis.struct.entity.Period;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.ProgramStructure;
import educate.sis.struct.entity.Session;
import educate.sis.struct.entity.Subject;
import educate.sis.struct.entity.SubjectCategory;
import educate.sis.struct.entity.SubjectGroup;
import educate.sis.struct.entity.SubjectPeriod;
import educate.sis.struct.entity.SubjectReg;
import lebah.portal.action.AjaxModule;

public class ProgramStructureModule2 extends AjaxModule {
	
	String path = "apps/util/program_structure2/";
	private String vm = "default.vm";
	HttpSession session;
	DbPersistence db = new DbPersistence();
	Hashtable data = null;
	String userId = "";

	protected boolean isAdmin = false;
	
	public String doAction() throws Exception {
		userId = (String) request.getSession().getAttribute("_portal_login");
		context.put("_formName", formName);
		context.put("isAdmin", isAdmin);
		session = request.getSession();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		context.put("dateFormat", dateFormat);
		context.put("programUtil", new ProgramUtil());
		String command = request.getParameter("command");
		System.out.println("command=" + command);
		if ( null == command || "".equals(command)) listPrograms();
		else if ( "back".equals(command)) back();
		else if ( "list_intakes".equals(command)) listIntakes();
		else if ( "get_structure".equals(command)) getStructure();
		else if ( "get_structure_by_centre".equals(command)) getStructureByCentre();
		else if ( "create_structure_by_centre".equals(command)) createStructureByCentre();
		else if ( "add_subject".equals(command)) addSubject();
		
		else if ( "remove_subjects".equals(command)) removeSubjects();
		else if ( "remove_subjects_students".equals(command)) removeSubjectsStudents();
		
		else if ( "assign_subjects_students".equals(command)) assignSubjectsStudents();
		
		else if ( "reassign_subjects_students".equals(command)) reassignSubjectsStudents();

		
		else if ( "save_subjects".equals(command)) saveSubjects();
		
		else if ( "add_subjects".equals(command)) addSubjects();
		else if ( "add_subjects_students".equals(command)) addSubjectsStudents();
		

		
		
		else if ( "select_period".equals(command)) selectPeriod();
		else if ( "copy_from_centre".equals(command)) copyFromCentre();
		else if ( "copy_from_intake".equals(command)) copyFromIntake();
		else if ( "filter_by_faculty".equals(command)) filterByFaculty();
		else if ( "order_by".equals(command)) orderBy();
		else if ( "allow_recurring".equals(command)) allowRecurring();
		
		else if ( "list_subject_groups".equals(command)) listSubjectGroups();
		
		else if ( "changeSubjectCategory".equals(command)) changeSubjectCategory();
		
		return vm;
	}

	private void changeSubjectCategory() throws Exception {
		String subjectRegId = getParam("subjectRegId");
		SubjectReg subjectReg = db.find(SubjectReg.class, subjectRegId);
		String categoryId = getParam(subjectRegId);
		SubjectCategory category = db.find(SubjectCategory.class, categoryId);
		db.begin();
		subjectReg.setCategory(category);
		db.commit();
		vm = path + "empty.vm";
	}

	private void listSubjectGroups() {
		String facultyId = getParam("faculty_id");
		List<SubjectGroup> subjectGroups = db.list("select s from SubjectGroup s where s.faculty.id = '" + facultyId + "'");
		context.put("subjectGroups", subjectGroups);
		vm = path + "list_groups.vm";
	}

	private void allowRecurring() {
		addSubject();
	}


	private void saveSubjects() throws Exception {
		
		
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
		
		vm = path + "div_subjects.vm";
	
	}

	private void orderBy() {
		String structure_id = request.getParameter("structure_id");
		
		ProgramStructure programStructure = (ProgramStructure) db.find(ProgramStructure.class, structure_id);
		
		context.put("programStructure", programStructure);
		
		//List of Subjects
		
		List<Subject> subjects = listOfSubjects(programStructure);
		context.put("subjects", subjects);
		
		vm = path + "subjects_list.vm";
		
	}

	private void filterByFaculty() {
		vm = path + "subjects_list.vm";
		String structureId = request.getParameter("structure_id");
		ProgramStructure programStructure = (ProgramStructure) db.find(ProgramStructure.class, structureId);
		List<Subject> subjects = listOfSubjects(programStructure);
		context.put("subjects", subjects);
		
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
		
		if ( psOther != null ) {
		
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
			//System.out.println("psCurrent : " + psCurrent != null ? psCurrent.getSession().getName() : " null");
			//do copy
			copyProgramStructure(psOther, psCurrent);
		
		}
		else {
			System.out.println("Trying to copy from a NULL Program Structure!");
		}
		
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
		addSubject();
	}
	
	private void reassignSubjectsStudents() throws Exception {
		
		String periodId = request.getParameter("period_id");
		Period period = db.find(Period.class, periodId);
		String structure_id = request.getParameter("structure_id");
		ProgramStructure ps = db.find(ProgramStructure.class, structure_id);
		

		//removes from student first
		LearningCentre centre = ps.getLearningCenter();
		Program program = ps.getProgram();
		List<StudentStatus> studentStatusList = db.list("select s from StudentStatus s where s.student.learningCenter.id = '" + centre.getId() + "' " +
				" and s.student.program.id = '" + program.getId() + "' " +
				" and s.period.id = '" + period.getId() + "'");
		
		//System.out.println("Program = " + program.getId());
		//System.out.println("Semester = " + period.getId());
		db.begin();
		for ( StudentStatus s : studentStatusList ) {
			List<StudentSubject> sx = new ArrayList<StudentSubject>();
			sx.addAll(s.getStudentSubjects());
			for ( StudentSubject ss : sx ) {
				if ( ss != null ) {
					s.delStudentSubject(ss);
					db.remove(ss);
				}
			}
		}
		db.commit();
		
		assignSubjectsStudents();
		
		
	}

	
	private void addSubjectsStudents() throws Exception {
		String categoryId = getParam("subjectCategoryId");
		SubjectCategory subjectCategory = db.find(SubjectCategory.class, categoryId);
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
					if ( subjectCategory == null ) sp.addSubject(s);
					else sp.addSubject(s, subjectCategory);
				}
				db.begin();
				programStructure.addSubjectPeriod(sp);
				db.commit();
			} else if ( sp != null ) {
				db.begin();
				for ( Subject s : subjects ) {
//					if subject already exit, do not add
					boolean found = false;
					if ( sp.getSubjectRegs() != null ) {
						for ( SubjectReg sr : sp.getSubjectRegs() ) {
							if (sr.getSubject().getId().equals(s.getId())) {
								found = true;
								break;
							}
						}
					}
					if ( !found ) {
						if ( subjectCategory == null ) sp.addSubject(s);
						else sp.addSubject(s, subjectCategory);
					}
				}
				db.commit();
			}
			
			
			LearningCentre centre = programStructure.getLearningCenter();
			Program program = programStructure.getProgram();
			Period period = sp.getPeriod();
			List<StudentStatus> studentStatusList = db.list("select s from StudentStatus s where s.student.learningCenter.id = '" + centre.getId() + "' " +
					" and s.student.program.id = '" + program.getId() + "' " +
					" and s.period.id = '" + period.getId() + "'");

			
			//assign to students
			db.begin();
			
			//String remark = "";
			
			for ( StudentStatus s : studentStatusList ) {
				//remark += s.getStudent().getMatricNo() + ", ";
				for ( Subject subject : subjects ) {
					StudentSubject studentSubject = s.getStudentSubject(subject.getId());
					if ( studentSubject == null ) {
						studentSubject = new StudentSubject();
						studentSubject.setSubject(subject);
						studentSubject.setCategory(subjectCategory);
						studentSubject.setSubjectStatus("RG");
						
						if ( s.addStudentSubject(studentSubject) ) {
							studentSubject.setStudentStatus(s);
							db.persist(studentSubject);
						}
						
					}
				}
			}
			
			db.commit();
			
			ActivityLogger.log("ProgramStructureModule", "Add subjects to students - " + program.getCode() + " " + program.getName() + ", " + period.getName() + ", " + centre.getName() + ".");
			

		}
		addSubject();
		
	}
	//ADD SUBJECTS
	private void addSubjects() throws Exception {
		String categoryId = getParam("subjectCategoryId");
		SubjectCategory subjectCategory = db.find(SubjectCategory.class, categoryId);
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
				System.out.println("sp is null");
				sp = new SubjectPeriod();
				Period period = (Period) db.find(Period.class, periodId);
				sp.setPeriod(period);
				sp.setProgramStructure(programStructure);
				for ( Subject s : subjects ) {
					if ( subjectCategory == null ) sp.addSubject(s);
					else sp.addSubject(s, subjectCategory);
				}
				db.begin();
				programStructure.addSubjectPeriod(sp);
				db.commit();
			} else if ( sp != null ) {
				System.out.println("sp is not null");
				db.begin();
				for ( Subject s : subjects ) {
//					if subject already exit, do not add
					boolean found = false;
					if ( sp.getSubjectRegs() != null ) {
						for ( SubjectReg sr : sp.getSubjectRegs() ) {
							if (sr.getSubject().getId().equals(s.getId())) {
								found = true;
								break;
							}
						}
					}
					if ( !found ) {
						if ( subjectCategory == null ) sp.addSubject(s);
						else sp.addSubject(s, subjectCategory);
					}
				}
				db.commit();
			}
		}
		addSubject();
	}

	private void removeSubjects() throws Exception {
		String[] subjectIds = request.getParameterValues("remove_ids");
		if ( subjectIds != null ) {
			String periodId = request.getParameter("period_id");
			String structure_id = request.getParameter("structure_id");
			ProgramStructure programStructure = (ProgramStructure) db.find(ProgramStructure.class, structure_id);
			deleteSubjects(programStructure, periodId, subjectIds);
		}
		addSubject();
	}
	
	private void deleteSubjects(ProgramStructure ps, String periodId, String[] subjectIds) throws Exception {
		//select subject period for this period id
		List<SubjectPeriod> subjectPeriods = db.list("select sp from ProgramStructure p JOIN p.subjectPeriod sp where p.id = '" + ps.getId() + "' and sp.period.id = '" + periodId + "'");
		if ( subjectPeriods.size() > 0 ) { //must be only ONE
			SubjectPeriod sp = subjectPeriods.get(0);
			Set<SubjectReg> subjectRegs = sp.getSubjectRegs();
			List<SubjectReg> removes = new ArrayList<SubjectReg>();
			for ( SubjectReg r : subjectRegs ) {
				for ( String id : subjectIds ) {
					if ( r.getId().equals(id)) removes.add(r);
				}
			}
			db.begin();
			for ( SubjectReg r : removes ) {
				subjectRegs.remove(r);
				db.remove(r);
			}
			db.commit();
		}
	}
	
	private void removeSubjectsStudents() throws Exception {
		String[] subjectIds = request.getParameterValues("remove_ids");
		if ( subjectIds != null ) {
			String periodId = request.getParameter("period_id");
			String structure_id = request.getParameter("structure_id");
			ProgramStructure programStructure = (ProgramStructure) db.find(ProgramStructure.class, structure_id);
			deleteSubjectsStudents(programStructure, periodId, subjectIds);
		}
		addSubject();
	}
	
	private void deleteSubjectsStudents(ProgramStructure ps, String periodId, String[] subjectIds) throws Exception {
		//select subject period for this period id
		List<SubjectPeriod> subjectPeriods = db.list("select sp from ProgramStructure p JOIN p.subjectPeriod sp where p.id = '" + ps.getId() + "' and sp.period.id = '" + periodId + "'");
		if ( subjectPeriods.size() > 0 ) { //must be only ONE
			SubjectPeriod sp = subjectPeriods.get(0);
			Set<SubjectReg> subjectRegs = sp.getSubjectRegs();
			List<SubjectReg> removes = new ArrayList<SubjectReg>();
			for ( SubjectReg r : subjectRegs ) {
				for ( String id : subjectIds ) {
					if ( r.getId().equals(id)) removes.add(r);
				}
			}

			//removes from student first
			LearningCentre centre = ps.getLearningCenter();
			Program program = ps.getProgram();
			Period period = sp.getPeriod();
			List<StudentStatus> studentStatusList = db.list("select s from StudentStatus s where s.student.learningCenter.id = '" + centre.getId() + "' " +
					" and s.student.program.id = '" + program.getId() + "' " +
					" and s.period.id = '" + period.getId() + "'");
			db.begin();
			for ( StudentStatus s : studentStatusList ) {
				for ( SubjectReg r : removes ) {
					StudentSubject studentSubject = s.getStudentSubject(r.getSubject().getId());
					if ( studentSubject != null ) {
						s.delStudentSubject(studentSubject);
						db.remove(studentSubject);
					}					
				}
			}
		
			//removes from program structure
			for ( SubjectReg r : removes ) {
				subjectRegs.remove(r);
				db.remove(r);
			}
			db.commit();
			
			
			ActivityLogger.log("ProgramStructureModule", "Delete subjects from students - " + program.getCode() + " " + program.getName() + ", " + period.getName() + ", " + centre.getName() + ".");
			
			
			

		}
	}	
	
	private void assignSubjectsStudents() throws Exception {
		String[] subjectIds = request.getParameterValues("remove_ids");
		if ( subjectIds != null ) {
			String periodId = request.getParameter("period_id");
			String structure_id = request.getParameter("structure_id");
			ProgramStructure programStructure = (ProgramStructure) db.find(ProgramStructure.class, structure_id);
			
			List<SubjectPeriod> subjectPeriods = db.list("select sp from ProgramStructure p JOIN p.subjectPeriod sp where p.id = '" + programStructure.getId() + "' and sp.period.id = '" + periodId + "'");
			List<SubjectReg> subjects = new ArrayList<SubjectReg>();
			if ( subjectPeriods.size() > 0 ) { //must be only ONE
				SubjectPeriod sp = subjectPeriods.get(0);
				Set<SubjectReg> subjectRegs = sp.getSubjectRegs();
				for ( SubjectReg r : subjectRegs ) {
					for ( String id : subjectIds ) {
						if ( r.getId().equals(id)) subjects.add(r);
					}
				}
			}
			
			LearningCentre centre = programStructure.getLearningCenter();
			Program program = programStructure.getProgram();
			Period period = db.find(Period.class, periodId);
			List<StudentStatus> studentStatusList = db.list("select s from StudentStatus s where s.student.learningCenter.id = '" + centre.getId() + "' " +
					" and s.student.program.id = '" + program.getId() + "' " +
					" and s.period.id = '" + period.getId() + "'");

			
			//assign to students
			db.begin();
			
			//String remark = "";
			
			for ( StudentStatus s : studentStatusList ) {
				//remark += s.getStudent().getMatricNo() + ", ";
				for ( SubjectReg subject : subjects ) {
					StudentSubject studentSubject = s.getStudentSubject(subject.getSubject());
					if ( studentSubject == null ) {
						studentSubject = new StudentSubject();
						studentSubject.setSubject(subject.getSubject());
						studentSubject.setCategory(subject.getCategory());
						studentSubject.setSubjectStatus("RG");
						
						if ( s.addStudentSubject(studentSubject) ) {
							studentSubject.setStudentStatus(s);
							db.persist(studentSubject);
						}
						
					}
				}
			}
			
			db.commit();
		
			ActivityLogger.log("ProgramStructureModule", "Re-assign subjects to students - " + program.getCode() + " " + program.getName() + ", " + period.getName() + ", " + centre.getName() + ".");
			
			
		}
		else {
			//not selected any subjects
		}
		addSubject();
	}
	

	private void addSubject() {
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
		
		String facultyId = getParam("faculty_id");
		String subjectGroupId = getParam("subjectGroup_id");
		

		context.put("faculty_id", facultyId);
		context.put("subjectGroup_id", subjectGroupId);
		
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
		
		if ( !"".equals(subjectGroupId) && !"".equals(facultyId)) 
			context.put("subjectGroups", db.list("select s from SubjectGroup s where s.faculty.id = '" + facultyId + "'"));
		else
			context.remove("subjectGroups");
		
		//List of Categories
		List<SubjectCategory> categories = db.list("select c from SubjectCategory c order by c.code");
		context.put("categories", categories);
		
		
	}

	private List<Subject> listOfSubjects(ProgramStructure programStructure) {
		
		String allowRecurring = request.getParameter("allow_recurring") != null ? request.getParameter("allow_recurring") : "no";
		context.put("allow_recurring", allowRecurring);

		String facultyId = request.getParameter("faculty_id") != null ? request.getParameter("faculty_id") : "";
		String subjectGroupId = request.getParameter("subjectGroup_id") != null ? request.getParameter("subjectGroup_id") : "";
		
		String orderBy = request.getParameter("order_by") != null ? request.getParameter("order_by") : "";
		if ( "".equals(orderBy)) {
			orderBy = request.getParameter("ordered_by") != null ? request.getParameter("ordered_by") : "";
		}
		context.put("faculty_id", facultyId);
		context.put("order_by", orderBy);
		ProgramUtil pu = new ProgramUtil();
		Set<Subject> list = pu.getSubjects(programStructure);
		
		//System.out.println("program structure id = " + programStructure.getId());
		//System.out.println("learning centre = " + programStructure.getLearningCenter().getCode());
		//System.out.println("number of subjects in " + programStructure.getProgram().getCode() + " = " + list.size());
		
		
		
		String sql = "";
		if ( list.size() > 0 && "no".equals(allowRecurring)) {
			String query_NOT_IN = "('";
			for ( Subject ps : list ) {
				if ( ps != null ) {
					String exclude = ps.getId();
					query_NOT_IN += exclude + "', '";
				}
			}
			query_NOT_IN = query_NOT_IN.substring(0, query_NOT_IN.length()-3) + ")";
			sql = "select s from Subject s where ";
			if ( !"".equals(facultyId) && "".equals(subjectGroupId) )
					sql += "s.faculty.id = '" + facultyId + "' and s.id NOT IN " +  query_NOT_IN + "";
			else if ( !"".equals(facultyId) && !"".equals(subjectGroupId) )
				if ( "_NA_".equals(subjectGroupId)) {
					sql += "s.faculty.id = '" + facultyId + "' and s.subjectGroup.id is null and s.id NOT IN " +  query_NOT_IN + "";
				} else
					sql += "s.faculty.id = '" + facultyId + "' and s.subjectGroup.id = '" + subjectGroupId + "' and s.id NOT IN " +  query_NOT_IN + "";
			else
				sql += "s.id NOT IN " +  query_NOT_IN + "";
			
			sql += " and s.code <> '' ";
			
			if ( "name".equals(orderBy))
				sql += " order by s.name";
			else
				sql += " order by s.code";
		}
		else {
			sql = "select s from Subject s ";
			if ( !"".equals(facultyId) && "".equals(subjectGroupId) )
				sql += "where s.faculty.id = '" + facultyId + "'";
			else if ( !"".equals(facultyId) && !"".equals(subjectGroupId) )
				if ( "_NA_".equals(subjectGroupId)) {
					sql += "where s.faculty.id = '" + facultyId + "' and s.subjectGroup.id is null ";
				} else
					sql += "where s.faculty.id = '" + facultyId + "' and s.subjectGroup.id = '" + subjectGroupId + "'";
			
			if ( "name".equals(orderBy))
				sql += " order by s.name";
			else
				sql += " order by s.code";			
		}
		
		//System.out.println(sql);
		
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
		
		if ( ps.getProgram() == null ) throw new Exception ("Program Structure has no Program!");
		if ( ps.getProgram().getPeriodScheme() == null ) throw new Exception("Program has no Period Scheme!");
		if ( ps.getProgram().getPeriodScheme().getFunctionalPeriodList() == null ) throw new Exception("Period Scheme has no Study Period!");
		
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
		listSessions(programId);
		
		params();
		
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
				context.put("current_session", currentSession);
				context.put("intake_id", currentSession.getId());
			}
			else {
				context.remove("current_session");
				//context.remove("intake_id");
			}
		}

	}

	private void listPrograms() {
		context.remove("intakes");
		params();
		
		vm = path + "start.vm";
	}
	
	private void back() {
		context.remove("intakes");
		
		String programId = getParam("program_id");
		String intakeId = getParam("intake_id");
		String centreId = getParam("centre_id");
		
		context.put("program_id", programId);
		context.put("intake_id", intakeId);
		context.put("centre_id", centreId);
		
		params();
		
		
		vm = path + "list_programs.vm";
	}
	
	
	private void params() {
		System.out.println("List all programs..");
		
		List<Program> programs = db.list("SELECT a from Program a order by a.code");
		context.put("programs", programs);
		
		
		
		if ( context.get("program_id") != null ) {
			String programId = (String) context.get("program_id");
			if ( programId != null ) {
				listSessions(programId);
			}
		}
		
		List<LearningCentre> centres = db.list("SELECT a from LearningCentre a");
		context.put("centres", centres);
	}


}
