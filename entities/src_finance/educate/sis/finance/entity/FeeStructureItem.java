package educate.sis.finance.entity;

import javax.persistence.*;
import educate.sis.struct.entity.Period;

@Entity
@Table(name="fin_fee_structure_item")
public class FeeStructureItem {
	
	@Id
	private String id;
	private double amount;
	private double amountInternational;
	private double amount2; //for part time local
	private double amountInternational2; //for part time international
	private int feePaymentType; //- not needed
	@ManyToOne
	private Period period;
	@ManyToOne(cascade=CascadeType.PERSIST)
	private FeeStructure feeStructure;
	@ManyToOne(cascade=CascadeType.PERSIST)
	private FeeItem feeItem;
	
	public FeeStructureItem() {
		setId(lebah.util.UIDGenerator.getUID());
	}
	
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public int getFeePaymentType() {
		return feePaymentType;
	}

	public void setFeePaymentType(int feePaymentType) {
		this.feePaymentType = feePaymentType;
	}

	public Period getPeriod() {
		return period;
	}

	public void setPeriod(Period period) {
		this.period = period;
	}

	public FeeItem getFeeItem() {
		return feeItem;
	}

	public void setFeeItem(FeeItem feeItem) {
		this.feeItem = feeItem;
	}

	public FeeStructure getFeeStructure() {
		return feeStructure;
	}

	public void setFeeStructure(FeeStructure feeStructure) {
		this.feeStructure = feeStructure;
	}

	public double getAmountInternational() {
		return amountInternational;
	}

	public void setAmountInternational(double amountInternational) {
		this.amountInternational = amountInternational;
	}

	public double getAmount2() {
		return amount2;
	}

	public void setAmount2(double amount2) {
		this.amount2 = amount2;
	}

	public double getAmountInternational2() {
		return amountInternational2;
	}

	public void setAmountInternational2(double amountInternational2) {
		this.amountInternational2 = amountInternational2;
	}


	

}
