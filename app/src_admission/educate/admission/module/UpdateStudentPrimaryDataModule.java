package educate.admission.module;

import java.util.Date;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.enrollment.entity.UpdatePrimaryDataLog;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class UpdateStudentPrimaryDataModule extends LebahModule {
	
	private DbPersistence db = new DbPersistence();
	private String path = "apps/update_primary_data";
	private String userId;

	@Override
	public String start() {
		return path + "/start.vm";
	}
	
	public void preProcess() {
		userId = (String) request.getSession().getAttribute("_user_login");
	}
	
	@Command("getStudent")
	public String getStudent() throws Exception {
		String matricNo = getParam("matricNo");
		Student student = (Student) db.get("select s from Student s where s.matricNo = '" + matricNo + "'");
		context.put("student", student);
		return path + "/getStudent.vm";
	}
	
	@Command("saveData")
	public String saveData() throws Exception {
		String studentId = getParam("studentId");
		Student student = db.find(Student.class, studentId);
		context.put("student", student);
		
		//log
		db.begin();
		UpdatePrimaryDataLog log = new UpdatePrimaryDataLog();
		context.put("log", log);
		log.setUserId(userId);
		log.setDateTime(new Date());
		log.setOldICNo(student.getBiodata().getIcno());
		log.setOldName(student.getBiodata().getName());
		log.setOldPassportNo(student.getBiodata().getPassport());
		db.persist(log);
		db.commit();
		
		db.begin();
		student.getBiodata().setName(getParam("name"));
		student.getBiodata().setIcno(getParam("icno"));
		student.getBiodata().setPassport(getParam("passport"));
		db.commit();
		

		return path + "/saveData.vm";
	}

}
