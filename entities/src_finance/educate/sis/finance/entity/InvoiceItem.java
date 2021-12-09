package educate.sis.finance.entity;

import javax.persistence.*;

@Entity
@Table(name="fin_invoice_item")
public class InvoiceItem {

	@Id
	private String id;
	private int courseFee;
	private String description;
	private double amount;
	private double balance;
	@ManyToOne(cascade=CascadeType.PERSIST)
	private Invoice invoice;
	@ManyToOne(cascade=CascadeType.PERSIST)
	private FeeItem feeItem;
	
	public InvoiceItem() {
		setId(lebah.util.UIDGenerator.getUID());
	}
	
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public FeeItem getFeeItem() {
		return feeItem;
	}
	public void setFeeItem(FeeItem feeItem) {
		this.feeItem = feeItem;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	public void setCourseFee(boolean b) {
		courseFee = b ? 1 : 0;
	}
	public boolean getCourseFee() {
		return courseFee == 1;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}
	
	
	
}
