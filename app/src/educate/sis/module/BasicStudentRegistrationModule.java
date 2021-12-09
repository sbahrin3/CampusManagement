package educate.sis.module;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.http.HttpSession;

import educate.db.DbPersistence;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.Session;
import lebah.portal.action.AjaxModule;

public class BasicStudentRegistrationModule  extends AjaxModule  {
	
	String path = "apps/util/registration/";
	private String vm = "default.vm";
	HttpSession session;
	DbPersistence db = new DbPersistence();

	
	@Override
	public String doAction() throws Exception {
		context.put("_formName", formName);
		session = request.getSession();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		context.put("dateFormat", dateFormat);
		context.put("programUtil", new ProgramUtil());
		String command = request.getParameter("command");
		System.out.println(command);
		if ( null == command || "".equals(command)) listPrograms();
		else if ( "list_intakes".equals(command)) listIntakes();
		return vm;
	}

	
	private void listIntakes() {
		vm = path + "list_programs.vm";
		String programId = request.getParameter("program_id");
		if ( "".equals(programId)) {
			listPrograms();
			return;
		}
		context.put("program_id", programId);
		Program program = db.find(Program.class, programId);
		List<Session> intakes = db.list("select s from Session s where s.pathNo = " + program.getLevel().getPathNo() + " order by s.startDate");
		context.put("intakes", intakes);
		session.setAttribute("intakes", intakes);
		
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
			context.remove("intake_id");
		}
		
		params();
		
	}
	
	private void listPrograms() {
		vm = path + "list_programs.vm";
		context.remove("intakes");
		params();
	}
	
	
	private void params() {
		List<Program> programs = db.list("SELECT a from Program a order by a.code");
		context.put("programs",programs);
		session.setAttribute("programs", programs);
	}

}
