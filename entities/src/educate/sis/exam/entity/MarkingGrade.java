package educate.sis.exam.entity;
import javax.persistence.*;
import java.util.*;

@Entity
@Table(name="exam_marking_grade")
public class MarkingGrade {
	
	@Id
	private String id;
	private String name;
	@OneToMany(cascade=CascadeType.ALL, mappedBy="markingGrade")
	private Set<Grade> grades;
//	@OneToMany
//	private Set<Subject> subjects;
	
	public MarkingGrade() {
		setId(lebah.util.UIDGenerator.getUID());
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<Grade> getGrades() {
		return grades;
	}
	public void setGrades(Set<Grade> grades) {
		for(Iterator<Grade>it = grades.iterator();it.hasNext();){
			it.next().setMarkingGrade(this);
		}
		this.grades = grades; 
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

//	public Set<Subject> getSubjects() {
//		return subjects;
//	}
//
//	public void setSubjects(Set<Subject> subjects) {
//		for(Iterator<Subject> it = subjects.iterator();it.hasNext();){
//			it.next().setMarkingGrade(this);
//		}
//		this.subjects = subjects;
//	}
//	
//	public boolean hasSubject(String subjectId) {
//		for ( Subject s : subjects ) {
//			if ( s.getId().equals(subjectId)) {
//				return true;
//			}
//		}
//		return false;
//	}

	
	
}
