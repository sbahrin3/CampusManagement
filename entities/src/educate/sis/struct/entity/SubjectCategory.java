package educate.sis.struct.entity;

import javax.persistence.*;
import metadata.EntityLister;

@Entity
@Table(name="struc_subjectcategory")
public class SubjectCategory implements EntityLister {
	
	@Id
	private String id;
	private String code;
	private String name;
	
	private int isMandatory; //shall automaticall add the subject during registration
		
	public SubjectCategory(){
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
		String value = getCode()+" - "+getName();
		return value;
	}


	public void setMandatory(boolean b) {
		isMandatory = b ? 1 : 0;
	}
	
	public boolean getMandatory() {
		return isMandatory == 1;
	}
	
}
