package educate.timetabling.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import educate.enrollment.entity.Student;

@Entity
@Table(name="ttb_student_attendance")
public class StudentAttendance {
	
	@Id @Column(length=50)
	private String id;
	@ManyToOne @JoinColumn(name="classroom_id")
	private Classroom classroom;
	@ManyToOne @JoinColumn(name="student_id")
	private Student student;
	private int attendance; //0. Present, 1. Absent with reason, 2. Absent without reason (MIA), 3. Late
	@Column(length=50)
	private String absentRemark;
	
	public StudentAttendance() {
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


	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

	public int getAttendance() {
		return attendance;
	}

	public void setAttendance(int attendance) {
		this.attendance = attendance;
	}

	public String getAbsentRemark() {
		return absentRemark;
	}

	public void setAbsentRemark(String absentRemark) {
		this.absentRemark = absentRemark;
	}
	
	

}
