package educate.hostel.entity;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="hostel")
public class Hostel implements Comparable {
	
	@Id
	private String id;
	private String code;
	private String name;
	private String address;
	@OneToMany(cascade=CascadeType.ALL, mappedBy="hostel")
	private List<Building> buildings;
	
	private int sequence;

	public Hostel() {
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

	public List<Building> getBuildings() {
		return buildings;
	}

	public void setBuildings(List<Building> buildings) {
		this.buildings = buildings;
	}
	
	public List<Building> getBuildingList() {
		List<Building> list = new ArrayList<Building>();
		list.addAll(buildings);
		Collections.sort(list);
		return list;
	}

	public void addBuilding(Building building) {
		if ( buildings == null ) buildings = new ArrayList<Building>();
		building.setHostel(this);
		buildings.add(building);
	}
	
	public void removeBuilding(Building building) {
		buildings.remove(building);
	}
	
	public List<Block> getBlocks() {
		List<Block> list = new ArrayList<Block>();
		for ( Building b : this.getBuildingList() ) {
			for ( Block bl : b.getBlockList() ) {
				list.add(bl);
			}
		}
		return list;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	
	@Override
	public int compareTo(Object o) {
		Hostel r = (Hostel) o;
		if ( this.getSequence() > r.getSequence() ) return 1;
		else if ( this.getSequence() < r.getSequence() ) return -1;
		return 0;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	
}
