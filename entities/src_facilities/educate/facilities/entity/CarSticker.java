package educate.facilities.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity @Table(name="fac_car_sticker")
public class CarSticker {
	
	@Id @Column(length=50)
	private String id;
	@Column(length=50)
	private String ownerId;
	@Column(length=100)
	private String ownerName;
	private String ownerType; //student, staff
	@Column(length=220)
	private String carDetail;
	@Column(length=50)
	private String plateNo;
	
	public CarSticker() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}
	public String getOwnerName() {
		return ownerName;
	}
	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getCarDetail() {
		return carDetail;
	}
	public void setCarDetail(String carDetail) {
		this.carDetail = carDetail;
	}
	public String getPlateNo() {
		return plateNo;
	}
	public void setPlateNo(String plateNo) {
		this.plateNo = plateNo;
	}

	public String getOwnerType() {
		return ownerType;
	}

	public void setOwnerType(String ownerType) {
		this.ownerType = ownerType;
	}
	
	

}
