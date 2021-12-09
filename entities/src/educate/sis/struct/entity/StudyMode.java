/**
 * 
 */
package educate.sis.struct.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Shamsul Bahrin bin Abd Mutalib
 *
 */
@Entity
@Table(name="struc_study_mode")
public class StudyMode {
	
	@Id @Column(length=50)
	private String id;
	@Column(length=10)
	private String code;
	@Column(length=50)
	private String name;
	@Column(length=10)
	private String matricCode;	
	
	public StudyMode() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
