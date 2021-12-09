package educate.sis.module;

import educate.enrollment.entity.StatusType;
import educate.enrollment.entity.Student;
import educate.sis.struct.entity.Period;
import educate.sis.struct.entity.Session;

public class SAmount {
	
	private String studentId;
	private double amount;
	private String semester;
	private Student student;
	private Period period;
	private Session session;
	private StatusType status;
	
	
	public SAmount(String id, double d, Student student, Period p, Session ses, StatusType t) {
		this.studentId = id;
		this.amount = d;
		this.student = student;
		this.period = p;
		this.session = ses;
		this.status = t;
	}
	
	public SAmount(String id, double d, Student student) {
		this.studentId = id;
		this.amount = d;
		this.student = student;
	}

	public double getAmount() {
		return amount;
	}

	public String getStudentId() {
		return studentId;
	}
	
	public String getSemester() {
		return semester;
	}
	
	@Override
	public boolean equals(Object o) {
		SAmount s = (SAmount) o;
		if (s.getStudentId().equals(this.getStudentId())) return true;
		return false;
	}
	
	
	
	public Period getPeriod() {
		return period;
	}


	public Student getStudent() {
		return student;
	}


	public Session getSession() {
		return session;
	}


	public StatusType getStatus() {
		return status;
	}


	@Override
	public int hashCode() {
		return this.getStudentId().hashCode();
	}

}
