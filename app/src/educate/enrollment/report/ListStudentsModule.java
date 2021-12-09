package educate.enrollment.report;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.sis.struct.entity.Program;
import lebah.portal.action.AjaxModule;

public class ListStudentsModule extends AjaxModule {
	
	private final String path = "apps/util/student_list/";
	private String vm = "default.vm";
	DbPersistence db = new DbPersistence();

	
	@Override
	public String doAction() throws Exception {
		context.put("_formName", formName);
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		context.put("dateFormat", dateFormat);
		String command = request.getParameter("command");
		if ( null == command || "".equals(command)) listPrograms();
		else if ( "list_students".equals(command)) listStudents();
		return vm;
	}

	private void listStudents() {
		vm = path + "report.vm";
		String programId = request.getParameter("program_id");
		if ( !"".equals(programId)) {
			Hashtable param = new Hashtable();
			param.put("date", new Date());
			param.put("program_id", programId);
			Program program = db.find(Program.class, programId);
			context.put("program", program);
			String sql = "";
			sql = "SELECT DISTINCT s FROM Student s JOIN s.status st " +
			"WHERE (s.fakeStudent is null OR s.fakeStudent = '') "+
			"and :date between st.session.startDate and st.session.endDate " +
			"and s.program.id = :program_id";
			List<Student> students = db.list(sql, param);
			context.put("students", students);
		}
		else {
			context.remove("students");
		}
	}

	private void listPrograms() {
		vm = path + "report.vm";
		List<Program> programs = db.list("select p from Program p order by p.code");
		context.put("programs", programs);
	}

}
