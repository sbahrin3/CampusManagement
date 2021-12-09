package educate.sis.general.entity;
import javax.persistence.*;
import metadata.EntityLister;

@Entity
@Table(name="cm_degreeclass")
public class DegreeClass implements EntityLister {

	@Id
	private String id;
	private String code;
	private String name;
	
	public DegreeClass(){
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
		return getCode() + " " + getName();
	}
}
