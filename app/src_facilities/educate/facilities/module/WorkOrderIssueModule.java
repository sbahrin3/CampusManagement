package educate.facilities.module;

import educate.db.DbPersistence;
import educate.facilities.entity.WorkOrderIssue;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class WorkOrderIssueModule extends LebahModule {
	
	private String path = "facilities/workorder_issue";
	private DbPersistence db = new DbPersistence();

	@Override
	public String start() {
		context.put("issues", db.list("select c from WorkOrderIssue c order by c.sequence"));
		return path + "/start.vm";
	}
	
	@Command("updateSequence")
	public String updateSequence() throws Exception {
		String[] ids = request.getParameterValues("issueIds");
		int i = 0;
		for ( String id : ids ) {
			i++;
			WorkOrderIssue c = db.find(WorkOrderIssue.class, id);
			db.begin();
			c.setSequence(i);
			db.commit();
		}
		context.put("issues", db.list("select c from WorkOrderIssue c order by c.sequence"));
		return path + "/listIssues.vm";
	}
	
	@Command("addIssue")
	public String addIssue() throws Exception {
		Integer seq = (Integer) db.get("select max(c.sequence) from WorkOrderIssue c");
		int sequence = seq != null ? seq.intValue() + 1 : 1;
		db.begin();
		WorkOrderIssue c = new WorkOrderIssue();
		c.setName(getParam("issueName"));
		c.setSequence(sequence);
		db.persist(c);
		db.commit();
		context.put("issues", db.list("select c from WorkOrderIssue c order by c.sequence"));
		return path + "/listIssues.vm";
	}
	

	@Command("changeName")
	public String changeName() throws Exception {
		String issueId = getParam("issueId");
		WorkOrderIssue issue = db.find(WorkOrderIssue.class, issueId);
		db.begin();
		issue.setName(getParam("issueName_" + issueId));
		db.commit();
		return path + "/empty.vm";
	}	
	 
	@Command("deleteIssue")
	public String deleteIssue() throws Exception {
		String issueId = getParam("issueId");
		WorkOrderIssue issue = db.find(WorkOrderIssue.class, issueId);
		db.begin();
		db.remove(issue);
		db.commit();
		context.put("issues", db.list("select c from WorkOrderIssue c order by c.sequence"));
		return path + "/listIssues.vm";
	}


}
