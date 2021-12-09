package iqwan.web;

import lebah.db.Db;
import lebah.db.SQLRenderer;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class ContactMessageModule extends LebahModule {
	
	String path = "contactUs";

	@Override
	public String start() {
		return path + "/start.vm";
	}
	
	@Command("contactMessage")
	public String contactMessage() throws Exception {
		String name = getParam("contactName");
		String email = getParam("contactEmail");
		String organization = getParam("contactOrganization");
		String phone = getParam("contactPhoneNo");
		String message = getParam("contactMessage");
		
		String remoteAddr = request.getRemoteAddr();
		
		Db db = null;
		try {
			db = new Db();
			SQLRenderer r = new SQLRenderer();
			r.add("id", lebah.db.UniqueID.getUID());
			r.add("name", name);
			r.add("email", email);
			r.add("organization", organization);
			r.add("phone", phone);
			r.add("message", message);
			r.add("remoteAddr", remoteAddr);
			String sql = r.getSQLInsert("contact_us");
			db.getStatement().executeUpdate(sql);
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			if ( db != null ) db.close();
		}
		
		return path + "/contactMessage.vm";
	}

}
