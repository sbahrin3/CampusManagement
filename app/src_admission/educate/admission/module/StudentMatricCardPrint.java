package educate.admission.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import org.apache.velocity.Template;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.enrollment.entity.StudentStatus;
import educate.sis.module.StudentStatusUtil;
import lebah.portal.velocity.VTemplate;

public class StudentMatricCardPrint extends VTemplate {
	
	String path = "admission/print/";
	DbPersistence db = new DbPersistence();
	
	@Override
	public Template doTemplate() throws Exception {
		
		String serverName = request.getServerName();
		int serverPort = request.getServerPort();
		String uri = request.getRequestURI();
		String s1 = uri.substring(1);
        String server = serverPort != 80 ? serverName + ":" + serverPort : serverName;
		String app = s1.substring(0, s1.indexOf("/"));   
		String http = request.getRequestURL().toString().substring(0, request.getRequestURL().toString().indexOf("://") + 3);
		context.put("applicationURL", http + server + "/" + app);

		setShowVM(false);
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy"));
		context.put("numFormat", new DecimalFormat("#,###,###.00"));
		Template template = engine.getTemplate(getStudent());	
		return template;		
	}
	
	private String getStudent() throws Exception {
		String studentId = request.getParameter("studentId");

		Student student = db.find(Student.class, studentId);
		context.put("student", student);
		
		
		StudentStatusUtil u = new StudentStatusUtil();
		StudentStatus studentStatus = u.getCurrentStudentStatus(student);
		context.put("studentStatus", studentStatus);
		context.put("session", studentStatus.getSession());
		
		Date startDate = null;
		Date endDate = null;
		Set<StudentStatus> statuses = student.getStatus();
		for ( StudentStatus s : statuses ) {
			if ( startDate == null || startDate.after(s.getSession().getStartDate())) {
				startDate = s.getSession().getStartDate(); 
			}
			if ( endDate == null || endDate.before(s.getSession().getStartDate())) {
				endDate = s.getSession().getEndDate();
			}
		}
		
		context.put("startDate", startDate);
		context.put("endDate", endDate);
		
		String color = student.getProgram().getCourse().getFaculty().getMatricCardColor();
		if ( color == null || "".equals(color)) color = "#A9ACB2";
		context.put("matricCardColor", color);
		
		String matricTemplateName =  student.getProgram().getCourse().getFaculty().getMatricTemplateName();
		context.put("matricTemplateName", matricTemplateName);
		
		//SimpleImageInfo imageInfo = new SimpleImageInfo(student.getPhoto());
		
		return path + "print_matric_card.vm";
	}
	
	public static void main(String[] args) throws Exception {
		
	}



}
