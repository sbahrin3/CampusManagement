package educate.sis.module;

import java.util.Date;

import educate.enrollment.entity.Student;

public class Occupier {
	
	protected Student student;
	protected Date date;
	private Date checkInDate;
	private Date checkInTime;
	private Date checkOutDate;
	private Date checkOutTime;
	
	public Occupier() {
		
	}
	
	public Occupier(Student student, Date date) {
		this.student = student;
		this.date = date;
	}
	
	public Occupier(Student student, Date date, Date checkInDate, Date checkInTime, Date checkOutDate, Date checkOutTime) {
		this.student = student;
		this.date = date;
		this.checkInDate = checkInDate;
		this.checkInTime = checkInTime;
		this.checkOutDate = checkOutDate;
		this.checkOutTime = checkOutTime;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

	public Date getCheckInDate() {
		return checkInDate;
	}

	public void setCheckInDate(Date checkInDate) {
		this.checkInDate = checkInDate;
	}

	public Date getCheckInTime() {
		return checkInTime;
	}

	public void setCheckInTime(Date checkInTime) {
		this.checkInTime = checkInTime;
	}

	public Date getCheckOutDate() {
		return checkOutDate;
	}

	public void setCheckOutDate(Date checkOutDate) {
		this.checkOutDate = checkOutDate;
	}

	public Date getCheckOutTime() {
		return checkOutTime;
	}

	public void setCheckOutTime(Date checkOutTime) {
		this.checkOutTime = checkOutTime;
	}
	
	

}
