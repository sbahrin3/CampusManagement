package educate.sis.exam.entity;

import javax.persistence.*;

@Entity
@Table(name="exam_grade")

public class Grade {
	
	@Id
	private String id;
	private String letter;
	private double maxMark;
	private double minMark;
	private double point;
	@ManyToOne
	private MarkingGrade markingGrade;
	
	public Grade() {
		setId(lebah.util.UIDGenerator.getUID());
	}
	public Grade(String s1, double p, double d1, double d2) {
		setId(lebah.util.UIDGenerator.getUID());
		letter = s1;
		point = p;
		minMark = d1;
		maxMark = d2;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getLetter() {
		return letter;
	}
	public void setLetter(String letter) {
		this.letter = letter;
	}
	public double getMaxMark() {
		return maxMark;
	}
	public void setMaxMark(double maxMark) {
		this.maxMark = maxMark;
	}
	public double getMinMark() {
		return minMark;
	}
	public void setMinMark(double minMark) {
		this.minMark = minMark;
	}
	public double getPoint() {
		return point;
	}
	public void setPoint(double point) {
		this.point = point;
	}
	public MarkingGrade getMarkingGrade() {
		return markingGrade;
	}
	public void setMarkingGrade(MarkingGrade markingGrade) {
		this.markingGrade = markingGrade;
	}

}
