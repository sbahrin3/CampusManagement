package educate.sis.exam.entity;

import javax.persistence.*;
import java.util.*;
import educate.sis.struct.entity.*;

@Entity
@Table(name="exam_session_result")
public class SessionResult implements Comparable {
	
	@Id
	private String id;
	@ManyToOne
	private ExamResult result;
	@OneToOne
	private Session session;
	@OneToOne
	private Period period;
	@OneToMany(cascade=CascadeType.ALL, mappedBy="session")
	private Set<SubjectResult> subjects;
	private double gpa;
	private int totalCreditHrs;
	private double totalPoint;
	private double cgpa;
	private int cumulativeCreditHrs;


	public SessionResult() {
		setId(lebah.util.UIDGenerator.getUID());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Period getPeriod() {
		return period;
	}

	public void setPeriod(Period period) {
		this.period = period;
	}

	public ExamResult getResult() {
		return result;
	}

	public void setResult(ExamResult result) {
		this.result = result;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public Set<SubjectResult> getSubjects() {
		return subjects;
	}

	public void setSubjects(Set<SubjectResult> subjects) {
		this.subjects = subjects;
	}

	public void addSubject(SubjectResult subject) {
		if ( subjects == null ) subjects = new HashSet<SubjectResult>();
		subject.setSession(this);
		subjects.remove(subject);
		subjects.add(subject);
	}
	
	public boolean hasSubject(SubjectResult subject) {
		if ( subjects == null ) return false;
		return subjects.contains(subject);
	}
	
	public SubjectResult getSubject(String code) {
		SubjectResult sr = null;
		for ( SubjectResult s : subjects ) {
			if ( s.getSubject().getCode().equals(code)) {
				sr = s;
				break;
			}
		}
		return sr;
	}
	

	public void removeSubject(SubjectResult subject) {
		if ( subjects == null ) return;
		if (hasSubject(subject)) {
			System.out.println("got this subject to be removed");
		}
		else {
			System.out.println("Nothing..");
		}
		subjects.remove(subject);
	}
	
	public void removeSubject(String code) {
		SubjectResult sr = null;
		for ( SubjectResult s : subjects ) {
			if ( s.getSubject().getCode().equals(code)) {
				sr = s;
				break;
			}
		}
		removeSubject(sr);
	}
	
	
	@Override
	public boolean equals(Object o) {
		SessionResult result = (SessionResult)o;
		Session session = result.getSession();
		//Period period = result.getPeriod();
		Session session2 = getSession();
		//Period period2 = getPeriod();
		if(session.getName().equals(session2.getName()))
			return true;
		else
			return false;
	}
	
	@Override
	public int hashCode() {
		return getSession().hashCode();
	}

	public int compareTo(Object o) {
		SessionResult s = (SessionResult) o;
		if( s.session.getStartDate().after(session.getStartDate()) ){
			return -1;
		}else{
			return 1;
		}
		
		//return 0;
	}

	public double getCgpa() {
		return cgpa;
	}

	public void setCgpa(double cgpa) {
		this.cgpa = cgpa;
	}

	public double getCumulativeCreditHrs() {
		return cumulativeCreditHrs;
	}

	public void setCumulativeCreditHrs(int cumulativeCreditHrs) {
		this.cumulativeCreditHrs = cumulativeCreditHrs;
	}

	public double getGpa() {
		calTotalCreditHrs();
		calTotalPoint();
		gpa = totalPoint/totalCreditHrs;
		return gpa;
	}


	public int getTotalCreditHrs() {
		calTotalCreditHrs();
		return totalCreditHrs;
	}

	private void calTotalCreditHrs() {
		int total = 0;
		for ( SubjectResult s : subjects ) {
			total += s.getSubject().getCredithrs();
		}
		totalCreditHrs = total;
	}

	public double getTotalPoint() {
		calTotalPoint();
		return totalPoint;
	}

	private void calTotalPoint() {
		double total = 0;
		for ( SubjectResult s : subjects ) {
			total += s.getPoint();
		}		
		totalPoint = total;
	}


}
