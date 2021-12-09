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

import educate.sis.struct.entity.Period;
import educate.sis.struct.entity.Subject;
import educate.sis.struct.entity.SubjectCategory;
import educate.sis.struct.entity.SubjectSection;
import educate.timetabling.entity.ClassroomSection; 


@Entity
@Table(name="enrl_studentsubject_temp")
public class StudentSubjectTemp {
	
	@Id
	private String id;
	@Column(length=50)
	private String status;
	@OneToOne(fetch= FetchType.LAZY)
	private Subject subject;
	@OneToOne(fetch= FetchType.LAZY)
	private SubjectCategory category;
	@ManyToOne(fetch= FetchType.LAZY)
	private Period period;	
	@ManyToOne(fetch= FetchType.LAZY)
	private Student student;
	
	//main section
	@ManyToOne(fetch= FetchType.LAZY)
	private SubjectSection section;
	
	//section by classroom type
	//@OneToMany(cascade=CascadeType.PERSIST)
	//private List<ClassroomSection> classroomSections;

	@Column(length=50)
	private String subjectStatus;
	
	public StudentSubjectTemp() {
		setId(lebah.db.UniqueID.getUID());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public SubjectCategory getCategory() {
		return category;
	}

	public void setCategory(SubjectCategory category) {
		this.category = category;
	}

	public Period getPeriod() {
		return period;
	}

	public void setPeriod(Period period) {
		this.period = period;
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

	public SubjectSection getSection() {
		return section;
	}

	public void setSection(SubjectSection section) {
		this.section = section;
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
