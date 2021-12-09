package educate.timetabling.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import educate.sis.struct.entity.Teacher;

@Entity
@Table(name="ttb_teacher_attendance")
public class TeacherAttendance {
	
	@Id @Column(length=50)
	private String id;
	@ManyToOne @JoinColumn(name="classroom_id")
	private Classroom classroom;
	@ManyToOne @JoinColumn(name="teacher_id")
	private Teacher teacher;
	private int attended;
	
	public TeacherAttendance() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Classroom getClassroom() {
		return classroom;
	}
	public void setClassroom(Classroom classroom) {
		this.classroom = classroom;
	}
	public boolean getAttended() {
		return attended == 1;
	}
	public void setAttended(boolean attended) {
		this.attended = attended ? 1 : 0;
	}

	public Teacher getTeacher() {
		return teacher;
	}

	public void setTeacher(Teacher teacher) {
		this.teacher = teacher;
	}
	
	

}
