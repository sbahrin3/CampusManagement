package educate.sis.module;

public class SubjectResultData {
	
	private double totalMark;
	private double point;
	private String grade;
	
	public SubjectResultData(double totalMark, double point, String grade) {
		this.totalMark = totalMark;
		this.point = point;
		this.grade = grade;
	}
	
	public double getTotalMark() {
		return (double) Math.round(totalMark * 100) / 100;
	}
	public void setTotalMark(double totalMark) {
		this.totalMark = totalMark;
	}
	public double getPoint() {
		return (double) Math.round(point * 100) / 100;
	}
	public void setPoint(double point) {
		this.point = point;
	}
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	


}
