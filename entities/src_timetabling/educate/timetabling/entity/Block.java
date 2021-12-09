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
import javax.persistence.Table;

@Entity
@Table(name="ttb_block")
public class Block {
	
	
	@Id @Column(length=50)
	private String id;
	@Column(length=50)
	private String code;
	@Column(length=50)
	private String name;
	
	@ManyToOne @JoinColumn(name="building_id")
	private Building building;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="block")
	private List<Floor> floors;
	
	@ManyToOne @JoinColumn(name="zone_id")
	private Zone zone;
	
	
	public Block() {
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
	public Building getBuilding() {
		return building;
	}
	public void setBuilding(Building building) {
		this.building = building;
	}
	public List<Floor> getFloors() {
		if ( floors == null ) floors = new ArrayList<Floor>();
		return floors;
	}
	public void setFloors(List<Floor> floors) {
		this.floors = floors;
	}


	public Zone getZone() {
		return zone;
	}


	public void setZone(Zone zone) {
		this.zone = zone;
	}
	
	
	

}
