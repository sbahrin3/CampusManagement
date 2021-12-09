package educate.sis.struct.entity;

import javax.persistence.*;

import educate.sis.exam.entity.Grade;
import educate.sis.exam.entity.MarkingGrade;

import java.util.*;

@Entity
@Table(name="struc_grad_requirement")
public class GraduationRequirement {
	
	@Id
	private String id;
	@ManyToOne
	private Program program;
	private String type;
	@ManyToOne
	private Session intake;
	@OneToMany(cascade=CascadeType.ALL)
	private Set<CategoryCount> categoryCounts;
	private double minCGPA;
	@OneToMany(cascade=CascadeType.ALL)
	private Set<GraduationSubjectRequirement> subjects;
	
	
	public GraduationRequirement() {
		setId(lebah.db.UniqueID.getUID());
		type = "";
	}
	
	public Set<CategoryCount> getCategoryCounts() {
		return categoryCounts;
	}
	public void setCategoryCounts(Set<CategoryCount> categoryCounts) {
		this.categoryCounts = categoryCounts;
	}
	public void addCategoryCount(CategoryCount count) {
		if ( categoryCounts == null ) categoryCounts = new HashSet<CategoryCount>();
		boolean add = true;
		for ( CategoryCount c : categoryCounts) {
			if ( c.getCategory().getId().equals(count.getCategory().getId())) {
				c.setCount(count.getCount());
				add = false;
			}
		}
		if ( add ) categoryCounts.add(count);
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Session getIntake() {
		return intake;
	}
	public void setIntake(Session intake) {
		this.intake = intake;
	}
	public Program getProgram() {
		return program;
	}
	public void setProgram(Program program) {
		this.program = program;
	}

	public double getMinCGPA() {
		return minCGPA;
	}

	public void setMinCGPA(double minCGPA) {
		this.minCGPA = minCGPA;
	}

	public Set<GraduationSubjectRequirement> getSubjects() {
		return subjects;
	}

	public void setSubjects(Set<GraduationSubjectRequirement> subjects) {
		this.subjects = subjects;
	}
	
	public void addSubjectRequirement(Subject subject, String mark) {
		if ( subjects == null ) subjects = new HashSet<GraduationSubjectRequirement>();
		boolean found = false;
		GraduationSubjectRequirement subjectRequirement = null;
		for ( GraduationSubjectRequirement s : subjects ) {
			if ( s.getSubject().getId().equals(subject.getId())) {
				found = true;
				subjectRequirement = s;
				break;
			}
		}
		if ( !found ) {
			subjectRequirement = new GraduationSubjectRequirement();
			subjectRequirement.setSubject(subject);
			subjectRequirement.setMark(mark);
			
			double point = getPoint(subject, mark);
			subjectRequirement.setPoint(point);
			if ( !"".equals(mark)) subjects.add(subjectRequirement);
		}
		else {
			if ( !"".equals(mark)) {
				double point = getPoint(subject, mark);
				subjectRequirement.setPoint(point);
				subjectRequirement.setMark(mark);
			}
			else subjects.remove(subjectRequirement);
		}
	}

	private double getPoint(Subject subject, String mark) {
		MarkingGrade marking = subject.getMarkingGrade();
		Set<Grade> grades = marking.getGrades();
		double point = 0.0;
		for ( Grade g : grades ) {
			if ( mark.equals(g.getLetter())) {
				point = g.getPoint();
				break;
			}
		}
		return point;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	

}
