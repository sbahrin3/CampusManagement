package educate.timetabling.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="ttb_failed_log")
public class FailedTemplateLog {
	
	@Id @Column(length=50)
	private String id;
	@Column(length=50)
	private String createId;
	@Temporal(TemporalType.TIMESTAMP)
	private Date logDate;
	@Column(length=50)
	private String subjectId;
	@Column(length=50)
	private String subjectCode;
	@Column(length=50)
	private String subjectName;
	@Column(length=50)
	private String activityName;
	@Column(length=50)
	private String roomCode;
	@Column(length=50)
	private String roomName;
	@Column(length=255)
	private String teacherNames;
	@Column(length=50)
	private String conflictCode;
	@Column(length=255)
	private String conflictMessage;
	
	public FailedTemplateLog() {
		setId(lebah.db.UniqueID.getUID());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCreateId() {
		return createId;
	}

	public void setCreateId(String createId) {
		this.createId = createId;
	}

	public Date getLogDate() {
		return logDate;
	}

	public void setLogDate(Date logDate) {
		this.logDate = logDate;
	}

	public String getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}

	public String getSubjectCode() {
		return subjectCode;
	}

	public void setSubjectCode(String subjectCode) {
		this.subjectCode = subjectCode;
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public String getRoomCode() {
		return roomCode;
	}

	public void setRoomCode(String roomCode) {
		this.roomCode = roomCode;
	}

	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public String getTeacherNames() {
		return teacherNames;
	}

	public void setTeacherNames(String teacherNames) {
		this.teacherNames = teacherNames;
	}

	public String getConflictCode() {
		return conflictCode;
	}

	public void setConflictCode(String conflictCode) {
		this.conflictCode = conflictCode;
	}

	public String getConflictMessage() {
		return conflictMessage;
	}

	public void setConflictMessage(String conflictMessage) {
		this.conflictMessage = conflictMessage;
	}
	
	

}
