package portal.module.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="p_ann_admin")
public class AdminRole {

	@Id
	private String id;
	private String userRole;
	
	public AdminRole() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public AdminRole(String userRole) {
		setId(lebah.db.UniqueID.getUID());
		this.userRole = userRole;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUserRole() {
		return userRole;
	}
	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}
	
	
}
