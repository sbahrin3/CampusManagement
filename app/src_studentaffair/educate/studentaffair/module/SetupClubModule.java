package educate.studentaffair.module;

import educate.db.DbPersistence;
import educate.studentaffair.entity.Club;
import educate.studentaffair.entity.ClubCategory;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class SetupClubModule extends LebahModule {
	
	private DbPersistence db = new DbPersistence();
	private String path = "studentaffair/club";

	@Override
	public String start() {
		context.put("clubs", db.list("select c from Club c"));
		context.put("categories", db.list("select c from ClubCategory c"));
		return path + "/start.vm";
	}
	
	@Command("addClub")
	public String addClub() throws Exception {
		String categoryId = getParam("categoryId");
		ClubCategory category = null;
		System.out.println("category id = " + categoryId);
		if ( "add_category".equals(categoryId)) {
			String categoryName = getParam("categoryName");
			db.begin();
			category = new ClubCategory();
			category.setName(categoryName);
			db.persist(category);
			db.commit();
		}
		else {
			category = db.find(ClubCategory.class, categoryId);
		}
		db.begin();
		
		Club club = new Club();
		club.setCategory(category);
		club.setName(getParam("clubName"));
		db.persist(club);
		db.commit();
		
		context.put("clubs", db.list("select c from Club c"));
		context.put("categories", db.list("select c from ClubCategory c"));
		return path + "/listClubs.vm";
	}
	
	@Command("editClub")
	public String editClub() throws Exception {
		String clubId = getParam("clubId");
		Club club = db.find(Club.class, clubId);
		context.put("club", club);
		context.put("categories", db.list("select c from ClubCategory c"));
		return path + "/editClub.vm";
	}
	
	@Command("saveClub")
	public String saveClub() throws Exception {
		String clubId = getParam("clubId");
		Club club = db.find(Club.class, clubId);
		
		String categoryId = getParam("categoryId_" + clubId);
		ClubCategory category = db.find(ClubCategory.class, categoryId);
		
		db.begin();
		club.setName(getParam("clubName_" + clubId));
		club.setDescription(getParam("clubDescription_" + clubId));
		club.setCategory(category);
		db.commit();
		
		context.put("clubs", db.list("select c from Club c"));
		context.put("categories", db.list("select c from ClubCategory c"));
		return path + "/listClubs.vm";
	}
	

}
