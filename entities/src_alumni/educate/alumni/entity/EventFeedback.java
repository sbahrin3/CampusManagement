package educate.alumni.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity @Table(name="almn_event_feedback")
public class EventFeedback {
	
	@Id @Column(length=50)
	private String id;
	@ManyToOne @JoinColumn(name="event_id")
	private AlumniActivityEvent event;
	@ManyToOne @JoinColumn(name="member_id")
	private AlumniMember member;
	@Lob
	private String remark;
	@Temporal(TemporalType.DATE)
	private Date date;
	@Column(length=100)
	private String portalUsername;
	
	
	public EventFeedback() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public AlumniActivityEvent getEvent() {
		return event;
	}
	public void setEvent(AlumniActivityEvent event) {
		this.event = event;
	}
	public AlumniMember getMember() {
		return member;
	}
	public void setMember(AlumniMember member) {
		this.member = member;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}

	public String getPortalUsername() {
		return portalUsername;
	}

	public void setPortalUsername(String portalUsername) {
		this.portalUsername = portalUsername;
	}
	
	
	
	

}
