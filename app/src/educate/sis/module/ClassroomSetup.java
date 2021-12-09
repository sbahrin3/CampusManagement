package educate.sis.module;

import educate.timetabling.entity.ClassroomSection;

public class ClassroomSetup {
	
	private ClassroomSection section;
	private int frequency;
	
	public ClassroomSetup() {
		
	}
	
	public ClassroomSetup(ClassroomSection s, int f) {
		this.section = s;
		this.frequency = f;
	}
	
	public ClassroomSection getSection() {
		return section;
	}
	public void setSection(ClassroomSection section) {
		this.section = section;
	}
	public int getFrequency() {
		return frequency;
	}
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
	
	

}
