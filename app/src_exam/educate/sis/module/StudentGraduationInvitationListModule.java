package educate.sis.module;

import java.util.List;

import educate.db.DbPersistence;
import educate.sis.struct.entity.Graduate;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.Session;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class StudentGraduationInvitationListModule extends LebahModule {
	
	private DbPersistence db = new DbPersistence();
	private String path = "apps/util/student_graduation2";

	@Override
	public String start() {
		List<Program> programs = db.list("select p from Program p");
		context.put("programs", programs);
		return path + "/start.vm";
	}
	
	@Command("listSessions")
	public String listSessions() throws Exception {
		String programId = getParam("programId");
		Program program = db.find(Program.class, programId);
		int pathNo = program.getLevel().getPathNo();
		List<Session> sessions = db.list("select s from Session s where s.pathNo = " + pathNo + " order by s.startDate");
		context.put("sessions", sessions);
		return path + "/listSessions.vm";
	}
	
	@Command("listStudents")
	public String listStudents() throws Exception {
		String programId = getParam("programId");
		String sessionId = getParam("sessionId");
		
		List<Graduate> graduates = db.list("select g from Graduate g where g.student.intake.id = '" + sessionId + "' and g.student.program.id = '" + programId + "'");
		context.put("graduates", graduates);
		
		context.put("util", new lebah.util.Util());
		
		return path + "/listStudents.vm";
	}	
	


}
