package educate.sis.struct.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "struc_teachersubject")
public class TeacherSubject implements Comparable{
	@Id
	private String id;
	@ManyToOne(fetch=FetchType.LAZY)
	private SubjectSection section;
	private String status;
	@OneToOne
	private Subject subject;
	
	private int mainCampus;
	private int otherCampus;
	
	@ManyToOne
	private Teacher teacher;

	public TeacherSubject() {
		setId(lebah.db.UniqueID.getUID());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public SubjectSection getSection() {
		return section;
	}

	public void setSection(SubjectSection section) {
		this.section = section;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Subject getSubject() {
		return subject;
	}

	public void setSubject(Subject subject) {
		this.subject = subject;
	}

	public Teacher getTeacher() {
		return teacher;
	}

	public void setTeacher(Teacher teacher) {
		this.teacher = teacher;
	}

	@Override
	public boolean equals(Object o) {
		TeacherSubject result = (TeacherSubject) o;
		if (result.id.equals(id))
			return true;
		else
			return false;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

//	public void setClassroomSlots(Set<ClassroomSlot> classroomSlots) {
//		this.classroomSlots = classroomSlots;
//	}
//
//	public Set<ClassroomSlot> getClassroomSlots() {
//		return classroomSlots;
//	}

	public int compareTo(Object o) {
		TeacherSubject t2 = (TeacherSubject) o;
		try {
			return this.getSubject().getCode().compareTo(t2.getSubject().getCode());
		} catch ( Exception e ) {
			e.printStackTrace();
			return 0;
		}
	}

	public boolean getMainCampus() {
		return mainCampus == 1;
	}

	public void setMainCampus(boolean mainCampus) {
		this.mainCampus = mainCampus ? 1 : 0;
	}

	public boolean getOtherCampus() {
		return otherCampus == 1;
	}

	public void setOtherCampus(boolean otherCampus) {
		this.otherCampus = otherCampus ? 1 : 0;
	}
	
	

}
