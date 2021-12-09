package educate.sis.module;

import educate.db.DbPersistence;
import lebah.portal.action.LebahModule;

public class UserLearningCentreModule extends LebahModule {
	
	private DbPersistence db = new DbPersistence();
	private String path = "apps/userLearningCentre";
	
	@Override
	public void preProcess() {
		context.put("path", path);
	}

	@Override
	public String start() {
		context.put("centres", db.list("select c from LearningCentre c"));
		return path + "/start.vm";
	}

}
