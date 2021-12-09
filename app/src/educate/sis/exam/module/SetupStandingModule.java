package educate.sis.exam.module;

import java.text.SimpleDateFormat;

import educate.db.DbPersistence;
import educate.sis.exam.entity.Standing;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class SetupStandingModule extends LebahModule {
	
	private String path = "apps/setup_standing";
	private DbPersistence db = new DbPersistence();
	
	public void preProcess() {
		context.put("path", path);
		context.put("util", new lebah.util.Util());
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy"));
	}

	@Override
	public String start() {
		context.put("standings", db.list("select p from Standing p order by p.sequence"));
		context.remove("saved");
		return path + "/start.vm";
	}
	
	@Command("editStanding")
	public String editStanding() {
		String row = getParam("row");
		context.put("row", row);
		if ( !"0".equals(row)) {
			String standingId = getParam("standingId");
			Standing standing = db.find(Standing.class, standingId);
			context.put("standing", standing);
		}
		else {
			context.remove("standing");
		}
		
		return path + "/edit_standing.vm";
	}
	
	@Command("saveStanding")
	public String saveStanding() throws Exception {
		context.put("saved", true);
		String row = getParam("row");
		context.put("row", row);
		String standingId = getParam("standingId");
		Standing standing = db.find(Standing.class, standingId);
		context.put("standing", standing);
		String code = getParam("code_" + row);
		String name = getParam("name_" + row);

		db.begin();
		standing.setCode(code);
		standing.setName(name);
		db.commit();
		
		return path + "/standing.vm";
	}
	
	@Command("addStanding")
	public String addStanding() throws Exception {
		String row = "0";
		
		String code = getParam("code_" + row);
		String name = getParam("name_" + row);

		
		//get sequence
		Integer seq = (Integer) db.get("select max(e.sequence) from Standing e");
		if ( seq == null ) seq = 0;
		seq++;	

		
		db.begin();
		Standing standing = new Standing();
		standing.setId(code);
		standing.setSequence(seq);
		standing.setCode(code);
		standing.setName(name);

		db.persist(standing);
		db.commit();
		
		context.put("standings", db.list("select p from Standing p order by p.sequence"));
		context.remove("saved");
		
		return path + "/standings.vm";
	}

	@Command("deleteStanding")
	public String deleteStanding() throws Exception {
		
		String standingId = getParam("standingId");
		Standing standing = db.find(Standing.class, standingId);
		
		db.begin();
		db.remove(standing);
		db.commit();
		
		context.put("standings", db.list("select p from Standing p order by p.sequence"));
		context.remove("saved");
		
		return path + "/standings.vm";
	}



}
