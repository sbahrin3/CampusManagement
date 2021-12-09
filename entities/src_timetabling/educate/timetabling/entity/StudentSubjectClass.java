package educate.timetabling.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import educate.sis.struct.entity.Subject;

@Entity
@Table(name="ttb_stusubject_class")
public class StudentSubjectClass {
	
	@Id @Column(length=50)
	private String id;
	@Column(length=50)
	private String code;
	@Column(length=100)
	private String name;
	
	@ManyToOne @JoinColumn(name="subject_id")
	private Subject subject;
	
	private int classNumber;
	
	@OneToMany
	private List<ClassroomTemplateGroup> groups;
	
	public StudentSubjectClass() {
		setId(lebah.db.UniqueID.getUID());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Subject getSubject() {
		return subject;
	}

	public void setSubject(Subject subject) {
		this.subject = subject;
	}

	public int getClassNumber() {
		return classNumber;
	}

	public void setClassNumber(int classNumber) {
		this.classNumber = classNumber;
	}

	public List<ClassroomTemplateGroup> getGroups() {
		return groups;
	}

	public void setGroups(List<ClassroomTemplateGroup> groups) {
		this.groups = groups;
	}
	
	

}
