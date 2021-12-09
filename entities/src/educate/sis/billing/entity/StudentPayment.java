package educate.sis.billing.entity;

import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import educate.enrollment.entity.Student;

@Entity
@Table(name="pay_student")
public class StudentPayment implements Financer {
	@Id
	private String id;
	private String receiptNo;
	@OneToOne
	private Student student;
	@OneToMany(cascade=CascadeType.ALL)
	private Set<PaymentDetail> paymentsDetails;
	@Temporal(TemporalType.DATE)
	private Date paymentDate;
	private String paymentType;
	private String paymentNo;
	private String voids;
	public StudentPayment() {
		setId(lebah.db.UniqueID.getUID());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getReceiptNo() {
		return receiptNo;
	}

	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

	public Set<PaymentDetail> getPaymentsDetails() {
		return paymentsDetails;
	}

	public void setPaymentsDetails(Set<PaymentDetail> paymentsDetails) {
		for(Iterator<PaymentDetail> it = paymentsDetails.iterator();it.hasNext();){
			it.next().setPayment(this);
		}
		this.paymentsDetails = paymentsDetails;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}
	public double getTotalPayment(){
		double total = 0.0d;
		if(paymentsDetails != null){
			for ( Iterator<PaymentDetail> it = paymentsDetails.iterator(); it.hasNext(); ) {
				PaymentDetail detail = it.next();
				total += detail.getAmount();
			}
		}
		return total;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentNo(String paymentNo) {
		this.paymentNo = paymentNo;
	}

	public String getPaymentNo() {
		return paymentNo;
	}

	public Date trasactionDate() {
		return getPaymentDate();
	}

	public String getVoids() {
		return voids;
	}

	public void setVoids(String voids) {
		this.voids = voids;
	}

	
	
	
}
