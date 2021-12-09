package educate.facilities.entity;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import educate.timetabling.entity.Room;

@Entity @Table(name="fac_reservation")
public class Reservation {
	
	@Id
	private String id;
	@Column(length=50)
	private String userId;
	@Column(length=50)
	private String email;
	@Column(length=50)
	private String contactNo;
	@Column(length=100)
	private String programmeName;
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateFrom;
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateTo;
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateCreate;
	@Column(length=50)
	private String status; //pending, approved, not-approved, canceled
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateStatus;
	
	
	@ManyToOne @JoinColumn(name="room_id")
	private Room room;
	
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="reservation")
	private List<LoanEquipment> equipments;
	
	public Reservation() {
		setId(lebah.db.UniqueID.getUID());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getContactNo() {
		return contactNo;
	}

	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}

	public String getProgrammeName() {
		return programmeName;
	}

	public void setProgrammeName(String programmeName) {
		this.programmeName = programmeName;
	}

	public Date getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Date getDateTo() {
		return dateTo;
	}

	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}

	public Date getDateCreate() {
		return dateCreate;
	}

	public void setDateCreate(Date dateCreate) {
		this.dateCreate = dateCreate;
	}

	
	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}
	

	public List<LoanEquipment> getEquipments() {
		if ( equipments == null ) equipments = new ArrayList<LoanEquipment>();
		return equipments;
	}

	public void setEquipments(List<LoanEquipment> equipments) {
		this.equipments = equipments;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getDateStatus() {
		return dateStatus;
	}

	public void setDateStatus(Date dateStatus) {
		this.dateStatus = dateStatus;
	}
	
	
	

}
