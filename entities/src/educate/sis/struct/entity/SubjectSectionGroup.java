package educate.sis.struct.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="enrl_section_group")
public class SubjectSectionGroup {
	
	@Id @Column(length=50)
	private String id;
	@ManyToOne
	private Subject subject;
	@ManyToOne
	private SubjectSection section;
	private int capacity;
	
	public SubjectSectionGroup() {
		
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Subject getSubject() {
		return subject;
	}
	public void setSubject(Subject subject) {
		this.subject = subject;
	}
	public SubjectSection getSection() {
		return section;
	}
	public void setSection(SubjectSection section) {
		this.section = section;
	}
	public int getCapacity() {
		return capacity;
	}
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
	
	

}
