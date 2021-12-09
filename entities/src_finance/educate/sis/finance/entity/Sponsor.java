package educate.sis.finance.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name="fin_sponsor")
public class Sponsor {
	
	@Id
	private String id;
	private String code;
	private String name;
	private String address;
	
	@Lob
	private String bottomContent;
	
	public Sponsor() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
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

	public String getBottomContent() {
		return bottomContent;
	}

	public void setBottomContent(String bottomContent) {
		this.bottomContent = bottomContent;
	}
	
	
	

}
