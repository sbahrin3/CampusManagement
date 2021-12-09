package educate.alumni.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity @Table(name="almn_activity_event")
public class AlumniActivityEvent {
	
	@Id @Column(length=50)
	private String id;
	@Column(length=100)
	private String name;
	private String description;
	@Lob
	private String remark;
	@Temporal(TemporalType.DATE)
	private Date startDate;
	@Temporal(TemporalType.DATE)
	private Date endDate;
	@OneToMany(cascade=CascadeType.PERSIST, mappedBy="event")
	private List<EventPhoto> photos;
	@OneToMany(cascade=CascadeType.PERSIST, mappedBy="event")
	private List<EventFeedback> feedbacks;	
	
	public AlumniActivityEvent() {
		setId(lebah.db.UniqueID.getUID());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
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

	public List<EventPhoto> getPhotos() {
		if ( photos == null ) photos = new ArrayList<EventPhoto>();
		return photos;
	}

	public void setPhotos(List<EventPhoto> photos) {
		this.photos = photos;
	}

	public List<EventFeedback> getFeedbacks() {
		if ( feedbacks == null ) feedbacks = new ArrayList<EventFeedback>();
		return feedbacks;
	}

	public void setFeedbacks(List<EventFeedback> feedbacks) {
		this.feedbacks = feedbacks;
	}
	
	
	

}
