package educate.sis.billing.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="inv_detail")
public class InvoiceDetail {
	@Id
	private String id;
	private String code;
	private String description;
	private double amount;
	@ManyToOne
	private StudentInvoice invoice;
	private double paid;
	private int seqNo;
	@ManyToOne
	private PaymentItem item;
	
	public InvoiceDetail(){
		setId(lebah.db.UniqueID.getUID());
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public void setInvoice(StudentInvoice invoice) {
		this.invoice = invoice;
	}
	public StudentInvoice getInvoice() {
		return invoice;
	}
	
	public double getPaid() {
		return paid;
	}
	public void setPaid(double paid) {
		this.paid = paid;
	}
	
	public int getSeqNo() {
		return seqNo;
	}
	public void setSeqNo(int seqNo) {
		this.seqNo = seqNo;
	}
	
	public PaymentItem getItem() {
		return item;
	}
	public void setItem(PaymentItem item) {
		this.item = item;
	}
	@Override
	public boolean equals(Object o) {
		InvoiceDetail detail = (InvoiceDetail)o;
		System.out.println(detail.id);
		if(detail.id.equals(id))
			return true;
		else
			return false;
	}
	@Override
	public int hashCode() {
		return id.hashCode();
	}
	
}
