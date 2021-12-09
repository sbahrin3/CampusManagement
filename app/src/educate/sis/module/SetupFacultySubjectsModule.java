package educate.sis.module;

import java.util.ArrayList;
import java.util.List;

import educate.db.DbPersistence;
import educate.sis.exam.entity.MarkingGrade;
import educate.sis.struct.entity.Faculty;
import educate.sis.struct.entity.Institution;
import educate.sis.struct.entity.Subject;
import educate.sis.struct.entity.SubjectGroup;
import educate.sis.struct.entity.Teacher;
import educate.sis.struct.entity.TeacherSubject;
import educate.timetabling.entity.ClassroomSection;
import educate.timetabling.entity.ClassroomTemplate;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

/**
 * 
 * @author Shamsul Bahrin bin Abd Mutalib
 *
 */
public class SetupFacultySubjectsModule extends LebahModule {
	
	protected String path = "apps/setup_faculty_subjects";
	protected DbPersistence db = new DbPersistence();
	protected boolean classroomMode = false;
	
	public void preProcess() {
		System.out.println(command);
		context.put("path", path);
		context.put("classroomMode", classroomMode);
	}

	@Override
	public String start() {
		Institution institution = (Institution) db.get("select i from Institution i");
		context.put("institution", institution);
		context.put("institutionId", institution.getId());
		List<Faculty> faculties = db.list("select f from Faculty f order by f.name");
		context.put("faculties", faculties);
		return path + "/start.vm";
	}

	@Command("getSubjectGroups")
	public String getSubjectGroups() throws Exception {
		String facultyId = getParam("getFacultyId");
		Faculty faculty = db.find(Faculty.class, facultyId);
		context.put("faculty", faculty);

		List<SubjectGroup> subjectGroups = db.list("select c from SubjectGroup c where c.faculty.id = '" + facultyId + "' order by c.sequence");

		context.put("subjectGroups", subjectGroups);
		return path + "/listSubjectGroups.vm";
	}
	
	@Command("getSubjects")
	public String getSubjects() throws Exception {
		String facultyId = getParam("facultyId");
		context.put("facultyId", facultyId);
		Faculty faculty = db.find(Faculty.class, facultyId);
		context.put("faculty", faculty);
		String subjectGroupId = getParam("getSubjectGroupId");
			
		if ( !subjectGroupId.equals("undefined")) {
			if ( !"".equals(subjectGroupId) ) {
				SubjectGroup subjectGroup = db.find(SubjectGroup.class, subjectGroupId);
				context.put("subjectGroup", subjectGroup);
				List<Subject> subjects = db.list("select p from Subject p where p.subjectGroup.id = '" + subjectGroupId + "'");
				context.put("subjects", subjects);
			} else if ( "".equals(subjectGroupId)) {
				context.remove("subjectGroup");
				List<Subject> subjects = db.list("select p from Subject p where p.faculty.id = '" + facultyId + "' and p.subjectGroup.id is null");
				context.put("subjects", subjects);
			}
		}
		else {
			context.remove("subjectGroup");
			List<Subject> subjects = db.list("select p from Subject p where p.faculty.id = '" + facultyId + "' and p.subjectGroup.id is null");
			context.put("subjects", subjects);			
		}
		
		return path + "/listSubjects.vm";
	}
	
	
	@Command("entrySubjectGroup")
	public String entrySubjectGroup() throws Exception {
		String facultyId = getParam("facultyId");
		context.put("facultyId", facultyId);
		
		List<MarkingGrade> markingGrades = db.list("select g from MarkingGrade g");
		context.put("markingGrades", markingGrades);
		
		return path + "/entrySubjectGroup.vm";
	}
	
	@Command("editSubjectGroup")
	public String editSubjectGroup() throws Exception {
		String facultyId = getParam("facultyId");
		context.put("facultyId", facultyId);

		String subjectGroupId = getParam("subjectGroupId");
		context.put("subjectGroupId", subjectGroupId);
		
		SubjectGroup subjectGroup = db.find(SubjectGroup.class, subjectGroupId);
		context.put("subjectGroup", subjectGroup);
		
		List<MarkingGrade> markingGrades = db.list("select g from MarkingGrade g");
		context.put("markingGrades", markingGrades);
		
		return path + "/editSubjectGroup.vm";
	}
	
