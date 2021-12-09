package educate.sis.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.enrollment.entity.StudentStatus;
import educate.sis.finance.entity.Sponsor;
import educate.sis.finance.entity.SponsorStudent;
import lebah.portal.action.AjaxModule;

public class SponsorStudentSetupModule extends AjaxModule {
	
	String path = "apps/sponsor_student/"; 
	private String vm = "start.vm";
	HttpSession session;
	DbPersistence db = new DbPersistence();

	@Override
	public String doAction() throws Exception {
		session = request.getSession();
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy"));
		context.put("timeFormat", new SimpleDateFormat("hh:mm a"));	
		context.put("numFormat", new DecimalFormat("#,###,###.00"));	
		String command = request.getParameter("command");
		System.out.println(command);
		if ( null == command || "".equals(command)) start();
		else if ( "get_students".equals(command)) getStudents();
		else if ( "find_student".equals(command)) findStudent();
		else if ( "select_students".equals(command)) selectStudents();
		else if ( "add_student".equals(command)) addStudent();
		else if ( "student_detail".equals(command)) studentDetail();
		else if ( "edit_student".equals(command)) editStudent();
		else if ( "delete_student".equals(command)) deleteStudent();
		return vm;
	}
	
	private void deleteStudent() throws Exception {
		
		String studentId = request.getParameter("student_id");
		String sponsorId = request.getParameter("current_sponsor_id");
		
		SponsorStudent sponsorStudent = (SponsorStudent) db.get("select s from SponsorStudent s where s.sponsor.id = '" + sponsorId + "' and s.student.id = '" + studentId + "'");
		if ( sponsorStudent != null ) {
			db.begin();
			db.remove(sponsorStudent);
			db.commit();
		}
		
		listSponsorStudents(sponsorId);
		
	}

	private void addStudent() throws Exception {
		// 
		String studentId = request.getParameter("student_id");
		String sponsorId = request.getParameter("current_sponsor_id");
		
		//look for existence of this pair first
		SponsorStudent sponsorStudent = (SponsorStudent) db.get("select s from SponsorStudent s where s.sponsor.id = '" + sponsorId + "' and s.student.id = '" + studentId + "'");
		
		String sponsorDateStart = getParam("sponsorDateStart");
		String sponsorDateEnd = getParam("sponsorDateEnd");
		String sponsorAmount = getParam("sponsorAmount");
		String sponsorRefNo = getParam("sponsorRefNo");
		
		Date dateStart = null;
		Date dateEnd = null;
		double amount = 0.0d;
		try {
			dateStart = new SimpleDateFormat("dd-MM-yyyy").parse(sponsorDateStart);
		} catch ( Exception e ) {}
		try {
			dateEnd = new SimpleDateFormat("dd-MM-yyyy").parse(sponsorDateEnd);
		} catch ( Exception e ) {}
		try {
			amount = Double.parseDouble(sponsorAmount);
		} catch ( Exception e ) {}


		
		if ( sponsorStudent == null ) {
			System.out.println("Creating pair of this sponsor and student.");
			Student student = db.find(Student.class, studentId);
			Sponsor sponsor = db.find(Sponsor.class, sponsorId);
			
			db.begin();
			
			SponsorStudent ss = new SponsorStudent();
			ss.setSponsor(sponsor);
			ss.setStudent(student);
			ss.setSponsorAmount(amount);
			ss.setSponsorDateStart(dateStart);
			ss.setSponsorDateEnd(dateEnd);
			ss.setSponsorRefNo(sponsorRefNo);

			db.persist(ss);
			db.commit();
			
			context.remove("sponsorStudent");
			
		}
		else {
			System.out.println("This pair of sponsor student existed!");
			System.out.println(sponsorStudent);
			context.put("sponsorStudent", sponsorStudent);
			
			db.begin();
			sponsorStudent.setSponsorAmount(amount);
			sponsorStudent.setSponsorDateStart(dateStart);
			sponsorStudent.setSponsorDateEnd(dateEnd);
			sponsorStudent.setSponsorRefNo(sponsorRefNo);
			db.commit();
			
			
		}
		
		listSponsorStudents(sponsorId);
		
	}

	private void listSponsorStudents(String sponsorId) {
		//get sponsor student list
		List sponsorStudents = db.list("select s from SponsorStudent s where s.sponsor.id = '" + sponsorId + "'");
		context.put("sponsor_students", sponsorStudents);
		
		vm = path + "list_students.vm";
	}
	
	private void editStudent() {
		
		String sponsorStudentId = getParam("sponsorStudentId");
		SponsorStudent sponsorStudent = db.find(SponsorStudent.class, sponsorStudentId);
		context.put("sponsorStudent", sponsorStudent);

		Student student = sponsorStudent.getStudent();
		context.put("student", student);
		
		context.remove("student_status");
		StudentStatus studentStatus = null;
		try {
			StudentStatusUtil u = new StudentStatusUtil();
			studentStatus = u.getCurrentStudentStatus(student);
			context.put("student_status", studentStatus);
		} catch (Exception e) {
			context.remove("student_status");
			e.printStackTrace();
		}
		if ( studentStatus == null ) {
			context.put("status_error", "Student Status Not Available!");
		}
		else {
			context.remove("status_error");
		}
		
		vm = path + "student_detail.vm";
		
	}

	private void studentDetail() {
		// 
		String id = request.getParameter("student_id");
		Student student = db.find(Student.class, id);
		context.put("student", student);
		
		context.remove("student_status");
		StudentStatus studentStatus = null;
		try {
			StudentStatusUtil u = new StudentStatusUtil();
			studentStatus = u.getCurrentStudentStatus(student);
			context.put("student_status", studentStatus);
		} catch (Exception e) {
			context.remove("student_status");
			e.printStackTrace();
		}
		if ( studentStatus == null ) {
			context.put("status_error", "Student Status Not Available!");
		}
		else {
			context.remove("status_error");
		}
		
		vm = path + "student_detail.vm";
		
	}



	private void selectStudents() {
		//
		String finder = request.getParameter("student_name");
		List<Student> students = new ArrayList<Student>();
		
		//look for matric number
		students = db.list("select s from Student s where s.matricNo like '%" + finder + "%'");
		//look for icno
		if ( students.size() == 0 ) students = db.list("select s from Student s where s.biodata.icno like '%" + finder + "%' or s.biodata.passport like '%" + finder + "%'");
		//look for name
		if ( students.size() == 0 ) students = db.list("select s from Student s where s.biodata.name like '%" + finder + "%'");
		
		
		context.put("search_list", students);
		
		vm = path + "select_students.vm"; 
		
	}

	private void findStudent() {
		// 
		
		vm = path + "student.vm";
		
	}

	private void getStudents() {
		String sponsorId = request.getParameter("sponsor_id");
		Sponsor sponsor = db.find(Sponsor.class, sponsorId);
		context.put("sponsor", sponsor);

		List<SponsorStudent> sponsorStudents = db.list("select s from SponsorStudent s where s.sponsor.id = '" + sponsorId + "'");
		context.put("sponsor_students", sponsorStudents);
		
		vm = path + "list_students_parent.vm";
		
	}

	private void start() {
		List<Sponsor> sponsors = db.list("select s from educate.sis.finance.entity.Sponsor s");
		context.put("sponsors", sponsors);
		
		vm = path + "start.vm";
		
	}
	
	public static void main(String[] args) throws Exception {
		DbPersistence db = new DbPersistence();
		List<SponsorStudent> list = db.list("select s from SponsorStudent s join s.sponsor p");
		System.out.println(list.size());
		
		
		
	}

}
