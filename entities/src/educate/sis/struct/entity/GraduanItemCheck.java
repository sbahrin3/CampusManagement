package educate.sis.struct.entity;

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

import educate.enrollment.entity.Student;

@Entity
@Table(name="grad_graduan_item")
public class GraduanItemCheck {
	
	@Id @Column(length=50)
	private String id;
	@ManyToOne @JoinColumn(name="student_id")
	private Student student;
	private int robeStatus;
	private int scrollStatus;
	private int transcriptStatus;
	private int photoStatus;
	private int alumniCardStatus;
	private int tracerStudyStatus;
	
	@Lob
	private String robeRemark;
	@Lob
	private String scrollRemark;
	@Lob
	private String transcriptRemark;
	@Lob
	private String photoRemark;
	@Lob
	private String alumniCardRemark;
	@Lob
	private String tracerStudyRemark;
	
	@Temporal(TemporalType.DATE)
	private Date robeDate;
	@Temporal(TemporalType.DATE)
	private Date scrollDate;
	@Temporal(TemporalType.DATE)
	private Date transcriptDate;
	@Temporal(TemporalType.DATE)
	private Date photoDate;
	@Temporal(TemporalType.DATE)
	private Date alumniCardDate;
	@Temporal(TemporalType.DATE)
	private Date tracerStudyDate;

	
	public GraduanItemCheck() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Student getStudent() {
		return student;
	}
	public void setStudent(Student student) {
		this.student = student;
	}
	public int getRobeStatus() {
		return robeStatus;
	}
	public void setRobeStatus(int robeStatus) {
		this.robeStatus = robeStatus;
	}
	public int getScrollStatus() {
		return scrollStatus;
	}
	public void setScrollStatus(int scrollStatus) {
		this.scrollStatus = scrollStatus;
	}
	public int getTranscriptStatus() {
		return transcriptStatus;
	}
	public void setTranscriptStatus(int transcriptStatus) {
		this.transcriptStatus = transcriptStatus;
	}
	public int getPhotoStatus() {
		return photoStatus;
	}
	public void setPhotoStatus(int photoStatus) {
		this.photoStatus = photoStatus;
	}
	public int getAlumniCardStatus() {
		return alumniCardStatus;
	}
	public void setAlumniCardStatus(int alumniCardStatus) {
		this.alumniCardStatus = alumniCardStatus;
	}
	public int getTracerStudyStatus() {
		return tracerStudyStatus;
	}
	public void setTracerStudyStatus(int tracerStudyStatus) {
		this.tracerStudyStatus = tracerStudyStatus;
	}
	public String getRobeRemark() {
		return robeRemark;
	}
	public void setRobeRemark(String robeRemark) {
		this.robeRemark = robeRemark;
	}
	public String getScrollRemark() {
		return scrollRemark;
	}
	public void setScrollRemark(String scrollRemark) {
		this.scrollRemark = scrollRemark;
	}
	public String getTranscriptRemark() {
		return transcriptRemark;
	}
	public void setTranscriptRemark(String transcriptRemark) {
		this.transcriptRemark = transcriptRemark;
	}
	public String getPhotoRemark() {
		return photoRemark;
	}
	public void setPhotoRemark(String photoRemark) {
		this.photoRemark = photoRemark;
	}
	public String getAlumniCardRemark() {
		return alumniCardRemark;
	}
	public void setAlumniCardRemark(String alumniCardRemark) {
		this.alumniCardRemark = alumniCardRemark;
	}
	public String getTracerStudyRemark() {
		return tracerStudyRemark;
	}
	public void setTracerStudyRemark(String tracerStudyRemark) {
		this.tracerStudyRemark = tracerStudyRemark;
	}

	public Date getRobeDate() {
		return robeDate;
	}

	public void setRobeDate(Date robeDate) {
		this.robeDate = robeDate;
	}

	public Date getScrollDate() {
		return scrollDate;
	}

	public void setScrollDate(Date scrollDate) {
		this.scrollDate = scrollDate;
	}

	public Date getTranscriptDate() {
		return transcriptDate;
	}

	public void setTranscriptDate(Date transcriptDate) {
		this.transcriptDate = transcriptDate;
	}

	public Date getPhotoDate() {
		return photoDate;
	}

	public void setPhotoDate(Date photoDate) {
		this.photoDate = photoDate;
	}

	public Date getAlumniCardDate() {
		return alumniCardDate;
	}

	public void setAlumniCardDate(Date alumniCardDate) {
		this.alumniCardDate = alumniCardDate;
	}

	public Date getTracerStudyDate() {
		return tracerStudyDate;
	}

	public void setTracerStudyDate(Date tracerStudyDate) {
		this.tracerStudyDate = tracerStudyDate;
	}
	

	
}
