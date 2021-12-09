package educate.sis.struct.entity;



import javax.persistence.*;
import java.util.*;

@Deprecated
@Entity
@Table(name="struc_teachsubject")
public class TeachSubject {
	@Id
	private String id;
	@OneToOne
	private Subject subject;
	@OneToMany(cascade=CascadeType.ALL)
	private Set<Teacher> teachers;
	
	public TeachSubject() {
		setId(lebah.util.UIDGenerator.getUID());
	}
	public TeachSubject(String id) {
		setId(id);
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
	public Set<Teacher> getTeachers() {
		return teachers;
	}
	public void setTeachers(Set<Teacher> teachers) {
		this.teachers = teachers;
	}
	
	public void addTeacher(Teacher teacher) {
		if ( teachers == null ) teachers = new HashSet<Teacher>();
		//teacher.setTeachSubject(this);
		teachers.remove(teacher);
		teachers.add(teacher);
		
		/*
		try {
			educate.sis.registration.TeacherRegistrationModule.addTeacher(subject.getId(), teacher.getId());
		} catch ( Exception e ) {
			System.out.println(e.getMessage());
		}
		*/
	}
	
	public void deleteTeacher(Teacher teacher) {
		teachers.remove(teacher);
		/*
		try {
			educate.sis.registration.TeacherRegistrationModule.removeTeacher(subject.getId(), teacher.getId());
		} catch ( Exception e ) {
			System.out.println(e.getMessage());
		}	
		*/	
	}

}
