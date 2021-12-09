package educate.exam.module;

public class Result {
	
	private String matricNo;
	private String sessionCode;
	private String subjectCode;
	private double mark;
	
	public Result() {
		
	}
	
	public Result(String matricNo, String sessionCode, String subjectCode, double mark) {
		this.matricNo = matricNo;
		this.sessionCode = sessionCode;
		this.subjectCode = subjectCode;
		this.mark = mark;
	}
	
	public String getMatricNo() {
		return matricNo;
	}
	public void setMatricNo(String matricNo) {
		this.matricNo = matricNo;
	}
	public String getSessionCode() {
		return sessionCode;
	}
	public void setSessionCode(String sessionCode) {
		this.sessionCode = sessionCode;
	}
	public String getSubjectCode() {
		return subjectCode;
	}
	public void setSubjectCode(String subjectCode) {
		this.subjectCode = subjectCode;
	}
	public double getMark() {
		return mark;
	}
	public void setMark(double mark) {
		this.mark = mark;
	}
	
	
	

}
