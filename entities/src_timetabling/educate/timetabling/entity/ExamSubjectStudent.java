package educate.timetabling.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import educate.enrollment.entity.Student;
import educate.sis.struct.entity.StudyMode;
import educate.sis.struct.entity.Subject;

@Entity
@Table(name="ttb_exam_student")
public class ExamSubjectStudent {
	
	
	@Id @Column(length=50)
	private String id;
	@ManyToOne
	private Subject subject;
	
	@ManyToOne @JoinColumn(name="campus_id")
	private Campus campus;
	
	@ManyToOne
	private ClassroomSection section;
	@ManyToOne
	private StudyMode studyMode;
	
	private String studentExamNo;
	
	
	public ExamSubjectStudent() {
		setId(lebah.db.UniqueID.getUID());
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

	public Campus getCampus() {
		return campus;
	}

	public void setCampus(Campus campus) {
		this.campus = campus;
	}

	public ClassroomSection getSection() {
		return section;
	}

	public void setSection(ClassroomSection section) {
		this.section = section;
	}

	public StudyMode getStudyMode() {
		return studyMode;
	}

	public void setStudyMode(StudyMode studyMode) {
		this.studyMode = studyMode;
	}

	public String getStudentExamNo() {
		return studentExamNo;
	}

	public void setStudentExamNo(String studentExamNo) {
		this.studentExamNo = studentExamNo;
	}
	
	
	@Override
	public boolean equals(Object o) {
		ExamSubjectStudent result = (ExamSubjectStudent) o;
		if(result.id.equals(id))
			return true;
		else
			return false;
	}
	@Override
	public int hashCode() {
		return id.hashCode();
	}	
	

}
