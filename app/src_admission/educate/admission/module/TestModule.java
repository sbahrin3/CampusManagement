package educate.admission.module;

import java.util.List;

import educate.db.DbPersistence;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;
import onapp.entity.ApplicantOnline;

public class TestModule extends LebahModule {
	
	private DbPersistence db = new DbPersistence();
	private String path = "admission/test";

	@Override
	public String start() {
		
		List<ApplicantOnline> applicants = db.list("select a from ApplicantOnline a where a.status.code = 'A'");
		context.put("applicants", applicants);
	

		return path + "/start.vm";
	}
	
	@Command("testCommand")
	public String testCommand() throws Exception {
		System.out.println("testCommand");
		return path + "/testCommand.vm";
	}

}
