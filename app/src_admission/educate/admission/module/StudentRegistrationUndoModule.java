package educate.admission.module;

import java.util.Date;

import educate.admission.entity.UndoRegistrationLog;
import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.enrollment.entity.StudentStatus;
import educate.sis.module.StudentStatusUtil;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;
import onapp.entity.ApplicantOnline;
import onapp.entity.ApplicantStatus;

public class StudentRegistrationUndoModule extends LebahModule {
	
	DbPersistence db = new DbPersistence();
	String path = "admission/registration_undo";

	@Override
	public String start() {
		return path + "/start.vm";
	}
	
	@Command("getStudent")
	public String getStudent() throws Exception {
		String matricNo = getParam("matricNo");
		Student student = (Student) db.get("select s from Student s where s.matricNo = '" + matricNo + "'");
		if ( student != null ) {
			context.put("student", student);
			StudentStatusUtil u = new StudentStatusUtil();
			StudentStatus studentStatus = u.getCurrentStudentStatus(student);
			context.put("studentStatus", studentStatus);
			return path + "/getStudent.vm";
		}
		return path + "/noStudent.vm";
	}
	
	@Command("undoRegistration")
	public String undoRegistration() throws Exception {
		String studentId = getParam("studentId");
		Student student = db.find(Student.class, studentId);
		System.out.println("student to undo = " + student.getBiodata().getName());
		String matricNo = student.getMatricNo();
		String studentName = student.getBiodata().getName();
		ApplicantOnline applicant = student.getApplicant2();
		if ( applicant != null ) {
			ApplicantStatus status = (ApplicantStatus) db.get("select s from ApplicantStatus s where s.code = 'A'");
			db.begin();
			applicant.setStatus(status);
			db.commit();
		}
		
		db.begin();
		db.remove(student);
		db.commit();
		
		String userId = (String) request.getSession().getAttribute("_portal_login");
		db.begin();
		UndoRegistrationLog log = new UndoRegistrationLog();
		log.setUserId(userId);
		log.setDateTime(new Date());
		log.setMatricNo(matricNo);
		log.setStudentName(studentName);
		log.setRemoteAddr(request.getRemoteAddr());
		db.persist(log);
		db.commit();
		
		return path + "/undoRegistration.vm";
	}

}
