package educate.enrollment.entity;
/*
 * History
 * -------
 * #	Date			Name				Remarks
 * ----	--------------	------------------	---------------------------------------------------
 * 1.	Nov 10, 2009	Shaiful Nizam		Change "status" field type from String to int. 
 */

import javax.persistence.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import educate.sis.struct.entity.Subject;
import educate.sis.struct.entity.*;

@Entity
@Table(name="enrl_defer_exam")
public class DeferExam {
	
	@Id
	private String id;
	@ManyToOne
	private Student student;
	@OneToOne
	private Subject subject;
	@OneToOne
	private Session session;
	@Column(name="date_of_apply")
	private Date dateOfApply;
	@Column(name="date_of_process")
	private Date dateOfProcess;
	/*
	 * The values for status is obtained from exam.defer.constant.DeferExamStatus
	 */
	private int status;
	
	public DeferExam(){
		setId(lebah.db.UniqueID.getUID());
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

	public Date getDateOfApply() {
		return dateOfApply;
	}
	
	public String getFormattedDateOfApply() {
		return formatDate(dateOfApply);
	}

	public void setDateOfApply(Date dateOfApply) {
		this.dateOfApply = dateOfApply;
	}

	public Date getDateOfProcess() {
		return dateOfProcess;
	}
	
	public String getFormattedDateOfProcess() {
		return formatDate(dateOfProcess);
	}

	public void setDateOfProcess(Date dateOfProcess) {
		this.dateOfProcess = dateOfProcess;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	private synchronized String formatDate(Date date) {
		if (date == null) {
			return "";
		} else {
			DateFormat df = new SimpleDateFormat("dd MMM yyyy, hh:mm a");
			return df.format(date);
		}
	}
}
