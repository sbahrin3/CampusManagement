package educate.sis.module;

import java.util.Date;
import java.util.Map;

public class Record {
	
	private String studentId;
	private String matricNo;
	private String name;
	private String intake;
	private String finalSemester;
	private String finalSemesterSession;
	private String finalSemesterName;
	private Date dateFinalSemester;
	private double creditEarned;
	private double creditTransfered;
	private double creditTotalEarned;
	private double creditNotEarned;
	private double finalSemesterGPA;
	private double resultCGPA;
	private String failedSubjects;
	
	private Map<String, Integer> specialStatusMap;
	
	
	public String getStudentId() {
		return studentId;
	}
	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}
	public double getCreditEarned() {
		return creditEarned;
	}
	public void setCreditEarned(double creditEarned) {
		this.creditEarned = creditEarned;
	}
	public double getCreditNotEarned() {
		return creditNotEarned;
	}
	public void setCreditNotEarned(double creditNotEarned) {
		this.creditNotEarned = creditNotEarned;
	}
	public double getCreditTotalEarned() {
		return creditTotalEarned;
	}
	public void setCreditTotalEarned(double creditTotalEarned) {
		this.creditTotalEarned = creditTotalEarned;
	}
	public double getCreditTransfered() {
		return creditTransfered;
	}
	public void setCreditTransfered(double creditTransfered) {
		this.creditTransfered = creditTransfered;
	}
	public String getFailedSubjects() {
		return failedSubjects;
	}
	public void setFailedSubjects(String failedSubjects) {
		this.failedSubjects = failedSubjects;
	}
	public String getFinalSemester() {
		return finalSemester;
	}
	public void setFinalSemester(String finalSemester) {
		this.finalSemester = finalSemester;
	}
	public double getFinalSemesterGPA() {
		return finalSemesterGPA;
	}
	public void setFinalSemesterGPA(double finalSemesterGPA) {
		this.finalSemesterGPA = finalSemesterGPA;
	}
	public String getIntake() {
		return intake;
	}
	public void setIntake(String intake) {
		this.intake = intake;
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
	public double getResultCGPA() {
		return resultCGPA;
	}
	public void setResultCGPA(double resultCGPA) {
		this.resultCGPA = resultCGPA;
	}
	public Date getDateFinalSemester() {
		return dateFinalSemester;
	}
	public void setDateFinalSemester(Date dateFinalSemester) {
		this.dateFinalSemester = dateFinalSemester;
	}
	public Map<String, Integer> getSpecialStatusMap() {
		return specialStatusMap;
	}
	public void setSpecialStatusMap(Map<String, Integer> specialStatusMap) {
		this.specialStatusMap = specialStatusMap;
	}
	public String getFinalSemesterSession() {
		return finalSemesterSession;
	}
	public void setFinalSemesterSession(String finalSemesterSession) {
		this.finalSemesterSession = finalSemesterSession;
	}
	public String getFinalSemesterName() {
		return finalSemesterName;
	}
	public void setFinalSemesterName(String finalSemesterName) {
		this.finalSemesterName = finalSemesterName;
	}


}
