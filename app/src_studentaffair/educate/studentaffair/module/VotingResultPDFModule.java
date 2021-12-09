package educate.studentaffair.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.velocity.Template;

import educate.db.DbPersistence;
import educate.studentaffair.entity.VotingCandidate;
import educate.studentaffair.entity.VotingPosition;
import educate.studentaffair.entity.VotingSession;
import lebah.portal.velocity.VTemplate;

public class VotingResultPDFModule  extends VTemplate {
	
	private String path = "studentaffair/voting_result";
	DbPersistence db = new DbPersistence();
	
	public Template doTemplate() throws Exception {
		setShowVM(false);
		String serverName = request.getServerName();
		int serverPort = request.getServerPort();
        String server = serverPort != 80 ? serverName + ":" + serverPort : serverName;
        String http = request.getRequestURL().toString().substring(0, request.getRequestURL().toString().indexOf("://") + 3);
        String serverUrl = http + server;
        context.put("serverUrl", serverUrl);
        String uri = request.getRequestURI();
        String appname = uri.substring(1);
        appname = appname.substring(0, appname.indexOf("/"));
        context.put("appUrl", serverUrl.concat("/").concat(appname));     
        
		context.put("today", new Date());
		context.put("dateFormat", new SimpleDateFormat("yyyy-MM-dd"));
		context.put("df", new SimpleDateFormat("dd MMM, yyyy"));
		context.put("timeFormat", new SimpleDateFormat("hh:mm a"));	
		context.put("numFormat", new DecimalFormat("#,###,###.00"));	
		context.put("util", new lebah.util.Util());
		Template template = engine.getTemplate(getVotingResult());	
		return template;		
	}

	private String getVotingResult() {
		String positionId = getParam("positionId");
		
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
			context.put("votingSession", votingSession);
			context.put("startDate", votingSession.getStartDate());
			context.put("endDate", votingSession.getEndDate());
		} else {
			context.remove("startDate");
			context.remove("endDate");
		}
		return path + "/print_template.vm";
	}


}
