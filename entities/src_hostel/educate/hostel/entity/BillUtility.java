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

import educate.facilities.entity.UtilityType;

@Entity
@Table(name="hostel_bill_utility")
public class BillUtility {
	
	@Id @Column(length=50)
	private String id;
	@ManyToOne @JoinColumn(name="type_id")
	private UtilityType type;
	
	@Column(length=50)
	private String refNo;
	
	private int meterRead;
	@Column(length=50)
	private String unitRead;	
	@ManyToOne @JoinColumn(name="unit_id")
	private Unit unit;
	
	
	private String previousMeterReading;
	private String latestMeterReading;
	private String unitUsed;
	
	@Temporal(TemporalType.DATE)
	private Date dateMeterReading;
	
	private double outstandingAmount;
	private double previousAmountPaid;
	private double latestAmountPayable;
	private double totalAmountPayable;
	
	public BillUtility() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public UtilityType getType() {
		return type;
	}
	public void setType(UtilityType type) {
		this.type = type;
	}

	public int getMeterRead() {
		return meterRead;
	}
	public void setMeterRead(int meterRead) {
		this.meterRead = meterRead;
	}
	public String getUnitRead() {
		return unitRead;
	}
	public void setUnitRead(String unitRead) {
		this.unitRead = unitRead;
	}
	public Unit getUnit() {
		return unit;
	}
	public void setUnit(Unit unit) {
		this.unit = unit;
	}

	public Date getDateMeterReading() {
		return dateMeterReading;
	}
	public void setDateMeterReading(Date dateMeterReading) {
		this.dateMeterReading = dateMeterReading;
	}

	public double getLatestAmountPayable() {
		return latestAmountPayable;
	}
	public void setLatestAmountPayable(double latestAmountPayable) {
		this.latestAmountPayable = latestAmountPayable;
	}
	public double getTotalAmountPayable() {
		return totalAmountPayable;
	}
	public void setTotalAmountPayable(double totalAmountPayable) {
		this.totalAmountPayable = totalAmountPayable;
	}
	public double getPreviousAmountPaid() {
		return previousAmountPaid;
	}
	public void setPreviousAmountPaid(double previousAmountPaid) {
		this.previousAmountPaid = previousAmountPaid;
	}


	public String getRefNo() {
		return refNo;
	}


	public void setRefNo(String refNo) {
		this.refNo = refNo;
	}


	public String getPreviousMeterReading() {
		return previousMeterReading;
	}


	public void setPreviousMeterReading(String previousMeterReading) {
		this.previousMeterReading = previousMeterReading;
	}


	public String getLatestMeterReading() {
		return latestMeterReading;
	}


	public void setLatestMeterReading(String latestMeterReading) {
		this.latestMeterReading = latestMeterReading;
	}


	public String getUnitUsed() {
		return unitUsed;
	}


	public void setUnitUsed(String unitUsed) {
		this.unitUsed = unitUsed;
	}


	public double getOutstandingAmount() {
		return outstandingAmount;
	}


	public void setOutstandingAmount(double outstandingAmount) {
		this.outstandingAmount = outstandingAmount;
	}
	
	
	
	
}
