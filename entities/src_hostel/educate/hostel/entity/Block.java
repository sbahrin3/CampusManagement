package educate.hostel.entity;

import java.util.*;

import javax.persistence.*;

@Entity
@Table(name="hostel_block")
public class Block implements Comparable {
	
	@Id
	private String id;
	private String code;
	private String name;
	@ManyToOne @JoinColumn(name="building_id")
	private Building building;
	@OneToMany(cascade=CascadeType.ALL, mappedBy="block")
	private List<Floor> floors;
	
	private int sequence;
	
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
		return floors;
	}

	public void setFloors(List<Floor> floors) {
		this.floors = floors;
	}
	
	public List<Floor> getFloorList() {
		List<Floor> list = new ArrayList<Floor>();
		list.addAll(floors);
		Collections.sort(list);
		return list;
	}

	public void addFloor(Floor floor) {
		if ( floors == null ) floors = new ArrayList<Floor>();
		floor.setBlock(this);
		floors.add(floor);
		
	}
	
	public void removeFloor(Floor floor) {
		floors.remove(floor);
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	
	@Override
	public int compareTo(Object o) {
		Block r = (Block) o;
		if ( this.getSequence() > r.getSequence() ) return 1;
		else if ( this.getSequence() < r.getSequence() ) return -1;
		return 0;
	}
	
}
