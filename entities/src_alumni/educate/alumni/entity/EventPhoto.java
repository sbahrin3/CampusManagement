package educate.alumni.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity @Table(name="almn_event_photo")
public class EventPhoto {
	
	@Id @Column(length=50)
	private String id;
	private String photoFileName;
	private String avatarFileName;
	@ManyToOne @JoinColumn(name="event_id")
	private AlumniActivityEvent event;
	
	public EventPhoto() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPhotoFileName() {
		return photoFileName;
	}
	public void setPhotoFileName(String photoFileName) {
		this.photoFileName = photoFileName;
	}
	public String getAvatarFileName() {
		return avatarFileName;
	}
	public void setAvatarFileName(String avatarFileName) {
		this.avatarFileName = avatarFileName;
	}

	public AlumniActivityEvent getEvent() {
		return event;
	}

	public void setEvent(AlumniActivityEvent event) {
		this.event = event;
	}
	
	

}
