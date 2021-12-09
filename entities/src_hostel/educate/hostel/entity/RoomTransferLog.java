package educate.hostel.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Entity
@Table(name="hostel_room_transfer")
public class RoomTransferLog {
	
	@Id
	private String id;
	@OneToOne @JoinColumn(name="transfer_from_id")
	private RoomAllocation transferFrom;
	@OneToOne @JoinColumn(name="transfer_to_id")
	private RoomAllocation transferTo;
	private String transferRemark;
	
	@Temporal(TemporalType.DATE)
	private Date transferDate;
	@Temporal(TemporalType.TIME)
	private Date transferTime;
	
	public RoomTransferLog() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	@Column(length=50)
	private String userId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public RoomAllocation getTransferFrom() {
		return transferFrom;
	}

	public void setTransferFrom(RoomAllocation transferFrom) {
		this.transferFrom = transferFrom;
	}

	public RoomAllocation getTransferTo() {
		return transferTo;
	}

	public void setTransferTo(RoomAllocation transferTo) {
		this.transferTo = transferTo;
	}

	public String getTransferRemark() {
		return transferRemark;
	}

	public void setTransferRemark(String transferRemark) {
		this.transferRemark = transferRemark;
	}

	public Date getTransferDate() {
		return transferDate;
	}

	public void setTransferDate(Date transferDate) {
		this.transferDate = transferDate;
	}

	public Date getTransferTime() {
		return transferTime;
	}

	public void setTransferTime(Date transferTime) {
		this.transferTime = transferTime;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	

}
