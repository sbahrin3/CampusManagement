package educate.sis.finance.entity;

import javax.persistence.*;


@Entity
@Table(name="fin_payment_item")
public class PaymentItem {
	
	@Id
	private String id;
	@ManyToOne(cascade=CascadeType.PERSIST)
	private Payment payment;
	
	private int isCreditNoteItem;
	private int flexi;
	private int ptptn;
	private int resourceFee;
	
	@OneToOne
	private PaymentScheduleItem scheduleItem; //if flexi, linked to which schedule item
	
	//shall be removed
	//@ManyToOne(cascade=CascadeType.PERSIST)
	//private InvoiceItem invoiceItem;
	//--
	
//	
//	@ManyToOne(cascade=CascadeType.PERSIST)
//	private FeeItem feeItem;
	
	private double amount;
	private double adjustedAmount; //the difference
	
	private String description;
	
	public PaymentItem() {
		setId(lebah.util.UIDGenerator.getUID());
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

//	public InvoiceItem getInvoiceItem() {
//		return invoiceItem;
//	}
//
//	public void setInvoiceItem(InvoiceItem invoiceItem) {
//		this.invoiceItem = invoiceItem;
//	}

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
	
	
	public void setCreditNote(boolean b) {
		isCreditNoteItem = b ? 1 : 0;
	}
	
	public boolean getCreditNote() {
		return isCreditNoteItem == 1 ? true : false;
	}

	public boolean getFlexi() {
		return flexi == 1;
	}

	public void setFlexi(boolean flexi) {
		this.flexi = flexi ? 1 : 0;
	}

	public PaymentScheduleItem getScheduleItem() {
		return scheduleItem;
	}

	public void setScheduleItem(PaymentScheduleItem scheduleItem) {
		this.scheduleItem = scheduleItem;
	}

	public double getAdjustedAmount() {
		return adjustedAmount;
	}

	public void setAdjustedAmount(double adjustedAmount) {
		this.adjustedAmount = adjustedAmount;
	}

	public boolean getPtptn() {
		return ptptn == 1;
	}

	public void setPtptn(boolean ptptn) {
		this.ptptn = ptptn ? 1 : 0;
	}
	
	public boolean getResourceFee() {
		return resourceFee == 1;
	}

	public void setResourceFee(boolean resourceFee) {
		this.resourceFee = resourceFee ? 1 : 0;
	}

}
