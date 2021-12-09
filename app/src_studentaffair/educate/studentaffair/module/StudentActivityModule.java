package educate.studentaffair.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.enrollment.entity.StudentStatus;
import educate.sis.module.StudentStatusUtil;
import educate.studentaffair.entity.Club;
import educate.studentaffair.entity.StudentClub;
import educate.studentaffair.entity.StudentClubPosition;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class StudentActivityModule extends LebahModule {
	
	private String path = "studentaffair/student";
	private DbPersistence db = new DbPersistence();

	@Override
	public String start() {
		return path + "/start.vm";
	}
	
	public void preProcess() {
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy"));
		context.put("timeFormat", new SimpleDateFormat("hh:mm a"));	
		context.put("numFormat", new DecimalFormat("#,###,###.00"));
	}
	
	@Command("getStudent")
	public String getStudent() throws Exception {
		String matricNo = getParam("matricNo");
		Student student = (Student) db.get("select s from Student s where s.matricNo = '" + matricNo + "'");
		context.put("student", student);
		StudentStatusUtil u = new StudentStatusUtil();
		StudentStatus studentStatus = u.getCurrentStudentStatus(student);
		if ( studentStatus == null ) context.remove("student_status");
		else context.put("studentStatus", studentStatus);
		
		List<StudentClub> studentClubs = db.list("select s from StudentClub s where s.student.id = '" + student.getId() + "'");
		context.put("studentClubs", studentClubs);
		
		return path + "/student.vm";
	}

	@Command("addClub")
	public String addClub() throws Exception {
		String studentId = getParam("studentId");
		Student student = db.find(Student.class, studentId);
		context.put("student", student);
		context.put("clubs", db.list("select c from Club c"));
		context.put("positions", db.list("select p from StudentClubPosition p order by p.sequence"));
		context.remove("studentClub");
		return path + "/addClub.vm";
	}
	
	
	@Command("getStudentClub")
	public String getStudentClub() throws Exception {
		String studentClubId = getParam("studentClubId");
		StudentClub studentClub = db.find(StudentClub.class, studentClubId);
		context.put("studentClub", studentClub);
		context.put("student", studentClub.getStudent());
		context.put("clubs", db.list("select c from Club c"));
		context.put("positions", db.list("select p from StudentClubPosition p order by p.sequence"));		
		return path + "/addClub.vm";
	}
	
	@Command("studentClub")
	public String studentClub() throws Exception {
		String studentId = getParam("studentId");
		Student student = db.find(Student.class, studentId);
		context.put("student", student);
		
		String clubId = getParam("clubId");
		Club club = db.find(Club.class, clubId);
		
		if ( db.list("select s from StudentClub s where s.student.id = '" + studentId + "' and s.club.id = '" + clubId + "'").size() == 0 ) {
		
			String positionId = getParam("positionId");
			StudentClubPosition position = db.find(StudentClubPosition.class, positionId);
			
			String dateJoined = getParam("dateJoined");
			Date date = new SimpleDateFormat("dd-MM-yyyy").parse(dateJoined);
			
			db.begin();
			StudentClub sc = new StudentClub();
			sc.setStudent(student);
			sc.setClub(club);
			sc.setPosition(position);
			sc.setDateJoined(date);
			db.persist(sc);
			db.commit();
			
		}

		List<StudentClub> studentClubs = db.list("select s from StudentClub s where s.student.id = '" + studentId + "'");
		context.put("studentClubs", studentClubs);
		return path + "/studentClub.vm";
	}
	
	
	@Command("saveStudentClub")
	public String saveStudentClub() throws Exception {
		String studentClubId = getParam("studentClubId");
		StudentClub studentClub = db.find(StudentClub.class, studentClubId);
		
		String clubId = getParam("clubId");
		Club club = db.find(Club.class, clubId);
		
		String positionId = getParam("positionId");
		StudentClubPosition position = db.find(StudentClubPosition.class, positionId);
		
		String dateJoined = getParam("dateJoined");
		Date date = new SimpleDateFormat("dd-MM-yyyy").parse(dateJoined);
		
		db.begin();
		studentClub.setClub(club);
		studentClub.setPosition(position);
		studentClub.setDateJoined(date);
		db.commit();
		
		List<StudentClub> studentClubs = db.list("select s from StudentClub s where s.student.id = '" + studentClub.getStudent().getId() + "'");
		context.put("studentClubs", studentClubs);
		return path + "/studentClub.vm";
	}

	
}