	@Command("saveSubjectGroup")
	public String saveSubjectGroup() throws Exception {
		String facultyId = getParam("facultyId");
		context.put("facultyId", facultyId);
		Faculty faculty = db.find(Faculty.class, facultyId);

		String subjectGroupId = getParam("subjectGroupId");
		context.put("subjectGroupId", subjectGroupId);
		
		String markingGradeId = getParam("markingGrade_" + subjectGroupId);
		MarkingGrade markingGrade = db.find(MarkingGrade.class, markingGradeId);
		
		int seq = 100;
		try {
			seq = Integer.parseInt(getParam("sequence_" + subjectGroupId));
		} catch ( Exception e ) { }
		
		SubjectGroup subjectGroup = db.find(SubjectGroup.class, subjectGroupId);
		db.begin();
		subjectGroup.setFaculty(faculty);
		subjectGroup.setCode(getParam("subjectGroupCode_" + subjectGroupId));
		subjectGroup.setName(getParam("subjectGroupName_" + subjectGroupId));
		
		subjectGroup.setSequence(seq);
		
		subjectGroup.setCreateAverage(Integer.parseInt(getParam("createAverage_" + subjectGroupId)));
		
		subjectGroup.setMarkingGrade(markingGrade);
		
		db.commit();
		
		List<SubjectGroup> subjectGroups = db.list("select c from SubjectGroup c where c.faculty.id = '" + facultyId + "' order by c.sequence");
		context.put("subjectGroups", subjectGroups);
		return path + "/divListSubjectGroups.vm";
	}
	
	@Command("addSubjectGroup")
	public String addSubjectGroup() throws Exception {
		String facultyId = getParam("facultyId");
		context.put("facultyId", facultyId);
		Faculty faculty = db.find(Faculty.class, facultyId);
		context.put("faculty", faculty);
		
		String markingGradeId = getParam("markingGrade");
		MarkingGrade markingGrade = db.find(MarkingGrade.class, markingGradeId);
		
		db.begin();
		SubjectGroup subjectGroup = new SubjectGroup();
		subjectGroup.setFaculty(faculty);
		subjectGroup.setCode(getParam("subjectGroupCode"));
		subjectGroup.setName(getParam("subjectGroupName"));
		subjectGroup.setSequence(100);
		
		
		subjectGroup.setCreateAverage(Integer.parseInt(getParam("createAverage")));
		
		subjectGroup.setMarkingGrade(markingGrade);
		
		db.persist(subjectGroup);
		db.commit();
		
		List<SubjectGroup> subjectGroups = db.list("select c from SubjectGroup c where c.faculty.id = '" + facultyId + "' order by c.sequence");
		context.put("subjectGroups", subjectGroups);
		return path + "/divListSubjectGroups.vm";
		
	}
	
	@Command("deleteSubjectGroup")
	public String deleteSubjectGroup() throws Exception {
		String facultyId = getParam("facultyId");
		context.put("facultyId", facultyId);

		String subjectGroupId = getParam("subjectGroupId");
		context.put("subjectGroupId", subjectGroupId);
		
		int cnt = 0;
		cnt = db.list("select p from Subject p where p.subjectGroup.id = '" + subjectGroupId + "'").size();
		
		if ( cnt == 0 ) {
			SubjectGroup subjectGroup = db.find(SubjectGroup.class, subjectGroupId);
			db.begin();
			db.remove(subjectGroup);
			db.commit();
		} else {
			System.out.println("Can't delete SubjectGroup");
		}
		
		List<SubjectGroup> subjectGroups = db.list("select c from SubjectGroup c where c.faculty.id = '" + facultyId + "' order by c.sequence");
		context.put("subjectGroups", subjectGroups);
		return path + "/divListSubjectGroups.vm";
	}


	
	@Command("entrySubject")
	public String entrySubject() throws Exception {
		String facultyId = getParam("facultyId");
		context.put("facultyId", facultyId);
		String subjectGroupId = getParam("subjectGroupId");
		context.put("subjectGroupId", subjectGroupId);
		SubjectGroup subjectGroup = db.find(SubjectGroup.class, subjectGroupId);
		context.put("subjectGroup", subjectGroup);
		
		context.put("subjectGroups", db.list("select s from SubjectGroup s where s.faculty.id = '" + facultyId + "' order by s.sequence"));
		
		List<MarkingGrade> markingGrades = db.list("select g from MarkingGrade g");
		context.put("markingGrades", markingGrades);
		
		return path + "/entrySubject.vm";
	}
	
