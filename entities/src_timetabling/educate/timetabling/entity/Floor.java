package educate.timetabling.entity;

import java.util.ArrayList;
import java.util.Collections;
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
@Table(name="ttb_floor")
public class Floor {
	
	
	@Id @Column(length=50)
	private String id;
	@Column(length=50)
	private String code;
	@Column(length=50)
	private String name;
	
	@ManyToOne @JoinColumn(name="block_id")
	private Block block;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="floor")
	private List<Room> rooms;
	
	
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
	public List<Room> getRooms() {
		if ( rooms == null ) rooms = new ArrayList<Room>();
		Collections.sort(rooms, new RoomComparator());
		return rooms;
	}
	public void setRooms(List<Room> rooms) {
		this.rooms = rooms;
	}
	
	
	static class RoomComparator extends educate.util.MyComparator {
		public int compare(Object o1, Object o2) {
			Room s1 = (Room) o1;
			Room s2 = (Room) o2;
			return s1.getSequence() < s2.getSequence() ? 1 : 0;
		}
	}	

}
