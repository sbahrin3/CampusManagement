package educate.sis.struct.entity;

import javax.persistence.*;

import java.util.*;

@Entity
@Table(name="struc_prerequisite")
public class Prerequisite {
	
	@Id
	private String id;
	@OneToMany(cascade=CascadeType.ALL)
	private Set<Subject> subjects;
	
	public Prerequisite(){
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Set<Subject> getSubjects() {
		return subjects;
	}
	/* tak perlu
	public void setSubjects(Set<Subject> subjects) {
		this.subjects = subjects;
	}
	*/
	public void addSubject(Subject subject) {
		if ( subjects == null ) subjects = new HashSet<Subject>();
		subjects.remove(subject);
		subjects.add(subject);
	}
}