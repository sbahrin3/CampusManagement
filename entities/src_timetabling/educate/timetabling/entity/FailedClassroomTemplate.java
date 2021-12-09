package educate.timetabling.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="ttb_failed_template")
public class FailedClassroomTemplate {
	
	@Id @Column(length=50)
	private String id;
	private int weekNumber;
	@OneToOne(cascade=CascadeType.PERSIST) @JoinColumn(name="template_id") 
	private ClassroomTemplate classroomTemplate;
	
	public FailedClassroomTemplate() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getWeekNumber() {
		return weekNumber;
	}
	public void setWeekNumber(int weekNumber) {
		this.weekNumber = weekNumber;
	}
	public ClassroomTemplate getClassroomTemplate() {
		return classroomTemplate;
	}
	public void setClassroomTemplate(ClassroomTemplate classroomTemplate) {
		this.classroomTemplate = classroomTemplate;
	}
	
	

}
