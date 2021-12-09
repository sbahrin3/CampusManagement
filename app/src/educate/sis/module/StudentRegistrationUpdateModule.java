package educate.sis.module;

import educate.admission.entity.Applicant;
import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import lebah.portal.action.AjaxModule;

public class StudentRegistrationUpdateModule extends AjaxModule {
	
	DbPersistence db = new DbPersistence();

	
	@Override
	public String doAction() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static void main(String[] args) throws Exception {
		DbPersistence db = new DbPersistence();
		String matricNo = "20091098";
		Student student = (Student) db.get("select s from Student s where s.matricNo = '" + matricNo + "'");
		if ( student != null ) {
			update(student);
		}
	}

	private static void update(Student student) {
		System.out.println(student.getBiodata().getName());
		Applicant applicant = student.getApplicant();
		if ( applicant == null ) {
			System.out.println("No Application Data!");
		}
		else {
			System.out.println(applicant.getReferenceNo());
			System.out.println(applicant.getProgramOffered());
		}
	}

}
