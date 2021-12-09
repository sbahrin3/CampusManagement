package educate.sis.module;

import educate.db.DbPersistence;
import educate.sis.exam.entity.CourseGroup;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class CourseGroupSetupModule extends LebahModule {
	
	DbPersistence db = new DbPersistence();
	String path = "apps/setup_course_group";

	@Override
	public String start() {
		return path + "start.vm";
	}
	
	@Command("addCourseGroup")
	public String addCourseGroup() throws Exception {
		String code = getParam("code");
		String name = getParam("name");
		
		db.begin();
		CourseGroup c = new CourseGroup();
		c.setCode(code);
		c.setName(name);
		db.persist(c);
		db.commit();
		
		return path + "/listCourseGroups.vm";
	}

}
