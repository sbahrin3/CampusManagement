package educate.lms2.entity;

import javax.persistence.*;

import java.util.*;
/**
 * 
 * @author Shamsul Bahrin Abd Mutalb
 * @version 1.0
 */

@Entity
@Table(name="lms2_posting")
@Inheritance(strategy=InheritanceType.JOINED)
@DiscriminatorColumn(name="POST_TYPE", discriminatorType=DiscriminatorType.STRING,length=10)
@DiscriminatorValue("P")
public class Posting {
	
	@Id
	@Column(length=50)
	private String id;
	
	@Column(length=50)
	private String subjectId;
	
	@ManyToOne(cascade=CascadeType.PERSIST)
	private UserProfile user;
	@Column(length=20)
	private String type = "";

	
	@Temporal(TemporalType.DATE)
	private Date postDate;
	@Temporal(TemporalType.TIME)
	private Date postTime;
	
	@OneToMany(cascade=CascadeType.PERSIST, mappedBy="posting")
	private List<Reply> replies;
	@OneToMany(cascade=CascadeType.PERSIST, mappedBy="posting")
	private List<Attachment> attachments;
	
	@Lob
	@Column(length=65535)
	private String message;
	
	@Temporal(TemporalType.DATE)
	private Date dueDate;
	private int rank;
	
	public Posting() {
		setId(lebah.db.UniqueID.getUID());
		setType("status");
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Reply> getReplies() {
		return replies;
	}

	public void setReplies(List<Reply> replies) {
		this.replies = replies;
	}

	public UserProfile getUser() {
		return user;
	}

	public void setUser(UserProfile user) {
		this.user = user;
	}

	public Date getPostDate() {
		return postDate;
	}

	public void setPostDate(Date postDate) {
		this.postDate = postDate;
	}

	public Date getPostTime() {
		return postTime;
	}

	public void setPostTime(Date postTime) {
		this.postTime = postTime;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}
	
	

}
