package educate.timetabling.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import educate.sis.struct.entity.StudyMode;
import educate.sis.struct.entity.Subject;

@Entity
@Table(name="ttb_preactivity")
public class PreDefineActivity {
	
	@Id @Column(length=50)
	private String id;
	@ManyToOne
	private Subject subject;
	
	@ManyToOne @JoinColumn(name="campus_id")
	private Campus campus;
	
	@ManyToOne
	private ClassroomSection section;
	@ManyToOne
	private StudyMode studyMode;
	private int scheduleType; //0=weekly, 1=monthly
	private int frequency;
	
	public PreDefineActivity() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Subject getSubject() {
		return subject;
	}
	public void setSubject(Subject subject) {
		this.subject = subject;
	}
	public ClassroomSection getSection() {
		return section;
	}
	public void setSection(ClassroomSection section) {
		this.section = section;
	}
	
	
	
	public Campus getCampus() {
		return campus;
	}

	public void setCampus(Campus campus) {
		this.campus = campus;
	}

	public StudyMode getStudyMode() {
		return studyMode;
	}
	public void setStudyMode(StudyMode studyMode) {
		this.studyMode = studyMode;
	}
	public int getScheduleType() {
		return scheduleType;
	}
	public void setScheduleType(int scheduleType) {
		this.scheduleType = scheduleType;
	}
	public int getFrequency() {
		return frequency;
	}
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
	
	
	

}
