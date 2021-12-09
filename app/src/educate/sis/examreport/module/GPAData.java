package educate.sis.examreport.module;

import educate.enrollment.entity.Student;
import educate.sis.exam.entity.FinalResult;

public class GPAData {
	
	private Student student;
	private FinalResult currentResult;
	private FinalResult lastResult;
	
	public GPAData(Student s, FinalResult c, FinalResult l) {
		student = s;
		currentResult = c;
		lastResult = l;
	}
	
	public FinalResult getCurrentResult() {
		return currentResult;
	}
	public void setCurrentResult(FinalResult currentResult) {
		this.currentResult = currentResult;
	}
	public FinalResult getLastResult() {
		return lastResult;
	}
	public void setLastResult(FinalResult lastResult) {
		this.lastResult = lastResult;
	}
	public Student getStudent() {
		return student;
	}
	public void setStudent(Student student) {
		this.student = student;
	}

}
