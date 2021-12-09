package educate.enrollment.registration;

import educate.admission.entity.Applicant;
import educate.enrollment.entity.Student;

public interface RegisterStudent {
	public Student add(String name, String icno, String program_id, String session_id, String center_id, String partner_id, String nationalityType,double amount,String fullPayment,Applicant applicant,String userID)throws Exception;
}
