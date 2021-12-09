/**
 * 
 */
package educate.sis.finance.module;

import java.util.Date;

import educate.sis.finance.entity.PaymentScheduleItem;

/**
 * @author Shamsul Bahrin bin Abd Mutalib
 *
 */
public class XPaymentScheduleItem {
	

	private String id;
	private Date paymentDate;
	private Date paidDate;
	private double amountDue;
	private double actualAmountDue;
	private double cumulativeAmountDue;
	private double amountPaid;
	private double currentBalance;
	private double outstandingBalance;
	private int isPaid;
	private int type;

	public XPaymentScheduleItem(PaymentScheduleItem i) {
		id = i.getId();
		paymentDate = i.getPaymentDate();
		paidDate = i.getPaidDate();
		amountDue = i.getAmountDue();
		actualAmountDue = i.getActualAmountDue();
		cumulativeAmountDue = i.getCumulativeAmountDue();
		amountPaid = i.getAmountPaid();
		currentBalance = i.getCurrentBalance();
		outstandingBalance = i.getOutstandingBalance();
		isPaid = i.getIsPaid();
		type = i.getType();
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
	public Date getPaidDate() {
		return paidDate;
	}
	public void setPaidDate(Date paidDate) {
		this.paidDate = paidDate;
	}
	public double getAmountDue() {
		return amountDue;
	}
	public void setAmountDue(double amountDue) {
		this.amountDue = amountDue;
	}
	public double getActualAmountDue() {
		return actualAmountDue;
	}
	public void setActualAmountDue(double actualAmountDue) {
		this.actualAmountDue = actualAmountDue;
	}
	public double getCumulativeAmountDue() {
		return cumulativeAmountDue;
	}
	public void setCumulativeAmountDue(double cumulativeAmountDue) {
		this.cumulativeAmountDue = cumulativeAmountDue;
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
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
	
	

}
