package educate.sis.finance.module;

import java.util.Date;

import educate.enrollment.entity.Student;

public class Adjust {
	
	private String refNo;
	private Date date;
	private Date time;
	private Student student;
	private double amountDiff;
	private double subtotal;
	private String userId;
	
	public Adjust(String refNo, Date date, Date time, Student student, double diff, double subtotal) {
		
		this.refNo = refNo;
		this.date = date;
		this.time = time;
		this.student = student;
		this.amountDiff = diff;
		this.subtotal = subtotal;
	}
	
	public Adjust(String refNo, Date date, Date time, String userId, Student student, double diff, double subtotal) {
		
		this.refNo = refNo;
		this.date = date;
		this.time = time;
		this.student = student;
		this.amountDiff = diff;
		this.subtotal = subtotal;
		this.userId = userId;
	}
	
	

	public String getRefNo() {
		return refNo;
	}



	public void setRefNo(String refNo) {
		this.refNo = refNo;
	}



	public double getAmountDiff() {
		return amountDiff;
	}

	public void setAmountDiff(double amountDiff) {
		this.amountDiff = amountDiff;
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

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}



	public double getSubtotal() {
		return subtotal;
	}



	public void setSubtotal(double subtotal) {
		this.subtotal = subtotal;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	

}
