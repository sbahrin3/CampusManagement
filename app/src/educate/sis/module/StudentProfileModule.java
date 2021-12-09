package educate.sis.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpSession;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.enrollment.entity.StudentStatus;
import lebah.portal.action.AjaxModule;

public class StudentProfileModule extends AjaxModule {
	
	String path = "apps/util/student_profile/";
	private String vm = "default.vm";
	HttpSession session;
	DbPersistence db = new DbPersistence();
	protected boolean studentMode = false;

	
	@Override
	public String doAction() throws Exception {
		context.put("_formName", formName);
		session = request.getSession();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		context.put("dateFormat", dateFormat);
		context.put("numFormat", new DecimalFormat("#,###,###.00"));
		context.put("programUtil", new ProgramUtil());
		String command = request.getParameter("command");
		
		if ( null == command || "".equals(command)) start();
		else if ( "get_student".equals(command)) getStudent();
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
		if ( student != null ) {
			context.put("student", student);
			StudentStatusUtil u = new StudentStatusUtil();
			StudentStatus currentStatus = u.getCurrentStudentStatus(student);
			context.put("student_status", currentStatus);
		}
		else {
			context.remove("student");
		}
		
	}

	private void start() {
		vm = path + "start.vm";
		
	}

}
