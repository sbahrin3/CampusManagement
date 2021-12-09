package educate.lms.entity;

import javax.persistence.*;

import java.util.*;

@Entity
@Table(name="lms_assignment")
public class Assignment {
	
	@Id
	private String id;
	@Column(length=255)
	private String subject;
	@Lob
	@Column(length=65535)
	private String message;
	@OneToMany(cascade=CascadeType.PERSIST)
	private Set<Attachment> attachments;
	
	private String userId;
	@Temporal(TemporalType.DATE)
	private Date datePosted; 
	
	public Assignment() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public Set<Attachment> getAttachments() {
		return attachments;
	}
	public void setAttachments(Set<Attachment> attachments) {
		this.attachments = attachments;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Date getDatePosted() {
		return datePosted;
	}

	public void setDatePosted(Date datePosted) {
		this.datePosted = datePosted;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	
	

}
