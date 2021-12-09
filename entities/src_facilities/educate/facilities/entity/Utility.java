package educate.facilities.entity;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity @Table(name="fac_utility")
public class Utility {
	
	@Id @Column(length=50)
	private String id;
	@ManyToOne @JoinColumn(name="type_id")
	private UtilityType type;
	private int month;
	private int year;
	private Date date;
	
	@Column(length=50)
	private String accountNo;
	@Column(length=50)
	private String unitConsumption; //eg: LITER, KW, etc...
	private double amountConsumption;
	
	public Utility() {
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
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getAccountNo() {
		return accountNo;
	}
	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}
	public String getUnitConsumption() {
		return unitConsumption;
	}
	public void setUnitConsumption(String unitConsumption) {
		this.unitConsumption = unitConsumption;
	}
	public double getAmountConsumption() {
		return amountConsumption;
	}
	public void setAmountConsumption(double amountConsumption) {
		this.amountConsumption = amountConsumption;
	}
	
	

}