	@Command("editSubject")
	public String editSubject() throws Exception {
		context.put("subjectGroups", db.list("select c from SubjectGroup c order by c.code"));
		String subjectId = getParam("subjectId");
		context.put("subjectId", subjectId);
		Subject subject = db.find(Subject.class, subjectId);
		context.put("subject", subject);
		String subjectGroupId = getParam("subjectGroupId");
		context.put("subjectGroupId", subjectGroupId);
		SubjectGroup subjectGroup = db.find(SubjectGroup.class, subjectGroupId);
		context.put("subjectGroup", subjectGroup);

		String facultyId = getParam("facultyId");
		context.put("facultyId", facultyId);

		//context.put("subjectGroups", db.list("select s from SubjectGroup s where s.faculty.id = '" + facultyId + "'"));
		
		List<SubjectGroup> subjectGroupList = new ArrayList<SubjectGroup>();
		//groups in this faculty
		List<SubjectGroup> subjectGroups = db.list("select c from SubjectGroup c where c.faculty.id = '" + facultyId + "' order by c.sequence");
		//groups in other faculties
		List<SubjectGroup> subjectGroups2 = db.list("select c from SubjectGroup c where c.faculty.id <> '" + facultyId + "' order by c.faculty.code, c.sequence");
		
		subjectGroupList.addAll(subjectGroups);
		subjectGroupList.addAll(subjectGroups2);
		
		context.put("subjectGroups", subjectGroupList);

		
		List<MarkingGrade> markingGrades = db.list("select g from MarkingGrade g");
		context.put("markingGrades", markingGrades);
		
		return path + "/editSubject.vm";
	}
	
	@Command("saveSubject")
	public String saveSubject() throws Exception {
		
		String facultyId = getParam("facultyId");
		context.put("facultyId", facultyId);
	
		String subjectId = getParam("subjectId");
		context.put("subjectId", subjectId);
		
		
		SubjectGroup subjectGroup = db.find(SubjectGroup.class, getParam("subjectGroup_" + subjectId));
		
		Subject subject = db.find(Subject.class, subjectId);
		context.put("subject", subject);
		
		String markingGradeId = getParam("markingGrade_" + subjectId);
		MarkingGrade markingGrade = db.find(MarkingGrade.class, markingGradeId);		
		
		db.begin();
		subject.setSubjectGroup(subjectGroup);
		subject.setCode(getParam("subjectCode_" + subjectId));
		subject.setName(getParam("subjectName_" + subjectId));
		subject.setShortName(getParam("shortName_" + subjectId));
		subject.setAltName(getParam("subjectAltName_" + subjectId));
		subject.setCredithrs(Integer.parseInt(getParam("subjectCreditHours_" + subjectId)));
		subject.setExcludeGpa(Integer.parseInt(getParam("subjectExcludeGpa_" + subjectId)));
		
		subject.setMarkingGrade(markingGrade);
		
		int quota = 0;
		try {
			quota = Integer.parseInt(getParam("quota_" + subjectId));
		} catch ( Exception e ) { }
		subject.setQuota(quota);
		
		db.commit();
		
		String subjectGroupId = getParam("subjectGroupId");
		if ( !"".equals(subjectGroupId)) {
			context.put("subjectGroupId", subjectGroupId);
			List<Subject> subjects = db.list("select p from Subject p where p.subjectGroup.id = '" + subjectGroupId + "'");
			context.put("subjects", subjects);
		} else {
			context.remove("subjectGroupId");
			List<Subject> subjects = db.list("select p from Subject p where p.faculty.id = '" + facultyId + "' and p.subjectGroup is null");
			context.put("subjects", subjects);
			
		}
		
		return path + "/divListSubjects.vm";
	}
	
