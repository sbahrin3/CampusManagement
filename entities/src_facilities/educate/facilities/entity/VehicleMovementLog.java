package educate.facilities.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity @Table(name="fac_vehicle_movement")
public class VehicleMovementLog {
	
	@Id @Column(length=50)
	private String id;
	@ManyToOne @JoinColumn(name="vehicle_id")
	private Vehicle vehicle;
	@Temporal(TemporalType.TIMESTAMP)
	private Date departDateTime;
	@Temporal(TemporalType.TIMESTAMP)
	private Date expectedReturnDateTime;
	@Temporal(TemporalType.TIMESTAMP)
	private Date returnDateTime;
	@Column(length=100)
	private String driverName;
	
	@OneToOne @JoinColumn(name="driver_id")
	private Driver driver;
	
	@Column(length=100)
	private String destinationName;
	@Lob
	private String destinationRemark;
	
	private int odometerDepart;
	private int odometerReturn;
	
	public VehicleMovementLog() {
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
	public Date getDepartDateTime() {
		return departDateTime;
	}
	public void setDepartDateTime(Date departDateTime) {
		this.departDateTime = departDateTime;
	}
	public Date getReturnDateTime() {
		return returnDateTime;
	}
	public void setReturnDateTime(Date returnDateTime) {
		this.returnDateTime = returnDateTime;
	}
	public String getDriverName() {
		return driverName;
	}
	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}
	public String getDestinationName() {
		return destinationName;
	}
	public void setDestinationName(String destinationName) {
		this.destinationName = destinationName;
	}
	public String getDestinationRemark() {
		return destinationRemark;
	}
	public void setDestinationRemark(String destinationRemark) {
		this.destinationRemark = destinationRemark;
	}

	public Date getExpectedReturnDateTime() {
		return expectedReturnDateTime;
	}

	public void setExpectedReturnDateTime(Date expectedReturnDateTime) {
		this.expectedReturnDateTime = expectedReturnDateTime;
	}

	public int getOdometerDepart() {
		return odometerDepart;
	}

	public void setOdometerDepart(int odometerDepart) {
		this.odometerDepart = odometerDepart;
	}

	public int getOdometerReturn() {
		return odometerReturn;
	}

	public void setOdometerReturn(int odometerReturn) {
		this.odometerReturn = odometerReturn;
	}

	public Driver getDriver() {
		return driver;
	}

	public void setDriver(Driver driver) {
		this.driver = driver;
	}
	
	

}
