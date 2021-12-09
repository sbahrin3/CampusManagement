/**
 * 
 */
package educate.sis.module;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

/**
 * @author Shamsul Bahrin bin Abd Mutalib
 *
 */
public class DeleteStudentDataModule extends LebahModule {

	private DbPersistence db = new DbPersistence();
	private String path = "admission/deleteStudentData";
	
	/* (non-Javadoc)
	 * @see lebah.portal.action.LebahModule#start()
	 */
	@Override
	public String start() {
		// TODO Auto-generated method stub
		return path + "/start.vm";
	}
	
	@Command("getStudent")
	public String getStudent() throws Exception {
		String matricNo = getParam("matricNo").trim();
		Student student = (Student) db.get("select s from Student s where s.matricNo = '" + matricNo + "'");
		context.put("student", student);
		return path + "/getStudent.vm";
	}
	
	@Command("deleteData")
	public String deleteData() throws Exception {
		String studentId = getParam("studentId");
		Student student = db.find(Student.class, studentId);
		db.begin();
		db.remove(student);
		db.commit();
		
		return path + "/deleteData.vm";
	}

}
