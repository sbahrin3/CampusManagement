package educate.sis.struct.entity;

import javax.persistence.*;

import metadata.EntityLister;

@Entity
@Table(name="struc_sessionpath")
public class SessionPath  implements EntityLister {

	@Id
	private String id;
	private int pathNo;
	private String name;
			
	public SessionPath() {
		setId(lebah.util.UIDGenerator.getUID());
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

	public int getPathNo() {
		return pathNo;
	}

	public void setPathNo(int pathNo) {
		this.pathNo = pathNo;
	}

	public String getValue() {
		return pathNo + " " + name;
	}
	
	
	
}
