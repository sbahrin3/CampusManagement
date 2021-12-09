package educate.studentaffair.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import educate.db.DbPersistence;
import educate.enrollment.entity.Student;
import educate.enrollment.entity.StudentStatus;
import educate.sis.module.StudentStatusUtil;
import educate.studentaffair.entity.VotingCandidate;
import educate.studentaffair.entity.VotingPosition;
import educate.studentaffair.entity.VotingResult;
import educate.studentaffair.entity.VotingSession;
import lebah.portal.action.Command;
import lebah.portal.action.LebahModule;

public class SetupVotingCandidateModule extends LebahModule {
	
	private DbPersistence db = new DbPersistence();
	private String path = "studentaffair/voting";
	
	public void preProcess() {
		context.put("dateFormat", new SimpleDateFormat("dd-MM-yyyy"));
		context.put("timeFormat", new SimpleDateFormat("hh:mm a"));	
		context.put("numFormat", new DecimalFormat("#,###,###.00"));
	}

	@Override
	public String start() {
		//context.put("candidates", db.list("select c from VotingCandidate c order by c.student.biodata.name"));
		context.put("positions", db.list("select p from VotingPosition p order by p.sequence"));
		return path + "/start.vm";
	}
	
	@Command("listCandidates")
	public String listCandidates() throws Exception {
		String positionId = getParam("positionId");
		if ( "".equals(positionId) ) return path + "/empty.vm";
		
		VotingSession votingSession = db.find(VotingSession.class, "1");
		context.put("votingSession", votingSession);
		
		VotingPosition position = db.find(VotingPosition.class, positionId);
		context.put("position", position);
		
		return listCandidates(positionId);
	}

	private String listCandidates(String positionId) {
		
		VotingSession votingSession = db.find(VotingSession.class, "1");
		
		if ( votingSession != null ) {
			context.put("votingSession", votingSession);
			context.put("startDate", votingSession.getStartDate());
			context.put("endDate", votingSession.getEndDate());
		} else {
			context.remove("votingSession");
			context.remove("startDate");
			context.remove("endDate");
		}
		
		context.put("candidates", db.list("select c from VotingCandidate c where c.removed = 0 and c.position.id = '" + positionId + "' order by c.student.biodata.name"));
		return path + "/candidates.vm";
	}
	
	@Command("findStudent")
	public String findStudent() {
		return path + "/findStudent.vm";
	}
	
	@Command("getStudent")
	public String getStudent() throws Exception {
		String matricNo = getParam("matricNo").trim();
		Student student = (Student) db.get("select s from Student s where s.matricNo = '" + matricNo + "'");
		context.put("student", student);
		StudentStatusUtil u = new StudentStatusUtil();
		StudentStatus studentStatus = u.getCurrentStudentStatus(student);
		if ( studentStatus == null ) context.remove("student_status");
		else context.put("studentStatus", studentStatus);
		
		return path + "/student.vm";
	}	
	
	@Command("addCandidate")
	public String addCandidate() throws Exception {
		
		String positionId = getParam("positionId");
		VotingPosition position = db.find(VotingPosition.class, positionId);
		context.put("position", position);
		
		String studentId = getParam("studentId");
		Student student = db.find(Student.class, studentId);
		
		List<VotingCandidate> candidates = db.list("select c from VotingCandidate c where c.student.id = '" + studentId + "' and c.position.id = '" + positionId + "'"); 
		if ( candidates.size() == 0 ) {
			db.begin();
			VotingCandidate c = new VotingCandidate();
			c.setPosition(position);
			c.setStudent(student);
			c.setRemoved(false);
			db.persist(c);
			db.commit();
		} else {
			VotingCandidate c = candidates.get(0);
			db.begin();
			c.setRemoved(false);
			db.commit();
		}
		
	
		return listCandidates(positionId);
	}
	
	@Command("deleteCandidate")
	public String deleteCandidate() throws Exception {
		
		String positionId = getParam("positionId");
		VotingPosition position = db.find(VotingPosition.class, positionId);
		context.put("position", position);
		
		
		String candidateId = getParam("candidateId");
		
		List<VotingResult> results = db.list("select r from VotingResult r where r.selectedCandidate.id = '" + candidateId + "'");
		if ( results.size() == 0 ) {
			VotingCandidate c = db.find(VotingCandidate.class, candidateId);
			db.begin();
			db.remove(c);
			db.commit();
		} else {
			VotingCandidate c = db.find(VotingCandidate.class, candidateId);
			db.begin();
			c.setRemoved(true);
			db.commit();
		}
		
		return listCandidates(positionId);
	}
	
	@Command("getVotingDate")
	public String getVotingDate() throws Exception {
		VotingSession votingSession = db.find(VotingSession.class, "1");
		if ( votingSession != null ) {
			context.put("startDate", votingSession.getStartDate());
			context.put("endDate", votingSession.getEndDate());
			
			Calendar c1 = Calendar.getInstance();
			c1.setTime(votingSession.getStartDate());
			int h1 = c1.get(Calendar.HOUR_OF_DAY);
			int m1 = c1.get(Calendar.MINUTE);
			
			Calendar c2 = Calendar.getInstance();
			c2.setTime(votingSession.getEndDate());
			int h2 = c2.get(Calendar.HOUR_OF_DAY);
			int m2 = c2.get(Calendar.MINUTE);
			
			context.put("h1", h1);
			context.put("m1", m1);
			context.put("h2", h2);
			context.put("m2", m2);
			
			
		} else {
			context.remove("startDate");
			context.remove("endDate");
		}
		return path + "/votingDate.vm";
	}
	
	@Command("saveVotingDate")
	public String saveVotingDate() throws Exception {
		
		String _startDate = getParam("startDate");
		String _endDate = getParam("endDate");
		String h1 = getParam("h1");
		String h2 = getParam("h2");
		String m1 = getParam("m1");
		String m2 = getParam("m2");
		String votingSessionName = getParam("votingSessionName");
		
		_startDate = _startDate + " " + h1 + ":" + m1;
		_endDate = _endDate + " " + h2 + ":" + m2;
		
		Date startDate = null;
		Date endDate = null;
		try {
			startDate = new SimpleDateFormat("dd-MM-yyyy HH:mm").parse(_startDate);
		} catch ( Exception e ) {}
		try {
			endDate = new SimpleDateFormat("dd-MM-yyyy HH:mm").parse(_endDate);
		} catch ( Exception e ) {}
		
		boolean isnew = false;
		VotingSession votingSession = db.find(VotingSession.class, "1");
		if ( votingSession == null ) {
			votingSession = new VotingSession();
			isnew = true;
		}
		db.begin();
		votingSession.setStartDate(startDate);
		votingSession.setEndDate(endDate);
		votingSession.setName(votingSessionName);
		if ( isnew ) {
			votingSession.setId("1");
			votingSession.setName("Voting Session 1");
			db.persist(votingSession);
		}
		db.commit();
		
		context.put("votingSession", votingSession);
		
		return path + "/voting_session.vm";
	}

}
