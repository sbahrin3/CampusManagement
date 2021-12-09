package educate.sis.module;

import java.util.Date;

import educate.db.DbPersistence;
import educate.sis.struct.entity.LetterTemplate;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class ComposeLetterModule extends LebahModule {
	
	String path = "apps/util/compose_letter/";
	String userId = "";
	DbPersistence db = new DbPersistence();
	
	@Override
	public void preProcess() {
		System.out.println("command = " + command);
		userId = (String) request.getSession().getAttribute("_portal_login");
		if ( userId == null ) userId = "none";
	}

	@Override
	public String start() {
		String letterId = "offered";
		LetterTemplate letter = db.find(LetterTemplate.class, letterId);
		if ( letter != null ) context.put("letter", letter);
		else context.remove("letter");
		return path + "start.vm";
	}
	
	@Command("save")
	public String save() throws Exception {
		String letterId = "offered";
		LetterTemplate letter = db.find(LetterTemplate.class, letterId);
		String text = request.getParameter("text");
		
		if ( letter == null ) {
			db.begin();
			letter = new LetterTemplate();
			letter.setId(letterId);
			letter.setText(text);
			letter.setModifyDate(new Date());
			letter.setUserId(userId);
			db.persist(letter);
			db.commit();
		}
		else {
			db.begin();
			letter.setText(text);
			letter.setModifyDate(new Date());
			letter.setUserId(userId);
			db.commit();		
		}
		
		context.put("util", new lebah.util.Util());
		context.put("letter", letter);
		return path + "save.vm";
	}
	
	@Command("edit")
	public String edit() throws Exception {
		String letterId = "offered";
		LetterTemplate template = db.find(LetterTemplate.class, letterId);
		context.put("letter", template);
		return path + "compose.vm";
	}
	
	@Command("view")
	public String viewSample() throws Exception {
		String letterId = "offered";
		LetterTemplate template = db.find(LetterTemplate.class, letterId);
		
		
		return path + "view.vm";
	}

}
