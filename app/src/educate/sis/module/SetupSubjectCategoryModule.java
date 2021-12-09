package educate.sis.module;

import java.util.List;

import javax.servlet.http.HttpSession;

import educate.db.DbPersistence;
import educate.sis.struct.entity.SubjectCategory;
import lebah.portal.action.AjaxModule;

public class SetupSubjectCategoryModule extends AjaxModule {
	
	String path = "apps/util/setup_subjects_category/";
	private String vm = "";
	HttpSession session;
	DbPersistence db = new DbPersistence();

	@Override
	public String doAction() throws Exception {
		session = request.getSession();
		String command = request.getParameter("command");
		System.out.println(command);
		if ( null == command || "".equals(command)) start();
		else if ( "change_code".equals(command)) changeCode();
		else if ( "change_name".equals(command)) changeName();
		else if ( "update_mandatory".equals(command)) updateMandatory();
		else if ( "add_new".equals(command)) addNew();
		else if ( "delete_category".equals(command)) deleteCategory();
		return vm;
	}
	
	private void deleteCategory() throws Exception {
		String categoryId = request.getParameter("category_id");
		
		long count = (Long) db.get("select count(r) from SubjectReg r where r.category.id = '" + categoryId + "'");
		if ( count == 0 ) {
		
			SubjectCategory subjectCategory = db.find(SubjectCategory.class, categoryId);
			
			
			db.begin();
			db.remove(subjectCategory);
			db.commit();
			
		}
		
		List<SubjectCategory> categories = db.list("select s from SubjectCategory s order by s.code");
		context.put("categories", categories);
		
		vm = path + "categories.vm";
		
	}

	private void addNew() throws Exception {
		String code = request.getParameter("code_new");
		String name = request.getParameter("name_new");
		
		db.begin();
		SubjectCategory subjectCategory = new SubjectCategory();
		subjectCategory.setCode(code);
		subjectCategory.setName(name);
		db.persist(subjectCategory);
		db.commit();
		
		List<SubjectCategory> categories = db.list("select s from SubjectCategory s order by s.code");
		context.put("categories", categories);
		
		vm = path + "categories.vm";
		
	}

	private void updateMandatory() throws Exception {
		
		String categoryId = request.getParameter("category_id");
		
		SubjectCategory subjectCategory = db.find(SubjectCategory.class, categoryId);
		boolean mandatory = subjectCategory.getMandatory();
		
		db.begin();
		subjectCategory.setMandatory(!mandatory);
		db.commit();
		
		List<SubjectCategory> categories = db.list("select s from SubjectCategory s order by s.code");
		context.put("categories", categories);
		
		vm = path + "categories.vm";
		
	}
	
	private void changeName() throws Exception {
		
		String categoryId = request.getParameter("category_id");
		String name = request.getParameter("name_" + categoryId);
		
		SubjectCategory subjectCategory = db.find(SubjectCategory.class, categoryId);
		
		db.begin();
		subjectCategory.setName(name);
		db.commit();
		
		vm = path + "empty.vm";
		
	}
	
	private void changeCode() throws Exception {
		
		String categoryId = request.getParameter("category_id");
		String code = request.getParameter("code_" + categoryId);
		
		SubjectCategory subjectCategory = db.find(SubjectCategory.class, categoryId);
		
		db.begin();
		subjectCategory.setCode(code);
		db.commit();
		
		vm = path + "empty.vm";
		
	}

	private void start() {
		
		List<SubjectCategory> categories = db.list("select s from SubjectCategory s order by s.code");
		context.put("categories", categories);
		
		vm = path + "start.vm";
	}

}
