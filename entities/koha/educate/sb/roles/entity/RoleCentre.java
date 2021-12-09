package educate.sb.roles.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import educate.sis.struct.entity.LearningCentre;

@Entity
@Table(name="sb_role_centre")
public class RoleCentre {
	
	@Id
	@Column(length=100)
	private String id;
	@Column(length=100)
	private String roleName; //
	@OneToMany(cascade=CascadeType.PERSIST)
	private List<LearningCentre> centres;
	
	public RoleCentre() {
		
	}
	
	public RoleCentre(String roleName) {
		this.roleName = roleName;
		this.id = roleName;
	}
	

	public List<LearningCentre> getCentres() {
		return centres;
	}



	public void setCentres(List<LearningCentre> centres) {
		this.centres = centres;
	}



	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	
	

}
