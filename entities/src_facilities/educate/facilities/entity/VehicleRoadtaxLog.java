package educate.facilities.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity @Table(name="fac_vehicle_roadtax")
public class VehicleRoadtaxLog {
	
	@Id @Column(length=50)
	private String id;
	@ManyToOne @JoinColumn(name="vehicle_id")
	private Vehicle vehicle;
	@Temporal(TemporalType.TIMESTAMP)
	private Date renewalDate;
	@Temporal(TemporalType.TIMESTAMP)
	private Date expiryDate;
	private double insuredValue;
	
	public VehicleRoadtaxLog() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Vehicle getVehicle() {
		return vehicle;
	}
	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}
	public Date getRenewalDate() {
		return renewalDate;
	}
	public void setRenewalDate(Date renewalDate) {
		this.renewalDate = renewalDate;
	}
	public Date getExpiryDate() {
		return expiryDate;
	}
	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}
	public double getInsuredValue() {
		return insuredValue;
	}
	public void setInsuredValue(double insuredValue) {
		this.insuredValue = insuredValue;
	}
	
	
	

}
