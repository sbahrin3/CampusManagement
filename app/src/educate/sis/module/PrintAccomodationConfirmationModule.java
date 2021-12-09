package educate.sis.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.sis.struct.entity.StudentAccomodation;
import educate.sis.struct.entity.StudentAccomodationStatus;
import lebah.portal.action.LebahModule;

public class PrintAccomodationConfirmationModule extends LebahModule {

	String path = "apps/util/accomodation_confirmation/";
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
	private String getStudent() {
		String matricNo = request.getParameter("matric_no");
		Student student = (Student) db.get("select s from Student s where s.matricNo = '" + matricNo + "'");
		context.put("student", student);
		StudentAccomodationStatus s = (StudentAccomodationStatus) db.get("select s from StudentAccomodationStatus s where s.expired = 0 and s.student.id = '" + student.getId() + "'");
		if ( s != null ) {
			StudentAccomodation accomodation = s.getAccomodation();
			context.put("accomodation", accomodation);
		}
		else context.remove("accomodation");
		return path + "accomodation_confirmation.vm";
	}

}
