package educate.hostel.entity;

import java.util.*;

import javax.persistence.*;

@Entity
@Table(name="hostel_floor")
public class Floor implements Comparable {
	
	@Id
	private String id;
	private String code;
	private String name;
	@ManyToOne @JoinColumn(name="block_id")
	private Block block;
	@OneToMany(cascade=CascadeType.ALL, mappedBy="floor")
	private List<Unit> units;
	@Column(length=200)
	private String floorPlanFilename;
	@Lob @Basic(fetch = FetchType.LAZY)
	//@Column(length=1000000)
    private byte[] floorPlanImage;	
	@Column(length=50)
	private String floorPlanImageId;

	
	private int sequence;
	
	public Floor() {
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


	public Block getBlock() {
		return block;
	}


	public void setBlock(Block block) {
		this.block = block;
	}


	public List<Unit> getUnits() {
		return units;
	}
	
	public List<Unit> getUnitList() {
		List<Unit> list = new ArrayList<Unit>();
		list.addAll(units);
		Collections.sort(list);
		return list;
	}


	public void setUnits(List<Unit> units) {
		this.units = units;
	}

	public void addUnit(Unit unit) {
		if ( units == null ) units = new ArrayList<Unit>();
		unit.setFloor(this);
		units.add(unit);
	}
	
	public void removeUnit(Unit unit) {
		units.remove(unit);
	}


	public int getSequence() {
		return sequence;
	}


	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	
	@Override
	public int compareTo(Object o) {
		Floor r = (Floor) o;
		if ( this.getSequence() > r.getSequence() ) return 1;
		else if ( this.getSequence() < r.getSequence() ) return -1;
		return 0;
	}


	public String getFloorPlanFilename() {
		return floorPlanFilename;
	}


	public void setFloorPlanFilename(String floorPlanFilename) {
		this.floorPlanFilename = floorPlanFilename;
	}


	public byte[] getFloorPlanImage() {
		return floorPlanImage;
	}


	public void setFloorPlanImage(byte[] floorPlanImage) {
		this.floorPlanImage = floorPlanImage;
	}


	public String getFloorPlanImageId() {
		return floorPlanImageId;
	}


	public void setFloorPlanImageId(String floorPlanImageId) {
		this.floorPlanImageId = floorPlanImageId;
	}
	
	
	
	
}
