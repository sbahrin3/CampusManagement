package educate.sis.module;

import java.util.Date;

import educate.enrollment.entity.Student;

public class PaymentAging {
	
	private String studentId;
	private Student student;
	private Date dateBegin;
	private Date dateEnd;
	private double amount;
	private int months;
	
	public PaymentAging(String studentId, double amount) {
		this.studentId = studentId;
		this.amount = amount;
	}
	
	public PaymentAging(Student student, Date dateBegin, Date dateEnd, double amount, int months) {
		this.student = student;
		this.dateBegin = dateBegin;
		this.dateEnd = dateEnd;
		this.amount = amount;
		this.months = months;
	}
	
	public Student getStudent() {
		return student;
	}
	public void setStudent(Student student) {
		this.student = student;
	}
	public Date getDateBegin() {
		return dateBegin;
	}
	public void setDateBegin(Date dateBegin) {
		this.dateBegin = dateBegin;
	}
	public Date getDateEnd() {
		return dateEnd;
	}
	public void setDateEnd(Date dateEnd) {
		this.dateEnd = dateEnd;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public int getMonths() {
		return months;
	}
	public void setMonths(int months) {
		this.months = months;
	}

	public String getStudentId() {
		return studentId;
	}

	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}
	
	
	

}
