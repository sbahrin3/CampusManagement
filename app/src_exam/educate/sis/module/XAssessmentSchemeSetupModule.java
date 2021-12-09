package educate.sis.module;

import java.util.Hashtable;
import java.util.List;

import educate.db.DbPersistence;
import educate.sis.exam.entity.AssessmentResult;
import educate.sis.exam.entity.CourseworkItem;
import educate.sis.exam.entity.CourseworkScheme;
import educate.sis.struct.entity.Subject;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class XAssessmentSchemeSetupModule extends LebahModule {
	
	String path = "apps/util/assessment_scheme/";
	DbPersistence db = new DbPersistence();
	
	@Override
	public void preProcess() {
		context.remove("errorMessage");
	}

	@Override
	public String start() {
		List<CourseworkScheme> schemes = db.list("select s from CourseworkScheme s");
		context.put("schemes", schemes);
		return path + "start.vm";
	}
	
	@Command("add_scheme")
	public String addScheme() throws Exception {
		String name = request.getParameter("name");
		
		db.begin();
		CourseworkScheme scheme = new CourseworkScheme();
		scheme.setName(name);
		db.persist(scheme);
		db.commit();
		
		List<CourseworkScheme> schemes = db.list("select s from CourseworkScheme s");
		context.put("schemes", schemes);
		
		return path + "schemes.vm";
	}
	
	@Command("delete_scheme")
	public String deleteScheme() throws Exception {
		
		String schemeId = request.getParameter("scheme_id");
		CourseworkScheme scheme = db.find(CourseworkScheme.class, schemeId);
		Hashtable h = new Hashtable();
		h.put("scheme", scheme);
		List<Subject> subjects = db.list("select s from Subject s where s.courseworkScheme = :scheme", h);
		if ( subjects.size() == 0 ) {
			db.begin();
			db.remove(scheme);
			db.commit();
		}
		else {
			System.out.println("Can't remove scheme " + scheme.getName());
			context.put("errorMessage", "Can't remove scheme " + scheme.getName());
		}
		
		List<CourseworkScheme> schemes = db.list("select s from CourseworkScheme s");
		context.put("schemes", schemes);
		
		return path + "schemes.vm";
	}
	
	@Command("update_code")
	public String updateSchemeCode() throws Exception {
		String id = request.getParameter("scheme_id");
		String code = request.getParameter("code_" + id);
		CourseworkScheme scheme = db.find(CourseworkScheme.class, id);
		db.begin();
		scheme.setCode(code);
		db.commit();
		
		return path + "empty.vm";
	}
	
	@Command("update_name")
	public String updateSchemeName() throws Exception {
		String id = request.getParameter("scheme_id");
		String name = request.getParameter("name_" + id);
		CourseworkScheme scheme = db.find(CourseworkScheme.class, id);
		db.begin();
		scheme.setName(name);
		db.commit();
		
		return path + "empty.vm";
	}
	
	@Command("get_items")
	public String getItems() throws Exception {
		String id = request.getParameter("scheme_id");
		CourseworkScheme scheme = db.find(CourseworkScheme.class, id);
		context.put("scheme", scheme);
		List<CourseworkItem> items = scheme.getCourseworkItems();
		context.put("items", items);
		return path + "items.vm";
	}
	
	@Command("add_item")
	public String addItem() throws Exception {
		String id = request.getParameter("scheme_id");
		CourseworkScheme scheme = db.find(CourseworkScheme.class, id);
		context.put("scheme", scheme);
		int sequence = scheme.getCourseworkItems().size() + 1;
		String name = request.getParameter("item_name");
		String code = request.getParameter("item_code");
		String _percentage = request.getParameter("item_percentage");
		double percentage = 0.0d;
		try {
			percentage = Double.parseDouble(_percentage);
		} catch ( Exception e ) {}
		db.begin();
		CourseworkItem item = new CourseworkItem();
		item.setCourseworkScheme(scheme);
		item.setCode(code);
		item.setName(name);
		item.setPercentage(percentage);
		item.setSequence(sequence);
		db.persist(item);
		scheme.addItem(item);
		db.commit();
		
		List<CourseworkItem> items = scheme.getCourseworkItems();
		context.put("items", items);
		
		return path + "items.vm";
	}
	
	@Command("update_item_code")
	public String updateItemCode() throws Exception {
		String itemId = request.getParameter("item_id");
		CourseworkItem item = db.find(CourseworkItem.class, itemId);
		context.put("scheme", item.getCourseworkScheme());
		String code = request.getParameter("item_code_" + itemId);
		db.begin();
		item.setCode(code);
		db.commit();
		return path + "empty.vm";
	}
	
	@Command("update_item_name")
	public String updateItemName() throws Exception {
		String itemId = request.getParameter("item_id");
		CourseworkItem item = db.find(CourseworkItem.class, itemId);
		context.put("scheme", item.getCourseworkScheme());
		String name = request.getParameter("item_name_" + itemId);
		db.begin();
		item.setName(name);
		db.commit();
		return path + "empty.vm";
	}
	
	@Command("update_item_percentage")
	public String updateItemPercentage() throws Exception {
		String itemId = request.getParameter("item_id");
		CourseworkItem item = db.find(CourseworkItem.class, itemId);
		context.put("scheme", item.getCourseworkScheme());
		String _percentage = request.getParameter("item_percentage_" + itemId);
		double percentage = 0.0d;
		try {
			percentage = Double.parseDouble(_percentage);
		} catch ( Exception e ) {}

		
		db.begin();
		item.setPercentage(percentage);
		db.commit();
		return path + "empty.vm";
	}
	
	
	@Command("delete_item")
	public String deleteItem() throws Exception {
		String itemId = request.getParameter("item_id");

		CourseworkItem item = db.find(CourseworkItem.class, itemId);
		context.put("scheme", item.getCourseworkScheme());
		CourseworkScheme scheme = item.getCourseworkScheme();
		
		List<AssessmentResult> results = db.list("select r from AssessmentResult r where r.courseworkItem.id = '" + itemId + "'");
		if ( results.size() == 0 ) {
			db.begin();
			scheme.deleteItem(itemId);
			db.remove(item);
			db.commit();
		}
		else {
			System.out.println("Can't delete coursework item.  It's being used in AssessmentResult");
		}
		
		List<CourseworkItem> items = scheme.getCourseworkItems();
		context.put("items", items);		
		
		return path + "items.vm";
	}

}
