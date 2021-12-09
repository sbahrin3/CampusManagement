package educate.sis.struct.entity;

import javax.persistence.*;

@Entity
@Table(name="struc_specialisation")
public class Specialisation {
	
	@Id
	private String id;
	private String code;
	private String name;
	@ManyToOne(cascade=CascadeType.PERSIST)
	private Program program;
	
	public Specialisation() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public Program getProgram() {
		return program;
	}

	public void setProgram(Program program) {
		this.program = program;
	}
	
	
	

}
