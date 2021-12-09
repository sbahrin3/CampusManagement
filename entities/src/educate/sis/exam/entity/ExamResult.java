package educate.sis.exam.entity;

import javax.persistence.*;

import java.util.*;
import educate.enrollment.entity.*;
import educate.sis.struct.entity.*;

@Entity
@Table(name="exam_result")
public class ExamResult {

	@Id
	private String id;
	@OneToMany(cascade=CascadeType.ALL, mappedBy="result")
	private Set<SessionResult> sessions;
	@OneToOne(fetch= FetchType.LAZY)
	private Student student;
	
	public ExamResult() {
		setId(lebah.util.UIDGenerator.getUID());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Set<SessionResult> getSessions() {
		return sessions;
	}

	public void setSessions(Set<SessionResult> sessions) {
		this.sessions = sessions;
	}
	
	public void addSession(SessionResult session) {
		if ( sessions == null ) sessions = new HashSet<SessionResult>();
		session.setResult(this);
		sessions.remove(session);
		sessions.add(session);
	}
	
	public void removeSession(SessionResult session) {
		sessions.remove(session);
	}
	
	public SessionResult getSessionResult(Session session) {
		SessionResult sessionResult = null;
		if ( sessions == null){
			System.out.println("session equal null");
			return null;
		}
		for ( SessionResult s : sessions ) {
			if ( s.getSession().getId().equals(session.getId())) {
				//System.out.println("Doing if part..");
				sessionResult = s;
				break;
			}
		}
		return sessionResult;
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}
	
}
