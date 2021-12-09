package educate.studentaffair.module;

import java.util.List;

import educate.db.DbPersistence;
import educate.studentaffair.entity.CouncellingIssue;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class SetupCouncellingIssueModule extends LebahModule {

	private String path = "studentaffair/councelling_issue";
	private DbPersistence db = new DbPersistence();	
	
	@Override
	public String start() {
		List<CouncellingIssue> issues = db.list("select c from CouncellingIssue c order by c.name");
		context.put("issues", issues);
		return path + "/start.vm";
	}
	
	@Command("getCouncellingIssue")
	public String getCouncellingIssue() throws Exception {
		String issueId = getParam("issueId");
		CouncellingIssue issue = null;
		if ( !"".equals(issueId)) {
			issue = db.find(CouncellingIssue.class, issueId);
			context.put("issue", issue);
		} else context.remove("issue");
		return path + "/getCouncellingIssue.vm";
	}
	
	@Command("saveCouncellingIssue")
	public String saveCouncellingIssue() throws Exception {
		String issueId = getParam("issueId");
		CouncellingIssue issue = null;
		if ( !"".equals(issueId)) {
			issue = db.find(CouncellingIssue.class, issueId);
		} else {
			issue = new CouncellingIssue();
		}
		
		db.begin();
		issue.setCode(getParam("code"));
		issue.setDescription(getParam("description"));
		issue.setName(getParam("name"));
		if ( "".equals(issueId)) db.persist(issue);
		db.commit();
		
		List<CouncellingIssue> issues = db.list("select c from CouncellingIssue c order by c.name");
		context.put("issues", issues);
		return path + "/listCouncellingIssues.vm";
	}
	
	@Command("deleteCouncellingIssue")
	public String deleteCouncellingIssue() throws Exception {
		String issueId = getParam("issueId");
		CouncellingIssue issue = db.find(CouncellingIssue.class, issueId);
		db.begin();
		db.remove(issue);
		db.commit();
		List<CouncellingIssue> issues = db.list("select c from CouncellingIssue c order by c.name");
		context.put("issues", issues);
		return path + "/listCouncellingIssues.vm";
	}

}
