package educate.sis.general.entity;

import javax.persistence.*;
import metadata.EntityLister;

@Entity
@Table(name="cm_country")
public class Country implements EntityLister{
	
	@Id
	private String id;
	@Column(length=10)
	public String code;
	@Column(length=10)
	public String abbrev;
	@Column(length=100)
	public String name;
	
	public Country(){
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

	public String getAbbrev() {
		return abbrev;
	}

	public void setAbbrev(String abbrev) {
		this.abbrev = abbrev;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return getName();
	}
}
