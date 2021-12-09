package educate.hostel.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import educate.enrollment.entity.Student;

@Entity
@Table(name="hostel_room_allocation")
public class RoomAllocation {
	
	@Id
	private String id;
	@OneToOne
	private Room room;
	@OneToOne
	private Student student;
	@Temporal(TemporalType.DATE)
	private Date checkInDate;
	@Temporal(TemporalType.TIME)
	private Date checkInTime;
	@Temporal(TemporalType.DATE)
	private Date checkOutDate;
	@Temporal(TemporalType.TIME)
	private Date checkOutTime;
	
	@Temporal(TemporalType.DATE)
	private Date expectedDateOut;	
	
	@Temporal(TemporalType.DATE)
	private Date realExpectedDateOut;
	
	private int checkedOut;
	private int extension;
	private String extensionRemark;
	@OneToMany(cascade=CascadeType.PERSIST, mappedBy="roomAllocation")
	private List<CheckOutExtension> checkOutExtensions;
	
	private int transfer;
	private int transferFrom;
	private int transferTo;
	
	@OneToOne @JoinColumn(name="transfer_id")
	private RoomTransferLog transferLog;
	
	public RoomAllocation() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getCheckInDate() {
		return checkInDate;
	}

	public void setCheckInDate(Date checkInDate) {
		this.checkInDate = checkInDate;
	}

	public Date getCheckOutDate() {
		return checkOutDate;
	}

	public void setCheckOutDate(Date checkOutDate) {
		this.checkOutDate = checkOutDate;
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

	public Date getCheckInTime() {
		return checkInTime;
	}

	public void setCheckInTime(Date checkInTime) {
		this.checkInTime = checkInTime;
	}

	public Date getCheckOutTime() {
		return checkOutTime;
	}

	public void setCheckOutTime(Date checkOutTime) {
		this.checkOutTime = checkOutTime;
	}

	public Date getExpectedDateOut() {
		return expectedDateOut;
	}

	public void setExpectedDateOut(Date expectedDateOut) {
		this.expectedDateOut = expectedDateOut;
	}

	public boolean getCheckedOut() {
		return checkedOut == 1;
	}

	public void setCheckedOut(boolean checkedOut) {
		this.checkedOut = checkedOut ? 1 : 0;
	}

	public boolean getExtension() {
		return extension == 1;
	}

	public void setExtension(boolean extension) {
		this.extension = extension ? 1 : 0;
	}

	public String getExtensionRemark() {
		return extensionRemark;
	}

	public void setExtensionRemark(String extensionRemark) {
		this.extensionRemark = extensionRemark;
	}

	public List<CheckOutExtension> getCheckOutExtensions() {
		if ( checkOutExtensions == null ) checkOutExtensions = new ArrayList<CheckOutExtension>();
		return checkOutExtensions;
	}

	public void setCheckOutExtensions(List<CheckOutExtension> checkOutExtensions) {
		this.checkOutExtensions = checkOutExtensions;
	}

	public void setCheckedOut(int checkedOut) {
		this.checkedOut = checkedOut;
	}

	public boolean getTransfer() {
		return transfer == 1;
	}

	public void setTransfer(boolean transfer) {
		this.transfer = transfer ? 1 : 0;
	}

	public RoomTransferLog getTransferLog() {
		return transferLog;
	}

	public void setTransferLog(RoomTransferLog transferLog) {
		this.transferLog = transferLog;
	}

	public Date getRealExpectedDateOut() {
		return realExpectedDateOut;
	}

	public void setRealExpectedDateOut(Date realExpectedDateOut) {
		this.realExpectedDateOut = realExpectedDateOut;
	}

	public boolean getTransferFrom() {
		return transferFrom == 1;
	}

	public void setTransferFrom(boolean transferFrom) {
		this.transferFrom = transferFrom ? 1 : 0;
	}

	public boolean getTransferTo() {
		return transferTo == 1;
	}

	public void setTransferTo(boolean transferTo) {
		this.transferTo = transferTo ? 1 : 0;
	}



}
