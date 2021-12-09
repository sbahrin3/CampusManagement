package educate.timetabling.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import educate.sis.struct.entity.Subject;

@Entity
@Table(name="ttb_stusubject_grp")
public class StudentSubjectGroup {
	
	@Id @Column(length=50)
	private String id;
	//@Column(length=50)
	@Transient
	private String code;
	//@Column(length=100)
	@Transient
	private String name;
	
	@ManyToOne @JoinColumn(name="subject_id")
	private Subject subject;
	
	@ManyToOne @JoinColumn(name="campus_id")
	private Campus campus;
	
	private int groupNumber;
	
	private int semester;
	
	@ManyToOne
	private ClassroomGroupFaculty facultyGroup;
	
	public StudentSubjectGroup() {
		setId(lebah.db.UniqueID.getUID());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCode() {
		if ( facultyGroup != null )
			code = facultyGroup.getCode();
		else
			code = "";
		return code;
	}

	public String getName() {
		if ( facultyGroup != null )
			name = facultyGroup.getName();
		else
			name = "";
		return name;
	}

	public Subject getSubject() {
		return subject;
	}

	public void setSubject(Subject subject) {
		this.subject = subject;
	}

	public int getGroupNumber() {
		return groupNumber;
	}

	public void setGroupNumber(int classNumber) {
		this.groupNumber = classNumber;
	}

	public ClassroomGroupFaculty getFacultyGroup() {
		return facultyGroup;
	}

	public void setFacultyGroup(ClassroomGroupFaculty facultyGroup) {
		this.facultyGroup = facultyGroup;
	}

	public Campus getCampus() {
		return campus;
	}

	public void setCampus(Campus campus) {
		this.campus = campus;
	}

	public int getSemester() {
		return semester;
	}

	public void setSemester(int semester) {
		this.semester = semester;
	}
	

}
