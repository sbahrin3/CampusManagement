package educate.sis.general.entity;
import javax.persistence.*;
import metadata.EntityLister;

@Entity
@Table(name="cm_regvanue")
public class RegVanue implements EntityLister{

	@Id
	private String id;
	
	private String venCode;
	private String name;
	
	public RegVanue(){
		setId(lebah.db.UniqueID.getUID());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getVenCode() {
		return venCode;
	}

	public void setVenCode(String venCode) {
		this.venCode = venCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getValue(){
		return getVenCode()+"-"+getName();
	}
}
