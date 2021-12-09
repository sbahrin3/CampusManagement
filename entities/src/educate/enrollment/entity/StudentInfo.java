package educate.enrollment.entity;

import javax.persistence.*;
import java.util.*;

/**
 * 
 * @author Shamsul Bahrin Abd Mutalb
 * @version 1.0
 * 
 * This entity keep all student's entities that belong to the same student.  The reason for this are:
 * The original Student entity, can only hold one program.  But, a student may take more than one programs.
 * A student may take 2 or more programs at the same time (concurrently), or he may take finished a program
 * first, then continue taking other program.
 * 
 * Each student's in a StudentInfo may have different matric no.
 */


@Entity
@Table(name="enrl_student_info")
public class StudentInfo {
	
	@Id
	private String id;
	@OneToMany
	private Set<Student> students;
	
	public StudentInfo() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Set<Student> getStudents() {
		return students;
	}
	public void setStudents(Set<Student> students) {
		this.students = students;
	}
	
	public void addStudent(Student student) {
		if ( students == null ) students = new HashSet<Student>();
		//student.setStudentInfo(this);
		students.add(student);
	}
	
	public List<Student> getStudentsInOrder() {
		List<Student> list = new ArrayList<Student>();
		if ( students == null ) return null;
		list.addAll(students);
		Collections.sort(list, new StudentListComparator());
		return list;
	}
	
	
	class StudentListComparator extends educate.util.MyComparator {

		public int compare(Object o1, Object o2) {
			Student s1 = (Student) o1;
			Student s2 = (Student) o2;
			if ( s1.getIntake() == null || s2.getIntake() == null ) return 0;
			if ( s1.getIntake().getStartDate().before(s2.getIntake().getStartDate())) return 1;
			else if ( s1.getIntake().getStartDate().after(s2.getIntake().getStartDate())) return -1;
			return 0;
		}
		
	}

	
	

}
