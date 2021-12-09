package educate.hostel.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="hostel_room_facility")
public class RoomFacility {
	
	@Id @Column(length=50)
	private String id;
	@OneToOne @JoinColumn(name="facility_id")
	private HostelFacility facility;
	@OneToOne @JoinColumn(name="room_id")
	private Room room;
	
	public RoomFacility() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public HostelFacility getFacility() {
		return facility;
	}
	public void setFacility(HostelFacility facility) {
		this.facility = facility;
	}
	public Room getRoom() {
		return room;
	}
	public void setRoom(Room room) {
		this.room = room;
	}
	
	
	
	

}
