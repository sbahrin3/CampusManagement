package educate.sis.finance.entity;

import java.util.*;

import javax.persistence.*;

import educate.enrollment.entity.Student;

@Entity
@Table(name="fin_payment")
public class Payment {
	
	@Id 
	private String id;
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
	
	//adjustment remarks
	@Column(length=255)
	private String adjustmentRemarks;
	
	@Column(length=50)
	private String paymentMode;
	@Column(length=50)
	private String checkNo;
	@Column(length=50)
	private String bankName;
	
	@OneToMany(cascade=CascadeType.PERSIST, mappedBy="payment")
	private Set<PaymentItem> paymentItems;
	@OneToMany(cascade=CascadeType.PERSIST, mappedBy="payment")
	private List<PaidItem> paidItems;
	
	@ManyToOne(cascade=CascadeType.PERSIST)
	private SponsorPayment sponsorPayment;
	
	private int isCreditNote;
	
	@Column(length=250)
	private String remark;
	
	private String documentFileName;
	
	public Payment() {
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
		paymentItem.setPayment(this);
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

	public SponsorPayment getSponsorPayment() {
		return sponsorPayment;
	}

	public void setSponsorPayment(SponsorPayment sponsorPayment) {
		this.sponsorPayment = sponsorPayment;
	}
	
	public void setCreditNote(boolean b) {
		isCreditNote = b ? 1 : 0;
	}
	
	public boolean getCreditNote() {
		return isCreditNote == 1 ? true : false;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}

	public List<PaidItem> getPaidItems() {
		if ( paidItems == null ) paidItems = new ArrayList<PaidItem>();
		return paidItems;
	}

	public void setPaidItems(List<PaidItem> paidItems) {
		this.paidItems = paidItems;
	}

	public String getCheckNo() {
		return checkNo;
	}

	public void setCheckNo(String checkNo) {
		this.checkNo = checkNo;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getDocumentFileName() {
		return documentFileName;
	}

	public void setDocumentFileName(String documentFileName) {
		this.documentFileName = documentFileName;
	}

	public String getAdjustmentRemarks() {
		return adjustmentRemarks;
	}

	public void setAdjustmentRemarks(String adjustmentRemarks) {
		this.adjustmentRemarks = adjustmentRemarks;
	}
	
	

}
