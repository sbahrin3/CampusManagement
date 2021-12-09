package educate.timetabling.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import educate.sis.struct.entity.Subject;

@Entity
@Table(name="ttb_studentgroup")
public class TeacherClassroomGroup {
	
	@Id @Column(length=50)
	private String id;
	@OneToOne
	private Subject subject;
	@OneToOne
	private ClassroomSection classroomSection;
	private int groupNumber;
	
	public TeacherClassroomGroup() {
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
	public ClassroomSection getClassroomSection() {
		return classroomSection;
	}
	public void setClassroomSection(ClassroomSection classroomSection) {
		this.classroomSection = classroomSection;
	}
	public int getGroupNumber() {
		return groupNumber;
	}
	public void setGroupNumber(int groupNumber) {
		this.groupNumber = groupNumber;
	}
	
	
	


}
