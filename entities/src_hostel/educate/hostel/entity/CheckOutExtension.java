package educate.hostel.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="hostel_check_out_ext")
public class CheckOutExtension {
	
	@Id @Column(length=50)
	private String id;
	@ManyToOne @JoinColumn(name="room_allocation_id")
	private RoomAllocation roomAllocation;
	@Temporal(TemporalType.DATE)
	private Date previousCheckOutDate;
	@Temporal(TemporalType.DATE)
	private Date checkOutDate;
	private String extensionRemark;
	
	public CheckOutExtension() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public RoomAllocation getRoomAllocation() {
		return roomAllocation;
	}
	public void setRoomAllocation(RoomAllocation roomAllocation) {
		this.roomAllocation = roomAllocation;
	}
	public Date getPreviousCheckOutDate() {
		return previousCheckOutDate;
	}
	public void setPreviousCheckOutDate(Date previousCheckOutDate) {
		this.previousCheckOutDate = previousCheckOutDate;
	}

	public Date getCheckOutDate() {
		return checkOutDate;
	}

	public void setCheckOutDate(Date checkOutDate) {
		this.checkOutDate = checkOutDate;
	}

	public String getExtensionRemark() {
		return extensionRemark;
	}

	public void setExtensionRemark(String extensionRemark) {
		this.extensionRemark = extensionRemark;
	}
	
	

}
