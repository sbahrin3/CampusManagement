package educate.sis.module;

import org.apache.velocity.VelocityContext;

import educate.db.DbPersistence;

public class ApplicationParam {
	
	public static void params(DbPersistence db, VelocityContext context) {
		context.put("grads", db.list("SELECT a FROM GradsLevel a ORDER BY a.code"));
		context.put("races", db.list("SELECT a from Race a"));
		context.put("states", db.list("SELECT a from State a") );
		context.put("countries", db.list("SELECT a from Country a"));
		context.put("nationalities",db.list("SELECT a from Nationality a order by a.name"));
		context.put("genders", db.list("SELECT a FROM Gender a"));
		context.put("subjects", db.list("SELECT a from SchoolSubject a WHERE a.stype='SPM'"));
		context.put("subjectsSTPM", db.list("SELECT a from SchoolSubject a WHERE a.stype='STPM'"));
		context.put("studylevels",db.list("SELECT a from StudyLevel a"));
		context.put("certs",db.list("SELECT a from ProfesionalQualification a"));
		context.put("status",db.list("SELECT a from EmploymentStatus a"));
		context.put("sectors",db.list("SELECT a from Sector a"));
		context.put("disabilities",db.list("SELECT a from Disability a"));
		context.put("sponsors",db.list("SELECT a from Sponsor a"));
		context.put("surveys",db.list("SELECT a from Survey a order by a.code"));
		context.put("centres",db.list("SELECT a from LearningCentre a"));
		context.put("methods",db.list("SELECT a FROM PaymentMethod a"));
		context.put("positions",db.list("SELECT a FROM Position a"));
		context.put("degreeClass",db.list("SELECT a FROM DegreeClass a"));
		context.put("educationLevels",db.list("SELECT a FROM EducationLevel a"));
		context.put("examGrades",db.list("SELECT a FROM GeneralExamGrade a ORDER BY a.name"));
		context.put("researchAreas",db.list("SELECT a FROM ResearchArea a"));
		context.put("UnderGradsPrograms", db.list("select x from Program x order by x.code"));
		
		context.put("modes",db.list("SELECT a from ModeOfStudy a"));
		context.put("venues",db.list("SELECT a from RegVanue a"));
		context.put("sessions",db.list("SELECT a FROM Session a"));
	}

}
