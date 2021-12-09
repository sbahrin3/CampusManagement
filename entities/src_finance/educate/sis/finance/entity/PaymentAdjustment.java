package educate.sis.finance.entity;

import javax.persistence.*;

import java.util.*;

@Entity
@Table(name="fin_payment_adjustment")
public class PaymentAdjustment {
	
	@Id
	private String id;
	@OneToOne
	private Payment payment; //the modified payment
	@OneToMany
	private Set<Payment> oldPayments; //payment history
	
	public PaymentAdjustment() {
		setId(lebah.util.UIDGenerator.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Set<Payment> getOldPayments() {
		return oldPayments;
	}
	public void setOldPayments(Set<Payment> oldPayments) {
		this.oldPayments = oldPayments;
	}
	public void addOldPayment(Payment oldPayment) {
		if ( oldPayments == null ) oldPayments = new HashSet<Payment>();
		oldPayments.add(oldPayment);
	}
	public Payment getPayment() {
		return payment;
	}
	public void setPayment(Payment payment) {
		this.payment = payment;
	}
	
	

}
