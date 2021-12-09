package educate.sis.module;

import educate.sis.struct.entity.Session;
import educate.sis.struct.entity.Subject;
import educate.sis.struct.entity.SubjectCategory;

public class SubjectVO {

	private Subject subject;
	private Session session;
	private SubjectCategory category;
	
	public SubjectVO() {
		
	}
	public SubjectVO(Subject su, SubjectCategory ca) {
		subject = su;
		category = ca;
	}
	public SubjectVO(Subject su, Session se, SubjectCategory ca) {
		subject = su;
		session = se;
		category = ca;
	}
	
	
	public Session getSession() {
		return session;
	}
	public void setSession(Session session) {
		this.session = session;
	}
	public Subject getSubject() {
		return subject;
	}
	public void setSubject(Subject subject) {
		this.subject = subject;
	}
	public SubjectCategory getCategory() {
		return category;
	}
	public void setCategory(SubjectCategory category) {
		this.category = category;
	}

	@Override
	public boolean equals(Object o) {
		SubjectVO vo = (SubjectVO) o;
		if ( vo.getSubject().getId().equals(this.getSubject().getId()))
			return true;
		return false;
	}
	@Override
	public int hashCode() {
		return this.getSubject().hashCode();
	}	
	

}
