package educate.enrollment.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import educate.sis.struct.entity.Subject;
import educate.sis.struct.entity.SubjectCategory;
import educate.sis.struct.entity.SubjectSection;
import educate.timetabling.entity.ClassroomSection; 

@Entity
@Table(name="enrl_studentsubject")
public class StudentSubject {
	@Id
	private String id;
	private String status;
	@OneToOne(fetch= FetchType.EAGER)
	private Subject subject;
	@OneToOne(fetch= FetchType.EAGER)
	private SubjectCategory category;
	@ManyToOne(fetch= FetchType.EAGER)
	private StudentStatus studentStatus;
	
	
	//main section
	@ManyToOne(fetch= FetchType.LAZY)
	private SubjectSection section;
	
	//section by classroom type
	//@OneToMany(cascade=CascadeType.PERSIST)
	//private List<ClassroomSection> classroomSections;
	
	//student subject status
	/*
	RG - Register
	RP - Repeat
	RS - Resit
	CT - Credit Transfer
	EX - Exempted
	CE - Credit Earned
	AU - Audit
	*/
	@Column(length=50)
	private String subjectStatus;
	
	public StudentSubject(){
		setId(lebah.db.UniqueID.getUID());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public SubjectCategory getCategory() {
		return category;
	}

	public void setCategory(SubjectCategory category) {
		this.category = category;
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

	public StudentStatus getStudentStatus() {
		return studentStatus;
	}

	public void setStudentStatus(StudentStatus studentStatus) {
		System.out.println("student status: " + studentStatus);
		this.studentStatus = studentStatus;
	}
	
	@Override 
	public boolean equals(Object o) {
		StudentSubject result = (StudentSubject)o;
		if(result.id.equals(id))
			return true;
		else
			return false;
	}
	@Override
	public int hashCode() {
		return id.hashCode();
	}

	/*
	public List<ClassroomSection> getClassroomSections() {
		return classroomSections;
	}

	public void setClassroomSections(List<ClassroomSection> classroomSections) {
		this.classroomSections = classroomSections;
	}
	*/

	public String getSubjectStatus() {
		return subjectStatus;
	}

	public void setSubjectStatus(String subjectStatus) {
		this.subjectStatus = subjectStatus;
	}
	
	
	
	
}
