package iqwan.web;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import lebah.db.Db;
import lebah.db.SQLRenderer;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class ContactMessageAdminModule extends LebahModule {
	
	String path = "contactUsAdmin";
	
	public void preProcess() {
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy hh:mm a"));
	}

	@Override
	public String start() {
		listMessages();
		return path + "/start.vm";
	}

	private void listMessages() {
		Db db = null;
		try {
			db = new Db();
			SQLRenderer r = new SQLRenderer();
			r.add("id");
			r.add("name");
			r.add("email");
			r.add("organization");
			r.add("phone");
			r.add("message");
			r.add("remoteAddr");
			r.add("createDate");
			String sql = r.getSQLSelect("contact_us", "createDate desc");
			ResultSet rs = db.getStatement().executeQuery(sql);
			List<ContactUs> contacts = new ArrayList<ContactUs>();
			context.put("contacts", contacts);
			while ( rs.next() ) {
				ContactUs c = new ContactUs();
				c.setId(rs.getString("id"));
				c.setName(rs.getString("name"));
				c.setEmail(rs.getString("email"));
				c.setOrganization(rs.getString("organization"));
				c.setPhone(rs.getString("phone"));
				c.setMessage(rs.getString("message"));
				c.setRemoteAddr(rs.getString("remoteAddr"));
				c.setCreateDate(rs.getTimestamp("createDate"));
				contacts.add(c);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if ( db != null ) db.close();
		}
	}
	
	@Command("deleteMessage")
	public String deleteMessage() throws Exception {
		String messageId = getParam("messageId");
		String sql = "delete from contact_us where id = '" + messageId + "'";
		Db db = null;
		try {
			db = new Db();
			db.getStatement().executeUpdate(sql);
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			if ( db != null ) db.close();
		}
		listMessages();
		return path + "/listMessages.vm";
	}

}