	@Command("addSubject")
	public String addSubject() throws Exception {
		
		String facultyId = getParam("facultyId");
		context.put("facultyId", facultyId);
		Faculty faculty = db.find(Faculty.class, facultyId);
		
		String subjectGroupId = getParam("subjectGroupId");
		SubjectGroup subjectGroup = db.find(SubjectGroup.class, subjectGroupId);
		
		String markingGradeId = getParam("markingGrade");
		MarkingGrade markingGrade = db.find(MarkingGrade.class, markingGradeId);
		
		db.begin();
		Subject subject = new Subject();
		subject.setFaculty(faculty);
		subject.setSubjectGroup(subjectGroup);
		subject.setCode(getParam("subjectCode"));
		subject.setName(getParam("subjectName"));
		subject.setShortName(getParam("shortName"));
		subject.setAltName(getParam("subjectAltName"));
		subject.setCredithrs(Integer.parseInt(getParam("subjectCreditHours")));
		subject.setExcludeGpa(Integer.parseInt(getParam("subjectExcludeGpa")));
		
		subject.setMarkingGrade(markingGrade);
		
		int quota = 0;
		try {
			quota = Integer.parseInt(getParam("quota"));
		} catch ( Exception e ) { }
		subject.setQuota(quota);
		
		db.persist(subject);
		db.commit();
		
		if ( !"".equals(subjectGroupId)) {
			context.put("subjectGroupId", subjectGroupId);
			List<Subject> subjects = db.list("select p from Subject p where p.subjectGroup.id = '" + subjectGroupId + "'");
			context.put("subjects", subjects);
		} else {
			context.remove("subjectGroupId");
			List<Subject> subjects = db.list("select p from Subject p where p.faculty.id = '" + facultyId + "' and p.subjectGroup is null");
			context.put("subjects", subjects);
			
		}
		
		return path + "/divListSubjects.vm";
	}
	
	
	@Command("deleteSubject")
	public String deleteSubject() throws Exception {
	
		String subjectId = getParam("subjectId");
		context.put("subjectId", subjectId);
		
		int cnt = 0;
		cnt = db.list("select s from StudentSubject s where s.subject.id = '" + subjectId + "'").size();
		if ( cnt == 0 ) cnt = db.list("select p from SubjectReg p where p.subject.id = '" + subjectId + "'").size();
		
		if ( cnt == 0 ) {
			Subject subject = db.find(Subject.class, subjectId);
			db.begin();
			db.remove(subject);
			db.commit();
		}
		else {
			System.out.println("Can't delete Subject ");
		}

		String subjectGroupId = getParam("subjectGroupId");
		String facultyId = getParam("facultyId");
		if ( !"".equals(subjectGroupId)) {
			context.put("subjectGroupId", subjectGroupId);
			List<Subject> subjects = db.list("select p from Subject p where p.subjectGroup.id = '" + subjectGroupId + "'");
			context.put("subjects", subjects);
		} else {
			context.remove("subjectGroupId");
			List<Subject> subjects = db.list("select p from Subject p where p.faculty.id = '" + facultyId + "' and p.subjectGroup is null");
			context.put("subjects", subjects);
			
		}
		
		return path + "/divListSubjects.vm";
	}
	
	@Command("assignTeachers")
	public String assignTeachers() throws Exception {
		String subjectId = getParam("subjectId");
		context.put("subjectId", subjectId);
		Subject subject = db.find(Subject.class, subjectId);
		context.put("subject", subject);
		
		return listAssignedTeachers(subjectId);
	}
	
	@Command("updateAssignTeachers")
	public String updateAssignTeachers() throws Exception {
		String subjectId = getParam("subjectId");
		context.put("subjectId", subjectId);
		Subject subject = db.find(Subject.class, subjectId);
		context.put("subject", subject);
		
		//list of assigned teachers to this subject
		List<TeacherSubject> teacherSubjects = db.list("select ts from TeacherSubject ts where ts.subject.id = '" + subjectId + "'");
		//update
		
		String[] removeIds = request.getParameterValues("removeTeacherIds");
		if ( removeIds != null ) {
			List<TeacherSubject> removes = new ArrayList<TeacherSubject>();
			for ( TeacherSubject t : teacherSubjects ) {
				for ( String teacherId : removeIds ) {
					if ( t.getTeacher() != null ) {
						if ( teacherId.equals(t.getTeacher().getId())) {
							removes.add(t);
						}
					}
				}
			}
			for ( TeacherSubject ts : removes ) {
				Teacher teacher = ts.getTeacher();
				if ( teacher != null ) {
					db.begin();
					teacher.removeTeacherSubject(ts);
					db.remove(ts);
					db.commit();
				}
			}
		}
		
		String[] teacherIds = request.getParameterValues("teacherIds");
		if ( teacherIds != null ) {
			
			//default, get section 1
			//SubjectSection section = db.find(SubjectSection.class, "1");
			
			for ( String teacherId : teacherIds ) {
				Teacher teacher = db.find(Teacher.class, teacherId);
				System.out.println("add teacher subject:" + teacher.getName());
				
				db.begin();
				TeacherSubject ts = new TeacherSubject();
				ts.setTeacher(teacher);
				ts.setSubject(subject);
				ts.setMainCampus(true);
				//ts.setSection(section);
				//teacher.addTeacherSubject(ts);
				db.persist(ts);
				db.commit();
			}
		}
		
		return listAssignedTeachers(subjectId);
	}

