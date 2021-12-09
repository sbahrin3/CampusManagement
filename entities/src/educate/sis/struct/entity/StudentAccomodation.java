package educate.sis.struct.entity;

import javax.persistence.*;

@Entity
@Table(name="struc_accomodation")
public class StudentAccomodation {
	
	@Id
	@Column(length=50)
	private String id;
	@Column(length=50)
	private String code;
	@Column(length=100)
	private String name;
	private int quotaFemale;
	private int quotaMale;
	private double monthlyFee;
	private int months;
	@Transient
	private double totalFee;
	
	public StudentAccomodation() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getQuotaFemale() {
		return quotaFemale;
	}
	public void setQuotaFemale(int quotaFemale) {
		this.quotaFemale = quotaFemale;
	}
	public int getQuotaMale() {
		return quotaMale;
	}
	public void setQuotaMale(int quotaMale) {
		this.quotaMale = quotaMale;
	}

	public double getMonthlyFee() {
		return monthlyFee;
	}

	public void setMonthlyFee(double monthlyFee) {
		this.monthlyFee = monthlyFee;
	}

	public int getMonths() {
		return months;
	}

	public void setMonths(int months) {
		this.months = months;
	}

	public double getTotalFee() {
		totalFee = monthlyFee * months;
		return totalFee;
	}

	public void setTotalFee(double totalFee) {
		this.totalFee = totalFee;
	}
	
	

}
