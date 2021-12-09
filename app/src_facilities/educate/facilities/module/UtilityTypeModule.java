package educate.facilities.module;

import educate.db.DbPersistence;
import educate.facilities.entity.UtilityType;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class UtilityTypeModule extends LebahModule {
	
	private String path = "facilities/utility_type";
	private DbPersistence db = new DbPersistence();

	@Override
	public String start() {
		context.put("utilities", db.list("select c from UtilityType c order by c.sequence"));
		return path + "/start.vm";
	}
	
	@Command("updateSequence")
	public String updateSequence() throws Exception {
		String[] ids = request.getParameterValues("utilityIds");
		int i = 0;
		for ( String id : ids ) {
			i++;
			UtilityType c = db.find(UtilityType.class, id);
			db.begin();
			c.setSequence(i);
			db.commit();
		}
		context.put("utilities", db.list("select c from UtilityType c order by c.sequence"));
		return path + "/listUtilities.vm";
	}
	
	@Command("addUtility")
	public String addUtility() throws Exception {
		Integer seq = (Integer) db.get("select max(c.sequence) from UtilityType c");
		int sequence = seq != null ? seq.intValue() + 1 : 1;
		db.begin();
		UtilityType c = new UtilityType();
		c.setName(getParam("name"));
		c.setSequence(sequence);
		db.persist(c);
		db.commit();
		context.put("utilities", db.list("select c from UtilityType c order by c.sequence"));
		return path + "/listUtilities.vm";
	}
	

	@Command("changeName")
	public String changeName() throws Exception {
		String utilityId = getParam("utilityId");
		UtilityType utility = db.find(UtilityType.class, utilityId);
		db.begin();
		utility.setName(getParam("name_" + utilityId));
		db.commit();
		return path + "/empty.vm";
	}	
	 
	@Command("deleteUtility")
	public String deleteUtility() throws Exception {
		String utilityId = getParam("utilityId");
		UtilityType utility = db.find(UtilityType.class, utilityId);
		db.begin();
		db.remove(utility);
		db.commit();
		context.put("utilitiess", db.list("select c from UtilityType c order by c.sequence"));
		return path + "/listIssues.vm";
	}


}
