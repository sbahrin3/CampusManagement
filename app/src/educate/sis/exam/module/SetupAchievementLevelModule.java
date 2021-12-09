package educate.sis.exam.module;

import java.util.List;

import educate.db.DbPersistence;
import educate.sis.exam.entity.AchievementLevel;
import educate.sis.struct.entity.Program;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class SetupAchievementLevelModule extends LebahModule {
	
	private String path = "apps/setup_achievement_level";
	private DbPersistence db = new DbPersistence();	

	@Override
	public String start() {
		List<Program> programs = db.list("select p from Program p order by p.code");
		context.put("programs", programs);
		return path + "/start.vm";
	}
	
	@Command("back")
	public String back() {
		List<Program> programs = db.list("select p from Program p order by p.code");
		context.put("programs", programs);		
		return path + "/select_params.vm";
	}
	
	@Command("getProgram")
	public String getProgram() throws Exception {
		String programId = getParam("programId");
		Program program = db.find(Program.class, programId);
		context.put("program", program);
		
		return listAchievementLevels(programId);
	}

	private String listAchievementLevels(String programId) {
		List<AchievementLevel> achievementLevels = db.list("select a from AchievementLevel a where a.program.id = '" + programId + "' order by a.gpaValue desc");
		context.put("achievementLevels", achievementLevels);
		return path + "/achievement_levels.vm";
	}
	
	@Command("updateGPA")
	public String updateGPA() throws Exception {
		String achievementLevelId = getParam("achievementLevelId");
		AchievementLevel a = db.find(AchievementLevel.class, achievementLevelId);
		
		double gpaValue = Double.parseDouble(getParam("gpaValue_" + achievementLevelId));
		db.begin();
		a.setGpaValue(gpaValue);
		db.commit();
		
		return path + "/empty.vm";
	}
	
	@Command("updateCreditHour")
	public String updateCreditHour() throws Exception {
		String achievementLevelId = getParam("achievementLevelId");
		AchievementLevel a = db.find(AchievementLevel.class, achievementLevelId);
		
		int creditHour = Integer.parseInt(getParam("creditHour_" + achievementLevelId));
		db.begin();
		a.setCreditHour(creditHour);
		db.commit();
		
		return path + "/empty.vm";
	}	
	
	@Command("updateName")
	public String updateName() throws Exception {
		String achievementLevelId = getParam("achievementLevelId");
		AchievementLevel a = db.find(AchievementLevel.class, achievementLevelId);
		
		String name = getParam("achievementName_" + achievementLevelId);
		db.begin();
		a.setName(name);
		db.commit();
		
		return path + "/empty.vm";
	}	
	
	@Command("updateCode")
	public String updateCode() throws Exception {
		String achievementLevelId = getParam("achievementLevelId");
		AchievementLevel a = db.find(AchievementLevel.class, achievementLevelId);
		
		String code = getParam("achievementCode_" + achievementLevelId);
		db.begin();
		a.setCode(code);
		db.commit();
		
		return path + "/empty.vm";
	}	
	
	@Command("addAchievementLevel")
	public String addAchievementLevel() throws Exception {
		String programId = getParam("programId");
		Program program = db.find(Program.class, programId);
		context.put("program", program);
		
		String name = getParam("achievementName");
		String code = getParam("achievementCode");
		double gpaValue = Double.parseDouble(getParam("gpaValue"));
		int creditHour = Integer.parseInt(getParam("creditHour"));
		
		db.begin();
		AchievementLevel a = new AchievementLevel();
		a.setName(name);
		a.setCode(code);
		a.setGpaValue(gpaValue);
		a.setCreditHour(creditHour);
		a.setProgram(program);
		db.persist(a);
		db.commit();
		
		return listAchievementLevels(programId);
	}
	
	@Command("updateAchievementLevel")
	public String updateAchievementLevel() throws Exception {
		String programId = getParam("programId");
		Program program = db.find(Program.class, programId);
		context.put("program", program);
		
		return listAchievementLevels(programId);
	}
	
	@Command("deleteAchievementLevel")
	public String deleteAchievementLevel() throws Exception {
		String programId = getParam("programId");
		Program program = db.find(Program.class, programId);
		context.put("program", program);
		
		String achievementLevelId = getParam("achievementLevelId");
		AchievementLevel a = db.find(AchievementLevel.class, achievementLevelId);
		db.begin();
		db.remove(a);
		db.commit();
		
		return listAchievementLevels(programId);
	}
	

}
