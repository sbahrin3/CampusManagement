package educate.hostel.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="hostel_room")
public class Room implements Comparable {
	
	@Id
	private String id;
	private String code;
	private String name;
	private int capacity;
	private int sequence;
	@ManyToOne @JoinColumn(name="unit_id")
	private Unit unit;
	
	@ManyToOne @JoinColumn(name="type_id")
	private RoomType roomType;
	
	@Transient
	private boolean available;
	@Transient
	private List<RoomAllocation> roomAllocations;
	@OneToMany(cascade=CascadeType.ALL, mappedBy="room")
	private List<HostelItem> items;
	
	private double bedRate;
	private double roomDeposit;
	
	public Room() {
		setId(lebah.db.UniqueID.getUID());
		capacity = 2; //default room capacity is two
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


	public Unit getUnit() {
		return unit;
	}


	public void setUnit(Unit unit) {
		this.unit = unit;
	}


	public boolean isAvailable() {
		return available;
	}


	public void setAvailable(boolean available) {
		this.available = available;
	}

	public int getCapacity() {
		return capacity;
	}


	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}


	public List<RoomAllocation> getRoomAllocations() {
		return roomAllocations;
	}


	public void setRoomAllocations(List<RoomAllocation> roomAllocations) {
		this.roomAllocations = roomAllocations;
	}


	public int getSequence() {
		return sequence;
	}


	public void setSequence(int sequence) {
		this.sequence = sequence;
	}


	@Override
	public int compareTo(Object o) {
		Room r = (Room) o;
		if ( this.getSequence() > r.getSequence() ) return 1;
		else if ( this.getSequence() < r.getSequence() ) return -1;
		return 0;
	}


	public List<HostelItem> getItems() {
		if ( items == null ) items = new ArrayList<HostelItem>();
		return items;
	}


	public void setItems(List<HostelItem> items) {
		this.items = items;
	}


	public double getBedRate() {
		return bedRate;
	}


	public void setBedRate(double bedRate) {
		this.bedRate = bedRate;
	}


	public double getRoomDeposit() {
		return roomDeposit;
	}


	public void setRoomDeposit(double roomDeposit) {
		this.roomDeposit = roomDeposit;
	}


	public RoomType getRoomType() {
		return roomType;
	}


	public void setRoomType(RoomType roomType) {
		this.roomType = roomType;
	}
	
	

	
}
