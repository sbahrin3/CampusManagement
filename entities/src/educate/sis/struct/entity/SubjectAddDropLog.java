package educate.sis.struct.entity;

import java.util.Date;
import java.util.Set;

import javax.persistence.*;

import educate.enrollment.entity.Student;

@Entity
@Table(name="struc_adddrop_log")
public class SubjectAddDropLog {
	
	@Id
	private String id;
	@ManyToOne
	private Student student;
	@Temporal(TemporalType.DATE)
	private Date date;
	@Temporal(TemporalType.TIME)
	private Date time;
	@OneToMany(cascade=CascadeType.PERSIST)
	private Set<Subject> originalSubjects;
	@OneToMany(cascade=CascadeType.PERSIST)
	private Set<Subject> addedSubjects;
	@OneToMany(cascade=CascadeType.PERSIST)
	private Set<Subject> droppedSubjects;
	@OneToMany(cascade=CascadeType.PERSIST)
	private Set<Subject> currentSubjects;
	private String userId;
	@ManyToOne
	private Session session;
	@ManyToOne
	private Period period;
	
	public SubjectAddDropLog() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public Set<Subject> getAddedSubjects() {
		return addedSubjects;
	}
	public void setAddedSubjects(Set<Subject> addedSubjects) {
		this.addedSubjects = addedSubjects;
	}
	public Set<Subject> getCurrentSubjects() {
		return currentSubjects;
	}
	public void setCurrentSubjects(Set<Subject> currentSubjects) {
		this.currentSubjects = currentSubjects;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public Set<Subject> getDroppedSubjects() {
		return droppedSubjects;
	}
	public void setDroppedSubjects(Set<Subject> droppedSubjects) {
		this.droppedSubjects = droppedSubjects;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Set<Subject> getOriginalSubjects() {
		return originalSubjects;
	}
	public void setOriginalSubjects(Set<Subject> originalSubjects) {
		this.originalSubjects = originalSubjects;
	}
	public Student getStudent() {
		return student;
	}
	public void setStudent(Student student) {
		this.student = student;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Period getPeriod() {
		return period;
	}

	public void setPeriod(Period period) {
		this.period = period;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}
	
	

}
