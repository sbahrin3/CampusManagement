/**
 * @author Shaiful
 * @since Jun 12, 2009
 */
package educate.lms.message.entity;

import static javax.persistence.TemporalType.TIMESTAMP;

import java.text.DateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;

@Entity
@Table(name="lms_message")

public class Message {

	@Id
	@Column(length=16)
	private String id;
	
	@Column(name="sender_id", length=16)	
	private String senderId;
	
	@Column(name="receiver_id", length=16)
	private String receiverId;
	
	@Column(name="subject_id", length=16)
	private String subjectId;
	
	@Column(length=50)
	private String title;
	
	@Lob
	private String content;
	
	@Column(name="date_of_receive")
	@Temporal(TIMESTAMP)
	private Date date;

	@Column(name="sender_name", length = 200)
	private String senderName;
	
	private int isRead;
	
	// CONSTRUCTOR
	public Message() {
		setId(lebah.db.UniqueID.getUID());		
	}
	
	// GETTER AND SETTER
	public String getId() {
		if (id == null) {
			id = "";
		}
		return id;
	}

	public void setId(String id) {
		if (id == null) {
			this.id = "";
		} else {
			this.id = id;
		}
	}

	public String getSenderId() {
		if (senderId == null) {
			senderId = "";
		}
		return senderId;
	}

	public void setSenderId(String senderId) {
		if (senderId == null) {
			this.senderId = "";
		} else {
			this.senderId = senderId;
		}
	}

	public String getReceiverId() {
		if (receiverId == null) {
			receiverId = "";
		}
		return receiverId;
	}

	public void setReceiverId(String receiverId) {
		if (receiverId == null) {
			this.receiverId = "";
		} else {
			this.receiverId = receiverId;
		}
	}

	public String getTitle() {
		if (title == null) {
			title = "[No Title]";
		}
		return title;
	}

	public void setTitle(String title) {
		if (title == null) {
			this.title = "[No Title]";
		} else {
			this.title = title;
		}
	}

	public String getContent() {
		if (content == null) {
			content = "";
		}
		return content;
	}

	public void setContent(String content) {
		if (content == null) {
			this.content = "";
		} else {
			this.content = content;
		}
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	public String getSenderName() {
		if (senderName == null) {
			senderName = "";
		}
		return senderName;
	}

	public void setSenderName(String senderName) {
		if (senderName == null) {
			this.senderName = "";
		} else {
			this.senderName = senderName;
		}
	}

	public String getSubjectId() {
		if (subjectId == null) {
			subjectId = "";
		}
		return subjectId;
	}

	public void setSubjectId(String subjectId) {
		if (subjectId == null) {
			this.subjectId = "";
		} else {
			this.subjectId = subjectId;
		}
	}

	/**
	 * Method for converting the date into the
	 * required format as a string.
	 * @param date
	 * @return String
	 */
	public String getFormattedDate() {
		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.SHORT);
		return df.format(date);
	}

	public int getIsRead() {
		return isRead;
	}

	public void setIsRead(int isRead) {
		this.isRead = isRead;
	}
	
	public void setRead(boolean read) {
		isRead = read ? 1 : 0;
	}
	
	public boolean getRead() {
		return isRead == 1;
	}
}
