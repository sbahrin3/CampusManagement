package educate.sis.module;

import java.util.List;

import educate.db.DbPersistence;
import educate.sis.general.entity.GradsLevel;
import educate.sis.struct.entity.Course;
import educate.sis.struct.entity.Faculty;
import educate.sis.struct.entity.Institution;
import educate.sis.struct.entity.PeriodScheme;
import educate.sis.struct.entity.Program;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class SetupFacultyStructureModule extends LebahModule {
	
	private String path = "apps/setup_faculty_structure";
	private DbPersistence db = new DbPersistence();
	
	public void preProcess() {
		System.out.println(command);
		context.put("path", path);
		
		String serverName = request.getServerName();
		int serverPort = request.getServerPort();
        String server = serverPort != 80 ? serverName + ":" + serverPort : serverName;
        String http = request.getRequestURL().toString().substring(0, request.getRequestURL().toString().indexOf("://") + 3);
        String serverUrl = http + server;
        context.put("serverUrl", serverUrl);
        String uri = request.getRequestURI();
        String appname = uri.substring(1);
        appname = appname.substring(0, appname.indexOf("/"));
        context.put("appUrl", serverUrl.concat("/").concat(appname)); 
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

	@Command("getCourses")
	public String getCourses() throws Exception {
		String facultyId = getParam("getFacultyId");
		Faculty faculty = db.find(Faculty.class, facultyId);
		context.put("faculty", faculty);
		List<Course> courses = db.list("select c from Course c where c.faculty.id = '" + facultyId + "'");
		context.put("courses", courses);
		return path + "/listCourses.vm";
	}
	
	@Command("getPrograms")
	public String getPrograms() throws Exception {
		String courseId = getParam("getCourseId");
		Course course = db.find(Course.class, courseId);
		context.put("course", course);
		List<Program> programs = db.list("select p from Program p where p.course.id = '" + courseId + "'");
		context.put("programs", programs);
		return path + "/listPrograms.vm";
	}
	
	@Command("entryFaculty")
	public String entryFaculty() throws Exception {
		String institutionId = getParam("institutionId");
		context.put("institutionId", institutionId);
		return path + "/entryFaculty.vm";
	}
	
	@Command("editFaculty")
	public String editFaculty() throws Exception {
		String institutionId = getParam("institutionId");
		context.put("institutionId", institutionId);
		String facultyId = getParam("facultyId");
		context.put("facultyId", facultyId);
		Faculty faculty = db.find(Faculty.class, facultyId);
		context.put("faculty", faculty);
		return path + "/editFaculty.vm";
	}
	
	
	@Command("saveFaculty")
	public String saveFaculty() throws Exception {
		String facultyId = getParam("facultyId");
		context.put("facultyId", facultyId);
		Faculty faculty = db.find(Faculty.class, facultyId);
		db.begin();
		faculty.setCode(getParam("facultyCode_" + facultyId));
		faculty.setName(getParam("facultyName_" + facultyId));
		faculty.setMatricCardColor(getParam("matricCardColor_" + facultyId));
		faculty.setMatricTemplateName(getParam("matricTemplateName_" + facultyId));
		db.commit();
		String institutionId = faculty.getInstitution().getId();
		List<Faculty> faculties = db.list("select c from Faculty c where c.institution.id = '" + institutionId + "'");
		context.put("faculties", faculties);
		return path + "/divListFaculties.vm";
	}
	
	@Command("addFaculty")
	public String addFaculty() throws Exception {
		String institutionId = getParam("institutionId");
		context.put("institutionId", institutionId);
		Institution institution = db.find(Institution.class, institutionId);
		context.put("institution", institution);
		
		db.begin();
		Faculty faculty = new Faculty();
		faculty.setInstitution(institution);
		faculty.setCode(getParam("facultyCode"));
		faculty.setName(getParam("facultyName"));
		db.persist(faculty);
		db.commit();
		
		List<Faculty> faculties = db.list("select c from Faculty c where c.institution.id = '" + institutionId + "'");
		context.put("faculties", faculties);
		return path + "/divListFaculties.vm";
		
	}
	
	@Command("deleteFaculty")
	public String deleteFaculty() throws Exception {
		String facultyId = getParam("facultyId");
		context.put("facultyId", facultyId);

		String institutionId = getParam("institutionId");
		context.put("institutionId", institutionId);
		
		int cnt = 0;
		cnt = db.list("select c from Course c where c.faculty.id = '" + facultyId + "'").size();
		
		if ( cnt == 0 ) {
			Faculty faculty = db.find(Faculty.class, facultyId);
			db.begin();
			db.remove(faculty);
			db.commit();
		} else {
			System.out.println("Can't delete Faculty");
		}
		
		//List<Faculty> faculties = db.list("select c from Faculty c where c.institution.id = '" + institutionId + "'");
		List<Faculty> faculties = db.list("select c from Faculty c");
		context.put("faculties", faculties);
		return path + "/divListFaculties.vm";
	}

	
	
	@Command("entryCourse")
	public String entryCourse() throws Exception {
		String facultyId = getParam("facultyId");
		context.put("facultyId", facultyId);
		return path + "/entryCourse.vm";
	}
	
	@Command("editCourse")
	public String editCourse() throws Exception {
		String facultyId = getParam("facultyId");
		context.put("facultyId", facultyId);

		String courseId = getParam("courseId");
		context.put("courseId", courseId);
		
		Course course = db.find(Course.class, courseId);
		context.put("course", course);
		
		return path + "/editCourse.vm";
	}
	
	@Command("saveCourse")
	public String saveCourse() throws Exception {
		String facultyId = getParam("facultyId");
		context.put("facultyId", facultyId);

		String courseId = getParam("courseId");
		context.put("courseId", courseId);
		
		Course course = db.find(Course.class, courseId);
		db.begin();
		course.setCode(getParam("courseCode_" + courseId));
		course.setName(getParam("courseName_" + courseId));
		if ( getParam("matricTemplateName_" + courseId).equals(course.getFaculty().getMatricTemplateName()))
			course.setMatricTemplateName("");
		else
			course.setMatricTemplateName(getParam("matricTemplateName_" + courseId));
		
		db.commit();
		
		List<Course> courses = db.list("select c from Course c where c.faculty.id = '" + facultyId + "'");
		context.put("courses", courses);
		return path + "/divListCourses.vm";
	}
	
	@Command("addCourse")
	public String addCourse() throws Exception {
		String facultyId = getParam("facultyId");
		context.put("facultyId", facultyId);
		Faculty faculty = db.find(Faculty.class, facultyId);
		context.put("faculty", faculty);
		
		db.begin();
		Course course = new Course();
		course.setFaculty(faculty);
		course.setCode(getParam("courseCode"));
		course.setName(getParam("courseName"));
		db.persist(course);
		db.commit();
		
		List<Course> courses = db.list("select c from Course c where c.faculty.id = '" + facultyId + "'");
		context.put("courses", courses);
		return path + "/divListCourses.vm";
		
	}
	
	@Command("deleteCourse")
	public String deleteCourse() throws Exception {
		String facultyId = getParam("facultyId");
		context.put("facultyId", facultyId);

		String courseId = getParam("courseId");
		context.put("courseId", courseId);
		
		int cnt = 0;
		cnt = db.list("select p from Program p where p.course.id = '" + courseId + "'").size();
		
		if ( cnt == 0 ) {
			Course course = db.find(Course.class, courseId);
			db.begin();
			db.remove(course);
			db.commit();
		} else {
			System.out.println("Can't delete Course");
		}
		
		List<Course> courses = db.list("select c from Course c where c.faculty.id = '" + facultyId + "'");
		context.put("courses", courses);
		return path + "/divListCourses.vm";
	}


	
	@Command("entryProgram")
	public String entryProgram() throws Exception {
		context.put("schemes", db.list("select s from PeriodScheme s order by s.code"));
		context.put("levels", db.list("select l from GradsLevel l order by l.code"));
		context.put("courses", db.list("select c from Course c order by c.code"));
		String courseId = getParam("courseId");
		context.put("courseId", courseId);
		Course course = db.find(Course.class, courseId);
		context.put("course", course);
		return path + "/entryProgram.vm";
	}
	
	@Command("editProgram")
	public String editProgram() throws Exception {
		context.put("schemes", db.list("select s from PeriodScheme s order by s.code"));
		context.put("levels", db.list("select l from GradsLevel l order by l.code"));
		context.put("courses", db.list("select c from Course c order by c.code"));
		String programId = getParam("programId");
		context.put("programId", programId);
		Program program = db.find(Program.class, programId);
		context.put("program", program);
		String courseId = getParam("courseId");
		context.put("courseId", courseId);
		Course course = db.find(Course.class, courseId);
		context.put("course", course);
		
		int cnt = 0;
		cnt = db.list("select s from Student s where s.program.id = '" + programId + "'").size();
		//if ( cnt == 0 ) cnt = db.list("select p from ProgramStructure p where p.program.id = '" + programId + "'").size();
		if ( cnt == 0 ) context.remove("hasStudents");
		else context.put("hasStudents", true);
		
		return path + "/editProgram.vm";
	}
	
	@Command("saveProgram")
	public String saveProgram() throws Exception {
	
		String programId = getParam("programId");
		context.put("programId", programId);
		
		
		Course course = db.find(Course.class, getParam("course_" + programId));
		
		PeriodScheme scheme = db.find(PeriodScheme.class, getParam("periodScheme_" + programId));
		GradsLevel level = db.find(GradsLevel.class, getParam("programLevel_" + programId));
		
		Program program = db.find(Program.class, programId);
		context.put("program", program);
		
		int cnt = 0;
		cnt = db.list("select s from Student s where s.program.id = '" + programId + "'").size();
		//if ( cnt == 0 ) cnt = db.list("select p from ProgramStructure p where p.program.id = '" + programId + "'").size();
		
		db.begin();
		program.setCourse(course);
		program.setCode(getParam("programCode_" + programId));
		program.setName(getParam("programName_" + programId));
		
		program.setDisplayName(getParam("displayName_" + programId));
		program.setProgramLevelType(getParam("programLevelType_" + programId));
		if ( getParam("matricTemplateName_" + programId).equals(program.getCourse().getMatricTemplateName()))
			program.setMatricTemplateName("");
		else
			program.setMatricTemplateName(getParam("matricTemplateName_" + programId));
		
		//if there are students enrolled, these two attr. must be disabled
		if ( cnt == 0 ) {
			if ( scheme != null ) program.setPeriodScheme(scheme);
			if ( level != null ) program.setLevel(level);

		}
		
		boolean noneSessionType = "1".equals(getParam("noneSessionType_" + programId));
		program.setIsNoneSessionType(noneSessionType);
		
		db.commit();
		
		String courseId = getParam("courseId");
		context.put("courseId", courseId);
		List<Program> programs = db.list("select p from Program p where p.course.id = '" + courseId + "'");
		context.put("programs", programs);
		
		if ( cnt == 0 ) context.remove("hasStudents");
		else context.put("hasStudents", true);
		
		return path + "/divListPrograms.vm";
	}
	
	@Command("addProgram")
	public String addProgram() throws Exception {
		
		String courseId = getParam("courseId");
		Course course = db.find(Course.class, courseId);
		
		if ( course != null ) {
			PeriodScheme scheme = db.find(PeriodScheme.class, getParam("periodScheme"));
			GradsLevel level = db.find(GradsLevel.class, getParam("programLevel"));
			db.begin();
			Program program = new Program();
			program.setCourse(course);
			program.setCode(getParam("programCode"));
			program.setName(getParam("programName"));
			program.setProgramLevelType(getParam("programLevelType"));
			
			program.setDisplayName(getParam("displayName"));
			

			if ( scheme != null ) program.setPeriodScheme(scheme);
			if ( level != null ) program.setLevel(level);
			
			db.persist(program);
			db.commit();
		}
		
		List<Program> programs = db.list("select p from Program p where p.course.id = '" + courseId + "'");
		context.put("programs", programs);
		
		context.remove("hasStudents");
		
		return path + "/divListPrograms.vm";
	}
	
	
	@Command("deleteProgram")
	public String deleteProgram() throws Exception {
	
		String programId = getParam("programId");
		context.put("programId", programId);
		
		int cnt = 0;
		cnt = db.list("select s from Student s where s.program.id = '" + programId + "'").size();
		if ( cnt == 0 ) cnt = db.list("select p from ProgramStructure p where p.program.id = '" + programId + "'").size();
		
		if ( cnt == 0 ) {
			Program program = db.find(Program.class, programId);
			db.begin();
			db.remove(program);
			db.commit();
		}
		else {
			System.out.println("Can't delete Program ");
		}
		
		String courseId = getParam("courseId");
		context.put("courseId", courseId);
		List<Program> programs = db.list("select p from Program p where p.course.id = '" + courseId + "'");
		context.put("programs", programs);
		
		return path + "/divListPrograms.vm";
	}
	
	
}
