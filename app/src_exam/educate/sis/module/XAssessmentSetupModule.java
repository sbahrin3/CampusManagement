package educate.sis.module;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import educate.db.DbPersistence;
import educate.sis.exam.entity.AssessmentResult;
import educate.sis.exam.entity.Coursework;
import educate.sis.exam.entity.CourseworkItem;
import educate.sis.struct.entity.Subject;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class XAssessmentSetupModule extends LebahModule {

	String path = "apps/util/assessment_setup/";
	DbPersistence db = new DbPersistence();
	
	@Override
	public String start() {
		List<Subject> subjects = db.list("select s from Subject s order by s.code");
		context.put("subjects", subjects);
		return path + "start.vm";
	}
	
	@Command("get_coursework")
	public String getCoursework() throws Exception {
		String subjectId = request.getParameter("subject_id");
		Subject subject = db.find(Subject.class, subjectId);
		context.put("subject", subject);
		String sql = "select c from Coursework c where c.subject.id = '" + subject.getId() + "'";
		List<Coursework> courseworks = db.list(sql);
		Set<CourseworkItem> items = new HashSet<CourseworkItem>();
		if ( courseworks.size() == 0 ) {
			db.begin();
			Coursework cw = new Coursework();
			cw.setSubject(subject);
			cw.setItems(items);
			db.persist(cw);
			db.commit();
			context.put("coursework", cw);
			context.put("items", cw.getCourseworkItems());
		}
		else {
			Coursework cw = courseworks.get(0);
			items = cw.getItems();
			context.put("coursework", cw);
			context.put("items", cw.getCourseworkItems());
		}
		return path + "coursework.vm";
	}
	
	@Command("add_item")
	public String addItem() throws Exception {
		
		String courseworkId = request.getParameter("coursework_id");
		Coursework cw = db.find(Coursework.class, courseworkId);
		
		String name = request.getParameter("item_name");
		String percentage = request.getParameter("item_percentage");
		
		db.begin();
		CourseworkItem item = new CourseworkItem();
		item.setName(name);
		item.setPercentage(Double.parseDouble(percentage));
		cw.addItem(item);
		db.commit();
		
		context.put("items", cw.getCourseworkItems());
		return path + "coursework.vm";
	}
	
	@Command("delete_item")
	public String deleteItem() throws Exception {
		
		String courseworkId = request.getParameter("coursework_id");
		Coursework cw = db.find(Coursework.class, courseworkId);
		
		String itemId = request.getParameter("item_id");
		CourseworkItem item = db.find(CourseworkItem.class, itemId);
		
		List<AssessmentResult> results = db.list("select r from AssessmentResult r where r.courseworkItem.id = '" + itemId + "'");
		if ( results.size() == 0 ) {
			db.begin();
			cw.getItems().remove(item);
			db.remove(item);
			db.commit();
		}
		
		context.put("items", cw.getCourseworkItems());
		return path + "coursework.vm";
	}
	
	@Command("update_name")
	public String updateName() throws Exception {
		String itemId = request.getParameter("item_id");
		String name = request.getParameter("item_name_" + itemId);
		
		CourseworkItem item = db.find(CourseworkItem.class, itemId);
		db.begin();
		item.setName(name);
		db.commit();
		
		return path + "empty.vm";
	}

	@Command("update_percentage")
	public String updatePercentage() throws Exception {
		String itemId = request.getParameter("item_id");
		String percentage = request.getParameter("item_percentage_" + itemId);
		
		CourseworkItem item = db.find(CourseworkItem.class, itemId);
		db.begin();
		item.setPercentage(Double.parseDouble(percentage));
		db.commit();
		
		return path + "empty.vm";
	}
}
