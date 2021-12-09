package educate.sis.module;

import java.util.List;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.enrollment.entity.StudentStatus;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class ExamTranscriptBarringModule extends LebahModule {
	
	private String path = "apps/exam_barring";
	private DbPersistence db = new DbPersistence();

	@Override
	public String start() {
		List<Student> students = db.list("select s from Student s where s.barredExamTranscript = 1 order by s.biodata.name");
		context.put("students", students);
		return path + "/start.vm";
	}
	
	@Command("getStudent")
	public String getStudent() throws Exception {
		String matricNo = getParam("matric_no");
		Student student = (Student) db.get("select s from Student s where s.matricNo = '" + matricNo + "'");
		if ( student == null ) {
			return path + "/no_student.vm";
		}
		context.put("student", student);
		
		StudentStatusUtil u = new StudentStatusUtil();
		StudentStatus studentStatus = u.getCurrentStudentStatus(student);
		context.put("student_status", studentStatus);
		
		return path + "/getStudent.vm";
		
	}
	
	@Command("barStudent")
	public String barStudent() throws Exception {
		String studentId = getParam("studentId");
		Student student = db.find(Student.class, studentId);
		db.begin();
		student.setBarredExamTranscript(true);
		db.commit();
		
		return listBarredStudents();
	}
	
	@Command("listBarredStudents")
	public String listBarredStudents() throws Exception {
		List<Student> students = db.list("select s from Student s where s.barredExamTranscript = 1 order by s.biodata.name");
		context.put("students", students);
		return path + "/listBarredStudents.vm";
	}
	
	@Command("unbarStudents")
	public String unbarStudents() throws Exception {
		String[] studentIds = request.getParameterValues("studentIds");
		if ( studentIds != null ) {
			db.begin();
			for ( String studentId : studentIds ) {
				Student student = db.find(Student.class, studentId);
				student.setBarredExamTranscript(false);
			}
			db.commit();
		}
		return listBarredStudents();
	}

}
