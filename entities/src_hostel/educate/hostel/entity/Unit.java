package educate.hostel.entity;


import javax.persistence.*;

import java.util.*;

@Entity
@Table(name="hostel_unit")
public class Unit implements Comparable {
	
	@Id
	private String id;
	private String code;
	private String name;
	private int capacity;
	private int sequence;
	@ManyToOne @JoinColumn(name="floor_id")
	private Floor floor;
	@OneToMany(cascade=CascadeType.ALL, mappedBy="unit")
	private List<Room> rooms;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="room")
	private List<HostelItem> items;
	
	private int gender;
	
	@Transient
	private boolean available;
	
	public Unit() {
		setId(lebah.db.UniqueID.getUID());
		capacity = 2; //default unit capacity is two
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


	public Floor getFloor() {
		return floor;
	}


	public void setFloor(Floor floor) {
		this.floor = floor;
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


	public int getSequence() {
		return sequence;
	}


	public void setSequence(int sequence) {
		this.sequence = sequence;
	}


	@Override
	public int compareTo(Object o) {
		Unit r = (Unit) o;
		if ( this.getSequence() > r.getSequence() ) return 1;
		else if ( this.getSequence() < r.getSequence() ) return -1;
		return 0;
	}


	public List<Room> getRooms() {
		Collections.sort(rooms);
		return rooms;
	}


	public void setRooms(List<Room> rooms) {
		this.rooms = rooms;
	}
	
	
	public void addRoom(Room room) {
		if ( rooms == null ) rooms = new ArrayList<Room>();
		room.setUnit(this);
		rooms.add(room);
	}


	public int getGender() {
		return gender;
	}


	public void setGender(int gender) {
		this.gender = gender;
	}


	public List<HostelItem> getItems() {
		if ( items == null ) items = new ArrayList<HostelItem>();
		return items;
	}


	public void setItems(List<HostelItem> items) {
		this.items = items;
	}
	
	

}
