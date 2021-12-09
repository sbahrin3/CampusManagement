package educate.sis.module;

public class SectionGroup {
	
	private String sectionId;
	private String sectionCode;
	private long numberOfStudents;
	
	public SectionGroup(String id, String code, long counter) {
		this.sectionId = id;
		this.sectionCode = code;
		this.numberOfStudents = counter;
	}
	
	

	public String getSectionId() {
		return sectionId;
	}



	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
	}



	public long getNumberOfStudents() {
		return numberOfStudents;
	}

	public String getSectionCode() {
		return sectionCode;
	}

	public void setNumberOfStudents(int numberOfStudents) {
		this.numberOfStudents = numberOfStudents;
	}

	public void setSectionCode(String sectionCode) {
		this.sectionCode = sectionCode;
	}
	
	

}
