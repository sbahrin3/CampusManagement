package educate.sis.finance.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import educate.enrollment.entity.Student;

@Entity
@Table(name="fin_xpayment")
public class XPayment {
	
	@Id
	private String id;
	@ManyToOne(cascade=CascadeType.PERSIST)
	private Payment payment;
	private String paymentNo;
	private double amount;
	@ManyToOne
	private Student student;
	@Temporal(TemporalType.DATE)
	private Date createDate;
	@Temporal(TemporalType.TIME)
	private Date createTime;
	private String createUserId;
	private int adjusted;
	private int adjustSeq;
	private String adjustUserId;
	@Temporal(TemporalType.DATE)
	private Date adjustDate;
	@Temporal(TemporalType.TIME)
	private Date adjustTime;
	
	@OneToMany(cascade=CascadeType.PERSIST)
	private Set<PaymentItem> paymentItems;
	
	private String description;
	
	public XPayment() {
		setId(lebah.util.UIDGenerator.getUID());
	}
	
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Set<PaymentItem> getPaymentItems() {
		return paymentItems;
	}
	public void setPaymentItems(Set<PaymentItem> paymentItems) {
		this.paymentItems = paymentItems;
	}
	public void addPaymentItems(PaymentItem paymentItem) {
		if ( paymentItems == null ) paymentItems = new HashSet<PaymentItem>();
		amount += paymentItem.getAmount();
		paymentItems.add(paymentItem);
	}
	public void removePaymentItems(PaymentItem paymentItem) {
		amount -= paymentItem.getAmount();
		paymentItems.remove(paymentItem);
	}
	public String getPaymentNo() {
		return paymentNo;
	}
	public void setPaymentNo(String paymentNo) {
		this.paymentNo = paymentNo;
	}
	public Student getStudent() {
		return student;
	}
	public void setStudent(Student student) {
		this.student = student;
	}

	public Date getAdjustDate() {
		return adjustDate;
	}

	public void setAdjustDate(Date adjustDate) {
		this.adjustDate = adjustDate;
	}

	public boolean getAdjusted() {
		return adjusted == 1 ? true : false;
	}

	public void setAdjusted(boolean adjusted) {
		this.adjusted = adjusted ? 1 : 0;
	}

	public Date getAdjustTime() {
		return adjustTime;
	}

	public void setAdjustTime(Date adjustTime) {
		this.adjustTime = adjustTime;
	}

	public void setAdjustSeq(int adjustSeq) {
		this.adjustSeq = adjustSeq;
	}

	public String getAdjustUserId() {
		return adjustUserId;
	}

	public void setAdjustUserId(String adjustUserId) {
		this.adjustUserId = adjustUserId;
	}

	public String getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}

	public int getAdjustSeq() {
		return adjustSeq;
	}

	public Payment getPayment() {
		return payment;
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	

}
