package educate.sis.module;

import educate.sis.struct.entity.SubjectSection;

public class ClassroomStatus {
	
	private SubjectSection section;
	private int currentSize;
	
	public ClassroomStatus(SubjectSection section, int currentSize) {
		this.section = section;
		this.currentSize = currentSize;
	}
	
	public SubjectSection getSection() {
		return section;
	}
	public void setSection(SubjectSection section) {
		this.section = section;
	}
	public int getCurrentSize() {
		return currentSize;
	}
	public void setCurrentSize(int currentSize) {
		this.currentSize = currentSize;
	}
	
	
	
	
	

}
