package educate.studentaffair.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.studentaffair.entity.VotingCandidate;
import educate.studentaffair.entity.VotingPosition;
import educate.studentaffair.entity.VotingResult;
import educate.studentaffair.entity.VotingSession;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class VotingModule extends LebahModule {
	
	private DbPersistence db = new DbPersistence();
	private String path = "studentaffair/voting_session";
	private String matricNo;
	
	public void preProcess() {
		matricNo = (String) request.getSession().getAttribute("_portal_login");
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy"));
		context.put("timeFormat", new SimpleDateFormat("hh:mm a"));	
		context.put("numFormat", new DecimalFormat("#,###,###.00"));
	}

	@Override
	public String start() {
		
		getVotingSession();
		
		
		context.put("positions", db.list("select p from VotingPosition p order by p.sequence"));
		return path + "/start.vm";
	}

	private void getVotingSession() {
		VotingSession votingSession = db.find(VotingSession.class, "1");
		if ( votingSession != null ) {
			context.put("votingSession", votingSession);
			context.put("startDate", votingSession.getStartDate());
			context.put("endDate", votingSession.getEndDate());
			
			Calendar c1 = Calendar.getInstance();
			c1.setTime(votingSession.getStartDate());
			//c1.add(Calendar.DATE, -1);
			
			Calendar c2 = Calendar.getInstance();
			c2.setTime(votingSession.getEndDate());
			//c2.add(Calendar.DATE, 1);			
			
			Date today = new Date();	
			if ( today.before(votingSession.getStartDate())) {
				context.remove("canVote");
			}
			else if ( today.after(c1.getTime()) && today.before(c2.getTime())) {
				context.put("canVote", true);
			}
			else if ( today.after(c2.getTime())) {
				context.remove("canVote");
			}
		} else {
			context.remove("startDate");
			context.remove("endDate");
			context.remove("canVote");
			context.remove("votingSession");
		}
	}	
	
	@Command("listCandidates")
	public String listCandidates() throws Exception {
		
		
		
		String positionId = getParam("positionId");
		if ( "".equals(positionId) ) return path + "/empty.vm";
		
		VotingPosition position = db.find(VotingPosition.class, positionId);
		context.put("position", position);
		
		Student student = (Student) db.get("select s from Student s where s.matricNo = '" + matricNo + "'");
		
		VotingSession votingSession = db.find(VotingSession.class, "1");
		VotingResult result = (VotingResult) db.get("select r from VotingResult r where r.votingSession.id = '" + votingSession.getId() + "' and r.selectedCandidate.position.id = '" + positionId + "' and r.student.id = '" + student.getId() + "'");
		if ( result != null )
			context.put("result", result);
		else
			context.remove("result");
		
		getVotingSession();
		
		context.put("candidates", db.list("select c from VotingCandidate c where c.position.id = '" + positionId + "' order by c.student.biodata.name"));
		return path + "/candidates.vm";
	}	
	
	
	
	@Command("vote")
	public String vote() throws Exception {
		
		String positionId = getParam("positionId");
		if ( "".equals(positionId) ) return path + "/empty.vm";
		
		VotingPosition position = db.find(VotingPosition.class, positionId);
		context.put("position", position);
		
		String candidateId = getParam("candidateId");
		VotingCandidate c = db.find(VotingCandidate.class, candidateId);
		
		Student student = (Student) db.get("select s from Student s where s.matricNo = '" + matricNo + "'");
		
		VotingSession votingSession = db.find(VotingSession.class, "1");
		VotingResult result =  (VotingResult) db.get("select r from VotingResult r where r.votingSession.id = '" + votingSession.getId() + "' and r.selectedCandidate.position.id = '" + positionId + "' and r.student.id = '" + student.getId() + "'");
		
		if ( result == null ) {

			db.begin();
			result = new VotingResult();
			result.setSelectedCandidate(c);
			result.setStudent(student);
			result.setVotingSession(votingSession);
			db.persist(result);
			db.commit();
		}
		else {
			db.begin();
			result.setSelectedCandidate(c);
			db.commit();
		}
		context.put("result", result);
		context.put("candidates", db.list("select c from VotingCandidate c where c.position.id = '" + positionId + "' order by c.student.biodata.name"));

		getVotingSession();
		
		return path + "/candidates.vm";
	}

}
