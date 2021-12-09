package educate.timetabling.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="ttb_activity_session_date")
public class ActivitySessionDate {

	@Id @Column(length=50)
	private String id;
	@Temporal(TemporalType.DATE)
	private Date startDate;
	@Temporal(TemporalType.DATE)
	private Date endDate;
	
	@ManyToOne @JoinColumn(name="activitySession_id")
	private ActivitySession activitySession;
	
	
	public ActivitySessionDate() {
		setId(lebah.db.UniqueID.getUID());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public ActivitySession getActivitySession() {
		return activitySession;
	}

	public void setActivitySession(ActivitySession activitySession) {
		this.activitySession = activitySession;
	}
	
	
	
	
	
}
