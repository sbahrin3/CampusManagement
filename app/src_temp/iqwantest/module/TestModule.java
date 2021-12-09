package iqwantest.module;

import java.util.List;

import educate.enrollment.entity.Student;
import lebah.portal.action.LebahModule;
import lebahmt.db.DbPersistence;

public class TestModule extends LebahModule {
	
	private String path = "test";
	DbPersistence db = new DbPersistence();

	@Override
	public String start() {
		String campus = request.getSession().getAttribute("_portal_campus") != null ? (String) request.getSession().getAttribute("_portal_campus") : "";
		context.put("user_campus", campus);	
		
		List<Student> students = db.list("select s from Student s");
		context.put("students", students);
		
		return path + "/start.vm";
	}

}
