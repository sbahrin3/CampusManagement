package educate.lms.entity;


import javax.persistence.*;

import java.util.*;

@Entity
@Table(name="lms_chat_message")
public class ChatMessage {
	
	@Id
	private String id;
	private String userId;
	private String userName;
	private String subjectId;
	@Temporal(TemporalType.DATE)
	private Date datePosted; 
	private String message;
	private int seq;
	
	public ChatMessage() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public ChatMessage(String userId, String message) {
		setId(lebah.db.UniqueID.getUID());
		setUserId(userId);
		setMessage(message);
		setDatePosted(new Date());
	}

	public Date getDatePosted() {
		return datePosted;
	}

	public void setDatePosted(Date datePosted) {
		this.datePosted = datePosted;
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

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public String getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}
	
	

}
