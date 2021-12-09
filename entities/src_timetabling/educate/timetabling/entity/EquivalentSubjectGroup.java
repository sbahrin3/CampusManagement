package educate.timetabling.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import educate.sis.struct.entity.Subject;

@Entity
@Table(name="eq_subject_group")
public class EquivalentSubjectGroup {
	@Id @Column(length=50)
	private String id;
	@OneToMany
	private List<Subject> subjects;
	
	public EquivalentSubjectGroup() {
		setId(lebah.db.UniqueID.getUID());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Subject> getSubjects() {
		if ( subjects == null ) subjects = new ArrayList<Subject>();
		return subjects;
	}

	public void setSubjects(List<Subject> subjects) {
		this.subjects = subjects;
	}
	
	

}
