package educate.studentaffair.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.enrollment.entity.StudentStatus;
import educate.sis.module.StudentStatusUtil;
import educate.studentaffair.entity.Enquiry;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class EnquiryListModule extends LebahModule {
	
	private String path = "studentaffair/enquiry_list";
	private DbPersistence db = new DbPersistence();
	private String status = "pending";
	
	
	public void preProcess() {
		context.put("util", new lebah.util.Util());
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy"));
		context.put("timeFormat", new SimpleDateFormat("hh:mm a"));	
		context.put("numFormat", new DecimalFormat("#,###,###.00"));	
		
	}

	@Override
	public String start() {
		context.put("enquiryStatusList", status);
		List<Enquiry> enquiries = db.list("select e from Enquiry e where e.status = '" + status + "' order by e.datePosted desc");
		context.put("enquiries", enquiries);
		return path + "/start.vm";
	}
	
	@Command("listEnquiries")
	public String listEnquiries() throws Exception {
		String statusList = getParam("enquiryStatusList");
		context.put("enquiryStatusList", statusList);
		List<Enquiry> enquiries = db.list("select e from Enquiry e where e.status = '" + statusList + "' order by e.datePosted desc");
		context.put("enquiries", enquiries);
		return path + "/enquiries.vm";		
	}
	
	@Command("openEnquiry")
	public String openEnquiry() throws Exception {
		String enquiryId = getParam("enquiryId");
		Enquiry e = db.find(Enquiry.class, enquiryId);
		context.put("enquiry", e);
		return path + "/openEnquiry.vm";
	}
	
	@Command("saveStatus")
	public String saveStatus() throws Exception {
		String enquiryId = getParam("enquiryId");
		Enquiry e = db.find(Enquiry.class, enquiryId);
		String status = getParam("enquiryStatus");
		db.begin();
		e.setStatus(status);
		e.setActionText(getParam("actionText"));
		if ( "close".equals(status)) {
			e.setClosed(true);
			e.setDateClosed(new Date());
		}
		else {
			e.setClosed(false);
			e.setDateClosed(null);
		}
		db.commit();
		String statusList = getParam("enquiryStatusList");
		context.put("enquiryStatusList", statusList);
		List<Enquiry> enquiries = db.list("select e from Enquiry e where e.status = '" + statusList + "' order by e.datePosted desc");
		context.put("enquiries", enquiries);
		return path + "/enquiries.vm";
	}
	
	@Command("getStudent")
	public String getStudentInfo() throws Exception {
		String studentId = getParam("studentId");
		Student student = db.find(Student.class, studentId);
		context.put("student", student);
		StudentStatusUtil u = new StudentStatusUtil();
		StudentStatus studentStatus = u.getCurrentStudentStatus(student);
		if ( studentStatus == null ) context.remove("student_status");
		else context.put("studentStatus", studentStatus);
		return path + "/student.vm";
	}

}
