package educate.sis.struct.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import educate.enrollment.entity.Student;
import educate.timetabling.entity.TimetableSlottingLog;

/**
 * 
 * @author Shamsul Bahrin Abd Mutalb
 * @version 1.0
 * 
 * Define group of students.
 */
@Entity
@Table(name="struc_classroom")
public class Classroom {
	
	@Id
	private String id;
	private String name;
	@ManyToOne
	private Program program;
	@ManyToOne
	private Session session;
	@ManyToOne
	private Period period;
	@OneToMany(cascade=CascadeType.PERSIST)
	private Set<Student> students;
	private int capacity;
	private int classroomOrder;
	

	
	public Classroom() {
		setId(lebah.util.UIDGenerator.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Period getPeriod() {
		return period;
	}
	public void setPeriod(Period period) {
		this.period = period;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Session getSession() {
		return session;
	}
	public void setSession(Session session) {
		this.session = session;
	}
	public Set<Student> getStudents() {
		return students;
	}
	public void setStudents(Set<Student> students) {
		this.students = students;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public Program getProgram() {
		return program;
	}

	public void setProgram(Program program) {
		this.program = program;
	}

	public int getClassroomOrder() {
		return classroomOrder;
	}

	public void setClassroomOrder(int classroomOrder) {
		this.classroomOrder = classroomOrder;
	}
	
	public void addStudent(Student student) {
		if ( students == null ) students = new HashSet<Student>();
		students.add(student);
	}
	
	public void removeStudent(Student student) {
		students.remove(student);
	}


	
	
	

}
