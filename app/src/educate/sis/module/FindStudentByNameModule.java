package educate.sis.module;

import java.util.List;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class FindStudentByNameModule extends LebahModule {
	
	private DbPersistence db = new DbPersistence();
	private String path = "/apps/find_student";

	@Override
	public String start() {
		
		return path + "/start.vm";
	}
	
	@Command("find")
	public String find() throws Exception {
		String findByName = getParam("findByName");
		context.put("findByName", findByName);
		List<Student> students = db.list("select s from Student s where s.biodata.name LIKE '%" + findByName + "%' order by s.biodata.name");
		context.put("students", students);
		
		String elementId = getParam("elementId");
		context.put("elementId", elementId);
		
		return path + "/find.vm";
	}

}
