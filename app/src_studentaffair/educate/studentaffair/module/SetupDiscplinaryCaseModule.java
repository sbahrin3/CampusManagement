package educate.studentaffair.module;

import educate.db.DbPersistence;
import educate.studentaffair.entity.DisciplinaryCase;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class SetupDiscplinaryCaseModule extends LebahModule {

	private String path = "studentaffair/discipline";
	private DbPersistence db = new DbPersistence();
	
	@Override
	public String start() {
		context.put("cases", db.list("select d from DisciplinaryCase d"));
		return path + "/start.vm";
	}
	
	@Command("addDisciplinaryCase")
	public String addDiscplinaryCase() throws Exception {
		
		db.begin();
		DisciplinaryCase d = new DisciplinaryCase();
		d.setCode(getParam("disciplinaryCode"));
		d.setName(getParam("disciplinaryName"));
		d.setDescription(getParam("disciplinaryDescription"));
		db.persist(d);
		db.commit();
		
		context.put("cases", db.list("select d from DisciplinaryCase d"));
		return path + "/listCases.vm";
	}
	
	@Command("getDisciplinaryCase")
	public String getDiscplinaryCase() throws Exception {
		String caseId = getParam("caseId");
		DisciplinaryCase d = db.find(DisciplinaryCase.class, caseId);
		context.put("case", d);
		return path + "/getCase.vm";
	}
	
	@Command("deleteDisciplinaryCase")
	public String deleteDiscplinaryCase() throws Exception {
		String caseId = getParam("caseId");
		
		if ( db.list("select s from StudentDiscipline s where s.disciplinaryCase.id = '" + caseId + "'").size() == 0 ) {
		
			DisciplinaryCase d = db.find(DisciplinaryCase.class, caseId);
			db.begin();
			db.remove(d);
			db.commit();
		
		}
		return path + "/listCases.vm";
	}	
	
	@Command("updateCode")
	public String updateCode() throws Exception {
		String caseId = getParam("caseId");
		DisciplinaryCase d = db.find(DisciplinaryCase.class, caseId);
		db.begin();
		d.setCode(getParam("disciplinaryCode_" + caseId));
		db.commit();
		return path + "/empty.vm";
	}
	
	@Command("updateName")
	public String updateName() throws Exception {
		String caseId = getParam("caseId");
		DisciplinaryCase d = db.find(DisciplinaryCase.class, caseId);
		db.begin();
		d.setName(getParam("disciplinaryName_" + caseId));
		db.commit();
		return path + "/empty.vm";
	}	

	@Command("updateDescription")
	public String updateDescription() throws Exception {
		String caseId = getParam("caseId");
		DisciplinaryCase d = db.find(DisciplinaryCase.class, caseId);
		db.begin();
		d.setDescription(getParam("disciplinaryDescription_" + caseId));
		db.commit();
		return path + "/empty.vm";
	}	

}
