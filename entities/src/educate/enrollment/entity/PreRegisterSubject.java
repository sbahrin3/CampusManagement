package educate.enrollment.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lebah.db.UniqueID;

import educate.sis.struct.entity.Session;
import educate.sis.struct.entity.Subject;

@Entity
@Table(name="enrl_preregister")
public class PreRegisterSubject {
	@Id
	private String id;
	@ManyToOne
	private Student student;
	@ManyToOne
	private Subject subject;
	@ManyToOne
	private Session session;
	private String status;
	@Temporal(TemporalType.DATE)
    private Date confirmDate;
	
	public PreRegisterSubject(){
		setId(UniqueID.getUID());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

	public Subject getSubject() {
		return subject;
	}

	public void setSubject(Subject subject) {
		this.subject = subject;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
    public Date getConfirmDate() {
        return confirmDate;
    }

    public void setConfirmDate(Date confirmDate) {
        this.confirmDate = confirmDate;	
    }
}
