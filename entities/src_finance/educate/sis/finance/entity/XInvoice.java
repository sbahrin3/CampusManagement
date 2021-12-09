package educate.sis.finance.entity;

import java.util.*;

import javax.persistence.*;

import educate.enrollment.entity.Student;
import educate.sis.struct.entity.Session;

@Entity
@Table(name="fin_xinvoice")
public class XInvoice {
	
	@Id
	private String id;
	private String invoiceNo;
	@ManyToOne
	private Student student;
	@ManyToOne
	private Session session;
	@Temporal(TemporalType.DATE)
	private Date createDate;
	@Temporal(TemporalType.TIME)
	private Date createTime;
	@OneToMany(cascade=CascadeType.PERSIST, mappedBy="invoice")
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
	private String deleteReason;
	
	@Temporal(TemporalType.DATE)
	private Date deleteDate;
	@Temporal(TemporalType.TIME)
	private Date deleteTime;
	private String userId;
	
	public XInvoice() {
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
		//item.setInvoice(this);
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

	public String getDeleteReason() {
		return deleteReason;
	}

	public void setDeleteReason(String deleteReason) {
		this.deleteReason = deleteReason;
	}

	public Date getDeleteDate() {
		return deleteDate;
	}

	public void setDeleteDate(Date deleteDate) {
		this.deleteDate = deleteDate;
	}

	public Date getDeleteTime() {
		return deleteTime;
	}

	public void setDeleteTime(Date deleteTime) {
		this.deleteTime = deleteTime;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	

}
