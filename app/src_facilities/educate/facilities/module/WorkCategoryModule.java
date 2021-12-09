package educate.facilities.module;

import educate.db.DbPersistence;
import educate.facilities.entity.WorkCategory;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class WorkCategoryModule extends LebahModule {
	
	private String path = "facilities/work_category";
	private DbPersistence db = new DbPersistence();

	@Override
	public String start() {
		context.put("categories", db.list("select c from WorkCategory c order by c.sequence"));
		return path + "/start.vm";
	}
	
	@Command("updateSequence")
	public String updateSequence() throws Exception {
		String[] ids = request.getParameterValues("categoryIds");
		int i = 0;
		for ( String id : ids ) {
			i++;
			WorkCategory c = db.find(WorkCategory.class, id);
			db.begin();
			c.setSequence(i);
			db.commit();
		}
		context.put("categories", db.list("select c from WorkCategory c order by c.sequence"));
		return path + "/listCategories.vm";
	}
	
	@Command("addCategory")
	public String addCategory() throws Exception {
		Integer seq = (Integer) db.get("select max(c.sequence) from WorkCategory c");
		int sequence = seq != null ? seq.intValue() + 1 : 1;
		db.begin();
		WorkCategory c = new WorkCategory();
		c.setCode(getParam("categoryCode"));
		c.setName(getParam("categoryName"));
		c.setSequence(sequence);
		db.persist(c);
		db.commit();
		context.put("categories", db.list("select c from WorkCategory c order by c.sequence"));
		return path + "/listCategories.vm";
	}
	
	@Command("changeCode")
	public String changeCode() throws Exception {
		String categoryId = getParam("categoryId");
		WorkCategory category = db.find(WorkCategory.class, categoryId);
		db.begin();
		category.setCode(getParam("categoryCode_" + categoryId));
		db.commit();
		return path + "/empty.vm";
	}
	
	@Command("changeName")
	public String changeName() throws Exception {
		String categoryId = getParam("categoryId");
		WorkCategory category = db.find(WorkCategory.class, categoryId);
		db.begin();
		category.setName(getParam("categoryName_" + categoryId));
		db.commit();
		return path + "/empty.vm";
	}	
	
	@Command("deleteCategory")
	public String deleteCategory() throws Exception {
		String categoryId = getParam("categoryId");
		WorkCategory category = db.find(WorkCategory.class, categoryId);
		db.begin();
		db.remove(category);
		db.commit();
		context.put("categories", db.list("select c from WorkCategory c order by c.sequence"));
		return path + "/listCategories.vm";
	}

}
