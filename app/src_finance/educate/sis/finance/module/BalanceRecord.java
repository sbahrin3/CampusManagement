package educate.sis.finance.module;

import educate.enrollment.entity.Student;
import educate.sis.struct.entity.Program;

public class BalanceRecord {
	
	Student student;
	double amount;
	Program program;
	
	public BalanceRecord(Student student, double amount) {
		this.student = student;
		this.amount = amount;
	}
	
	public BalanceRecord(Program program, double amount) {
		this.program = program;
		this.amount = amount;
	}
	
	
	public Student getStudent() {
		return student;
	}
	public void setStudent(Student student) {
		this.student = student;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}

	public Program getProgram() {
		return program;
	}

	public void setProgram(Program program) {
		this.program = program;
	}
	
	

}
