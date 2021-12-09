package educate.admission.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import onapp.entity.OnappProgram;
import educate.sis.struct.entity.Program;

@Entity @Table(name="onapp_program_mapper")
public class OnappProgramMapper {
	
	@Id @Column(length=50)
	private String id;
	@OneToOne @JoinColumn(name="onappProgram_id")
	private OnappProgram onappProgram;
	@OneToOne @JoinColumn(name="program_id")
	private Program program;
	
	public OnappProgramMapper() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public OnappProgram getOnappProgram() {
		return onappProgram;
	}
	public void setOnappProgram(OnappProgram onappProgram) {
		this.onappProgram = onappProgram;
	}
	public Program getProgram() {
		return program;
	}
	public void setProgram(Program program) {
		this.program = program;
	}
	
	

}
