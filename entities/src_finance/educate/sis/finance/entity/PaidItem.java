package educate.sis.finance.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="fin_paid_item")
public class PaidItem {

	@Id @Column(length=50)
	private String id;
	@OneToOne @JoinColumn(name="invoice_item_id")
	private InvoiceItem invoiceItem;
	@ManyToOne @JoinColumn(name="payment_id")
	private Payment payment;
	private double amount;
	
	public PaidItem() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public InvoiceItem getInvoiceItem() {
		return invoiceItem;
	}
	public void setInvoiceItem(InvoiceItem invoiceItem) {
		this.invoiceItem = invoiceItem;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}

	public Payment getPayment() {
		return payment;
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
	}
	
	
	
}
