package educate.sis.exam.entity;

public class Result {
	
	private String studentId;
	private double value;
	
	public Result(String studentId, double value) {
		this.studentId = studentId;
		this.value = value;
	}

	public String getStudentId() {
		return studentId;
	}

	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}
	
	

}
