package educate.sis.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.sis.struct.entity.StudentAccomodation;
import educate.sis.struct.entity.StudentAccomodationStatus;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class StudentAccomodationStatusModule extends LebahModule {
	
	private String path = "apps/util/accomodation_status/";
	private DbPersistence db = new DbPersistence();

	@Override
	public String start() {
		try {
			List<StudentAccomodation> accomodations = db.list("select a from StudentAccomodation a order by a.name");
			context.put("accomodations", accomodations);
		} catch ( Exception e ) {
			
		}
		return path + "start.vm";
	}
	
	
	@Override
	public void preProcess() {
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy"));
		context.put("timeFormat", new SimpleDateFormat("hh:mm a"));	
		context.put("numFormat", new DecimalFormat("#,###,###.00"));	
		context.remove("_message");
	}
	
	@Command("get_accomodation")
	public String getAccomodation() throws Exception {
		String accomodationId = request.getParameter("accomodation_id");
		StudentAccomodation accomodation = db.find(StudentAccomodation.class, accomodationId);
		context.put("accomodation", accomodation);
		
		String genderId = request.getParameter("gender_id");
		context.put("gender_id", genderId);
		
		return path + "accomodation.vm";
	}
	
	@Command("list_students")
	public String listStudents() throws Exception {
		String accomodationId = request.getParameter("accomodation_id");
		StudentAccomodation accomodation = db.find(StudentAccomodation.class, accomodationId);
		context.put("accomodation", accomodation);
		
		String genderId = request.getParameter("gender_id");
		context.put("gender_id", genderId);
		
		listStudents(accomodation, genderId);
		return path + "students.vm";
	}


	private void listStudents(StudentAccomodation accomodation, String genderId) {
		List<StudentAccomodationStatus> students = db.list("select s from StudentAccomodationStatus s where s.expired = 0 and s.student.biodata.gender.id = '" + genderId + "' and s.accomodation.id = '" + accomodation.getId() + "' order by s.date desc, s.time desc");
		int quota = 0;
		if ( "L".equals(genderId)) {
			quota = accomodation.getQuotaMale();
		} else {
			quota = accomodation.getQuotaFemale();
		}
		int listOfStudents = students.size();
		int quotaBalance = quota - listOfStudents;
		context.put("quota_balance", quotaBalance);
		
		context.put("students", students);
	}
	

	@Command("find_students")
	public String findStudents() throws Exception {
		context.remove("students");
		String find = request.getParameter("find");
		
		List<Student> students = null;
		//first check on matric no
		students = db.list("select s from Student s where s.matricNo = '" + find + "'");
		if ( students.size() == 0 )
			students = db.list("select s from Student s where s.biodata.icno = '" + find + "'");
		if ( students.size() == 0 )
			students = db.list("select s from Student s where s.biodata.passport = '" + find + "'");
		if ( students.size() == 0 )
			students = db.list("select s from Student s where s.biodata.name like '%" + find + "%'");		
		
		context.put("students", students);
		return path + "find_students.vm";
	}
	
	@Command("add_student")
	public String addStudent() throws Exception {
		String studentId = request.getParameter("student_id");
		Student student = db.find(Student.class, studentId);
		context.put("student", student);
		String accomodationId = request.getParameter("accomodation_id");
		StudentAccomodation accomodation = db.find(StudentAccomodation.class, accomodationId);
		context.put("accomodation", accomodation);
		
		List<StudentAccomodationStatus> list = null;
		//check for expired status first
		list = db.list("select s from StudentAccomodationStatus s where s.expired = 1 and s.student.id = '" + studentId + "' and s.accomodation.id = '" + accomodationId + "'");
		if ( list.size() > 0 ) {
			//if found, make expired to false
			StudentAccomodationStatus s = list.get(0);
			db.begin();
			s.setExpired(false);
			s.setDate(new Date());
			s.setTime(new Date());
			db.commit();
			
			context.put("_message","successfull");
			context.put("checked_accomodation", s.getAccomodation());
			context.put("checked_date", s.getDate());
			context.put("accomodation_status", s);
		}
		else {
			//else
			list = db.list("select s from StudentAccomodationStatus s where s.expired = 0 and s.student.id = '" + studentId + "'");
			if ( list.size() > 0 ) {
				StudentAccomodationStatus s = list.get(0);
				context.put("_message", "student_is_checked");
				context.put("checked_accomodation", s.getAccomodation());
				context.put("checked_date", s.getDate());
			}
			else {
				db.begin();
				StudentAccomodationStatus status = new StudentAccomodationStatus();
				status.setAccomodation(accomodation);
				status.setStudent(student);
				status.setDate(new Date());
				status.setTime(new Date());
				status.setExpired(false);
				db.persist(status);
				db.commit();
				
				context.put("_message","successfull");
				context.put("checked_accomodation", status.getAccomodation());
				context.put("checked_date", status.getDate());
				context.put("accomodation_status", status);
				
			}
		}
		
		String genderId = request.getParameter("gender_id");
		listStudents(accomodation, genderId);
		return path + "students.vm";
	}
	
	@Command("make_expired")
	public String makeExpired() throws Exception {
		String statusId = request.getParameter("status_id");
		StudentAccomodationStatus status = db.find(StudentAccomodationStatus.class, statusId);
		String accomodationId = status.getAccomodation().getId();
		db.begin();
		status.setExpired(true);
		db.commit();
		
		String genderId = request.getParameter("gender_id");
		listStudents(status.getAccomodation(), genderId);		
		return path + "students.vm";
	}
	
	@Command("checkout_selection")
	public String checkOutSelection() throws Exception {
		String accomodationId = request.getParameter("accomodation_id");
		StudentAccomodation accomodation = db.find(StudentAccomodation.class, accomodationId);
		String[] statusIds = request.getParameterValues("status_ids");
		if ( statusIds != null ) {
			for ( String statusId : statusIds ) {
				StudentAccomodationStatus status = db.find(StudentAccomodationStatus.class, statusId);
				db.begin();
				status.setExpired(true);
				db.commit();
			}
		}
	
		String genderId = request.getParameter("gender_id");
		listStudents(accomodation, genderId);		
		return path + "students.vm";
	}
	
	@Command("accomodation_confirmation")
	public String accomodationConfirmation() throws Exception {
		String statusId = request.getParameter("accomodation_status_id");
		StudentAccomodationStatus status = db.find(StudentAccomodationStatus.class, statusId);
		context.put("accomodation_status", status);
		context.put("student", status.getStudent());
		context.put("accomodation", status.getAccomodation());
		
		return path + "accomodation_confirmation.vm";
	}
	
}
