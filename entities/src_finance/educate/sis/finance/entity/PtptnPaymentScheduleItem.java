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
@Table(name="fin_pmt_ptptnsch_item")
public class PtptnPaymentScheduleItem {
	
	@Id @Column(length=50)
	private String id;
	@ManyToOne(cascade=CascadeType.PERSIST)
	private PtptnPaymentSchedule paymentSchedule;
	@Temporal(TemporalType.DATE)
	private Date dateDue;
	private Double amountDue;
	

	public PtptnPaymentScheduleItem() {
		setId(lebah.db.UniqueID.getUID());
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	public PtptnPaymentSchedule getPaymentSchedule() {
		return paymentSchedule;
	}
	public void setPaymentSchedule(PtptnPaymentSchedule paymentSchedule) {
		this.paymentSchedule = paymentSchedule;
	}
	public Date getDateDue() {
		return dateDue;
	}
	public void setDateDue(Date dateDue) {
		this.dateDue = dateDue;
	}

	public Double getAmountDue() {
		return amountDue;
	}

	public void setAmountDue(Double amountDue) {
		this.amountDue = amountDue;
	}


}
