package educate.admission.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.velocity.Template;

import educate.admission.entity.RegistrationSlipItem;
import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import lebah.portal.velocity.VTemplate;

public class RegistrationSlipPrint extends VTemplate {
	
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
		context.put("dateFormat", new SimpleDateFormat("yyyy-MM-dd"));
		context.put("numFormat", new DecimalFormat("#,###,###.00"));
		Template template = engine.getTemplate(getStudent());	
		return template;		
	}
	
	private String getStudent() {
		String matricNo = request.getParameter("matric_no");
		matricNo = matricNo.replaceAll("_", " ");
		Student student = (Student) db.get("select s from Student s where s.matricNo = '" + matricNo + "'");
		context.put("student", student);
		
		List<RegistrationSlipItem> checkedItems = db.list("select i from RegistrationSlipItem i where i.student.id = '" + student.getId() + "' order by i.sequence");
		context.put("checkedItems", checkedItems);

		return path + "print_all.vm";
	}



}
