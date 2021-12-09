package educate.sis.finance.entity;

import java.util.*;

import javax.persistence.*;

import educate.enrollment.entity.Student;
import educate.sis.struct.entity.Session;

@Entity
@Table(name="fin_invoice")
public class Invoice {
	
	@Id
	private String id;
	private String invoiceNo;
	@Column(length=50)
	private String referenceNo;
	@ManyToOne
	private Student student;
	@ManyToOne
	private Session session;
	@Temporal(TemporalType.DATE)
	private Date createDate;
	@Temporal(TemporalType.TIME)
	private Date createTime;
	@OneToMany(cascade=CascadeType.ALL, mappedBy="invoice")
	private Set<InvoiceItem> invoiceItems;
	private double amount;
	private double balance;
	@ManyToOne(cascade=CascadeType.PERSIST)
	private SponsorInvoice sponsorInvoice;
	/**
	 * There are two invoice type
	 * 1 - Primary Invoice - this invoice is only one per semester - usually consists of tuition fees, registration fee, ...
	 * 2 - Secondary Invoice - can have as many secondary invoice per semester
	 */
	private int invoiceType = 0;
	
	private int deleted;
	private int isDebitNote;
	private int isRefund;
	private int isFlexi;
	private String scheduleItemId;
	
	@Column(length=250)
	private String remark;
	
	public Invoice() {
		setId(lebah.util.UIDGenerator.getUID());
	}

	public String getInvoiceNo() {
		return invoiceNo;
	}
	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
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
	public Set<InvoiceItem> getInvoiceItems() {
		return invoiceItems;
	}
	
	public List<InvoiceItem> getOrderedInvoiceItems() {
		List<InvoiceItem> list = new ArrayList<InvoiceItem>();
		list.addAll(invoiceItems);
		Collections.sort(list, new InvoiceItemComparator());
		return list;
	}
	
	public void setInvoiceItems(Set<InvoiceItem> invoiceItems) {
		this.invoiceItems = invoiceItems;
	}
	public void addInvoiceItem(InvoiceItem item) {
		if ( invoiceItems == null ) invoiceItems = new HashSet<InvoiceItem>();
		item.setInvoice(this);
		item.setBalance(item.getAmount());
		amount += item.getAmount();
		balance += item.getBalance();
		invoiceItems.add(item);
	}
	public void removeInvoiceItem(InvoiceItem item) {
		amount -= item.getAmount();
		balance -= item.getBalance();
		invoiceItems.remove(item);
	}
	public Student getStudent() {
		return student;
	}
	public void setStudent(Student student) {
		this.student = student;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}
	
	public class InvoiceItemComparator extends educate.util.MyComparator {

		public int compare(Object o1, Object o2) {
			InvoiceItem i1 = (InvoiceItem) o1;
			InvoiceItem i2 = (InvoiceItem) o2;
			if ( i1.getFeeItem() == null && i2.getFeeItem() == null ) return 0;
			else if ( i1.getFeeItem() == null ) return 1;
			else if ( i2.getFeeItem() == null ) return -1;
			else if ( i1.getFeeItem().getSeq() > i2.getFeeItem().getSeq() ) return 1;
			else if ( i1.getFeeItem().getSeq() < i2.getFeeItem().getSeq() ) return 1;
			else return 0;
		}
		
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}
	
	public void setDeleted(boolean del) {
		deleted = del ? 1 : 0;
	}
	
	public boolean getDeleted() {
		return deleted == 1;
	}

	public SponsorInvoice getSponsorInvoice() {
		return sponsorInvoice;
	}

	public void setSponsorInvoice(SponsorInvoice sponsorInvoice) {
		this.sponsorInvoice = sponsorInvoice;
	}

	public int getInvoiceType() {
		return invoiceType;
	}

	public void setInvoiceType(int invoiceType) {
		this.invoiceType = invoiceType;
	}
	
	public void setDebitNote(boolean b) {
		isDebitNote = b ? 1 : 0;
	}
	
	public boolean getDebitNote() {
		return isDebitNote == 1;
	}
	
	public void setRefund(boolean b) {
		isRefund = b ? 1 : 0;
	}
	
	public boolean getRefund() {
		return isRefund == 1;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getReferenceNo() {
		return referenceNo;
	}

	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
	}

	public boolean getIsFlexi() {
		return isFlexi == 1;
	}

	public void setIsFlexi(boolean isFlexi) {
		this.isFlexi = isFlexi ? 1 : 0;
	}

	public String getScheduleItemId() {
		return scheduleItemId;
	}

	public void setScheduleItemId(String scheduleItemId) {
		this.scheduleItemId = scheduleItemId;
	}
	
	

}
