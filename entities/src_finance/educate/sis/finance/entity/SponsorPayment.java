package educate.sis.finance.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.*;

@Entity
@Table(name="fin_sponsor_payment")
public class SponsorPayment {
	
	@Id
	private String id;
	private double amount;
	@Temporal(TemporalType.DATE)
	private Date paymentDate;
	@ManyToOne(cascade=CascadeType.PERSIST)
	private SponsorInvoice sponsorInvoice;
	@OneToMany(cascade=CascadeType.PERSIST, mappedBy="sponsorPayment")
	private List<Payment> payments;
	
	private String paymentRemark;
	
	public SponsorPayment() {
		setId(lebah.db.UniqueID.getUID());
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
	public Date getPaymentDate() {
		return paymentDate;
	}
	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}
	public List<Payment> getPayments() {
		return payments;
	}
	public void setPayments(List<Payment> payments) {
		this.payments = payments;
	}
	public SponsorInvoice getSponsorInvoice() {
		return sponsorInvoice;
	}
	public void setSponsorInvoice(SponsorInvoice sponsorInvoice) {
		this.sponsorInvoice = sponsorInvoice;
	}

	public String getPaymentRemark() {
		return paymentRemark;
	}

	public void setPaymentRemark(String paymentRemark) {
		this.paymentRemark = paymentRemark;
	}
	
	

}
