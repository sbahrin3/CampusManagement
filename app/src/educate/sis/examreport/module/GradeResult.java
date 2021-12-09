package educate.sis.examreport.module;

public class GradeResult {
	
	String grade;
	int count;
	double percent;
	double cgpa;
	int cgpaGroup;
	String name;
	String matricNo;
	double mark;
	
	public GradeResult(String name, String matricNo, String g, double mark) {
		this.name = name;
		this.matricNo = matricNo;
		this.grade = g;
		this.mark = mark;
	}
	
	public GradeResult(String name, String matricNo, double g) {
		this.name = name;
		this.matricNo = matricNo;
		this.cgpa = g;
	}
	
	public GradeResult(String g, int c) {
		grade = g;
		count = c;
	}
	
	public GradeResult(double g, int c) {
		cgpa = g;
		count = c;
	}
	
	public GradeResult(int g, int c) {
		cgpaGroup = g;
		count = c;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public double getPercent() {
		return percent;
	}

	public void setPercent(double percent) {
		this.percent = percent;
	}

	public double getCgpa() {
		return cgpa;
	}

	public void setCgpa(double cgpa) {
		this.cgpa = cgpa;
	}

	public int getCgpaGroup() {
		return cgpaGroup;
	}

	public void setCgpaGroup(int cgpaGroup) {
		this.cgpaGroup = cgpaGroup;
	}
	
	public String getGPARange() {
		if ( cgpaGroup == 1 ) return "4.00 - 3.67";
		else if ( cgpaGroup == 2 ) return "3.66 - 3.00";
		else if ( cgpaGroup == 3 ) return "2.99 - 2.00 ";
		else if ( cgpaGroup == 4 ) return "1.99 - 1.00";
		else if ( cgpaGroup == 5 ) return "0.99 - 0.00";
		else return "";
	}

	public double getMark() {
		return mark;
	}

	public void setMark(double mark) {
		this.mark = mark;
	}

	public String getMatricNo() {
		return matricNo;
	}

	public void setMatricNo(String matricNo) {
		this.matricNo = matricNo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	

}
