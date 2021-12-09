package educate.sis.exam.entity;

import educate.enrollment.entity.Student;
import java.util.*;

public class Transcript {
	
	private Student student;
	private Vector<TranscriptSection> sections;
	
	public Student getStudent() {
		return student;
	}
	public void setStudent(Student student) {
		this.student = student;
	}
	public Vector<TranscriptSection> getSections() {
		return sections;
	}
	public void setSections(Vector<TranscriptSection> sections) {
		this.sections = sections;
	}
	
	public void addSection(TranscriptSection section) {
		if ( sections == null ) sections = new Vector<TranscriptSection>();
		sections.addElement(section);
	}
	

}
