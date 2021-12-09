/**
 * 
 */
package educate.sis.module;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import educate.db.DbPersistence;
import educate.sis.struct.entity.Session;
import educate.sis.struct.entity.StudentEnrollmentIntake;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

/**
 * @author Shamsul Bahrin bin Abd Mutalib
 *
 */
public class SetupStudentEnrollmentIntakeModule extends LebahModule {
	
	private DbPersistence db = new DbPersistence();
	private String path = "apps/setupStudentEnrollmentIntake";
	
	public void preProcess() {
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy"));
		System.out.println("command: " + command);
	}

	/* (non-Javadoc)
	 * @see lebah.portal.action.LebahModule#start()
	 */
	@Override
	public String start() {
		String pathNo = "0";
		List<Session> sessions = db.list("select s from Session s where s.pathNo = " + pathNo + " order by s.startDate desc");
		context.put("sessions", sessions);
		return path + "/start.vm";
	}
	
	@Command("listSessions")
	public String listSessions() throws Exception {
		String pathNo = getParam("pathNo");
		List<Session> sessions = db.list("select s from Session s where s.pathNo = " + pathNo + " order by s.startDate desc");
		context.put("sessions", sessions);
		return path + "/list.vm";
	}
	
	@Command("addEnrollmentIntake")
	public String addEnrollmentIntake() throws Exception {
		String sessionId = getParam("sessionId");
		Session session = db.find(Session.class, sessionId);
		context.remove("enrollmentIntake");
		context.put("session", session);
		return path + "/addEnrollmentIntake.vm";
	}
	
	@Command("editEnrollmentIntake")
	public String editEnrollmentIntake() throws Exception {
		String enrollmentIntakeId = getParam("enrollmentIntakeId");
		StudentEnrollmentIntake enrollmentIntake = db.find(StudentEnrollmentIntake.class, enrollmentIntakeId);
		context.put("enrollmentIntake", enrollmentIntake);
		context.put("session", enrollmentIntake.getSession());
		return path + "/addEnrollmentIntake.vm";
	}
	
	@Command("saveEnrollmentIntake")
	public String saveEnrollmentIntake() throws Exception {
		String sessionId = getParam("sessionId");
		Session session = db.find(Session.class, sessionId);
		
		boolean add = false;
		String enrollmentIntakeId = getParam("enrollmentIntakeId");
		StudentEnrollmentIntake intake = db.find(StudentEnrollmentIntake.class, enrollmentIntakeId);
		
		System.out.println("enrollmentIntakeId = " + enrollmentIntakeId);
		System.out.println(intake);
		
		Date date = new SimpleDateFormat("dd-MM-yyyy").parse(getParam("date"));
		
		if ( intake == null ) {
			intake = new StudentEnrollmentIntake();
			add = true;
		}
		
		String code = getParam("code");
		String name = getParam("name");
		if ( "".equals(code)) code = getParam("date");
		if ( "".equals(name)) name = code;
		
		intake.setCode(code);
		intake.setName(name);
		intake.setDate(date);
		db.begin();
		if ( add ) {
			session.getEnrollmentIntakes().add(intake);
			intake.setSession(session);
			db.persist(intake);
		}
		db.commit();
		
		context.put("s", session);
		return path + "/saveEnrollmentIntake.vm";
	}
	
	
	@Command("deleteEnrollmentIntake")
	public String deleteEnrollmentIntake() throws Exception {
		String sessionId = getParam("sessionId");
		Session session = db.find(Session.class, sessionId);
		
		String enrollmentIntakeId = getParam("enrollmentIntakeId");
		StudentEnrollmentIntake intake = db.find(StudentEnrollmentIntake.class, enrollmentIntakeId);
		
		if ( db.list("select s from Student s where s.enrollmentIntake.id = '" + intake.getId() + "'").size() == 0 ) {
		
			db.begin();
			db.remove(intake);
			db.commit();
			session.getEnrollmentIntakes().remove(intake);

		
		}
		
		context.put("s", session);
		return path + "/saveEnrollmentIntake.vm";
	}

}
