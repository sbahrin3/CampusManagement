package educate.sis.general.entity;
import javax.persistence.*;

@Entity
@Table(name="cm_maritialstatus")
public class MaritialStatus {

	@Id
	private String id;
	private String code;
	private String name;
	
	public MaritialStatus(){
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
		return getId()+""+getName();
	}

}
