package educate.alumni.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import educate.db.DbPersistence;
import lebah.portal.action.LebahModule;

public class AlumniSecretariatInfoModule extends LebahModule {
	
	private DbPersistence db = new DbPersistence();
	private String path = "alumni/secretariat_info";
	
	public void preProcess() {
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy"));
		context.put("timeFormat", new SimpleDateFormat("hh:mm a"));	
		context.put("numFormat", new DecimalFormat("#,###,###.00"));
	}

	@Override
	public String start() {
		context.put("secretariats", db.list("select s from AlumniSecretariat s order by s.position.sequence"));
		return path + "/start.vm";
	}

}
