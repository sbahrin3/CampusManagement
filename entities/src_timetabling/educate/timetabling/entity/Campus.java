/**
 * 
 */
package educate.timetabling.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;


@Entity
@Table(name="ttb_campus")
public class Campus {
	
	@Id @Column(length=50)
	private String id;
	@Column(length=50)
	private String code;
	@Column(length=50)
	private String name;
	@Column(length=50)
	private String description;
	@OneToMany(cascade=CascadeType.PERSIST, mappedBy="campus")
	private List<Building> buildings;
	
	@ManyToOne @JoinColumn(name="state_id")
	private State state;
	
	@OneToOne
	private AIMCampus aimCampus;
	
	public Campus() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCode() {
		if ( code == null || "".equals(code)) code = id;
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
	public List<Building> getBuildings() {
		if ( buildings == null ) buildings = new ArrayList<Building>();
		return buildings;
	}
	public void setBuildings(List<Building> buildings) {
		this.buildings = buildings;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public AIMCampus getAimCampus() {
		return aimCampus;
	}

	public void setAimCampus(AIMCampus aimCampus) {
		this.aimCampus = aimCampus;
	}
	
	
	
	

}
