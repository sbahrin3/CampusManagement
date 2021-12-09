package educate.sis.billing.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="pay_detail")
public class PaymentDetail {
	@Id
	private String id;
	private String invoiceNo;
	private String code;
	private String description;
	private double amount;
	@ManyToOne
	private StudentPayment payment;
	@ManyToOne
	private PaymentItem item;
	public PaymentDetail() {
		setId(lebah.db.UniqueID.getUID());
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getInvoiceNo() {
		return invoiceNo;
	}
	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public void setPayment(StudentPayment payment) {
		this.payment = payment;
	}
	public StudentPayment getPayment() {
		return payment;
	}
	public PaymentItem getItem() {
		return item;
	}
	public void setItem(PaymentItem item) {
		this.item = item;
	}
	@Override
	public boolean equals(Object o) {
		PaymentDetail detail = (PaymentDetail)o;
		if(detail.getId().equals(id))
			return true;
		else
			return false;
	}
	@Override
	public int hashCode() {
		return id.hashCode();
	}
	
}
