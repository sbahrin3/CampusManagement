package educate.sis.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import educate.admission.entity.Applicant;
import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import lebah.portal.action.LebahModule;

public class PrintAdmissionFormModule extends LebahModule {
	
	String path = "apps/util/admission_form/";
	DbPersistence db = new DbPersistence();
	
	@Override
	public void preProcess() {
		context.put("dateFormat", new SimpleDateFormat("yyyy-MM-dd"));
		context.put("numFormat", new DecimalFormat("#,###,###.00"));
	}

	@Override
	public String start() {
		try {
			return getStudent();
		} catch (Exception e ) {
			
		}
		return path + "start.vm";
	}

	private String getStudent() throws Exception {
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
