/**
 * 
 */
package educate.sis.finance.entity;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * @author Shamsul Bahrin bin Abd Mutalib
 *
 */


@Entity
@Table(name="fin_pmt_sched_item2")
public class PaymentScheduleItem2 {
	
	@Id @Column(length=50)
	private String id;
	@ManyToOne(cascade=CascadeType.PERSIST)
	private PaymentScheduleItem paymentScheduleItem;
	private double amountPaid;
	@Temporal(TemporalType.DATE)
	private Date date;
	
	@Column(length=50)
	private String paymentMode;
	@Column(length=50)
	private String paymentRef;
	@Column(length=255)
	private String remark;
	
	@Column(length=50)
	private String paymentNo; 
	
	
	public PaymentScheduleItem2() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public PaymentScheduleItem getPaymentScheduleItem() {
		return paymentScheduleItem;
	}
	public void setPaymentScheduleItem(PaymentScheduleItem paymentScheduleItem) {
		this.paymentScheduleItem = paymentScheduleItem;
	}
	public double getAmountPaid() {
		return amountPaid;
	}
	public void setAmountPaid(double amountPaid) {
		this.amountPaid = amountPaid;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}

	public String getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}

	public String getPaymentRef() {
		return paymentRef;
	}

	public void setPaymentRef(String paymentRef) {
		this.paymentRef = paymentRef;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getPaymentNo() {
		return paymentNo;
	}

	public void setPaymentNo(String paymentNo) {
		this.paymentNo = paymentNo;
	}

}
