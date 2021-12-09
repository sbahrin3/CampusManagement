package educate.sis.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.velocity.VelocityContext;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.enrollment.entity.StudentStatus;
import lebah.portal.action.AjaxModule;

public class StudentInfoModule extends AjaxModule {
	
	String path = "apps/util/student_info/";
	private String vm = "default.vm";
	HttpSession session;
	DbPersistence db = new DbPersistence();
	boolean studentMode = false;
	
	public StudentInfoModule(HttpServletRequest request, VelocityContext context) {
		this.request = request;
		this.context = context;
	}

	
	@Override
	public String doAction() throws Exception {
		session = request.getSession();
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy"));
		context.put("timeFormat", new SimpleDateFormat("hh:mm a"));	
		context.put("numFormat", new DecimalFormat("#,###,###.00"));	
		String command = request.getParameter("command");
		System.out.println(command);
		if ( null == command || "".equals(command)) start();
		else if ( "get_student".equals(command)) getStudent();
		else if ( "get_student_by_id".equals(command)) getStudentById();
		return vm;
	}

	private void getStudent() throws Exception {
		vm = path + "student.vm";
		String studentNo = "";
		if ( studentMode ) {
			studentNo = (String) session.getAttribute("_portal_login");
		}
		else {
			studentNo = request.getParameter("student_no");
		}
		context.put("student_no", studentNo);
		Student student = (Student) db.get("select s from Student s where s.matricNo = '" + studentNo + "'");
		context.put("student", student);
		if ( student != null ) {
			StudentStatusUtil u = new StudentStatusUtil();
			StudentStatus studentStatus = u.getCurrentStudentStatus(student);
			context.put("student_status", studentStatus);
		}
		else {
			context.remove("student");
		}
		
	}
	
	private void getStudentById() throws Exception {
		vm = path + "student.vm";
		String studentNo = "";
		String studentId = request.getParameter("student_id");
		Student student = db.find(Student.class, studentId);
		context.put("student", student);
		if ( student != null ) {
			StudentStatusUtil u = new StudentStatusUtil();
			StudentStatus studentStatus = u.getCurrentStudentStatus(student);
			context.put("student_status", studentStatus);
		}
		else {
			context.remove("student");
		}
		
	}	

	private void start() {
		vm = path + "start.vm";
		
	}

}
