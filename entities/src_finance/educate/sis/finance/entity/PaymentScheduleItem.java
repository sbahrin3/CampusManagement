/**
 * 
 */
package educate.sis.finance.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * @author Shamsul Bahrin bin Abd Mutalib
 *
 */


@Entity
@Table(name="fin_pmt_sched_item")
public class PaymentScheduleItem { 
	
	@Id @Column(length=50)
	private String id;
	@ManyToOne(cascade=CascadeType.PERSIST)
	private PaymentSchedule paymentSchedule;
	@Temporal(TemporalType.DATE)
	private Date paymentDate;
	
	private int seq;
	private int month;
	private int year;
	private double amountDue;
	
	
	private double actualAmountDue;
	private double currentBalance;
	private double outstandingBalance;
	private double cumulativeAmountDue;
	
	private double actualAmountDueFlexi;
	private double currentBalanceFlexi;
	private double outstandingBalanceFlexi;
	private double cumulativeAmountDueFlexi;
	
	private double actualAmountDuePtptn;
	private double currentBalancePtptn;
	private double outstandingBalancePtptn;
	private double cumulativeAmountDuePtptn;
	
	
	private double amountPaid;
	@Temporal(TemporalType.DATE)
	private Date paidDate;
	
	private int isPaid;
	private int type; //0 - flexi, 1 - ptptn
	private String ptptnItemId;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="paymentScheduleItem")
	private List<PaymentScheduleItem2> items;
	
	public PaymentScheduleItem() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public PaymentSchedule getPaymentSchedule() {
		return paymentSchedule;
	}
	public void setPaymentSchedule(PaymentSchedule paymentSchedule) {
		this.paymentSchedule = paymentSchedule;
	}
	public Date getPaymentDate() {
		return paymentDate;
	}
	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}
	public int getSeq() {
		return seq;
	}
	public void setSeq(int seq) {
		this.seq = seq;
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public double getAmountDue() {
		return amountDue;
	}
	public void setAmountDue(double amountDue) {
		this.amountDue = amountDue;
	}
	public double getAmountPaid() {
		return amountPaid;
	}
	public void setAmountPaid(double amountPaid) {
		this.amountPaid = amountPaid;
	}
	public double getCurrentBalance() {
		return currentBalance;
	}
	public void setCurrentBalance(double currentBalance) {
		this.currentBalance = currentBalance;
	}
	public double getOutstandingBalance() {
		return outstandingBalance;
	}
	public void setOutstandingBalance(double outstandingBalance) {
		this.outstandingBalance = outstandingBalance;
	}
	public int getIsPaid() {
		return isPaid;
	}
	public void setIsPaid(int isPaid) {
		this.isPaid = isPaid;
	}

	public double getActualAmountDue() {
		return actualAmountDue;
	}

	public void setActualAmountDue(double actualAmountDue) {
		this.actualAmountDue = actualAmountDue;
	}

	public Date getPaidDate() {
		return paidDate;
	}

	public void setPaidDate(Date paidDate) {
		this.paidDate = paidDate;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getPtptnItemId() {
		return ptptnItemId;
	}

	public void setPtptnItemId(String ptptnItemId) {
		this.ptptnItemId = ptptnItemId;
	}

	public double getCumulativeAmountDue() {
		return cumulativeAmountDue;
	}

	public void setCumulativeAmountDue(double cumulativeAmountDue) {
		this.cumulativeAmountDue = cumulativeAmountDue;
	}

	public double getCurrentBalanceFlexi() {
		return currentBalanceFlexi;
	}

	public void setCurrentBalanceFlexi(double currentBalanceFlexi) {
		this.currentBalanceFlexi = currentBalanceFlexi;
	}

	public double getOutstandingBalanceFlexi() {
		return outstandingBalanceFlexi;
	}

	public void setOutstandingBalanceFlexi(double outstandingBalanceFlexi) {
		this.outstandingBalanceFlexi = outstandingBalanceFlexi;
	}

	public double getActualAmountDueFlexi() {
		return actualAmountDueFlexi;
	}

	public void setActualAmountDueFlexi(double actualAmountDueFlexi) {
		this.actualAmountDueFlexi = actualAmountDueFlexi;
	}

	public double getCumulativeAmountDueFlexi() {
		return cumulativeAmountDueFlexi;
	}

	public void setCumulativeAmountDueFlexi(double cumulativeAmountDueFlexi) {
		this.cumulativeAmountDueFlexi = cumulativeAmountDueFlexi;
	}

	public double getActualAmountDuePtptn() {
		return actualAmountDuePtptn;
	}

	public void setActualAmountDuePtptn(double actualAmountDuePtptn) {
		this.actualAmountDuePtptn = actualAmountDuePtptn;
	}

	public double getCurrentBalancePtptn() {
		return currentBalancePtptn;
	}

	public void setCurrentBalancePtptn(double currentBalancePtptn) {
		this.currentBalancePtptn = currentBalancePtptn;
	}

	public double getOutstandingBalancePtptn() {
		return outstandingBalancePtptn;
	}

	public void setOutstandingBalancePtptn(double outstandingBalancePtptn) {
		this.outstandingBalancePtptn = outstandingBalancePtptn;
	}

	public double getCumulativeAmountDuePtptn() {
		return cumulativeAmountDuePtptn;
	}

	public void setCumulativeAmountDuePtptn(double cumulativeAmountDuePtptn) {
		this.cumulativeAmountDuePtptn = cumulativeAmountDuePtptn;
	}

	public List<PaymentScheduleItem2> getItems() {
		if ( items == null ) items = new ArrayList<PaymentScheduleItem2>();
		Collections.sort(items, new ScheduleItemComparator());
		return items;
	}

	public void setItems(List<PaymentScheduleItem2> items) {
		this.items = items;
	}
	
	public double getItemsTotal() {
		double total = 0.0d;
		for ( PaymentScheduleItem2 item : items ) {
			total += item.getAmountPaid();
		}
		return total;
	}
	
	static class ScheduleItemComparator extends educate.util.MyComparator {
		
		public int compare(Object o1, Object o2) {
			PaymentScheduleItem2 s1 = (PaymentScheduleItem2) o1;
			PaymentScheduleItem2 s2 = (PaymentScheduleItem2) o2;
			return s1.getDate().compareTo(s2.getDate());
		}
		
	}

}
