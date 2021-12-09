package portal.module.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="p_ann_module_admin")
public class AnnouncementAdmin {
	
	@Id
	private String id; //is moduleId
	
	@OneToMany(cascade=CascadeType.PERSIST, fetch=FetchType.LAZY)
	private List<AdminRole> adminRoles;
	
	public AnnouncementAdmin() {
		
	}
	
	public AnnouncementAdmin(String moduleId) {
		setId(moduleId);
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public List<AdminRole> getAdminRoles() {
		return adminRoles;
	}
	public void setAdminRoles(List<AdminRole> adminRoles) {
		this.adminRoles = adminRoles;
	}
	
	

}
