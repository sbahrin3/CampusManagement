package educate.timetabling.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="aim_department")
public class AIMDepartment {
	
	@Id @Column(length=50)
	private String id;
	@Column(length=255)
	private String name;
	/*
	@Column(length=50)
	private String code;
	*/

	public AIMDepartment() {
		setId(lebah.db.UniqueID.getUID());
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

	/*
	public String getCode() {
		if ( code == null || "".equals(code)) code = id;
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	*/
	


}