	private String listAssignedTeachers(String subjectId) {
		//list of assigned teachers to this subject
		List<TeacherSubject> assignedTeachers = db.list("select ts from TeacherSubject ts Join ts.teacher t where ts.subject.id = '" + subjectId + "' order by t.name");
		context.put("assignedTeachers", assignedTeachers);

		System.out.println("assigned teachers size = " + assignedTeachers.size());
		
		//list of teachers
		List<Teacher> teachers = new ArrayList<Teacher>();
		List<Teacher> allTeachers = db.list("select t from Teacher t order by t.name");
		for ( Teacher t : allTeachers ) {
			boolean f = false;
			for ( TeacherSubject x : assignedTeachers ) {
				if ( x.getTeacher() != null ) {
					if ( x.getTeacher().getId().equals(t.getId())) {
						f = true;
						break;
					}
				}
			}
			if ( !f ) {
				teachers.add(t);
			}			
		}
		context.put("teachers", teachers);
		return path + "/assignTeachers.vm";
	}	
	
	@Command("assignClassrooms")
	public String assignClassrooms() throws Exception {
		String subjectId = getParam("subjectId");
		context.put("subjectId", subjectId);
		Subject subject = db.find(Subject.class, subjectId);
		context.put("subject", subject);
		
		List<ClassroomTemplate> assignedClassrooms  = db.list("select c from ClassroomTemplate c where c.subject.id = '" + subjectId + "' order by c.section.sequence");
		context.put("assignedClassrooms", assignedClassrooms);
		
		List<ClassroomSection> classrooms = new ArrayList<ClassroomSection>();
		context.put("classrooms", classrooms);
		List<ClassroomSection> classroomSections = db.list("select r from ClassroomSection r order by r.sequence");
		
		for ( ClassroomSection c : classroomSections ) {
			boolean f = false;
			for ( ClassroomTemplate x : assignedClassrooms ) {
				if ( x.getSection().getId().equals(c.getId()) ) {
					f = true;
					break;
				}
			}
			if ( !f ) {
				classrooms.add(c);
			}				
		}
		
		return path + "/assignClassrooms.vm";
	}
	
	@Command("updateAssignClassrooms")
	public String updateAssignClassrooms() throws Exception {
		String subjectId = getParam("subjectId");
		context.put("subjectId", subjectId);
		Subject subject = db.find(Subject.class, subjectId);
		context.put("subject", subject);
		
		List<ClassroomTemplate> assignedClassrooms  = db.list("select c from ClassroomTemplate c where c.subject.id = '" + subjectId + "' order by c.section.sequence");
		
		//remove
		String[] removeIds = request.getParameterValues("removeClassroomIds");
		if ( removeIds != null ) {
			List<ClassroomTemplate> removes = new ArrayList<ClassroomTemplate>();
			for ( ClassroomTemplate c : assignedClassrooms ) {
				boolean f = false;
				for ( String id : removeIds ) {
					if ( id.equals(c.getId())) {
						f = true;
						break;
					}
				}
				if ( f ) {
					removes.add(c);
				}
			}
			for ( ClassroomTemplate c : removes ) {
				db.begin();
				db.remove(c);
				db.commit();
			}
		}

		//add classroom
		String[] classroomIds = request.getParameterValues("classroomIds");
		if ( classroomIds != null ) {
			for ( String classroomId : classroomIds ) {
				ClassroomSection classroomSection = db.find(ClassroomSection.class, classroomId);
				
				db.begin();
				ClassroomTemplate ct = new ClassroomTemplate();
				ct.setDelete(true);
				ct.setFrequency(1);
				ct.setSection(classroomSection);
				ct.setSubject(subject);
				db.persist(ct);
				db.commit();
			}
		}

		return assignClassrooms();
		
	}	
	
