package educate.sis.module;

import java.util.List;

import educate.db.DbPersistence;
import educate.sis.struct.entity.Graduate;
import educate.sis.struct.entity.LearningCentre;
import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.Session;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class StudentGraduationListReportModule extends LebahModule {
	
	private DbPersistence db = new DbPersistence();
	private String path = "apps/util/student_graduation3";

	@Override
	public String start() {
		context.put("programs", db.list("select p from Program p order by p.code"));
		context.put("centres", db.list("select c from LearningCentre c order by c.code"));
		return path + "/start.vm";
	}
	
	@Command("listIntakes")
	public String listIntakes() throws Exception {
		String programId = getParam("programId");
		Program program = db.find(Program.class, programId);
		if ( program != null && program.getLevel() != null ) {
			List<Session> intakes = db.list("select s from Session s where s.pathNo = " + program.getLevel().getPathNo() + " order by s.startDate");
			context.put("intakes", intakes);
		} else
			context.remove("intakes");
		return path + "/intakes.vm";
	}
	
	@Command("listGraduates")
	public String listGraduates() throws Exception {
		String programId = getParam("programId");
		String intakeId = getParam("intakeId");
		String centreId = getParam("centreId");
		listGraduates(programId, intakeId, centreId);
		return path + "/listGraduates.vm";
	}
	
	private void listGraduates(String programId, String intakeId, String centreId) throws Exception {
		
		context.put("program", db.find(Program.class, programId));
		context.put("intake", db.find(Session.class, intakeId));
		context.put("centre", db.find(LearningCentre.class, centreId));
		
		String sql = "select f from Graduate f where (f.student.fakeStudent is null OR f.student.fakeStudent = '')";
		sql += " and f.student.program.id = '" + programId + "'";
		sql += " and f.student.intake.id = '" + intakeId + "' ";
		sql += " and f.student.learningCenter.id = '" + centreId + "' ";
		sql += " and f.clearance = 1";
		List<Graduate> graduates = db.list(sql);
		
		int total = graduates.size();
		
		//attend rehearsal
		sql = "select count(f) from Graduate f where (f.student.fakeStudent is null OR f.student.fakeStudent = '')";
		sql += " and f.student.program.id = '" + programId + "'";
		sql += " and f.student.intake.id = '" + intakeId + "' ";
		sql += " and f.student.learningCenter.id = '" + centreId + "' ";
		sql += " and f.clearance = 1";
		sql += " and f.attendRehearsal = 1";
		
		long attendRehearsal = (Long) db.get(sql);
		long notAttendRehearsal = total - attendRehearsal;
		//attend ceremony
		sql = "select count(f) from Graduate f where (f.student.fakeStudent is null OR f.student.fakeStudent = '')";
		sql += " and f.student.program.id = '" + programId + "'";
		sql += " and f.student.intake.id = '" + intakeId + "' ";
		sql += " and f.student.learningCenter.id = '" + centreId + "' ";
		sql += " and f.clearance = 1";
		sql += " and f.attendCeremony = 1";
		
		long attendCeremony = (Long) db.get(sql);
		long notAttendCeremony = total - attendCeremony;
		
		context.put("graduates", graduates);
		context.put("attendRehearsal", attendRehearsal);
		context.put("notAttendRehearsal", notAttendRehearsal);
		context.put("attendCeremony", attendCeremony);
		context.put("notAttendCeremony", notAttendCeremony);
		context.put("total", total);
		
		request.getSession().setAttribute("graduates", graduates);
		request.getSession().setAttribute("attendRehearsal", attendRehearsal);
		request.getSession().setAttribute("notAttendRehearsal", notAttendRehearsal);
		request.getSession().setAttribute("attendCeremony", attendCeremony);
		request.getSession().setAttribute("notAttendCeremony", notAttendCeremony);
		request.getSession().setAttribute("total", total);
		
		
	}

}
