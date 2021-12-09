package educate.sis.exam.module;

import educate.db.DbPersistence;
import educate.sis.exam.entity.Standing;
import educate.sis.exam.entity.StandingRule;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class SetupStandingRuleModule extends LebahModule {
	
	private String path = "apps/setup_standing_rule";
	private DbPersistence db = new DbPersistence();

	public void preProcess() {
		context.put("path", path);
		context.put("standings", db.list("select s from Standing s order by s.sequence"));
	}

	@Override
	public String start() {
		//on start get undergraduate
		String programLevelType = "undergraduate";
		context.put("standingRules", db.list("select p from StandingRule p where p.programLevelType = '" + programLevelType + "' order by p.sequence"));
		context.remove("saved");
		return path + "/start.vm";
	}
	
	@Command("getStandingRules")
	public String getStandingRules() throws Exception {
		String programLevelType = getParam("programLevelType");
		context.put("standingRules", db.list("select p from StandingRule p where p.programLevelType = '" + programLevelType + "' order by p.sequence"));
		context.remove("saved");
		return path + "/standingRules.vm";
	}
	
	@Command("editStandingRule")
	public String editStandingRule() {
		String row = getParam("row");
		context.put("row", row);
		if ( !"0".equals(row)) {
			String standingRuleId = getParam("standingRuleId");
			StandingRule standingRule = db.find(StandingRule.class, standingRuleId);
			context.put("standingRule", standingRule);
		}
		else {
			context.remove("standingRule");
		}
		
		return path + "/edit_standingRule.vm";
	}
	
	@Command("saveStandingRule")
	public String saveStandingRule() throws Exception {
		context.put("saved", true);
		String row = getParam("row");
		context.put("row", row);
		String standingRuleId = getParam("standingRuleId");
		StandingRule standingRule = db.find(StandingRule.class, standingRuleId);
		context.put("standingRule", standingRule);
		String comparator = getParam("comparator_" + row);
		String cgpaValue = getParam("cgpaValue_" + row);
		
		String comparator2 = getParam("comparator2_" + row);
		String gpaValue = getParam("gpaValue_" + row);		
		
		double cgpa = 0.0d;
		double gpa = 0.0d;
		try {
			cgpa = Double.parseDouble(cgpaValue);
		} catch ( Exception e ) { }
		try {
			gpa = Double.parseDouble(gpaValue);
		} catch ( Exception e ) { }
		
		String prevStandingId = getParam("previousStandingId_" + row);
		String standingId = getParam("currentStandingId_" + row);
		
		Standing prevStanding = db.find(Standing.class, prevStandingId);
		Standing standing = db.find(Standing.class, standingId);
		
		db.begin();
		standingRule.setComparator(comparator);
		standingRule.setCgpaValue(cgpa);
		
		standingRule.setComparator2(comparator2);
		standingRule.setGpaValue(gpa);

		
		standingRule.setPreviousStanding(prevStanding);
		standingRule.setCurrentStanding(standing);
		db.commit();
		
		return path + "/standingRule.vm";
	}
	
	@Command("addStandingRule")
	public String addStandingRule() throws Exception {
		String row = "0";
		
		String programLevelType = getParam("programLevelType");
		
		String comparator = getParam("comparator_" + row);
		String cgpaValue = getParam("cgpaValue_" + row);
		
		String comparator2 = getParam("comparator2_" + row);
		String gpaValue = getParam("gpaValue_" + row);	
		
		String prevStandingId = getParam("previousStandingId_" + row);
		String standingId = getParam("currentStandingId_" + row);
		
		Standing prevStanding = db.find(Standing.class, prevStandingId);
		Standing standing = db.find(Standing.class, standingId);
		
		//get sequence
		Integer seq = (Integer) db.get("select max(e.sequence) from StandingRule e");
		if ( seq == null ) seq = 0;
		seq++;	
		
		double cgpa = 0.0d;
		double gpa = 0.0d;
		try {
			cgpa = Double.parseDouble(cgpaValue);
		} catch ( Exception e ) { }
		try {
			gpa = Double.parseDouble(gpaValue);
		} catch ( Exception e ) { }

		
		db.begin();
		StandingRule standingRule = new StandingRule();
		standingRule.setProgramLevelType(programLevelType);
		standingRule.setSequence(seq);
		standingRule.setComparator(comparator);
		standingRule.setCgpaValue(cgpa);
		
		standingRule.setComparator2(comparator2);
		standingRule.setGpaValue(gpa);
		
		standingRule.setPreviousStanding(prevStanding);
		standingRule.setCurrentStanding(standing);		
		db.persist(standingRule);
		db.commit();
		
		context.put("standingRules", db.list("select p from StandingRule p where p.programLevelType = '" + programLevelType + "' order by p.sequence"));
		context.remove("saved");
		
		return path + "/standingRules.vm";
	}

	@Command("deleteStandingRule")
	public String deleteStandingRule() throws Exception {
		String programLevelType = getParam("programLevelType");
		String standingRuleId = getParam("standingRuleId");
		StandingRule standingRule = db.find(StandingRule.class, standingRuleId);
		
		db.begin();
		db.remove(standingRule);
		db.commit();
		
		context.put("standingRules", db.list("select p from StandingRule p where p.programLevelType = '" + programLevelType + "' order by p.sequence"));
		context.remove("saved");
		
		return path + "/standingRules.vm";
	}

}
