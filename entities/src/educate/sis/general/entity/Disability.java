package educate.sis.general.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import metadata.EntityLister;
@Entity
@Table(name="cm_disability")
public class Disability implements EntityLister {
	@Id
	public String id;
	@Column(length=10)
	public String code;
	@Column(length=50)
	public String name;
	
	public Disability(){
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


	public String getValue() {
		return getCode()+"-"+getName();
	}
	
}
