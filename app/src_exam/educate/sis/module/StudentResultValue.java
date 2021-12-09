package educate.sis.module;

import java.util.Date;

public class StudentResultValue {
	
	private String studentName;
	private double creditHours;
	private Date date;
	
	public StudentResultValue(String name, double c) {
		studentName = name;
		creditHours = c;
	}
	
	public double getCreditHours() {
		return creditHours;
	}
	public void setCreditHours(double creditHours) {
		this.creditHours = creditHours;
	}
	public String getStudentName() {
		return studentName;
	}
	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	

}
