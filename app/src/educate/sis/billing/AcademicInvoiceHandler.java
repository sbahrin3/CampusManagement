package educate.sis.billing;

import java.util.Date;

import educate.enrollment.entity.Student;
import educate.enrollment.entity.StudentStatus;
import educate.sis.struct.entity.Subject;

public interface AcademicInvoiceHandler {
	public void invoicingChangeCentre(Student student,String userId)throws Exception;
	public void invoicingDeferStudy(Student student,StudentStatus studentSemester,Date applicationDate,String userId)throws Exception;
	public void invoicingChangeProgram(Student student,String userId)throws Exception;
	public void invoicingDropSubject(Student student,Subject subject, String userId,Date applicationDate)throws Exception;
	public void invoicingAddSubject(Student student,Subject subject,String userId)throws Exception;
	
}
