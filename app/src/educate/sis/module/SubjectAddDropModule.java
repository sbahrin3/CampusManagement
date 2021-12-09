package educate.sis.module;

import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.http.HttpSession;

import educate.db.DbPersistence;
import educate.enrollment.StudentRegistrationUtil;
import educate.enrollment.entity.Student;
import lebah.portal.action.AjaxModule;

public class SubjectAddDropModule  extends AjaxModule  {
	
	String path = "apps/util/graduation_setup/";
	private String vm = "default.vm";
	HttpSession session;
	DbPersistence db = new DbPersistence();

	
	@Override
	public String doAction() throws Exception {
		context.put("_formName", formName);
		session = request.getSession();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		context.put("dateFormat", dateFormat);

		String command = request.getParameter("command");
		System.out.println(command);
		
		
		
		
		return vm;
	}
	
	public static void main(String[] args) throws Exception {
		DbPersistence db = new DbPersistence();
		String semester = StudentRegistrationUtil.getCurrentSession().getCode();
		System.out.println(semester);
		Hashtable param = new Hashtable();
		param.put("semester", semester);
		List<Student> list = db.list("select x.student from SubjectAddDrop x where x.status='IN_PROCESS' and x.act='ADD' and x.semester=:semester group by x.student.id", param);
		for ( Student student : list ) {
			System.out.println(student.getBiodata().getName());
		}

	}

}
