package educate.sis.struct.entity;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="struc_programssubject")
public class ProgramsSubject {
	@Id
	private String id;
	@ManyToOne
	private Program program;
	@OneToMany
	private Set<Subject> subject;
	public ProgramsSubject() {
		setId(lebah.db.UniqueID.getUID());
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Program getProgram() {
		return program;
	}
	public void setProgram(Program program) {
		this.program = program;
	}
	public void setSubject(Set<Subject> subject) {
		this.subject = subject;
	}
	public Set<Subject> getSubject() {
		return subject;
	}
	
}
