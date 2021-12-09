package educate.sis.module;

import java.util.List;

import educate.sis.exam.entity.MarkingGrade;
import educate.sis.struct.entity.Faculty;
import educate.sis.struct.entity.Subject;
import educate.sis.struct.entity.SubjectGroup;
import lebah.portal.action.Command;

public class SubjectPropertiesModule extends SetupFacultySubjectsModule {
	
	@Override
	public String start() {
		
		return path + "/subject_properties/start.vm";
	}
	
	@Command("getSubject")
	public String getSubject() throws Exception {
		String subjectCode = getParam("subjectCode");
		List<Subject> subjects = db.list("select s from Subject s where s.code = '" + subjectCode + "'");
		context.put("subjects", subjects);
		return path + "/subject_properties//getSubject.vm";
	}
	
	@Command("getSubjectProperties")
	public String getSubjectProperties() throws Exception {
		String subjectId = getParam("subjectId");
		Subject subject = db.find(Subject.class, subjectId);
		context.put("subject", subject);
		
		MarkingGrade markingGrade = subject.getMarkingGrade();
		Faculty faculty = subject.getFaculty();
		
		//list markingGrade
		List<MarkingGrade> markingGrades = db.list("select g from MarkingGrade g");
		context.put("markingGrades", markingGrades);
		
		//list faculties
		List<Faculty> faculties = db.list("select f from Faculty f order by f.name");
		context.put("faculties", faculties);

		//list subject groups
		if ( faculty != null ) {
			List<SubjectGroup> subjectGroups = db.list("select g from SubjectGroup g where g.faculty.id = '" + faculty.getId() + "'");
			context.put("subjectGroups", subjectGroups);
		}
		else context.remove("subjectGroups");
		
		return path + "/subject_properties//getSubjectProperties.vm";
	}
	
	@Command("listSubjectGroups")
	public String listSubjectGroups() throws Exception {
		String facultyId = getParam("facultyId");
		List<SubjectGroup> subjectGroups = db.list("select g from SubjectGroup g where g.faculty.id = '" + facultyId + "'");
		context.put("subjectGroups", subjectGroups);
		
		String subjectId = getParam("subjectId");
		Subject subject = db.find(Subject.class, subjectId);
		context.put("subject", subject);
		
		return path + "/subject_properties//listSubjectGroups.vm";
	}
	
	@Command("updateSubjectProperties")
	public String updateSubjectProperties() throws Exception {
		String subjectId = getParam("subjectId");
		Subject subject = db.find(Subject.class, subjectId);
		context.put("subject", subject);
		
		
		String facultyId = getParam("facultyId");
		Faculty faculty = db.find(Faculty.class, facultyId);
		
		String subjectGroupId = getParam("subjectGroupId");
		SubjectGroup subjectGroup = db.find(SubjectGroup.class, subjectGroupId);
		
		String markingGradeId = getParam("markingGradeId");
		MarkingGrade markingGrade = db.find(MarkingGrade.class, markingGradeId);
		
		String subjectCode = getParam("subjectCode");
		String subjectName = getParam("subjectName");
		String subjectAltName = getParam("subjectAltName");
		String shortName = getParam("shortName");
		
		int excludeGpa = Integer.parseInt(getParam("excludeGpa"));
		
		
		db.begin();
		subject.setCode(subjectCode);
		subject.setName(subjectName);
		subject.setAltName(subjectAltName);
		subject.setShortName(shortName);
		subject.setFaculty(faculty);
		subject.setSubjectGroup(subjectGroup);
		subject.setMarkingGrade(markingGrade);
		subject.setExcludeGpa(excludeGpa);
		db.commit();
		
		return getSubjectProperties();

	}

}
