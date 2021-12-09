package educate.sis.exam.entity;

public class GroupResult {
	
	private String groupName;
	private double point;
	private String grade;
	private int creditHours;
	
	private double totalPoint;
	private double averagePoint;
	
	private int sequence;
	
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public double getPoint() {
		return point;
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
	public int getCreditHours() {
		return creditHours;
	}
	public void setCreditHours(int creditHours) {
		this.creditHours = creditHours;
	}
	public double getTotalPoint() {
		return totalPoint;
	}
	public void setTotalPoint(double totalPoint) {
		this.totalPoint = totalPoint;
	}
	public double getAveragePoint() {
		return averagePoint;
	}
	public void setAveragePoint(double averagePoint) {
		this.averagePoint = averagePoint;
	}
	public int getSequence() {
		return sequence;
	}
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	
	

	

}
