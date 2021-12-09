package educate.sis.exam.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import educate.sis.struct.entity.Subject;

@Entity
@Table(name="exam_course_group")
public class CourseGroup {
	
	@Id @Column(length=50)
	private String id;
	@Column(length=50)
	private String code;
	@Column(length=100)
	private String name;
	
	@OneToMany(cascade=CascadeType.PERSIST)
	private List<Subject> subjects;
	
	public CourseGroup() {
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

	public List<Subject> getSubjects() {
		return subjects;
	}

	public void setSubjects(List<Subject> subjects) {
		this.subjects = subjects;
	}
	
	

}
