package educate.sis.module;

import educate.db.DbPersistence;
import lebah.portal.action.AjaxModule;

public class PersistenceReloadModule extends AjaxModule {

	
	@Override
	public String doAction() throws Exception {
		DbPersistence db = new DbPersistence();
		db.getEntityManager().clear();
		db.reload();
		return "vtl/reload_em.vm";
	}

}
