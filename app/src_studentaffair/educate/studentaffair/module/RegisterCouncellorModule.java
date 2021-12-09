package educate.studentaffair.module;

import educate.db.DbPersistence;
import educate.studentaffair.entity.Councellor;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class RegisterCouncellorModule extends LebahModule {
	
	private String path = "studentaffair/councellor";
	private DbPersistence db = new DbPersistence();

	@Override
	public String start() {
		context.put("councellors", db.list("select c from Councellor c"));
		return path + "/start.vm";
	}
	
	@Command("addCouncellor")
	public String addCouncellor() throws Exception {
		
		db.begin();
		Councellor c = new Councellor();
		c.setIdNo(getParam("idNo"));
		c.setName(getParam("name"));
		c.setContactNo(getParam("contactNo"));
		c.setEmail(getParam("email"));
		db.persist(c);
		db.commit();
		
		//PortalUtil.createPortalLogin(c.getIdNo(), c.getIdNo(), c.getName());
		
		context.put("councellors", db.list("select c from Councellor c"));
		return path + "/start.vm";
	}
	
	@Command("updateIdNo")
	public String updateIdNo() throws Exception {
		String id = getParam("councellorId");
		Councellor c = db.find(Councellor.class, id);
		db.begin();
		c.setIdNo(getParam("idNo_" + id));
		db.commit();
		return path + "/empty.vm";
	}
	
	@Command("updateName")
	public String updateName() throws Exception {
		String id = getParam("councellorId");
		Councellor c = db.find(Councellor.class, id);
		db.begin();
		c.setName(getParam("name_" + id));
		db.commit();
		return path + "/empty.vm";
	}	
	
	@Command("updateContactNo")
	public String updateContactNo() throws Exception {
		String id = getParam("councellorId");
		Councellor c = db.find(Councellor.class, id);
		db.begin();
		c.setContactNo(getParam("contactNo_" + id));
		db.commit();
		return path + "/empty.vm";
	}	
	
	@Command("updateEmail")
	public String updateEmail() throws Exception {
		String id = getParam("councellorId");
		Councellor c = db.find(Councellor.class, id);
		db.begin();
		c.setEmail(getParam("email_" + id));
		db.commit();
		return path + "/empty.vm";
	}
	
	@Command("deleteCouncellor")
	public String deleteCouncellr() throws Exception {
		String id = getParam("councellorId");
		Councellor c = db.find(Councellor.class, id);
		
		if ( db.list("select s from StudentCouncelling s where s.councellor.id = '" + id + "'").size() == 0  
			&& db.list("select s from StudentCouncellingSession s where s.councellor.id = '" + id + "'").size() == 0 ) {
		
			db.begin();
			db.remove(c);
			db.commit();
		
		}
		context.put("councellors", db.list("select c from Councellor c"));
		return path + "/start.vm";
	}		

}
