package educate.questionnare.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import educate.sis.struct.entity.Program;
import educate.sis.struct.entity.Session;
import educate.sis.struct.entity.Subject;
import educate.sis.struct.entity.Teacher;

@Entity
@Table(name="te_questionnare")
public class TEQuestionnare {
	
	@Id @Column(length=50)
	private String id;
	@Temporal(TemporalType.DATE)
	private Date startDate;
	@Temporal(TemporalType.DATE)
	private Date endDate;
	@Column(length=20)
	private String audienceType; //student, teacher, public
	@ManyToOne @JoinColumn(name="set_id")
	private TESet set; //questionnare set
	private String userId; //id of user publish this questionnare
	@Temporal(TemporalType.DATE)
	private Date createDate;
	
	@ManyToOne @JoinColumn(name="program_id")
	private Program program;
	@ManyToOne @JoinColumn(name="subject_id")
	private Subject subject;
	@ManyToOne @JoinColumn(name="teacher_id")
	private Teacher teacher;
	@ManyToOne @JoinColumn(name="intake_id")
	private Session intake;
	@Column(length=50)
	private String publishedId;
	
	
	public TEQuestionnare() {
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
	public String getAudienceType() {
		return audienceType;
	}
	public void setAudienceType(String audienceType) {
		this.audienceType = audienceType;
	}
	public TESet getSet() {
		return set;
	}
	public void setSet(TESet set) {
		this.set = set;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public Program getProgram() {
		return program;
	}
	public void setProgram(Program program) {
		this.program = program;
	}
	public Subject getSubject() {
		return subject;
	}
	public void setSubject(Subject subject) {
		this.subject = subject;
	}
	public Session getIntake() {
		return intake;
	}
	public void setIntake(Session intake) {
		this.intake = intake;
	}

	public Teacher getTeacher() {
		return teacher;
	}

	public void setTeacher(Teacher teacher) {
		this.teacher = teacher;
	}

	public String getPublishedId() {
		return publishedId;
	}

	public void setPublishedId(String publishedId) {
		this.publishedId = publishedId;
	}
	
	
	

}
