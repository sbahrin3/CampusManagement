package educate.sis.teacher;

public class SubjectVO {
	
	private String subjectCode;
	private String subjectName;
	private String sectionCode;
	private String sectionName;
	
	public SubjectVO(String subjectCode, String subjectName, String sectionCode, String sectionName) {
		this.subjectCode = subjectCode;
		this.subjectName = subjectName;
		this.sectionCode = sectionCode;
		this.sectionName = sectionName;
	}
	
	public String getSectionCode() {
		return sectionCode;
	}
	public void setSectionCode(String sectionCode) {
		this.sectionCode = sectionCode;
	}
	public String getSectionName() {
		return sectionName;
	}
	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}
	public String getSubjectCode() {
		return subjectCode;
	}
	public void setSubjectCode(String subjectCode) {
		this.subjectCode = subjectCode;
	}
	public String getSubjectName() {
		return subjectName;
	}
	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}
	
	

}
