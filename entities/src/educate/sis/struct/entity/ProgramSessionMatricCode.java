/**
 * 
 */
package educate.sis.struct.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author Shamsul Bahrin bin Abd Mutalib
 *
 */

@Entity
@Table(name="matric_program_session")
public class ProgramSessionMatricCode {
	
	@Id @Column(length=50)
	private String id;
	@ManyToOne @JoinColumn(name="program_id")
	private Program program;
	@ManyToOne @JoinColumn(name="session_id")
	private Session session;
	@Column(length=50)
	private String code;
	@Column(length=100)
	private String name;
	@Column(length=10)
	private String matricCode;
	
	
	public ProgramSessionMatricCode() {
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
	public Session getSession() {
		return session;
	}
	public void setSession(Session session) {
		this.session = session;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMatricCode() {
		return matricCode;
	}
	public void setMatricCode(String matricCode) {
		this.matricCode = matricCode;
	}
	
	
}
