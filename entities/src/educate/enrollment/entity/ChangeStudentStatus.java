/**
 * @author Shaiful
 * @since Nov 18, 2009
 */
package educate.enrollment.entity;
/*
 * History
 * -------
 * #	Date			Name				Remarks
 * ----	--------------	------------------	---------------------------------------------------
 * 1.	Nov 18, 2009	Shaiful Nizam		File created.
 * 2.	Nov 23, 2009	Shaiful Nizam		Add field updatedBy.
 */

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import educate.sis.struct.entity.Session;

@Entity
@Table(name="enrl_change_studentstatus")
public class ChangeStudentStatus {

	@Id
	private String id;
	
	@Column(name="date_of_update")
	private Date dateOfUpdate;
	
	@ManyToOne
	private Student student;
	
	@ManyToOne
	private Session session;
	
	@Column(name="old_status", length=50)
	private String oldStatus;
	
	@Column(name="new_status", length=50)
	private String newStatus;
	
	@Column(name="updated_by", length=20)
	private String updatedBy;
	
	public ChangeStudentStatus() {
		setId(lebah.db.UniqueID.getUID());		
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Date getDateOfUpdate() {
		return dateOfUpdate;
	}
	public String getFormattedDateOfUpdate() {
		return formatDate(dateOfUpdate);
	}
	public void setDateOfUpdate(Date dateOfUpdate) {
		this.dateOfUpdate = dateOfUpdate;
	}
	public Student getStudent() {
		return student;
	}
	public void setStudent(Student student) {
		this.student = student;
	}
	public Session getSession() {
		return session;
	}
	public void setSession(Session session) {
		this.session = session;
	}
	public String getOldStatus() {
		if (oldStatus == null) {
			oldStatus = "";
		}
		return oldStatus;
	}
	public void setOldStatus(String oldStatus) {
		if (oldStatus == null) {
			this.oldStatus = "";
		} else {
			this.oldStatus = oldStatus;
		}
	}
	public String getNewStatus() {
		if (newStatus == null) {
			newStatus = "";
		}
		return newStatus;
	}
	public void setNewStatus(String newStatus) {
		if (newStatus == null) {
			this.newStatus = "";
		} else {
			this.newStatus = newStatus;
		}
	}
	public String getUpdatedBy() {
		if (updatedBy == null) {
			updatedBy = "";
		}
		return updatedBy;
	}
	public void setUpdatedBy(String updatedBy) {
		if (updatedBy == null) {
			this.updatedBy = "";
		} else {
			this.updatedBy = updatedBy;
		}
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
