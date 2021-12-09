package educate.enrollment.report;

import java.util.LinkedHashSet;
import java.util.Set;

import educate.sis.struct.entity.Session;
import educate.sis.struct.entity.SessionPath;

public class Path {
	
	private SessionPath sessionPath;
	private Set<Session> sessions;
	
	public SessionPath getSessionPath() {
		return sessionPath;
	}
	public void setSessionPath(SessionPath sessionPath) {
		this.sessionPath = sessionPath;
	}
	public Set<Session> getSessions() {
		return sessions;
	}
	public void setSessions(Set<Session> sessions) {
		this.sessions = sessions;
	}
	public void add(Session session) {
		if ( sessions == null ) sessions = new LinkedHashSet<Session>();
		sessions.add(session);
	}
	
	

}
