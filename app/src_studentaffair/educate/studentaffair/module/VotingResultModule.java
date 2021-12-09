package educate.studentaffair.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import educate.db.DbPersistence;
import educate.studentaffair.entity.VotingCandidate;
import educate.studentaffair.entity.VotingPosition;
import educate.studentaffair.entity.VotingSession;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class VotingResultModule extends LebahModule {
	
	private DbPersistence db = new DbPersistence();
	private String path = "studentaffair/voting_result";
	
	public void preProcess() {
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy"));
		context.put("timeFormat", new SimpleDateFormat("hh:mm a"));	
		context.put("numFormat", new DecimalFormat("#,###,###.00"));
	}

	@Override
	public String start() {
		context.put("positions", db.list("select p from VotingPosition p order by p.sequence"));

		return path + "/start.vm";
	}
	
	@Command("listResult")
	public String listResult() throws Exception {
		
		
		
		String positionId = getParam("positionId");
		if ( "".equals(positionId) ) return path + "/empty.vm";
		
		VotingPosition position = db.find(VotingPosition.class, positionId);
		context.put("position", position);
		
		List<VotingCandidate> candidates = db.list("select c from VotingCandidate c where c.position.id = '" + positionId + "' order by c.student.biodata.name");
		Map<String, Long> resultMap = new HashMap<String, Long>();
		context.put("resultMap", resultMap);
		VotingSession votingSession = db.find(VotingSession.class, "1");
		for ( VotingCandidate c : candidates ) {
			Long count = (Long) db.get("select count(r) from VotingResult r where r.votingSession.id = '" + votingSession.getId() + "' and r.selectedCandidate.position.id = '" + positionId + "' and r.selectedCandidate.id = '" + c.getId() + "'");
			resultMap.put(c.getId(), count);
		}
		context.put("candidates", candidates);
		
		if ( votingSession != null ) {
			context.put("startDate", votingSession.getStartDate());
			context.put("endDate", votingSession.getEndDate());
		} else {
			context.remove("startDate");
			context.remove("endDate");
		}
		return path + "/candidates.vm";
	}
	
}
