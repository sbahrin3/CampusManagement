package educate.sis.print;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import org.apache.velocity.Template;

import educate.admission.entity.Applicant;
import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import lebah.portal.velocity.VTemplate;

public class AdmissionFormPrint extends VTemplate {
	
	String path = "apps/util/document_print/";
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
		context.put("dateFormat", new SimpleDateFormat("yyyy-MM-dd"));
		context.put("numFormat", new DecimalFormat("#,###,###.00"));
		Template template = engine.getTemplate(getStudent());	
		return template;		
	}
	
	private String getStudent() {
		String matricNo = request.getParameter("matric_no");
		Student student = (Student) db.get("select s from Student s where s.matricNo = '" + matricNo + "'");
		if ( student != null ) {
			Applicant applicant = student.getApplicant();
			if ( applicant == null ) {
				return path + "data_not_found.vm";
			}
			else {
				context.put("applicant", applicant); //admission data comes from applicant information
				context.put("student", student);
				return path + "admission_form.vm";
			}
		}
		else 
			return path + "data_not_found.vm";
	}


}
