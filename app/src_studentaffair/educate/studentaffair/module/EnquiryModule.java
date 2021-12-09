package educate.studentaffair.module;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.studentaffair.entity.Enquiry;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class EnquiryModule extends LebahModule {
	
	protected String path = "studentaffair/enquiry";
	protected DbPersistence db = new DbPersistence();
	protected boolean studentMode = false;
	protected String matricNo = "";
	
	public void preProcess() {
		matricNo = (String) request.getSession().getAttribute("_portal_login");
		context.put("dateFormat", new SimpleDateFormat("dd-MMM-yyyy"));
	}

	@Override
	public String start() {
		Student student = (Student) db.get("select s from Student s where s.matricNo = '" + matricNo + "'");
		context.put("student", student);
		return path + "/start.vm";
	}
	
	@Command("submitEnquiry")
	public String submitEnquiry() throws Exception {
		String studentId = getParam("studentId");
		Student student = db.find(Student.class, studentId);
		
		db.begin();
		Enquiry e = new Enquiry();
		e.setDatePosted(new Date());
		e.setEnquiryText(getParam("enquiryText"));
		e.setStatus("pending");
		e.setDateClosed(null);
		e.setClosed(false);
		e.setStudent(student);
		db.persist(e);
		db.commit();
		
		context.put("enquiry", e);
		
		return path + "/submitEnquiry.vm";
	}
	
	@Command("listEnquiries")
	public String listEnquiries() throws Exception {
		List<Enquiry> enquiries = db.list("select e from Enquiry e where e.student.matricNo = '" + matricNo + "' order by e.datePosted desc");
		context.put("enquiries", enquiries);
		return path + "/enquiries.vm";			
		
	}

}