	@Command("assignClassrooms2")
	public String assignClassrooms2() throws Exception {
		String subjectId = getParam("subjectId");
		context.put("subjectId", subjectId);
		Subject subject = db.find(Subject.class, subjectId);
		context.put("subject", subject);
		
		List<ClassroomTemplate> assignedClassrooms  = db.list("select c from ClassroomTemplate c where c.subject.id = '" + subjectId + "' order by c.section.sequence");
		context.put("assignedClassrooms", assignedClassrooms);
		
		List<ClassroomSetup> classrooms = new ArrayList<ClassroomSetup>();
		context.put("classrooms", classrooms);
		List<ClassroomSection> classroomSections = db.list("select r from ClassroomSection r order by r.sequence");
		
		for ( ClassroomSection c : classroomSections ) {
			ClassroomSetup cs = new ClassroomSetup();
			int freq = 0;
			for ( ClassroomTemplate t : assignedClassrooms ) 
				if ( t.getSection().getId().equals(c.getId())) freq++;
			cs.setSection(c);
			cs.setFrequency(freq);
			classrooms.add(cs);
		}
		
		return path + "/assignClassrooms2.vm";
	}
	
	@Command("updateAssignClassrooms2")
	public String updateAssignClassrooms2() throws Exception {
		String subjectId = getParam("subjectId");
		context.put("subjectId", subjectId);
		Subject subject = db.find(Subject.class, subjectId);
		context.put("subject", subject);
		
		db.begin();
		db.executeUpdate("delete from ClassroomTemplate c where c.subject.id = '" + subjectId + "'");
		db.commit();
		
		String[] classroomIds = request.getParameterValues("classroomIds_" + subjectId);
		if ( classroomIds != null && classroomIds.length > 0 ) {
			
			for ( String classroomId : classroomIds ) {
				int freq = Integer.parseInt(getParam("freq_" + subjectId + "_" + classroomId));
				ClassroomSection classroomSection = db.find(ClassroomSection.class, classroomId);
				
				for ( int j =0; j < freq; j++ ) {
					db.begin();
					ClassroomTemplate ct = new ClassroomTemplate();
					ct.setDelete(true);
					ct.setSection(classroomSection);
					ct.setSubject(subject);
					ct.setFrequency(freq);
					ct.setSequence(j);
					db.persist(ct);
					db.commit();
				}
				
			}
			
		}
		return assignClassrooms2();

	}
	
	@Command("prerequisiteSubjects")
	public String prerequisiteSubjects() throws Exception {
		String subjectId = getParam("subjectId");
		context.put("subjectId", subjectId);
		Subject subject = db.find(Subject.class, subjectId);
		context.put("subject", subject);
		
		//list of other subjects
		List<Subject> subjects = new ArrayList<Subject>();
		context.put("subjects", subjects);
		List<Subject> subjects2 = db.list("select s from Subject s order by s.faculty.name, s.name");
		for ( Subject s : subjects2 ) {
			boolean f = false;
			for ( Subject p : subject.getPrerequisites() ) {
				if ( p.getId().equals(s.getId())) {
					f = true;
					break;
				}
			}
			if ( !f && !s.getId().equals(subject.getId())) subjects.add(s);
		}
		
		return path + "/assignPrerequisites.vm";
	}
	
	@Command("updatePrerequisiteSubjects")
	public String updatePrerequisiteSubjects() throws Exception {
		String subjectId = getParam("subjectId");
		context.put("subjectId", subjectId);
		Subject subject = db.find(Subject.class, subjectId);
		context.put("subject", subject);
		
		//remove first
		String[] removeIds = request.getParameterValues("removePrerequisiteIds");
		if ( removeIds != null ) {
			for ( String id : removeIds ) {
				Subject r = db.find(Subject.class, id);
				db.begin();
				subject.getPrerequisites().remove(r);
				db.commit();
			}
		}
		
		//add prerequisites
		String[] ids = request.getParameterValues("prerequisiteIds");
		if ( ids != null ) {
			for ( String id : ids ) {
				Subject r = db.find(Subject.class, id);
				db.begin();
				subject.getPrerequisites().add(r);
				db.commit();
			}
		}
		return prerequisiteSubjects();
	}
	
	@Command("setMainCampus")
	public String setMainCampus() throws Exception {
		String teacherSubjectId = getParam("teacherSubjectId");
		TeacherSubject teacherSubject = db.find(TeacherSubject.class, teacherSubjectId);
		
		String status = getParam("status");
		db.begin();
		teacherSubject.setMainCampus("yes".equals(status));
		db.commit();
		return path + "/empty.vm";
	}
	
	@Command("setOtherCampus")
	public String setOtherCampus() throws Exception {
		String teacherSubjectId = getParam("teacherSubjectId");
		TeacherSubject teacherSubject = db.find(TeacherSubject.class, teacherSubjectId);
		
		String status = getParam("status");
		db.begin();
		teacherSubject.setOtherCampus("yes".equals(status));
		db.commit();
		return path + "/empty.vm";
	}

}
